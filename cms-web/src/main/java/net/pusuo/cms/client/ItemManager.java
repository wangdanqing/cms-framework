package net.pusuo.cms.client;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.Proxy;
import com.hexun.cms.client.book.ContentProcess;
import com.hexun.cms.client.tool.HWClient;
import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.client.util.KeyWordUtil;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.TimeUtils;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.tool.HotWordItem;
import com.hexun.swarmcache.CacheConfiguration;
import com.hexun.swarmcache.CacheConfigurationManager;
import com.hexun.swarmcache.CacheFactory;
import com.hexun.swarmcache.ObjectCache;

/**
 * CMS2客户端统一的Item容器类，内部为每个Item封装了独立的Cache<br>
 * 所有的Item调用通过，三个方法进行get、update、remove
 * 具体使用请参看下面的方法说明
 * 
 * @author     
 * @created    
 */
public class ItemManager {
	private static final Log LOG = LogFactory.getLog(ItemManager.class);

	// locale items cache
	private HashMap caches = null;

	// locale items name->id mapping cache
	private HashMap nameCache = null;

	// locale item all lists class->list cache
	private HashMap itemListCache = null;

	// sigleton instance
	private static ItemManager manager = null;

	// rmi remote interface
	private static Proxy itemproxy = null;

	// load properties of swarmcache.properties
	static Properties pps = initializeDefaults();

	// mutilcasting bind ip
	static String bind_addr = getBindAddr();

	static String mcast_addr = null;

	private static final Object lock = new Object();

	static Properties initializeDefaults() {
		LOG.info("Initializing scache configuration defaults.");
		Properties defaults = new Properties();
		java.io.InputStream is = ItemManager.class.getResourceAsStream("/swarmcache.properties");
		if (is != null) {
			LOG.info("Reading scache configuration from 'swarmcache.properties'...");
			Properties props = new Properties(pps);
			try {
				props.load(is);
			} catch (IOException e) {
				String s = "Unable to load 'swarmcache.properties' due to IOException.";
				LOG.error("Unable to load 'swarmcache.properties' due to IOException.", e);
				throw new IllegalStateException(
						"Unable to load 'swarmcache.properties' due to IOException.");
			}
			pps = props;
		} else {
			LOG.info("Unable to load 'swarmcache.properties'... Using defaults.");
		}
		LOG.info( "swarmcache properties:"+pps );
		return pps;
	}

	static String getBindAddr() {
		String ret = null;
		String tmp = null;
		String ip_prefix = pps.getProperty("swarmcache.ip_prefix", "192.168");
		LOG.info("Initializing udp bind address.");
		try {
			Enumeration enu = NetworkInterface.getNetworkInterfaces();
			while (enu.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) (enu.nextElement());
				Enumeration ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					InetAddress i = (InetAddress) (ias.nextElement());
					tmp = i.getHostAddress();
					if (tmp.startsWith(ip_prefix)) {
						ret = tmp;
						LOG.debug("bind address : " + ret);
						break;
					}
				}
			}
			if (ret == null) {
				throw new Exception("can't bind upd address.");
			}
		} catch (Exception e) {
			LOG.error("Unable to get host address . " + e.toString());
			throw new IllegalStateException("Unable to get host address.");
		}
		return ret;
	}

	/**
	 * 
	 * 1.将初始化一个存放所有Items cache的Map——caches
	 * 2.根据配置文件针对不同的Item初始化不同类型的SwarmCache
	 * 3.绑定remote interface，建立与RMI SERVER的rmi连接
	 * 
	 */
	private ItemManager() throws Exception {
		caches = new HashMap(ItemInfo.ITEM_CLASSES.length);
		nameCache = new HashMap();
		itemListCache = new HashMap();
		for (int i = 0; i < ItemInfo.ITEM_CLASSES.length; i++) {
			caches.put(ItemInfo.ITEM_CLASSES[i], buildCache(ItemInfo.ITEM_CLASSES[i].getName()));
		}
		itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
	}

	/**
	 * 
	 * 对不同的Item Class通过配置文件，产生不同类型的Cache
	 * 
	 */
	private ObjectCache buildCache(String region) throws Exception {
		CacheConfiguration config = CacheConfigurationManager.getConfig(pps);
		if (mcast_addr == null) {
			mcast_addr = pps.getProperty("swarmcache.multicast.ip", "224.1.2.31");
		}
		config.setChannelProperties("UDP(bind_addr="
				+ bind_addr
				+ ";mcast_addr="
				+ mcast_addr
				+ ";mcast_port=45567;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):pbcast.STABLE(desired_avg_gossip=20000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)");

		String cache_type = pps.getProperty(region + ".swarmcache.cache.type");
		if (cache_type != null) {
			LOG.debug("Setting cache type to " + cache_type);
			config.setCacheType(cache_type);
		}

		String lru_size = pps.getProperty(region + ".swarmcache.lru.size");
		if (lru_size != null) {
			LOG.debug("Setting LRU size to " + lru_size);
			config.setLRUCacheSize(lru_size);
		}
		CacheFactory factory = new CacheFactory(config);
		return factory.createCache(region);
	}

	/**
	 * 
	 * 获得单子模式的ItemManager实例
	 * 
	 */
	public static ItemManager getInstance() {
		try {
			if (manager == null) {
				synchronized ( lock ) {
					if (manager == null) {
						manager = new ItemManager();
					}
				}
			}
			return manager;
		} catch (Exception e) {
			LOG.error("Unable to create ItemManager instance . " + e.toString());
			throw new IllegalStateException("Unable to create ItemManager instance.");
		}
	}

	/**
	 * 获得一个Item对象实例
	 * 
	 * @return	返回Item对象实例
	 * @param	key - Item对象的唯一标识ID，必须是可序列化对象
	 * 		theClass - 需要加载对象的Class
	 *		方法必须通过这个Class识别其具体的cacheable class，以便对不同的Item Cache进行操作
	 * 
	 */
	public Item get(Serializable key, Class theClass) {
		Item ret = null;
		ObjectCache cache = null;
		if ((cache = getCache(theClass)) != null) {
			ret = (Item) cache.get(key);
			if (ret == null) {
				// not hit in cache,
				// get item from RMI server.
				ret = getItemFromServer(key, theClass);
				if (ret != null) {
					// just put , don't send a clear signal
					cache.putOnly(key, ret);
				}
			}
		}
		return ret;
	}

	private Item getItemFromServer(Serializable key, Class theClass) {
		Item ret = null;
		try {
			ret = itemproxy.get(key, theClass);
		} catch (RemoteException re) {
			LOG.error("unable to get item from server . " + re.toString());
			itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
		}
		return ret;
	}

	/**
	 * 通过item name获得一个Item对象实例
	 * 
	 * @return	返回Item对象实例
	 * @param	key - Item对象的name
	 * 		theClass - 需要加载对象的Class
	 *		方法必须通过这个Class识别其具体的cacheable class，以便对不同的Item Cache进行操作
	 * 
	 */
	public Item getItemByName(String key, Class theClass) {
		Item ret = null;
		Class rootClass = ItemInfo.getItemClass(theClass);
		String itemName = rootClass.getName() + ":" + key;
		// get item ID from locale cache
		Integer itemID = (Integer) nameCache.get(itemName);
		if (itemID == null) {
			// no hit name cache 
			// get item from RMI Server
			try {
				ret = itemproxy.getItemByName(key, theClass);
				if (ret != null) {
					// set name mapping to locale name cache
					nameCache.put(itemName, new Integer(ret.getId()));
					// set item to locale item cache
					((ObjectCache) caches.get(rootClass)).putOnly(new Integer(ret.getId()), ret);
				}
			} catch (RemoteException re) {
				LOG.error("unable to get item from server . " + re.toString());
				itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
			}
		} else {
			ret = get(itemID, theClass);
		}
		return ret;
	}

	/**
	 *	refresh name cache
	 *	added by wangzhigang 2005.11.18
	 */
	public void refreshItemByName( String key, Class theClass )
	{
		Class rootClass = ItemInfo.getItemClass( theClass );
		String itemNameKey = rootClass.getName()+":"+key;
		nameCache.remove( itemNameKey );
		try
		{
			itemproxy.refreshItemByName( key, theClass );
		}catch( RemoteException e ) {
			LOG.error("unable to refresh name item from server . " + e.toString());
			itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
		}
	}
	/**
	 * 更新一个Item对象实例
	 * 
	 * @return	返回Item对象实例
	 * @param	value - 一个待更新Item对象实例
	 *		方法通过这个实例识别其具体的cacheable class，以便对不同的Item Cache进行操作
	 * 
	 */
	public Item update(Item value) {
		long start =TimeUtils.currentTimeMillis();
		boolean isUpdate = false;
		
		ObjectCache cache = null;
		if ((cache = getCache(value)) != null) {
			Integer key = null;
			if (value.getId() > 0) {
				key = new Integer(value.getId());
				isUpdate = true;
			}

			// save/update item to RMI server.
			try {
				if (key == null) {
					//新闻热词和提取关键字处理
					if(value instanceof News){
						Date t1 = new Date();
						value = hotWord(value,-1);
						Date t2 = new Date();
						LOG.info("hotWordReplace time:"+(t2.getTime()-t1.getTime()));
						value = keyWord(value);
						value = searchRelativenewsC(value,8);
					}
					// create item
					value = itemproxy.save((Item) value);
					key = new Integer(value.getId());
				}else {
					//暂时用
					//if(value instanceof News){
					//	 ((News)value).setText(ContentProcess.process(((News)value).getText()));//process Garbage content
					//}

					// update item
					//value = keyWord(value);
					value = itemproxy.update((Item) value);
				}
				
				long p1=TimeUtils.currentTimeMillis();
				if (isUpdate && value != null && value instanceof EntityItem) {
					EntityItem entity = (EntityItem)value;
					if (entity.getType() == ItemInfo.HOMEPAGE_TYPE 
							|| entity.getType() == ItemInfo.SUBJECT_TYPE) {
						LOG.debug("ItemManager-update: to update. (id=" 
								+ entity.getId() + ")(cost=" + (p1 - start) + ")");
					}
				}
				
				if (value != null) {
					removeItemListCache(value.getClass());

					if(key != null) {
						// clear local cache and send a clear sign
						cache.clear(key);
					
						// put a new value to local cache
						cache.put(key, value);
						
						// update relative objects
						Class clazz = value.getClass();
						Method[] methods = clazz.getMethods();
						for (int i = 0; i < methods.length; i++) {
							if (methods[i].getReturnType().isAssignableFrom(Set.class)) {
								Set items = (Set) methods[i].invoke(value, new Object[0]);
								Iterator iterator = items.iterator();
								while (iterator.hasNext()) {
									refreshItemCache((Item)iterator.next());
								}
							}
						}
				   	   //refresh commonfrag,channel and etc little used object cache added by shijinkui 08.10.20
						//if(!clazz.isAssignableFrom(EntityItem.class))
						//{
						//	refreshItemCache(value);
						//	LOG.info("::refresh cache sjk ::" + value.getClass());
						//}


					}
				}
				
				long p2=TimeUtils.currentTimeMillis();
				if (isUpdate && value != null && value instanceof EntityItem) {
					EntityItem entity = (EntityItem)value;
					if (entity.getType() == ItemInfo.HOMEPAGE_TYPE 
							|| entity.getType() == ItemInfo.SUBJECT_TYPE) {
						LOG.debug("ItemManager-update: to refreshItemCache. (id=" 
								+ entity.getId() + ")(cost=" + (p2 - p1) + ")");
					}
				}				
			}
			catch (Exception re) {
				LOG.error("unable to update item from server . " + re.toString());
				itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
			}
		}
		return value;
	}

	/**
	 * 删除一个Item对象实例
	 * 
	 * @return	返回Item对象实例
	 * @param	value - 一个待删除Item对象实例
	 *		方法通过这个实例识别其具体的cacheable class，以便对不同的Item Cache进行操作
	 * 
	 */
	public Item remove(Item value) {
		ObjectCache cache = null;
		if ((cache = getCache(value)) != null) {
			Integer key = new Integer(value.getId());
			// remove item to RMI server.
			try {
				value = itemproxy.delete(value);
				// reset the locale item list cache
				removeItemListCache(value.getClass());
			} catch (RemoteException re) {
				LOG.error("unable to delete item from server . " + re.toString());
				itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
			}

			// check item remove from locale cache
			if (cache.get(key) != null) {
				System.out.println("item remove without clear cache");
				cache.clear(key);
			}
		}
		return value;
	}

	private ObjectCache getCache(Object obj) {
		return getCache(obj.getClass());

	}

	private ObjectCache getCache(Class theClass) {
		Object cache = null;
		cache = caches.get(ItemInfo.getItemClass(theClass));
		return (ObjectCache) cache;

	}

	List getListFromServer(Class itemClass, int first, int count) {
		List ret = new ArrayList();
		try {
			List ids = itemproxy.getIDList(itemClass, first, count);
			if (ids != null) {
				Iterator itor = ids.iterator();
				while (itor.hasNext()) {
					try {
						Integer id = (Integer) itor.next();
						ret.add(get(id, itemClass));
					} catch (NumberFormatException nfe) {
						LOG.error("ItemManager getListFromServer parse item id error . "
								+ nfe.toString());
						continue;
					}
				}
			}
		} catch (RemoteException re) {
			LOG.error("unable to get item list from server . " + re.toString());
			itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
		}
		return ret;
	}

	public List getList(Class itemClass) {
		// don't load all entityitem
		if (ItemInfo.isEntity(itemClass))
			return null;

		Object ret = null;
		try {
			ret = getItemListCache(itemClass);
			if (ret == null) {
				// add sync by Mark 2004.10.13
				synchronized (itemClass) {
					if ((ret = getItemListCache(itemClass)) == null) {
						ret = loadItemListCache(itemClass);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("get item[" + itemClass.getName() + "] list error. " + e);
		}
		return (List) ret;
	}

	// encapsulate itemListCache get , remove , set(load) method
	private Object getItemListCache(Class itemClass) {
		return itemListCache.get(ItemInfo.getItemClass(itemClass));
	}

	private Object removeItemListCache(Class itemClass) {
		return itemListCache.remove(ItemInfo.getItemClass(itemClass));
	}

	private Object loadItemListCache(Class itemClass) {
		Object ret = null;
		Class rootClass = ItemInfo.getItemClass(itemClass);

		// get all items list
		ret = getListFromServer(rootClass, -1, -1);
		if (ret != null) {
			itemListCache.put(rootClass, ret);
		}
		return ret;
	}

	public List getList(String sql, Collection values, int first, int count) {
		List ret = null;
		try {
			ret = itemproxy.getList(sql, values, first, count);
		} catch (RemoteException re) {
			LOG.error("unable to get item list from server . " + re.toString());
			itemproxy = (Proxy) ClientUtil.renewRMI("ItemProxy");
		}
		return ret;
	}

	public void refreshItemListCache(Class itemClass) {
		removeItemListCache(itemClass);
		loadItemListCache(itemClass);
	}

	public void refreshItemCache(Item item) {
		Class rootClass = ItemInfo.getItemClass(item.getClass());
		Integer key = new Integer(item.getId());
		Item renewItem = getItemFromServer(key, rootClass);
		((ObjectCache) caches.get(rootClass)).putOnly(key, renewItem);
	}

	// for test added 
	public Item getRealItem(Serializable key, Class theClass) {
		return getItemFromServer(key, theClass);
	}

	/**
	 * @param item
	 * @param channelID
	 * @return Item
	 */
	private Item hotWord(Item item, int channelID) {

		String newsContent = ((News)item).getText();
		int pid = ((News)item).getPid();
		Subject subject = (Subject)ItemManager.getInstance().get(new Integer(pid), Subject.class);
		String category = subject.getCategory();
		int real_channelID = ((News)item).getChannel();
		boolean is_hkstock = false;
		if(category.indexOf("100235854")>-1){//判断是否是港股下的文章
			is_hkstock = true;
		}
		List local_hwList = (List)HWClient.getInstance().list(real_channelID);//本频道热词
		List hwList = (List)HWClient.getInstance().list(channelID);//公共热词
		if(is_hkstock){
			hwList = (List)joinHotWord(local_hwList,hwList);
		}else{
			hwList = (List)joinHotWord(hwList,local_hwList);
		}
		if(hwList!=null&&hwList.size()>0){
			if( newsContent!=null&&!newsContent.equals("") ){
				if( ((News)item).getChannel()!=127 ){//千股宝典频道不处理
					newsContent = HWClient.getInstance().hotWordReplace(newsContent,hwList,false);
					((News)item).setText(newsContent);
				}
			}
		}

		return item;
	}
	/**
	 * 把频道热词追加到公共热词中
	 * @param global
	 * @param channelhot
	 * @return
	 */
	private Collection joinHotWord(Collection global,Collection channelhot){
		if(channelhot!=null){
			Iterator it = channelhot.iterator();
			while(it.hasNext()){
				HotWordItem hw = (HotWordItem)it.next();
				global.add(hw);
			}
		}
		return global;
	}
	/**
	 * @param item
	 * @return Item
	 */
	private Item keyWord(Item item) {
		String newsKeyword = ((News)item).getKeyword();
		if (newsKeyword == null || newsKeyword.trim().length() == 0) {
			String newsDesc = ((News)item).getDesc();
			String newsText = com.hexun.cms.util.Util.RemoveHTML(((News)item).getText());
			//newsKeyword = KeyWordUtil.getKeyword(newsDesc,newsText);
			KeyWordUtil kw = new KeyWordUtil();
			if(newsText.length()>500000) newsText="";
			newsKeyword = kw.getKeywordPoading(newsDesc, newsText);			
			// 将庖丁封装成符合Lucene要求的Analyzer规范
/*
			org.apache.lucene.analysis.Analyzer analyzer = new net.paoding.analysis.analyzer.PaodingAnalyzer();		
			Reader r = new StringReader(newsDesc);
			net.paoding.analysis.analyzer.PaodingTokenizer pt = (net.paoding.analysis.analyzer.PaodingTokenizer) analyzer.tokenStream("", r);
			org.apache.lucene.analysis.Token to;
			try {
				int flag = 0;
				while((to = pt.next())!=null)
				{
					if(to.termText().length() >1 && flag < 3)
					{
						if(newsKeyword == null)newsKeyword="";
						newsKeyword += to.termText() + " ";
						flag++;
					}
				}
			} catch (IOException e) {
				LOG.info("paoding 切词错误: " + e.toString());
				e.printStackTrace();
			}
*/
			
			if(newsKeyword!= null && newsKeyword.trim().length()>0){
				((News)item).setKeyword(newsKeyword);
			}
		}

		return item;
	}

	        /**
         * @param item
         * @param count
         * @return Item
         */
        private Item searchRelativenewsC(Item item, int count) {

                String keyword = ((News)item).getKeyword();
                if( keyword != null && !keyword.equals("") ){
                        String relativenews = ItemUtil.searchRelativenewsC(keyword, ((EntityItem)item).getChannel(), count);
                        if( relativenews != null && !relativenews.equals("") ){
                                ((News)item).setRelativenews(relativenews);
                        }
                }

                return item;
        }
}
