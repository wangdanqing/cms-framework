package net.pusuo.cms.web.dao;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-3
 * Time: 上午9:11
 * To change this template use File | Settings | File Templates.
 */
public class DaoFactory {

    private static BeanFactory bf;

    static {
        init();
    }

    public static synchronized void init() {
        Resource resource = new ClassPathResource("dao-beans.xml");
        bf = new XmlBeanFactory(resource);
    }

    public static DataSource getChannelDataSource() {
        return (DataSource) bf.getBean("channelDatasource");
    }
}
