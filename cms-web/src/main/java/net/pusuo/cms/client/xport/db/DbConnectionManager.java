package net.pusuo.cms.client.xport.db;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author		Alfred.Yuan
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.*;
import com.hexun.cms.client.xport.config.PropertyManager;

/**
 * Central manager of database connections. All methods are static so that they
 * can be easily accessed throughout the classes in the database package.
 */
public class DbConnectionManager {

    private static DbConnectionProvider connectionProvider;
    private static DbConnectionProvider connectionProvider61;
    private static Object providerLock = new Object();

    /**
     * Returns a database connection from the currently active connection
     * provider.
     */
    public static Connection getConnection() {
        if (connectionProvider == null) {
            synchronized (providerLock) {
                if (connectionProvider == null) {
                    //Attempt to load the connection provider classname as
                    //a Jive property.
                    String className =
                        PropertyManager.getProperty("connectionProvider.className");
                    if (className != null) {
                        //Attempt to load the class.
                        try {
                            Class conClass = Class.forName(className);
                            connectionProvider = (DbConnectionProvider)conClass.newInstance();
                        }
                        catch(Exception e) {
                            System.err.println("Warning: failed to create the " +
                                "connection provider specified by connection" +
                                "Provider.className. Using the default pool.");
                            connectionProvider = new DbConnectionDefaultPool();
                        }
                    }
                    else {
                        connectionProvider = new DbConnectionDefaultPool();
                        //connectionProvider.initPool("");
                    }
                    connectionProvider.start();
                }
            }
        }
        Connection con = connectionProvider.getConnection();
        if (con == null) {
            System.err.println("WARNING: DbConnectionManager.getConnection() " +
                "failed to obtain a connection.");
        }
        return con;
    }

    /**
     * Returns a database connection from the currently active connection provider.
     */
    public static Connection getConnectionInterface(String connname) {
        if (connectionProvider61 == null) {
            synchronized (providerLock) {
                if (connectionProvider61 == null) {
                    //Attempt to load the connection provider classname as
                        try {
                        	connectionProvider61 = new DbConnectionDefaultPool(connname);
                        	//connectionProvider.initPool(connname);
                        	connectionProvider61.start(connname);
                        }
                        catch(Exception e) {
                            System.err.println("Warning: failed to create the " +
                                "connection provider specified by connection" +
                                "Provider.className. Using the default pool.");
                            //connectionProvider = new DbConnectionDefaultPool();
                        }
                }
            }
        }
        Connection con = connectionProvider61.getConnection(connname);
        if (con == null) {
            System.err.println("WARNING: DbConnectionManager.getConnection() " +
                "failed to obtain a connection.");
        }
        return con;
    }


    /**
     * Returns the current connection provider. The only case in which this
     * method should be called is if more information about the current
     * connection provider is needed. Database connections should always be
     * obtained by calling the getConnection method of this class.
     */
    public static DbConnectionProvider getDbConnectionProvider() {
        return connectionProvider;
    }

    /**
     * Sets the connection provider. The old provider (if it exists) is shut
     * down before the new one is started. A connection provider <b>should
     * not</b> be started before being passed to the connection manager
     * because the manager will call the start() method automatically.
     */
    public static void setDbConnectionProvider(DbConnectionProvider provider) {
        synchronized (providerLock) {
            if (connectionProvider != null) {
                connectionProvider.destroy();
                connectionProvider = null;
            }
            connectionProvider = provider;
            provider.start();
        }
    }
    /*
     * ��ȡrmi server����ݿ�����
     */
    public static Connection getServerConnection(){
    	Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("��ݿ�����û�ҵ�");
		}
		String serverName = "10.0.251.122";
		String portNumber = "1521";
		String sid = "cms";
		String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
		String username = "cms4_test";
		String password = "cms170";		
    	try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
    }
}
