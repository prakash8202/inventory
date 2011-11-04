package co.pointred.fx.gateway;

import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;
import co.pointred.core.startup.PrStartupManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.serverpush.core.ServerPushConstants;
import co.pointred.fx.serverpush.core.ServerPushFactory;
import co.pointred.fx.users.ActiveUserSessions;
import flex.messaging.FlexContext;

/**
 * Support Class for User Login & Logout
 * 
 * @author kandeepasundaram
 * 
 */
public enum LoginSupport
{
	instance;

	private static final int maxRetriesAllowed = 4;

	/**
	 * API to Login User
	 * 
	 * @param userObject
	 * @return userObject
	 */
	public UserObject loginUser(UserObject userObject)
	{
		String userName = userObject.getUserName();
		String clientIp = FlexContext.getHttpRequest().getRemoteAddr();
		String sessionId = FlexContext.getFlexSession().getId();
		userObject.setSessionId(sessionId);

		try
		{
			// do not allow users to login until EMS is in default mode..
			String emsMode = PrStartupManager.instance.getCurrentMode();
			if (emsMode.equals(ProjectStartupConstants.DEFAULT_MODE))
			{
				if (PrStartupManager.serverStartedInDefaultMode.get() == false)
				{
					userObject.setStatus(SharedConstants.FAILURE);
					String ctx = PrStartupManager.instance.getCurrentCtx();
					userObject.setStatusMsg("EMS is still coming up..Please do retry after some time...current ctx is : <font color='blue'>" + ctx + "</font>");
					return userObject;
				}
			} else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("EMS is in IDLE MODE...Login Denied..");
				return userObject;
			}

			// check if user has already logged in another terminal

			// if (UserManagementSupport.instance.userQueue.contains(userName))
			if (true == ActiveUserSessions.instance.isUserAlreadyLoggedIn(userName))
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg(userName + " Already logged in another Terminal.. Login denied..!!");
				return userObject;
			}

			userObject = checkUserCredentialsOnLogin(userObject, clientIp);

			// put other objects only if Credentials match
			if (userObject.getStatus().equals(SharedConstants.SUCCESS))
			{
				// Check if user is assigned with atleast with 1 NE
				Object obj = DbManager.instance.selectOneAsObject("USERNEMAP.SELECT.NETYPE", userName);
				if (null == obj)
				{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("No NE's are assigned for " + userName);
					return userObject;
				}
				// get the Privilege Hash
				HashMap<String, String> privHash = getUserPrivilegeMap(userObject);

				// put the ActiveUser in the queue for ActiveUserSessions
				ActiveUserSessions.instance.addNewActiveUser(userObject, clientIp);

				// clear login attempted
				userObject.setLoginAttempts(0);

				// set the main menu based upon privileges as the data object

				HashMap<String, String> hashOfData = new HashMap<String, String>();
				hashOfData.put("autoLogoutTime", ""+userObject.getDataObject());
				// Inject Server Type (Primary / Secondary)
				String serverType = PrBeanFactory.instance.getProjectConfig().getServerType();
				hashOfData.put("serverType", serverType);
				userObject.setDataObject(hashOfData);
			}
		} catch (Exception e)
		{

			LoggerService.instance.error(e.getMessage(), LoginSupport.class, e);
			// remove the user from ActiveUser
			ActiveUserSessions.instance.removeActiveUser(userName);
		}
		return userObject;
	}

	/**
	 * Api to check the user credentails before allowing him to login
	 * 
	 * @param userObject
	 * @param clientIp
	 * @return
	 */
	public UserObject checkUserCredentialsOnLogin(UserObject userObject, String clientIp)
	{
		String userName = userObject.getUserName();
		final String password = userObject.getPassword();
		try
		{
			String currentServerCtx = PrStartupManager.instance.getCurrentCtx();

			// Allow login only when the state is RUNNING..
			if (!currentServerCtx.equals(ProjectStartupConstants.STARTUP_DEFAULT_DONE))
			{
				userObject.setStatus(SharedConstants.FAILURE);
				String descr = PrStartupManager.instance.getCtxDescr();
				userObject.setStatusMsg("Server Starting up..Current State = " + descr + " Please retry after some time..");
				return userObject;
			}
			return userObject;
		} catch (final Exception e)
		{
			LoggerService.instance.error("Error in checkUserCredentialsOnLogin()", LoginSupport.class, e);
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg(e.getMessage());

			ActiveUserSessions.instance.removeActiveUser(userName);
			return userObject;
		}
	}

	/**
	 * API to Logout User
	 * 
	 * @param userObject
	 * @return userObject
	 */
	public boolean logOutUser(UserObject userObject, String logoutCtx)
	{
		boolean retVal = false;
		retVal = ActiveUserSessions.instance.removeActiveUser(userObject.getUserName());
		return retVal;
	}

	/**
	 * 
	 * Api to Force Logout List of Users with the reason (message) for the logout
	 * 
	 * @param userObject
	 * @param message
	 */
	public void forceLogout(String userName, String message)
	{
		// User Name, Force Logout Reason
		ServerPushFactory.instance.getUserSessionManager().sendServerPush("", "", ServerPushConstants.FORCE_LOGOUT, userName, message);
	}
	
	public HashMap<String, String> getUserPrivilegeMap(UserObject userObject) throws Exception
    {
	String userId = userObject.getUserId();
	List<HashMap<String, String>> hashList = DbManager.instance.selectAllAsHashMap("ACTIVIT_PRIVILEGE_MAP.USER", userId);

	HashMap<String, String> privMap = new HashMap<String, String>();

	for (HashMap<String, String> map : hashList)
	{
	    privMap.put(map.get("activity_id"), map.get("operation"));
	}

	privMap.put("1", "1");
	List<Object> privList = DbManager.instance.selectAllAsObject("ACTIVIT_PRIVILEGE_MAP.USER", userId);

	HashMap<String, String> defaultPrivileges = new HashMap<String, String>();
	defaultPrivileges.put("operation", "1");
	defaultPrivileges.put("activity_id", "1");

	privList.add(defaultPrivileges);

	userObject.setPrivilegeList(privList);

	return privMap;
    }
}
