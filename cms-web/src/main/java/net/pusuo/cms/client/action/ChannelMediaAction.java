/*
 * Created on 2005-11-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.ChannelMediaUtil;
import com.hexun.cms.client.file.ClientFile;

import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Media;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelMediaAction extends BaseAction {
 
	public ActionForward view(ActionMapping mapping, 
	        					ActionForm form,
	        					HttpServletRequest request, 
	        					HttpServletResponse response) {
	    
		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm)form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		}
		catch(UnauthenticatedException ue) {
	        errors.add("auth.failure", new ActionError("auth.failure"));
	        saveErrors(request, errors);
	        return mapping.findForward("failure");
		}
		
		// channel list
		List channels = auth.getChannelList();
		cmForm.set("channels", ItemUtil.ListToLVB(channels));
		
		// current channel
		int channelid = -1;
		if (request.getParameter("channelid") != null) {
		    channelid = Integer.parseInt(request.getParameter("channelid"));
		}
		else {
		    channelid = ((Channel)channels.get(0)).getId();
		}
		cmForm.set("channelid", new Integer(channelid));
		
		// media list
		List medias = ItemManager.getInstance().getList(Media.class);
		cmForm.set("medias", ItemUtil.ListToLVB(medias));
		
		// selected medias
		String fileName = ChannelMediaUtil.getChannelMediaFileName(channelid);
		String fileContent = null;
		try {
		    fileContent = ClientFile.getInstance().read(fileName);
		}
		catch (Exception e) {
		    fileContent = null;
		}
		List mediaids = null;
		if (fileContent != null) {
		    mediaids = ChannelMediaUtil.decodeMedias(fileContent);
		}
		if (mediaids == null) {
		    mediaids = new ArrayList();
		}
		cmForm.set("mediaids", ItemUtil.StringArrayConverter(mediaids));
	
		return mapping.findForward("view");
	}

	public ActionForward save(ActionMapping mapping, 
			ActionForm form,
			HttpServletRequest request, 
			HttpServletResponse response) {
	    
		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm)form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		}
		catch(UnauthenticatedException ue) {
	        errors.add("auth.failure", new ActionError("auth.failure"));
	        saveErrors(request, errors);
	        return mapping.findForward("failure");
		}
		
		int channelid = ((Integer)cmForm.get("channelid")).intValue();
		String fileName = ChannelMediaUtil.getChannelMediaFileName(channelid);
		
		String[] mediaids = (String[])cmForm.get("mediaids");
		String fileContent = ChannelMediaUtil.encodeMedias(mediaids);
		
		try {
		    ClientFile.getInstance().write(fileContent, fileName, false);
		}
		catch (Exception e) {		    
		}
		//����ChannelMediaUtil����һ�������ʱ������ȡ���µ�ý��
		ChannelMediaUtil.removeChannelMedia(channelid);
		return mapping.findForward("refresh");
	}

}
