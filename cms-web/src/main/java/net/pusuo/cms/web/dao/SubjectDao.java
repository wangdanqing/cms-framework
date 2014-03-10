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

	@SqlUpdate("insert into subject (pid, shortName, tags, category, name, `desc`, ctime, uptime, priority, status, channelId, editorId, templateId, type) values (:pid, :shortName, :tags, :category, :name, :`desc`, :ctime, :uptime, :priority, :status, :channelId, :editorId, :templateId, :type)")
	int insertBean(@BindBean Subject subject);

	@SqlQuery("select * from subject where id = :id")
	@MapResultAsBean
	Subject findById(@Bind("id") int id);

	@SqlQuery("select * from subject where id >= :id order by id desc")
	List<Subject> getAll(@Bind("id") int id);

	@SqlQuery("select * from subject where channelId = :channelId order by id desc")
	List<Subject> queryByChannelId(@Bind("channelId") int channelId);

	@SqlQuery("select * from subject where pid = :pid order by id desc")
	List<Subject> queryByPid(@Bind("pid") int pid);

	@SqlQuery("delete from subject where id = :id")
	int delete(@Bind("id") int id);

	@SqlUpdate("update subject set tags = :tags, category = :category, uptime = :uptime, name = :name, `desc` = :desc, priority = :priority, status = :status, channelId = :channelId, templateId = :templateId, bakTemplateList = :bakTemplateList  where id = :id")
	int update(@BindBean Subject subject);
}
