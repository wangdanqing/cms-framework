package net.pusuo.cms.web.web;

import net.pusuo.cms.web.util.ChannelUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-27
 * Time: 上午12:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MainController {


    @RequestMapping("/")
    public ModelAndView defaultPage() {
        ModelAndView view = new ModelAndView("index");
        ChannelUtil.fillChannel(view);
        view.addObject("include_page", "_welcome.jsp");
        return view;
    }

}
