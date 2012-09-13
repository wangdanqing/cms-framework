package net.pusuo.cms.client.xport.config;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains constant values representing various objects in Jive as well as
 * other settings such as the global locale and Jive version number.<p>
 *
 * The class also controls Jive properties. Jive properties are only meant to
 * be set and retrevied by core Jive classes.
 * <p>
 * All properties are stored in the file <tt>jive_config.xml</tt> which is
 * located in the <tt>jiveHome</tt> directory. The location of that
 * directory should be specified one of two ways:<ol>
 *   <li>Indicate its value in the <tt>jive_init.properties</tt> file. This
 *       is a standard properties file so the property should be something
 *       like:<br>
 *       <tt>jiveHome=c:\\some\\directory\\jiveHome</tt> (Windows) <br>
 *       or <br>
 *       <tt>jiveHome=/home/some/directory/jiveHome</tt> (Unix)
 *       <p>
 *       The file must be in your classpath so that it can be loaded by Java's
 *       classloader.
 *   <li>Use another class in your VM to set the
 *      <tt>JiveGlobals.jiveHome</tt> variable. This must be done before
 *      the rest of Jive starts up, for example: in a servlet that is set to run
 *      as soon as the appserver starts up.
 * </ol>
 * <p>
 * All Jive property names must be in the form <code>prop.name</code> - parts of
 * the name must be seperated by ".". The value can be any valid String,
 * including Strings with line breaks.
 */
public class CommonXportConfig {

    private static final Log log = LogFactory.getLog(CommonXportConfig.class);

    // Constant values
    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR   = 60 * MINUTE;
    public static final long DAY    = 24 * HOUR;
    public static final long WEEK   = 7 * DAY;

    public static String HOME = null;

    private static final String XMPORT_CONFIG_FILENAME = "xport_db.xml";
    private static String DYNAMIC_LOADED_XMLFILE = new String();
    private static Locale locale = null;
    
    
    /**
     * XML properties to actually get and set the Jive properties.
     */
    private static XMLProperties properties = null;
    
    public CommonXportConfig(String xmlfile)
    {
    	DYNAMIC_LOADED_XMLFILE = xmlfile;    	
    }
 
    public static String getHome() {
      if (HOME == null) {
          HOME = PropertyManager.getProperty("Xmport.home");

          // If HOME is still null, try loading it as a system property
          if (HOME == null) {
              HOME = System.getProperty("XMPORT_HOME");
          }
      }
      return HOME;
    }


    /**
     * Returns a Jive property. Jive properties are stored in the file
     * <tt>jive_config.xml</tt> that exists in the <tt>jiveHome</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * @param name the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getProperty(String name) {
        //if (properties == null) {
            loadProperties();
        //}
        if (properties == null) {
            return null;
        }
        return properties.getProperty(name);
    }

    /**
     * Sets a Jive property. If the property doesn't already exists, a new
     * one will be created. Jive properties are stored in the file
     * <tt>jive_config.xml</tt> that exists in the <tt>jiveHome</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * @param name the name of the property being set.
     * @param value the value of the property being set.
     */
    public static void setProperty(String name, String value) {
        //if (properties == null) {
            loadProperties();
        //}
        if (properties == null) {
            return;
        }
        properties.setProperty(name, value);
    }

    /**
     * Deletes a Jive property. If the property doesn't exist, the method
     * does nothing.
     *
     * @param name the name of the property to delete.
     */
    public static void deleteProperty(String name) {
        //if (properties == null) {
            loadProperties();
        //}
        if (properties == null) {
            return;
        }
        properties.deleteProperty(name);
    }

    /**
     * Returns the global Locale used by Jive. A locale specifies language
     * and country codes, and is used for internationalization. The default
     * locale is Locale.US
     *
     * @return the global locale used by Jive.
     */
    public static Locale getLocale() {
        if (locale == null) {
            loadProperties();
        }
        return locale;
    }

    /**
     * Loads properties if necessary. Property loading must be done lazily so
     * that we give outside classes a chance to set <tt>jiveHome</tt>.
     */
    private synchronized static void loadProperties() {
        //if (properties == null) {
            // Create a manager with the full path to the xml config file.
            try {
                if (HOME == null) {
                  HOME = PropertyManager.getProperty("Xmport.home");
                }
                //����ָ��xml�ļ�
                if (HOME != null) {
                    if(DYNAMIC_LOADED_XMLFILE.equals(""))
                    	properties = new XMLProperties(HOME + File.separator + XMPORT_CONFIG_FILENAME);
                    else
                    	properties = new XMLProperties(HOME + File.separator + DYNAMIC_LOADED_XMLFILE);
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        //}
     }
}