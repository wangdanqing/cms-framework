package net.pusuo.cms.web.dao;

import org.skife.jdbi.v2.DBI;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-3
 * Time: 上午9:11
 * To change this template use File | Settings | File Templates.
 */
public class DaoFactory {

	private static ApplicationContext context;

	static {
		context = new ClassPathXmlApplicationContext("dao-beans.xml");
	}

	public static DBI getBaseDBI() {
		DataSource ds = (DataSource) context.getBean("datasource");
		return new DBI(ds);
	}


	public static DBI getChannelDBI() {
		DataSource ds = (DataSource) context.getBean("datasource");
		return new DBI(ds);
	}

	public static DBI getSubjectDBI() {
		DataSource ds = (DataSource) context.getBean("datasource");
		return new DBI(ds);
	}

	public static DBI getTemplateDBI() {
		DataSource ds = (DataSource) context.getBean("datasource");
		return new DBI(ds);
	}

	public static DBI getEntityItemDBI() {
		DataSource ds = (DataSource) context.getBean("datasource");
		return new DBI(ds);
	}
}