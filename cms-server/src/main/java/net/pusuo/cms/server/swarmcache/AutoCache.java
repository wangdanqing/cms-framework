package net.pusuo.cms.server.swarmcache;

import java.util.*;
import java.io.Serializable;

import org.apache.commons.logging.*;
import org.apache.commons.collections.ReferenceMap;

/**
 * This cache implementation uses soft references so that cached objects are
 * automatically garbage collected when needed. Note that this maximizes the
 * cache size at the expense of making no guarantees as to the cache-clearing
 * algorithm used. So, it makes for good memory use at the possible expense of
 * cache hit frequency.
 *
 * @author John Watkinson
 */
public class AutoCache implements ObjectCache {

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	private String type;
	private Map cache;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	/**
	 * Default contsructor required.
	 */
	public AutoCache() {
		cache = Collections.synchronizedMap(new ReferenceMap());
	}

	public AutoCache(String cacheType) {
		// Use a synchronized map
		cache = Collections.synchronizedMap(new ReferenceMap());
		setType(cacheType);
	}

	//-------------------------------------------------------------------------
	// Public methods
	//-------------------------------------------------------------------------

	/**
	 * Sets the common name of the type of objects to cache.
	 */
	public void setType(String cacheType) {
		log.debug("Cache type set to '" + cacheType + "'.");
		type = cacheType;
	}

	public String getType() {
		return type;
	}

	/**
	 * Adds an object to the cache.
	 */
	public void put(Serializable key,
	                Object object) {
		cache.put(key, object);
		log.debug("Put " + type + " #" + key + " in to cache.");
	}

	public void putOnly(Serializable key,
	                Object object) {
		put(key, object);
	}

	public Object get(Serializable key) {
		Object object = cache.get(key);
		if (object != null) {
			log.debug("Got " + type + " #" + key + " from cache.");
		}
		return object;
	}

	public Object clear(Serializable key) {
		log.debug("Cleared " + type + " #" + key + " from cache.");
		return cache.remove(key);
	}

	public void clearAll() {
		// Just make a new one
		log.debug("Cleared entire " + type + " cache.");
		cache = Collections.synchronizedMap(new ReferenceMap());
	}
}
