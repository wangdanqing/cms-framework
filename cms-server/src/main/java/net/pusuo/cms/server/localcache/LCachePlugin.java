package net.pusuo.cms.server.localcache;

import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.Timestamper;

import java.io.Serializable;

public class LCachePlugin implements Cache {
    private static final int MS_PER_MINUTE = 60000;
    private LCache cache;

    public LCachePlugin(String regionName) throws CacheException {
        try {
            LCacheManager manager = LCacheManager.getInstance();
            cache = manager.getCache(regionName);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Object get(Object key) throws CacheException {
        if (key instanceof Serializable) {
            return cache.get(key);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    public void put(Object key, Object value) throws CacheException {
        if (key instanceof Serializable) {
            cache.put(key, value);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    public void remove(Object key) throws CacheException {
        if (key instanceof Serializable) {
            cache.remove(key);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    public void clear() throws CacheException {
        cache.clear();
    }

    public void destroy() throws CacheException {
    }

    public void lock(Object key) throws CacheException {
    }

    public void unlock(Object key) throws CacheException {
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public int getTimeout() {
        return Timestamper.ONE_MS * MS_PER_MINUTE;
    }
}

