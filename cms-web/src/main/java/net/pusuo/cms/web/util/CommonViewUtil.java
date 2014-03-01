package net.pusuo.cms.web.util;

import net.pusuo.cms.web.service.ChannelService;
import org.springframework.web.servlet.ModelAndView;

/**
 * 框架视图
 * 跳转到json数据页面
 *
 * @author 玄畅
 * @date: 14-3-1 下午8:09
 */
public class CommonViewUtil {
	private final static ChannelService channelService = new ChannelService();

	public static void fillChannel(ModelAndView view) {
		view.addObject("channelList", channelService.query(0));
	}

	public static ModelAndView renderJsonView(String result) {
		ModelAndView view = new ModelAndView(Constant.COMMON_JSON_PAGE);
		view.addObject("result", result);
		return view;
	}

	public static ModelAndView renderObjView(String page, Object item) {
		ModelAndView view = new ModelAndView("index");
		view.addObject("include_page", page);
		view.addObject("item", item);
		view.addObject("channelList", channelService.query(0));
		return view;
	}

	public static ModelAndView renderListView(String includePage, Object list) {
		ModelAndView view = new ModelAndView("index");
		view.addObject("include_page", includePage);
		view.addObject("list", list);
		view.addObject("channelList", channelService.query(0));    //	左边栏，频道列表数据
		return view;
	}
}
