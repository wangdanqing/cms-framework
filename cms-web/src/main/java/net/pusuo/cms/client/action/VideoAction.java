package net.pusuo.cms.client.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.core.*;

public class VideoAction extends VideoNewsAction {

	private static final Log LOG = LogFactory.getLog(VideoAction.class);

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		Authentication auth = null;
		ActionErrors errors = new ActionErrors();
		Video videoItem = null;
		
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		try {
			BaseForm fm = (BaseForm) form;
			Integer _id = (Integer) fm.get("id");
			int id = _id.intValue();

			String pPname = (String) fm.get("pname");
			if (pPname == null || pPname.trim().length() == 0) {
				throw new Exception("No video pPname");
			}

			String video0 = request.getParameter("video0");
			String videolen0 = request.getParameter("videolen0");
			String video_bigpic = request.getParameter("video_bpic0");
			String video_smallpic = request.getParameter("video_spic0");
			String video_desc = request.getParameter("video_desc0");

			if(!StringUtils.isEmpty(video0) && !StringUtils.isEmpty(videolen0) && !StringUtils.isEmpty(video_bigpic) && !StringUtils.isEmpty(video_smallpic) && !StringUtils.isEmpty(video_desc)){
			fm.set("type", new Integer(ItemInfo.VIDEO_TYPE));
            fm.set("editor", new Integer(auth.getUserID()));
			fm.set("desc", video_desc); //������
			fm.set("url",video0);
			fm.set("bigpic",video_bigpic);
			fm.set("smallpic",video_smallpic);
			Integer videoLen = new Integer(videolen0);
			fm.set("length",videoLen);
			String ext = "";
			if(video0.lastIndexOf(".")>0){
				ext = video0.substring(video0.lastIndexOf(".") + 1);
			}	
					
					
			Object[] result = super.saveProcesser(fm, ext);
            videoItem = (Video) result[0];
            errors = (ActionErrors) result[1];

            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                ret = mapping.findForward("failure");
				return ret;
            } else {
                 EntityItem pItem = (EntityItem) ItemManager.getInstance().get(new Integer(videoItem.getPid()),EntityItem.class);
                 if (pItem.getType() == ItemInfo.NEWS_TYPE) {
                      String vids = ((News) pItem).getVideos();
                      vids = (vids == null ? "" : vids);
                      ((News) pItem).setVideos(videoItem.getId()+"");
                      ItemManager.getInstance().update(pItem);
                 }
        		response.sendRedirect("/video.do?method=view&id="+ videoItem.getId());
             }
		}else{
        		response.sendRedirect("/video.do?method=view&id="+ _id);
		}
		} catch (Exception e) {
			LOG.error("save error",e);
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
			return ret;
		}
		return ret;
	}
}
