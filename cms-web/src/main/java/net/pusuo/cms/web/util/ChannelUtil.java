package net.pusuo.cms.web.util;

import net.pusuo.cms.web.service.ChannelService;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午11:38
 * To change this template use File | Settings | File Templates.
 */
public class ChannelUtil {
    private final static ChannelService channelService = new ChannelService();

    public static void fillChannel(ModelAndView view) {
        view.addObject("channelList", channelService.query(0));
    }
}
