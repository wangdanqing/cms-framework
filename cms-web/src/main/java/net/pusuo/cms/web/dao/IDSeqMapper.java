package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.IDSeq;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 玄畅
 * @date 14-3-4 下午10:46
 */
public class IDSeqMapper implements ResultSetMapper<IDSeq> {

	@Override
	public IDSeq map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		IDSeq seq = new IDSeq(r.getString("group"), r.getLong("id"));

		return seq;
	}
}
