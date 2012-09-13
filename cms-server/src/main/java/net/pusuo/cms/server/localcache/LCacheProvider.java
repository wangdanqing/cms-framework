package net.pusuo.cms.server.localcache;

import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.Timestamper;

import java.util.Properties;

public class LCacheProvider implements CacheProvider
{
	public Cache buildCache( String regionName, Properties properties ) throws CacheException
	{
		return new LCachePlugin( regionName );
	}

	public long nextTimestamp()
	{
		return Timestamper.next();
	}

    @Override
    public void start(Properties properties) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

