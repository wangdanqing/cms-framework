package net.pusuo.cms.web.service;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.core.bean.auth.User;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.UserDao;
import net.pusuo.cms.web.result.UserEnum;
import net.pusuo.cms.web.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.pusuo.cms.web.result.UserEnum.SUCCESS;
import static net.pusuo.cms.web.result.UserEnum.UNLOGINED;

/**
 * @author 玄畅
 * @date: 4/22/14 23:27
 */
public class UserService {

	private final DBI dbi = DaoFactory.getBaseDBI();
	private final ChannelService channelService = new ChannelService();

	private static final Cache<Long, String> tokenCache;

	static {
		tokenCache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(60, TimeUnit.MINUTES)
				.build();
	}

	public User getByName(String name) {
		UserDao dao = dbi.onDemand(UserDao.class);
		return dao.getByName(name);
	}

	public User getById(long id) {
		UserDao dao = dbi.onDemand(UserDao.class);
		return dao.getById(id);
	}

	public User getByUserNamePasswd(String name, String passwd) {
		UserDao dao = dbi.onDemand(UserDao.class);
		return dao.getByUserNamePasswd(name, passwd);
	}


	/**
	 * 校验用户登录态
	 *
	 * @param session httpSession
	 *
	 * @return userEnum
	 */
	public UserEnum checkLoginToken(HttpSession session) {

		if (session == null) {
			return UNLOGINED;
		}

		Object _userId = session.getAttribute("userId");
		Object _token = session.getAttribute("token");
		Object _ctime = session.getAttribute("ctime");
		if (_userId == null || _token == null || _ctime == null) {
			return UNLOGINED;
		}

		//	session有效期一个小时
		long ct = (System.currentTimeMillis() - (Long) _ctime) / 1000;
		if (ct > 3600) {
			return UNLOGINED;
		}

		//	检查token
		String token = tokenCache.getIfPresent(_userId);
		if (StringUtils.isEmpty(token)) {
			return UNLOGINED;
		}

		return SUCCESS;
	}


	/**
	 * 设置用户登录态
	 *
	 * @param userId  用户ID
	 * @param session httpsession
	 *
	 * @return bool
	 */
	public boolean setLoginToken(long userId, HttpSession session) {
		UserDao dao = dbi.onDemand(UserDao.class);
		User user = dao.getById(userId);
		if (user == null) {
			return false;
		}

		DateTime dt = new DateTime();

		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher()
				.putInt(dt.getDayOfYear()) //	天数
				.putInt(dt.getHourOfDay()) //	小时
				.putString(user.getName(), Charsets.UTF_8)
				.putString(user.getPasswd(), Charsets.UTF_8)
				.putLong(user.getId())
				.hash();

		String token = hc.toString();

		tokenCache.put(userId, token);

		session.setAttribute("userId", userId);
		session.setAttribute("token", token);
		session.setAttribute("ctime", System.currentTimeMillis());
		//
		session.setAttribute(Constants.myChannelTree, buildChannelTree(userId));

		return true;
	}


	/**
	 * 构建登录用户的频道树
	 *
	 * @param userId
	 *
	 * @return
	 */
	private List<Channel> buildChannelTree(long userId) {
		//	todo 根据用户权限取得相应得频道	channel_tree
		List<Channel> list = channelService.query(0);
		if (list == null) {
			return null;
		}

		return list;
	}

}
