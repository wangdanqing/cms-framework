package net.pusuo.cms.web.web;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pusuo.cms.core.bean.Media;
import net.pusuo.cms.web.service.MediaService;
import net.pusuo.cms.web.util.CommonViewUtil;
import net.pusuo.cms.web.util.FormRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 媒体管理
 *
 * @author 玄畅
 * @date 14-3-5 下午10:12
 */
@Controller
@RequestMapping("media")
public class MediaController {
	private final MediaService service = new MediaService();

	@RequestMapping("list")
	public ModelAndView list() {
		List<Media> list = service.query(0);
		JSONArray array = new JSONArray();
		for (Media ch : list) {
			array.add(ch.toJson());
		}

		return CommonViewUtil.renderListView("_media.jsp", array.toJSONString());
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String add(HttpServletRequest request, HttpServletResponse response) {

		JSONObject json = FormRequestUtil.parseData(request);
		String name = (String) json.get("desc");
		if (name == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return "媒体名不能为空";
		}
		String siteUrl = (String) json.get("siteurl");
		String logoUrl = (String) json.get("logourl");

		Media media = new Media();
		media.setDesc(name);
		media.setSiteurl(siteUrl);
		media.setLogourl(logoUrl);
		service.insert(media);

		Media ret = service.findByName(name);
		return ret.toJson().toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = FormRequestUtil.parseData(request);

		Object _id = json.get("id");
		if (_id == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return "媒体Id不能为空";
		}

		int id = Integer.parseInt(_id.toString());
		Media media = service.getById(id);
		if (media != null) {
			service.delete(id);
			return media.toJson().toJSONString();
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "媒体[id=" + _id + "]不存在";
		}
	}

	@ResponseBody
	@RequestMapping(value = "modify", method = RequestMethod.POST)
	public String modify(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = FormRequestUtil.parseData(request);

		Object _id = json.get("id");
		if (_id == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return "媒体Id不能为空";
		}

		int id = Integer.parseInt(_id.toString());
		Media media = service.getById(id);
		if (media != null) {
			String desc = (String) json.get("desc");
			String logourl = (String) json.get("logourl");
			String siteurl = (String) json.get("siteurl");
			if (StringUtils.isNotBlank(desc)) {
				media.setDesc(desc);
			}
			if (StringUtils.isNotBlank(logourl)) {
				media.setLogourl(logourl);
			}
			if (StringUtils.isNotBlank(siteurl)) {
				media.setSiteurl(siteurl);
			}

			return media.toJson().toJSONString();
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "媒体[id=" + _id + "]不存在";
		}
	}
}
