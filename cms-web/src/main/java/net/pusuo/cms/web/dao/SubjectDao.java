package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Subject;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午9:28
 * To change this template use File | Settings | File Templates.
 */
@RegisterMapper(SubjectMapper.class)
public interface SubjectDao {

    @SqlUpdate("insert into subject (pid, fullpath, name, desc, ctime, priority, status, channelId, editorId, templateId, bakTemplateList, type) values (:pid, :fullpath, :name, :desc, :ctime, :priority, :status, :channelId, :editorId, :templateId, :bakTemplateList, :type)")
    public void insertBean(@BindBean Subject subject);

    @SqlQuery("select id, pid, fullpath, name, 'desc', ctime, priority, status, channelId, editorId, templateId, bakTemplateList, type from subject where id = :id")
    @MapResultAsBean
    public Subject findById(@Bind("id") int id);


    @SqlQuery("select id, pid, fullpath, name, 'desc', ctime, priority, status, channelId, editorId, templateId, bakTemplateList, type from subject where channelId = :channelId order by id asc")
    public List<Subject> queryByChannel(@Bind("channelId") int channelId);

    @SqlQuery("delete subject where id = :id")
    public int delete(@Bind("id") int id);

    @SqlUpdate("update subject set name = :name, desc = :desc, priority = :priority, status = :status, channelId = :channelId, templateId = :templateId, bakTemplateList = :bakTemplateList  where id = :id")
    int update(@BindBean Subject subject);
}
