package co.pointred.core.startup;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.utils.Console;

class RemotePingerSecondary extends Thread
{
	private static final String MODULE_NAME = "[ EMS START UP ]";
	private final AtomicBoolean keepPinging = new AtomicBoolean(false);
	private final long _5SEC = 5000;
	private final long _10SEC = 10000;

	private RemotingClientSecondary remotingClientSecondary;

	private volatile boolean pingerSuspended = false;
	private final ReentrantLock pingerLock = new ReentrantLock();

	protected RemotePingerSecondary(RemotingClientSecondary remotingClientSecondary)
	{
		this.remotingClientSecondary = remotingClientSecondary;
	}

	@Override
	public void run()
	{
		logWarn("run()", "SECONDARY Remoting PINGER Started !!!!");
		while (keepPinging.get() == true)
		{
			try
			{
				if (this.pingerSuspended == true)
				{
					while (this.pingerSuspended == true)
					{
						logWarn("run()", "SECONDARY Remoting PINGER in SUSPENDED Mode..!!");
						Thread.sleep(_10SEC);
					}
					logWarn("run()", "SECONDARY Remoting PINGER RESUMED after suspension..!!");
				}

				startNewSessionWithPrimary();
				sleep(_5SEC);
			} catch (Exception e)
			{
				if (e instanceof InterruptedException)
					logError("run() interrupted in RemotePingerPrimary.. interrupt status = " + interrupted(), e);
				else
					logError("run()", e);
			}
		}
		Console.print("Exited out of Remote Pinger Secondary.....!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	// core api which keeps talking to Secondary
	private void startNewSessionWithPrimary()
	{
		try
		{
			this.pingerLock.lock();
			RemotingMsg resp = this.remotingClientSecondary.sendRemotingMsgToPrimaryServer(RemotingMsg.PING);
			if (resp.equals(RemotingMsg.PONG))
			{
				// The PRIMARY is available... chk the states of SEC (Self chk)
				String currentMode = PrStartupManager.instance.getCurrentMode();

				if (currentMode.equals(ProjectStartupConstants.IDLE_MODE) || currentMode.equals(ProjectStartupConstants.INTERMEDIATE_MODE))
				{
					// no problem here..Secondary should be in IDLE mode under Normal circumstances..
				} else if (currentMode.equals(ProjectStartupConstants.DEFAULT_MODE))
				{
					// even if SEC is on DEFAULT_MODE...Primary overrides SEC and so.. SEC has to get Recovered from PRIMARY
					// the primary will also know abt this... and he will ask the SEC to do a Recovery..
					// for now.. just log this and see..
					logWarn("startNewSessionWithPrimary()", "SECONDARY is running on DEFAULT mode while PRIMARY is also REACHABLE.. primary should ask SEC to recover..");
				}

			} else if (resp.equals(RemotingMsg.ERROR))
			{
				startupDefault();
			} else
			{
				throw new Exception("Expected PONG.. But Got -" + resp + " as Resp from Primary");
			}
		} catch (Throwable e)
		{
			// possible causes for coming to this exception is COMMUNICATION LOSS with Primary - so, startup in DEFAULT..
			logError("startNewSessionWithPrimary()", e);
		} finally
		{
			this.pingerLock.unlock();
		}
	}

	private void startupDefault()
	{
		String currState = PrStartupManager.instance.getCurrentMode();
		if (currState.equals(ProjectStartupConstants.IDLE_MODE))
		{
			logWarn("startNewSessionWithPrimary()", "Commnication Link Failure with Primary... Bringing SECONDARY UP UP UP !!!!");
			PrStartupManager.instance.setSelfDirty(false);

			// start the secondary in DEFAULT MODE..!!!
			PrStartupManager.instance.startupDefault();
		}
	}

	protected void startSecondaryPinger()
	{
		keepPinging.set(true);
		start();
	}

	protected void stopSecondaryPinger()
	{
		keepPinging.set(false);
		Thread.currentThread().interrupt();
		logWarn("stopPinger()", "Received msg to Stop SECONDARY Remote Pinger");
	}

	protected boolean suspendSecondaryPinger()
	{
		try
		{
			this.pingerLock.lock();
			this.pingerSuspended = true;
			logWarn("run()", "SECONDARY Remoting PINGER Acquired Pinger Lock for Suspension !!!!");
		} finally
		{
			this.pingerLock.unlock();
		}
		return true;
	}

	protected boolean resumeSecondaryPinger()
	{
		this.pingerSuspended = false;
		return true;
	}

	private void logError(String apiName, Throwable e)
	{
		LoggerService.instance.error(MODULE_NAME + " - " + apiName,RemotePingerSecondary.class, e);
	}

	private void logWarn(String apiName, String msg)
	{
		LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, RemotePingerSecondary.class);
	}

}
