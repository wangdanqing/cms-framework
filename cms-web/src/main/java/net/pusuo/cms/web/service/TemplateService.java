package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Template;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.TemplateDao;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

/**
 * template service
 * User: shijinkui
 * Date: 14-2-24
 * Time: 下午10:38
 */
public class TemplateService implements IService<Template> {

	private final DBI dbi = DaoFactory.getTemplateDBI();

	public boolean insert(Template template) {
		if (template == null) {
			return false;
		}

		Handle handle = dbi.open();
		try {
			TemplateDao db = handle.attach(TemplateDao.class);
			return db.insertBean(template) > 0;
		} finally {
			handle.close();
		}
	}

	public Template getById(int id) {
		if (id < 0) {
			return null;
		}

		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		return dao.getById(id);
	}

	public Template getByName(String name) {
		if (name == null) {
			return null;
		}

		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		return dao.getByName(name);
	}

	public boolean delete(int id) {
		if (id < 0) {
			return false;
		}

		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		return dao.delete(id) > 0;
	}


	public List<Template> query(int id) {
		if (id < 0) {
			return null;
		}

		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		return dao.query(id);
	}

	public boolean update(Template template) {
		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		int ret = dao.update(template);

		return ret > 0;
	}
}
