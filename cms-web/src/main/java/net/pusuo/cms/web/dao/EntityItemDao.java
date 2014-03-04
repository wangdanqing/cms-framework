package net.pusuo.cms.web.dao;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.pusuo.cms.core.bean.EntityItem;
import net.pusuo.cms.core.bean.IDSeq;
import net.pusuo.cms.web.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * 新闻实体Dao
 * <p/>
 * User: shijinkui
 * Date: 13-3-19
 * Time: 上午12:35
 */
@RegisterMapper(EntityItemMapper.class)
public interface EntityItemDao {
	@SqlUpdate("insert into entity_item (id, pid, title,subhead, content, ctime, uptime, priority, status, channelId, mediaId, author,editor, dutyEditor, category, shortName,reurl) values " +
			"(:id, :pid, :title, :subhead, :content, :ctime, :uptime, :priority, :status, :channelId, :mediaId, :author, :editor, :dutyEditor, :category, :shortName, :reurl)")
	public void insertBean(@BindBean EntityItem entity);

	@SqlQuery("select * from entity_item where id = :id")
	@MapResultAsBean
	public EntityItem getById(@Bind("id") long id);

	@SqlQuery("select * from entity_item where id > :id order by id desc")
	public List<EntityItem> query(@Bind("id") long id);

	@SqlUpdate("delete from entity_item where id = :id")
	public void delete(@Bind("id") long id);

	@SqlUpdate("update title, subhead, content, uptime, priority, status, channelId, mediaId, author, dutyEditor, category, shortName, reurl" +
			" tags, keyword, pictures set title= :title, subhead = :subhead, uptime = :uptime, priority = :priority, status = :status," +
			" channelId = :channelId, mediaId = :mediaId, author = :author, dutyEditor = :dutyEditor, content = :content, category = :category, " +
			" shortName = :shortName, tags = :tags, keyword = :keyword, pictures = :pictures where id = :id")
	int update(@BindBean EntityItem entityItem);
}