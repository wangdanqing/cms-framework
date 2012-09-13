package net.pusuo.cms.server.tool;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 
 * @author denghua
 *
 */
public interface CFInterface extends Remote {

	/**
	 * @deprecated
	 * @param channelName
	 * @return
	 * @throws java.rmi.RemoteException
	 */
  public List list(String channelName) throws RemoteException;

  /**
   * @deprecated
   */
  public void add(String channelName, int id, String desc)  throws RemoteException;

  /**
   * @deprecated
   */
  public void delete(String channelName, int id)  throws RemoteException;

  /**
   * @deprecated
   */
  public void delete(String channelName, List ids) throws RemoteException;
  
  public List listCategory(String channelName) throws RemoteException;
  
  public List list(String channelName, String category) throws RemoteException;
  
  public void add(String channelName, int id, String name, String categoryName) throws RemoteException;
  
  public void addCategory(String channelName, String categoryName) throws RemoteException;
  
  public void delete(String channelName, int id, String categoryName) throws RemoteException;
  
  public void deleteCategory(String channelName, String categoryName) throws RemoteException;

  public void delete(String channelName, List ids, String categoryName) throws RemoteException ;
  
  public void deleteCategory(String channelName, List names) throws RemoteException ;
}
