package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import org.codehaus.jackson.JsonFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-17
 * Time: 下午11:53
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

	private final ChannelService channelService = new ChannelService();
	JsonFactory jsonFactory = new JsonFactory();

	@RequestMapping("getroot")
	public String getAll(HttpServletRequest request) {
		List<Channel> list = channelService.query(0);
		if (list == null) {
			return "[]";
		}

		JSONArray array = new JSONArray();
		for (Channel ch : list) {
			JSONObject obj = new JSONObject();
			obj.put("id", ch.getId());
			obj.put("text", ch.getName());
			obj.put("parent", "#");
			array.add(obj);
		}

		return array.toJSONString();
	}


	/**
	 * 根据菜单ID获取子列表
	 *
	 * @param id pid
	 * @return childres list
	 */
	@RequestMapping("getbypid")
	@ResponseBody
	public String getByPid(@RequestParam("pid") String id) {
		return "[]";
	}
}
