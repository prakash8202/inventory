package co.pointred.core.startup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import org.jboss.remoting.InvocationRequest;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;

public class MsgHndlrRecovery implements MessageHandlerIface
{
    private static final String MODULE_NAME = "[ EMS RECOVERY ]";
    private static final String sqlDumpFile = PrBeanFactory.instance.getProjectConfig().getSqlDirectory().getAbsolutePath() + File.separator + "emsbackup.sql.gz";
    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public Object invoke(InvocationRequest invocation) throws Throwable
    {
	RecoveryMsgCarrier incomingMsgCarrier = (RecoveryMsgCarrier) invocation.getParameter();
	RecoveryMsg recoveryMsg = incomingMsgCarrier.getRecoverymsg();
	RecoveryMsgCarrier returnMsgCarrier = new RecoveryMsgCarrier(RecoveryMsg.CONNECTION_ERROR);

	switch (recoveryMsg)
	{
	case PING:
	    returnMsgCarrier.setRecoverymsg(RecoveryMsg.PONG);
	    break;
	case START_DATABASE_BACKUP:
	    returnMsgCarrier = startDbBackup();
	    break;
	case SERVER_SOCKET_STARTED:
	    returnMsgCarrier = startClientAndPushDumpFile(incomingMsgCarrier);
	    break;
	case FILE_RECEIVED_SUCCESS:
	    PrStartupManager.instance.setRemoteDirty(false);
	    logWarn("invoke()", "RecoverySuccess msg Recd from Remote.. setting RemoteDirty as False");
	    returnMsgCarrier.setRecoverymsg(RecoveryMsg.FILE_RECEIVED_SUCCESS);
	default:
	    break;
	}

	return returnMsgCarrier;
    }

    private RecoveryMsgCarrier startClientAndPushDumpFile(RecoveryMsgCarrier msgCarrier)
    {
	RecoveryMsgCarrier returnMsgCarrier = new RecoveryMsgCarrier(RecoveryMsg.FILE_TX_SUCCESS);
	int serverPort = (int) msgCarrier.getValue();
	long sTime = System.currentTimeMillis();
	Socket socket = null;
	BufferedInputStream in = null;
	BufferedOutputStream out = null;
	try
	{
	    byte[] buffer = new byte[BUFFER_SIZE];
	    String servertType = PrStartupManager.instance.getServerType();

	    // take the other server name..
	    String serverAddress = PrStartupManager.instance.getSecondaryServerName();
	    if (servertType.equals(ProjectStartupConstants.SECONDARY))
		serverAddress = PrStartupManager.instance.getPrimaryServerName();

	    socket = new Socket(serverAddress, serverPort);

	    logWarn("startClientAndPushDumpFile()", "Socket for sending DumpFile created at port " + serverPort + " bound to IPAddress : " + serverAddress);
	    in = new BufferedInputStream(new FileInputStream(sqlDumpFile));
	    out = new BufferedOutputStream(socket.getOutputStream());
	    int len = 0;
	    while ((len = in.read(buffer)) > 0)
	    {
		out.write(buffer, 0, len);
	    }
	    logWarn("startClientAndPushDumpFile()", "Dump file send to Remote...Time taken = " + (System.currentTimeMillis() - sTime) + " ms");
	} catch (Exception e)
	{
	    logError("startClientAndPushDumpFile()", e);
	    returnMsgCarrier.setRecoverymsg(RecoveryMsg.FILE_TX_FAILED);
	} finally
	{
	    try
	    {
		if (socket != null)
		{
		    out.close();
		    in.close();
		    socket.close();
		}
	    } catch (Exception ee)
	    {
		logError("startClientAndPushDumpFile() - NO CHANGE IN RETURN MSG..", ee);
	    }
	}
	return returnMsgCarrier;
    }

    public RecoveryMsgCarrier startDbBackup()
    {
	RecoveryMsgCarrier msgCarrier = new RecoveryMsgCarrier(RecoveryMsg.DATABASE_BACKUP_TAKEN);
	boolean backupSuccess = false;
	try
	{
	    String fileName = sqlDumpFile;
	    File file = new File(sqlDumpFile);
	    if (file.exists())
		file.delete();

	    BackUpDatabase backupDatabase = new BackUpDatabase();
	    backupSuccess = backupDatabase.backup(fileName);
	    if (backupSuccess == true)
	    {
		file = new File(sqlDumpFile);
		long fileSize = file.length();
		msgCarrier.setValue(fileSize);
		logWarn("startDbBackup()", "Database Backup Size==" + fileSize);
	    }
	} catch (Exception e)
	{
	    msgCarrier.setRecoverymsg(RecoveryMsg.DATABASE_BACKUP_FAILED);
	    logError("startDbBackup()", e);
	}
	return msgCarrier;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, MsgHndlrRecovery.class,e);
    }

    private void logWarn(String apiName, String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + apiName + " - " + msg, MsgHndlrRecovery.class);
    }
}