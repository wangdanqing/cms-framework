package net.pusuo.cms.server.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.map.LRUMap;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.ItemProxy;
import com.hexun.cms.cache.exception.CacheException;
import com.hexun.cms.cache.util.CacheLRUMap;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

public class
        ListManager {

	private static Log log = LogFactory.getLog(ListManager.class);
	
	private static ListManager lm;

	private final Map items;

	private static Map LOADLOCKMAP = new HashMap();

	public static ListManager getInstance() throws CacheException {
		if (lm == null) {
			synchronized (ListManager.class) {
				if (lm == null)
					lm = new ListManager(CacheConfig.getInstance().getInt(
							"cache.manager.list.maxsize"));
			}
		}
		return lm;
	}

	private ListManager() {
		//items = new CacheLRUMap();
		items = Collections.synchronizedMap(new LRUMap());
	}

	private ListManager(int max) {
		//items = new CacheLRUMap(max);
		items = Collections.synchronizedMap(new LRUMap(max));
		
	}

	public int size() {
		return items.size();
	}

	public List getItemList(int id, int type, int subtype) throws CacheException {
		ListItem listitem = this.get(id, type, subtype);
		if (listitem == null)
			return null;
		List items = listitem.getObjectData();
		return items;
	}

	//	 this function is applicated to maintain relating cache when updating
	// item(save,delete,update).
	// importing com.hexun.core.EntityItem means that cache unions core.at
	// last the result is hard to use cache absolutely
	public boolean updateListCache(EntityItem item, boolean isCreate) {
		if (item == null)
			return false;

		try {
			int oldpid = item.getOldpid();
			boolean pid_change = (oldpid > 0 && oldpid != item.getPid());

			// 1��ʾ״̬��Ϊ���ӣ�-1��ʾ״̬�����ӱ�Ϊ�����ӣ� ����Ϊ0

			final int ACTION_UNCHANGE = 0;
			final int ACTION_INSERT = 1;
			final int ACTION_REMOVE = -1;

			int action = ACTION_UNCHANGE;
			if (!isCreate) {
				int newstatus = item.getStatus();
				int oldstatus = item.getOldstatus();
				int newPriority = item.getPriority();
				int oldPriority = item.getOldpriority();
				if (oldstatus != EntityItem.ENABLE_STATUS && newstatus == EntityItem.ENABLE_STATUS) {
					action = ACTION_INSERT;
				}
				// Commented by Alfred.Yuan
				// When new priority is not equal to old, we should trigger the INSERT-ACTION
				// becouse of priority queue in ListCache.

				if( pid_change ){
					action = ACTION_INSERT;
				}
				if (newPriority != oldPriority && newstatus == EntityItem.ENABLE_STATUS) {
					action = ACTION_INSERT;
				}
				if (oldstatus == EntityItem.ENABLE_STATUS && newstatus != EntityItem.ENABLE_STATUS) {
					action = ACTION_REMOVE;
				}
			}else{
				if( item.getStatus()==EntityItem.ENABLE_STATUS )
				{
					action = ACTION_INSERT;
				}
			}

			// update sort item of this item
			updateSortItem(item);
			if (pid_change) {
				// remove item from all old parents
				EntityItem oldPItem = (EntityItem)ItemProxy.getInstance()
					.get(new Integer(oldpid), EntityItem.class);
				if (oldPItem != null) {
					List parents = Util.getEntityAllParent(oldPItem);
					parents.add( oldPItem );

					EntityItem pItem = null;
					for (int i = 0; parents != null && i < parents.size(); i++) {
						pItem = (EntityItem) parents.get(i);
						if (pItem == null)
							continue;
						removeItem(pItem.getId(), item);
					}
				}
			}

			if (action != ACTION_UNCHANGE) {
				List parents = Util.getEntityAllParent(item);
				if (parents != null) {
					EntityItem pItem = null;
					for (int i = 0; i < parents.size(); i++) {
						pItem = (EntityItem) parents.get(i);
						// filter self
						if (pItem == null || pItem.getId() == item.getId())
							continue;

						if (action == ACTION_REMOVE) {
							removeItem(pItem.getId(), item);
						} else {
							insertItem(pItem.getId(), item);
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("ListManager.updateListCache(item) error ." + e.toString());
			return false;
		}

		return true;
	}

	void updateSortItem(EntityItem item) throws Exception {
		try {
			SortManager.getInstance().update(item);
		} catch (Exception e) {
			log.error("ListManager.updateSortItem(item) error ." + e.toString());
			throw e;
		}
	}
	
	public void updateItemListPrioData(int id, int type, int subtype, List db_list)  
		throws CacheException{
		ListItem listItem = get(id, type, subtype);
		if(listItem != null){
			listItem.resetPrioData(db_list);
		}
	}

	public List resetList(int id, int type) throws CacheException{
		// remove everything.
		removeAll(id);
		
		// load list of default subtype
		ListItem listItem = loadList(id, type, Query.DEFAULT_SUBTYPE);
		if (listItem == null)
			throw new CacheException("loadList return null");
		List items = listItem.getObjectData();
		return items;
	}
	
	public List resetList(int id, int type, int subtype) throws CacheException{
		
		ListItem listItem = loadList(id, type, subtype);
		if (listItem == null)
			throw new CacheException("loadList return null");
		List items = listItem.getObjectData();
		return items;		
	}
	
	private Object getKey(int id, int type, int subtype) {
		return type + "-" + subtype + "-" + id;
	}

	private ListItem get(int id, int type, int subtype) throws CacheException {
		Object key = getKey(id, type, subtype);
		Object lock = null;
		boolean isLoader = false;
		
		try {
			Object obj = items.get(key);
			if (obj == null) {
				/*
				 * ������û�����,ִ�����²���
				 * 1.���LOADLOCKMAP�Ƿ������ֵ��Ӧ��key,���û������ǵ�һ�μ��أ�
				 * ���һ�������(ʹ�õ�ǰ�̵߳�������Ϊ��,��������־�ڲ鿴���ĸ��߳�ִ�м�������.)
				 * �����������뵽LOADLOCKMAP��ȥ,������isLoader��־,ִ�м���
				 * 2.����ǵ�һ�μ���,���Ѿ����߳��ڼ�������ж���,һֱ�ȴ���Ϊֹ
				 */
				synchronized(LOADLOCKMAP){
					lock = LOADLOCKMAP.get(key);
					if(lock == null){
						lock = new String(Thread.currentThread().toString());
						LOADLOCKMAP.put(key,lock);
						isLoader = true;
					}			
				}
				
				if(!isLoader){
					// ����ǵ�һ�μ���,�ͼ����̼߳��������� 
					synchronized(lock){
						try {
							lock.wait();
						} catch (InterruptedException e) {
							if(log.isErrorEnabled()){
								log.error("lock breaked.",e);
							}
						}
					}
				}
				obj = items.get(key);
				if(obj == null){
					obj = loadList(id, type, subtype);
				}
			}
			return (ListItem) obj;
		} catch (Exception eee) {
			log.error("ListManager.get(id,type) error : ",eee);
			throw new CacheException("ListManager get error" + eee.toString());
		} finally{			
			if(lock != null && isLoader){
				// ����߳��Ǽ����߳�,�����LOADLOCKMAP��ȥ�������,ͬʱ��Ҫ�����������ĵȴ���߳�
				synchronized(LOADLOCKMAP){				
					LOADLOCKMAP.remove(key);
					synchronized(lock){
						lock.notifyAll();
					}
				}
			}
		}
	}
	
	private ListItem loadList(int id, int type, int subtype) {
		Object key = getKey(id, type, subtype);
		try {
			Object obj = CmsListFactory.getInstance().get(id, type, subtype);
			if (obj != null) {
				items.put(key, obj);
			} else {
				log.error("CmsListFactory.get return null!");
			}
			return (ListItem) obj;
		} catch (Exception eee) {
			log.error("ListManager.loadList(id="+id+",type="+type+") error.",eee);
			return null;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 

	// ɾ���id��Ӧ������list
	protected boolean removeAll(int entity_id) {
		try {
			Object key1 = getKey(entity_id, ItemInfo.SUBJECT_TYPE, Query.DEFAULT_SUBTYPE);
			
			// Maybe generalized some time.:)
			Object key2 = getKey(entity_id, ItemInfo.NEWS_TYPE, Query.DEFAULT_SUBTYPE);
			Object key23 = getKey(entity_id, ItemInfo.NEWS_TYPE, Query.NEWS_SUBTYPE_ZUTU);
			Object key24 = getKey(entity_id, ItemInfo.NEWS_TYPE, Query.NEWS_SUBTYPE_VIDEO);
			
			Object key3 = getKey(entity_id, ItemInfo.PICTURE_TYPE, Query.DEFAULT_SUBTYPE);

			items.remove(key1);
			items.remove(key2);
			items.remove(key23);
			items.remove(key24);
			items.remove(key3);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	// ɾ���id��Ӧ������list�е�һ��Ԫ��
	protected boolean removeItem(int parentId, EntityItem child) {
		if (parentId < 0 || child == null)
			return false;
		
		removeChild(parentId, child, Query.DEFAULT_SUBTYPE);
		
		// ......
		if (child.getType() == ItemInfo.NEWS_TYPE) {
			removeChild(parentId, child, Query.NEWS_SUBTYPE_ZUTU);
			removeChild(parentId, child, Query.NEWS_SUBTYPE_VIDEO);
		}
		
		return true;
	}
	
	private boolean removeChild(int parentId, EntityItem child, int subtype) {
		
		try {
			Object key = getKey(parentId, child.getType(), subtype);
			if (items.containsKey(key)) { // �����ж�,������ܵ����ȼ��غ�ɾ��
				ListItem list = get(parentId, child.getType(), subtype);
				if (list != null)
					list.remove(child.getId());
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * �ڸ�id��Ӧ��һ��(������)list�в���һ��Ԫ��
	 * ����:(1)�ڴ��б��в���
	 * 	   (2)�������ͼ����,����Ҫ����ͼ�б��в���
	 *     (3)�������Ƶ����,����Ҫ����Ƶ�б��в���
	 * �ο�Query.setSubtype(...)
	 */
	protected boolean insertItem(int parentId, EntityItem child) {
		if (parentId < 0 || child == null)
			return false;
		
		insertChild(parentId, child, Query.DEFAULT_SUBTYPE);
			
		if (child.getType() == ItemInfo.NEWS_TYPE) {
			int subtype = child.getSubtype();
			
			if (subtype == News.SUBTYPE_ZUTU) {
				insertChild(parentId, child, Query.NEWS_SUBTYPE_ZUTU);
			}
			else if (subtype == News.SUBTYPE_VIDEO) {
				insertChild(parentId, child, Query.NEWS_SUBTYPE_VIDEO);
			}
		}
		
		return true;
	}
	
	private boolean insertChild(int parentId, EntityItem child, int subtype) {
		
		try {
			Object key = getKey(parentId, child.getType(), subtype);
			if (items.containsKey(key)) { // ?
				ListItem list = get(parentId, child.getType(), subtype);
				if (list != null)
					list.insert(child);
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

}
