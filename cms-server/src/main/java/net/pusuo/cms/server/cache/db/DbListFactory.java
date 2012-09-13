package net.pusuo.cms.server.cache.db;


import net.pusuo.cms.server.cache.ListItem;

public class DbListFactory {
    private static DbListFactory dlf;
    private static Integer lock = new Integer(0);

    public static DbListFactory getInstance() {
        if (dlf == null) {
            synchronized (lock) {
                if (dlf == null) dlf = new DbListFactory();
            }
        }
        return dlf;
    }

    private DbListFactory() {
    }

    public ListItem get(int id, int type) {
        return null;
    }

    public ListItem loadFromDB(int id, int type) {
        return null;
    }
}
