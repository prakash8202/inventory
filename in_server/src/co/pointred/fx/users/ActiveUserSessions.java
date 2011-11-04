package co.pointred.fx.users;

import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import co.pointred.core.constants.SharedConstants;
import co.pointred.fx.dataobjects.ServerPushObject;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.gateway.LoginSupport;

/**
 * Class responsible managing the User based Data/Property Cache
 * 
 * @author kandeepa
 * 
 */
public enum ActiveUserSessions
{
	instance;

	// Hashmap with userName as Key & corresponding ActiveUser Object as value
	ConcurrentHashMap<String, ActiveUser> hashOfLoggedInUsers = new ConcurrentHashMap<String, ActiveUser>();

	public ConcurrentHashMap<String, ActiveUser> getHashOfLoggedInUsers()
	{
		return hashOfLoggedInUsers;
	}

	public Set<String> getUserNamesLoggedIn()
	{
		return hashOfLoggedInUsers.keySet();
	}

	public void setHashOfLoggedInUsers(ConcurrentHashMap<String, ActiveUser> hashOfLoggedInUsers)
	{
		this.hashOfLoggedInUsers = hashOfLoggedInUsers;
	}

	public ActiveUser getActiveUserObject(String userName)
	{
		return this.hashOfLoggedInUsers.get(userName);
	}

	/**
	 * API to add a new user logged-in to hashOfLoggedInUsers
	 * 
	 * @param userObject
	 * @param clientIpAddress
	 * @return
	 */
	public UserObject addNewActiveUser(UserObject userObject, String clientIpAddress)
	{
		String userName = userObject.getUserName();
		if (false == hashOfLoggedInUsers.containsKey(userName))
		{
			ActiveUser activeUser = new ActiveUser();

			activeUser.setUserId(userObject.getUserId());
			activeUser.setUserName(userName);
			activeUser.setIpAddress(clientIpAddress);
			activeUser.setSessionId(userObject.getSessionId());

			// server push for the client
			activeUser.getServerPushAssembler().setUserName(userName);
			activeUser.getServerPushAssembler().startConsumerThread();

			this.hashOfLoggedInUsers.put(userName, activeUser);

			return userObject;

		} else
		{
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg(userObject.getUserName() + " already logged in");
			return userObject;
		}
	}

	/**
	 * API to remove a logged-out user from hashOfLoggedInUsers
	 * 
	 * @param userName
	 * @return
	 */
	public boolean removeActiveUser(String userName)
	{
		if (true == hashOfLoggedInUsers.containsKey(userName))
		{
			hashOfLoggedInUsers.get(userName).getServerPushAssembler().poisonProducer();
			hashOfLoggedInUsers.remove(userName);
		}
		return !hashOfLoggedInUsers.containsKey(userName);
	}

	/**
	 * API to check whether an user is already logged-in in another terminal
	 * 
	 * @param userName
	 * @return
	 */
	public boolean isUserAlreadyLoggedIn(String userName)
	{
		return hashOfLoggedInUsers.containsKey(userName);
	}

	/**
	 * API to initiate server push by adding serverpush object to producer queue of all users logged-in
	 * 
	 * @param xmlStr
	 * @param neKey
	 * @param neType
	 * @param requestCtx
	 */
	public void sendServerPush(ServerPushObject serverPushObject)
	{
		for (Map.Entry<String, ActiveUser> entry : hashOfLoggedInUsers.entrySet())
		{
			entry.getValue().getServerPushAssembler().addToProducerQueue(serverPushObject);
		}
	}

	public boolean isServerPushInitialized(String userName)
	{
		return hashOfLoggedInUsers.get(userName).getServerPushAssembler().getClientSocket() != null;
	}

	public void initializeClientSocket(String userName, Socket clientSocket)
	{
		hashOfLoggedInUsers.get(userName).getServerPushAssembler().setClientSocket(clientSocket);
	}

	/**
	 * API to register a new service for an user to registeredServicesVector
	 * 
	 * @param userName
	 * @param requestCtx
	 * @param clientSocket
	 */
	public void addToRegisteredServices(String userName, String requestCtx)
	{
		hashOfLoggedInUsers.get(userName).getServerPushAssembler().addToRegisteredServices(requestCtx);
	}

	public void forceLogoutAllUsers(String logoutMsg)
	{
		for (String user : hashOfLoggedInUsers.keySet())
		{
			LoginSupport.instance.forceLogout(user, logoutMsg);
		}
	}
}
