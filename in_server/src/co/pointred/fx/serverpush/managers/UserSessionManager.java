package co.pointred.fx.serverpush.managers;

import co.pointred.fx.serverpush.core.IServerPush;
import co.pointred.fx.serverpush.core.ServerPushConstants;
import co.pointred.fx.serverpush.core.ServerPushService;


public class UserSessionManager implements IServerPush
{

    private static final String REQ_CONTEXT = ServerPushConstants.USER_SESSION_MONITOR;

    // Types
    // FORCE_LOGOUT

    /**
     * objects[0] --> Push Type
     */
    @Override
    public void sendServerPush(String neKey, String neType, Object... objects)
    {
	String xmlData = "<usersession><type>%TYPE%</type><msg>%Message%</msg></usersession>";
	if (ServerPushConstants.FORCE_LOGOUT.equals(objects[0] + ""))
	{
	    xmlData = "<usersession><type>" + objects[0] + "</type><message>" + objects[2] + "</message></usersession>";
	    neKey = objects[1] + "";
	    neType = objects[0] + "";
	}
	ServerPushService.instance.sendServerPush(xmlData, neKey, neType, REQ_CONTEXT);
    }
}
