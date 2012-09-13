package net.pusuo.cms.server.tool;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class StockCodeProxy extends UnicastRemoteObject implements
		StockCodeInterface {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(StockCodeProxy.class);

	private static StockCodeProxy proxy = null;
	private static String s_path = Configuration.getInstance().get(
			"cms4.stockcode.path");
	private static String f_path = Configuration.getInstance().get(
			"cms4.fundscode.path");

	private static final String MAP_CODE = "codemap";
	private static final String MAP_NAME = "namemap";

	// private static String s_path =
	// "D:\\ssh_download\\10.0.251.123\\s_code.txt";
	// private static String f_path =
	// "D:\\ssh_download\\10.0.251.123\\f_code.txt";

	private Map s_codeItems = null;
	private Map s_nameItems = null;
	private Map f_codeItems = null;
	private Map f_nameItems = null;

	protected StockCodeProxy() throws RemoteException {
		super();
	}

	public static StockCodeProxy getInstance() {
		if (s_path == null || f_path == null)
			throw new NullPointerException(
					"required cms4.stockcode.path & cms4.fundscode.path in configuration file.");
		if (proxy == null)
			try {
				proxy = new StockCodeProxy();
				proxy.loadAll();
			} catch (RemoteException e) {
				log.error("unable to create StockCodeProxy instance."
						+ e.toString());
				throw new IllegalStateException(
						"unable to create StockCodeProxy instance.");
			}
		return proxy;
	}

	public StockCodeItem getByCode(String code) throws RemoteException {
		try {
			if (code.endsWith("s") || code.endsWith("S"))
				return getByCode(code.substring(0, code.length() - 1),
						TYPE_STOCK);
			else if (code.endsWith("f") || code.endsWith("F"))
				return getByCode(code.substring(0, code.length() - 1),
						TYPE_FUNDS);
			else
				return getByCodeRandom(code);
		} catch (Exception e) {
			log.error("unable to get StockCodeItem." + e.toString());
			return null;
		}
	}

	private StockCodeItem getByCode(String code, int type) {
		StockCodeItem item = null;
		switch (type) {
		case TYPE_STOCK:
			item = (StockCodeItem) this.s_codeItems.get(code);
			break;
		case TYPE_FUNDS:
			item = (StockCodeItem) this.f_codeItems.get(code);
			break;
		}
		return item;
	}

	private StockCodeItem getByCodeRandom(String code) {
		StockCodeItem item = null;
		item = getByCode(code, TYPE_STOCK);
		if (item == null)
			item = getByCode(code, TYPE_FUNDS);
		return item;
	}

	public StockCodeItem getByName(String name) throws RemoteException {
		try {
			name = Util.unicodeToGBK(name);
			StockCodeItem item = getByName(name, TYPE_STOCK);
			if (item == null)
				item = getByName(name, TYPE_FUNDS);
			return item;
		} catch (Exception e) {
			log.error("unable to get StockCodeItem." + e.toString());
			return null;
		}
	}

	private StockCodeItem getByName(String name, int type) {
		StockCodeItem item = null;
		switch (type) {
		case TYPE_STOCK:
			item = (StockCodeItem) this.s_nameItems.get(name);
			break;
		case TYPE_FUNDS:
			item = (StockCodeItem) this.f_nameItems.get(name);
			break;
		}
		return item;
	}

	public void loadAll() throws RemoteException {
		Map map = loadItemsText(CODE_KEY, TYPE_STOCK);
		this.s_codeItems = (Map) map.get(MAP_CODE);
		this.s_nameItems = (Map) map.get(MAP_NAME);
		map = loadItemsText(CODE_KEY, TYPE_FUNDS);
		this.f_codeItems = (Map) map.get(MAP_CODE);
		this.f_nameItems = (Map) map.get(MAP_NAME);
	}

	private Map loadItemsText(int keyType, int type) {
		Map items = new HashMap();
		Map code_items = new HashMap();
		Map name_items = new HashMap();

		FileReader fr = null;
		BufferedReader br = null;
		String filepath = null;
		switch (type) {
		case TYPE_STOCK:
			filepath = s_path;
			break;
		case TYPE_FUNDS:
			filepath = f_path;
			break;
		default:
			filepath = s_path;
		}

		try {
			fr = new FileReader(filepath);
			br = new BufferedReader(fr);
			while (true) {
				try {
					String line = br.readLine();
					if (line == null || line.equals(""))
						break;
					String[] array = line.split("\\|");
					if (array.length == 2) {
						String code = array[1].trim();
						code = code.replaceAll("\\s", "");
						String name = array[0].trim();
						name = name.replaceAll("\\s", "");
						StockCodeItem item = new StockCodeItem(code, name, type);
						code_items.put(array[1], item);
						name_items.put(array[0], item);
					}
				} catch (Exception e) {
					log.warn("One Line Not Contains Require Value");
				}
			}
		} catch (Exception e) {
			log.error("Read file: '" + filepath + "' failure" + e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
			}
		}
		items.put(MAP_CODE, code_items);
		items.put(MAP_NAME, name_items);
		return items;
	}

	public int size(int type) throws RemoteException {
		int size = 0;
		switch (type) {
		case TYPE_STOCK:
			size = this.s_codeItems.size();
			break;
		case TYPE_FUNDS:
			size = this.f_codeItems.size();
			break;
		}
		return size;
	}
}
