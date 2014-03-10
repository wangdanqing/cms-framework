package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.web.service.ChannelService;
import net.pusuo.cms.web.service.SubjectService;
import net.pusuo.cms.web.service.TemplateService;
import net.pusuo.cms.web.util.FormRequestUtil;
import net.pusuo.cms.web.util.ViewUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
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
	private final ChannelService channelService = new ChannelService();
	private final TemplateService templateService = new TemplateService();

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request) {
		String channelId = request.getParameter("channelId");
		int cid = channelId == null ? -1 : Integer.parseInt(channelId);
		List<Subject> list = service.query(cid);
		JSONArray array = new JSONArray();
		for (Subject sub : list) {
			array.add(sub.toJson());
		}

		return ViewUtil.renderListView("subject/_list.jsp", array);
	}

	@RequestMapping(value = "toitem", method = RequestMethod.GET)
	public ModelAndView toitem(@RequestParam(value = "id", required = false) Integer id,
							   @RequestParam(value = "op", required = true) String op) {
		Subject item = null;
		op = op == null ? "create" : op;

		//	带上一次创建的新闻信息，创建新闻
		if (id != null && id > 0 && op.equals("create")) {
			//	新生成一篇文章，默认带上一些共同属性
			Subject current = service.getById(id);
			item = new Subject();
			item.setPid(current.getPid());
			item.setCategory(current.getCategory());
			item.setChannelId(current.getChannelId());
			item.setEditorId(current.getEditorId());
			item.setStatus(current.getStatus());
		}

		if (id != null && id > 0 && op.equals("update")) {
			item = service.getById(id);
		}

		ModelAndView view = new ModelAndView("index");

		view.addObject("include_page", "subject/_item.jsp");
		view.addObject("item", item);
		view.addObject("channelList", channelService.query(0));
		view.addObject("templateList", templateService.query(0));
		view.addObject("op", op);    //	operation, create or update

		return view;
	}

	@RequestMapping("create")
	public ModelAndView create(HttpServletRequest requet, HttpServletResponse response) {

		String name = requet.getParameter("name");
		String templateId = requet.getParameter("templateId");
		String channelId = requet.getParameter("channelId");
		String type = requet.getParameter("type");
		String shortName = requet.getParameter("shortName");
		if (name == null || templateId == null || channelId == null || type == null || shortName == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			ModelAndView view = ViewUtil.renderObjView("subject/_item.jsp", null);
			view.addObject("channelList", channelService.query(0));
			view.addObject("templateList", templateService.query(0));
			view.addObject("error", "栏目名，英文缩写，栏目类型，频道ID，模版ID不能为空");
			return view;
		}

		String desc = requet.getParameter("desc");
		String pid = requet.getParameter("pid");
		String priority = requet.getParameter("priority");
		String status = requet.getParameter("status");

		Subject subject = new Subject();
		subject.setChannelId(Integer.parseInt(channelId.trim()));
		subject.setDesc(desc);
		subject.setName(name);
		subject.setShortName(shortName);
		subject.setTemplateId(Integer.parseInt(templateId));
		subject.setPid(Integer.parseInt(pid));
		subject.setCategory("/");
		subject.setCtime(new Timestamp(System.currentTimeMillis()));
		subject.setUptime(new Timestamp(System.currentTimeMillis()));
		subject.setPriority(Integer.parseInt(priority));
		subject.setStatus(Integer.parseInt(status));
		subject.setEditorId(0);
		subject.setBakTemplateList("");
		subject.setType(Integer.parseInt(type));

		boolean ret = service.insert(subject);

		ModelAndView view = new ModelAndView("index");
		view.addObject("include_page", "subject/_list.jsp");
		view.addObject("channelList", channelService.query(0));
		view.addObject("list", service.query(-1));

		view.setViewName("redirect:/subject/list");
		return view;
	}


	@RequestMapping("update")
	public ModelAndView update(HttpServletRequest requet, HttpServletResponse response) {
		String id = requet.getParameter("id");
		if (StringUtils.isEmpty(id)) {
			return null;
		}

		Subject item = service.getById(Integer.parseInt(id.trim()));

		//	update
		String name = requet.getParameter("name");
		String templateId = requet.getParameter("templateId");
		String channelId = requet.getParameter("channelId");
		String type = requet.getParameter("type");
		String shortName = requet.getParameter("shortName");
		String desc = requet.getParameter("desc");
		String pid = requet.getParameter("pid");
		String priority = requet.getParameter("priority");
		String status = requet.getParameter("status");
		String tags = requet.getParameter("tags");

		item.setDesc(desc);
		item.setTags(tags);

		if (StringUtils.isNotEmpty(status)) {
			item.setStatus(Integer.parseInt(status));
		}
		if (StringUtils.isNotEmpty(priority)) {
			item.setPriority(Integer.parseInt(priority));
		}
		if (StringUtils.isNotEmpty(pid)) {
			item.setPid(Integer.parseInt(pid));
		}
		if (StringUtils.isNotEmpty(name)) {
			item.setName(name);
		}
		if (StringUtils.isNotEmpty(templateId)) {
			item.setTemplateId(Integer.parseInt(templateId));
		}
		if (StringUtils.isNotEmpty(channelId)) {
			item.setChannelId(Integer.parseInt(channelId));
		}
		if (StringUtils.isNotEmpty(type)) {
			item.setType(Integer.parseInt(type));
		}
		if (StringUtils.isNotEmpty(shortName)) {
			item.setShortName(shortName);
		}

		boolean ret = service.update(item);
		try {
			response.sendRedirect("/subject/toitem?op=update&id=" + id);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@ResponseBody
	@RequestMapping("delete")
	public String delete(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = FormRequestUtil.parseData(request);
		Object _id = json.get("id");
		if (_id == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return "栏目Id不能为空";
		}
		int id = Integer.parseInt(String.valueOf(_id));
		Subject subject = service.getById(id);
		if (subject != null) {
			boolean ret = service.delete(id);
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "栏目[id=" + _id + "]不存在";
		}


		return "";
	}
}
