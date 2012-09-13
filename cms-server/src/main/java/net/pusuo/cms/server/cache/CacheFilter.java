package net.pusuo.cms.server.cache;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CacheFilter extends UnicastRemoteObject implements FilterInterface {

    private static final long serialVersionUID = 1L;

    private static CacheFilter cf = null;

    private CacheFilter() throws RemoteException {
    }

    public static CacheFilter getInstance() throws RemoteException {
        if (cf == null) {
            synchronized (CacheFilter.class) {
                if (cf == null)
                    cf = new CacheFilter();
            }
        }
        return cf;
    }

    // Alfred.Yuan 2007.1.24
    public List filter(Query query) throws RemoteException {

        // ��֤��ѯ����
        if (!Query.verify(query))
            return null;

        // ��ʼ��Filterʵ��
        Filter filter = query.initFilter();
        if (filter == null)
            return null;

        // ί�й�����
        return filter.filter(query);
    }

    public List TimeFilter(int id, int type, int start, int count) throws RemoteException {

        Query query = new Query();
        query.setId(id);
        query.setType(type);
        query.setStart(start);
        query.setCount(count);

        query.setSortType(Query.SORT_TYPE_TIME);

        return filter(query);
    }

    public List TimeFilter(int id, int type, int minp, int maxp, int count) throws RemoteException {

        Query query = new Query();
        query.setId(id);
        query.setType(type);
        query.setMinPriority(minp);
        query.setMaxPriority(maxp);
        query.setCount(count);

        query.setSortType(Query.SORT_TYPE_PRIORITY_AND_TIME);

        return filter(query);
    }

    public List PrioFilter(int id, int type, int minp, int maxp, int count) throws RemoteException {

        Query query = new Query();
        query.setId(id);
        query.setType(type);
        query.setMinPriority(minp);
        query.setMaxPriority(maxp);
        query.setCount(count);

        query.setSortType(Query.SORT_TYPE_PRIORITY);

        return filter(query);
    }

}
