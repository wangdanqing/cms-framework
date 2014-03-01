package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.web.service.ChannelService;
import net.pusuo.cms.web.service.SubjectService;
import net.pusuo.cms.web.util.CommonViewUtil;
import net.pusuo.cms.web.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午10:42
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "subject")
public class SubjectController {

	private final SubjectService service = new SubjectService();
	private final ChannelService channelService = new ChannelService();

	@RequestMapping("list")
	public ModelAndView list() {
		List<Subject> list = service.query(0);
		return CommonViewUtil.renderListView("subject/_subject_list.jsp", list);
	}

	@RequestMapping("homepage")
	public ModelAndView getHomepage() {
		ModelAndView view = new ModelAndView("index");
		CommonViewUtil.fillChannel(view);

		List<Subject> list = service.getHomePageList();
		view.addObject("home_list", list);

		List<Channel> channelList = channelService.query(0);
		view.addObject("channel_list", channelList);

		view.addObject(Constants.var_include, "subject/_homepage_list.jsp");

		Map<Channel, Subject> map = new HashMap<Channel, Subject>();
		for (Channel ch : channelList) {
			for (Subject subject : list) {
				if (subject.getChannelId() == ch.getId()) {
					map.put(ch, subject);
					break;
				} else {
					map.put(ch, null);
				}
			}
		}

		view.addObject("channel_homepage", map);

		return view;
	}

	@RequestMapping("createHomepage")
	public ModelAndView createHomepage(HttpServletRequest requet) {
		String name = requet.getParameter("name");
		String desc = requet.getParameter("desc");
		String templateId = requet.getParameter("templateId");
		String channelId = requet.getParameter("channelId");
		if (name == null || desc == null || templateId == null || channelId == null) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Subject home = new Subject();
		home.setChannelId(Integer.parseInt(channelId.trim()));
		home.setDesc(desc);
		home.setName(name);
		home.setTemplateId(Integer.parseInt(templateId));
		home.setParentId(-1);
		home.setFullpath("/");
		home.setCtime(System.currentTimeMillis());
		home.setPriority(60);
		home.setStatus(0);
		home.setEditorId(0);
		home.setBakTemplateList("");
		home.setType(0);

		service.insert(home);

		////////////     todo ajax
		ModelAndView view = new ModelAndView("index");
		CommonViewUtil.fillChannel(view);

		List<Subject> list = service.getHomePageList();
		view.addObject("home_list", list);

		List<Channel> channelList = channelService.query(0);
		view.addObject("channel_list", channelList);

		view.addObject(Constants.var_include, "subject/_homepage_list.jsp");

		return view;
	}
}
