package co.pointred.core.startup;

import org.jboss.remoting.Client;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.InvokerRegistry;
import org.jboss.remoting.transport.ClientInvoker;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;


/**
 * Remoting client which communicates to the Secondary Remoting Client
 * 
 * @author shajee
 * 
 */
class RemotingClientPrimary
{
    private static final String MODULE_NAME = "[ EMS START UP ] - REMOTING CLIENT PRIMARY ";

    private static String transport = "socket";
    private static int port = ProjectStartupConstants.JBOSS_REMOTING_SERVER_CEMS;
    private String locatorURI = null;
    private InvokerLocator locator = null;
    private String host = "";

    private Client remotingClient;

    private volatile boolean connectionAlive = false;

    public RemotingClientPrimary()
    {

    }

    protected boolean startPrimaryRemotingClient() throws Throwable
    {
	boolean retval=true;
	try
	{
	    this.host = PrStartupManager.instance.getSecondaryServerName();
	    if (this.host.equals("0.0.0.0"))
		return false;

	    this.locatorURI = transport + "://" + host + ":" + port;
	    // create InvokerLocator with the url type string indicating the target
	    // remoting server to call upon.
	    this.locator = new InvokerLocator(locatorURI);

	    this.remotingClient = new Client(locator);
	    this.remotingClient.connect();
	    Object response = this.remotingClient.invoke(RemotingMsg.PING);
	    this.remotingClient.disconnect();
	    logWarn("startPrimaryRemotingClient()", "Started PRIMARY REMOTING CLIENT with locator uri of: " + locatorURI + " Resp Got = " + response);

	    // set the connection flag
	    this.connectionAlive = true;
	} catch (Exception e)
	{
	    retval=false;
	    LoggerService.instance.error("Error in StartPrimaryRemotingClient()", RemotingClientPrimary.class, e);
	}
	
	return retval;
    }

    protected void stopPrimaryRemotingClient()
    {
	if (this.remotingClient.isConnected())
	    this.remotingClient.disconnect();

	this.remotingClient = null;
	logWarn("startPrimaryRemotingClient()", "Stopped PRIMARY REMOTING CLIENT...");
    }

    // sends the REMOTING message to secondary..
    protected RemotingMsg sendRemotingMsgToSecondaryServer(RemotingMsg msg)
    {
	Object response = RemotingMsg.ERROR;
	if (connectionAlive == false)
	{
	    if (reconnectClient() == true)
	    {
		try
		{
		    this.remotingClient.connect();
		    response = this.remotingClient.invoke(msg);
		    this.remotingClient.disconnect();
		    connectionAlive = true;

		} catch (Throwable e)
		{
		    connectionAlive = false;
		    response = RemotingMsg.ERROR;
		}
	    }
	} else
	{
	    try
	    {
		this.remotingClient.connect();
		response = this.remotingClient.invoke(msg);
		this.remotingClient.disconnect();
		connectionAlive = true;
	    } catch (Throwable e)
	    {
		connectionAlive = false;
		response = RemotingMsg.ERROR;
	    }
	}
	return (RemotingMsg) response;
    }

    // sends the STARTUP message to secondary..
    protected StartUpMsg sendStartupMsgToSecondaryServer(StartUpMsg startUpMsg) throws Throwable
    {
	Object response = StartUpMsg.START_UP_ERROR;
	if (connectionAlive == false)
	{
	    if (reconnectClient() == true)
	    {
		try
		{
		    this.remotingClient.connect();
		    response = this.remotingClient.invoke(startUpMsg);
		    this.remotingClient.disconnect();
		    connectionAlive = true;

		} catch (Throwable e)
		{
		    connectionAlive = false;
		    response = StartUpMsg.START_UP_ERROR;
		}
	    }
	} else
	{
	    try
	    {
		this.remotingClient.connect();
		response = this.remotingClient.invoke(startUpMsg);
		this.remotingClient.disconnect();
		connectionAlive = true;
	    } catch (Throwable e)
	    {
		connectionAlive = false;
		response = StartUpMsg.START_UP_ERROR;
	    }
	}
	return (StartUpMsg) response;
    }

    // sends the RECOVERY message to secondary..
    protected RecoveryMsgCarrier sendRecoveryMsgToSecondaryServer(RecoveryMsgCarrier recoveryMsgCarrier)
    {
	Object response = new RecoveryMsgCarrier(RecoveryMsg.CONNECTION_ERROR);
	if (connectionAlive == false)
	{
	    if (reconnectClient() == true)
	    {
		try
		{
		    this.remotingClient.connect();
		    response = this.remotingClient.invoke(recoveryMsgCarrier);
		    this.remotingClient.disconnect();
		    connectionAlive = true;

		} catch (Throwable e)
		{
		    connectionAlive = false;
		    response = new RecoveryMsgCarrier(RecoveryMsg.CONNECTION_ERROR);
		}
	    }
	} else
	{
	    try
	    {
		this.remotingClient.connect();
		response = this.remotingClient.invoke(recoveryMsgCarrier);
		this.remotingClient.disconnect();
		connectionAlive = true;
	    } catch (Throwable e)
	    {
		connectionAlive = false;
		response = new RecoveryMsgCarrier(RecoveryMsg.CONNECTION_ERROR);
	    }
	}
	return (RecoveryMsgCarrier) response;
    }

    private boolean reconnectClient()
    {
	cleanup();
	boolean retVal = false;
	this.locator = null;
	this.locatorURI = null;
	this.remotingClient = null;
	try
	{
	    this.locatorURI = transport + "://" + host + ":" + port;
	    this.locator = new InvokerLocator(locatorURI);
	    this.remotingClient = new Client(locator);
	    this.remotingClient.connect();
	    // just a string msg..
	    this.remotingClient.invoke(RemotingMsg.PING);
	    this.remotingClient.disconnect();
	    connectionAlive = true;
	    retVal = true;
	    logWarn("reconnectClient()", "Re-connected to Secondary Server - ");
	} catch (Throwable e)
	{
	    // no need to log.. just not able to re-connect to secondary
	}
	return retVal;
    }

    private void cleanup()
    {
	// ServerInvoker[] serverInvokers = InvokerRegistry.getServerInvokers();
	// for (ServerInvoker invoker : serverInvokers)
	// {
	// invoker.stop();
	// invoker.destroy();
	// InvokerRegistry.destroyServerInvoker(invoker);
	// }
	ClientInvoker[] clientInvokers = InvokerRegistry.getClientInvokers();
	for (ClientInvoker invoker : clientInvokers)
	{
	    InvokerRegistry.destroyClientInvoker(invoker.getLocator(), null);
	}
    }

    protected boolean isConnectionEstablished()
    {
	return connectionAlive;
    }

    protected void setConnectionEstablished(boolean connectionEstablished)
    {
	this.connectionAlive = connectionEstablished;
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(msg, RemotingClientPrimary.class);
    }
}