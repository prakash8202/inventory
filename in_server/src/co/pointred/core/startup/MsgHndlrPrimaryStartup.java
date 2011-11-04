package co.pointred.core.startup;

import org.jboss.remoting.InvocationRequest;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;

public class MsgHndlrPrimaryStartup implements MessageHandlerIface
{
    private static final String MODULE_NAME = "[ EMS START UP ]";

    @Override
    public Object invoke(InvocationRequest invocationrequest) throws Throwable
    {
	StartUpMsg startUpMsg = (StartUpMsg) invocationrequest.getParameter();
	StartUpMsg respMsg = StartUpMsg.START_UP_ERROR;
	switch (startUpMsg)
	{
	case PING:
	    respMsg = StartUpMsg.PONG;
	    break;
	case SUSPEND_PRIMARY_PINGER:
	    respMsg = suspendPrimaryPringer();
	    break;
	case GOTO_IDLE_MODE_I_WANT_TO_RECOVER:
	    respMsg = gotoIdle4SecToRecover();
	    break;
	case GOTO_DEFAULT_MODE_I_HAVE_RECOVERED:
	    respMsg = gotoDefault4SecHasRecovered();
	    break;
	case RESUME_PRIMARY_PINGER:
	    respMsg = resumePrimaryPinger();
	    break;
	default:
	    break;
	}
	return respMsg;
    }

    private StartUpMsg resumePrimaryPinger()
    {
	if (PrStartupManager.instance.resumePinger() == true)
	{
	    return StartUpMsg.PRIMARY_PINGER_RESUMED;
	}
	return StartUpMsg.START_UP_ERROR;
    }

    private StartUpMsg gotoDefault4SecHasRecovered()
    {
	StartUpMsg resp = StartUpMsg.GONE_TO_DEFAULT_MODE;
	PrStartupManager.instance.startupDefault();
	return resp;
    }

    private StartUpMsg gotoIdle4SecToRecover()
    {
	// push to Idle mode..
	StartUpMsg resp = StartUpMsg.GONE_TO_IDLE_MODE_YOU_RECOVER;
	PrStartupManager.instance.startupIdle();
	while (!PrStartupManager.instance.getCurrentMode().equals(ProjectStartupConstants.IDLE_MODE))
	{
	    try
	    {
		Thread.sleep(1000);
		logWarn("gotoIdle4SecToRecover()", "Waiting for PRIMARY to come to IDLE Mode for SEC Recovery");
	    } catch (Exception e)
	    {
		resp = StartUpMsg.START_UP_ERROR;
		logError("gotoIdle4SecToRecover()", e);
	    }
	}

	return resp;
    }

    private StartUpMsg suspendPrimaryPringer()
    {
	if (PrStartupManager.instance.suspendPinger() == true)
	{
	    return StartUpMsg.PRIMARY_PINGER_SUSPENDED;
	}
	return StartUpMsg.START_UP_ERROR;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, MsgHndlrPrimaryStartup.class, e);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, MsgHndlrPrimaryStartup.class);
    }
}
