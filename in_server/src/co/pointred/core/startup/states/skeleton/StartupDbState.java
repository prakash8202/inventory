package co.pointred.core.startup.states.skeleton;

import co.pointred.core.database.DbManager;

public class StartupDbState extends AbstractStartupState
{
    protected static volatile boolean serviceStarted = false;

    public StartupDbState(String ctx)
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

	try
	{
	    DbManager.instance.start();
	} catch (Exception e1)
	{
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	serviceStarted = true;

	boolean retval = true;
	try
	{
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
	serviceStarted = false;
	try
	{
	    DbManager.instance.stop();
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	boolean retval = true;
	return retval;
    }

    @Override
    public void reset()
    {
	stopService();
	startService();
    }
}
