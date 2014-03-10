package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Item;
import net.pusuo.cms.core.bean.Template;
import net.pusuo.cms.web.service.TemplateService;
import net.pusuo.cms.web.util.ViewUtil;
import net.pusuo.cms.core.bean.Constant;
import net.pusuo.cms.web.util.FormRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;

import static net.pusuo.cms.web.util.ViewUtil.*;

/**
 * 模版的增删改查
 * User: shijinkui
 * Date: 13-4-27
 * Time: 上午12:28
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("template")
public class TemplateController {
	private static final Logger log = LoggerFactory.getLogger(TemplateController.class);

	private final TemplateService service = new TemplateService();

	@RequestMapping("list")
	public ModelAndView list() {
		List<Template> list = service.query(0);
		JSONArray array = new JSONArray();
		for (Template tmp : list) {
			array.add(tmp.toJson());
		}

		return ViewUtil.renderListView("_template.jsp", array.toJSONString());
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView add(HttpServletRequest request) {

		JSONObject json = FormRequestUtil.parseData(request);
		String name = (String) json.get("name");
		String type = (String) json.get("type");
		if (name == null || type == null) {
			ModelAndView view = new ModelAndView(Constant.COMMON_JSON_PAGE);
			view.addObject("result", "模版名和模版类型不能为空");
			return view;
		}

		Template t = new Template();
		t.setName(name);
		t.setCreateTime(new Timestamp(System.currentTimeMillis()));
		t.setUptime(new Timestamp(System.currentTimeMillis()));
		t.setStatus(Item.STATUS_ENABLE);
		t.setCreator(-1);   // todo 从session中取出登录的ID
		t.setType(Integer.parseInt(type));

		service.insert(t);
		Template ret = service.getByName(name);
		return renderJsonView(ret.toJson().toJSONString());
	}

	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public ModelAndView getDetail(@RequestParam("id") int id) {
		Template template = null;
		if (id != 0) {
			template = service.getById(id);
		}

		return renderObjView("_template_modify.jsp", template);
	}

	@RequestMapping(value = "modify", method = RequestMethod.POST)
	public ModelAndView modify(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String content = request.getParameter("content");
		if (id == null) {
			return list();
		}

//		content = content == null ? "" : StringEscapeUtils.escapeHtml(content);
		Template tmp = service.getById(Integer.parseInt(id));
		tmp.setType(Integer.parseInt(type));
		tmp.setName(name);
		tmp.setContent(content);
		tmp.setUptime(new Timestamp(System.currentTimeMillis()));

		service.update(tmp);

		return renderListView("_template.jsp", tmp);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest request) {
		JSONObject json = FormRequestUtil.parseData(request);
		Object _id = json.get("id");
		if (_id == null) {
			return null;
		}

		service.delete(Integer.parseInt(_id.toString()));

		return renderJsonView("");
	}

}
