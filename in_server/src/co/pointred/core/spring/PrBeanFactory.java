package co.pointred.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import co.pointred.core.config.ConfigPath;
import co.pointred.core.config.ProjectConfig;
import co.pointred.core.constants.ProjectConstants;
import co.pointred.core.utils.CastorXmlUtil;

public enum PrBeanFactory
{
    instance;
    private ApplicationContext ctx;

    private PrBeanFactory()
    {
	init();
    }

    private void init()
    {
	String serverPathCfgFile = "config/server-cfg.xml";
	ctx = new ClassPathXmlApplicationContext(serverPathCfgFile, ProjectConstants.CASTOR_XML_CONFIG, ProjectConstants.COMMON_APP_CONFIG);
    }

    public ProjectConfig getProjectConfig()
    {
	return (ProjectConfig) ctx.getBean("serverConfig");
    }
    
    public CastorXmlUtil getCastorXmlUtil()
    {
	return (CastorXmlUtil) ctx.getBean("castorGenerator");
    }
    
    public ConfigPath getConfigPath()
    {
	return (ConfigPath) ctx.getBean("configPath");
    }
}
