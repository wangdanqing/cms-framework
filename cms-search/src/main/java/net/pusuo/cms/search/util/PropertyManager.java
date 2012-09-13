package net.pusuo.cms.search.util;

import java.util.*;
import java.io.*;

/**
 * PropertyManager  是管理系统配置文件的，配置文件为iflow.properties
 */
public class PropertyManager {

    /**
     * The Major version number of Iflow. i.e. 1.x.x
     */
    private static final int MAJOR_VERSION = 2;

    /**
     * The Minor version number of Iflow. i.e. x.1.x.
     */
    private static final int MINOR_VERSION = 0;

    /**
     * The revision version number of Iflow. i.e. x.x.1.
     */
    private static final int REVISION_VERSION = 0;

    private static PropertyManager manager = null;
    private static Object managerLock = new Object();
    private static String propsName = "/search.properties";

    /**
     * Returns a Iflow property.
     *
     * @param name the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getProperty(String name) {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        return manager.getProp(name);
    }

    /**
     * Sets a Iflow property. If the property doesn't already exists, a new
     * one will be created.
     *
     * @param name  the name of the property being set.
     * @param value the value of the property being set.
     */
    public static void setProperty(String name, String value) {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        manager.setProp(name, value);
    }

    /**
     * Deletes a Iflow property. If the property doesn't exist, the method
     * does nothing.
     *
     * @param name the name of the property to delete.
     */
    public static void deleteProperty(String name) {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        manager.deleteProp(name);
    }

    /**
     * Returns the names of the Iflow properties.
     *
     * @return an Enumeration of the Iflow property names.
     */
    public static Enumeration propertyNames() {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        return manager.propNames();
    }

    /**
     * Returns true if the properties are readable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public static boolean propertyFileIsReadable() {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        return manager.propFileIsReadable();
    }

    /**
     * Returns true if the properties are writable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public static boolean propertyFileIsWritable() {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        return manager.propFileIsWritable();
    }

    /**
     * Returns true if the Iflow.properties file exists where the path property
     * purports that it does.
     */
    public static boolean propertyFileExists() {
        if (manager == null) {
            synchronized (managerLock) {
                if (manager == null) {
                    manager = new PropertyManager(propsName);
                }
            }
        }
        return manager.propFileExists();
    }

    /**
     * Returns the version number of Iflow as a String. i.e. -- major.minor.revision
     */
    public static String getIflowVersion() {
        return MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;
    }

    /**
     * Returns the major version number of Iflow. i.e. -- 1.x.x
     */
    public static int getIflowVersionMajor() {
        return MAJOR_VERSION;
    }

    /**
     * Returns the minor version number of Iflow. i.e. -- x.1.x
     */
    public static int getIflowVersionMinor() {
        return MINOR_VERSION;
    }

    /**
     * Returns the revision version number of Iflow. i.e. -- x.x.1
     */
    public static int getIflowVersionRevision() {
        return REVISION_VERSION;
    }

    private Properties properties = null;
    private Object propertiesLock = new Object();
    private String resourceURI;

    /**
     * Creates a new PropertyManager. Singleton access only.
     */
    private PropertyManager(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * Gets a Iflow property. Iflow properties are stored in Iflow.properties.
     * The properties file should be accesible from the classpath. Additionally,
     * it should have a path field that gives the full path to where the
     * file is located. Getting properties is a fast operation.
     *
     * @param name the name of the property to get.
     * @return the property specified by name.
     */
    protected String getProp(String name) {
        //If properties aren't loaded yet. We also need to make this thread
        //safe, so synchronize...
        if (properties == null) {
            synchronized (propertiesLock) {
                //Need an additional check
                if (properties == null) {
                    loadProps();
                }
            }
        }
        String property = properties.getProperty(name);
        if (property == null) {
            return null;
        } else {
            return property.trim();
        }
    }

    /**
     * Sets a Iflow property. Because the properties must be saved to disk
     * every time a property is set, property setting is relatively slow.
     */
    protected void setProp(String name, String value) {
        //Only one thread should be writing to the file system at once.
        synchronized (propertiesLock) {
            //Create the properties object if necessary.
            if (properties == null) {
                loadProps();
            }
            properties.setProperty(name, value);
            saveProps();
        }
    }

    protected void deleteProp(String name) {
        //Only one thread should be writing to the file system at once.
        synchronized (propertiesLock) {
            //Create the properties object if necessary.
            if (properties == null) {
                loadProps();
            }
            properties.remove(name);
            saveProps();
        }
    }

    protected Enumeration propNames() {
        //If properties aren't loaded yet. We also need to make this thread
        //safe, so synchronize...
        if (properties == null) {
            synchronized (propertiesLock) {
                //Need an additional check
                if (properties == null) {
                    loadProps();
                }
            }
        }
        return properties.propertyNames();
    }

    /**
     * Loads Iflow properties from the disk.
     */
    private void loadProps() {
        properties = new Properties();
        InputStream in = null;
        //System.out.println(resourceURI);
        try {
            in = getClass().getResourceAsStream(resourceURI);
            properties.load(in);
        } catch (Exception e) {
            System.err.println("Error reading Iflow properties in PropertyManager.loadProps() " + e);
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Saves Iflow properties to disk.
     */
    private void saveProps() {
        //Now, save the properties to disk. In order for this to work, the user
        //needs to have set the path field in the properties file. Trim
        //the String to make sure there are no extra spaces.
        String path = properties.getProperty("path").trim();
        OutputStream out = null;
        try {
            out = new FileOutputStream(path);
            properties.store(out, "Iflow.properties -- " + (new java.util.Date()));
        } catch (Exception ioe) {
            System.err.println("There was an error writing Iflow.properties to " + path + ". " +
                    "Ensure that the path exists and that the Iflow process has permission " +
                    "to write to it -- " + ioe);
            ioe.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns true if the properties are readable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public boolean propFileIsReadable() {
        try {
            InputStream in = getClass().getResourceAsStream(resourceURI);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the Iflow.properties file exists where the path property
     * purports that it does.
     */
    public boolean propFileExists() {
        String path = getProp("path");
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if (file.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the properties are writable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public boolean propFileIsWritable() {
        String path = getProp("path");
        File file = new File(path);
        if (file.isFile()) {
            //See if we can write to the file
            if (file.canWrite()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
