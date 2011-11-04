package co.pointred.core.startup;

import org.jboss.remoting.Client;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.InvokerRegistry;
import org.jboss.remoting.transport.ClientInvoker;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;

class RemotingClientSecondary
{

	private static final String MODULE_NAME = "[ EMS START UP ] - REMOTING CLIENT SECONDARY ";

	private static String transport = "socket";
	private static int port = ProjectStartupConstants.JBOSS_REMOTING_SERVER_CEMS;
	private String locatorURI = null;
	private InvokerLocator locator = null;
	private String host = "";

	private Client remotingClient;

	private volatile boolean connectionAlive = false;
	

	public RemotingClientSecondary()
	{

	}

	protected void startSecondaryRemotingClient() throws Throwable
	{
		this.host = PrStartupManager.instance.getPrimaryServerName();
		if (this.host.equals("0.0.0.0"))
			return;
		
		this.locatorURI = transport + "://" + host + ":" + port;
		// create InvokerLocator with the url type string indicating the target remoting server to call upon.
		this.locator = new InvokerLocator(locatorURI);

		this.remotingClient = new Client(locator);
		this.remotingClient.connect();
		Object response = this.remotingClient.invoke(RemotingMsg.PING);
		this.remotingClient.disconnect();
		logWarn("startSecondaryRemotingClient()", "Started SECONDARY REMOTING CLIENT with locator uri of: " + locatorURI + " Resp Got = " + response);

		// set the connection flag
		this.connectionAlive = true;
	}

	protected void stopSecondaryRemotingClient()
	{
		if (this.remotingClient.isConnected())
			this.remotingClient.disconnect();

		this.remotingClient = null;
		logWarn("startSecondaryRemotingClient()", "Stopped SECONDARY REMOTING CLIENT...");
	}

	// sends the REMOTING message to Primary..
	protected RemotingMsg sendRemotingMsgToPrimaryServer(RemotingMsg msg) 
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

	// sends the STARTUP message to primary..
	protected StartUpMsg sendStartupMsgToPrimaryServer(StartUpMsg startUpMsg)
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

	// sends the RECOVERY message to Primary..
	protected RecoveryMsgCarrier sendRecoveryMsgToPrimaryServer(RecoveryMsgCarrier recoveryMsgCarrier) 
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
			logWarn("reconnectClient()", "Re-connected to PRIMARY Server - ");
		} catch (Throwable e)
		{
			// no need to log.. just failed to re-connect to primary 
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
		LoggerService.instance.warning(msg, RemotingClientSecondary.class);
	}

}