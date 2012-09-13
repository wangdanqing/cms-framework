package net.pusuo.cms.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * 各个代理容器都应实现统一的接口，这是为了便于模块之间的调用，同时对RMI提供统一的BINDING
 * @since CMS4.0
 * @see Item
 * @version 4.0
 * @author Mark
 */
public interface Proxy extends Remote
{
	public Item get(Serializable id , Class theClass) throws RemoteException;
	
	public void refreshItemByName(String name , Class theClass) throws RemoteException;

	public Item getItemByName(String name , Class theClass) throws RemoteException;

	public Item save(Item item) throws RemoteException;

	public Item update(Item item) throws RemoteException;

	public Item delete(Item item) throws RemoteException;

	public List getIDList(Class itemClass,int first,int count) throws RemoteException;

	public List getList(String sql,Collection parameters,int first,int count) throws RemoteException;
}
