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
public class TemplateService {

	private final DBI dbi = DaoFactory.getTemplateDBI();

	public void insert(Template template) {
		if (template == null) {
			return;
		}

		Handle handle = dbi.open();
		try {
			TemplateDao db = handle.attach(TemplateDao.class);
			db.insertBean(template);
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

	public void delete(int id) {
		if (id < 0) {
			return;
		}

		TemplateDao dao = dbi.onDemand(TemplateDao.class);
		dao.delete(id);
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
