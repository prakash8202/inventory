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

public class SecondaryServerStartupManager implements PrStartupMgrIface
{
	private static final String MODULE_NAME = "[ EMS START UP ]";
	private String ctx = ProjectStartupConstants.SHUTDOWN;

	private final RemotingServerSecondary remotingServerSecondary = new RemotingServerSecondary();
	private final RemotingClientSecondary remotingClientSecondary = new RemotingClientSecondary();
	private RemotePingerSecondary remotePingerSecondary;

	private final DefaultStartupStatesContainer defaultStartupStatesContainer = new DefaultStartupStatesContainer();
	private final IdleStartupStatesContainer idleStartupStatesContainer = new IdleStartupStatesContainer();

	private LinkedHashMap<String, PrStartupIface> emsStartupStatesMap = new LinkedHashMap<String, PrStartupIface>();

	public SecondaryServerStartupManager()
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
	public String getServerType()
	{
		return ProjectStartupConstants.SECONDARY;
	}

	@Override
	public boolean resumePinger()
	{
		return this.remotePingerSecondary.resumeSecondaryPinger();

	}

	@Override
	public boolean suspendPinger()
	{
		return this.remotePingerSecondary.suspendSecondaryPinger();

	}

	@Override
	public void startupDefault()
	{
		shutdownCurrentStats();
		resetStartupVars();
		PrStartupManager.instance.setCurrentMode(ProjectStartupConstants.DEFAULT_MODE);
		logWarn("startServer()", "!!!!!!!!!!!!!!!!! BRINGING UP SECONDARY EMS IN DEFAULT MODE !!!!!!!!!!!!!!!!!!!!!!");

		this.emsStartupStatesMap = this.defaultStartupStatesContainer.getMapOfDefaultEmsStates();

		for (Map.Entry<String, PrStartupIface> map : this.emsStartupStatesMap.entrySet())
		{
			String ctx = map.getKey();
			PrStartupIface emsStartupIface = map.getValue();
			setCurrentCtx(ctx);
			emsStartupIface.startService();
		}
		PrStartupManager.instance.setStartupDone(true);
		PrStartupManager.serverStartedInDefaultMode.set(true);
		PrStartupManager.allowUsersToLogin.set(true);

		logWarn("startServer()", "\n= = = = = = = = = = = SECONDARY CEMS STARTED IN DEFAULT MODE = = = = = = = = = = = \n");
	}

	@Override
	public void startupIdle()
	{
		shutdownCurrentStats();
		resetStartupVars();
		PrStartupManager.instance.setCurrentMode(ProjectStartupConstants.IDLE_MODE);

		this.emsStartupStatesMap = this.idleStartupStatesContainer.getMapOfIdleEmsStates();
		logWarn("startServer()", "##################### BRINGING UP SECONDARY EMS IN IDLE MODE #######################");
		for (Map.Entry<String, PrStartupIface> map : this.emsStartupStatesMap.entrySet())
		{
			String ctx = map.getKey();
			PrStartupIface emsStartupIface = map.getValue();
			setCurrentCtx(ctx);
			emsStartupIface.startService();
		}
		PrStartupManager.instance.setStartupDone(true);

		logWarn("startServer()", "\n= = = = = = = = = = = SECONDARY CEMS STARTED IN    I D L E   MODE = = = = = = = = = = = \n");

	}

	@Override
	public void startServer()
	{
		logWarn("startServer()", ".......................... STARTING SECONDARY EMS ..................");

		// start the Secondary Remoting Server
		if (startSecondaryRemotingServer() == true)
		{
			// start the Secondary Remoting Client - this might fail if Primary
			// Server is NOT UP !!
			if (startSecondaryRemotingClient() == true)
			{
				try
				{
					// check all conditions with Primary and proceed..MAIN API
					// for DR
					handShakeWithPrimaryAndProceed();
				} catch (Throwable e)
				{
					logError("startupServer()", e);
				}
			} else
			{
				markPrimaryAsDirty();
				startupDefault();
			}

			// start Secondary Pinger..
			this.remotePingerSecondary = new RemotePingerSecondary(this.remotingClientSecondary);
			this.remotePingerSecondary.startSecondaryPinger();
		} else
		{
			logWarn("startServer()", "Cannot Continue......... as startup of RemotingServerSecondary failed");
			System.exit(0);
		}
	}

	private void markPrimaryAsDirty()
	{
		PrStartupManager.instance.setRemoteDirty(true);
		logWarn("startServer()", "Failed to HANDSHAKE with Primary..Continuing with PRIMARY MARKED AS DIRTY..");
	}

	private void handShakeWithPrimaryAndProceed()
	{
		try
		{
			StartUpMsg resp = this.remotingClientSecondary.sendStartupMsgToPrimaryServer(StartUpMsg.PING);
			if (resp.equals(StartUpMsg.PONG))
			{
				// ask Primary to Stop the Pinger...
				resp = this.remotingClientSecondary.sendStartupMsgToPrimaryServer(StartUpMsg.SUSPEND_PRIMARY_PINGER);

				if (resp.equals(StartUpMsg.PRIMARY_PINGER_SUSPENDED))
				{
					// ask Primary to come to IDLE state..
					resp = this.remotingClientSecondary.sendStartupMsgToPrimaryServer(StartUpMsg.GOTO_IDLE_MODE_I_WANT_TO_RECOVER);

					// primary expected to go to IDLE mode here

					if (resp.equals(StartUpMsg.GONE_TO_IDLE_MODE_YOU_RECOVER))
					{
						// start Recovery..
						if (recoverDatabase())
						{
							logWarn("handShakeWithPrimaryAndProceed()", "Secondary Database Restored from Primary in FIRST Attempt");
						} else
						{
							logWarn("handShakeWithPrimaryAndProceed()", "To Hell with IT... Database RECOVERY FAILED.. Starting in IDLE anyways..Do a manual restore...");
						}

						// Push the Primary to DEFAULT and RESUME its PINGER
						resp = this.remotingClientSecondary.sendStartupMsgToPrimaryServer(StartUpMsg.GOTO_DEFAULT_MODE_I_HAVE_RECOVERED);

						// primary expected to Switch to DEFAULT MODE here..
						if (resp.equals(StartUpMsg.GONE_TO_DEFAULT_MODE))
						{
							resp = this.remotingClientSecondary.sendStartupMsgToPrimaryServer(StartUpMsg.RESUME_PRIMARY_PINGER);
							if (resp.equals(StartUpMsg.PRIMARY_PINGER_RESUMED))
							{
								// all izzz well.. the primary Pinger also
								// resumed.. start with idle operations
								startupIdle();
							} else
							{
								// the Primary is in IDLE.. with this Exception,
								// it will never come to DEFAULT Mode.. so,
								// better start SEC in DEFAULT Mode.. the
								// Pingers should take
								// charge of the rest
								logWarn("handShakeWithPrimaryAndProceed()",
								"the Primary is in IDLE.. with this Exception, it will never come to DEFAULT Mode.. so, better start SEC in DEFAULT Mode.. the Pingers should take charge of the rest");
								startupDefault();
							}
						} else
						{
							// the Primary has NOT come in DEFAULT mode after
							// asking him to COME SO.. The Pingers should take
							// care of this.. stat SEC in DEFAULT
							logWarn("handShakeWithPrimaryAndProceed()",
							"the Primary has NOT come in DEFAULT mode after asking him to COME SO.. The Pingers should take care of this.. stat SEC in DEFAULT");
							startupDefault();
						}

					} else
					{
						// primary could not be pushed to IDLE mode
						logWarn("handShakeWithPrimaryAndProceed()", "Expected GONE_TO_IDLE_MODE_YOU_RECOVER.. but received.." + resp.name() + " will proceed in DEFAULT MODE");
						markPrimaryAsDirty();
						startupDefault();
					}
				} else
				{
					// error.. the primary Pringer could not be suspended.
					// Primary is UP, so u Start in Idle..
					logWarn("handShakeWithPrimaryAndProceed()", "Expected PRIMARY_PINGER_SUSPENDED.. but received.." + resp.name() + " will proceed in DEFAULT MODE");
					markPrimaryAsDirty();
					startupDefault();
				}
			} else
			{
				// there is No response from Primary... Will start up in DEFAULT
				// MODE..
				logWarn("handShakeWithPrimaryAndProceed()", "Expected PONG.. but received.." + resp.name() + " will proceed in DEFAULT MODE");
				markPrimaryAsDirty();
				startupDefault();
			}

			// start secondary pinger...
			// this.remotePingerSecondary = new
			// RemotePingerSecondary(this.remotingClientSecondary);
			// this.remotePingerSecondary.startSecondaryPinger();

		} catch (Throwable e)
		{
			logError("handShakeWithPrimaryAndProceed()", e);
		}
	}

	@Override
	public void stopServer()
	{
		logWarn("stopServer()", "============== STOPPING SECONDARY EMS =============== Current Ctx =" + getCurrentCtx());
		String currentMode = PrStartupManager.instance.getCurrentMode();
		// wait for EMS to stabilize...
		if (currentMode.equals(ProjectStartupConstants.DEFAULT_MODE))
		{
			while (!getCurrentCtx().equals(ProjectStartupConstants.STARTUP_DEFAULT_DONE))
			{
				logWarn("stopServer()", " Waiting for EMS to come to DEFAULT MODE !!! - Ctx now is : " + getCurrentCtx());
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
		this.remotePingerSecondary.stopSecondaryPinger();

		if (stopSecondaryRemotingServer() == true)
		{
			stopSecondaryRemotingClient();
		}

		// reset the startup variables
		resetStartupVars();
		logWarn("stopServer()", "============== STOPPING SECONDARY EMS - DONE  ===============");
	}

	// //////////////////////////////////////////

	// shuts down all the current states..
	protected void shutdownCurrentStats()
	{
		// stop all the Startup States
		Set<String> statesSet = this.emsStartupStatesMap.keySet();

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
			PrStartupIface emsStartupIface = this.emsStartupStatesMap.get(ctx);
			emsStartupIface.stopService();
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

	// Start Secondary Remoting Server
	private boolean startSecondaryRemotingServer()
	{
		boolean retVal = true;
		try
		{
			this.remotingServerSecondary.startSecondaryRemotingServer();
		} catch (Exception e)
		{
			retVal = false;
			logError("startSecondaryRemotingServer()", e);
		}

		return retVal;
	}

	// Stop Secondary Remoting Server
	private boolean stopSecondaryRemotingServer()
	{
		boolean retVal = true;
		try
		{
			this.remotingServerSecondary.stopSecondaryRemotingServer();
		} catch (Exception e)
		{
			retVal = false;
			logError("stopSecondaryRemotingServer()", e);
		}
		return retVal;
	}

	// Start Secondary Remoting Client
	private boolean startSecondaryRemotingClient()
	{
		boolean retVal = true;
		try
		{
			this.remotingClientSecondary.startSecondaryRemotingClient();

		} catch (Throwable e)
		{
			retVal = false;
			logError("startSecondaryRemotingClient()", e);
		}

		return retVal;
	}

	// Stop Secondary Remoting Client
	private boolean stopSecondaryRemotingClient()
	{
		boolean retval = true;
		this.remotingClientSecondary.stopSecondaryRemotingClient();
		return retval;
	}

	// recovery of db..
	private boolean recoverDatabase()
	{
		return PrStartupManager.instance.recoverOwnDatabase();
	}

	private void logError(String apiName, Throwable e)
	{
		LoggerService.instance.error(MODULE_NAME + " - " + apiName, SecondaryServerStartupManager.class, e);
	}

	private void logWarn(String apiName, String msg)
	{
		LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, SecondaryServerStartupManager.class);
	}

	@Override
	public RecoveryMsgCarrier sendRecoveryMsgToRemote(RecoveryMsgCarrier recoveryMsg)
	{
		return this.remotingClientSecondary.sendRecoveryMsgToPrimaryServer(recoveryMsg);
	}

}