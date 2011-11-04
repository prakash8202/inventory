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

import com.ibatis.sqlmap.client.SqlMapClient;

class SecondaryDatabase extends SqlMapClientDaoSupport implements IDatabase
{

    public SecondaryDatabase()
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
		    innerVector.add(rs.getObject(ii).toString());
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
	return true;
    }

    @Override
    public boolean stop()
    {
	return true;
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
    public boolean createTxObject(Object objToCreate, String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	boolean retval = true;
	try
	{
	    sqlMapClientHolder.getSecondarySqlMapClient().insert(stmtName, objToCreate);
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
	    retVal = sqlMapClientHolder.getSecondarySqlMapClient().update(stmtName, paramObject);
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
	    retVal = sqlMapClientHolder.getSecondarySqlMapClient().delete(stmtName, paramObject);
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
	    retVal = sqlMapClientHolder.getSecondarySqlMapClient().delete(stmtName);
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
	    sqlMapClientHolder.getSecondarySqlMapClient().startBatch();
	    for (Object obj : objectList)
	    {
		sqlMapClientHolder.getSecondarySqlMapClient().insert(stmtName, obj);
	    }
	    retval = sqlMapClientHolder.getSecondarySqlMapClient().executeBatch();
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
	    sqlMapClientHolder.getSecondarySqlMapClient().startBatch();
	    for (Object obj : objectList)
	    {
		sqlMapClientHolder.getSecondarySqlMapClient().insert(stmtName, obj);
	    }
	    retval = sqlMapClientHolder.getSecondarySqlMapClient().executeBatch();
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
	    sqlMapClientHolder.getSecondarySqlMapClient().startBatch();
	    for (Object obj : objectList)
	    {
		sqlMapClientHolder.getSecondarySqlMapClient().update(stmtName, obj);
	    }
	    retval = sqlMapClientHolder.getSecondarySqlMapClient().executeBatch();
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
	    sqlMapClientHolder.getSecondarySqlMapClient().startBatch();
	    for (Object obj : objectList)
	    {
		sqlMapClientHolder.getSecondarySqlMapClient().update(stmtName, obj);
	    }
	    retval = sqlMapClientHolder.getSecondarySqlMapClient().executeBatch();
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
	    sqlMapClientHolder.getSecondarySqlMapClient().startBatch();
	    for (Object obj : objectList)
	    {
		sqlMapClientHolder.getSecondarySqlMapClient().delete(stmtName, obj);
	    }
	    retval = sqlMapClientHolder.getSecondarySqlMapClient().executeBatch();
	} catch (Exception e)
	{
	    throw e;
	}
	return retval;
    }

    @Override
    public SqlMapClientHolder getTwoSqlMapClientsForTx() throws Exception
    {
	SqlMapClientHolder sqlMapClientHolder = new SqlMapClientHolder();
	// no need for setting the primarysqlMapClient..
	sqlMapClientHolder.setSecondarySqlMapClient(getSqlMapClientTemplate().getSqlMapClient());
	return sqlMapClientHolder;
    }

    @Override
    public void beginTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	sqlMapClientHolder.getSecondarySqlMapClient().startTransaction();
    }

    @Override
    public void commitTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	sqlMapClientHolder.getSecondarySqlMapClient().commitTransaction();
	sqlMapClientHolder.getPrimarySqlMapClient().flushDataCache();
    }

    @Override
    public void endTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	sqlMapClientHolder.getSecondarySqlMapClient().endTransaction();
    }

    @Override
    public List<Object> selectAllAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {

	return sqlMapClientHolder.getSecondarySqlMapClient().queryForList(stmtName, paramObj);
    }

    @Override
    public Object selectOneAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return sqlMapClientHolder.getSecondarySqlMapClient().queryForObject(stmtName, paramObj);
    }

	@Override
	public void executeSqlQuery(String sql) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
