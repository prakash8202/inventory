package co.pointred.fx.serverpush.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import co.pointred.core.config.ConfigPath;
import co.pointred.core.constants.ProjectConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.utils.CastorXmlUtil;
import co.pointred.fx.dataobjects.ServerPushRequestObject;

/**
 * Class responsible for starting/stopping Xml Socket Server
 * 
 * @author kandeepasundaram
 * 
 */
public enum XmlServerSocketController
{
    instance;

    // XML Socket Port
    private static final int XML_SOCKET_PORT = 4321;

    private ServerSocket serverSocket;
    private Thread xmlSocketServer;
    private ConfigPath configPath;

    private volatile AtomicBoolean listening = new AtomicBoolean(false);

    private static ApplicationContext castorContext = new ClassPathXmlApplicationContext(ProjectConstants.CASTOR_XML_CONFIG);

    /**
     * API to start XML Socket server port
     * 
     * @return
     */
    public boolean startXmlSocket()
    {
	boolean retVal = false;

	LoggerService.instance.warning("Starting XMl Socket Server @ port : " + XML_SOCKET_PORT, XmlServerSocketController.class);

	setListening(true);

	xmlSocketServer = new Thread(new XmlSocketServer());
	xmlSocketServer.start();

	if (serverSocket != null && serverSocket.isClosed() == false)
	{
	    retVal = true;
	}

	return retVal;
    }

    /**
     * API to stop XML Socket server port
     * 
     * @return
     */
    public boolean stopXmlSocket()
    {
	boolean retVal = false;

	try
	{
	    if (serverSocket != null && serverSocket.isClosed() == false)
	    {
		setListening(false);
		serverSocket.close();

		if (serverSocket.isClosed() == true)
		{
		    retVal = true;
		}
	    }
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error Stopping XML Socket [" + XML_SOCKET_PORT + "] ", XmlServerSocketController.class, e);
	}

	return retVal;
    }

    public ServerPushRequestObject getServerPushObject(String xmlString)
    {
	ServerPushRequestObject serverPushObject = new ServerPushRequestObject();
	CastorXmlUtil castorXmlUtil = (CastorXmlUtil) castorContext.getBean("castorGenerator");

	Resource xmlFile = new ByteArrayResource(xmlString.getBytes());

	serverPushObject = (ServerPushRequestObject) castorXmlUtil.convertXmlToObject(xmlFile);

	return serverPushObject;
    }

    private boolean isListening()
    {
	return listening.get();
    }

    public void setListening(boolean isListening)
    {
	this.listening.set(isListening);
    }

    /**
     * Server Class For Xml Socket
     * 
     * @author kandeepasundaram
     * 
     */
    private class XmlSocketServer implements Runnable
    {

	@Override
	public void run()
	{
	    try
	    {
		serverSocket = new ServerSocket(XML_SOCKET_PORT);

		while (isListening() == true)
		{
		    // this will throw an exception with the server socket is closed !!!
		    // will block until anything is gotten from client
		    Socket client = serverSocket.accept();

		    if (null != client && true == client.isConnected())
		    {
			LoggerService.instance.warning("Starting Client Socket Thread For Client [IP Address : " + client.getInetAddress() + ":" + client.getPort() + "]",
				XmlServerSocketController.class);

			Thread clientSocketThread = new Thread(new ServerPushHandler(client), "Client Socket : " + client.getInetAddress() + ":" + client.getPort());
			clientSocketThread.start();
		    }
		}
	    } catch (Exception e)
	    {
		LoggerService.instance.error("Error in starting XML client Socket [" + XML_SOCKET_PORT + "].  XmlServerSocket is Closed == " + serverSocket.isClosed(),
			XmlServerSocketController.class, e);
	    }
	}
    }
}
