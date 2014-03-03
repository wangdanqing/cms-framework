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
	@SqlUpdate("insert into entity_item (pid, title,subhead, content, ctime, uptime, priority, status, channelId, mediaId, author,editor, dutyEditor, category, shortName,reurl) values " +
			"(:pid, :title, :subhead, :content, :ctime, :uptime, :priority, :status, :channelId, :mediaId, :author, :editor, :dutyEditor, :category, :shortName, :reurl)")
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


class EntityItemMapper implements ResultSetMapper<EntityItem> {

	@Override
	public EntityItem map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		EntityItem i = new EntityItem();
		i.setId(r.getLong("id"));
		i.setTitle(r.getString("title"));
		i.setPid(r.getInt("pid"));
		i.setSubhead(r.getString("subhead"));
		Blob content = r.getBlob("content");
		if (content != null) {
			i.setContent(new String(content.getBytes(1, (int) content.length())));
		}
		i.setCtime(r.getTimestamp("ctime"));
		i.setUptime(r.getTimestamp("uptime"));
		i.setPriority(r.getInt("priority"));
		i.setStatus(r.getInt("status"));
		i.setChannelId(r.getInt("channelId"));
		i.setMediaId(r.getInt("mediaId"));
		i.setAuthor(r.getString("author"));
		i.setEditor(r.getInt("editor"));
		i.setDutyEditor(r.getInt("dutyEditor"));
		i.setUrl(r.getString("url"));
		String category = r.getString("category");
		if (StringUtils.isNotBlank(category)) {
			List<Integer> list = Lists.newArrayList();
			Iterator<String> it = Splitter.on(Constant.CATEGORY_SEP).split(category).iterator();
			while (it.hasNext()) {
				list.add(Integer.parseInt(it.next()));
			}
			i.setCategory(list);
		}
		i.setReurl(r.getString("reurl"));
		i.setShortName(r.getString("shortName"));
		i.setKeyword(r.getString("keyword"));
		i.setTags(r.getString("tags"));

//		i.setPictures(); todo

		return i;
	}
}