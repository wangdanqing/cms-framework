package net.pusuo.cms.server.swarmcache;

import java.util.*;

import org.apache.commons.collections.*;

import java.io.Serializable;

import org.apache.commons.logging.*;

/**
 * Cache implementation that uses the Least Recently Used algorithm.
 * This algorithm provides good cache hit frequency.
 * @author John Watkinson
 */

public class LRUCache implements ObjectCache {

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Inner classes
	//-------------------------------------------------------------------------

	public class ListeningLRUMap extends LRUMap {

		public ListeningLRUMap(int size) {
			super(size);
		}

		/**
		 * The listener for objects automatically removed from the cache.
		 */
		private LRUCacheListener listener = null;

		public void setListener(LRUCacheListener l) {
			listener = l;
		}

		protected void processRemovedLRU(Object key, Object value) {
			if (listener != null) {
				listener.objectRemoved((Serializable) key, value);
			}
		}
	}

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	/**
	 * The property containing the maximum number of objects to cache.
	 */
	public static final String LRU_CACHE_SIZE_PROPERTY = "lru.cache.size";

	/**
	 * The default cache size (1000).
	 */
	public static final int DEFAULT_CACHE_SIZE = 1000;

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	private String type;

	/**
	 * The maximum number of objects that can be cached.
	 */
	private int size;

	/**
	 * The map that will store the cache.
	 */
	private Map cache;

	/**
	 * A reference to underlying ListeningLRUMap
	 */
	private ListeningLRUMap map;

	public LRUCache() {
		size = DEFAULT_CACHE_SIZE;
		String property = System.getProperty(LRU_CACHE_SIZE_PROPERTY);
		if (property != null) {
			try {
				size = Integer.parseInt(property);
			} catch (NumberFormatException nfe) {
				log.warn("LRU cache size was improperly specified.");
				nfe.printStackTrace();
			}
		}
		map = new ListeningLRUMap(size);
		cache = Collections.synchronizedMap(map);
	}

	public void setSize(int newSize) {
		size = newSize;
		map.setMaximumSize(newSize);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		log.debug("Cache type set to '" + type + "'.");
		this.type = type;
	}

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
		log.debug("Cleared entire " + type + " cache.");
		cache = Collections.synchronizedMap(new LRUMap(size));
	}

	public void setListener(LRUCacheListener l) {
		map.setListener(l);
	}

}
