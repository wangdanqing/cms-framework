package net.pusuo.cms.server.util;

import net.pusuo.cms.server.swarmcache.ObjectCache;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * @author Mark
 */
public class SCache implements Cache {

    private static final Log log = LogFactory.getLog(SCache.class);

    private ObjectCache cache;

    public SCache(ObjectCache cache) {
        this.cache = cache;
    }

    /**
     * Get an item from the cache
     *
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws CacheException
     */
    public Object get(Object key) throws CacheException {
        if (key instanceof Serializable) {
            return cache.get((Serializable) key);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    /**
     * Add an item to the cache
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(Object key, Object value) throws CacheException {
        if (key instanceof Serializable) {
            //do it in swarmcache
            //cache.clear( (Serializable) key );
            cache.put((Serializable) key, value);

            //log.info("put object["+key+"] to cache");
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    /**
     * Remove an item from the cache
     */
    public void remove(Object key) throws CacheException {
        if (key instanceof Serializable) {
            cache.clear((Serializable) key);
            //log.info("remove object["+key+"] from cache");
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    /**
     * Clear the cache
     */
    public void clear() throws CacheException {
        cache.clearAll();
    }

    /**
     * Clean up
     */
    public void destroy() throws CacheException {
        cache.clearAll();
    }

    /**
     * If this is a clustered cache, lock the item
     */
    public void lock(Object key) throws CacheException {
        throw new UnsupportedOperationException("SwarmCache does not support locking (use nonstrict-read-write)");
    }

    /**
     * If this is a clustered cache, unlock the item
     */
    public void unlock(Object key) throws CacheException {
        throw new UnsupportedOperationException("SwarmCache does not support locking (use nonstrict-read-write)");
    }

    /**
     * Generate a (coarse) timestamp
     */
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    /**
     * Get a reasonable "lock timeout"
     */
    public int getTimeout() {
        return 600;
    }

}
