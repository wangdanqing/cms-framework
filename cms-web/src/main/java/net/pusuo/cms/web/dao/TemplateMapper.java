package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Template;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplateMapper implements ResultSetMapper<Template> {
	public Template map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		Template t = new Template();
		t.setId(r.getInt("id"));
		t.setName(r.getString("name"));
		t.setType(r.getInt("type"));

		Blob content = r.getBlob("content");
		if (content != null) {
			t.setContent(new String(content.getBytes(1, (int) content.length())));
		}
		t.setCreateTime(r.getTimestamp("createTime"));
		t.setUptime(r.getTimestamp("uptime"));
		t.setStatus(r.getInt("status"));
		t.setCreator(r.getInt("creator"));

		return t;
	}
}
