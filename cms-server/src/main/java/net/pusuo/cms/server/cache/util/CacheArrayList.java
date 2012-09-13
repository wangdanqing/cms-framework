package net.pusuo.cms.server.cache.util;

import org.apache.commons.collections.FastArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CacheArrayList extends FastArrayList {
    private int max_size = 0;
    private int percent = 0;//percent of the max size

    public Object lock = new Object();

    public CacheArrayList() {
        super();
        super.setFast(true);
    }

    /**
     * bx changed at 20040428
     * new para added : max
     * capacity param is redefined
     */
    public CacheArrayList(int capacity, int max, int percent) {
        super(capacity);
        super.setFast(true);
        max_size = max;
        this.percent = percent;
    }

    public CacheArrayList(Collection collection, int max, int percent) {
        super(collection);
        super.setFast(true);
        max_size = max;
        this.percent = percent;
    }

    public CacheArrayList(List list, int max, int percent) {
        super(list);
        super.setFast(true);
        max_size = max;
        this.percent = percent;
    }

    public boolean add(Object obj) {
        boolean ret = false;
        if (!contains(obj)) {
            synchronized (this) {
                if (!contains(obj)) {
                    ret = super.add(obj);
                } else {
                    return true;
                }
            }
        }
        if (max_size != 0 && percent != 0 && size() >= max_size) {
            evict();
        }
        return ret;
    }

    private void evict() {
        if (max_size != 0 && percent != 0 && size() >= max_size) {
            synchronized (this) {
                if (size() >= max_size) {
                    // sort to evict older data
                    Collections.sort(list);
                    for (int i = 0; i < max_size * percent / 100; i++) {
                        remove(0);
                    }
                }
            }
        }
    }

    public List getData() {
        return (List) super.clone();
    }

    public void setMaxSize(int s) {
        max_size = s;
    }

    public void setPercent(int p) {
        percent = p;
    }

    public int getMaxSize() {
        return max_size;
    }

    public int getPercent() {
        return percent;
    }
}
