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

    @Override
    public void insert(Subject subject) {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        dao.insertBean(subject);
    }

    @Override
    public Subject getById(int id) {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        return dao.findById(id);
    }

    @Override
    public void delete(int id) {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        dao.delete(id);
    }

    @Override
    public List<Subject> query(int key) {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        return dao.queryByChannel(key);
    }

    public List<Subject> getHomePageList() {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        return dao.queryChannelList();
    }

    @Override
    public boolean update(Subject obj) {
        SubjectDao dao = dbi.onDemand(SubjectDao.class);
        return dao.update(obj) > 0;
    }
}
