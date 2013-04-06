package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Channel;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.springframework.context.annotation.Bean;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-31
 * Time: 下午11:28
 * To change this template use File | Settings | File Templates.
 */
public interface ChannelMappingDao {

    @SqlUpdate("insert into channel (name, dir) values (:name, :dir)")
    public void insertBean(@BindBean Channel channel);

    @SqlQuery("select id, name, dir from channel where dir = :dir")
    @MapResultAsBean
    public Channel findByName(@Bind("dir") String dir);
}
