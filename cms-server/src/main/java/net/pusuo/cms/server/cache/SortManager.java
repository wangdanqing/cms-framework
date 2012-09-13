package net.pusuo.cms.server.cache;

import java.util.Map;

import net.sf.hibernate.Session;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Item;
import com.hexun.cms.cache.exception.CacheException;
import com.hexun.cms.cache.util.CacheLRUMap;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.util.HibernateUtil;

public class SortManager {
	private static SortManager sm;

	private final Map items;

	private static Log log = LogFactory.getLog(SortManager.class);

	public static SortManager getInstance() throws CacheException {
		if (sm == null) {
			synchronized (SortManager.class) {
				if (sm == null) {
					sm = new SortManager(CacheConfig.getInstance().getInt(
							"cache.manager.sort.maxsize"));
				}
			}
		}
		return sm;
	}

	
	SortManager() {
		items = new CacheLRUMap();
	}

	SortManager(int max) {
		items = new CacheLRUMap(max);
	}

	public Item get(int id) throws CacheException {
		Integer key = new Integer(id);
		return get(key);
	}

	public Item get(Integer key) throws CacheException {
		Object ret = items.get(key);
		if (ret == null) {
			try {
				Session session = HibernateUtil.currentSession();
				long timeStart = System.currentTimeMillis();
				ret = session.get(CmsSortItem.class, key);
				long timeEnd = System.currentTimeMillis();
				log.info("SortManager-get,the cost is: " + (timeEnd - timeStart) + ",key:" + key);
				if (ret != null) {					
					items.put(key, ret);
				} else {
					log.error("can't load item key=" + key);
					throw new CacheException("can't load item key=" + key);
				}
			} catch (Exception e) {
				log.error("load CmsSortItem[" + key + "] error. " + e);
				throw new CacheException(e);
			} finally {
				try {
					HibernateUtil.closeSession();
				} catch (Exception ex) {
					log.error("can't close session. " + ex);
				}
			}
		}
		return (Item) ret;
	}

	/**
	 * @param item
	 * @return
	 */
	public Item update(EntityItem item) throws CacheException {
		Integer key = new Integer(item.getId());
		CmsSortItem csitem = (CmsSortItem) items.get(key);
		if (csitem != null) {
			try {
				PropertyUtils.copyProperties(csitem, item);
			} catch (Exception e) {
				log.error("copyproperties error " + e);
				throw new CacheException("copyproperties error " + e);
			} 
		} else {
			try {
				Session session = HibernateUtil.currentSession();
				csitem = (CmsSortItem) session.get(CmsSortItem.class, key);
				if (csitem == null)
					throw new CacheException("can't load item key=" + key);
				items.put(key, csitem);
			} catch (Exception e) {
				log.error("can't load item key=" + key);
				throw new CacheException(e);
			} finally {
				try {
					HibernateUtil.closeSession();
				} catch (Exception ex) {
					log.error("can't close session. " + ex);
				}
			}
		}
		return csitem;
	}

	/**
	 * @param id
	 * @param item
	 */
	public void put(int id, Object value) {
		items.put(new Integer(id), value);
	}

	/**
	 * @param id
	 * @param item
	 */
	public void put(Object id, Object value) {
		items.put(id, value);
	}

	/**
	 * @return
	 */
	public int size() {
		return items.size();
	}

}
