package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.cache.exception.CacheException;

public class CacheConfig {
    private static CacheConfig config = null;
    private static Integer lock = new Integer(0);

    protected static void buildCacheConfig(Configuration conf) {
        if (config == null) {
            synchronized (lock) {
                if (config == null) config = new CacheConfig(conf);
            }
        }
    }

    public static CacheConfig getInstance()
            throws CacheException {
        if (config == null) throw new CacheException("CacheConfig not init ...");
        return config;
    }

    private CacheConfig() {
    }

    private Configuration cacheconfig = null;

    private CacheConfig(Configuration config) {
        cacheconfig = config;
    }

    public String get(String property) throws CacheException {
        return cacheconfig.get(property);
    }

    public int getInt(String property) throws CacheException {
        return Integer.parseInt(get(property));
    }

    public int getInt(String property, int defaultValue) {
        int value = defaultValue;
        try {
            String valueParam = get(property);
            if (valueParam != null)
                value = Integer.parseInt(valueParam);
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }

    public boolean getBoolean(String property) throws CacheException {
        return Boolean.valueOf(get(property)).booleanValue();
    }

    public boolean getBoolean(String property, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            String valueParam = get(property);
            if (valueParam != null)
                value = Boolean.parseBoolean(valueParam);
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }

}
