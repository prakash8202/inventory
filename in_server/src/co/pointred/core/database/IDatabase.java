package co.pointred.core.database;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import co.pointred.core.database.ibatis.SqlMapClientHolder;

public interface IDatabase
{
    public boolean createObject(Object objToCreate, String stmtName) throws Exception;

    public List<Object> selectAllAsObject(String stmtName) throws Exception;

    public List<HashMap<String, String>> selectAllAsHashMap(String stmtName) throws Exception;

    public List<HashMap<String, String>> selectAllAsHashMap(String stmtName, Object paramObj) throws Exception;

    public List<Object> selectAllAsObject(String stmtName, Object paramObj) throws Exception;

    public Object selectOneAsObject(String stmtName, Object paramObj) throws Exception;

    public Object selectOneAsObject(String stmtName) throws Exception;

    public HashMap<String, String> selectOneAsHashMap(String stmtName, Object paramObj) throws Exception;

    public int updateObject(String stmtName, Object paramObject) throws Exception;

    public int deleteRow(String stmtName, Object paramObject) throws Exception;

    public int delete(String stmtName) throws Exception;

    public Vector<Vector<String>> executeDirectSelect(String sql) throws Exception;

    public int bulkInsertByObject(String stmtName, List<Object> objectList) throws Exception;

    public int bulkInsertByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception;

    public int bulkUpdateByObject(String stmtName, List<Object> objectList) throws Exception;

    public int bulkUpdateByHashMap(String stmtName, Vector<HashMap<String, String>> objectList) throws Exception;

    public int bulkDeleteByObject(String stmtName, List<Object> objectList) throws Exception;

    public boolean start() throws Exception;

    public boolean stop() throws Exception;

    public boolean ping() throws Exception;

    public boolean createTxObject(Object objToCreate, String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int updateTxObject(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int deleteTxRow(String stmtName, Object paramObject, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int deleteTx(String stmtName, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int bulkInsertByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int bulkInsertByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int bulkUpdateByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int bulkUpdateByTxHashMap(String stmtName, Vector<HashMap<String, String>> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public int bulkDeleteByTxObject(String stmtName, List<Object> objectList, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public List<Object> selectAllAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public Object selectOneAsTxObject(String stmtName, Object paramObj, SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public SqlMapClientHolder getTwoSqlMapClientsForTx() throws Exception;

    public void beginTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public void commitTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception;

    public void endTransaction(SqlMapClientHolder sqlMapClientHolder) throws Exception;
    
    public void executeSqlQuery(String sql) throws Exception;
}