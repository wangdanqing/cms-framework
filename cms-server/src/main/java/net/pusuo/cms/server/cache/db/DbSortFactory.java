package net.pusuo.cms.server.cache.db;

import net.pusuo.cms.server.Item;

public class DbSortFactory {
    private static DbSortFactory dsf;
    private static Integer lock = new Integer(0);

    public static DbSortFactory getInstance() {
        if (dsf == null) {
            synchronized (lock) {
                if (dsf == null) dsf = new DbSortFactory();
            }
        }
        return dsf;
    }

    private DbSortFactory() {
    }

    public Item get(int id) {
        return null;
    }

    public Item loadFromDB(int id) {
        return null;
    }
}
