package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.dao.ChannelMappingDao;
import net.pusuo.cms.web.dao.DaoFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-6
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
public class ChannelService {

    private final static DataSource ds = DaoFactory.getChannelDataSource();
    private final static DBI dbi = new DBI(ds);

    public void insert(Channel channel) {
        if (channel == null) {
            return;
        }

        Handle handle = dbi.open();
        try {
            ChannelMappingDao db = handle.attach(ChannelMappingDao.class);
            db.insertBean(channel);
        } finally {
            handle.close();
        }
    }

    public static void main(String... args) {

        Handle handle = dbi.open();
        try {
            ChannelMappingDao db = handle.attach(ChannelMappingDao.class);
            Channel c = new Channel();
            c.setDir("www");
            c.setName("普索");
            db.insertBean(c);

            Channel cc = db.findByName("www");
            System.out.println(cc.getId() + "||" + cc.getDir() + "||" + cc.getName());
        } finally {
            handle.close();
        }
    }

}
