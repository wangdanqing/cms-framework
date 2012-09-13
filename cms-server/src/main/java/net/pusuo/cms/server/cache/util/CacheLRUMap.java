/*
 * 
 * @author chenqj
 * Created on 2004-10-28
 *
 */
package net.pusuo.cms.server.cache.util;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

import EDU.oswego.cs.dl.util.concurrent.ReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;

/**
 * @author chenqj
 *
 */
public class CacheLRUMap extends LRUMap {

	private final ReadWriteLock rwLock = new ReentrantWriterPreferenceReadWriteLock();

	/**
	 * 
	 */
	public CacheLRUMap() {
		super();
	}

	/**
	 * @param maxSize
	 */
	public CacheLRUMap(int maxSize) {
		super(maxSize);
	}

	/**
	 * @param maxSize
	 * @param loadFactor
	 */
	public CacheLRUMap(int maxSize, float loadFactor) {
		super(maxSize, loadFactor);
	}

	/**
	 * @param map
	 */
	public CacheLRUMap(Map map) {
		super(map);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		try {
			rwLock.readLock().acquire();
			try {
				return super.get(key);
			} finally {
				rwLock.readLock().release();
			}
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		try {
			rwLock.writeLock().acquire();
			try {
				return super.put(key, value);
			} finally {
				rwLock.writeLock().release();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.collections.map.LRUMap#reuseMapping(org.apache.commons.collections.map.AbstractLinkedMap.LinkEntry, int, int, java.lang.Object, java.lang.Object)
	 */
/*
	protected synchronized void reuseMapping(LinkEntry entry, int hashIndex, int hashCode, Object key,
			Object value) {
		super.reuseMapping(entry, hashIndex, hashCode, key, value);
	}
*/
}
