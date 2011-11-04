package co.pointred.core.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import co.pointred.core.database.ibatis.SqlMapClientHolder;
import co.pointred.core.database.replication.ReplicationController;
import co.pointred.core.database.replication.ReplicationEnum;
import co.pointred.core.database.replication.ReplicationSupport;
import co.pointred.core.log.LoggerService;

import com.ibatis.sqlmap.client.SqlMapClient;

class PrimaryDatabase extends SqlMapClientDaoSupport implements IDatabase
{

	public PrimaryDatabase()
	{

	}

	@Override
	public boolean createObject(Object objToCreate, String stmtName) throws Exception
	{
		boolean retval = true;
		SqlMapClient sqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
		try
		{
			sqlMapClient.startTransaction();
			sqlMapClient.insert(stmtName, objToCreate);
			sqlMapClient.commitTransaction();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objToCreate, ReplicationEnum.CREATE_OBJECT, null));
		} catch (Exception e)
		{
			retval = false;
			throw e;
		} finally
		{
			sqlMapClient.endTransaction();
		}
		return retval;
	}

	// UPDATE_OBJECT
	@Override
	public int updateObject(String stmtName, Object paramObject) throws Exception
	{
		int retVal = -1;
		SqlMapClient sqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
		try
		{

			sqlMapClient.startTransaction();
			retVal = sqlMapClient.update(stmtName, paramObject);
			sqlMapClient.commitTransaction();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, paramObject, ReplicationEnum.UPDATE_OBJECT, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlMapClient.endTransaction();
		}
		return retVal;
	}

	// DELETE_ROW
	@Override
	public int deleteRow(String stmtName, Object paramObject) throws Exception
	{
		int retVal = -1;
		SqlMapClient sqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
		try
		{
			sqlMapClient.startTransaction();
			retVal = sqlMapClient.delete(stmtName, paramObject);
			sqlMapClient.commitTransaction();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, paramObject, ReplicationEnum.DELETE_ROW, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlMapClient.endTransaction();
		}

		return retVal;
	}

	// DELETE MULTIPLE ROWS
	@Override
	public int delete(String stmtName) throws Exception
	{
		int retVal = -1;
		SqlMapClient sqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
		try
		{
			sqlMapClient.startTransaction();
			retVal = sqlMapClient.delete(stmtName);
			sqlMapClient.commitTransaction();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, null, ReplicationEnum.DELETE, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlMapClient.endTransaction();
		}
		return retVal;
	}

	// BULKINSERT_MAP
	@Override
	public int bulkInsertByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception
	{
		int retval = -1;
		SqlMapClient sqlClient = getSqlMapClientTemplate().getSqlMapClient();

		try
		{
			sqlClient.startTransaction();
			sqlClient.startBatch();
			for (Object obj : objectList)
			{
				sqlClient.insert(stmtName, obj);
			}
			retval = sqlClient.executeBatch();
			sqlClient.commitTransaction();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKINSERT_MAP, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlClient.endTransaction();
		}
		return retval;
	}

	// BULKINSERT_OBJECT
	@Override
	public int bulkInsertByObject(String stmtName, List<Object> objectList) throws Exception
	{
		int retval = -1;
		SqlMapClient sqlClient = getSqlMapClientTemplate().getSqlMapClient();

		try
		{
			sqlClient.startTransaction();
			sqlClient.startBatch();
			for (Object obj : objectList)
			{
				sqlClient.insert(stmtName, obj);
			}
			retval = sqlClient.executeBatch();
			sqlClient.commitTransaction();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKINSERT_OBJECT, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlClient.endTransaction();
		}
		return retval;
	}

	@Override
	public Vector<Vector<String>> executeDirectSelect(String sql) throws Exception
	{
		Vector<Vector<String>> dataVector = new Vector<Vector<String>>();
		Vector<String> innerVector = null;
		Connection conn = null;
		try
		{
			conn = getDataSource().getConnection();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			final ResultSetMetaData rsMetaData = rs.getMetaData();
			final int numOfColumns = rsMetaData.getColumnCount();
			while (rs.next())
			{
				innerVector = new Vector<String>();
				for (int ii = 1; ii < numOfColumns + 1; ii++)
				{
					if(null != rs.getObject(ii))
						innerVector.add(rs.getObject(ii).toString());
					else
						innerVector.add("-");
				}
				dataVector.add(innerVector);
			}
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				throw e;
			}
		}
		return dataVector;
	}
	
	@Override
	public void executeSqlQuery(String sql) throws Exception {
		Connection conn = null;
		try
		{
			conn = getDataSource().getConnection();
			conn.createStatement().execute(sql);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				throw e;
			}
		}
		
	}

	private HashMap<String, String> convertObjToStringVal(HashMap<String, Object> objHash)
	{
		HashMap<String, String> newMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> map : objHash.entrySet())
		{
			String attrName = map.getKey();
			if (null == map.getValue())
			{
				newMap.put(attrName, "");
				continue;
			}

			String attrVal = map.getValue().toString();
			newMap.put(attrName, attrVal);
		}
		return newMap;
	}

	@Override
	public List<Object> selectAllAsObject(String stmtName) throws Exception
	{
		List<Object> retList = null;
		try
		{
			retList = getSqlMapClientTemplate().queryForList(stmtName);
		} catch (Exception e)
		{
			throw e;
		}
		return retList;
	}

	public List<Object> selectAllAsObject(String stmtName, Object paramObj) throws Exception
	{
		List<Object> retList = null;
		try
		{
			retList = getSqlMapClientTemplate().queryForList(stmtName, paramObj);
		} catch (Exception e)
		{
			throw e;
		}
		return retList;
	}

	@Override
	public List<HashMap<String, String>> selectAllAsHashMap(String stmtName) throws Exception
	{
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try
		{
			List<Object> dbList = getSqlMapClientTemplate().queryForList(stmtName);

			for (Object obj : dbList)
			{
				HashMap<String, Object> dbHash = (HashMap<String, Object>) obj;
				HashMap<String, String> newMap = convertObjToStringVal(dbHash);
				list.add(newMap);
			}
		} catch (Exception e)
		{
			throw e;
		}
		return list;
	}

	@Override
	public List<HashMap<String, String>> selectAllAsHashMap(String stmtName, Object paramObj) throws Exception
	{
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try
		{
			List<Object> dbList = getSqlMapClientTemplate().queryForList(stmtName, paramObj);

			for (Object obj : dbList)
			{
				HashMap<String, Object> dbHash = (HashMap<String, Object>) obj;
				HashMap<String, String> newMap = convertObjToStringVal(dbHash);
				list.add(newMap);
			}
		} catch (Exception e)
		{
			throw e;
		}
		return list;
	}

	@Override
	public Object selectOneAsObject(String stmtName, Object paramObj) throws Exception
	{
		Object retObj = null;
		try
		{
			retObj = getSqlMapClientTemplate().queryForObject(stmtName, paramObj);
		} catch (Exception e)
		{
			throw e;
		}
		return retObj;
	}

	@Override
	public Object selectOneAsObject(String stmtName) throws Exception
	{
		Object retObj = null;
		try
		{
			retObj = getSqlMapClientTemplate().queryForObject(stmtName);
		} catch (Exception e)
		{
			throw e;
		}
		return retObj;
	}

	@Override
	public HashMap<String, String> selectOneAsHashMap(String stmtName, Object paramObj) throws Exception
	{
		HashMap<String, String> retHash = null;
		try
		{
			HashMap<String, Object> dbHash = (HashMap<String, Object>) getSqlMapClientTemplate().queryForObject(stmtName, paramObj);
			retHash = convertObjToStringVal(dbHash);
		} catch (Exception e)
		{
			throw e;
		}
		return retHash;
	}

	@Override
	public boolean start()
	{
		boolean retval = true;

		if (DbManager.instance.isSecondaryDbAvailable() == true)
			ReplicationController.instance.start();
		else
			ReplicationController.instance.setRemoteDirty(true);
		return retval;
	}

	@Override
	public boolean stop()
	{
		boolean retval = true;
		// put a poison object for the Replication to Stop
		ReplicationSupport rs = new ReplicationSupport("POISON", null, ReplicationEnum.POISON, null);
		ReplicationController.instance.addToReplicationQueue(rs);
		LoggerService.instance.warning("\n\t\t POISON Added to ReplicationQueue from PrimaryDatabase\n", PrimaryDatabase.class);
		ReplicationController.instance.stop();
		return retval;
	}

	@Override
	public boolean ping() throws Exception
	{
		boolean retval = true;
		try
		{
			List<Object> dbList = this.selectAllAsObject("PING");
			if (dbList == null)
				retval = false;
		} catch (Exception e)
		{
			retval = false;
		}

		return retval;
	}

	@Override
	public int bulkUpdateByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception
	{
		int retval = -1;
		SqlMapClient sqlClient = getSqlMapClient();
		try
		{
			sqlClient.startTransaction();
			sqlClient.startBatch();
			for (Object obj : objectList)
			{
				sqlClient.update(stmtName, obj);
			}
			retval = sqlClient.executeBatch();
			sqlClient.commitTransaction();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKUPDATE_MAP, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlClient.endTransaction();
		}

		return retval;

	}

	@Override
	public int bulkUpdateByObject(String stmtName, List<Object> objectList) throws Exception
	{
		int retval = -1;
		SqlMapClient sqlClient = getSqlMapClient();
		try
		{
			sqlClient.startTransaction();
			sqlClient.startBatch();
			for (Object obj : objectList)
			{
				sqlClient.update(stmtName, obj);
			}
			retval = sqlClient.executeBatch();
			sqlClient.commitTransaction();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKUPDATE_OBJECT, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlClient.endTransaction();
		}
		return retval;

	}

	@Override
	public int bulkDeleteByObject(String stmtName, List<Object> objectList) throws Exception
	{
		int retval = -1;
		SqlMapClient sqlClient = getSqlMapClient();
		try
		{
			sqlClient.startTransaction();
			sqlClient.startBatch();
			for (Object obj : objectList)
			{
				sqlClient.delete(stmtName, obj);
			}
			retval = sqlClient.executeBatch();
			sqlClient.commitTransaction();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKDELETE_OBJECT, null));
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			sqlClient.endTransaction();
		}
		return retval;

	}

	// ///////////////////////////////////////////// all Tx related APIs
	// ///////////////////////////

	@Override
	public boolean createTxObject(Object objToCreate, String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		boolean retval = true;
		try
		{
			SqlMapClient sqlMapClient = sqlMapClientHolder.getPrimarySqlMapClient();
			sqlMapClient.insert(stmtName, objToCreate);
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objToCreate, ReplicationEnum.CREATE_TX_OBJECT, sqlMapClientHolder));
		} catch (Exception e)
		{
			retval = false;
			throw e;
		}
		return retval;
	}

	@Override
	public int updateTxObject(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retVal = -1;
		try
		{
			retVal = sqlMapClientHolder.getPrimarySqlMapClient().update(stmtName, paramObject);
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, paramObject, ReplicationEnum.UPDATE_TX_OBJECT, sqlMapClientHolder));
		} catch (Exception e)
		{
			retVal = -1;
			throw e;
		}
		return retVal;
	}

	@Override
	public int deleteTxRow(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retVal = -1;
		try
		{
			retVal = sqlMapClientHolder.getPrimarySqlMapClient().delete(stmtName, paramObject);
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, paramObject, ReplicationEnum.DELETE_TX_ROW, sqlMapClientHolder));
		} catch (Exception e)
		{
			retVal = -1;
			throw e;
		}
		return retVal;
	}

	@Override
	public int deleteTx(String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retVal = -1;
		try
		{
			retVal = sqlMapClientHolder.getPrimarySqlMapClient().delete(stmtName);
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, null, ReplicationEnum.DELETE_TX, sqlMapClientHolder));
		} catch (Exception e)
		{
			retVal = -1;
			throw e;
		}
		return retVal;
	}

	@Override
	public int bulkInsertByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retval = -1;

		try
		{
			sqlMapClientHolder.getPrimarySqlMapClient().startBatch();
			for (Object obj : objectList)
			{
				sqlMapClientHolder.getPrimarySqlMapClient().insert(stmtName, obj);
			}
			retval = sqlMapClientHolder.getPrimarySqlMapClient().executeBatch();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKINSERT_TX_OBJECT, sqlMapClientHolder));
		} catch (Exception e)
		{
			throw e;
		}

		return retval;
	}

	@Override
	public int bulkInsertByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retval = -1;
		try
		{
			sqlMapClientHolder.getPrimarySqlMapClient().startBatch();
			for (Object obj : objectList)
			{
				sqlMapClientHolder.getPrimarySqlMapClient().insert(stmtName, obj);
			}
			retval = sqlMapClientHolder.getPrimarySqlMapClient().executeBatch();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKINSERT_TX_MAP, sqlMapClientHolder));
		} catch (Exception e)
		{
			throw e;
		}
		return retval;
	}

	@Override
	public int bulkUpdateByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retval = -1;
		try
		{
			sqlMapClientHolder.getPrimarySqlMapClient().startBatch();
			for (Object obj : objectList)
			{
				sqlMapClientHolder.getPrimarySqlMapClient().update(stmtName, obj);
			}
			retval = sqlMapClientHolder.getPrimarySqlMapClient().executeBatch();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKUPDATE_TX_OBJECT, sqlMapClientHolder));
		} catch (Exception e)
		{
			throw e;
		}
		return retval;
	}

	@Override
	public int bulkUpdateByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retval = -1;
		try
		{
			sqlMapClientHolder.getPrimarySqlMapClient().startBatch();
			for (Object obj : objectList)
			{
				sqlMapClientHolder.getPrimarySqlMapClient().update(stmtName, obj);
			}
			retval = sqlMapClientHolder.getPrimarySqlMapClient().executeBatch();

			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKUPDATE_TX_MAP, sqlMapClientHolder));
		} catch (Exception e)
		{
			throw e;
		}
		return retval;
	}

	@Override
	public int bulkDeleteByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		int retval = -1;
		try
		{
			sqlMapClientHolder.getPrimarySqlMapClient().startBatch();
			for (Object obj : objectList)
			{
				sqlMapClientHolder.getPrimarySqlMapClient().delete(stmtName, obj);
			}
			retval = sqlMapClientHolder.getPrimarySqlMapClient().executeBatch();
			ReplicationController.instance.addToReplicationQueue(new ReplicationSupport(stmtName, objectList, ReplicationEnum.BULKDELETE_TX_OBJECT, sqlMapClientHolder));
		} catch (Exception e)
		{
			throw e;
		}
		return retval;
	}

	@Override
	public SqlMapClientHolder getTwoSqlMapClientsForTx() throws Exception
	{
		SqlMapClient primarySqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
		SqlMapClient secondarySqlMapClient = ReplicationController.instance.getSqlMapClientFromSecondary();
		SqlMapClientHolder sqlMapClientHolder = new SqlMapClientHolder();
		sqlMapClientHolder.setPrimarySqlMapClient(primarySqlMapClient);
		sqlMapClientHolder.setSecondarySqlMapClient(secondarySqlMapClient);
		return sqlMapClientHolder;
	}

	@Override
	public void beginTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		sqlMapClientHolder.getPrimarySqlMapClient().startTransaction();
	}

	@Override
	public void commitTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		sqlMapClientHolder.getPrimarySqlMapClient().commitTransaction();
		sqlMapClientHolder.getPrimarySqlMapClient().flushDataCache();
	}

	@Override
	public void endTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		sqlMapClientHolder.getPrimarySqlMapClient().endTransaction();
	}

	@Override
	public List<Object> selectAllAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		return sqlMapClientHolder.getPrimarySqlMapClient().queryForList(stmtName, paramObj);
	}

	@Override
	public Object selectOneAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
	{
		return sqlMapClientHolder.getPrimarySqlMapClient().queryForObject(stmtName, paramObj);
	}
}