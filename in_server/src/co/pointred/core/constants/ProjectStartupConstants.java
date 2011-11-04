package co.pointred.core.constants;

import co.pointred.core.spring.PrBeanFactory;


public class ProjectStartupConstants
{
    public static final String PRIMARY = "PRIMARY";
    public static final String SECONDARY = "SECONDARY";
    public static final String emsServerType = PrBeanFactory.instance.getProjectConfig().getServerType();
    public static final String serverIp = PrBeanFactory.instance.getProjectConfig().getApplicationServerIp();
    public static final String STARTUP_ENV_VARS_STATE = "STARTUP_ENV_VARS_STATE";
    public static final String STARTUP_DATABASE_STATE = "STARTUP_DATABASE_STATE";
    public static final String STARTUP_RMI_SERVER_STATE = "STARTUP_RMI_SERVER_STATE";
    public static final String STARTUP_EXECUTOR_STATE = "STARTUP_EXECUTOR_STATE";
    public static final String STARTUP_INIT_CACHE_STATE = "STARTUP_INIT_CACHE_STATE";
    public static final String STARTUP_TRAPHANDLER_STATE = "STARTUP_TRAPHANDLER_STATE";
    public static final String STARTUP_ASN_STATUS_POLLER_STATE = "STARTUP_ASN_STATUS_POLLER_STATE";
    public static final String STARTUP_BTS_STATUS_POLLER_STATE = "STARTUP_BTS_STATUS_POLLER_STATE";
    public static final String STARTUP_BTS_PERF_POLLER_STATE = "STARTUP_BTS_PERF_POLLER_STATE";
    public static final String STARTUP_BTSIDIP_POLLER_STATE = "STARTUP_BTSIDIP_POLLER_STATE";
    public static final String STARTUP_BTS_DISCOVERY_POLLER_STATE = "STARTUP_DISCOVERY_POLLER_STATE";
    public static final String STARTUP_PERFORMANCE_POLLER_STATE = "STARTUP_PERFORMANCE_POLLER_STATE";
    public static final String STARTUP_CPE_STATUS_POLLER_STATE = "STARTUP_CPE_STATUS_POLLER_STATE";
    public static final String STARTUP_QUARTZ_SCHEDULER_STATE = "STARTUP_QUARTZ_SCHEDULER_STATE";
    public static final String STARTUP_NOTIFICATION_STATE = "STARTUP_NOTIFICATION_STATE";
    public static final String STARTUP_XML_SERVER_SOCKET_STATE = "STARTUP_XML_SERVER_SOCKET_STATE";
    public static final String STARTUP_OBJECT_CACHE_STATE = "STARTUP_OBJECT_CACHE_STATE";
    public static final String STARTUP_INSTRUMENTATION_STATE = "STARTUP_INSTRUMENTATION_STATE";
    public static final String STARTUP_BTS_FIRMWARE_UPGRADE_STATE = "STARTUP_BTS_FIRMWARE_UPGRADE_STATE";
    public static final String STARTUP_CPE_MANAGER_STATE = "STARTUP_CPE_MANAGER_STATE";

    public static final String SHUTDOWN = "SHUTDOWN";
    public static final String STANDBY = "STANDBY";
    public static final String STARTUP = "STARTUP";
    public static final String SHUTDOWN_PLANNED = "SHUTDOWN_PLANNED";

    // These are the Startup Types..

    public static final String DEFAULT_MODE = "DEFAULT_MODE";
    public static final String IDLE_MODE = "IDLE_MODE";
    public static final String INTERMEDIATE_MODE = "INTERMEDIATE_MODE";

    public static final String STARTUP_DEFAULT_DONE = "STARTUP_DEFAULT_DONE";
    public static final String STARTUP_IDLE_DONE = "STARTUP_IDLE_DONE";

    // for server start and stop functionalities
    public static final int JMXPORT_WEB = 8885;
    public static final int JMXSERVERPORT_WEB = 8886;
    public static final int JMXHTMLPORT_WEB = 8887;

    // for replication and realtime sync
    public static final int JBOSS_REMOTING_SERVER_CEMS = 7400;

}