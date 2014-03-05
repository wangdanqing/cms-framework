package net.pusuo.cms.web.dao;

import net.pusuo.cms.core.bean.Media;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 玄畅
 * @date 14-3-5 下午10:14
 */
public class MediaMapper implements ResultSetMapper<Media> {
	@Override
	public Media map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		Media m = new Media();
		m.setDesc(r.getString("desc"));
		m.setId(r.getInt("id"));
		m.setLogourl(r.getString("logourl"));
		m.setSiteurl(r.getString("siteurl"));
		return m;
	}
}
