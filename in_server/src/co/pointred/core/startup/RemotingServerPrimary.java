package co.pointred.core.startup;

import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;

import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.ServerInvocationHandler;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.transport.Connector;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;

class RemotingServerPrimary
{
	private static final String MODULE_NAME = "[ EMS START UP ] - REMOTING SERVER PRIMARY ";
	// Default locator values
	private static final String transport = "socket";
	private static final int port = ProjectStartupConstants.JBOSS_REMOTING_SERVER_CEMS;
	private InvokerLocator locator;
	private Connector connector;
	// create the handler to receive the invocation request from the client for processing
	private PrimaryServerInvocationHandler primaryServerInvocationHandler = new PrimaryServerInvocationHandler();

	// Message Handlers..
	private final MessageHandlerIface msgHandlerPrimaryRemoting = new MsgHndlrPrimaryRemoting();
	private final MessageHandlerIface msgHandlerPrimaryStartup = new MsgHndlrPrimaryStartup();
	private final MessageHandlerIface msgHandlerPrimaryRecovery = new MsgHndlrRecovery();

	private final ConcurrentHashMap<String, MessageHandlerIface> msgHandlerHashPrimary = new ConcurrentHashMap<String, MessageHandlerIface>();

	protected RemotingServerPrimary()
	{
		populateMsgHandlerHash();
	}
	
	private void populateMsgHandlerHash()
	{
		this.msgHandlerHashPrimary.put(RemotingMsg.class.getSimpleName(), msgHandlerPrimaryRemoting);
		this.msgHandlerHashPrimary.put(StartUpMsg.class.getSimpleName(), msgHandlerPrimaryStartup);
		this.msgHandlerHashPrimary.put(RecoveryMsgCarrier.class.getSimpleName(), msgHandlerPrimaryRecovery);
	}
	

	protected void startPrimaryRemotingServer() throws Exception
	{
		final String host = PrStartupManager.instance.getPrimaryServerName();
		final String locatorURI = transport + "://" + host + ":" + port;

		// create the InvokerLocator based on url string format to indicate the transport, host, and port to use for the server invoker.
		this.locator = new InvokerLocator(locatorURI);

		this.connector = new Connector(locator);

		// creates all the connector's needed resources, such as the server invoker
		this.connector.create();

		// first parameter is sub-system name. can be any String value.
		this.connector.addInvocationHandler("PrimaryServer", primaryServerInvocationHandler);

		// start with a new non daemon thread so server will wait for request and not exit
		this.connector.start();
		logWarn("startPrimaryRemotingServer()", "Started PRIMARY REMOTING SERVER with locator uri : " + locatorURI);
	}

	protected void stopPrimaryRemotingServer() throws Exception
	{
		connector.removeInvocationHandler("PrimaryServer");
		connector.destroy();
		connector = null;
		logWarn("stopPrimaryRemotingServer()", "Stopped PRIMARY REMOTING SERVER..");
	}

	private void logWarn(String apiName, String msg)
	{
		LoggerService.instance.warning(msg, RemotingServerPrimary.class);
	}

	/**
	 * Simple invocation handler implementation. This is the code that will be called with the invocation payload from the client.
	 */
	class PrimaryServerInvocationHandler implements ServerInvocationHandler
	{
		@Override
		public Object invoke(InvocationRequest invocation) throws Throwable
		{
			Object msgObj = invocation.getParameter();
			return msgHandlerHashPrimary.get(msgObj.getClass().getSimpleName()).invoke(invocation);
		}

		@Override
		public void addListener(InvokerCallbackHandler arg0)
		{

		}

		@Override
		public void removeListener(InvokerCallbackHandler arg0)
		{

		}

		@Override
		public void setInvoker(ServerInvoker arg0)
		{

		}

		@Override
		public void setMBeanServer(MBeanServer arg0)
		{

		}

	}
}