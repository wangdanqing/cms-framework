package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Media;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.MediaDao;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

/**
 * 媒体service类
 *
 * @author 玄畅
 * @date 14-3-5 下午10:24
 */
public class MediaService implements IService<Media> {
	private final DBI dbi = DaoFactory.getBaseDBI();

	public boolean insert(Media media) {
		if (media == null) {
			return false;
		}

		Handle handle = dbi.open();
		try {
			MediaDao db = handle.attach(MediaDao.class);
			return db.insertBean(media) > 0;
		} finally {
			handle.close();
		}
	}

	public Media findByName(String desc) {
		if (desc == null) {
			return null;
		}

		MediaDao dao = dbi.onDemand(MediaDao.class);
		return dao.findByName(desc);
	}


	public Media getById(int id) {
		if (id < 0) {
			return null;
		}

		MediaDao dao = dbi.onDemand(MediaDao.class);
		return dao.getById(id);
	}

	public boolean delete(int id) {
		if (id < 0) {
			return false;
		}

		MediaDao dao = dbi.onDemand(MediaDao.class);
		return dao.delete(id) > 0;
	}


	public List<Media> query(int id) {
		if (id < 0) {
			return null;
		}

		MediaDao dao = dbi.onDemand(MediaDao.class);

		return dao.query(id);
	}

	public boolean update(Media media) {
		MediaDao dao = dbi.onDemand(MediaDao.class);
		int ret = dao.update(media);

		return ret > 0;
	}
}
