package net.pusuo.cms.client.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.client.util.Favorites;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Subject;
import com.hexun.cms.tool.CFInterface;

public class ChannelFavAction extends BaseAction {

	private static final Log LOG = LogFactory.getLog(ChannelFavAction.class);

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
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
		} else {
			channelid = ((Channel) channels.get(0)).getId();
		}
		cmForm.set("channelid", new Integer(channelid));

		// channel name
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return mapping.findForward("failure");
		String channelName = channel.getDir();

		// ���Ƶ���õ������б�
		CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");
		List categorys;
		try {
			categorys = cf.listCategory(channelName);
			cmForm.set("categorys", categorys);
		} catch (RemoteException e1) {
			LOG
					.error("ChannelFavAction view action from CFInterface list method.");
			return mapping.findForward("failure");
		}

		// ��ݷ�����õ��������б�
		String categoryName = (String) cmForm.get("categoryName");
		if (StringUtils.isNotEmpty(categoryName)) {

			try {

				if (StringUtils.isNotEmpty(categoryName)) {
					List subjects = cf.list(channelName, categoryName);
					cmForm.set("subjects", ItemUtil.ListToLVBByName(subjects));
					cmForm.set("categoryName", categoryName);
				}
			} catch (Exception e) {
				LOG
						.error("ChannelFavAction view action from CFInterface list method.");
				return mapping.findForward("failure");
			}
		}

		return mapping.findForward("view");
	}

	public ActionForward add(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// channel name
		int channelid = ((Integer) cmForm.get("channelid")).intValue();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return mapping.findForward("failure");
		String channelName = channel.getDir();

		// subject
		int subjectId = ((Integer) cmForm.get("subjectid")).intValue();
		Subject subject = (Subject) ItemManager.getInstance().get(
				new Integer(subjectId), Subject.class);
		if (subject == null) {
			errors.add("errors.item.pnamenotexist", new ActionError(
					"errors.item.pnamenotexist"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		String subjectName = subject.getName();

		String categoryName = (String) cmForm.get("categoryName");
		if (StringUtils.isEmpty(categoryName)) {
			errors.add("error.category.notexist", new ActionError(
					"error.category.notexist"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		try {
			CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");

			// max number of list size is: 20
			List subjects = cf.list(channelName, categoryName);
			if (subjects != null && subjects.size() >= 20) {
				errors.add("error.number.large", new ActionError(
						"error.number.large", "20"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			cf.add(channelName, subjectId, subjectName, categoryName);
		} catch (Exception e) {
			LOG
					.error("ChannelFavAction add action from CFInterface add method.");
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"errors.detail", e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		Favorites.getInstance().initChannelParents(channelName);
		return mapping.findForward("refresh");
	}

	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// channel name
		int channelid = ((Integer) cmForm.get("channelid")).intValue();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return mapping.findForward("failure");
		String channelName = channel.getDir();

		// fav ids
		String[] favids = (String[]) cmForm.get("favids");
		List ids = new ArrayList();
		if (favids != null) {
			for (int i = 0; i < favids.length; i++) {
				int id = -1;
				try {
					id = Integer.parseInt(favids[i]);
				} catch (Exception e) {
					id = -1;
				}
				if (id > 0) {
					ids.add(new Integer(id));
				}
			}
		}

		String categoryName = (String) cmForm.get("categoryName");
		if (StringUtils.isEmpty(categoryName)) {
			errors.add("error.category.notexist", new ActionError(
					"error.category.notexist"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		
		try {
			CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");
			cf.delete(channelName, ids,categoryName);
		} catch (Exception e) {
			LOG
					.error("ChannelFavAction delete action from CFInterface delete method.");
		}
		Favorites.getInstance().initChannelParents(channelName);
		return mapping.findForward("refresh");
	}

	public ActionForward addCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// channel name
		int channelid = ((Integer) cmForm.get("channelid")).intValue();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return mapping.findForward("failure");
		String channelName = channel.getDir();

		String categoryName = (String) cmForm.get("categoryName");
		CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");
		try {
			List a=cf.listCategory(channelName);
			if(a.size()>=25){
				errors.add("error.number.large", new ActionError(
						"error.number.large", "25"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
				
			}
			cf.addCategory(channelName, categoryName);
		} catch (RemoteException e) {
			errors.add("error.server.io.error", new ActionError(
					"error.server.io.error"));

			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		Favorites.getInstance().initChannelParents(channelName);

		return mapping.findForward("refresh");
	}

	public ActionForward deleteCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm cmForm = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// channel name
		int channelid = ((Integer) cmForm.get("channelid")).intValue();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return mapping.findForward("failure");
		String channelName = channel.getDir();

		String[] catenames = (String[]) cmForm.get("catenames");
		List names = new ArrayList();
		if (catenames != null) {
			for (int i = 0; i < catenames.length; i++) {
				if (StringUtils.isNotEmpty(catenames[i])) {
					names.add(catenames[i]);
				}
			}
		}

		CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");
		try {
			cf.deleteCategory(channelName, names);
		} catch (RemoteException e) {
			errors.add("errors.adp.faill", new ActionError(
					"errors.adp.faill"));

			saveErrors(request, errors);

		}

		Favorites.getInstance().initChannelParents(channelName);

		return mapping.findForward("refresh");
	}
}
