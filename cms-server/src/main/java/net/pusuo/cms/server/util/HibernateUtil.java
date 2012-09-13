package net.pusuo.cms.server.util;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;

/**
 * @author Mark
 * @version 4.0
 * @since CMS4.0
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    public static final ThreadLocal sessionContext = new ThreadLocal();

    private Session session = null;
    private int signal = 0;

    static {
        try {
            //Configuration cfg = new Configuration().addClass(com.test.EntityItem.class);
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            throw new RuntimeException("Exception building SessionFactory: " + ex.getMessage(), ex);
        }
    }

    public static Session currentSession() throws HibernateException {
        HibernateUtil hu = (HibernateUtil) sessionContext.get();
        // Open a new Session, if this Thread has none yet
        if (hu == null) {
            hu = new HibernateUtil();
            hu.session = sessionFactory.openSession();
            hu.signal = 0;
            sessionContext.set(hu);
        }
        hu.signal++;
        return hu.session;
    }

    public static void closeSession() throws HibernateException {
        HibernateUtil hu = (HibernateUtil) sessionContext.get();
        if (hu != null) {
            hu.signal--;
            if (hu.signal <= 0) {
                if (hu.session != null) {
                    hu.session.close();
                }
                sessionContext.set(null);
            }
        }
    }
}
