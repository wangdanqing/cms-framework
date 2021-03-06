package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.IDSeq;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

/**
 * id序列Dao
 *
 * @author 玄畅
 * @date: 14-3-2 下午11:39
 */
@RegisterMapper(IDSeqMapper.class)
public interface IDSeqDao {

	@SqlUpdate("insert into id_seq values(:id, :group)")
	int insert(@BindBean IDSeq seq);

	@SqlUpdate("update id_seq set id = :id where `group` = :group")
	int update(@BindBean IDSeq seq);

	@SqlQuery("select * from id_seq where `group` = :group")
	@MapResultAsBean
	IDSeq get(@Bind("group") String group);
}
