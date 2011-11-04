package co.pointred.core.startup;

import org.jboss.remoting.InvocationRequest;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;

public class MsgHndlrSecondaryStartup implements MessageHandlerIface
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
	case SUSPEND_SECONDARY_PINGER:
	    respMsg = suspendSecondaryPinger();
	    break;
	case WHAT_MODE_ARE_YOU_IN:
	    respMsg = getCurrentMode();
	    break;
	case GOTO_IDLE_MODE_I_WANT_TO_RECOVER:
	    respMsg = gotoIdle4PrimaryToRecover();
	    break;
	case IS_PRIMARY_DIRTY:
	    respMsg = isPrimaryDirty();
	    break;
	case RESUME_SECONDARY_PINGER:
	    respMsg = resumeSecondaryPinger();
	    break;
	default:
	    break;
	}
	return respMsg;
    }

    private StartUpMsg resumeSecondaryPinger()
    {
	if (PrStartupManager.instance.resumePinger() == true)
	{
	    return StartUpMsg.SECONDARY_PINGER_RESUMED;
	}
	return StartUpMsg.START_UP_ERROR;
    }

    private StartUpMsg suspendSecondaryPinger()
    {
	if (PrStartupManager.instance.suspendPinger() == true)
	{
	    return StartUpMsg.SECONDARY_PINGER_SUSPENDED;
	}
	return StartUpMsg.START_UP_ERROR;
    }

    private StartUpMsg isPrimaryDirty()
    {
	if (PrStartupManager.instance.isRemoteDirty() == true)
	    return StartUpMsg.PRIMARY_IS_DIRTY;
	else
	    return StartUpMsg.PRIMARY_IS_NOT_DIRTY;
    }

    private StartUpMsg gotoIdle4PrimaryToRecover()
    {
	// start IDLE mode
    	PrStartupManager.instance.startupIdle();

	String currMode = PrStartupManager.instance.getCurrentMode();
	if (currMode.equals(ProjectStartupConstants.IDLE_MODE))
	    return StartUpMsg.GONE_TO_IDLE_MODE_YOU_RECOVER;

	return StartUpMsg.START_UP_ERROR;
    }

    private StartUpMsg getCurrentMode()
    {
	String currMode = PrStartupManager.instance.getCurrentMode();

	if (currMode.equals(ProjectStartupConstants.IDLE_MODE))
	    return StartUpMsg.IDLE_MODE_I_AM_IN;
	else if (currMode.equals(ProjectStartupConstants.DEFAULT_MODE))
	    return StartUpMsg.DEFAULT_MODE_I_AM_IN;
	else if (currMode.equals(ProjectStartupConstants.INTERMEDIATE_MODE))
	    return StartUpMsg.INTERMEDIATE_MODE_I_AM_IN;
	else
	    return StartUpMsg.START_UP_ERROR;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, MsgHndlrSecondaryStartup.class,e);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, MsgHndlrSecondaryStartup.class);
    }

}
