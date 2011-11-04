package co.pointred.core.startup;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.spring.PrBeanFactory;

public class MbeanProvider
{
    public static MainMbean getMainMbean() throws Exception
    {
	String serverIP = PrBeanFactory.instance.getProjectConfig().getApplicationServerIp();

	JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + serverIP + ":" + ProjectStartupConstants.JMXPORT_WEB + "/jmxServer");
	JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
	MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
	ObjectName objectName = new ObjectName("DefaultDomain:type=JmxServer,name=JmxServer");

	MainMbean mainMBean = (MainMbean) MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, MainMbean.class, false);
	return mainMBean;
    }
}
