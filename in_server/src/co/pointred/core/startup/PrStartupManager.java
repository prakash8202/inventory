package co.pointred.core.startup;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;
import co.pointred.core.utils.Console;


public enum PrStartupManager
{
    instance;

    private JMXConnectorServer jmxc_server;
    ObjectName serverName;
    MBeanServer mbs;
    StandardMBean mbean;
    private static final String MODULE_NAME = "[ SERVER RECOVERY STARTUP MANAGER ]";
    public static int getcount = 0;

    // for Recovery...!!!
    public static final String FILE_TX_WAIT = "FILE_TX_WAIT";
    public static final String FILE_RX_IN_PROGRESS = "FILE_TX_IN_PROGRESS";
    public static final String FILE_RX_SUCCESS = "FILE_TX_SUCCESS";
    public static final String FILE_RX_FAILED = "FILE_TX_FAILED";

    public static volatile AtomicBoolean serverStartedInDefaultMode = new AtomicBoolean(false);
    public static volatile AtomicBoolean allowUsersToLogin = new AtomicBoolean(false);

    private PrStartupMgrIface currentServerStartupManager;
    private RecoveryManagerDestn rmanDestn;

    private boolean selfDirty = false;
    private boolean remoteDirty = false;

    private String currentMode = ProjectStartupConstants.INTERMEDIATE_MODE;

    private boolean serverStarted = false;
    private volatile boolean startupDone = false;

    private PrStartupManager()
    {
	init();
    }

    protected boolean isStartupDone()
    {
	return startupDone;
    }

    protected void setStartupDone(boolean startupDone)
    {
	this.startupDone = startupDone;
    }

    private String currentStartupType;

    protected boolean isSelfDirty()
    {
	return selfDirty;
    }

    protected void setSelfDirty(boolean selfDirty)
    {
	this.selfDirty = selfDirty;
    }

    // below two api's are public since the ReplicationDbMgr would also set and
    // get the data
    public boolean isRemoteDirty()
    {
	return remoteDirty;
    }

    public void setRemoteDirty(boolean remoteDirty)
    {
	this.remoteDirty = remoteDirty;
    }

    /**
     * The current Mode of the Startup can be IDLE_MODE, DEFAULT_MODE or INTERMEDIATE_MODE
     * 
     * @return
     */
    public String getCurrentMode()
    {
	return this.currentMode;
    }

    protected void setCurrentMode(String currentMode)
    {
	this.currentMode = currentMode;
    }

    private void init()
    {
	try
	{
	    this.currentStartupType = ProjectStartupConstants.emsServerType;
	    startJmxServer();
	    if (this.currentStartupType.equalsIgnoreCase(ProjectStartupConstants.PRIMARY))
	    {
		this.currentServerStartupManager = new PrimaryServerStartupManager();
	    } else
	    {
		this.currentServerStartupManager = new SecondaryServerStartupManager();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void runOnce()
    {
	// init();
	// StartupDbState s = new StartupDbState(null);
	// String startCold = s.startCold();
	// if (SharedConstants.DB_CREATED.equals(startCold) == true)
	// {
	// s.stopCold();
	// }
    }

    public String getCurrentCtx()
    {
	return this.currentServerStartupManager.getCurrentCtx();
    }

    public String getCtxDescr()
    {
	return this.currentServerStartupManager.getCtxDescr();
    }

    public void setCurrentCtx(String ctx)
    {
	this.currentServerStartupManager.setCurrentCtx(ctx);
    }

    public String getServerType()
    {
	return this.currentServerStartupManager.getServerType();
    }

    public void startServer()
    {
	if (this.serverStarted == false)
	{
	    this.serverStarted = true;
	    this.currentServerStartupManager.startServer();
	} else
	{
	    logError("startServer", new Exception("Error Starting MEMS Server"));
	}
    }

    public void startupDefault()
    {
	this.currentServerStartupManager.startupDefault();
    }

    public void startupIdle()
    {
	this.currentServerStartupManager.startupIdle();
    }

    private boolean stopServer()
    {
	boolean retval = true;
	try
	{
	    this.currentServerStartupManager.stopServer();
	    this.serverStarted = false;
	} catch (Exception e)
	{
	    retval = false;
	    e.printStackTrace();
	}
	return retval;
    }

    public boolean shutdownServer()
    {
	boolean retval = stopServer();
	try
	{
	    mbs.unregisterMBean(serverName);
	    jmxc_server.stop();
	    System.exit(0);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return retval;
    }

    public String getPrimaryServerName()
    {
	return PrBeanFactory.instance.getProjectConfig().getPrimaryServerIp();
    }

    public String getSecondaryServerName()
    {
	return PrBeanFactory.instance.getProjectConfig().getSecondaryServerIp();
    }

    public boolean suspendPinger()
    {
	return this.currentServerStartupManager.suspendPinger();
    }

    public boolean resumePinger()
    {
	return this.currentServerStartupManager.resumePinger();
    }

    private RecoveryMsgCarrier sendRecoveryMsgToRemote(RecoveryMsgCarrier recoveryMsgCarrier)
    {
	return this.currentServerStartupManager.sendRecoveryMsgToRemote(recoveryMsgCarrier);
    }

    protected boolean recoverOwnDatabase()
    {
	boolean retval = true;
	this.rmanDestn = new RecoveryManagerDestn();
	try
	{
	    RecoveryMsgCarrier resp = sendRecoveryMsgToRemote(new RecoveryMsgCarrier(RecoveryMsg.PING));
	    if (resp.getRecoverymsg().equals(RecoveryMsg.PONG))
	    {
		resp = sendRecoveryMsgToRemote(new RecoveryMsgCarrier(RecoveryMsg.START_DATABASE_BACKUP));
		if (resp.getRecoverymsg().equals(RecoveryMsg.DATABASE_BACKUP_TAKEN))
		{
		    long fileSize = resp.getValue();
		    rmanDestn.setActualFileSize(fileSize);

		    // start the server socket on a thread..
		    if (rmanDestn.startServerSocket() == true)
		    {
			// send the server port to start the client on that port
			long serverPort = rmanDestn.getServerSocketPort();
			RecoveryMsgCarrier serverSockedStartedMsg = new RecoveryMsgCarrier(RecoveryMsg.SERVER_SOCKET_STARTED);
			serverSockedStartedMsg.setValue(serverPort);
			// the Src side with now start the CLIENT and send the
			// file.. by now the server socket shoud start receiving
			// the file..
			logWarn("recoverDatabase()", "The client will create a SOCKET now and connect to the Server.. should start receiving file by now..");
			resp = sendRecoveryMsgToRemote(serverSockedStartedMsg);

			if (resp.getRecoverymsg().equals(RecoveryMsg.FILE_TX_FAILED))
			{
			    logWarn("recoverDatabase()", "Got message of FILE_TX_FAILED from Src.. setting Recovery status as FALSE and Returning..");
			    retval = false;
			} else if (resp.getRecoverymsg().equals(RecoveryMsg.CONNECTION_ERROR))
			{
			    logWarn("recoverDatabase()", "Got message of CONNECTION_ERROR during wire Tx from Src.. setting Recovery status as FALSE and Returning..");
			    retval = false;
			} else if (resp.getRecoverymsg().equals(RecoveryMsg.FILE_TX_SUCCESS))
			{
			    while (getDumpFileRxStatus().equals(FILE_RX_IN_PROGRESS))
			    {
				logWarn("recoverDatabase()", "File Receive status = " + getDumpFileRxStatus() + " Waiting to FAIL or SUCCEED..");
				Thread.sleep(1000);
			    }

			    String fileRxStatus = getDumpFileRxStatus();
			    logWarn("recoverDatabase()", "File Receive status = " + fileRxStatus + " will proceed according to Status..");

			    if (fileRxStatus.equals(FILE_RX_FAILED))
			    {
				logWarn("recoverDatabase()", "Failed to Receive Backup File Successfully...setting Recovery status as FALSE and Returning..");
				retval = false;
			    } else if (fileRxStatus.equals(FILE_RX_SUCCESS))
			    {
				// check the file Size - the received size with
				// the actual one..
				long actual = rmanDestn.getActualFileSize();

				// give a break for the file to settle down
				Thread.sleep(1000 * 5);

				long received = rmanDestn.getFileSizeReceived();
				if (actual != received)
				{
				    logWarn("recoverDatabase()", "File sizes Failed to Match.." + " actual =" + actual + " received =" + received
					    + " setting Recovery status as FALSE and Returning..");
				    retval = false;
				} else
				{
				    // do the Db Restoration from the received
				    // File..
				    if (rmanDestn.restoreDb())
				    {
					logWarn("recoverDatabase()", "Restoration of DB Successful !!!");
					resp = sendRecoveryMsgToRemote(new RecoveryMsgCarrier(RecoveryMsg.FILE_RECEIVED_SUCCESS));
					retval = true;
				    } else
				    {
					logWarn("recoverDatabase()", "Restoration of DB FAILED !!!!!!!!!!");
					retval = false;
				    }
				}
			    }
			}
		    } else
		    {
			logWarn("recoverDatabase()", "Failed to Start ServerSocket...setting Recovery status as FALSE and Returning..");
			retval = false;
		    }

		} else
		{// DATABASE_BACKUP_TAKEN
		    retval = false;
		    logWarn("recoverDatabase()", "Expected DATABASE_BACKUP_TAKEN, but received " + resp.getRecoverymsg().name()
			    + " setting Recovery status as FALSE and Returning..");
		}

	    } else
	    // PING
	    {
		retval = false;
		logWarn("recoverDatabase()", "Expected PONG, but received " + resp.getRecoverymsg().name() + " setting Recovery status as FALSE and Returning..");
	    }
	} catch (Exception e)
	{
	    retval = false;
	    logError("recoverDatabase()", e);
	} finally
	{
	    if (retval == true)
	    {
		setRemoteDirty(false);
	    }
	}
	return retval;
    }

    private String getDumpFileRxStatus()
    {
	return rmanDestn.getFileRxStatus();
    }

    private void startJmxServer() throws Exception
    {
	int rmiport = ProjectStartupConstants.JMXPORT_WEB;
	int rmiServerPort = ProjectStartupConstants.JMXSERVERPORT_WEB;
	int htmlport = ProjectStartupConstants.JMXHTMLPORT_WEB;

	String serverIP = PrBeanFactory.instance.getProjectConfig().getApplicationServerIp();
	Properties systemProperty = System.getProperties();
	systemProperty.setProperty("java.rmi.server.hostname", serverIP);
	String serviceURL = "service:jmx:rmi://" + serverIP + ":" + rmiServerPort + "/jndi/rmi://" + serverIP + ":" + rmiport + "/jmxServer";
	java.rmi.registry.LocateRegistry.createRegistry(rmiport);
	mbs = MBeanServerFactory.createMBeanServer();
	String domain = mbs.getDefaultDomain();
	Console.print("The domain is :" + domain + " Server IP == " + serverIP);
	serverName = ObjectName.getInstance("DefaultDomain:type=JmxServer,name=JmxServer");

	// have a reference to the MainBeanServer

//	MainMbean main = new MainMbeanImpl();
//	mbean = new StandardMBean(main, MainMbean.class);
//	mbs.registerMBean(mbean, serverName);
//
//	JMXServiceURL url = new JMXServiceURL(serviceURL);
//	jmxc_server = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
//	jmxc_server.start();

	Console.print("Mbean registered...");
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, PrStartupManager.class);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, PrStartupManager.class);
    }
}