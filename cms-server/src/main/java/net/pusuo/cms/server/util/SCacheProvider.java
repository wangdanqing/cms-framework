package net.pusuo.cms.server.util;

import net.pusuo.cms.server.swarmcache.CacheConfiguration;
import net.pusuo.cms.server.swarmcache.CacheConfigurationManager;
import net.pusuo.cms.server.swarmcache.CacheFactory;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Support for SwarmCache replicated cache. SwarmCache does not support
 * locking, so strict "read-write" semantics are unsupported.
 * @author Mark
 */
public class SCacheProvider implements CacheProvider {
	private static final Log LOG;

	static 
	{
		LOG = LogFactory.getLog(SCacheProvider.class);
	}

	static Properties pps = initializeDefaults();

	static String bind_addr = getBindAddr();

	static String mcast_addr = null;

	static Properties initializeDefaults()
	{
		LOG.debug("Initializing scache configuration defaults.");
		Properties defaults = new Properties();
		java.io.InputStream is = SCacheProvider.class.getResourceAsStream("/swarmcache.properties");
		if(is != null)
		{
			LOG.debug("Reading scache configuration from 'swarmcache.properties'...");
			Properties props = new Properties(pps);
			try
			{
				props.load(is);
			}
			catch(IOException e)
			{
				String s = "Unable to load 'swarmcache.properties' due to IOException.";
				LOG.error("Unable to load 'swarmcache.properties' due to IOException.", e);
				throw new IllegalStateException("Unable to load 'swarmcache.properties' due to IOException.");
			}
			pps = props;
		}
		else
		{
			LOG.info("Unable to load 'swarmcache.properties'... Using defaults.");
		}
		return pps;
	}

	static String getBindAddr()
	{
		String ret = null;
		String tmp = null;
		String ip_prefix = pps.getProperty("swarmcache.ip_prefix","192.168");
		LOG.debug("Initializing udp bind address.");
		try {
			Enumeration enu = NetworkInterface.getNetworkInterfaces();
			while (enu.hasMoreElements()){
				NetworkInterface ni = (NetworkInterface)(enu.nextElement());
				Enumeration ias = ni.getInetAddresses();
				while (ias.hasMoreElements()){
					InetAddress i = (InetAddress)(ias.nextElement());
					tmp = i.getHostAddress();
					if ( tmp.startsWith(ip_prefix) ) {
						ret = tmp;
						LOG.debug("bind address : " + ret);
						break;
					}
				}
			}
			if (ret==null) {
				throw new Exception ("can't bind upd address.");
			}
		}
		catch (Exception e)
		{
			LOG.error("Unable to get host address . "+e.toString());
			throw new IllegalStateException("Unable to get host address.");
		}
		return ret;
	}

	public Cache buildCache(String region, Properties properties) throws CacheException 
	{
		CacheConfiguration config = CacheConfigurationManager.getConfig(properties);
		if ( mcast_addr==null ) {
			// load multicast ip 
			mcast_addr = pps.getProperty("swarmcache.multicast.ip","224.1.2.31");
		}
		config.setChannelProperties("UDP(bind_addr="+bind_addr+";mcast_addr="+mcast_addr+";mcast_port=45567;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):pbcast.STABLE(desired_avg_gossip=20000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)");
		String cache_type = pps.getProperty(region+".swarmcache.cache.type");
		if ( cache_type != null ) {
			LOG.debug("Setting cache type to " + cache_type);
			config.setCacheType(cache_type);
		}
		String lru_size = pps.getProperty(region+".swarmcache.lru.size");
		if(lru_size != null)
		{
			LOG.debug("Setting LRU size to " + lru_size);
			config.setLRUCacheSize(lru_size);
		}
		CacheFactory factory = new CacheFactory(config);
		return new SCache( factory.createCache(region) );
	}

	public long nextTimestamp() 
	{
		return System.currentTimeMillis() / 100;
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
