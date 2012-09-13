package net.pusuo.cms.client.tool;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.tool.StockCodeInterface;
import com.hexun.cms.tool.StockCodeItem;

public class StockCodeClient {

	private static StockCodeInterface proxy = null;
	private static StockCodeClient client = null;
	private static final Log log = LogFactory.getLog(StockCodeClient.class);

	public static StockCodeClient getInstance() {
		try {
			if (client == null) {
				synchronized (StockCodeClient.class) {
					if (client == null) {
						client = new StockCodeClient();
					}
				}
			}
			return client;
		} catch (Exception e) {
			log.error("Unable to create StockCodeClient instance . "
					+ e.toString());
			throw new IllegalStateException(
					"Unable to create StockCodeClient instance.");
		}
	}

	private StockCodeClient() {
		proxy = (StockCodeInterface) ClientUtil.renewRMI("StockCode");
	}

	public void loadAll() {
		try {
			proxy.loadAll();
		} catch (RemoteException e) {
			log.error("unable to load all from server ." + e.toString());
		}
	}

	public StockCodeItem getByCode(String code) {
		try {
			return proxy.getByCode(code);
		} catch (RemoteException e) {
			log.error("unable to get by code from server ." + e.toString());
			return null;
		}
	}

	public StockCodeItem getByName(String name) {
		try {
			return proxy.getByName(name);
		} catch (RemoteException e) {
			log.error("unable to get by name from server ." + e.toString());
			return null;
		}
	}

	public int getStockSize() {
		try {
			return proxy.size(StockCodeInterface.TYPE_STOCK);
		} catch (RemoteException e) {
			log.error("unable to get Size from server ." + e.toString());
			return 0;
		}
	}

	public int getFundsSize() {
		try {
			return proxy.size(StockCodeInterface.TYPE_FUNDS);
		} catch (RemoteException e) {
			log.error("unable to get Size from server ." + e.toString());
			return 0;
		}
	}

}
