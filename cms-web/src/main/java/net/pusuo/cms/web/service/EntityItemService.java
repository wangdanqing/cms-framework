package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.EntityItem;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.EntityItemDao;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

/**
 * @author 玄畅
 * @date: 14-3-2 下午11:11
 */
public class EntityItemService {
	private final DBI dbi = DaoFactory.getEntityItemDBI();

	public boolean insert(EntityItem entity) {
		if (entity == null) {
			return false;
		}

		Handle handle = dbi.open();
		try {
			EntityItemDao db = handle.attach(EntityItemDao.class);
			return db.insertBean(entity) > 0;
		} finally {
			handle.close();
		}
	}

	public EntityItem getById(long id) {
		if (id < 0) {
			return null;
		}

		EntityItemDao dao = dbi.onDemand(EntityItemDao.class);
		return dao.getById(id);
	}

	public boolean delete(long id) {
		if (id < 0) {
			return false;
		}

		EntityItemDao dao = dbi.onDemand(EntityItemDao.class);
		return dao.delete(id) > 0;
	}

	public List<EntityItem> query(long id) {
		if (id < 0) {
			return null;
		}

		EntityItemDao dao = dbi.onDemand(EntityItemDao.class);
		return dao.query(id);
	}

	public boolean update(EntityItem entity) {
		EntityItemDao dao = dbi.onDemand(EntityItemDao.class);
		int ret = dao.update(entity);

		return ret > 0;
	}
}
