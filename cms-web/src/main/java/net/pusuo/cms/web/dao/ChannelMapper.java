package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Channel;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChannelMapper implements ResultSetMapper<Channel> {
    public Channel map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Channel c = new Channel();
        c.setId(r.getInt("id"));
        c.setName(r.getString("name"));
        c.setDir(r.getString("dir"));
        return c;
    }
}
