						================================================
						POINTS TO FOLLOW FOR IBATIS INTEGRATION WITH EMS
						================================================

1.  Keep the attrName and the Column name same - they are case sensitive.

2.	Give primitive data types in POJO (long, int etc) for sql Data types (BIGINT, INTEGER etc).

3.	For TINYINT, give boolean (primitive) in POJO. For insert, cast false as "0" and true as "1" and send it in the hash.  JdbcType for the boolean attrs in the POJOS shall be TINYINT

4.	Don't give ParameterMap in Select stmts in the XML - as of now, the PKID is messing - but it is nice to have it..

5.	Don't give ParameterMap in Update stmts in the XML - JdbcType is messing as of now - however, nice to have it.
