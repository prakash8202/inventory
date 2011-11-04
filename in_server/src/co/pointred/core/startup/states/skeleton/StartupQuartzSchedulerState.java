package co.pointred.core.startup.states.skeleton;


public class StartupQuartzSchedulerState extends AbstractStartupState
{
	protected static volatile boolean serviceStarted = false;

	public StartupQuartzSchedulerState(String ctx)
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
			addObjectsToQuartzPool();
			//SingletonFactory.getSingletonFactory().getQuartzScheduler().startQuartzScheduler();
			setCurrentState(SERVICE_UP);
			logStartup();
		} catch (final Exception e)
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
		try
		{
			//SingletonFactory.getSingletonFactory().getQuartzScheduler().shutdownScheduler();
			setCurrentState(SERVICE_DOWN);
			logShutdown();
		} catch (final Exception e)
		{
			retval = false;
			logError(e);
		}
		return retval;
	}

	private void addObjectsToQuartzPool()
	{
		// Add EmsZipFileManagement to Quartz Pool
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new EmsZipFileManagement());

		// Add Performance Metrics Collector
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new PerformanceMetricsCollectorJob());

		// Add Daily House Keeper
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new DailyHouseKeepingJob());

		// Add Ftp Scheduler to Quartz Pool
	//	PrimaryFtpScheduler primaryFtpScheduler = new PrimaryFtpScheduler();
		//SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(primaryFtpScheduler);

//		SecondaryFtpScheduler secondaryFtpScheduler = new SecondaryFtpScheduler();
		// Add Ftp Scheduler to Quartz Pool
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(secondaryFtpScheduler);
		//Add GlobalSummary Update Scheduler to Quartz Pool
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new GlobalSummaryScheduler());
		//Add House Keeping of Traps Logger Summary
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new HouseKeepingTrapSummary());
		//Add House Keeping of Traps Logger
//		SingletonFactory.getSingletonFactory().getQuartzScheduler().addSchedulerToEms(new HouseKeepingTrapsLogger());
		

	}

	@Override
	public String getState()
	{
//		try
//		{
//			if (true == SingletonFactory.getSingletonFactory().getQuartzScheduler().pingQuartzScheduler())
//				return SERVICE_UP;
//			else
//				return SERVICE_DOWN;
//		} catch (Exception e)
//		{
//			SingletonFactory.getSingletonFactory().getEmsLogManager().error("Failed to ping quartz scheduler", "QuartzSchedulerState", e);
//		}
		return AbstractStartupState.SERVICE_DOWN;
	}

	@Override
	public void reset()
	{
		stopService();
		startService();
	}
}