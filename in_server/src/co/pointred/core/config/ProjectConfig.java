package co.pointred.core.config;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;

public class ProjectConfig
{
    private String serverType;
    private String primaryServerIp;
    private String secondaryServerIp;
    private String applicationServerIp;
    private Resource sqlBackupDir;
    private String eventLogger;
    
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getPrimaryServerIp() {
		return primaryServerIp;
	}
	public void setPrimaryServerIp(String primaryServerIp) {
		this.primaryServerIp = primaryServerIp;
	}
	public String getSecondaryServerIp() {
		return secondaryServerIp;
	}
	public void setSecondaryServerIp(String secondaryServerIp) {
		this.secondaryServerIp = secondaryServerIp;
	}
	public String getApplicationServerIp() {
		return applicationServerIp;
	}
	public void setApplicationServerIp(String applicationServerIp) {
		this.applicationServerIp = applicationServerIp;
	}
	public Resource getSqlBackupDir() {
		return sqlBackupDir;
	}
	public void setSqlBackupDir(Resource sqlBackupDir) {
		this.sqlBackupDir = sqlBackupDir;
	}
	
	public File getSqlDirectory()
    {
	try
	{
	    return this.sqlBackupDir.getFile();
	} catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
	public String getEventLogger() {
		return eventLogger;
	}
	public void setEventLogger(String eventLogger) {
		this.eventLogger = eventLogger;
	}
}