package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.SubjectDao;
import org.skife.jdbi.v2.DBI;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午10:33
 * To change this template use File | Settings | File Templates.
 */
public class SubjectService implements IService<Subject> {

	private final DBI dbi = DaoFactory.getSubjectDBI();

	/**
	 * 根据栏目Id，获取所有的子栏目
	 *
	 * @param id 栏目ID
	 *
	 * @return list subject
	 */
	public List<Subject> getSubListById(int id) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);
		List<Subject> list;
		if (id > 0) {
			list = dao.queryByPid(id);
		} else {
			list = dao.getAll(id);
		}

		return list;
	}

	@Override
	public boolean insert(Subject subject) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);
		return dao.insertBean(subject) > 0;
	}

	@Override
	public Subject getById(int id) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);
		return dao.findById(id);
	}

	@Override
	public boolean delete(int id) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);
		return dao.delete(id) > 0;
	}

	@Override
	public List<Subject> query(int channelId) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);

		List<Subject> list;
		if (channelId > -1) {
			list = dao.queryByChannelId(channelId);
		} else {
			list = dao.getAll(channelId);
		}

		return list;
	}

	@Override
	public boolean update(Subject obj) {
		SubjectDao dao = dbi.onDemand(SubjectDao.class);
		return dao.update(obj) > 0;
	}
}
