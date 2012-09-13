package net.pusuo.cms.server.swarmcache;

import java.util.*;

import org.apache.commons.collections.LRUMap;

import java.io.*;

import org.apache.commons.logging.*;

/**
 * An LRUMap that allows an unbounded size.
 * This implementation is an amortized O(1) amount slower than the bounded Jakarta Commons LRUMap implementation.
 *
 * @author John Watkinson
 */
public class UnboundedLRUMap implements Map {

	Log log = LogFactory.getLog(this.getClass());

	public static final int MINIMUM_SIZE = 100;

	private LRUMap map;
	int size = MINIMUM_SIZE;

	public UnboundedLRUMap() {
		map = new LRUMap(size);
	}

	public void clear() {
		// Shrink the map down to minimum size
		if (size > MINIMUM_SIZE) {
			map = new LRUMap(MINIMUM_SIZE);
			size = MINIMUM_SIZE;
		} else {
			map.clear();
		}
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object obj) {
		return map.equals(((UnboundedLRUMap) obj).map);
	}

    public int hashCode() {
        return map.hashCode();
    }

	public Object get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set keySet() {
		return map.keySet();
	}

	public Object put(Object key, Object value) {
		// See if the "put" is actually a remove
		if (value == null) {
			if (map.get(key) != null) {
				// See if we are using less than %25 of the map
				if ((map.size() == size / 4) && (size > MINIMUM_SIZE)) {
					// Shrink the map
					size = size / 2;
					log.debug("Map capacity has shrunk to " + size + ", size is " + (map.size() - 1) + ".");
					LRUMap newMap = new LRUMap(size);
					newMap.putAll(map);
					map = newMap;
				}
				return map.put(key, value);
			} else {
				// Nothing to add
				return null;
			}
		}
		// Check to see if it will be a replace
		if (map.get(key) != null) {
			// Just do the replace
			return map.put(key, value);
		} else {
			// Check and see if we have overflowed this map
			if (map.size() == size) {
				// Make a new map of double size
				size = size * 2;
				log.debug("Map capacity has grown to " + size + ", size is " + (map.size() + 1) + ".");
				LRUMap newMap = new LRUMap(size);
				newMap.putAll(map);
				map = newMap;
			}
			return map.put(key, value);
		}
	}

	public void putAll(Map t) {
		Iterator keys = t.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			put(key, t.get(key));
		}
	}

	public Object remove(Object key) {
		if (map.get(key) != null) {
			// Check to see if we have less than %25 utilization
			if ((map.size() == size / 4) && (size > MINIMUM_SIZE)) {
				// Shrink the map
				size = size / 2;
				log.debug("Map capacity has shrunk to " + size + ", size is " + (map.size() - 1) + ".");
				LRUMap newMap = new LRUMap(size);
				newMap.putAll(map);
				map = newMap;
			}
			return map.remove(key);
		} else {
			// Nothing to remove
			return null;
		}
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		return map.values();
	}

	public Object getFirstKey() {
		return map.getFirstKey();
	}
}
