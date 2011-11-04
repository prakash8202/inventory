package co.pointred.core.database.ibatis;

import java.io.Serializable;

import com.ibatis.sqlmap.client.SqlMapClient;

public class SqlMapClientHolder implements Serializable
{
    private static final long serialVersionUID = -707919544046506872L;
    private SqlMapClient primarySqlMapClient;
    private SqlMapClient secondarySqlMapClient;

    public SqlMapClientHolder()
    {

    }

    public SqlMapClient getPrimarySqlMapClient()
    {
	return primarySqlMapClient;
    }

    public void setPrimarySqlMapClient(SqlMapClient primarySqlMapClient)
    {
	this.primarySqlMapClient = primarySqlMapClient;
    }

    public SqlMapClient getSecondarySqlMapClient()
    {
	return secondarySqlMapClient;
    }

    public void setSecondarySqlMapClient(SqlMapClient secondarySqlMapClient)
    {
	this.secondarySqlMapClient = secondarySqlMapClient;
    }
}
