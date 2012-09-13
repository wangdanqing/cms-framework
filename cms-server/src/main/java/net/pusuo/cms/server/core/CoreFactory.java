package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 所有Core包中Item工厂类，用于产生各种 ITEM实例
 *
 * @author Mark
 * @version 4.0
 * @see Configuration
 * @since CMS4.0
 */

public class CoreFactory {
    private static CoreFactory factory = null;

    private static Log log = LogFactory.getLog(CoreFactory.class);

    private CoreFactory() {
    }

    public static CoreFactory getInstance()
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
        factory = new CoreFactory();
    }

    public Object createItem(Class itemClass) {
        Object ret = null;
        try {
            ret = itemClass.newInstance();
        } catch (Exception ie) {
        }
        return ret;
    }

    public TFMap createTFMap() {
        return new TFMap();
    }

    public TypeItem createTypeItem() {
        return new TypeItem();
    }

    public Template createTemplate() {
        return new Template();
    }

    public Frag createFrag() {
        return new Frag();
    }

    public Subject createSubject() {
        return new Subject();
    }

    public News createNews() {
        return new News();
    }

    public Picture createPicture() {
        return new Picture();
    }

    public EntityItem createHomepage() {
        return new EntityItem(EntityItem.HOMEPAGE_TYPE);
    }

    public Media createMedia() {
        return new Media();
    }

    public Channel createChannel() {
        return new Channel();
    }

    public Category createCategory() {
        return new Category();
    }

    public CommonFrag createCommonfrag() {
        return new CommonFrag();
    }

    public ExTFMap createExTFMap() {
        return new ExTFMap();
    }

    public Author createAuthor() {
        return new Author();
    }
}

