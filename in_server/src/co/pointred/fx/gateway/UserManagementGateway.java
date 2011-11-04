package co.pointred.fx.gateway;

import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.ProjectConstants;
import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.fx.dataobjects.UserObject;

/**
 * Flex Gateway to perform user management tasks
 * 
 * @author kandeepa
 * 
 */

public class UserManagementGateway
{
	/**
	 * API to Test Flex-Java Connection Establishment
	 * 
	 * @return connection status
	 */
	public boolean testConnection()
	{
		return true;
	}

	/**
	 * API to Login User
	 * 
	 * @param userObject
	 * @return userObject
	 */
	public UserObject loginUser(UserObject userObject)
	{
//		return LoginSupport.instance.loginUser(userObject);
		userObject.setSessionId("sadasdsadad");
		
		userObject.setStatus(SharedConstants.SUCCESS);
		return userObject;
	}

	/**
	 * API to Logout User
	 * 
	 * @param userObject
	 * @return userObject
	 */
	public boolean logOutUser(UserObject userObject, String logoutCtx)
	{
		return LoginSupport.instance.logOutUser(userObject, logoutCtx);
	}

	/**
	 * API to fetch the privileges assigned to particular user
	 * 
	 * @param userObject
	 * @return list of activity privileges
	 */
	public List<Object> fetchUserPrivilege(UserObject userObject)
	{
		List<Object> objList = null;
		try
		{
			String stmtName = ((HashMap<String, String>) userObject.getDataObject()).get("IBATIS_STATEMENT");
			String userId = ((HashMap<String, String>) userObject.getDataObject()).get("selectedUserId");

			objList = DbManager.instance.selectAllAsObject(stmtName, userId);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return objList;
	}

	/**
	 * 
	 * Api to Force Logout List of Users with the reason (message) for the logout
	 * 
	 * @param userObject
	 * @param message
	 */
	public void forceLogout(UserObject userobject, String message)
	{
		List<String> arrList = (List<String>) userobject.getDataObject();
		for (String userName : arrList)
		{
			LoginSupport.instance.forceLogout(userName, message);
		}
	}
}
