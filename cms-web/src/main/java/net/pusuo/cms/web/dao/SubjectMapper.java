package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Subject;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午9:29
 * To change this template use File | Settings | File Templates.
 */
public class SubjectMapper implements ResultSetMapper<Subject> {
    public Subject map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Subject c = new Subject();
        c.setId(r.getInt("id"));
        c.setPid(r.getInt("pid"));
        c.setFullpath(r.getString("fullpath"));
        c.setName(r.getString("name"));
        c.setDesc(r.getString("desc"));
        c.setCtime(r.getLong("ctime"));
        c.setPid(r.getInt("priority"));
        c.setStatus(r.getInt("status"));
        c.setChannelId(r.getInt("channelId"));
        c.setStatus(r.getInt("editorId"));
        c.setTemplateId(r.getInt("templateId"));
        c.setBakTemplateList(r.getString("bakTemplateList"));
        c.setType(r.getInt("type"));

        return c;
    }
}
