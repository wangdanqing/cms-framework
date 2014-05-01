package net.pusuo.cms.web;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author 玄畅
 * @date 4/20/14 20:39
 */
public class CmsDispatcherServlet extends DispatcherServlet {

	//	把channel放入每一个response里
	private final ChannelService channelService = new ChannelService();

	public CmsDispatcherServlet(WebApplicationContext context) {
		super(context);
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> map = mv.getModel();
		String sideChannelName = "side_channel_list";
		if (map != null && !map.containsKey(sideChannelName)) {
			List<Channel> list = channelService.query(-1);
			mv.addObject(sideChannelName, list);
		}

		super.render(mv, request, response);
	}
}
