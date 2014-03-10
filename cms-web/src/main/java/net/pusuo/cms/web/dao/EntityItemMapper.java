package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.EntityItem;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 玄畅
 * @date 14-3-4 下午10:47
 */
public class EntityItemMapper implements ResultSetMapper<EntityItem> {

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
		i.setCategory(category);
		i.setReurl(r.getString("reurl"));
		i.setShortName(r.getString("shortName"));
		i.setKeyword(r.getString("keyword"));
		i.setTags(r.getString("tags"));

//		i.setPictures(); todo

		return i;
	}
}