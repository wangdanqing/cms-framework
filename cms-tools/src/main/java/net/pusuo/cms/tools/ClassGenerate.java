package net.pusuo.cms.tools;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-9
 * Time: 下午11:58
 * To change this template use File | Settings | File Templates.
 */
public class ClassGenerate {

	private final static VelocityUtil util = new VelocityUtil();
	private final static String path = "/Users/shijinkui/workerspace/git/shijinkui/cms-framework/cms-web/src/main/java/net/pusuo/cms/web/dao";

	static {
		util.setContextValue("date", new Date());

	}

	public static void main(String... args) {
		generateDaoFile();
	}

	private static void generateDaoFile() {
		util.clearContext();
		String classnanem = "User";

		util.setTemplate("dao");
		util.setContextValue("classname", classnanem);
		util.setContextValue("classnane_var", classnanem.toLowerCase());
		util.setContextValue("sql_insert", "name, desc, email, passwd, phone, mobile, address, group");
		util.setContextValue("sql_update", "name = :name, desc := desc, email = :email, passwd = :passwd, phone = :phone, mobile = :mobile, address = :address, group = :group");


		util.mergeToFile(path + "/" + classnanem + "Dao.java");
	}

	private static void generateDaoMapperFile() {
		util.clearContext();
		String classnanem = "User";

		util.setTemplate("dao");
		util.setContextValue("classname", classnanem);
		util.setContextValue("classnane_var", classnanem.toLowerCase());
		util.setContextValue("sql_insert", "name, desc, email, passwd, phone, mobile, address, group");
		util.setContextValue("sql_update", "name = :name, desc := desc, email = :email, passwd = :passwd, phone = :phone, mobile = :mobile, address = :address, group = :group");


		util.mergeToFile(path + "/" + classnanem + "Mapper.java");
	}
}
