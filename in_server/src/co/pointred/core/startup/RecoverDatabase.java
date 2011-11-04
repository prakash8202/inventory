package co.pointred.core.startup;

import java.io.File;

import co.pointred.core.config.ProjectConfig;
import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;
import co.pointred.core.utils.Console;

public class RecoverDatabase
{

    public boolean restore(String fileName)
    {
	boolean retval = true;
	ProjectConfig cemsConfig = PrBeanFactory.instance.getProjectConfig();

	try
	{
	    String[] startCommand = null;

	    // Check the Server Operating System
	    if (System.getProperty("os.name").contains("Windows"))
	    {

	    } else if (System.getProperty("os.name").contains("Linux"))
	    {
		// command to convert file from DOS to UNIX
		String[] unixFileFormatCommandForInitiateRestart = new String[]
		{ "sed", "-i", "s/\\x0D$//", cemsConfig.getSqlDirectory().getAbsoluteFile() + File.separator + "recover.sh" };
		execProcess(unixFileFormatCommandForInitiateRestart);

		// command to set permissions for file
		String[] filePermissionRestart = new String[]
		{ "chmod", "ug+x", cemsConfig.getSqlDirectory().getAbsoluteFile() + File.separator + "recover.sh" };
		execProcess(filePermissionRestart);

		// Mention the shell script to be executed
		startCommand = new String[]
		{ "sh", cemsConfig.getSqlDirectory().getAbsoluteFile() + File.separator + "recover.sh", fileName };
		retval = execProcess(startCommand);
		// executeCommand(startCommand);

	    } else
	    {
		Console.print("Server Operating system not supported.");
	    }

	} catch (Exception e)
	{
	    LoggerService.instance.error("Error occured in restore() api", RecoverDatabase.class, e);
	    retval = false;
	}
	return retval;
    }

    private boolean execProcess(String[] command)
    {
	boolean retval = true;
	StringBuffer sb = new StringBuffer();
	for (String s : command)
	{
	    sb.append(s);
	}

	LoggerService.instance.warning("[RecoverDatabase] Command String for backup/recovery in execProcess= " + sb.toString(), RecoverDatabase.class);

	Process process = null;
	try
	{
	    process = Runtime.getRuntime().exec(command);
	    process.waitFor();
	} catch (Exception e)
	{
	    retval = false;
	    LoggerService.instance.error("Error in execProcess() api in RecoverDatabase", RecoverDatabase.class, e);
	} finally
	{
	    if (process != null)
		process.destroy();
	}

	return retval;
    }
}
