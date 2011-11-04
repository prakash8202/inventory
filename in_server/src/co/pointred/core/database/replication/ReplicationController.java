package co.pointred.core.database.replication;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import co.pointred.core.database.DbManager;
import co.pointred.core.database.ibatis.SqlMapClientHolder;
import co.pointred.core.log.LoggerService;
import co.pointred.core.startup.PrStartupManager;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * An enum to hold the status of the Remote Server. The Primary Server would require this to determine when to stop replication
 * 
 * @author shajee
 * 
 */
public enum ReplicationController
{
    instance;

    // producer
    private volatile LinkedBlockingQueue<ReplicationSupport> replicationQueue = new LinkedBlockingQueue<ReplicationSupport>();

    // consumer
    private ReplicationThread replicationThread;

    private volatile AtomicBoolean keepConsuming = new AtomicBoolean(false);

    private ReplicationController()
    {

    }

    public void setRemoteDirty(boolean isRemoteDirty)
    {
	PrStartupManager.instance.setRemoteDirty(isRemoteDirty);
	LoggerService.instance.warning("Setting the Remote as Dirty in ReplicationController..", ReplicationController.class);
    }

    public void start()
    {
	replicationThread = new ReplicationThread();
	replicationThread.start();
    }

    public void stop()
    {
	int cnt = 0;
	while (replicationQueue.size() > 0)
	{
	    try
	    {
		ReplicationEnum r = replicationQueue.peek().getApiName();
		if (r == ReplicationEnum.POISON)
		{
		    break;
		}

		LoggerService.instance.info("Waiting for replication queue to become 0.. Obj insid is " + replicationQueue.peek().getStmtName(), ReplicationController.class);
		Thread.sleep(1000);
		cnt++;

		if (cnt >= 5)
		{
		    LoggerService.instance.warning("Waited for 5 Sec for ReplicationQueue to become Empty.. Can't help it..BREAKING !!", ReplicationController.class);
		    break;
		}
	    } catch (InterruptedException e)
	    {
		LoggerService.instance.error("Error.. Interrupted while Sleeping in stop() ao=pi of ReplicationController", ReplicationController.class);
	    }
	}
	keepConsuming.set(false);
	replicationQueue.clear();
    }

    /**
     * says whether the remote server is dirty
     * 
     * @return true if the remote server is dirty - i.e incapable of replicating
     */
    public boolean isRemoteDirty()
    {
	if (PrStartupManager.instance.isRemoteDirty() == true)
	    return true;
	return false;
    }

    public void addToReplicationQueue(ReplicationSupport rs)
    {
	try
	{
	    if (isRemoteDirty() == false)
	    {
		this.replicationQueue.put(rs);
		LoggerService.instance.info("Put an Object " + rs.getStmtName() + " in Replication Queue..", ReplicationController.class);
	    }
	} catch (InterruptedException e)
	{
	    // log a fatal error..
	    e.printStackTrace();
	    LoggerService.instance.error(e.getMessage(), ReplicationController.class, e);
	}
    }

    public SqlMapClient getSqlMapClientFromSecondary()
    {
	if (isRemoteDirty() == false)
	{
	    try
	    {
		return DbManager.instance.getSecondaryDatabase().getTwoSqlMapClientsForTx().getSecondarySqlMapClient();
	    } catch (Exception e)
	    {
		LoggerService.instance.error("Error getting sqlMapClient from Secondary", ReplicationController.class, e);
		e.printStackTrace();
	    }
	}

	return null;
    }

    /**
     * The replication thread
     * 
     * @author shajee
     * 
     */
    private class ReplicationThread extends Thread
    {
	@Override
	public void run()
	{
	    LoggerService.instance.info("Replication Thread Started !!.. At the start of Replication Thread.. keepConsiming is = " + keepConsuming.get(),
		    ReplicationController.class);

	    keepConsuming.set(true);

	    while (keepConsuming.get() == true)
	    {
		try
		{
		    ReplicationSupport rs = replicationQueue.take();
		    LoggerService.instance.info("Took an Object " + rs.getStmtName() + " from replication queue !!", ReplicationController.class);
		    if (rs.getApiName() == ReplicationEnum.POISON)
		    {
			replicationQueue.clear();
			keepConsuming.set(false);
			// log for closing of Replication
			LoggerService.instance.info("Replication Thread consumed Poison.. Stopping RealTime Replication...", ReplicationController.class);
		    } else
		    {
			try
			{
			    replicate(rs);
			} catch (ReplicationException e)
			{
			    LoggerService.instance.error("Replication Failed attempt = 1/3.. will retry after 5 sec", ReplicationController.class, e);
			    // try once more..waiting for 5 sec
			    Thread.currentThread().sleep(5000);
			    try
			    {
				replicate(e.getReplicationSupport());
			    } catch (ReplicationException e1)
			    {
				LoggerService.instance.error("Replication Failed attempt = 2/3.. will retry after 5 sec", ReplicationController.class, e);
				Thread.currentThread().sleep(5000);
				try
				{
				    replicate(e1.getReplicationSupport());
				} catch (ReplicationException e2)
				{
				    LoggerService.instance.error("Replication Failed attempt = 3/3.. will retry after 5 sec", ReplicationController.class, e);
				    // mark the remote as dirty
				    setRemoteDirty(true);

				    keepConsuming.set(false);

				    // clear all the queue..
				    replicationQueue.clear();

				    replicationQueue.put(new ReplicationSupport("POISON", null, null, null));

				    LoggerService.instance.error(e2.getMessage(), ReplicationController.class, e);
				}
			    }
			}
		    }
		} catch (InterruptedException e)
		{
		    keepConsuming.set(false);
		    e.printStackTrace();
		    LoggerService.instance.error("Replication Thread interrupted.." + e.getMessage(), ReplicationController.class, e);
		}
	    }

	    System.out.println("Exited out of Consumer Thread...!!!");
	}

	private void replicate(ReplicationSupport rs) throws ReplicationException
	{
	    if (isRemoteDirty() == true)
	    {
		LoggerService.instance.warning("Replication cannot happen as isRemoteDirty() is holding true value", ReplicationController.class);
		return;
	    }

	    String stmtName = rs.getStmtName();
	    Object dataObj = rs.getDataObject();
	    SqlMapClientHolder sqlMapClientHolder = rs.getSqlMapClientHolder();

	    try
	    {
		switch (rs.getApiName())
		{
		case CREATE_OBJECT:
		{
		    DbManager.instance.getSecondaryDatabase().createObject(dataObj, stmtName);
		    break;
		}
		case UPDATE_OBJECT:
		{
		    DbManager.instance.getSecondaryDatabase().updateObject(stmtName, dataObj);
		    break;
		}
		case BULKINSERT_OBJECT:
		{
		    List<Object> objList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkInsertByObject(stmtName, objList);
		    break;
		}
		case BULKINSERT_MAP:
		{
		    Vector<HashMap<String, String>> dataVec = (Vector<HashMap<String, String>>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkInsertByHashMap(stmtName, dataVec);
		    break;
		}
		case DELETE_ROW:
		{
		    DbManager.instance.getSecondaryDatabase().deleteRow(stmtName, dataObj);
		    break;
		}
		case DELETE:
		{
		    DbManager.instance.getSecondaryDatabase().delete(stmtName);
		    break;
		}
		case BULKUPDATE_MAP:
		{
		    Vector<HashMap<String, String>> objectMap = (Vector<HashMap<String, String>>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkUpdateByHashMap(stmtName, objectMap);
		    break;
		}

		case BULKUPDATE_OBJECT:
		{
		    List<Object> objectList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkUpdateByObject(stmtName, objectList);
		    break;
		}
		case BULKDELETE_OBJECT:
		{
		    List<Object> objectList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkDeleteByObject(stmtName, objectList);
		    break;
		}

		    // /////////////////////// ALL TX ORIENTED START FROM BELOW /////////////////////////////////
		case CREATE_TX_OBJECT:
		{
		    DbManager.instance.getSecondaryDatabase().createTxObject(dataObj, stmtName, sqlMapClientHolder);
		    break;
		}
		case UPDATE_TX_OBJECT:
		{
		    DbManager.instance.getSecondaryDatabase().updateTxObject(stmtName, dataObj, sqlMapClientHolder);
		    break;
		}
		case DELETE_TX_ROW:
		{
		    DbManager.instance.getSecondaryDatabase().deleteTxRow(stmtName, dataObj, sqlMapClientHolder);
		    break;
		}
		case DELETE_TX:
		{
		    DbManager.instance.getSecondaryDatabase().deleteTx(stmtName, sqlMapClientHolder);
		    break;
		}
		case BULKINSERT_TX_OBJECT:
		{
		    List<Object> objectList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkInsertByTxObject(stmtName, objectList, sqlMapClientHolder);
		    break;
		}
		case BULKINSERT_TX_MAP:
		{
		    Vector<HashMap<String, String>> objectMap = (Vector<HashMap<String, String>>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkInsertByTxHashMap(stmtName, objectMap, sqlMapClientHolder);
		    break;
		}
		case BULKUPDATE_TX_OBJECT:
		{
		    List<Object> objectList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkUpdateByTxObject(stmtName, objectList, sqlMapClientHolder);
		    break;
		}
		case BULKUPDATE_TX_MAP:
		{
		    Vector<HashMap<String, String>> objectMap = (Vector<HashMap<String, String>>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkUpdateByTxHashMap(stmtName, objectMap, sqlMapClientHolder);
		    break;
		}
		case BULKDELETE_TX_OBJECT:
		{
		    List<Object> objectList = (List<Object>) dataObj;
		    DbManager.instance.getSecondaryDatabase().bulkDeleteByTxObject(stmtName, objectList, sqlMapClientHolder);
		    break;
		}
		case BEGIN_TX:
		{
		    DbManager.instance.getSecondaryDatabase().beginTransaction(sqlMapClientHolder);
		    break;
		}

		default:
		    break;
		}
	    } catch (Exception e)
	    {
		ReplicationException rx = new ReplicationException(rs, e);
		LoggerService.instance.error("Failed to Replicate..error msg is : " + e.getMessage(), ReplicationController.class, e);
		throw rx;
	    }
	}
    } // end of ReplicationThread

    private class ReplicationException extends Exception
    {
	private static final long serialVersionUID = -4608832302651527208L;
	private ReplicationSupport rs;

	public ReplicationException(ReplicationSupport rs, Exception e)
	{
	    super(e.getMessage());
	    super.setStackTrace(e.getStackTrace());
	    this.rs = rs;
	}

	public ReplicationSupport getReplicationSupport()
	{
	    return this.rs;
	}
    }
}// end of ReplicationController