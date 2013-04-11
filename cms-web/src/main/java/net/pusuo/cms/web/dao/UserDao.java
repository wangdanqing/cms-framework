package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.auth.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * data access object
 * User: shijinkui
 * Time: ${date}
 * Generate by Util class
 */
@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlUpdate("insert into channel (name, desc, email, passwd, phone, mobile, address, group) values (:name, :dir)")
    public void insert(@BindBean User user);

    @SqlQuery("select * from channel where id = :id")
    @MapResultAsBean
    public User getById(@Bind("id") long id);

    @SqlQuery("select * from channel where id > :id order by id asc")
    public List<User> query(@Bind("id") long id);

    @SqlQuery("delete channel where id = :id")
    public List<User> delete(@Bind("id") long id);

    @SqlUpdate("update channel set name = :name, desc := desc, email = :email, passwd = :passwd, phone = :phone, mobile = :mobile, address = :address, group = :group where id = :id")
    int update(@BindBean User bean);
}


