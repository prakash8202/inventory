package co.pointred.core.startup.states.skeleton;

public class StartupIdleDoneState extends AbstractStartupState
{
    protected static volatile boolean serviceStarted = false;

    public StartupIdleDoneState(String ctx)
    {
	super(ctx);
    }

    @Override
    public boolean startService()
    {
	if (serviceStarted == true)
	{
	    logDoubleStart();
	    return false;
	}
	boolean retval = true;
	setCurrentState(SERVICE_UP);
	logStartup();
	return retval;
    }

    @Override
    public boolean stopService()
    {
	serviceStarted = false;
	boolean retval = true;
	setCurrentState(SERVICE_DOWN);
	logShutdown();
	return retval;
    }

    @Override
    public void reset()
    {
	if (stopService())
	{
	    startService();
	}
    }
}