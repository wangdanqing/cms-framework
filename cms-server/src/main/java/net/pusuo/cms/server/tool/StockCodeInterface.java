package net.pusuo.cms.server.tool;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StockCodeInterface extends Remote {

	public static final int CODE_KEY = 1;
	public static final int NAME_KEY = 2;

	public static final int TYPE_STOCK = 1;
	public static final int TYPE_FUNDS = 2;

	public void loadAll() throws RemoteException;

	public StockCodeItem getByCode(String code) throws RemoteException;

	public StockCodeItem getByName(String name) throws RemoteException;

	public int size(int type) throws RemoteException;

}
