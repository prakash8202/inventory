package co.pointred.core.startup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import co.pointred.core.log.LoggerService;
import co.pointred.core.spring.PrBeanFactory;

/**
 * The core class which does the recovery. The instance is made from EmsStartupManager and it is the one who keeps invoking APIs on this Contains APIs for the RECEIVER i.e the
 * Destn who is getting Recovered
 * 
 * @author shajee
 */
public class RecoveryManagerDestn
{
    private static final String MODULE_NAME = "[ EMS RECOVERY MANAGER (SRC)]";
    private static final String sqlDumpFile = PrBeanFactory.instance.getProjectConfig().getSqlDirectory().getAbsolutePath() + File.separator + "emsbackup.sql.gz";
    private static final int BUFFER_SIZE = 1024 * 4;

    private long actualFileSize;
    private int serverPortNo;

    private String fileRxStatus = PrStartupManager.FILE_TX_WAIT;
    private ServerSocketForRecovery serverSocketForRecovery;

    public RecoveryManagerDestn()
    {

    }

    public long getActualFileSize()
    {
	return actualFileSize;
    }

    public void setActualFileSize(long actualFileSize)
    {
	this.actualFileSize = actualFileSize;
    }

    public String getFileRxStatus()
    {
	return fileRxStatus;
    }

    public void setFileRxStatus(String fileRxStatus)
    {
	this.fileRxStatus = fileRxStatus;
    }

    public long getFileSizeReceived()
    {
	File file = new File(sqlDumpFile);
	return file.length();
    }

    public int getServerSocketPort()
    {
	return this.serverPortNo;
    }

    // Create a Server Socket
    public boolean startServerSocket()
    {
	boolean retval = true;
	try
	{
	    // create the socket on any available port - (0)
	    ServerSocket serverSocket = new ServerSocket(0);
	    serverSocket.setSoTimeout(1000 * 60 * 20); // 20 Min
	    this.serverPortNo = serverSocket.getLocalPort();
	    logWarn("Server socket Bound on PORT : " + serverPortNo);

	    // start the server socket Thread
	    serverSocketForRecovery = new ServerSocketForRecovery(serverSocket);
	    serverSocketForRecovery.start();
	} catch (Exception e)
	{
	    retval = false;
	    logError("startServerSocket()", e);
	}
	return retval;
    }

    private void logError(String apiName, Throwable e)
    {
	LoggerService.instance.error(MODULE_NAME + " - " + apiName, RecoveryManagerDestn.class, e);
    }

    private void logWarn(String msg)
    {
	LoggerService.instance.warning(MODULE_NAME + " - " + msg, RecoveryManagerDestn.class);
    }

    public boolean restoreDb()
    {
	boolean retval = true;

	long sTime = System.currentTimeMillis();
	try
	{
	    RecoverDatabase recoverDb = new RecoverDatabase();
	    String fileName = sqlDumpFile;
	    retval = recoverDb.restore(fileName);
	    logWarn("Recovery from Dump File State = " + retval + " Time taken = " + (System.currentTimeMillis() - sTime) + " ms");
	} catch (Exception e)
	{
	    retval = false;
	    logError("restoreDb", e);
	}

	return retval;
    }

    // ////////////////////////////////////////// SERVER SOCKET
    // /////////////////////////////////////////////

    // The server socket to recover at the Destn
    private class ServerSocketForRecovery extends Thread
    {
	ServerSocket serverSocket = null;
	File dumpFile = null;
	FileOutputStream fos = null;
	Socket clientSocket = null;
	BufferedInputStream in = null;
	BufferedOutputStream out = null;

	ServerSocketForRecovery(ServerSocket serverSocket)
	{
	    this.serverSocket = serverSocket;
	}

	@Override
	public void run()
	{
	    logWarn("ServerSocket started... on port " + serverPortNo);
	    long stTime = System.currentTimeMillis();
	    try
	    {
		File file = new File(sqlDumpFile);
		if (file.exists())
		{
		    file.delete();
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		logWarn("ServerSocket Waiting for Client to connect..!!!");
		clientSocket = serverSocket.accept();
		logWarn("Client Socket Accepted after " + (System.currentTimeMillis() - stTime) + " ms");
		dumpFile = new File(sqlDumpFile);
		in = new BufferedInputStream(clientSocket.getInputStream());
		fos = new FileOutputStream(dumpFile);
		out = new BufferedOutputStream(fos);
		int len = 0;

		setFileRxStatus(PrStartupManager.FILE_RX_IN_PROGRESS);
		logWarn("Starting to Receive Dump File...");
		while ((len = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, len);
		}
		logWarn("Dump File Received successfully after " + (System.currentTimeMillis() - stTime) + " ms");
	    } catch (Exception e)
	    {
		setFileRxStatus(PrStartupManager.FILE_RX_FAILED);
		logError("RECOVERY OF DATABASE ERRORRRRR......run()", e);
	    } finally
	    {
		try
		{
		    if (!getFileRxStatus().equals(PrStartupManager.FILE_RX_FAILED))
			setFileRxStatus(PrStartupManager.FILE_RX_SUCCESS);
		    in.close();
		    out.flush();
		    clientSocket.close();
		    serverSocket.close();
		    fos.close();
		    logWarn("Dump File Received Size === " + dumpFile.length());

		} catch (IOException ee)
		{
		    logError("run()", ee);
		}
	    }

	} // end of run()
    }// end of ServerSocket
}
