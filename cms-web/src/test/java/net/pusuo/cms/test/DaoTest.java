package net.pusuo.cms.test;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.StringMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-1
 * Time: 下午11:28
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class DaoTest {

//    Connection conn = DriverManager.getConnection("proxool.smc-datasource");


	public static void main(String... args) throws SQLException {
		Connection conn = DriverManager.getConnection("proxool.smc-datasource");

		DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test",
				"username",
				"password");
		DBI dbi = new DBI(ds);
		Handle h = dbi.open();
		h.execute("create table something (id int primary key, name varchar(100))");

		h.execute("insert into something (id, name) values (?, ?)", 1, "Brian");

		String name = h.createQuery("select name from something where id = :id")
				.bind("id", 1)
				.map(StringMapper.FIRST)
				.first();
		System.out.println(name);

		h.close();
	}
}
