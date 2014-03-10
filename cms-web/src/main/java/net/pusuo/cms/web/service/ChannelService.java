package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.dao.ChannelDao;
import net.pusuo.cms.web.dao.DaoFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-6
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
public class ChannelService implements IService<Channel> {

	private final DBI dbi = DaoFactory.getChannelDBI();

	public boolean insert(Channel channel) {
		if (channel == null) {
			return false;
		}

		Handle handle = dbi.open();
		try {
			ChannelDao db = handle.attach(ChannelDao.class);
			return db.insertBean(channel) > 0;
		} finally {
			handle.close();
		}
	}

	public Channel findByName(String dir) {
		if (dir == null) {
			return null;
		}

		ChannelDao dao = dbi.onDemand(ChannelDao.class);
		return dao.findByName(dir);
	}


	public Channel getById(int id) {
		if (id < 0) {
			return null;
		}

		ChannelDao dao = dbi.onDemand(ChannelDao.class);
		return dao.getById(id);
	}

	public boolean delete(int id) {
		if (id < 0) {
			return false;
		}

		ChannelDao dao = dbi.onDemand(ChannelDao.class);
		return dao.delete(id) > 0;
	}


	public List<Channel> query(int id) {
		if (id < 0) {
			return null;
		}

		ChannelDao dao = dbi.onDemand(ChannelDao.class);

		return dao.query(id);
	}

	public boolean update(Channel channel) {
		ChannelDao dao = dbi.onDemand(ChannelDao.class);
		int ret = dao.update(channel);

		return ret > 0;
	}
}
