package co.pointred.fx.dataobjects;

import java.util.List;
import java.util.Vector;

import co.pointred.core.constants.ProjectConstants;

public class NavigationConfig
{
    private Vector<Vector<String>> dataList;
//    private List<DBObject> mongoDataList;
    private String action = ProjectConstants._First;
    private String prevAction = ProjectConstants._First;
    private String sqlString;
    private String prevSqlString;
    private int numberOfRecords = 0;
    private long startIndex;
    private long endIndex;
    private String dbType;
    private int bucketSize;

    private long totalPages;
    private long currentPage;
    private boolean audited;

    public String getPrevSqlString()
    {
	return prevSqlString;
    }

    public void setPrevSqlString(String prevSqlString)
    {
	this.prevSqlString = prevSqlString;
    }

//    public List<DBObject> getMongoDataList()
//    {
//        return mongoDataList;
//    }
//
//    public void setMongoDataList(List<DBObject> mongoDataList)
//    {
//        this.mongoDataList = mongoDataList;
//    }

    public String getPrevAction()
    {
	return prevAction;
    }

    public void setPrevAction(String prevAction)
    {
	this.prevAction = prevAction;
    }

    public long getStartIndex()
    {
	return startIndex;
    }

    public void setStartIndex(long startIndex)
    {
	this.startIndex = startIndex;
    }

    public long getEndIndex()
    {
	return endIndex;
    }

    public void setEndIndex(long endIndex)
    {
	this.endIndex = endIndex;
    }

    public Vector<Vector<String>> getDataList()
    {
	return dataList;
    }

    public void setDataList(Vector<Vector<String>> dataList)
    {
	this.dataList = dataList;
    }

    public String getAction()
    {
	return action;
    }

    public void setAction(String action)
    {
	this.action = action;
    }

    public String getSqlString()
    {
	return sqlString;
    }

    public void setSqlString(String sqlString)
    {
	this.sqlString = sqlString;
    }

    public int getNumberOfRecords()
    {
	return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords)
    {
	this.numberOfRecords = numberOfRecords;
    }

    public long getTotalPages()
    {
	return totalPages;
    }

    public void setTotalPages(long totalPages)
    {
	this.totalPages = totalPages;
    }

    public long getCurrentPage()
    {
	return currentPage;
    }

    public void setCurrentPage(long currentPage)
    {
	this.currentPage = currentPage;
    }

    public boolean isAudited()
    {
	return audited;
    }

    public void setAudited(boolean audited)
    {
	this.audited = audited;
    }

    public String getDbType()
    {
	return dbType;
    }

    public void setDbType(String dbType)
    {
	this.dbType = dbType;
    }

    public int getBucketSize()
    {
	return bucketSize;
    }

    public void setBucketSize(int bucketSize)
    {
	this.bucketSize = bucketSize;
    }

}
