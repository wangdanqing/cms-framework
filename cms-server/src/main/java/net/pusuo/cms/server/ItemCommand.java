package net.pusuo.cms.server;

import net.pusuo.cms.server.util.HibernateUtil;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ItemCommand implements Command {

    private static Log log = LogFactory.getLog(ItemCommand.class);

    public Item save(Item item) {
	
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
                //e.printStackTrace();
        } finally {
        	try {
                	HibernateUtil.closeSession();
                } catch ( Exception ex ) {
                        log.error("can't close session. "+ex);
                }
        }
	return ret;
    }

    public Item update(Item item) {
	
	Item ret = null;
        Transaction tx= null;

        try {
        	Session session = HibernateUtil.currentSession();
                tx= session.beginTransaction();
                session.update( item );
                tx.commit();
                ret = item;

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
	return ret;
    
    }

    public Item delete(Item item) {
	Item ret = null;
        Transaction tx= null;
        try {
        	ret = item;
                Session session = HibernateUtil.currentSession();
                tx= session.beginTransaction();
                session.delete( item );
                tx.commit();

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
} 
