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
import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.core.bean.auth.User;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.UserDao;
import net.pusuo.cms.web.result.UserEnum;
import net.pusuo.cms.web.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;

import javax.servlet.http.HttpSession;
import java.util.Collections;
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
	private final SubjectService subjectService = new SubjectService();

	private static final Cache<Long, String> tokenCache;
	private static final Cache<Long, String> menuCache;

	static {
		tokenCache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(60, TimeUnit.MINUTES)
				.build();

		menuCache = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterAccess(60, TimeUnit.MINUTES)
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
	 * @return
	 */
	private String buildChannelTree(long userId) {

		String jsonMenu = menuCache.getIfPresent(userId);
		if (jsonMenu != null) {
			return jsonMenu;
		}

		//  root node
		JSONObject root = new JSONObject();
		root.put("id", "root");
		root.put("parent", "#");
		root.put("text", "频道");

		//	todo 根据用户权限取得相应得频道	channel_tree
		List<Channel> list = channelService.query(0);
		if (list == null) {
			return "[]";
		}

		JSONArray ret = new JSONArray();
		ret.add(root);
		String channelPrefix = "ch_";
		for (Channel ch : list) {
			JSONObject chobj = new JSONObject();
			chobj.put("id", channelPrefix + ch.getId());
			chobj.put("parent", "root");
			chobj.put("text", ch.getName());

			JSONObject link = new JSONObject();
			link.put("href", "/channel/get/" + ch.getId());
			chobj.put("a_attr", link);

			ret.add(chobj);

			List<Subject> subjects = subjectService.query(ch.getId());
			if (subjects == null || subjects.size() == 0) {
				continue;
			}

			Collections.sort(subjects);

			for (Subject subject : subjects) {
				JSONObject obj = new JSONObject();
				obj.put("id", subject.getId());
				String pid = subject.getPid() == -1 ? channelPrefix + ch.getId() : String.valueOf(subject.getPid());
				obj.put("parent", pid);
				obj.put("text", subject.getName());

				JSONObject slink = new JSONObject();
				slink.put("href", "/subject/toitem?op=update&id=" + subject.getId());
				obj.put("a_attr", slink);

				ret.add(obj);
			}

			/*

			//  对栏目排序，树状结构
			Map<Integer, List<Subject>> treeMap = new TreeMap<Integer, List<Subject>>();
			Map<Integer, Subject> map = new HashMap<Integer, Subject>();
			for (Subject sub : subjects) {
				if (treeMap.containsKey(sub.getPid())) {
					treeMap.get(sub.getPid()).add(sub);
				} else {
					List<Subject> sl = new ArrayList<Subject>();
					sl.add(sub);
					treeMap.put(sub.getPid(), sl);
				}

				map.put(sub.getId(), sub);
			}

			JSONArray subjectList = new JSONArray();
			for (Map.Entry<Integer, List<Subject>> entry : treeMap.entrySet()) {
				JSONObject obj = new JSONObject();

				if (entry.getKey() != -1) {
					obj.put("id", entry.getKey());
					obj.put("text", map.get(entry.getKey()).getName());
					chobj.put("type", "subject");
					List<Subject> sl = entry.getValue();
					if (sl != null && sl.size() > 0) {
						JSONArray ja = new JSONArray();
						for (Subject s1 : sl) {
							JSONObject js = new JSONObject();
							js.put("id", s1.getId());
							js.put("text", s1.getName());
							chobj.put("type", "subject");
							ja.add(js);
						}

						obj.put("children", ja);
					}

					subjectList.add(obj);
				} else {
					//  首页
					List<Subject> sl = entry.getValue();
					for (Subject s1 : sl) {
						JSONObject js = new JSONObject();
						js.put("id", s1.getId());
						js.put("text", s1.getName());
						chobj.put("type", "home");
						subjectList.add(js);
					}
				}
			}
			chobj.put("children", subjectList);
			*/

		}

		String result = ret.toJSONString();
		menuCache.put(userId, result);

		return result;
	}

}
