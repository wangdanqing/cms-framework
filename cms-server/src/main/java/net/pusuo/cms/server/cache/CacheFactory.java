package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.Configuration;

/**
 * 所有CACHE类的工厂类，用于产生各种CACHE ITEM实例
 *
 * @author bx
 * @version 4.0
 * @see Configuration
 * @since CMS4.0
 */

public class CacheFactory {
    private static CacheFactory factory = null;

    private CacheFactory() {
    }

    public static CacheFactory getInstance() throws Exception {
        if (factory == null) {
            throw new Exception("CacheFactory is not inited.");
        }
        return factory;
    }

    /**
     * 一般在Configuration里面调用，可用于动态改变实例的加载过程
     */
    public static void buildFactory(Configuration config) throws Exception {
        CacheConfig.buildCacheConfig(config);
        factory = new CacheFactory();
    }

    public ListItem createList() {
        return new ListItem();
    }
}
