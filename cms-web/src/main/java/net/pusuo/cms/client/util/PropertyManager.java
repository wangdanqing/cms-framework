/**
 * $RCSfile: PropertyManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/24 02:23:49 $
 *
 */
package net.pusuo.cms.client.util;

import java.util.*;
import java.io.*;

/**
 PropertyManager  �ǹ���ϵͳ�����ļ���
 */
public class PropertyManager {

    private Properties properties = null;
    private Object propertiesLock = new Object();
    private String resourceURI;
    private static PropertyManager p = null;

    /**
     * Creates a new PropertyManager. Singleton access only.
     */
    public PropertyManager(String resourceURI) {
        this.resourceURI = resourceURI;
        //loadProps();
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
            synchronized(propertiesLock) {
                //Need an additional check
                if (properties == null) {
                    loadProps();
                }
            }
        }
        String property = properties.getProperty(name);
        if (property == null) {
            return null;
        }
        else {
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
            synchronized(propertiesLock) {
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
        //System.out.println("resourceURI ="+resourceURI);
        try {
            //in = getClass().getResourceAsStream(resourceURI);
            in = new FileInputStream(resourceURI);
            properties.load(in);
        }
        catch (Exception e) {
            System.err.println("Error reading Iflow properties in PropertyManager.loadProps() " + e);
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (Exception e) { }
        }
    }

    /**
     * Saves Iflow properties to disk.
     */
    private void saveProps() {
        //Now, save the properties to disk. In order for this to work, the user
        //needs to have set the path field in the properties file. Trim
        //the String to make sure there are no extra spaces.
        //String path = properties.getProperty("path").trim();
        OutputStream out = null;
        try {
            out = new FileOutputStream(resourceURI);
            properties.store(out, "Iflow.properties -- " + (new Date()));
        }
        catch (Exception ioe) {
            System.err.println("There was an error writing Iflow.properties to " + resourceURI + ". " +
                "Ensure that the path exists and that the Iflow process has permission " +
                "to write to it -- " + ioe);
            ioe.printStackTrace();
        }
        finally {
            try {
               out.close();
            } catch (Exception e) { }
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
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the Iflow.properties file exists where the path property
     * purports that it does.
     */
    public boolean propFileExists() {
        String path = getProp("path");
        if( path == null ) {
            return false;
        }
        File file = new File(path);
        if (file.isFile()) {
            return true;
        }
        else {
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
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
    *
    * main
    *
    */
     public static void main (String[] args) {
     	//String content = LocalFile.read("E:\\wzg\\pushmedia.properties");
     	//System.out.println("the old.max.id content is " + content);
        /**
     	File f = new File("E:\\wzg\\pushmedia.properties");
     	if(f.exists()) {
	     	PropertyManager p = new PropertyManager("E:\\wzg\\pushmedia.properties");
	     	String Old_MaxId = p.getProp("old.max.id");
	     	System.out.println("the old.max.id value is " + Old_MaxId);
	     	if(Old_MaxId==null) {
	     		p.setProp("old.max.id",String.valueOf(1234));
	     	}
	     	Old_MaxId = p.getProp("old.max.id");
	     	System.out.println("the old.max.id value is " + Old_MaxId);
	}
        */
     }
}
