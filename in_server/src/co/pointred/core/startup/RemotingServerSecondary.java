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

class RemotingServerSecondary
{

	private static final String MODULE_NAME = "[ EMS START UP ] - REMOTING SERVER SECONDARY ";
	// Default locator values
	private static final String transport = "socket";
	private static final int port = ProjectStartupConstants.JBOSS_REMOTING_SERVER_CEMS;
	private InvokerLocator locator;
	private Connector connector;
	// create the handler to receive the invocation request from the client for processing
	private SecondaryServerInvocationHandler secondaryServerInvocationHandler = new SecondaryServerInvocationHandler();

	// Message Handlers..
	private final MessageHandlerIface msgHandlerSecondaryRemoting = new MsgHndlrSecondaryRemoting();
	private final MessageHandlerIface msgHandlerSecondaryStartup = new MsgHndlrSecondaryStartup();
	private final MessageHandlerIface msgHandlerSecondaryRecovery = new MsgHndlrRecovery();

	private final ConcurrentHashMap<String, MessageHandlerIface> msgHandlerHashSecondary = new ConcurrentHashMap<String, MessageHandlerIface>();

	protected RemotingServerSecondary()
	{
		populateMsgHandlerHash();
	}

	private void populateMsgHandlerHash()
	{
		this.msgHandlerHashSecondary.put(RemotingMsg.class.getSimpleName(), msgHandlerSecondaryRemoting);
		this.msgHandlerHashSecondary.put(StartUpMsg.class.getSimpleName(), msgHandlerSecondaryStartup);
		this.msgHandlerHashSecondary.put(RecoveryMsgCarrier.class.getSimpleName(), msgHandlerSecondaryRecovery);
	}

	protected void startSecondaryRemotingServer() throws Exception
	{
		final String host = PrStartupManager.instance.getSecondaryServerName();
		final String locatorURI = transport + "://" + host + ":" + port;

		// create the InvokerLocator based on url string format to indicate the transport, host, and port to use for the server invoker.
		this.locator = new InvokerLocator(locatorURI);

		this.connector = new Connector(locator);

		// creates all the connector's needed resources, such as the server invoker
		this.connector.create();

		// first parameter is sub-system name. can be any String value.
		this.connector.addInvocationHandler("SecondaryServer", secondaryServerInvocationHandler);

		// start with a new non daemon thread so server will wait for request and not exit
		this.connector.start();
		logWarn("startSecondaryRemotingServer()", "Started SECONDARY REMOTING SERVER with locator uri : " + locatorURI);
	}

	protected void stopSecondaryRemotingServer() throws Exception
	{
		connector.removeInvocationHandler("SecondaryServer");
		connector.destroy();
		connector = null;
		logWarn("stopSecondaryRemotingServer()", "Stopped SECONDARY REMOTING SERVER..");
	}

	private void logWarn(String apiName, String msg)
	{
		LoggerService.instance.warning(msg, RemotingServerSecondary.class);
	}

	/**
	 * Simple invocation handler implementation. This is the code that will be called with the invocation payload from the client.
	 */
	class SecondaryServerInvocationHandler implements ServerInvocationHandler
	{
		public Object invoke(InvocationRequest invocation) throws Throwable
		{
			Object msgObj = invocation.getParameter();
			return msgHandlerHashSecondary.get(msgObj.getClass().getSimpleName()).invoke(invocation);
		}

		public void addListener(InvokerCallbackHandler callbackHandler)
		{
			// NO OP as do not handling callback listeners in this example
		}

		public void removeListener(InvokerCallbackHandler callbackHandler)
		{
			// NO OP as do not handling callback listeners in this example
		}

		public void setMBeanServer(MBeanServer server)
		{
			// NO OP as do not need reference to MBeanServer for this handler
		}

		public void setInvoker(ServerInvoker invoker)
		{
			// NO OP as do not need reference back to the server invoker
		}
	}

}