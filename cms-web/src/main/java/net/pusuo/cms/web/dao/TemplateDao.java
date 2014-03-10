package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Template;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * template dao
 * User: shijinkui
 * Date: 13-3-31
 * Time: 下午11:28
 */
@RegisterMapper(TemplateMapper.class)
public interface TemplateDao {

	@SqlUpdate("insert into template (name, type, content, createTime, uptime, status, creator) values " +
			"(:name, :type, :content, :createTime, :uptime, :status, :creator)")
	int insertBean(@BindBean Template template);

	@SqlQuery("select * from template where name = :name")
	@MapResultAsBean
	Template getByName(@Bind("name") String name);

	@SqlQuery("select * from template where id = :id")
	@MapResultAsBean
	Template getById(@Bind("id") int id);

	@SqlQuery("select * from template where id > :id order by id desc")
	List<Template> query(@Bind("id") int id);

	@SqlUpdate("delete from template where id = :id")
	int delete(@Bind("id") long id);

	@SqlUpdate("update template set name = :name, type = :type, uptime = :uptime, status = :status," +
			" content = :content where id = :id")
	int update(@BindBean Template template);
}


