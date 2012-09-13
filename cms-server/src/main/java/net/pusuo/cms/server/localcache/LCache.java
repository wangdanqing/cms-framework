package net.pusuo.cms.server.localcache;

import EDU.oswego.cs.dl.util.concurrent.ReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class LCache extends LRUMap {
    private static final Log log = LogFactory.getLog(LCache.class);

    private final ReadWriteLock rwLock = new ReentrantWriterPreferenceReadWriteLock();// new ReentrantWriterPreferenceReadWriteLock();

    public LCache() {
        super();
    }

    public LCache(int maxSize) {
        super(maxSize);
    }

    public LCache(int maxSize, int loadFactor) {
        super(maxSize, loadFactor);
    }

    public LCache(Map map) {
        super(map);
    }

    public Object get(Object key) {
        log.info("get Key:" + key);
        try {
            rwLock.readLock().acquire();
            return super.get(key);
        } catch (Exception e) {
            log.error("LCache.get exception -- ", e);
            return null;
        } finally {
            rwLock.readLock().release();
        }
    }

    public Object put(Object key, Object value) {
        log.info("put Key:" + key + " value:" + value);
        try {
            rwLock.writeLock().acquire();
            return super.put(key, value);
        } catch (Exception e) {
            log.error("LCache.put exception -- ", e);
            return null;
        } finally {
            rwLock.writeLock().release();
        }
    }

    public Object remove(Object key) {
        try {
            rwLock.writeLock().acquire();
            return super.remove(key);
        } catch (Exception e) {
            log.error("LCache.remove exception -- ", e);
            return null;
        } finally {
            rwLock.writeLock().release();
        }
    }

    public void clear() {
        try {
            rwLock.writeLock().acquire();
            super.clear();
        } catch (Exception e) {
            log.error("LCache.clear exception -- ", e);
        } finally {
            rwLock.writeLock().release();
        }
    }
}
