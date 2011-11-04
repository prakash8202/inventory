package co.pointred.core.startup.states.skeleton;

public class StartupNotificationState extends AbstractStartupState
{
	protected static volatile boolean serviceStarted = false;
	public StartupNotificationState(String ctx)
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
		setCurrentState(SERVICE_UP);
		logStartup();
		return retval;
	}

	@Override
	public boolean stopService()
	{
		serviceStarted=false;
		boolean retval = true;
		try
		{
			// unregister all contexts..
			//SingletonFactory.getSingletonFactory().getNotificationService().unregisterAll();
			setCurrentState(SERVICE_DOWN);
		} catch (Exception e)
		{
			//SingletonFactory.getSingletonFactory().getEmsLogManager().error(e.getMessage(), this.getClass().getSimpleName(), e);
		}
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
