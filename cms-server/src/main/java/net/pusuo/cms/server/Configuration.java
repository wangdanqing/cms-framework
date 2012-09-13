package net.pusuo.cms.server;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration
{
	private static final Log log = LogFactory.getLog(Configuration.class);
	
	private Properties properties = new Properties();

	private static Configuration globalconf = null;

	private static final Object lock = new Object();

	public static Configuration getInstance()
	{
		if( globalconf == null )
		{
			synchronized( lock )
			{
				if( globalconf==null ) globalconf = new Configuration();
			}
		}
		return globalconf;
	} 

	private Configuration () 
	{
		this("/cms4.properties");
	}

	public Configuration (String filename) 
	{
		reset(filename);
	}

	protected void reset(String filename) {
		InputStream in = null;
		try {
			in = Configuration.class.getResourceAsStream(filename);
			log.debug(filename);
			properties.load(in);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
				log.error("could not close stream on config file ", ioe);
			}
		}
	}
	
	public String get(String property) {
		return properties.getProperty(property);
	}
	
	public int getInt(String property) {
		try {
			return Integer.valueOf(get(property)).intValue();
		} catch (Exception e) {
			log.error(e);
			return Integer.MAX_VALUE;
		}
	}

	public boolean getBoolean(String property) {
		try {
			return Boolean.valueOf(get(property)).booleanValue();
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}

}
