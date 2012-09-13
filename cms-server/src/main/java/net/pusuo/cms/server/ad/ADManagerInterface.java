package net.pusuo.cms.server.ad;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ADManagerInterface extends Remote
{
	public void load() throws RemoteException;

	public StringBuffer list() throws RemoteException;

	public boolean append(int fragId, int entityId) throws RemoteException;

	public boolean delete(int fragId, int entityId) throws RemoteException;

	public boolean belong(int fragId, int entityId) throws RemoteException;

}


