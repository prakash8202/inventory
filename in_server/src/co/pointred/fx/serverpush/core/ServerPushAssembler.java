package co.pointred.fx.serverpush.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import co.pointred.core.log.LoggerService;
import co.pointred.fx.dataobjects.ServerPushObject;

/**
 * Class responsible for controlling producer/consumer for Server Push
 * 
 * @author kandeepa
 * 
 */
public class ServerPushAssembler
{
    private final LinkedBlockingQueue<ServerPushObject> producer = new LinkedBlockingQueue<ServerPushObject>();

    // Vector Of services registered by the user
    private final Vector<String> registeredServicesVector = new Vector<String>();
    private String userName;
    private Socket clientSocket;
    private Consumer consumer;

    /**
     * API to add ServerPushObject to producer queue
     * 
     * @param serverPushObject
     */
    public void addToProducerQueue(ServerPushObject serverPushObject)
    {
	producer.add(serverPushObject);
    }

    /**
     * API to register a new service to registeredServicesVector
     * 
     * @param reqCtx
     */
    public void addToRegisteredServices(String reqCtx)
    {
	registeredServicesVector.add(reqCtx);
    }

    /**
     * API to unregister a service from registeredServicesVector
     * 
     * @param reqCtx
     */
    public void removeFromRegisteredServices(String reqCtx)
    {
	try
	{
	    registeredServicesVector.remove(reqCtx);
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error Unregistering Service " + reqCtx + " for user : " + userName, ServerPushAssembler.class, e);
	}
    }

    /**
     * API to poison producer queue which in turn will stop the consumer thread
     */
    public void poisonProducer()
    {
	try
	{
	    LoggerService.instance.warning("Poisoning Producer For User : " + this.userName, ServerPushHandler.class);

	    this.clientSocket.close();

	    ServerPushObject serverPushObject = new ServerPushObject();
	    serverPushObject.setRequestCtx("POISON");
	    producer.add(serverPushObject);
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error Poisoning Server Push Consumer for user : " + userName, ServerPushAssembler.class, e);
	}
    }

    /**
     * API to start consumer thread
     */
    public void startConsumerThread()
    {
	consumer = new Consumer();
	consumer.setName(getUserName() + ":SERVER_PUSH_ASSEMBLER");
	consumer.start();
    }

    /**
     * Consumer class responsible for processing serverpushobjects in the producer queue
     * 
     * @author kandeepa
     * 
     */
    private class Consumer extends Thread
    {
	boolean keepConsuming = true;

	@Override
	public void run()
	{
	    while (keepConsuming == true)
	    {
		try
		{
		    ServerPushObject serverPushObject = producer.take();
		    String neType = serverPushObject.getNeType();
		    String neKey = serverPushObject.getNeKey();
		    if (false == serverPushObject.getRequestCtx().equals("POISON"))
		    {
			if (null != clientSocket && true == clientSocket.isConnected())
			{
			    if (serverPushObject.getRequestCtx().equals(ServerPushConstants.USER_SESSION_MONITOR))
			    {
				if (serverPushObject.getNeType().equals(ServerPushConstants.FORCE_LOGOUT))
				{
				    if (userName.equals(serverPushObject.getNeKey()))
				    {
					pushData(serverPushObject);
				    }
				} else
				{
				    if (null != clientSocket && false == clientSocket.isClosed())
				    {
					pushData(serverPushObject);
				    }
				}
			    } else
			    {
//				if (UserManagementSupport.instance.isNeInScope(userName, neType, neKey) == true)
//				{
//				    pushData(serverPushObject);
//				}
			    }
			} else
			{
			    // Log it
			}
		    } else
		    {
			keepConsuming = false;
		    }
		} catch (Exception e)
		{
		    LoggerService.instance.error("Starting Consumer Thread", ServerPushAssembler.class, e);
		}
	    }
	}

	private void pushData(ServerPushObject serverPushObject) throws IOException
	{
	    OutputStream outputStream = clientSocket.getOutputStream();
	    outputStream.write(serverPushObject.getXmlStr().getBytes());
	    outputStream.write((byte) 0);
	    outputStream.flush();
	    // No need to close .... will be closed when the socket is closed
	    // outputStream.close();
	}
    }

    public String getUserName()
    {
	return userName;
    }

    public void setUserName(String userName)
    {
	this.userName = userName;
    }

    public Socket getClientSocket()
    {
	return clientSocket;
    }

    public void setClientSocket(Socket clientSocket)
    {
	this.clientSocket = clientSocket;
    }

}
