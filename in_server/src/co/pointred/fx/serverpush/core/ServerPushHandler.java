package co.pointred.fx.serverpush.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import co.pointred.core.log.LoggerService;
import co.pointred.core.utils.Console;
import co.pointred.fx.dataobjects.ServerPushRequestObject;
import co.pointred.fx.users.ActiveUser;
import co.pointred.fx.users.ActiveUserSessions;

/**
 * Class responsible for listening to an established client-server socket connection
 * 
 * @author kandeepa
 * 
 */
public class ServerPushHandler implements Runnable
{
    private Socket clientSocket;
    private String reqCtx;
    private String userName;

    public ServerPushHandler(Socket clientSocket)
    {
	this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
	StringBuffer requestString = new StringBuffer();
	ServerPushRequestObject serverPushRequestObject = null;
	boolean isPolicyFileRequest = false;
	try
	{
	    InputStream inputStream = clientSocket.getInputStream();
	    OutputStream outputStream = clientSocket.getOutputStream();

	    byte byteValue;
	    boolean listening = true;

	    while (true == listening)
	    {
		byteValue = (byte) inputStream.read();
		if (byteValue < 0)
		{
		    listening = false;
		    break;
		}

		do
		{
		    if (byteValue == 0)
		    {
			String request = requestString.toString();

			if (request.equalsIgnoreCase("<policy-file-request/>"))
			{
			    // If the request is for Server Policy File
			    LoggerService.instance.warning("Serving policy file to client...." + clientSocket.getInetAddress().getHostAddress(), ServerPushHandler.class);
			    // <cross-domain-policy><allow-access-from domain="*" to-ports="*" /></cross-domain-policy>
			    isPolicyFileRequest = true;
			    outputStream.write("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>".getBytes());
			} else
			{
			    // If the request is for a Server Push service
			    LoggerService.instance.warning("Registering For Service...." + clientSocket.getInetAddress().getHostAddress(), ServerPushHandler.class);
			    isPolicyFileRequest = false;
			    serverPushRequestObject = XmlServerSocketController.instance.getServerPushObject(request);
			    this.reqCtx = serverPushRequestObject.getRequestCtx();
			    this.userName = serverPushRequestObject.getUserName();
			    Console.print(request);
			    LoggerService.instance.warning("Registering For Service : " + serverPushRequestObject.getRequestCtx() + " for User " + this.userName,
				    ServerPushHandler.class);

			    // Assembler Part
			    String userName = serverPushRequestObject.getUserName();
			    ConcurrentHashMap<String, ActiveUser> hashOfRegisteredUsers = ActiveUserSessions.instance.getHashOfLoggedInUsers();
			    if (true == hashOfRegisteredUsers.containsKey(userName) && false == ActiveUserSessions.instance.isServerPushInitialized(userName))
			    {
				// Initialize a client socket
				ActiveUserSessions.instance.initializeClientSocket(userName, clientSocket);
				LoggerService.instance.warning("Connecting for ServerPush for User " + this.userName + " @Port " + clientSocket.getInetAddress().getHostAddress(),
					ServerPushHandler.class);
			    }
			    // Add the service to user's register services list
			    ActiveUserSessions.instance.addToRegisteredServices(userName, serverPushRequestObject.getRequestCtx());
			    request = "";
			    requestString = new StringBuffer();
			}
			// Write 0 byte to specify end of data
			outputStream.write((byte) 0);
			// Flush stream to client
			outputStream.flush();
		    } else
		    {
			byte[] buffer = new byte[1];
			buffer[0] = byteValue;
			requestString.append(new String(buffer));
		    }
		} while (false == clientSocket.isClosed() && (byteValue = (byte) inputStream.read()) > -1);
	    }
	} catch (Exception e)
	{
	    if (e instanceof java.net.SocketException)
		LoggerService.instance.warning("Socket Closed..for user " + userName, ServerPushHandler.class);
	    else
		LoggerService.instance.error("Processing client Request...", ServerPushHandler.class, e);
	} finally
	{
	    closeClient(serverPushRequestObject, isPolicyFileRequest);
	}
    }

    /**
     * API to close a clientSocket
     * 
     * @param serverPushObject
     * @param isPolicyFileRequest
     */
    private void closeClient(ServerPushRequestObject serverPushObject, boolean isPolicyFileRequest)
    {
	try
	{
	    if (false == isPolicyFileRequest)
	    {
		if (null != this.userName && true == ActiveUserSessions.instance.getHashOfLoggedInUsers().containsKey(userName))
		{
		    // Remove the corresponding service from the user's hashOfRegisteredServices
		    // ActiveUserSessions.instance.getHashOfLoggedInUsers().get(this.userName).getServerPushAssembler().removeFromRegisteredServices(this.reqCtx);
		    ActiveUserSessions.instance.removeActiveUser(userName);
		}
	    }
	    // Close the clientSocket [ also will close any input/output Streams opened for the socket]
	    clientSocket.close();
	} catch (Exception e)
	{
	    LoggerService.instance.error("Closing Client Socket ", ServerPushHandler.class, e);
	}
    }
}
