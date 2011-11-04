package co.pointred.core.database.replication;

import co.pointred.core.database.ibatis.SqlMapClientHolder;

/**
 * A support class which wraps the stmtName and the Object inside its attrs. It
 * also contains the type of db operation as a String. Based upon this String
 * value, the corresponding api is invoked over the secondary database
 * 
 * @author shajee
 * 
 */

public class ReplicationSupport
{
	private String stmtName;
	private Object dataObject;
	private ReplicationEnum apiName;
	private SqlMapClientHolder sqlMapClientHolder;

	public ReplicationSupport(String stmtName, Object dataObject, ReplicationEnum apiName, SqlMapClientHolder sqlMapClientHolder)
	{
		this.stmtName=stmtName;
		this.dataObject=dataObject;
		this.apiName=apiName;
		this.sqlMapClientHolder=sqlMapClientHolder;
	}

	public String getStmtName()
	{
		return stmtName;
	}

	public SqlMapClientHolder getSqlMapClientHolder()
	{
		return this.sqlMapClientHolder;
	}

	public Object getDataObject()
	{
		return dataObject;
	}

	public ReplicationEnum getApiName()
	{
		return apiName;
	}
}
