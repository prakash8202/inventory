package co.pointred.fx.dataobjects;

public class ServerPushObject
{
    private String neKey;
    private String neType;
    private String xmlStr;
    private String requestCtx;

    public String getNeKey()
    {
	return neKey;
    }

    public void setNeKey(String neKey)
    {
	this.neKey = neKey;
    }

    public String getNeType()
    {
	return neType;
    }

    public void setNeType(String neType)
    {
	this.neType = neType;
    }

    public String getXmlStr()
    {
	return xmlStr;
    }

    public void setXmlStr(String xmlStr)
    {
	this.xmlStr = xmlStr;
    }

    public String getRequestCtx()
    {
	return requestCtx;
    }

    public void setRequestCtx(String requestCtx)
    {
	this.requestCtx = requestCtx;
    }
}
