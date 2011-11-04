package co.pointred.core.startup.states.skeleton;

import java.util.LinkedHashMap;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.startup.PrStartupIface;

public class IdleStartupStatesContainer
{
	private final LinkedHashMap<String, PrStartupIface> mapOfIdleEmsStates = new LinkedHashMap<String, PrStartupIface>();

	public IdleStartupStatesContainer()
	{
		populateMapOfIdleEmsStates();
	}

	private void populateMapOfIdleEmsStates()
	{
		mapOfIdleEmsStates.put(ProjectStartupConstants.STARTUP_ENV_VARS_STATE, new StartupEnvironmentState(ProjectStartupConstants.STARTUP_ENV_VARS_STATE));
		mapOfIdleEmsStates.put(ProjectStartupConstants.STARTUP_NOTIFICATION_STATE, new StartupNotificationState(ProjectStartupConstants.STARTUP_NOTIFICATION_STATE));
		mapOfIdleEmsStates.put(ProjectStartupConstants.STARTUP_IDLE_DONE, new StartupIdleDoneState(ProjectStartupConstants.STARTUP_IDLE_DONE));
		LoggerService.instance.warning("[ EMS STARTUP ] Map populated for Idle Mode", DefaultStartupStatesContainer.class);
	}

	public LinkedHashMap<String, PrStartupIface> getMapOfIdleEmsStates()
	{
		return this.mapOfIdleEmsStates;
	}

}
