package net.pusuo.cms.server.swarmcache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * CacheConfigurationManager holds a static CacheConfiguration for use
 * throughout a system. It tries to load configuration properties from a file
 * named 'swarmcache.properties' in the classpath, and uses the defaults if that
 * file does not exist.
 *
 * @author Jason Carreira
 */
public class CacheConfigurationManager {
	private static final Log LOG = LogFactory.getLog(CacheConfigurationManager.class);
	public static final String SWARMCACHE_PROP_FILE_NAME = "swarmcache.properties";
	public static final String SWARMCACHE_CHANNEL_PROPS = "swarmcache.channel.properties";
	public static final String SWARMCACHE_MULTICAST_IP = "swarmcache.multicast.ip";
	public static final String SWARMCACHE_CACHE_TYPE = "swarmcache.cache.type";
	public static final String SWARMCACHE_LRU_SIZE = "swarmcache.lru.size";

	private CacheConfigurationManager() {
	}

	static Properties defaults = initializeDefaults();

	static Properties initializeDefaults() {
		LOG.debug("Initializing cache configuration defaults.");
		Properties defaults = new Properties();
		defaults.put(SWARMCACHE_MULTICAST_IP, CacheConfiguration.DEFAULT_MULTICAST_IP);
		defaults.put(SWARMCACHE_CACHE_TYPE, CacheConfiguration.TYPE_LRU);
		defaults.put(SWARMCACHE_LRU_SIZE, "" + CacheConfiguration.DEFAULT_LRU_CACHE_SIZE);
		InputStream is = CacheConfigurationManager.class.getClassLoader().getResourceAsStream(SWARMCACHE_PROP_FILE_NAME);
		if (is != null) {
			LOG.debug("Reading cache configuration from '" + SWARMCACHE_PROP_FILE_NAME + "'...");
			Properties props = new Properties(defaults);
			try {
				props.load(is);
			} catch (IOException e) {
				final String s = "Unable to load '" + SWARMCACHE_PROP_FILE_NAME + "' due to IOException.";
				LOG.error(s, e);
				throw new IllegalStateException(s);
			}
			defaults = props;
		} else {
			LOG.info("Unable to load '" + SWARMCACHE_PROP_FILE_NAME + "'... Using defaults.");
		}
		LOG.debug("Using default values: " + defaults);
		return defaults;
	}

	static CacheConfiguration buildConfigurationInternal(Properties props) {
		CacheConfiguration myConfig = new CacheConfiguration();
		final String channelProps = props.getProperty(SWARMCACHE_CHANNEL_PROPS);
		final String multicastIp = props.getProperty(SWARMCACHE_MULTICAST_IP);
		if (channelProps != null) {
			LOG.debug("Setting channel properties to " + channelProps);
			myConfig.setChannelProperties(channelProps);
		} else if (multicastIp != null) {
			LOG.debug("Setting multicast IP to " + multicastIp);
			myConfig.setMulticastIP(multicastIp);
		} else {
			throw new IllegalArgumentException("Either the channel properties or the multicast IP address must be specified.");
		}
		final String cacheType = props.getProperty(SWARMCACHE_CACHE_TYPE);
		if (cacheType != null) {
			LOG.debug("Setting cache type to " + cacheType);
			myConfig.setCacheType(cacheType);
		}
		final String lruSize = props.getProperty(SWARMCACHE_LRU_SIZE);
		if (lruSize != null) {
			LOG.debug("Setting LRU size to " + lruSize);
			myConfig.setLRUCacheSize(lruSize);
		}
		return myConfig;
	}

	public static CacheConfiguration getConfig() {
		return buildConfigurationInternal(defaults);
	}

	public static CacheConfiguration getConfig(Properties props) {
		Properties defaultedProps = new Properties(defaults);
		defaultedProps.putAll(props);
		return buildConfigurationInternal(defaultedProps);
	}
}
