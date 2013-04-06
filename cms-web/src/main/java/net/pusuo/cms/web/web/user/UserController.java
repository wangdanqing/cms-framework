package net.pusuo.cms.web.web.user;

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
public class UserController {

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView m = new ModelAndView();
        m.setViewName("user/login");
        return m;
    }
}
