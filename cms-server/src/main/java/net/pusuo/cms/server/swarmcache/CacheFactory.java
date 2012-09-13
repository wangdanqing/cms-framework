package net.pusuo.cms.server.swarmcache;

import org.apache.commons.logging.*;

/**
 * A convenient generator of multicast caches using an underlying LRU or Automatic algorithm.
 *
 * @author John Watkinson
 * @author Rajeev Kaul
 */
public class CacheFactory {
	Log log = LogFactory.getLog(this.getClass());
	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------
	private Class cacheType;
	private int lruCacheSize;
	private boolean isLRU;
	private MultiCacheManager manager;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	protected CacheFactory() {
	}

	public CacheFactory(CacheConfiguration conf) {
		cacheType = null;
		String cacheTypeProperty = conf.getCacheType();
		if (cacheTypeProperty != null) {
			try {
				// Set up the underlying cache
				isLRU = false;
				if (CacheConfiguration.TYPE_LRU.equals(cacheTypeProperty)) {
					cacheType = LRUCache.class;
					isLRU = true;
				} else if (
				        CacheConfiguration.TYPE_AUTO.equals(cacheTypeProperty)) {
					cacheType = AutoCache.class;
				} else if (
				        CacheConfiguration.TYPE_TIMER.equals(cacheTypeProperty)) {
					cacheType = TimerCache.class;
				} else if (
				        CacheConfiguration.TYPE_HYBRID.equals(cacheTypeProperty)) {
					cacheType = HybridCache.class;
					isLRU = true;
				} else {
					throw new Exception(
					        "Unknown cache type: " + cacheType + ".");
				}
				if (isLRU) {
					if (conf.getLRUCacheSize() != null) {
						lruCacheSize = Integer.parseInt(conf.getLRUCacheSize());
					} else {
						lruCacheSize = LRUCache.DEFAULT_CACHE_SIZE;
					}
				}
				// Try to instantiate one to make sure it works
				ObjectCache sampleCache = (ObjectCache) cacheType.newInstance();
				// Set up the Manager
				String channelProperties = conf.getChannelProperties();
				if (channelProperties == null) {
					throw new Exception("Either the channel properties or the multicast IP address must be specified.");
				}
				log.debug(
				        "Creating a JavaGroups cache manager with properties: "
				        + channelProperties);
				// Set up communication channel
				manager = MultiCacheManager.getManager(channelProperties);
			} catch (Exception e) {
				log.error("Problem instantiating cache:", e);
			}
		}
	}
	//-------------------------------------------------------------------------
	// Public methods
	//-------------------------------------------------------------------------
	/**
	 * Creates a new cache.
	 * @param name a name for the cache. Useful if there will be multiple caches for various objects.
	 * @return a new cache of either the LRU or AUTO underlying type, based on configuration.
	 */
	public ObjectCache createCache(String name) {
		ObjectCache cache = null;
		MultiCache multi = null;
		//
		// do not create cache, if it already exists
		if (manager.containsCache(name)) {
			log.error("Cache of type [" + name + "] already exists.");
		} else {
			if (cacheType != null) {
				try {
					cache = (ObjectCache) cacheType.newInstance();
					if (cache instanceof LRUCache) {
						((LRUCache) cache).setSize(lruCacheSize);
					} else if (cache instanceof HybridCache) {
						((HybridCache) cache).setSize(lruCacheSize);
					}
					cache.setType(name);
					multi = new MultiCache(cache, manager);
					manager.addCache(multi);
				} catch (Exception e) {
					log.error("Problem instantiating cache:");
					e.printStackTrace();
				}
			}
		}
		return multi;
	}

	/**
	 * Returns the manager that handles inter-cache communication.
	 */
	public MultiCacheManager getCacheManager() {
		return manager;
	}

	/**
	 *  CacheFactory lifecycle shutdown method.
	 *  This method should be called before exiting an application.
	 *  Otherwise, the application will not terminate.
	 */

	public void shutdown() {
		MultiCacheManager.shutDown();
	}
}
