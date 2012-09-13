package net.pusuo.cms.server.swarmcache;

import java.util.*;

import org.apache.commons.collections.*;

import java.io.Serializable;

import org.apache.commons.logging.*;

/**
 * Cache implementation that times out cached elements.
 * Each item put in the cache is timed out after a specified number of milliseconds (unless removed before the timeout).
 *
 * @author John Watkinson
 */
public class TimerCache implements ObjectCache, Runnable {

	//-------------------------------------------------------------------------
	// Inner classes
	//-------------------------------------------------------------------------

	static class TStampObject {
		public long time;
		public Object object;
	}

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	/**
	 * The property containing the cache timeout (in milliseconds).
	 */
	public static final String CACHE_TIMEOUT_PROPERTY = "cache.timeout";

	/**
	 * The default cache timeout (1 minute).
	 */
	public static final int DEFAULT_CACHE_TIMEOUT = 10000;

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	/**
	 * Cache type
	 */
	private String type;

	/**
	 * The next wake-up deadline for the queue thread.
	 */
	private long deadline = Long.MAX_VALUE;

	/**
	 * True if the cache-clearing thread should keep running.
	 */
	private boolean running = true;

	/**
	 * The timer thread that clears cached objects.
	 */
	private Thread thread;

	/**
	 * The cache timeout (in milliseconds).
	 */
	private long timeout;

	/**
	 * The map that will store the cache.
	 */
	private UnboundedLRUMap cache;

	public TimerCache() {
		timeout = DEFAULT_CACHE_TIMEOUT;
		// Checks to see if there is a System property that sets the cache timeout.
		String property = System.getProperty(CACHE_TIMEOUT_PROPERTY);
		if (property != null) {
			try {
				timeout = Long.parseLong(property);
			} catch (NumberFormatException nfe) {
				log.warn("Cache timeout was improperly specified.");
				nfe.printStackTrace();
			}
		}
		cache = new UnboundedLRUMap();
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Sets a new timeout value-- only do this before using the cache!
	 */
	public void setTimeout(long newTimeout) {
		timeout = newTimeout;
	}

	/**
	 * Called to stop the timer thread. This should only be called once and the cache should not be used afterwards.
	 */
	public synchronized void stop() {
		running = false;
		notify();
	}

	/**
	 * Gets the cache type name.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the cache type name.
	 */
	public void setType(String type) {
		log.debug("Cache type set to '" + type + "'.");
		this.type = type;
		thread.setName("TimerCache-" + type);
	}

	/**
	 * Caches an object. If object is <code>null</code>, then any object cached at the given key is removed from the cache.
	 */
	public void put(Serializable key,
	                Object object) {
		if (object == null) {
			clear(key);
		} else {
			TStampObject tobj = new TStampObject();
			tobj.object = object;
			tobj.time = System.currentTimeMillis() + timeout;
			cache.put(key, tobj);
			setDeadline(tobj.time);
			log.debug("Put " + type + " #" + key + " in to cache.");
		}
	}

	public void putOnly(Serializable key,
	                Object object) {
		put(key, object);
	}

	public Object get(Serializable key) {
		TStampObject tobj = (TStampObject) cache.get(key);
		if (tobj != null) {
			log.debug("Got " + type + " #" + key + " from cache.");
			tobj.time = System.currentTimeMillis() + timeout;
			return tobj.object;
		} else {
			return null;
		}
	}

	public Object clear(Serializable key) {
		log.debug("Cleared " + type + " #" + key + " from cache.");
		return cache.remove(key);
	}

	public void clearAll() {
		log.debug("Cleared entire " + type + " cache.");
		cache.clear();
	}

	//// Timer methods

	private synchronized void setDeadline(long time) {
		if (time < deadline) {
			deadline = time;
			notify();
		}
	}

	public void run() {
		while (running) {
			try {
				synchronized (this) {
					long now = System.currentTimeMillis();
					long waitTime = deadline - now;
					if (waitTime > 0) {
						wait(waitTime);
					}
				}
			} catch (Exception e) {
				log.error(e);
			}
			trimQueue();
		}
	}

	private void trimQueue() {
		log.debug("Waking up.");
		Object key = cache.getFirstKey();
		TStampObject tobj = (TStampObject) cache.get(key);
		if (tobj != null) {
			long now = System.currentTimeMillis();
			while ((tobj != null) && (tobj.time <= now)) {
				cache.remove(key);
				log.debug("Timed out object with key '" + key + "'.");
				key = cache.getFirstKey();
				tobj = (TStampObject) cache.get(key);
			}
		}
		if (tobj != null) {
			deadline = tobj.time;
		} else {
			deadline = Long.MAX_VALUE;
		}
		log.debug("Sleeping.");
	}
}
