package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import net.pusuo.cms.web.util.ViewUtil;
import net.pusuo.cms.core.bean.Constant;
import net.pusuo.cms.web.util.FormRequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-31
 * Time: 下午11:28
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("channel")
public class ChannelController {
	private final ChannelService channelService = new ChannelService();

	@RequestMapping("list")
	public ModelAndView list() {
		List<Channel> list = channelService.query(0);
		JSONArray array = new JSONArray();
		for (Channel ch : list) {
			array.add(ch.toJson());
		}

		return ViewUtil.renderListView("_channel.jsp", array.toJSONString());
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView add(HttpServletRequest request) {
		ModelAndView view = new ModelAndView(Constant.COMMON_JSON_PAGE);

		JSONObject json = FormRequestUtil.parseData(request);
		String name = (String) json.get("name");
		String dir = (String) json.get("dir");
		if (name == null || dir == null) {
			view.addObject("result", "频道名和频道目录不能为空");
			return view;
		}

		Channel ch = new Channel();
		ch.setDir(dir);
		ch.setName(name);
		channelService.insert(ch);

		Channel channel = channelService.findByName(dir);
		view.addObject("result", channel.toJson().toString());
		return view;
	}

	/**
	 * 删除频道
	 *
	 * @param request request
	 *
	 * @return json
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest request) {
		JSONObject json = FormRequestUtil.parseData(request);
		ModelAndView view = new ModelAndView(Constant.COMMON_JSON_PAGE);

		Object _id = json.get("id");
		if (_id == null) {
			return null;
		}
		int id = Integer.parseInt(_id.toString());
		Channel channel = channelService.getById(id);
		if (channel != null) {
			channelService.delete(id);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", channel.getId());
			jsonObject.put("name", channel.getName());
			jsonObject.put("dir", channel.getDir());
			view.addObject("result", jsonObject.toJSONString());
		} else {
			view.addObject("result", "频道[id=" + _id + "]不存在");
		}

		return view;
	}

}
