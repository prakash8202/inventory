package co.pointred.core.startup.states.skeleton;


public class StartupExecutorState extends AbstractStartupState
{
	protected static volatile boolean serviceStarted = false;
	public StartupExecutorState(String ctx)
	{
		super(ctx);
	}

	@Override
	public synchronized boolean startService()
	{
		if (serviceStarted == true)
		{
			logDoubleStart();
			return false;
		}
		
		serviceStarted=true;
		boolean retval = true;
		try
		{
			//SingletonFactory.getSingletonFactory().getemsExecutorPool().startEmsExecutor();
			setCurrentState(SERVICE_UP);
			logStartup();
		} catch (Exception e)
		{
			retval = false;
			logError(e);
		}
		return retval;
	}

	@Override
	public boolean stopService()
	{
		serviceStarted=false;
		boolean retval = true; 
			//SingletonFactory.getSingletonFactory().getemsExecutorPool().stopEmsExecutor();
		setCurrentState(SERVICE_DOWN);
		logShutdown();
		return retval;
	}


	@Override
	public void reset()
	{
		stopService();
		startService();
	}
}
