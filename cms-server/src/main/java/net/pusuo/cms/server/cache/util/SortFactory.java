package net.pusuo.cms.server.cache.util;

import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.ItemProxy;
import net.pusuo.cms.server.cache.CacheConfig;
import net.pusuo.cms.server.cache.db.DbSortFactory;
import net.pusuo.cms.server.cache.exception.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SortFactory {
    boolean usehibernate = true;
    private static Log log = LogFactory.getLog(SortFactory.class);

    private SortFactory()
            throws CacheException {
        usehibernate = CacheConfig.getInstance().getBoolean("cache.object.sort.usehibernate");
    }

    public Item get(int id)
            throws CacheException {
        try {
            String classname = CacheConfig.getInstance().get("cache.object.sort.class");
            Class ins = Class.forName(classname);
            Item si = (Item) ins.newInstance();
            if (usehibernate)
                //return (Item)ItemProxy.getInstance().get( si , new Integer(id) );
                return (Item) ItemProxy.getInstance().get(new Integer(id), ins);
            else return DbSortFactory.getInstance().get(id);
        } catch (Exception e) {
            throw new CacheException(e.getMessage());
        }
    }

    private static SortFactory sf;

    public static SortFactory getInstance()
            throws CacheException {
        if (sf == null) {
            synchronized (SortFactory.class) {
                if (sf == null) sf = new SortFactory();
            }
        }
        return sf;
    }
}
