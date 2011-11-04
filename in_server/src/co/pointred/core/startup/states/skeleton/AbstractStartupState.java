package co.pointred.core.startup.states.skeleton;

import co.pointred.core.log.LoggerService;
import co.pointred.core.startup.PrStartupIface;

public abstract class AbstractStartupState implements PrStartupIface
{
    public static final String SERVICE_UP = "SERVICE_UP";
    public static final String SERVICE_DOWN = "SERVICE_DOWN";
    public static final String SERVICE_IDLE = "SERVICE_IDLE";
    public static final String SERVICE_PROCESSING = "SERVICE_PROCESSING";
    public static final String SERVICE_ERROR = "SERVICE_ERROR";
    public static final String MODULE_NAME = "[ EMS START UP ]";

    private String ctx;
    private String currentState;

    public AbstractStartupState(String ctx)
    {
	this.ctx = ctx;
    }

    @Override
    public String getState()
    {
	return this.currentState;
    }

    @Override
    public String getContext()
    {
	return this.ctx;
    }

    public void setCurrentState(String currentState)
    {
	this.currentState = currentState;

    }

    public void logStartup()
    {
	LoggerService.instance.warning("\n [ [ STARTING UP ] ] : " + getContext() + "State = " + getState() + "\n", AbstractStartupState.class);
    }

    public void logShutdown()
    {
	LoggerService.instance.warning("\n [ [ SHUTTING DOWN ] ] : " + getContext() + "State = " + getState() + "\n", AbstractStartupState.class);
    }

    public void logError(Exception e)
    {
	LoggerService.instance.warning("\n [ [ STARTING UP / SHUTTING DOWN ERR ] ] : " + getContext() + "State = " + getState() + "\n", AbstractStartupState.class);
    }

    public void logDoubleStart()
    {
	LoggerService.instance.warning("Already in Startup mode.. need not to startup once again.. ignoring startup cmd" + getContext() + "(" + this.getClass().getSimpleName()
		+ ").", AbstractStartupState.class);
    }
}
