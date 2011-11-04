package co.pointred.fx.users;

import co.pointred.fx.serverpush.core.ServerPushAssembler;


public class ActiveUser
{
    private String userId;
    private String userName;
    private String sessionId;
    private String ipAddress;
    private ServerPushAssembler serverPushAssembler = new ServerPushAssembler();

   public ActiveUser()
   {
       
   }
    
    public String getUserId()
    {
	return userId;
    }

    public void setUserId(String userId)
    {
	this.userId = userId;
    }

    public String getUserName()
    {
	return userName;
    }

    public void setUserName(String userName)
    {
	this.userName = userName;
    }

    public String getSessionId()
    {
	return sessionId;
    }

    public void setSessionId(String sessionId)
    {
	this.sessionId = sessionId;
    }

    public String getIpAddress()
    {
	return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
	this.ipAddress = ipAddress;
    }

	public ServerPushAssembler getServerPushAssembler() {
		return serverPushAssembler;
	}

	public void setServerPushAssembler(ServerPushAssembler serverPushAssembler) {
		this.serverPushAssembler = serverPushAssembler;
	}
}
