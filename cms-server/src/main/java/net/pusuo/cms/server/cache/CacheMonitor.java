/*
 * 
 * @author chenqj
 * Created on 2004-10-27
 *
 */
package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.cache.exception.CacheException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Properties;

/**
 * @author chenqj
 */
public class CacheMonitor extends UnicastRemoteObject implements MonitorInterface {

    private static final long serialVersionUID = 1L;

    private static CacheMonitor cf = null;

    private CacheMonitor() throws RemoteException {
    }

    public static CacheMonitor getInstance() throws RemoteException {
        if (cf == null) {
            synchronized (CacheMonitor.class) {
                if (cf == null)
                    cf = new CacheMonitor();
            }
        }
        return cf;
    }

    /* (non-Javadoc)
      * @see com.hexun.cms.cache.MonitorInterface#resetList(int, int)
      */
    public List resetList(int pid, int type) throws RemoteException, CacheException {
        return ListManager.getInstance().resetList(pid, type);
    }

    /* (non-Javadoc)
      * @see com.hexun.cms.cache.MonitorInterface#sysInfo()
      */
    public Properties sysInfo() throws RemoteException, CacheException {
        Properties info = new Properties();

        CacheConfig config = CacheConfig.getInstance();
        SortManager sm = SortManager.getInstance();
        ListManager lm = ListManager.getInstance();
        info.put("cache.manager.list.maxsize", config.get("cache.manager.list.maxsize"));
        info.put("cache.manager.sort.maxsize", config.get("cache.manager.sort.maxsize"));
        info.put("cache.object.list.initsize", config.get("cache.object.list.initsize"));
        info.put("cache.object.list.maxsize", config.get("cache.object.list.maxsize"));
        info.put("cache.manager.list.cursize", "" + lm.size());
        info.put("cache.manager.sort.cursize", "" + sm.size());
        return info;
    }

}
