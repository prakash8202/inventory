package co.pointred.fx.serverpush.core;

import co.pointred.fx.dataobjects.ServerPushObject;
import co.pointred.fx.users.ActiveUserSessions;

public enum ServerPushService
{
    instance;

    public void sendServerPush(String xmlData, String neKey, String neType, String requestCtx)
    {
	StringBuffer buffer = new StringBuffer("<serverpush><context>");
	buffer.append(requestCtx);
	buffer.append("</context><xmlData>");
	buffer.append(xmlData);
	buffer.append("</xmlData></serverpush>");
	ServerPushObject serverPushObject = new ServerPushObject();
	serverPushObject.setRequestCtx(requestCtx);
	serverPushObject.setNeKey(neKey);
	serverPushObject.setNeType(neType);

	serverPushObject.setXmlStr(buffer.toString());

	ActiveUserSessions.instance.sendServerPush(serverPushObject);
    }

}
