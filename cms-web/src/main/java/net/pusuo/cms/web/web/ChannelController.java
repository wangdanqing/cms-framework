package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import net.pusuo.cms.web.util.ChannelUtil;
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
        ModelAndView view = new ModelAndView("index");
        List<Channel> list = channelService.query(0);
        view.addObject("list", list);
        view.addObject("include_page", "_channel.jsp");
        ChannelUtil.fillChannel(view);

        return view;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ModelAndView add(HttpServletRequest request) {
        String name = request.getParameter("name");
        String dir = request.getParameter("dir");
        if (name == null || dir == null) {
            return null;
        }
        Channel ch = new Channel();
        ch.setDir(dir);
        ch.setName(name);
        channelService.insert(ch);

        //
        ModelAndView view = new ModelAndView("index");
        List<Channel> list = channelService.query(0);
        view.addObject("list", list);
        view.addObject("include_page", "_channel.jsp");

        ChannelUtil.fillChannel(view);
        return view;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ModelAndView delete(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id == null) {
            return null;
        }

        channelService.delete(Integer.parseInt(id));

        //
        ModelAndView view = new ModelAndView("index");
        List<Channel> list = channelService.query(0);
        view.addObject("list", list);
        view.addObject("include_page", "_channel.jsp");

        ChannelUtil.fillChannel(view);
        return view;
    }

}
