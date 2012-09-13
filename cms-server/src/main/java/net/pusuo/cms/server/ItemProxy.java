package net.pusuo.cms.server;

import com.hexun.cms.core.EntityItem;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.SearchManager;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.util.HibernateUtil;
import com.hexun.cms.util.SearchUtil;
import com.hexun.cms.util.Util;
import net.pusuo.cms.server.Proxy;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * 实现了Item接口，作为所有Item子类的统一代理
 * @since CMS2.0
 * @see Proxy
 * @version 2.0
 * @author xulin
 */
public class ItemProxy extends UnicastRemoteObject implements Proxy
{
	private static final long serialVersionUID = 1L;

	private static ItemProxy itemproxy = null;

	private static Log log = LogFactory.getLog(ItemProxy.class);
	
	private static HashMap nameCache = new HashMap();

        private ItemProxy()
	throws RemoteException
        {
		for ( int i=0;i<ItemInfo.ITEM_CLASSES.length;i++ )
		{
			// init all Items list except EntityItem
			if ( !ItemInfo.ITEM_CLASSES[i].isAssignableFrom(com.hexun.cms.core.EntityItem.class) )
			{
				initItemCache(ItemInfo.ITEM_CLASSES[i]);
			}
		}
        }

	public static ItemProxy getInstance()
        {
                if( itemproxy==null )
                {
			try
			{
				synchronized ( ItemProxy.class )
				{
					if ( itemproxy==null )
					{
						itemproxy = new ItemProxy();
					}
				}
			}
			catch ( RemoteException re )
			{
				log.error("unable to create ItemProxy instance ."+re.toString());
				throw new IllegalStateException("unable to create ItemProxy instance .");
			}
                }
                return itemproxy;
        }

	private void initItemCache ( Class theClass )
	{
		try
		{
			log.info("init items["+theClass.getName()+"] cache ... ");

			Session session = HibernateUtil.currentSession();
			Query query = session.createQuery("from "+theClass.getName());
			query.list();

			log.info("init items["+theClass.getName()+"] ok . ");
		}
		catch ( Exception e )
		{
			log.error("initItemCache["+theClass.getName()+"] error. "+e);
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
	}

	public Item get(Serializable id , Class theClass) 
	throws RemoteException
	{
		Item ret = null;
		try{
			Session session = HibernateUtil.currentSession();
			ret = (Item)session.get( theClass ,id );
                } catch(Exception e) {
			log.error("load item["+id+"] error. ", e);
			e.printStackTrace();
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
		return ret;
	}

	public Item getItemByName(String name , Class theClass)
	throws RemoteException
	{
		Item ret = null;
		Class rootClass = ItemInfo.getItemClass(theClass);
		String itemName = rootClass.getName()+":"+name;
		Integer itemID = (Integer)nameCache.get(itemName);
		if ( itemID==null )
		{
			try{
				Session session = HibernateUtil.currentSession();
				Query query = session.createQuery("select item.id from "+rootClass.getName()+" item where item.name=?");
				query.setString(0,name);
				itemID = (Integer) query.iterate().next();
				if ( itemID!=null )
				{
					nameCache.put(itemName,itemID);
					ret = get( itemID , theClass );
				}
			} catch(Exception e) {
				log.error("load item["+Util.GBKToUnicode(name)+"] error. "+e);
			} finally {
				try {
					HibernateUtil.closeSession();
				} catch ( Exception ex ) {
					log.error("can't close session. "+ex);
				}
			}
		}
		else 
		{
			ret = get( itemID , theClass );
		}
		return ret;
	}

	/**
	 *	refresh name cache
	 */
	public void refreshItemByName( String name, Class theClass )
	{
		Class rootClass = ItemInfo.getItemClass(theClass);
		String itemName = rootClass.getName()+":"+name;
		nameCache.remove( itemName );
	}

	public Item save(Item item)
	throws RemoteException
	{
		long timeStart = System.currentTimeMillis();
		
		Item ret = null;
		Transaction tx= null;

		try{
			Session session = HibernateUtil.currentSession();
			tx= session.beginTransaction();
			session.save( item );
			tx.commit();
			ret = item;
                } catch(Exception e) {
			try {
				if (tx!=null) tx.rollback();
			} catch ( net.sf.hibernate.HibernateException he ) {
				log.error("save item rollback error. "+he);
			}
			log.error("save item error. "+e);
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
     
        long timeMiddle = System.currentTimeMillis();
        
		// here is a dirty code,for update list cache
		try
		{
			if ( ret instanceof com.hexun.cms.core.EntityItem )
			{
				log.info("list cache save!  #"+ret.getId());
				com.hexun.cms.cache.ListManager.getInstance().updateListCache((com.hexun.cms.core.EntityItem)ret,true);
			}
		} catch ( com.hexun.cms.cache.exception.CacheException ce ) 
		{
			log.warn("update list cache failure. id = "+ret.getId());
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("ItemProxy-save,cost of save item(" + item.getId() + ") is:" + (timeEnd - timeStart) + 
				" (" + (timeMiddle - timeStart) + " : " + (timeEnd - timeMiddle) + ")");
				
		return ret;
	}

	public Item update(Item item)
	throws RemoteException
	{
		long timeStart = System.currentTimeMillis();
		
		Item ret = null;
		Transaction tx= null;

		try {
			Session session = HibernateUtil.currentSession();
			tx= session.beginTransaction();

			session.update( item );
			tx.commit();
			ret = item;
	
			Class clazz = ret.getClass();
                        Method[] methods = clazz.getMethods();
                        for (int i=0; i< methods.length; i++) {
                                if ( methods[i].getReturnType().isAssignableFrom(Set.class) ) {
                                        Set items =  (Set)methods[i].invoke(ret,new Object[0]);
                                        Iterator iterator = items.iterator();
                                        while ( iterator.hasNext() ) {
                                                session.refresh((Item)iterator.next());
                                        }
                                }
                        }
                } catch(Exception e) {
			try {
				if (tx!=null) tx.rollback();
			} catch ( net.sf.hibernate.HibernateException he ) {
				log.error("update item rollback error. "+he);
			}
			log.error("update item["+item.getId()+"] error. "+e);
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
                
        long timeMiddle = System.currentTimeMillis();

		// here is a dirty code,for update list cache
		try
		{
			if ( ret instanceof com.hexun.cms.core.EntityItem )
			{
				log.info("list cache update!  #"+ret.getId());
				com.hexun.cms.cache.ListManager.getInstance().updateListCache((com.hexun.cms.core.EntityItem)ret,false);
			}
		// catch all exceptions
		//} catch ( com.hexun.cms.cache.exception.CacheException ce ) 
		} catch ( Exception ce ) 
		{
			log.error("update list cache failure. id = "+ret.getId(), ce);
		}

		// alse dirty for update lucene index
		try {
			if ( ret instanceof com.hexun.cms.core.EntityItem ) {
			    CmsEntry entry = SearchUtil.item2Entry((EntityItem)ret);
			    SearchManager searchManager = SearchClient.getInstance().getSearchManager();
			    searchManager.updateToIndex(entry);
			}
		} catch (Exception e) {
			log.error("update lucene index failure. id = "+ret.getId());
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("ItemProxy-update,cost of update item(" + item.getId() + ") is:" + (timeEnd - timeStart) + 
				" (" + (timeMiddle - timeStart) + " : " + (timeEnd - timeMiddle) + ")");
		
		return ret;
	}

	public Item delete(Item item)
	throws RemoteException
	{
		Item ret = null;
		Transaction tx= null;
		try {
			ret = item;
			Session session = HibernateUtil.currentSession();
			tx= session.beginTransaction();
			session.delete( item );
			tx.commit();

			Class clazz = ret.getClass();
                        Method[] methods = clazz.getMethods();
                        for (int i=0; i< methods.length; i++) {
                                if ( methods[i].getReturnType().isAssignableFrom(Set.class) ) {
                                        Set items =  (Set)methods[i].invoke(ret,new Object[0]);
                                        Iterator iterator = items.iterator();
                                        while ( iterator.hasNext() ) {
                                                session.refresh((Item)iterator.next());
                                        }
                                }
                        }
                } catch(Exception e) {
			try {
				if (tx!=null) tx.rollback();
			} catch ( net.sf.hibernate.HibernateException he ) {
				log.error("delete item rollback error. "+he);
			}
			log.error("delete item["+item.getId()+"] error. "+e);
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
		return ret;
	}

	public List getIDList(Class itemClass,int first,int count)
	throws RemoteException
	{
		String sql = "select item.id from "+itemClass.getName()+" item";
		List ret = null;
		try {
			ret = getList(sql,null,first,count);
                } catch(Exception e) {
			log.error("get id list query["+sql+"] error. "+e);
                }
		return ret;
	}

	public List getList(String sql,Collection values,int first,int count)
	throws RemoteException
	{
		List ret = null;
		try {
			Session session = HibernateUtil.currentSession();
			Query query = session.createQuery(sql);
			if ( values!=null )
			{
				Iterator iterator = values.iterator();
				// hibernate hql
				// the position of the parameter in the query string, numbered from 0
				int i = 0;
				while ( iterator.hasNext() )
				{
					query.setParameter(i++,iterator.next());
				}
			}
			if ( first>=0 && count>0 )
			{
				query.setFirstResult(first);
				query.setMaxResults(count);
			}
			ret = query.list();
                } catch(Exception e) {
			log.error("get list query["+sql+"] error. "+e);
                } finally {
			try {
				HibernateUtil.closeSession();
			} catch ( Exception ex ) {
				log.error("can't close session. "+ex);
			}
		}
		return ret;
	}
}
