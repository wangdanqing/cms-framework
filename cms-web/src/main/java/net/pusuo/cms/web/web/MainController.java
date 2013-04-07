package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-27
 * Time: 上午12:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MainController {

    private final ChannelService channelService = new ChannelService();

    @RequestMapping("/")
    public ModelAndView defaultPage() {
        ModelAndView view = new ModelAndView("index");
        view.addObject("channelList", getChannelList());

        return view;
    }

    private List<Channel> getChannelList() {
        return channelService.query(0);
    }
}
