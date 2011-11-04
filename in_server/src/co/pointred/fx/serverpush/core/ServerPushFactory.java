package co.pointred.fx.serverpush.core;

import co.pointred.fx.serverpush.managers.UserSessionManager;

/**
 * Factory class responsible for providing instances of various Server Push Managers
 * 
 * @author kandeepa
 * 
 */
public enum ServerPushFactory
{
    instance;

    private UserSessionManager userSessionManager;

    public UserSessionManager getUserSessionManager()
    {
	if (null == userSessionManager)
	{
	    userSessionManager = new UserSessionManager();
	}
	return userSessionManager;
    }
}
