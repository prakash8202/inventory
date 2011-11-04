package co.pointred.core.startup.states.skeleton;

import java.util.LinkedHashMap;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.startup.PrStartupIface;

public class DefaultStartupStatesContainer
{
    private final LinkedHashMap<String, PrStartupIface> mapOfDefaultEmsStates = new LinkedHashMap<String, PrStartupIface>();

    public DefaultStartupStatesContainer()
    {
	populateMapOfDefaultEmsStates();
    }

    private void populateMapOfDefaultEmsStates()
    {
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_ENV_VARS_STATE, new StartupEnvironmentState(ProjectStartupConstants.STARTUP_ENV_VARS_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_DATABASE_STATE, new StartupDbState(ProjectStartupConstants.STARTUP_DATABASE_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_INIT_CACHE_STATE, new StartupInitCacheState(ProjectStartupConstants.STARTUP_INIT_CACHE_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_EXECUTOR_STATE, new StartupExecutorState(ProjectStartupConstants.STARTUP_EXECUTOR_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_NOTIFICATION_STATE, new StartupNotificationState(ProjectStartupConstants.STARTUP_NOTIFICATION_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_QUARTZ_SCHEDULER_STATE, new StartupQuartzSchedulerState(ProjectStartupConstants.STARTUP_QUARTZ_SCHEDULER_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_XML_SERVER_SOCKET_STATE, new StartupXmlServerSocketState(ProjectStartupConstants.STARTUP_XML_SERVER_SOCKET_STATE));
	mapOfDefaultEmsStates.put(ProjectStartupConstants.STARTUP_DEFAULT_DONE, new StartupDefaultDoneState(ProjectStartupConstants.STARTUP_DEFAULT_DONE));

	LoggerService.instance.warning("[ EMS STARTUP ] Map populated for Default Mode", DefaultStartupStatesContainer.class);
    }

    public LinkedHashMap<String, PrStartupIface> getMapOfDefaultEmsStates()
    {
	return this.mapOfDefaultEmsStates;
    }
}
