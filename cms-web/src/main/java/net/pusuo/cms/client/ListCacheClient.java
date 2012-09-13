package net.pusuo.cms.client;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.cache.FilterInterface;
import com.hexun.cms.cache.MonitorInterface;
import com.hexun.cms.cache.Query;
import com.hexun.cms.cache.exception.CacheException;
import com.hexun.cms.client.util.ClientUtil;

public class ListCacheClient {
	private static FilterInterface fi = null;

	private static MonitorInterface mi = null;

	private static ListCacheClient cc = null;

	private static Log log = LogFactory.getLog(ListCacheClient.class);

	public static ListCacheClient getInstance() {
		try {
			if (cc == null) {
				synchronized (ListCacheClient.class) {
					if (cc == null) {
						cc = new ListCacheClient();
					}
				}
			}
			return cc;
		} catch (Exception e) {
			log.error("Unable to create ListCacheClient instance . " + e.toString());
			throw new IllegalStateException("Unable to create ListCacheClient instance.");
		}
	}

	private ListCacheClient() {
		fi = (FilterInterface) ClientUtil.renewRMI("ListCacheFilter");
		mi = (MonitorInterface) ClientUtil.renewRMI("ListCacheMonitor");
	}
	
	public List filter(Query query) {
		List ret = null;
		try {
			ret = fi.filter(query);
		} catch (RemoteException re) {
			log.error("unable to get list from server ." + re.toString());
			fi = (FilterInterface) ClientUtil.renewRMI("ListCacheFilter");
		}
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// ���ݾɰ汾
	////////////////////////////////////////////////////////////////////////////

	public List TimeFilter(int id, int type, int start, int count) {
		List ret = null;
		try {
			ret = fi.TimeFilter(id, type, start, count);
		} catch (RemoteException re) {
			log.error("unable to get time filter list from server ." + re.toString());
			fi = (FilterInterface) ClientUtil.renewRMI("ListCacheFilter");
		}
		return ret;
	}

	public List TimeFilter(int id, int type, int minp, int maxp, int count) {
		List ret = null;
		try {
			ret = fi.TimeFilter(id, type, minp, maxp, count);
		} catch (RemoteException re) {
			log.error("unable to get time filter list from server ." + re.toString());
			fi = (FilterInterface) ClientUtil.renewRMI("ListCacheFilter");
		}
		return ret;
	}

	public List PrioFilter(int id, int type, int minp, int maxp, int count) {
		List ret = null;
		try {
			ret = fi.PrioFilter(id, type, minp, maxp, count);
		} catch (RemoteException re) {
			log.error("unable to get prio filter list from server ." + re.toString());
			fi = (FilterInterface) ClientUtil.renewRMI("ListCacheFilter");
		}
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////////////

	public Properties sysInfo() throws CacheException{
		Properties info = new Properties();
		try{
			info = mi.sysInfo();
		}catch(RemoteException e){
			log.error("unable to get system info from server ." + e.toString());
			mi = (MonitorInterface) ClientUtil.renewRMI("ListCacheMonitor");
		}
		return info;
	}

	public List resetList(int pid, int type) throws CacheException{
		List list = null;
		try{
			list = mi.resetList(pid, type);
		}catch(RemoteException e){
			log.error("unable to reset list from server ." + e.toString());
			mi = (MonitorInterface) ClientUtil.renewRMI("ListCacheMonitor");
		}
		return list;
	}
	
}
