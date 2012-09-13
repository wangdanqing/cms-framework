package net.pusuo.cms.server.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FilterInterface extends Remote {
	
	/**
	 * 查询ListCache中的数据
	 * @param query 查询条件
	 * @return List或者null
	 * @throws RemoteException
	 */
	public List filter(Query query) throws RemoteException;
	
	////////////////////////////////////////////////////////////////////////////
	// 兼容旧版本
	////////////////////////////////////////////////////////////////////////////
	
	public List TimeFilter(int id, int type, int start, int count) throws RemoteException;
	
	public List TimeFilter(int id, int type, int minp, int maxp, int count) throws RemoteException;
	
	public List PrioFilter(int id, int type, int minp, int maxp, int count) throws RemoteException;
	
}