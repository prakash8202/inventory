package co.pointred.core.database;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import co.pointred.core.constants.ProjectStartupConstants;
import co.pointred.core.database.ibatis.SqlMapClientHolder;
import co.pointred.core.log.LoggerService;

public enum DbManager implements IDatabase
{
    instance;

    private volatile IDatabase iDatabase;
    private String serverType = ProjectStartupConstants.emsServerType;
    private SecondaryDatabase secondaryDatabase;
    private ApplicationContext applicationContext;

    private DbManager()
    {
	this.applicationContext = new ClassPathXmlApplicationContext("co/pointred/core/spring/databaseConfig.xml");
    }

    /**
     * sets up the Database depending upon the server type. The secondaryDatabase variable will be always null if the serverType is Secondary
     */
    private void setupDatabase()
    {
	if (serverType.equals("PRIMARY"))
	{
	    this.iDatabase = applicationContext.getBean("databasePrimary", PrimaryDatabase.class);
	    try
	    {
		this.secondaryDatabase = applicationContext.getBean("databaseSecondary", SecondaryDatabase.class);
	    } catch (Exception e)
	    {
		LoggerService.instance.error("Error in getting connection to Secondary DB..ther will be no Replication.." + e.getMessage(), DbManager.class, e);
	    }
	} else
	{
	    this.iDatabase = applicationContext.getBean("databaseSecondary", SecondaryDatabase.class);
	}
    }

    public boolean isSecondaryDbAvailable()
    {
	boolean secondaryAlive = true;
	try
	{
	    secondaryAlive = this.secondaryDatabase.ping();
	} catch (Exception e)
	{
	    secondaryAlive = false;
	}
	return secondaryAlive;
    }

    @Override
    public boolean ping() throws Exception
    {
	return iDatabase.ping();
    }

    @Override
    public boolean createObject(Object objToCreate, String stmtName) throws Exception
    {
	return iDatabase.createObject(objToCreate, stmtName);
    }

    @Override
    public List<Object> selectAllAsObject(String stmtName) throws Exception
    {
	return iDatabase.selectAllAsObject(stmtName);
    }

    public List<Object> selectAllAsObject(String stmtName, Object paramObj) throws Exception
    {
	return iDatabase.selectAllAsObject(stmtName, paramObj);
    }

    @Override
    public List<HashMap<String, String>> selectAllAsHashMap(String stmtName) throws Exception
    {
	return iDatabase.selectAllAsHashMap(stmtName);
    }

    @Override
    public List<HashMap<String, String>> selectAllAsHashMap(String stmtName, Object paramObj) throws Exception
    {
	return iDatabase.selectAllAsHashMap(stmtName, paramObj);
    }

    @Override
    public Object selectOneAsObject(String stmtName, Object paramObj) throws Exception
    {
	return iDatabase.selectOneAsObject(stmtName, paramObj);
    }

    @Override
    public Object selectOneAsObject(String stmtName) throws Exception
    {
	return iDatabase.selectOneAsObject(stmtName);
    }

    @Override
    public HashMap<String, String> selectOneAsHashMap(String stmtName, Object paramObj) throws Exception
    {
	return iDatabase.selectOneAsHashMap(stmtName, paramObj);
    }

    @Override
    public int updateObject(String stmtName, Object paramObject) throws Exception
    {
	return iDatabase.updateObject(stmtName, paramObject);
    }

    @Override
    public int deleteRow(String stmtName, Object paramObject) throws Exception
    {
	return iDatabase.deleteRow(stmtName, paramObject);
    }

    @Override
    public int delete(String stmtName) throws Exception
    {
	return iDatabase.delete(stmtName);
    }

    @Override
    public Vector<Vector<String>> executeDirectSelect(String sql) throws Exception
    {
	return iDatabase.executeDirectSelect(sql);
    }

    @Override
    public int bulkInsertByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception
    {
	return iDatabase.bulkInsertByHashMap(stmtName, objectList);

    }

    @Override
    public int bulkInsertByObject(String stmtName, List<Object> objectList) throws Exception
    {
	return iDatabase.bulkInsertByObject(stmtName, objectList);
    }

    @Override
    public int bulkUpdateByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception
    {
	return iDatabase.bulkUpdateByHashMap(stmtName, objectList);
    }

    @Override
    public int bulkUpdateByObject(String stmtName, List<Object> objectList) throws Exception
    {
	return iDatabase.bulkUpdateByObject(stmtName, objectList);
    }

    @Override
    public int bulkDeleteByObject(String stmtName, List<Object> objectList) throws Exception
    {
	return iDatabase.bulkDeleteByObject(stmtName, objectList);
    }

    public IDatabase getSecondaryDatabase()
    {
	return this.secondaryDatabase;
    }

    /**
     * sets up the Database type depending upon the server type. Call this API from the state manager. Everytime the datasource is obtained, we get the same instance from the
     * beanfactory, so no issue using it again and again
     */

    @Override
    public boolean start() throws Exception
    {
	setupDatabase();
	return iDatabase.start();
    }

    /**
     * There is no way to stop the data source through spring.. so just make the iDatabase as null.. and that should stop the communication with the Database layer altogether
     */
    @Override
    public boolean stop() throws Exception
    {
	boolean retval = iDatabase.stop();
	// set the iDatabase to Null so that no one can talk to the database
	iDatabase = null;
	LoggerService.instance.warning("IDatabase NULLIFIED in DbManager.. no one can talk to DB now", DbManager.class);
	return retval;
    }

    @Override
    public boolean createTxObject(Object objToCreate, String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.createTxObject(objToCreate, stmtName, sqlMapClientHolder);
    }

    @Override
    public int updateTxObject(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.updateTxObject(stmtName, paramObject, sqlMapClientHolder);
    }

    @Override
    public int deleteTxRow(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.deleteTxRow(stmtName, paramObject, sqlMapClientHolder);
    }

    @Override
    public int deleteTx(String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.deleteTx(stmtName, sqlMapClientHolder);
    }

    @Override
    public int bulkInsertByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.bulkInsertByTxObject(stmtName, objectList, sqlMapClientHolder);
    }

    @Override
    public int bulkInsertByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.bulkInsertByTxHashMap(stmtName, objectList, sqlMapClientHolder);
    }

    @Override
    public int bulkUpdateByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.bulkUpdateByTxObject(stmtName, objectList, sqlMapClientHolder);
    }

    @Override
    public int bulkUpdateByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.bulkUpdateByTxHashMap(stmtName, objectList, sqlMapClientHolder);
    }

    @Override
    public int bulkDeleteByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.bulkDeleteByTxObject(stmtName, objectList, sqlMapClientHolder);
    }

    @Override
    public SqlMapClientHolder getTwoSqlMapClientsForTx() throws Exception
    {
	return iDatabase.getTwoSqlMapClientsForTx();
    }

    @Override
    public void beginTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	iDatabase.beginTransaction(sqlMapClientHolder);
    }

    @Override
    public void commitTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	iDatabase.commitTransaction(sqlMapClientHolder);
    }

    @Override
    public void endTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	iDatabase.endTransaction(sqlMapClientHolder);
    }

    @Override
    public List<Object> selectAllAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.selectAllAsTxObject(stmtName, paramObj, sqlMapClientHolder);
    }

    @Override
    public Object selectOneAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception
    {
	return iDatabase.selectOneAsTxObject(stmtName, paramObj, sqlMapClientHolder);
    }

	@Override
	public void executeSqlQuery(String sql) throws Exception {
		iDatabase.executeSqlQuery(sql);
	}
}