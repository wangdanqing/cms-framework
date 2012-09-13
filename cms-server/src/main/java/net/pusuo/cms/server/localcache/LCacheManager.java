package net.pusuo.cms.server.localcache;

import java.io.InputStream;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LCacheManager
{
	private static LCacheManager instance;
	private static Properties props = new Properties();

	private Map caches;

	private static final Log log = LogFactory.getLog(LCacheManager.class);

	private LCacheManager() throws Exception
	{
		this( "/localcache.properties" );
	}
	private LCacheManager( String confFileName ) throws Exception
	{
		configure( confFileName );
	}
	public static LCacheManager getInstance() throws Exception
	{
		synchronized( LCacheManager.class )
		{
			if( instance==null ) instance = new LCacheManager();
		}
		return instance;
	}

	private synchronized void configure( String confFileName ) throws Exception
	{
		log.info( "load configuration file "+confFileName+"." );
		caches = new HashMap();

		InputStream in = LCacheManager.class.getResourceAsStream( confFileName );
		try
		{
			if( in==null )
			{
				throw new Exception( "can not find LocalCache configuration file -- "+confFileName );
			}
			props.load( in );
		}catch(Exception e) {
			throw new Exception( "configure exception on loading "+confFileName, e );
		}finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e ) {
				throw new Exception( "count not close stream on "+confFileName );
			}
		}
		log.info( "localcache properties: " + props );
	}
	public void dump()
	{
		System.out.println( "properties: "+props );
	}
	public LCache getCache( String name )
	{
		LCache cache = (LCache)caches.get( name );
		if( cache==null )
		{
			cache = createCache( name );
			caches.put( name, cache );
		}
		return cache;
	}
	private LCache createCache( String name )
	{
		int maxElement = getProp( name, 1000 );
		log.info( "create LCache ["+name+"]  capacity ["+maxElement+"] ." );
		System.out.println( "create LCache ["+name+"]  capacity ["+maxElement+"] ." );
		return new LCache( maxElement );
	}

	private int getProp( String key, int defaultvalue )
	{
		String value = (String) props.get(key);
		int intValue = 0;

		if( value==null ) return defaultvalue;
		try
		{
			intValue = Integer.parseInt( value );
		}catch( NumberFormatException e ) {
			log.error( "getProp exception -- ", e );
			return defaultvalue;
		}
		return intValue;
	}
}

