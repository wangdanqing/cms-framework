package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.auth.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        User c = new User();
        c.setId(r.getInt("id"));
        c.setName(r.getString("name"));
        c.setDesc(r.getString("desc"));
        c.setEmail(r.getString("email"));
        c.setPasswd(r.getString("passwd"));
        c.setPhone(r.getString("phone"));
        c.setMobile(r.getString("mobile"));
        c.setAddress(r.getString("address"));
        c.setGroup(r.getInt("group"));
        return c;
    }
}
