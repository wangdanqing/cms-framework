package net.pusuo.cms.server.tool;

import java.util.*;
import java.rmi.*;

public interface HWInterface extends Remote{

  public void loadAll() throws RemoteException;
  
  public Collection load(int chennelID) throws RemoteException;
  
  public HotWordItem get(String keyword, int chennelID) throws RemoteException;

  public boolean add(HotWordItem hwi, int chennelID) throws RemoteException;
  
  public boolean update(HotWordItem ohwi, HotWordItem nhwi, int chennelID) throws RemoteException;
  
  public boolean delete(HotWordItem hwi, int chennelID)throws RemoteException;
  
  public Collection list(int chennelID)throws RemoteException;
  
  public Hashtable listAll()throws RemoteException;
}
