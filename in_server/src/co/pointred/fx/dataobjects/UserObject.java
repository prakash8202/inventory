/**
 * 
 */
package co.pointred.fx.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Communicator Class
 * 
 * @author kandeepa
 * 
 */
public class UserObject
{
    private String userName;
    private String password;
    private String userId;
    private String sessionId;
    private Object dataObject;
   
    private List<Object> privilegeList = new ArrayList<Object>();
    private String status;
    private String statusMsg;
    private int loginAttempts;

    private String auditContext;
    private String auditDescr;
    
    public UserObject()
    {
    }

    public String getAuditDescr()
    {
	return auditDescr;
    }

    public void setAuditDescr(String auditDescr)
    {
	this.auditDescr = auditDescr;
    }

    public String getAuditContext()
    {
	return auditContext;
    }

    public void setAuditContext(String auditContext)
    {
	this.auditContext = auditContext;
    }

    public String getUserName()
    {
	return userName;
    }

    public void setUserName(String userName)
    {
	this.userName = userName;
    }

    public String getPassword()
    {
	return password;
    }

    public void setPassword(String password)
    {
	this.password = password;
    }

    public String getUserId()
    {
	return userId;
    }

    public void setUserId(String userId)
    {
	this.userId = userId;
    }

    public String getSessionId()
    {
	return sessionId;
    }

    public void setSessionId(String sessionId)
    {
	this.sessionId = sessionId;
    }

    public Object getDataObject()
    {
	return dataObject;
    }

    public void setDataObject(Object dataObject)
    {
	this.dataObject = dataObject;
    }

    public List<Object> getPrivilegeList()
    {
	return privilegeList;
    }

    public void setPrivilegeList(List<Object> privilegeList)
    {
	this.privilegeList = privilegeList;
    }

    public String getStatusMsg()
    {
	return statusMsg;
    }

    public void setStatusMsg(String statusMsg)
    {
	this.statusMsg = statusMsg;
    }

    public String getStatus()
    {
	return status;
    }

    public void setStatus(String status)
    {
	this.status = status;
    }

    public int getLoginAttempts()
    {
	return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts)
    {
	this.loginAttempts = loginAttempts;
    }
}
