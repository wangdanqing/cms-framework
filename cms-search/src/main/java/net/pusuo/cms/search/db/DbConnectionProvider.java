package net.pusuo.cms.search.db;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author			Alfred.Yuan
 * @version 1.0
 */

import java.sql.Connection;
import java.util.Enumeration;

/**
 * Abstract class that defines the connection provider framework. Other classes
 * extend this abstract class to make connection to actual data sources.
 */
public abstract class DbConnectionProvider {

    /** Dummy values. Override in subclasses. **/
    private static final String NAME = "";
    private static final String DESCRIPTION = "";
    private static final String AUTHOR = "";
    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 0;
    private static final boolean POOLED = false;

    /**
     * Returns the name of the connection provider.
     *
     * @return the name of the connection provider.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns a description of the connection provider.
     *
     * @return the description of the connection provider.
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Returns the author of the connection provider.
     *
     * @return the name of the author of this connection provider.
     */
    public String getAuthor() {
        return AUTHOR;
    }

    /**
     * Returns the major version of the connection provider, i.e. 1.x.
     *
     * @return the major version number of the provider.
     */
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * Returns the minor version of the connection provider, i.e. x.1.
     *
     * @return the minor version number of the provider.
     */
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * Returns true if this connection provider provides connections out
     * of a connection pool. Implementing and using connection providers that
     * are pooled is strongly recommended, as they greatly increase the speed
     * of Jive.
     *
     * @return true if the Connection objects returned by this provider are
     *      pooled.
     */
    public boolean isPooled() {
        return POOLED;
    }

    /**
     * Returns a database connection. When a Jive component is done with a
     * connection, it will call the close method of that connection. Therefore,
     * connection pools with special release methods are not directly
     * supported by the connection provider infrastructure. Instead, connections
     * from those pools should be wrapped such that calling the close method
     * on the wrapper class will release the connection from the pool.
     *
     * @return a Connection object.
     */
    public abstract Connection getConnection();

    /**
     * Starts the connection provider. For some connection providers, this
     * will be a no-op. However, connection provider users should always call
     * this method to make sure the connection provider is started.
     */
    protected abstract void start();

    /**
     * This method should be called whenever properties have been changed so
     * that the changes will take effect.
     */
    protected abstract void restart();

    /**
     * Tells the connection provider to destroy itself. For many connection
     * providers, this will essentially result in a no-op. However,
     * connection provider users should always call this method when changing
     * from one connection provider to another to ensure that there are no
     * dangling database connections.
     */
    protected abstract void destroy();

    /**
     * Returns the value of a property of the connection provider.
     *
     * @param name the name of the property.
     * @return the value of the property.
     */
    public abstract String getProperty(String name);

    /**
     * Returns the description of a property of the connection provider.
     *
     * @param name the name of the property.
     * @return the description of the property.
     */
    public abstract String getPropertyDescription(String name);

    /**
     * Returns an enumeration of the property names for the connection provider.
     *
     * @return an Enumeration of the property names.
     */
    public abstract Enumeration propertyNames();

    /**
     * Sets a property of the connection provider. Each provider has a set number
     * of properties that are determined by the author. Trying to set a non-
     * existant property will result in an IllegalArgumentException.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     */
    public abstract void setProperty(String name, String value);

}
