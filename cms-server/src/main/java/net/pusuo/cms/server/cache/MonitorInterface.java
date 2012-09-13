/*
 * 
 * @author chenqj
 * Created on 2004-10-27
 *
 */
package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.cache.exception.CacheException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

/**
 * @author chenqj
 *
 */
public interface MonitorInterface extends Remote {
	public Properties sysInfo() throws RemoteException, CacheException;

	public List resetList(int pid, int type) throws RemoteException, CacheException;
}