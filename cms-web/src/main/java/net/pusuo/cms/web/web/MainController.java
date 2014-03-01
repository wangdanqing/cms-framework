package net.pusuo.cms.web.web;

import net.pusuo.cms.web.util.CommonViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 首页欢迎页
 * User: shijinkui
 * Date: 13-3-27
 * Time: 上午12:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MainController {
	@RequestMapping("/")
	public ModelAndView defaultPage() {
		return CommonViewUtil.renderListView("_welcome.jsp", null);
	}
}
