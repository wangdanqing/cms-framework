package net.pusuo.cms.server.swarmcache;

import java.util.*;
import java.io.Serializable;

import org.apache.commons.logging.*;

/**
 * Manages the communications between other cache managers.
 * @author John Watkinson
 * @author Rajeev Kaul
 */

public class MultiCacheManager {

	protected static Map instances = new HashMap();

	public static synchronized MultiCacheManager getManager(String channelProperties) {
		MultiCacheManager manager = (MultiCacheManager) instances.get(channelProperties);
		if (manager == null) {
			Communicator communicator = new JavaGroupsCommunicator(channelProperties);
			manager = new MultiCacheManager(communicator);
			communicator.setManager(manager);
			instances.put(channelProperties, manager);
		}
		return manager;
	}

	/*
	 *  Shutdown all the managers in the list
	 *  @author Rajeev Kaul
	 */
	public static synchronized void shutDown() {
		if (!instances.isEmpty()) {
			Set keys = instances.keySet();
			Iterator iter = keys.iterator();
			String key;
			MultiCacheManager manager;
			while (iter.hasNext()) {
				key = (String) iter.next();
				manager = (MultiCacheManager) instances.get(key);
				manager.close();
			}
		}
	}

	/*
	 * closes a manager by stopping its communication bus
	 * @author Rajeev Kaul
	 */
	public void close() {
		comm.shutDown();
	}

	Log log = LogFactory.getLog(this.getClass());

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	public static final String CLEAR_ALL = "(ALL)";

	//-------------------------------------------------------------------------
	// Fields
	//-------------------------------------------------------------------------

	private HashMap caches;
	private Communicator comm;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	public MultiCacheManager(Communicator comm) {
		caches = new HashMap();
		this.comm = comm;
	}

	//-------------------------------------------------------------------------
	// Public Methods
	//-------------------------------------------------------------------------

	public void addCache(MultiCache cache) {
		caches.put(cache.getType(), cache);
		log.info( "addCache "+cache.getType()+" to MultiCacheManager." );
	}

	/*
	 * checks if a manager contains the cache of a certain type
	 * @param type of cache
	 * @return true if cache exists, false otherwise
	 *
	 * @author Rajeev Kaul
	 */
	public boolean containsCache(String type) {
		return caches.containsKey(type);
	}

	public void sendClear(String type, Serializable key) {
		CacheNotification notification = new CacheNotification(type, key);
		// Send this to all cache managers
		log.info("Sending a clear to all cache managers: (" + type + ", " + key + ").");
		comm.send(notification);
	}

	public void receiveNotification(CacheNotification notification) {
		// Get the cache to clear this object
		MultiCache cache = (MultiCache) caches.get(notification.getType());
		/*
		 * for cms4.0
		 * I hate this dirty code , but it can solve our problem:)
		 * added by Mark 2004.10.21
		 */
		if ( cache==null ) {
			String type = notification.getType().toString();
			int index = type.lastIndexOf(".");
			if ( index>0 ) {
				type = type.substring(0,index);
				cache = (MultiCache) caches.get(type);
			}
		}
		/*
		 * end added
		 */
		if (cache != null) {
			if (notification.getKey() == CLEAR_ALL) {
				log.info("Received a clear-all: (" + notification.getType() + ".");
				cache.doClearAll();
			} else {
				log.info("Received a clear: (" + notification.getType() + ", " + notification.getKey() + ").");
				cache.doClear(notification.getKey());
			}
		} else {
			log.info("Received info about an object that we do not cache here: " + notification.getType() + ".");
		}
	}

}
