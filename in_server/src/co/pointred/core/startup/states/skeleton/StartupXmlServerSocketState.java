/** 
 * Copyright @2007-2009@ PointRed Telecom Ltd. All rights reserved. Unauthorized reproduction
 * is a violation of applicable law. This material contains certain
 * confidential or proprietary information and trade secrets of PointRed Telecom Ltd
 **/

package co.pointred.core.startup.states.skeleton;

import co.pointred.core.log.LoggerService;
import co.pointred.fx.serverpush.core.XmlServerSocketController;
import co.pointred.fx.users.ActiveUserSessions;


public class StartupXmlServerSocketState extends AbstractStartupState
{
    protected static volatile boolean serviceStarted = false;

    public StartupXmlServerSocketState(String ctx)
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

	serviceStarted = true;
	boolean retval = true;
	setCurrentState(SERVICE_UP);
	XmlServerSocketController.instance.startXmlSocket();
	logStartup();
	return retval;
    }

    @Override
    public boolean stopService()
    {
	serviceStarted = false;
	boolean retval = true;
	try
	{
	    // @ Force logout all users here.. before closing the socket
	    ActiveUserSessions.instance.forceLogoutAllUsers("EMS Server is shutting down");
	    XmlServerSocketController.instance.stopXmlSocket();
	    setCurrentState(SERVICE_DOWN);
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error while stopping Xml Server Socket", StartupXmlServerSocketState.class, e);
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
