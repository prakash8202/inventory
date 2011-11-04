package co.pointred.core.startup;

import org.jboss.remoting.InvocationRequest;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;

public class MsgHndlrSecondaryRemoting implements MessageHandlerIface
{
    private static final String MODULE_NAME = "[ EMS START UP ]";

    @Override
    public Object invoke(InvocationRequest invocation) throws Throwable
    {
	RemotingMsg remotingMsg = (RemotingMsg) invocation.getParameter();
	RemotingMsg respMsg = RemotingMsg.ERROR;
	switch (remotingMsg)
	{
	case PING:
	    respMsg = RemotingMsg.PONG;
	    break;

	case WHAT_MODE_ARE_YOU_IN:
	    respMsg = getCurrentMode();
	    break;

	case SECONDARY_GOTO_IDLE_MODE_FOR_RECOVERY:
	    respMsg = gotoIdleModeForRecovery();
	    break;

	case SUSPEND_SECONDARY_PINGER:
	    respMsg = suspendSecondaryPinger();
	    break;
	case PRIMARY_IN_IDLE_RECOVER_SECONDARY_FROM_PRIMARY:
	    respMsg = recoverSecondaryFromPrimary();
	    break;
	case RESUME_SECONDARY_PINGER:
	    respMsg = resumeSecondaryPinger();
	    break;

	default:
	    break;
	}

	return respMsg;
    }

    private RemotingMsg recoverSecondaryFromPrimary()
    {
	if (PrStartupManager.instance.recoverOwnDatabase())
	{
	    return RemotingMsg.SECONDARY_RECOVERED_FROM_PRIMARY;
	}
	return RemotingMsg.ERROR;
    }

    private RemotingMsg suspendSecondaryPinger()
    {
	boolean pingerSuspended = PrStartupManager.instance.suspendPinger();
	if (pingerSuspended == true)
	{
	    return RemotingMsg.SECONDARY_PINGER_SUSPENDED;
	}

	return RemotingMsg.ERROR;
    }

    private RemotingMsg resumeSecondaryPinger()
    {
	boolean pingerResumed = PrStartupManager.instance.resumePinger();
	if (pingerResumed == true)
	{
	    return RemotingMsg.SECONDARY_PINGER_SUSPENDED;
	}

	return RemotingMsg.ERROR;
    }

    private RemotingMsg gotoIdleModeForRecovery()
    {
	// push to Idle mode..
	RemotingMsg resp = RemotingMsg.SECONDARY_GONE_TO_IDLE_MODE_FOR_RECOVERY;
	PrStartupManager.instance.startupIdle();
	while (!PrStartupManager.instance.getCurrentMode().equals(ProjectStartupConstants.IDLE_MODE))
	{
	    try
	    {
		Thread.sleep(1000);
		logWarn("gotoIdleModeAndRecover()", "Waiting for SECONDARY to come to IDLE Mode");
	    } catch (InterruptedException e)
	    {
		resp = RemotingMsg.ERROR;
		logError("gotoIdleModeAndRecover()", e);
	    }
	}

	return resp;
    }

    private RemotingMsg getCurrentMode()
    {
	String currMode = PrStartupManager.instance.getCurrentMode();

	if (currMode.equals(ProjectStartupConstants.IDLE_MODE))
	    return RemotingMsg.IDLE_MODE_I_AM_IN;
	else if (currMode.equals(ProjectStartupConstants.DEFAULT_MODE))
	    return RemotingMsg.DEFAULT_MODE_I_AM_IN;
	else if (currMode.equals(ProjectStartupConstants.INTERMEDIATE_MODE))
	    return RemotingMsg.INTERMEDIATE_MODE_I_AM_IN;
	else
	    return RemotingMsg.ERROR;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, MsgHndlrSecondaryRemoting.class, e);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, MsgHndlrSecondaryRemoting.class);
    }
}
