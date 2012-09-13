package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.cache.exception.CacheException;
import net.pusuo.cms.server.cache.util.CacheArrayList;
import net.pusuo.cms.server.core.EntityItem;
import net.pusuo.cms.server.util.HibernateUtil;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;
public class 
        CmsListFactory {

	private static Log log = LogFactory.getLog(CmsListFactory.class);

	private static String tquery = "from com.hexun.cms.cache.CmsSortItem csi where csi.category like :cate and csi.type=:type and csi.status = "
			+ EntityItem.ENABLE_STATUS + " order by csi.time desc";
	
	private static String tqueryWithSubtype = "from com.hexun.cms.cache.CmsSortItem csi where csi.category like :cate and csi.type=:type and csi.subtype=:subtype and csi.status = "
			+ EntityItem.ENABLE_STATUS + " order by csi.time desc";
	
	private static String pquery = "from com.hexun.cms.cache.CmsSortItem csi where csi.category like :cate and csi.type=:type and csi.status = "
		+ EntityItem.ENABLE_STATUS + " and csi.priority>=:minp and csi.priority<=:maxp order by csi.time desc";
	
	private static String pqueryWithSubtype = "from com.hexun.cms.cache.CmsSortItem csi where csi.category like :cate and csi.type=:type and csi.subtype=:subtype and csi.status = "
		+ EntityItem.ENABLE_STATUS + " and csi.priority>=:minp and csi.priority<=:maxp order by csi.time desc";
	
	////////////////////////////////////////////////////////////////////////////
	
	private static CmsListFactory lf;
	
	private static int initSize = 500;
	private static int maxSize = 1200;
	private static int evictPercent = 10;

	private CmsListFactory() throws CacheException {
		initSize = CacheConfig.getInstance().getInt("cache.object.list.initsize");
		maxSize = CacheConfig.getInstance().getInt("cache.object.list.maxsize");
		evictPercent = CacheConfig.getInstance().getInt("cache.object.list.evict.percent");
	}

	public static CmsListFactory getInstance() throws CacheException {
		if (lf == null) {
			synchronized (CmsListFactory.class) {
				if (lf == null)
					lf = new CmsListFactory();
			}
		}
		return lf;
	}

	public List HibernateGet(int id, int type, int subtype) throws CacheException {
		
		List list = null;
		
		try {
			CmsSortItem csi = (CmsSortItem) SortManager.getInstance().get(id);
			if (csi == null)
				return null;
			String cate = csi.getCategory();
			if (cate == null || cate.equals("")) {
				return null;
			}
			
			boolean valid = false;
			if (subtype != Query.DEFAULT_SUBTYPE)
				valid = true;
			
			Session session = HibernateUtil.currentSession();
			Query query = session.createQuery(valid ? tqueryWithSubtype : tquery);
			query = query.setString("cate", cate + "%");
			query = query.setInteger("type", type);
			if (valid)
				query.setInteger("subtype", subtype);
			query.setMaxResults(initSize);
			
			long timeStart = System.currentTimeMillis();
			list = query.list();
			long timeEnd = System.currentTimeMillis();
			
			log.info("Time of HibernateGet(id=" + id + ")(type=" + type + ")(subtype=" + subtype + ")(cost=" 
					+ (timeEnd - timeStart) + ")(size=" + (list == null ? 0 : list.size()) + ")");
		} catch (Exception e) {
			log.error("CmsListFactory.HibernateGet error :" + e.toString());
			throw new CacheException(e.getMessage());
		} finally {
			try {
				HibernateUtil.closeSession();
			} catch (Exception ex) {
				log.error("can't close session. " + ex);
			}
		}

		return list;
	}

	public ListItem get(int id, int type, int subtype) throws CacheException {
		ListItem ret = null;
		try {
			List list = HibernateGet(id, type, subtype);
			CacheArrayList idlist =null;
			if(list != null){
				idlist = new CacheArrayList(list.size(), maxSize, evictPercent);
				fillListItem(id, list,idlist);
			}
			ret = new ListItem(id, type, subtype, idlist);
		} catch (Exception e) {
			log.error("CmsListFactory.get error :" + e.toString());
			throw new CacheException(e.getMessage());
		}
		return ret;
	}

	/**
	 * @param id
	 * @param list
	 * @throws CacheException
	 */
	private void fillListItem(int id, List list, CacheArrayList idlist) throws CacheException {		
		if (list != null) {
			SortManager sm = SortManager.getInstance();
			Iterator it = list.iterator();			
			while (it.hasNext()) {
				Item item = (Item) it.next();
				int iid = item.getId();
				sm.put(iid, item);
				if (item.getId() != id && idlist !=null) {
					idlist.add(new Integer(iid));
				}
			}
		}
	}

	// added by Mark 2004.9.13
	// һЩ�����Ȩ�����󣬻���ΪListItem�ĳ��������޷����㷵�ظ���
	// ��ʱ���ǽ�����ݿ�ֱ�ӽ��в�ѯ���ؽ��
	// ���������ֵ���Ӧ�ò��࣬����Ҫ�����˹��������
	public List getByPriority(int id, int type, int subtype, int minp, int maxp, int count) throws CacheException {
		
		if (minp < 0 || maxp < 0 || count < 0 || minp > maxp) {
			return null;
		}
		
		List list = null;
		
		try {
			CmsSortItem csi = (CmsSortItem) SortManager.getInstance().get(id);
			if (csi == null)
				return null;
			String cate = csi.getCategory();
			if (cate == null || cate.equals("")) {
				return null;
			}
			
			boolean valid = false;
			if (subtype != com.hexun.cms.cache.Query.DEFAULT_SUBTYPE)
				valid = true;
			
			Session session = HibernateUtil.currentSession();
			Query query = session.createQuery(valid ? pqueryWithSubtype : pquery);
			query = query.setString("cate", cate + "%");
			query = query.setInteger("type", type);
			query = query.setInteger("minp", minp);
			query = query.setInteger("maxp", maxp);
			if (valid)
				query.setInteger("subtype", subtype);
			query.setMaxResults(count);
			
			long timeStart = System.currentTimeMillis();
			list = query.list();
			long timeEnd = System.currentTimeMillis();
			log.info("Time of GetListByPriority(id=" + id + ")(type=" + type + ")(subtype=" + subtype 
					+ ")(minp=" + minp + ")(maxp=" + maxp + ")(count=" + count + ")(cost=" 
					+ (timeEnd - timeStart) + ")(size=" + (list == null ? 0 : list.size()) + ")");
			
			fillListItem(id,list,null);
		} catch (Exception e) {
			log.error("CmsListFactory.getByPriority error :" , e);
			throw new CacheException(e.getMessage());
		} finally {
			try {
				HibernateUtil.closeSession();
			} catch (Exception ex) {
				log.error("can't close session. " + ex);
			}
		}
		
		return list;
	}
}
