package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.dao.ChannelDao;
import net.pusuo.cms.web.dao.DaoFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-6
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
public class ChannelService {

    private final DataSource ds = DaoFactory.getChannelDataSource();
    private final DBI dbi = new DBI(ds);


    public void insert(Channel channel) {
        if (channel == null) {
            return;
        }

        Handle handle = dbi.open();
        try {
            ChannelDao db = handle.attach(ChannelDao.class);
            db.insertBean(channel);
        } finally {
            handle.close();
        }
    }

    public Channel get(long id) {
        if (id < 0) {
            return null;
        }

        ChannelDao dao = dbi.onDemand(ChannelDao.class);
        Channel c = dao.getById(id);

        return c;
    }

    public void delete(long id) {
        if (id < 0) {
            return;
        }

        ChannelDao dao = dbi.onDemand(ChannelDao.class);
        dao.delete(id);
    }


    public List<Channel> query(long id) {
        if (id < 0) {
            return null;
        }

        ChannelDao dao = dbi.onDemand(ChannelDao.class);
        List<Channel> list = dao.query(id);

        return list;
    }

    public boolean update(Channel channel) {
        ChannelDao dao = dbi.onDemand(ChannelDao.class);
        int ret = dao.update(channel);

        return ret > 0;
    }

    public static void main(String... args) {
        DataSource ds = DaoFactory.getChannelDataSource();
        final DBI dbi = new DBI(ds);
        Handle handle = dbi.open();
        try {
            ChannelDao db = handle.attach(ChannelDao.class);
            Channel c = new Channel();
            c.setDir("www");
            c.setName("abc");
            db.insertBean(c);

            Channel cc = db.findByName("www");
            System.out.println(cc.getId() + "||" + cc.getDir() + "||" + cc.getName());

            List<Channel> list = db.query(0);
            for (Channel channel : list) {
                System.out.println(channel.getId() + "||" + channel.getDir() + "||" + channel.getName());
            }

        } finally {
            handle.close();
        }
    }

}
