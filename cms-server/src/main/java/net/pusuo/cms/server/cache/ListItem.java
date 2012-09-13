package net.pusuo.cms.server.cache;

import com.hexun.cms.Item;
import com.hexun.cms.core.EntityItem;
import net.pusuo.cms.server.cache.exception.CacheException;
import net.pusuo.cms.server.cache.util.CacheArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class ListItem implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(ListItem.class);
	
	public static final int PRIORITY_LIST_MIN_PRIORITY; 	// 加入到权重队列的最小权重
	public static final int PRIORITY_LIST_MAX_SIZE;
	public static final int PRIORITY_LIST_INIT_SIZE;
	public static final int PRIORITY_LIST_EVICT_PERCENT;

	private int id;
	private int type;
	private int subtype;

	private long lastAccess;
	private long accessCount;

	// bx redefine data at 20040428 , data will hold integer object (id)
	private CacheArrayList data;
	private CacheArrayList priorityData; // 保存高优先级的队列,一般来讲保存4-5类的实体

	//这里需要用到对象指针，使得SortItem更新时能同步更新
	// 所以不能使用CacheArrayList(FastArrayList)，这个东东是对值的clone而非指针
	private ArrayList objectData = new ArrayList();

	private boolean updateFlag = true;
	
	static{
		CacheConfig instance = null;
		try {
			instance = CacheConfig.getInstance();
		} catch (Exception e) {
			// instance is null...
		}
		PRIORITY_LIST_MIN_PRIORITY = instance.getInt("cache.object.list.priority.minp", 80);
		PRIORITY_LIST_INIT_SIZE = instance.getInt("cache.object.list.priority.initsize", 100);
		PRIORITY_LIST_MAX_SIZE = instance.getInt("cache.object.list.priority.maxsize", 300);
		PRIORITY_LIST_EVICT_PERCENT = instance.getInt("cache.object.list.priority.evict.percent", 10);
	}

	ListItem() {
	}
	
	ListItem(int id, int type, int subtype, CacheArrayList data, CacheArrayList prioData){
		accessCount = 1;
		lastAccess = System.currentTimeMillis();
		this.id = id;
		this.type = type;
		this.subtype = subtype;
		this.data = data;
		if(prioData == null){			
			this.priorityData = initalPrioList();
		}else{
			this.priorityData = prioData;
		}
	}
	
	ListItem(int id, int type, int subtype, CacheArrayList data) {
		this(id, type, subtype, data, null);
	}

	// Commented by Alfred.Yuan
	// For priority queue in ListCache, we must pass parameter with type of EntityItem, 
	// and this can reduce number of invoked-method(SortManager.get(Integer)).
	// Now, CmsSortItem retains the characteristic of lazy-load.
	public boolean insert(EntityItem item) throws CacheException {
		
		if (item == null)
			return false;
		
		Integer id = new Integer(item.getId());
		
		try{
			if(this.priorityData.contains(id) && item.getPriority() < PRIORITY_LIST_MIN_PRIORITY){
				if(this.priorityData.remove(id)){
					updateFlag = true;
				}					
			}else if(item.getPriority() >= PRIORITY_LIST_MIN_PRIORITY ){
				if(this.priorityData.add(id)){
					updateFlag = true;
				}				
			}
		}catch(Throwable te){
			if(log.isErrorEnabled()){
				log.error("Insert priority list error.",te);
			}
		}
		
		// because of CacheArrayList's add function have verify		
		if (data.add(id)) {			
			updateFlag = true;
			return true;
		} else {
			return false;
		}
	}

	public boolean remove(int id) throws CacheException {
		if(this.priorityData.remove(new Integer(id))){
			if(log.isWarnEnabled()){
				log.warn("remove Entity["+id+"] from priorityData.");
			}
			updateFlag = true;
		}
		
		if (data.remove(new Integer(id))) {
			updateFlag = true;
			return true;
		} else {
			return false;
		}
	} 
	
	/**
	 * 重置权重队列
	 * @param list
	 */
	public void resetPrioData(List list){
		if(list == null){
			return;
		}
		if(log.isWarnEnabled()){
			log.warn("reset PrioData for:"+this.id+" list size:"+list.size());
		}
		CacheArrayList _prioList = this.initalPrioList();
		for(int i=0;i<list.size();i++){
			CmsSortItem item = (CmsSortItem)list.get(i);
			if(item.getPriority() >= PRIORITY_LIST_MIN_PRIORITY){
				//只有大于等最小权重的对象才会被放入到权重队列中
				_prioList.add(new Integer(item.getId()));
			}
		}
		this.priorityData = _prioList;
		this.updateFlag = true;		
	}

	public int length() {
		return data.size();
	}

	public long access() {
		++accessCount;
		lastAccess = System.currentTimeMillis();
		return accessCount;
	}

	public int getID() {
		return id;
	}

	public int getType() {
		return type;
	}
	
	public int getSubtype() {
		return subtype;
	}

	/**
	 * Get integer object list
	 */
	public List getData() {
		//access();
		return this.data;
	}

	/**
	 * Get sort object list
	 * modified by Mark 2004.9.10
	 * remove the lock , use value clone
	 */
	public List getObjectData()
	{
		// 如果同步维护这个对象数组，一定程度上在insert和remove时会形成瓶颈
		// 所以，采用临时取值的方式，并进行无变更cache
		try {
			if (updateFlag) {				
				updateFlag = false;
				
				// 把一般的缓存和高优先级的缓存列表合并起来,并且过滤相同的id号
				List ls = new ArrayList(this.data.size()+this.priorityData.size());
				ls.addAll(this.data);
				for(int i=0,size = this.priorityData.size();i<size;i++){
					Object prioID = this.priorityData.get(i);
					if(!ls.contains(prioID)){
						ls.add(prioID);
					}
				}
				
				long t1 = System.currentTimeMillis();
				
				// id->CmsSortItem
				Item tsi = null;
				int size = ls.size();	
				ArrayList tempData = new ArrayList(size);
				for (int i = 0; i < size; i++) {
					try {						
						tsi = SortManager.getInstance().get((Integer)ls.get(i));
						if (tsi != null) {
							tempData.add(tsi);
						}
					} catch (Exception dd) {
						log.error("ListItem.getObjectData.get error : " + dd);
					}
				}
				
				long t2 = System.currentTimeMillis();
				log.info("GetObjectData "+this.id+" load "+size+" items. Time:"+(t2-t1)+" ms");

				objectData = tempData;
			}
		} catch (Exception e) {
			log.error("ListItem:getObjectData:Exception:" + e);
		}
		return this.objectData;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public void setData(CacheArrayList data) {
		this.data = data;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public long getAccessCount() {
		return accessCount;
	}
	
	private CacheArrayList initalPrioList() {
		return new CacheArrayList(PRIORITY_LIST_INIT_SIZE,
				PRIORITY_LIST_MAX_SIZE,
				PRIORITY_LIST_EVICT_PERCENT);
	}
}
