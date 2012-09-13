package net.pusuo.cms.client.db;

import java.sql.*;
import com.caucho.sql.*;

public class Cms4Db
{
	private static final Object lock = new Object();
	private static Cms4Db instance;
	private DBPool dp = null;

	private Cms4Db() throws Exception
	{
		if( dp==null )
		{
			dp = new DBPool(
				"Cms4Pool",
				"jdbc:oracle:thin:@192.168.1.170:1525:cms4",
				"cms4_test",
				"cms170",
				"oracle.jdbc.driver.OracleDriver",
				null,
				64 );
		}
	}

	public static Cms4Db getInstance() throws Exception
	{
		if( instance==null )
		{
			synchronized( lock )
			{
				if( instance==null ) instance = new Cms4Db();
			}
		}
		return instance;
	}

	public DBPool getDBPool() throws Exception
	{
		return dp;
	}
	public Connection getConnection() throws Exception
	{
		return getDBPool().getConnection();
	}

	public void close() throws Exception
	{
		getDBPool().close();
	}
}

