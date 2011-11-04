package co.pointred.core.startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;
import co.pointred.core.utils.Console;
/**
 * Backs up the Database
 * @author shajee
 *
 */

public class BackUpDatabase
{
	public boolean backup(String fileName)
	{
		boolean retval = true;
		String sqlBackupDirName = PrBeanFactory.instance.getProjectConfig().getSqlDirectory().getAbsoluteFile() + File.separator;
		try
		{
			String[] startCommand = null;

			// Check the Server Operating System
			if (System.getProperty("os.name").contains("Windows"))
			{

				// startCommand = new String[]
				// {"cmd", "/c", "start", Config.configRootDir + "run.vbs"};
				//
				// // Create a process builder and start the process execution
				// executeCommand(startCommand);
			} else if (System.getProperty("os.name").contains("Linux"))
			{
				// command to convert file from DOS to UNIX
				String[] unixFileFormatCommandForInitiateRestart = new String[]
				                                                              { "sed", "-i", "s/\\x0D$//", sqlBackupDirName + "backup.sh" };
				execProcess(unixFileFormatCommandForInitiateRestart);

				// command to set permissions for file
				String[] filePermissionRestart = new String[]
				                                            { "chmod", "ug+x", sqlBackupDirName + "backup.sh" };
				execProcess(filePermissionRestart);

				// Mention the shell script to be executed
				startCommand = new String[]
				                          { "sh", sqlBackupDirName + "backup.sh", fileName };
				executeCommand(startCommand);

			} else
			{
				// Out.println("Server Operating system not supported.");
			}

		} catch (Exception e)
		{
			retval = false;

			LoggerService.instance.warning("Error in backup() api - BackupDatabase", BackUpDatabase.class);
		}

		try
		{
			// safety precaution...
			File file = new File(fileName);
			long fileSize = file.length();
			LoggerService.instance.warning("Size of backup file on initial read = " + fileSize, BackUpDatabase.class);

			int matchCnt = 0;
			while (matchCnt < 4)
			{
				long currentSize = file.length();
				if (fileSize != currentSize)
				{
					LoggerService.instance.warning("Size of backup CHANGING !!!!.  Initial read = " + fileSize + " Current Read = " + currentSize, BackUpDatabase.class);
					fileSize = currentSize;
					matchCnt = 0;
				} else
				{
					matchCnt++;
					LoggerService.instance.warning("Size of backup matched....  Initial read = " + fileSize + " Current Read = " + currentSize, BackUpDatabase.class);
				}

				Thread.sleep(200);
			}

		} catch (Exception e)
		{
			retval = false;
			LoggerService.instance.error("Error in backup() api of BackupDatabase", BackUpDatabase.class, e);
		}

		return retval;

	}

	private boolean execProcess(String[] command)
	{
		boolean retval = true;
		Process process = null;
		try
		{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (Exception e)
		{
			retval = false;
			LoggerService.instance.error("Error occured in execProcess() api of BackupDatabase", BackUpDatabase.class, e);
		} finally
		{
			if (process != null)
				process.destroy();
		}
		return retval;
	}

	private boolean executeCommand(String[] startCommand) throws IOException
	{
		// Create a process builder and start the process execution
		boolean retval = true;
		Process process = null;
		try
		{
			ProcessBuilder processBuilder = new ProcessBuilder(startCommand);
			processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
			{
				Console.print("Line----------- " + line);
			}

		} catch (Exception e)
		{
			retval = false;
			LoggerService.instance.error("Error in executeCommand() api of BackupDatabase", BackUpDatabase.class, e);
		} finally
		{
			if (process != null)
			{
				try
				{
					process.waitFor();
					process.getInputStream().close();
					process.getOutputStream().close();
					process.getErrorStream().close();
					process.destroy();
				} catch (Exception e)
				{
					LoggerService.instance.error("Error in finally clause of executeCommand() api of BackupDatabase", BackUpDatabase.class, e);
				}
			}
		}

		return retval;
	}

}
