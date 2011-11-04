package co.pointred.core.startup;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.startup.states.skeleton.DefaultStartupStatesContainer;
import co.pointred.core.startup.states.skeleton.IdleStartupStatesContainer;

public class PrimaryServerStartupManager implements PrStartupMgrIface
{
	private static final String MODULE_NAME = "[ SERVER START UP ]";

	private String ctx = ProjectStartupConstants.SHUTDOWN;
	private final RemotingServerPrimary remotingServerPrimary = new RemotingServerPrimary();
	private final RemotingClientPrimary remotingClientPrimary = new RemotingClientPrimary();
	private RemotePingerPrimary remotePingerPrimary;

	private final DefaultStartupStatesContainer defaultStartupStatesContainer = new DefaultStartupStatesContainer();
	private final IdleStartupStatesContainer idleStartupStatesContainer = new IdleStartupStatesContainer();

	private LinkedHashMap<String, PrStartupIface> prStartupStatesMap = new LinkedHashMap<String, PrStartupIface>();

	public PrimaryServerStartupManager()
	{

	}

	@Override
	public String getCurrentCtx()
	{
		return this.ctx;
	}

	@Override
	public String getCtxDescr()
	{
		return this.ctx;
	}

	@Override
	public void setCurrentCtx(String ctx)
	{
		this.ctx = ctx;
	}

	@Override
	public void startServer()
	{
		logWarn("startServer()", "\n= = = = = = = = = = =  [ STARTING SERVER ] = = = = = = = = = = = \n");

		// start the Primary Remoting Server
		if (startPrimaryRemotingServer() == true)
		{
			// start the Primary Remoting Client - this might fail if Secondary Server is NOT UP !!
			if (startPrimaryRemotingClient() == true)
			{
				try
				{
					// check all conditions with sec and proceed..MAIN API for DR
					handShakeWithSecondaryAndProceed();
				} catch (Throwable e)
				{
					logError("startupServer(). handshakeWithSecondaryAndProceed()", e);
				}
			} else
			{
				// if sec is not available now - proceed with default startup
				PrStartupManager.instance.setRemoteDirty(true);
				logWarn("startServer()", "Failed to HANDSHAKE with Secondary..Continuing with SECONDARY MARKED AS DIRTY..");

				startupDefault();
			}

			// start primary Pinger..
			this.remotePingerPrimary = new RemotePingerPrimary(this.remotingClientPrimary);
			this.remotePingerPrimary.startPrimaryPinger();

		} else
		{
			// the Remoting SERVER could not be started !!!
			logWarn("startServer()", "CRITICAL ERROR.... REMOTING SERVER ON PRIMARY CANNOT BE STARTED.. CHK FIREWALL FOR PORT " + ProjectStartupConstants.JBOSS_REMOTING_SERVER_CEMS
					+ "..NO REPLICATION.. NO RECOVERY... NO REALTIME SYNC.. SO, SYSTEM.SHUTDOWN !!!!");
			System.exit(0);
		}
	}

	@Override
	public void stopServer()
	{
		logWarn("stopServer()", "\n= = = = = = = = = ATTEMPTING TO STOP SERVER = = = = = = = = = = Current Ctx =" + getCurrentCtx() + "\n");
		String currentMode = PrStartupManager.instance.getCurrentMode();
		// wait for SERVER to stabilize...
		if (currentMode.equals(ProjectStartupConstants.DEFAULT_MODE))
		{
			while (!getCurrentCtx().equals(ProjectStartupConstants.STARTUP_DEFAULT_DONE))
			{
				logWarn("stopServer()", " Waiting for SERVER to come to DEFAULT MODE !!! - Ctx now is : " + getCurrentCtx());
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		// bring down all the states
		shutdownCurrentStats();

		// stop the pinger first..
		this.remotePingerPrimary.stopPrimaryPinger();

		if (stopPrimaryRemotingServer() == true)
		{
			stopPrimaryRemotingClient();
		}

		// reset the startup variables
		resetStartupVars();
		logWarn("stopServer()", "\n= = = = = = = = = =  STOPPING SERVER - DONE  = = = = = = = = = =\n");
	}

	// shuts down all the current states..
	protected void shutdownCurrentStats()
	{
		// stop all the Startup States
		Set<String> statesSet = this.prStartupStatesMap.keySet();

		if (statesSet.size() == 0)
			return;

		Vector<String> statesVec = new Vector<String>();
		for (String ctx : statesSet)
		{
			statesVec.add(ctx);
		}
		Collections.reverse(statesVec);

		for (String ctx : statesVec)
		{
			PrStartupIface prStartupIface = this.prStartupStatesMap.get(ctx);
			prStartupIface.stopService();
		}
	}

	private void resetStartupVars()
	{
		PrStartupManager.instance.setCurrentMode(ProjectStartupConstants.INTERMEDIATE_MODE);
		PrStartupManager.instance.setStartupDone(false);
		PrStartupManager.allowUsersToLogin.set(false);
		PrStartupManager.serverStartedInDefaultMode.set(false);
		PrStartupManager.allowUsersToLogin.set(false);
		setCurrentCtx(ProjectStartupConstants.SHUTDOWN);
	}

	@Override
	public String getServerType()
	{
		return ProjectStartupConstants.PRIMARY;
	}

	// ////////////////////////////////// proprietory implementations
	// ///////////////////

	// Start Primary Remoting Server
	private boolean startPrimaryRemotingServer()
	{
		boolean retVal = true;
		try
		{
			this.remotingServerPrimary.startPrimaryRemotingServer();
		} catch (Exception e)
		{
			retVal = false;
			logError("startPrimaryRemotingServer()", e);
		}

		return retVal;
	}

	// Stop Primary Remoting Server
	private boolean stopPrimaryRemotingServer()
	{
		boolean retVal = true;
		try
		{
			this.remotingServerPrimary.stopPrimaryRemotingServer();
		} catch (Exception e)
		{
			retVal = false;
			logError("stopPrimaryRemotingServer()", e);
		}
		return retVal;
	}

	// Start Primary Remoting Client
	private boolean startPrimaryRemotingClient()
	{
		boolean retVal;
		try
		{
			retVal = this.remotingClientPrimary.startPrimaryRemotingClient();
		} catch (Throwable e)
		{
			retVal = false;
			LoggerService.instance.error("Error in startPrimaryRemotingClient()", PrimaryServerStartupManager.class, e);
		}

		return retVal;
	}

	// Stop Primary Remoting Client
	private boolean stopPrimaryRemotingClient()
	{
		boolean retval = true;
		this.remotingClientPrimary.stopPrimaryRemotingClient();
		return retval;
	}

	/**
	 * Main API to Sync with the Secondary and resolve all DR related issues
	 * 
	 * @return
	 * @throws Throwable
	 */
	private boolean handShakeWithSecondaryAndProceed() throws Throwable
	{
		boolean retVal = true;
		try
		{
			StartUpMsg resp = this.remotingClientPrimary.sendStartupMsgToSecondaryServer(StartUpMsg.WHAT_MODE_ARE_YOU_IN);
			if (resp.equals(StartUpMsg.IDLE_MODE_I_AM_IN))
			{
				// need not to do anything... just a normal case.. proceed with
				// DEFAULT
				startupDefault();
			} else if (resp.equals(StartUpMsg.DEFAULT_MODE_I_AM_IN))
			{
				resp = this.remotingClientPrimary.sendStartupMsgToSecondaryServer(StartUpMsg.SUSPEND_SECONDARY_PINGER);
				if (resp.equals(StartUpMsg.SECONDARY_PINGER_SUSPENDED))
				{
					resp = this.remotingClientPrimary.sendStartupMsgToSecondaryServer(StartUpMsg.GOTO_IDLE_MODE_I_WANT_TO_RECOVER);

					if (resp.equals(StartUpMsg.GONE_TO_IDLE_MODE_YOU_RECOVER))
					{
						// the secondary has gone to idle mode.. now check if
						// dirty..
						resp = this.remotingClientPrimary.sendStartupMsgToSecondaryServer(StartUpMsg.IS_PRIMARY_DIRTY);
						if (resp.equals(StartUpMsg.PRIMARY_IS_DIRTY) || resp.equals(StartUpMsg.PRIMARY_IS_NOT_DIRTY))
						{
							// we need to recover the DB Now...there is no need
							// to start any startup type (default / idle) as
							// there is nothing started so far
							logWarn("handShakeWithSecondaryAndProceed()", "Primary will GET RECOVERED NOW !!!");
							// start recovery
							if (recoverDatabase() == true)
							{
								logWarn("handShakeWithSecondaryAndProceed()", "RECOVERY SUCCESS at FIRST attempt.. will go to DEFAULT startup Now..!!!");
								// recovery successful.. switch to DefaultMode
								startupDefault();
							} else
							{
								// try once More...
								logWarn("handShakeWithSecondaryAndProceed()", "RECOVERY FAILED at FIRST attempt.. will TRY once more Now..!!!");
								if (recoverDatabase() == true)
								{
									logWarn("handShakeWithSecondaryAndProceed()", "RECOVERY SUCCESS at SECOND attempt.. will go to DEFAULT startup Now..!!!");
									// recovery successful.. switch to
									// DefaultMode
									startupDefault();
								} else
								{
									logWarn("handShakeWithSecondaryAndProceed()", "RECOVERY FAILED even at SECOND attempt.. will go to DEFAULT startup BY FORCE Now..!!!");
									// Recovery Failed... no other go - start in
									// DefaultMode...
									startupDefault();
								}
							}

						} else
						{
							logWarn("handShakeWithSecondaryAndProceed()", "Expected PRIMARY_IS_NOT_DIRTY or PRIMARY_IS_DIRTY as resp - But Got " + resp
									+ " as Resp from Secondary..marking sec as dirty and starting Default");
							markSecondaryDirty();
							startupDefault();
						}
					} else
					{
						logWarn("handShakeWithSecondaryAndProceed()", "Expected GONE_TO_IDLE_MODE as resp - But Got " + resp
								+ " as Resp from Secondary..marking sec as dirty and starting Default");
						markSecondaryDirty();
						startupDefault();
					}

					// ask SECONDARY to resume PINGER Post Recovery
					resp = this.remotingClientPrimary.sendStartupMsgToSecondaryServer(StartUpMsg.RESUME_SECONDARY_PINGER);
					if (resp.equals(StartUpMsg.SECONDARY_PINGER_RESUMED))
					{
						logWarn("handShakeWithSecondaryAndProceed()", "secondary PINGER successfully RESUMED suspension..");
					} else
					{
						// can't do anything if the Pinger does not resume..
						logWarn("handShakeWithSecondaryAndProceed()", "secondary PINGER could not RESUME suspension.. cannot do anything from PRIMARY Side");
					}

				} else
				{
					// could not suspend the PRIMARY Pinger..
					logWarn("handShakeWithSecondaryAndProceed()", "Could Not Suspend SECONDARY Pinger.. starting in Default with Sec as Dirty");
					markSecondaryDirty();
					startupDefault();
				}
			} else
			{
				// No resp when asked for WHAT MODE
				logWarn("handShakeWithSecondaryAndProceed()", "Expected IDLE_MODE_I_AM_IN or DEFAULT_MODE_I_AM_IN as resp - But Got " + resp
						+ " as Resp from Secondary..marking sec as dirty and starting DEFAULT");
				markSecondaryDirty();
				startupDefault();
			}
		} catch (Exception e)
		{
			logError("handShakeWithSecondaryAndProceed() FATAL ERROR..... SHUTTING DOWN PRIMARY...!!!!", e);
			System.exit(0);
		}

		return retVal;
	}

	private void markSecondaryDirty()
	{
		PrStartupManager.instance.setRemoteDirty(true);
	}

	// recovery of db..
	private boolean recoverDatabase()
	{
		return PrStartupManager.instance.recoverOwnDatabase();
	}

	private void logError(String apiName, Throwable e)
	{
		LoggerService.instance.error(MODULE_NAME + " - " + apiName, PrimaryServerStartupManager.class, e);
	}

	private void logWarn(String apiName, String msg)
	{
		LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, PrimaryServerStartupManager.class);
	}

	// starts up the Primary SERVER on DEFAULT Mode
	@Override
	public void startupDefault()
	{
		shutdownCurrentStats();
		resetStartupVars();
		PrStartupManager.instance.setCurrentMode(ProjectStartupConstants.DEFAULT_MODE);

		this.prStartupStatesMap = this.defaultStartupStatesContainer.getMapOfDefaultEmsStates();

		for (Map.Entry<String, PrStartupIface> map : this.prStartupStatesMap.entrySet())
		{
			String ctx = map.getKey();
			PrStartupIface emsStartupIface = map.getValue();
			setCurrentCtx(ctx);
			emsStartupIface.startService();
		}
		PrStartupManager.instance.setStartupDone(true);
		PrStartupManager.serverStartedInDefaultMode.set(true);
		PrStartupManager.allowUsersToLogin.set(true);

		logWarn("startServer()", "\n= = = = = = = = = = = PRIMARY CEMS STARTED IN DEFAULT MODE = = = = = = = = = = = \n");
	}

	// starts up the Primary SERVER on IDLE Mode
	@Override
	public void startupIdle()
	{
		shutdownCurrentStats();
		resetStartupVars();
		PrStartupManager.instance.setCurrentMode(ProjectStartupConstants.IDLE_MODE);

		this.prStartupStatesMap = this.idleStartupStatesContainer.getMapOfIdleEmsStates();

		for (Map.Entry<String, PrStartupIface> map : this.prStartupStatesMap.entrySet())
		{
			String ctx = map.getKey();
			PrStartupIface emsStartupIface = map.getValue();
			setCurrentCtx(ctx);
			emsStartupIface.startService();
		}
		PrStartupManager.instance.setStartupDone(true);

		logWarn("startServer()", "\n= = = = = = = = = = = PRIMARY CEMS STARTED IN    I D L E   MODE = = = = = = = = = = = \n");
	}

	@Override
	public boolean resumePinger()
	{
		return this.remotePingerPrimary.resumePrimaryPinger();
	}

	@Override
	public boolean suspendPinger()
	{
		return this.remotePingerPrimary.suspendPrimaryPinger();
	}

	@Override
	public RecoveryMsgCarrier sendRecoveryMsgToRemote(RecoveryMsgCarrier recoveryMsgCarrier)
	{
		return this.remotingClientPrimary.sendRecoveryMsgToSecondaryServer(recoveryMsgCarrier);
	}
}
