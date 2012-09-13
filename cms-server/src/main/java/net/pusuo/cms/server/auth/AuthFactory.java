package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 所有AUTH类的工厂类，用于产生各种AUTH ITEM实例
 *
 * @author Mark
 * @version 4.0
 * @since CMS4.0
 */

public class AuthFactory {
    private static AuthFactory factory = null;

    private static Log log = LogFactory.getLog(AuthFactory.class);

    private AuthFactory() {
    }

    public static AuthFactory getInstance()
    //throws Exception
    {
        if (factory == null) {
            try {
                buildFactory(Configuration.getInstance());
            } catch (Exception e) {
                log.error("build factory instance error . " + e);
            }
        }
        return factory;
    }

    /**
     * 一般在Configuration里面调用，可用于动态改变实例的加载过程
     */
    public static void buildFactory(Configuration config) throws Exception {
        factory = new AuthFactory();
    }

    public Object createItem(Class itemClass) {
        Object ret = null;
        try {
            ret = itemClass.newInstance();
        } catch (Exception ie) {
        }
        return ret;
    }

    public User createUser() {
        return new User();
    }

    public Role createRole() {
        return new Role();
    }

    public Realm createRealm() {
        return new Realm();
    }

    public Perm createPerm() {
        return new Perm();
    }

    public Group createGroup() {
        return new Group();
    }
}
