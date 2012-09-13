package net.pusuo.cms.server.ad;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.file.LocalFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;

public class ADManagerImpl extends UnicastRemoteObject implements ADManagerInterface {

    private static final Log log = LogFactory.getLog(ADManagerImpl.class);

    private static final Object lock = new Object();
    private static ADManagerImpl instance = null;

    private static final String addatapath = Configuration.getInstance().get("cms4.ad.addata");

    private static HashMap hPool = new HashMap(100);
    private static final long serialVersionUID = -6171961114793280721L;

    public static ADManagerImpl getInstance() {
        try {
            if (addatapath == null) throw new NullPointerException("required cms4.ad.addata in configuration file.");

            if (instance == null) {
                synchronized (lock) {
                    if (instance == null) instance = new ADManagerImpl();
                }
            }
        } catch (Exception e) {
            log.error("getinstance exception. " + e.toString());
        }
        return instance;
    }

    private ADManagerImpl() throws RemoteException {
        load();
    }

    private void setResource(Object key, Object value) {
        hPool.put(key, value);
    }

    private void removeResource(Object key) {
        hPool.remove(key);
    }

    public void load() throws RemoteException {
        try {
            String content = LocalFile.read(addatapath);
            if (content == null) return;

            String[] ids = content.split("\\|");
            for (int i = 0; ids != null && i < ids.length; i++) {
                setResource(ids[i], "Y");
            }
        } catch (Exception e) {
            log.error("load addatapath exception. " + e.toString());
        }
    }

    public StringBuffer list() throws RemoteException {
        try {
            StringBuffer sb = new StringBuffer();
            Iterator itr = hPool.keySet().iterator();
            while (itr.hasNext()) {
                String item = (String) itr.next();
                sb.append(item);
                sb.append("<br>");
            }
            return sb;
        } catch (Exception e) {
            log.error("list data exception. " + e.toString());
            return null;
        }
    }

    public boolean append(int fragId, int entityId) throws RemoteException {
        try {
            if (belong(fragId, entityId)) return true;
            hPool.put(fragId + "@" + entityId, "Y");
            return writeFile();
        } catch (Exception e) {
            log.error("append data exception. " + e.toString());
            return false;
        }
    }

    public boolean delete(int fragId, int entityId) throws RemoteException {
        try {
            removeResource(fragId + "@" + entityId);
            return writeFile();
        } catch (Exception e) {
            log.error("delete data exception. " + e.toString());
            return false;
        }
    }

    public boolean belong(int fragId, int entityId) throws RemoteException {
        Object obj = hPool.get(fragId + "@" + entityId);
        if (obj == null) return false;
        else return true;
    }

    private boolean writeFile() throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            Iterator itr = hPool.keySet().iterator();
            if (itr.hasNext()) {
                sb.append((String) itr.next());
            }
            while (itr.hasNext()) {
                sb.append("|");
                sb.append((String) itr.next());
            }
            return LocalFile.write(sb.toString(), addatapath);
        } catch (Exception e) {
            log.error("write data file exception. " + e.toString());
            return false;
        }
    }
}


