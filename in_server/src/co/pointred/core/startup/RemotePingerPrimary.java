package co.pointred.core.startup;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import co.pointred.core.log.LoggerService;

class RemotePingerPrimary extends Thread
{
    private static final String MODULE_NAME = "[ EMS START UP ]";
    private final AtomicBoolean keepPinging = new AtomicBoolean(false);
    private final long _5SEC = 5000;
    private final long _10SEC = 10000;

    private RemotingClientPrimary remotingClientPrimary;

    private volatile boolean pingerSuspended = false;

    private final ReentrantLock pingerLock = new ReentrantLock();

    protected RemotePingerPrimary(RemotingClientPrimary remotingClientPrimary)
    {
	this.remotingClientPrimary = remotingClientPrimary;
    }

    @Override
    public void run()
    {
	logWarn("run()", "PRIMARY Remoting PINGER Started !!!!");
	while (keepPinging.get() == true)
	{
	    try
	    {
		if (this.pingerSuspended == true)
		{
		    while (this.pingerSuspended == true)
		    {
			logWarn("run()", "PRIMARY Remoting PINGER in SUSPENDED Mode..!!");
			Thread.sleep(_10SEC);
		    }
		    logWarn("run()", "PRIMARY Remoting PINGER RESUMED from SUSPENDED Mode..!!");
		}

		startNewSessionWithSecondary();
		sleep(_5SEC);
	    } catch (Exception e)
	    {
		keepPinging.set(false);
		if (e instanceof InterruptedException)
		{
		    logError("run() interrupted in RemotePingerPrimary.. interrupt status = " + interrupted(), e);
		} else
		{
		    logError("run()", e);
		}
	    }
	}
	logWarn("run()", "PRIMARY Remoting PINGER Stopped !!!!");
    }

    // core api which keeps talking to Secondary..
    private void startNewSessionWithSecondary()
    {
	try
	{
	    this.pingerLock.lock();

	    RemotingMsg resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.PING);
	    // the resp will be ERR if the secondary is not up.. so it goes to the else block
	    if (resp.equals(RemotingMsg.PONG))
	    {
		resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.WHAT_MODE_ARE_YOU_IN);
		if (resp.equals(RemotingMsg.INTERMEDIATE_MODE_I_AM_IN))
		{
		    // there is a switch over in states - skip for now..
		} else if (resp.equals(RemotingMsg.IDLE_MODE_I_AM_IN))
		{
		    // continue - need not to do anything..normal case

		} else if (resp.equals(RemotingMsg.DEFAULT_MODE_I_AM_IN))
		{
		    // trickiest part...wait for 10 sec and ask again..
		    logWarn("startNewSessionWithSecondary()",
			    "Seems the Secondary was also running...to make him to go back to Idle and DO HIS REC.. sleeping for 30 sec to confirm");
		    Thread.sleep(10 * 1000);
		    // again ask him the same question..
		    resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.WHAT_MODE_ARE_YOU_IN);
		    if (resp.equals(RemotingMsg.DEFAULT_MODE_I_AM_IN))
		    {
			// The sec is somehow in default Mode..This is not acceptable - push him to IDLE..
			resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.SECONDARY_GOTO_IDLE_MODE_FOR_RECOVERY);
			if (resp.equals(RemotingMsg.SECONDARY_GONE_TO_IDLE_MODE_FOR_RECOVERY))
			{
			    logWarn("startNewSessionWithSecondary()", "Secondary was also UP SIMULTANEOUSLY.... HE has gone to IDLE now..");
			    resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.SUSPEND_SECONDARY_PINGER);
			    if (resp.equals(RemotingMsg.SECONDARY_PINGER_SUSPENDED))
			    {
				// bring the Primary to IDLE MODE so that Secondary can Recover..
				PrStartupManager.instance.startupIdle();

				// the secondary pinger has been stopped.. ask him to recover..
				resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.PRIMARY_IN_IDLE_RECOVER_SECONDARY_FROM_PRIMARY);

				// / there will be a GAP here.... during this time..Secondary recovers from Primary
				if (resp.equals(RemotingMsg.SECONDARY_RECOVERED_FROM_PRIMARY))
				{
				    // no matter what - the Primary has to come back in DEFAULT mode
					PrStartupManager.instance.startupDefault();

				    // secondary has recovered from primary.. ask him to resume PINGER
				    resp = this.remotingClientPrimary.sendRemotingMsgToSecondaryServer(RemotingMsg.RESUME_SECONDARY_PINGER);
				    if (resp.equals(RemotingMsg.SECONDARY_PINGER_RESUMED))
				    {
					// all izzz well.. the secondary pinger has also resumed back to work..
					// nothing to worry here.. continue normally
				    } else
				    {
					communicationFailedWithSecondary("SECONDARY_PINGER_RESUMED", resp.name());
				    }
				} else
				{
				    logWarn("startNewSessionWithSecondary()", "Seems SEC did not Recover Properly from PRI - but Still will bring Primary in Default by FORCE.. ");
				    // no matter what - the Primary has to come back in DEFAULT mode
				    PrStartupManager.instance.startupDefault();
				    communicationFailedWithSecondary("SECONDARY_RECOVERED_FROM_PRIMARY", resp.name());
				}
			    } else
			    {
				communicationFailedWithSecondary("SECONDARY_PINGER_STOPPED", resp.name());
			    }
			} else
			{
			    communicationFailedWithSecondary("GONE_TO_IDLE_MODE_FOR_RECOVERY", resp.name());
			}
		    } else
		    {
			logWarn("startNewSessionWithSecondary()", "Secondary was UP for a while.. but he Managed to go back to IDLE within 10 sec.. no worried..");
		    }

		} else
		{
		    communicationFailedWithSecondary("INTERMEDIATE_MODE_I_AM_IN, IDLE_MODE_I_AM_IN, DEFAULT_MODE_I_AM_IN", resp.name());
		}
	    } else
	    {
		communicationFailedWithSecondary("PONG", resp.name());
	    }
	} catch (Throwable e)
	{
	    logError("startNewSessionWithSecondary()", e);
	} finally
	{
	    pingerLock.unlock();
	}
    }

    private void communicationFailedWithSecondary(String respExpected, String respGot)
    {
	// possible cause for this exception is COMMUNICATION LOSS with Secondary
	if (PrStartupManager.instance.isRemoteDirty() == false)
	{
		PrStartupManager.instance.setRemoteDirty(true);
	}
    }

    protected void startPrimaryPinger()
    {
	keepPinging.set(true);
	start();
    }

    protected void stopPrimaryPinger()
    {
	keepPinging.set(false);
	interrupt();
	logWarn("stopPinger()", "Received msg to Stop Primary Remote Pinger");
    }

    protected boolean suspendPrimaryPinger()
    {
	try
	{
	    this.pingerLock.lock();
	    this.pingerSuspended = true;
	    logWarn("run()", "PRIMARY Remoting PINGER Acquired Pinger Lock for Suspension !!!!");
	} finally
	{
	    this.pingerLock.unlock();
	}
	return true;
    }

    protected boolean resumePrimaryPinger()
    {
	this.pingerSuspended = false;
	return true;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, RemotePingerPrimary.class, e);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, RemotePingerPrimary.class);
    }
}
