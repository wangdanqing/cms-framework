package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.EntityItem;
import net.pusuo.cms.core.bean.Media;
import net.pusuo.cms.core.bean.Subject;
import net.pusuo.cms.web.service.*;
import net.pusuo.cms.web.util.FormRequestUtil;
import net.pusuo.cms.web.util.ViewUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

import static net.pusuo.cms.web.util.ViewUtil.renderJsonView;

/**
 * @author 玄畅
 * @date 14-3-2 下午11:15
 */

@Controller
@RequestMapping("entity")
public class EntityItemController {
	private final EntityItemService service = new EntityItemService();
	private final MediaService mediaService = new MediaService();
	private final ChannelService channelService = new ChannelService();
	private final IDSeqService idSeqService = new IDSeqService();
	private final SubjectService subjectService = new SubjectService();
	private final String group = "entity";

	@RequestMapping("list")
	public ModelAndView list(@RequestParam(value = "pid", defaultValue = "-1", required = false) int pid,
							 @RequestParam(value = "subjectId", defaultValue = "-1", required = false) int subjectId,
							 @RequestParam(value = "channelId", defaultValue = "-1", required = false) int channelId) {
		List<EntityItem> list = service.query(0);
		JSONArray array = new JSONArray();
		for (EntityItem item : list) {
			array.add(item.toJson());
		}
		return ViewUtil.renderListView("entity/_list.jsp", array.toJSONString());
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView create(HttpServletRequest request) {
		String title = request.getParameter("title");
		String channelId = request.getParameter("channelId");
		String pid = request.getParameter("pid");
		if (StringUtils.isBlank(title) || StringUtils.isBlank(channelId)) {//|| StringUtils.isBlank(pid)
			ModelAndView view = new ModelAndView("index");
			view.addObject("include_page", "entity/_list");
			view.addObject("error", "新闻标题、频道Id、父栏目不能为空");

			return view;
		}

		String content = request.getParameter("content");
		String priority = request.getParameter("priority");
		String status = request.getParameter("status");
		String mediaId = request.getParameter("mediaId");
		String author = request.getParameter("author");
		String editor = request.getParameter("editor");
		String dutyEditor = request.getParameter("dutyEditor");
		String shortName = request.getParameter("shortName");

		EntityItem item = new EntityItem();
		item.setId(idSeqService.next(group));
		//todo
		if (StringUtils.isNotBlank(pid)) {
			item.setPid(Integer.parseInt(pid));
		}
		item.setTitle(title);
		item.setChannelId(Integer.parseInt(channelId));
		item.setContent(content);
		item.setPriority(Integer.parseInt(priority));
		item.setStatus(Integer.parseInt(status));

		if (StringUtils.isNotBlank(mediaId)) {
			item.setMediaId(Integer.parseInt(mediaId));
		}
		item.setAuthor(author);
		item.setEditor(100);    // todo Integer.parseInt(editor)
		if (StringUtils.isNotBlank(dutyEditor)) {
			item.setDutyEditor(Integer.parseInt(dutyEditor));
		}
		item.setShortName(shortName);
		Timestamp ct = new Timestamp(System.currentTimeMillis());
		item.setCtime(ct);
		item.setUptime(ct);

		item.setCategory("");    //	todo

		service.insert(item);

		return list(-1, -1, -1);
	}

	@RequestMapping(value = "toitem", method = RequestMethod.GET)
	public ModelAndView toitem(@RequestParam(value = "id", required = false) Long id,
							   @RequestParam(value = "op", required = true) String op) {
		EntityItem item = null;
		op = op == null ? "create" : op;
		if (id != null && id > 0 && op.equals("create")) {
			//	新生成一篇文章，默认带上一些共同属性
			EntityItem current = service.getById(id);
			item = new EntityItem();
			item.setPid(current.getPid());
			item.setCategory(current.getCategory());
			item.setChannelId(current.getChannelId());
			item.setEditor(current.getEditor());
			item.setStatus(current.getStatus());
			item.setMediaId(current.getMediaId());
		}

		if (id != null && id > 0 && op.equals("update")) {
			item = service.getById(id);
		}

		List<Subject> pidList = subjectService.getSubListById(-1);
		List<Media> mediaList = mediaService.query(0);
		ModelAndView view = new ModelAndView("index");
		view.addObject("include_page", "entity/_item.jsp");
		view.addObject("item", item);
		view.addObject("pidList", pidList);
		view.addObject("mediaList", mediaList);
		view.addObject("channelList", channelService.query(0));
		return view;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest request) {
		JSONObject json = FormRequestUtil.parseData(request);
		Object _id = json.get("id");
		if (_id == null) {
			return null;
		}

		boolean ret = service.delete(Integer.parseInt(_id.toString()));

		return renderJsonView("");
	}
}
