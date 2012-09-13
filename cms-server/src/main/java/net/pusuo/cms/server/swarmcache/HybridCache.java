package net.pusuo.cms.server.swarmcache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.LRUMap;

import java.util.Map;
import java.util.Collections;
import java.io.Serializable;

/**
 * A hybrid cache solution that uses a (presumably small) LRU cache backed by a larger AutoCache.
 * This implementation aims to be a good trade-off between cache hit frequency and agressive memory usage.
 */
public class HybridCache implements ObjectCache, LRUCacheListener {

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	private String type;

	/**
	 * The LRU Cache.
	 */
	private LRUCache lruCache;

	/**
	 * The LRU Cache.
	 */
	private AutoCache autoCache;

	public HybridCache() {
		lruCache = new LRUCache();
		autoCache = new AutoCache();
		// Listen for automatic removals of objects
		lruCache.setListener(this);
	}

	/**
	 * Sets the size of the LRU cache.
	 * <b>Note</b>: this clears the LRU cache!
	 */
	public void setSize(int newSize) {
		lruCache.setSize(newSize);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		log.debug("Cache type set to '" + type + "'.");
		this.type = type;
		lruCache.setType(type);
		autoCache.setType(type);
	}

	/**
	 * Called when an object is automatically removed from the LRU cache.
	 */
	public void objectRemoved(Serializable key, Object value) {
		// Put the removed object in the auto cache.
		autoCache.put(key, value);
		log.debug("Moved " + type + " #" + key + " to the auto cache.");
	}

	public void put(Serializable key,
	                Object object) {
		lruCache.put(key, object);
		log.debug("Put " + type + " #" + key + " in to cache.");
	}

	public void putOnly(Serializable key,
	                Object object) {
		put(key, object);
	}

	public Object get(Serializable key) {
		Object object = lruCache.get(key);
		if (object != null) {
			log.debug("Got " + type + " #" + key + " from LRU cache.");
		} else {
			object = autoCache.get(key);
			if (object != null) {
				log.debug("Got " + type + " #" + key + " from auto cache.");
				// Upgrade to LRU cache
				lruCache.put(key, object);
				autoCache.clear(key);
			}
		}
		return object;
	}

	public Object clear(Serializable key) {
		log.debug("Cleared " + type + " #" + key + " from cache.");
		Object o = lruCache.clear(key);
		if (o != null) {
			return o;
		} else {
			o = autoCache.clear(key);
			return o;
		}
	}

	public void clearAll() {
		log.debug("Cleared entire " + type + " cache.");
		lruCache.clearAll();
		autoCache.clearAll();
	}

}
