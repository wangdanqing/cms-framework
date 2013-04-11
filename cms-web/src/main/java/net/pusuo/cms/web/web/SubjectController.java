package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.web.service.SubjectService;
import net.pusuo.cms.web.util.ChannelUtil;
import net.pusuo.cms.web.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    @RequestMapping("list")
    public ModelAndView list() {
        ModelAndView view = new ModelAndView("index");
        ChannelUtil.fillChannel(view);

        List<Subject> list = service.query(0);
        view.addObject("subject_list", list);
        view.addObject(Constants.var_include, "subject/_subject_list.jsp");

        return view;
    }

}
