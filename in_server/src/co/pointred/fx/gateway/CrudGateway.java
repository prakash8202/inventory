package co.pointred.fx.gateway;

import java.util.HashMap;
import java.util.Vector;

import co.pointred.core.constants.ProjectConstants;
import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.core.log.LoggerService;
import co.pointred.fx.dataobjects.NavigationConfig;
import co.pointred.fx.dataobjects.UserObject;
import flex.messaging.io.amf.ASObject;

/**
 * The generic gateway for all CRUD operations
 * 
 * @author shajee
 * 
 */

public class CrudGateway
{

    public CrudGateway()
    {

    }

    public String createObject(UserObject userObject)
    {
	ASObject asObject = (ASObject) userObject.getDataObject();
	String retval = "Failed";
	HashMap<String, String> dataHash = asObject;
	String stmtName = dataHash.remove("IBATIS_STATEMENT");
	boolean create = false;
	try
	{
	    create = DbManager.instance.createObject(dataHash, stmtName);

	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally
	{
		//
	}
	if (create == true)
	    retval = SharedConstants.SUCCESS;
	return retval;
    }

    /**
     * API to select an object from the database using IBATIS stmnt
     * 
     * @param userObject
     * @return
     */
    public UserObject selectObject(UserObject userObject)
    {
	ASObject asObject = (ASObject) userObject.getDataObject();
	HashMap<String, String> hash = asObject;
	String stmtName = hash.remove("IBATIS_STATEMENT");
	try
	{
	    Object obj = DbManager.instance.selectOneAsObject(stmtName);
	    if (obj != null)
	    {
		userObject.setDataObject(obj);
	    }
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally
	{
	}
	return userObject;
    }

    /**
     * API to update an Object
     * 
     * @param asObject
     * @return
     */
    public String updateObject(UserObject userObject)
    {
	ASObject asObject = (ASObject) userObject.getDataObject();
	String retval = "Failed";
	HashMap<String, String> dataHash = asObject;
	String stmtName = dataHash.remove("IBATIS_STATEMENT");
	if (dataHash.containsKey("stmt_name_READ"))
	    dataHash.remove("stmt_name_READ");
	if (dataHash.containsKey("stmt_name_UPDATE"))
	    dataHash.remove("stmt_name_UPDATE");
	int updateCnt = -1;
	try
	{
	    updateCnt = DbManager.instance.updateObject(stmtName, dataHash);
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally
	{
	}
	if (updateCnt > 0)
	    retval = SharedConstants.SUCCESS;
	return retval;
    }

    /**
     * API to delete an object
     * 
     * @param asObject
     * @return
     */
    public String deleteObject(UserObject userObject)
    {
	ASObject asObject = (ASObject) userObject.getDataObject();
	String retval = "Failed";
	HashMap<String, String> dataHash = asObject;
	String stmntName = dataHash.remove("IBATIS_STATEMENT");
	int deleteCnt = -1;
	try
	{
	    deleteCnt = DbManager.instance.deleteRow(stmntName, dataHash);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    LoggerService.instance.error(e.getMessage(), CrudGateway.class, e);
	} finally
	{
	}
	if (deleteCnt > 0)
	    retval = SharedConstants.SUCCESS;
	return retval;
    }

    /**
     * Executes a sql coming directly from the client. API used for GRID
     * 
     * @param sql
     * @param tgtEms
     * @return
     */
    public Vector<Vector<String>> executeDirectSelect(UserObject userObject)
    {
	String sql = userObject.getDataObject().toString();
	try
	{
	    return DbManager.instance.executeDirectSelect(sql);
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error in executeDirectSelect()", CrudGateway.class, e);
	    e.printStackTrace();
	} finally
	{
	}
	return null;
    }

    /**
     * Executes a sql coming directly from the client. API used for GRID
     * 
     * @param sql
     * @param tgtEms
     * @return
     */

    public NavigationConfig getPaginatedData(UserObject userObject)
    {
	NavigationConfig navConfig = (NavigationConfig) userObject.getDataObject();

	try
	{
	    if ("0".equals(navConfig.getDbType()))
	    {
		getPaginatedDataFromMySql(navConfig);
	    } else
	    {
//		MongoDbService.instance.getPaginatedDataFromMongo(navConfig);
	    }
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error in getPagedData()", CrudGateway.class, e);
	    e.printStackTrace();
	}

	finally
	{
	}
	return navConfig;
    }

    /**
     * @param navConfig
     * @throws Exception
     */
    private void getPaginatedDataFromMySql(NavigationConfig navConfig) throws Exception
    {
	String sqlString = navConfig.getSqlString();
	String prevSqlString = navConfig.getPrevSqlString();

	int bucketSize = ProjectConstants.BUCKET_SIZE;
	if (0 < navConfig.getBucketSize())
	{
	    bucketSize = navConfig.getBucketSize();
	}

	final String action = navConfig.getAction();
	long prevStIndex = navConfig.getStartIndex();
	long currStIndex = prevStIndex;
	boolean sqlChanged = false;
	String LIMIT_SQL = "";
	// if there is a change in the sql string - because of the filter clauses, then start from the first page
	if (!sqlString.equals(prevSqlString))
	{
	    sqlChanged = true;
	    currStIndex = 0;
	    int recCount = getRecCount(sqlString);
	    int totalPages = (recCount / bucketSize) + (recCount % bucketSize > 0 ? 1 : 0);
	    navConfig.setTotalPages(totalPages);
	} else if (action.equals(SharedConstants.FIRST))
	{
	    int recCount = getRecCount(sqlString);
	    int totalPages = (recCount / bucketSize) + (recCount % bucketSize > 0 ? 1 : 0);
	    navConfig.setTotalPages(totalPages);
	    currStIndex = 0;
	} else if (action.equals(SharedConstants.LAST))
	{
	    int recCount = getRecCount(sqlString);
	    int tailRecords = recCount % bucketSize;
	    if (tailRecords == 0)
	    {
		currStIndex = recCount - bucketSize;
	    } else
	    {
		currStIndex = recCount - tailRecords;
	    }
	    int totalPages = (recCount / bucketSize) + (recCount % bucketSize > 0 ? 1 : 0);
	    navConfig.setTotalPages(totalPages);

	} else if (action.equals(SharedConstants.NEXT))
	{
	    currStIndex = prevStIndex + bucketSize;

	} else if (action.equals(SharedConstants.PREVIOUS))
	{
	    currStIndex = prevStIndex - bucketSize;

	} else if (action.equals(SharedConstants.REFRESH))
	{
	    int recCount = getRecCount(sqlString);
	    int totalPages = (recCount / bucketSize) + (recCount % bucketSize > 0 ? 1 : 0);
	    navConfig.setTotalPages(totalPages);
	}

	if (currStIndex < 0)
	{
	    currStIndex = 0;
	}

//	LIMIT_SQL = "LIMIT " + currStIndex + "s," + bucketSize;
	LIMIT_SQL = "LIMIT " + bucketSize + " OFFSET " + currStIndex;
	
	sqlString = sqlString + " " + LIMIT_SQL;

	Vector<Vector<String>> vecFromDb = DbManager.instance.executeDirectSelect(sqlString);
	navConfig.setNumberOfRecords(vecFromDb.size());
	if (vecFromDb.size() > 0)
	{
	    // update the indices properly
	    navConfig.setStartIndex(currStIndex);
	}

	if (currStIndex == prevStIndex && !action.equals(SharedConstants.REFRESH) && sqlChanged == false)
	{
	    navConfig.setNumberOfRecords(0);
	}

	navConfig.setDataList(vecFromDb);
	navConfig.setPrevAction(action);
	navConfig.setPrevSqlString(navConfig.getSqlString());
    }

    // gets the record count of the table.. used in pagination
    private int getRecCount(String sqlString) throws Exception
    {
	String upperSql = sqlString.toUpperCase();
	int idx = upperSql.indexOf("FROM");
	
	int lastIdx = upperSql.lastIndexOf("FROM");
	
//	if((lastIdx - idx) > 0)
//	{
//		idx = upperSql.indexOf("FROM PRODUCT");
//	}
	
	String sql = "Select count(*) " + sqlString.substring(idx);
	sql = sql.replace("group by", " ");
	Vector<Vector<String>> rCountVec = DbManager.instance.executeDirectSelect(sql);
	String recCountStr = rCountVec.get(0).get(0);
	int recCount = Integer.parseInt(recCountStr);
	return recCount;
    }

}
