package co.pointred.fx.dataobjects;


public class ServerPushRequestObject
{
    private String userName;
    private String clientId;
    private String requestCtx;
    private String xmlStr;

    public String getRequestCtx()
    {
	return requestCtx;
    }

    public void setRequestCtx(String requestCtx)
    {
	this.requestCtx = requestCtx;
    }

    public String getClientId()
    {
	return clientId;
    }

    public void setClientId(String clientId)
    {
	this.clientId = clientId;
    }

    public String getUserName()
    {
	return userName;
    }

    public void setUserName(String userName)
    {
	this.userName = userName;
    }

    public String getXmlStr()
    {
	return xmlStr;
    }

    public void setXmlStr(String xmlStr)
    {
	this.xmlStr = xmlStr;
    }
}
