package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Media;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * 媒体dao
 *
 * @author 玄畅
 * @date 14-3-5 下午10:13
 */
@RegisterMapper(MediaMapper.class)
public interface MediaDao {
	@SqlUpdate("insert into media(`desc`, siteurl, logourl) values (:desc, :siteurl, :logourl)")
	int insertBean(@BindBean Media media);

	@SqlQuery("select * from media where `desc` = :desc")
	@MapResultAsBean
	Media findByName(@Bind("desc") String desc);

	@SqlQuery("select * from media where id = :id")
	@MapResultAsBean
	Media getById(@Bind("id") int id);

	@SqlQuery("select * from media where id > :id order by id desc")
	List<Media> query(@Bind("id") int id);

	@SqlUpdate("delete from media where id = :id")
	int delete(@Bind("id") int id);

	@SqlUpdate("update media set desc = :desc, siteurl = :siteurl, logourl = :logourl where id = :id")
	int update(@BindBean Media media);
}
