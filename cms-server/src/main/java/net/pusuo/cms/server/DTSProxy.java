package net.pusuo.cms.server;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DTSProxy extends Remote
{
	public List getList(int count) throws RemoteException;
	public void delete(int count) throws RemoteException;
	
}

