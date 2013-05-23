package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Channel;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-31
 * Time: 下午11:28
 * To change this template use File | Settings | File Templates.
 */
@RegisterMapper(ChannelMapper.class)
public interface ChannelDao {

    @SqlUpdate("insert into channel (name, dir) values (:name, :dir)")
    public void insertBean(@BindBean Channel channel);

    @SqlQuery("select id, name, dir from channel where dir = :dir")
    @MapResultAsBean
    public Channel findByName(@Bind("dir") String dir);

    @SqlQuery("select id, name, dir from channel where id = :id")
    @MapResultAsBean
    public Channel getById(@Bind("id") long id);

    @SqlQuery("select id, name, dir from channel where id > :id order by id asc")
    public List<Channel> query(@Bind("id") long id);

    @SqlUpdate("delete from channel where id = :id")
    public void delete(@Bind("id") long id);

    @SqlUpdate("update channel set name = :name, dir = :dir where id = :id")
    int update(@BindBean Channel s);
}


