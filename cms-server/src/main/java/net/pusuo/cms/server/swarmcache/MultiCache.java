package net.pusuo.cms.server.swarmcache;

import java.io.Serializable;

import org.apache.commons.logging.*;

/**
 * A wrapper cache type that notifies the multicast cache manager.
 * @author John Watkinson
 */

public class MultiCache implements ObjectCache {

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	/**
	 * The property holding the type of the underlying cache to use.
	 */
	public static final String CACHE_TYPE_PROPERTY = "multi.cache.type";

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	/**
	 * The underlying cache
	 */
	private ObjectCache cache;

	/**
	 * The cache manager.
	 */
	private MultiCacheManager manager;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------


	public MultiCache(ObjectCache cache, MultiCacheManager manager) {
		this.cache = cache;
		this.manager = manager;
	}

	public String getType() {
		return cache.getType();
	}

	public void setType(String type) {
		cache.setType(type);
		// Register this type with the manager
		manager.addCache(this);
	}

	public void put(Serializable key,
	                Object object) {
		if (cache.get(key) != null) {
			clear(key);
		}
		cache.put(key, object);
	}

	public void putOnly(Serializable key,
	                Object object) {
		cache.putOnly(key, object);
	}

	public Object get(Serializable key) {
		return cache.get(key);
	}

	/**
	 * In this implementation, the clear is multicast to all caches.
	 */
	public Object clear(Serializable key) {
		Object returnValue = cache.clear(key);
		// Multicast the request to other caches
		manager.sendClear(getType(), key);
		return returnValue;
	}

	/**
	 * Here the clear is actually done.
	 */
	public void doClear(Serializable key) {
		cache.clear(key);
	}

	public void clearAll() {
		cache.clearAll();
		// multicast the request
		manager.sendClear(getType(), MultiCacheManager.CLEAR_ALL);
	}

	public void doClearAll() {
		cache.clearAll();
	}
}
