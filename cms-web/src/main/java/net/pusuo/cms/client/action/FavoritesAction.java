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

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.client.util.Favorites;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Subject;
import com.hexun.cms.tool.UFInterface;

public class FavoritesAction extends BaseAction {

	private static final Log LOG = LogFactory.getLog(FavoritesAction.class);

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
		//String channelName = channel.getDir();

		// //////////////////////////////////////////////////////////////////////

		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");

		List categorys;
		try {
			categorys = cf.listCategory(userName);
			cmForm.set("categorys", categorys);
		} catch (RemoteException e1) {
			LOG
					.error("ChannelFavAction view action from CFInterface list method.");
			return mapping.findForward("failure");
		}

		String categoryName = (String) cmForm.get("categoryName");
		if (StringUtils.isNotEmpty(categoryName)) {
			// user subject list
			List subjects = null;
			try {
				subjects = cf.list(userName, categoryName);
			} catch (Exception e) {
				subjects = null;
				LOG
						.error("ChannelFavAction view action from CFInterface list method.");
				return mapping.findForward("failure");
			}
			if (subjects == null) {
				subjects = new ArrayList();
			}
			cmForm.set("subjects", ItemUtil.ListToLVBByName(subjects));
		}
		processSelectSubjectId(request);
		return mapping.findForward("view");
	}

	/**
	 * @param request
	 */
	private void processSelectSubjectId(HttpServletRequest request) {
		String alreadySelectSubjectId=request.getParameter("SelectSubjectId");
		if(StringUtils.isNotBlank(alreadySelectSubjectId))request.setAttribute("SelectSubjectId", alreadySelectSubjectId);
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

		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		// subject
		int subjectId = ((Integer) cmForm.get("subjectid")).intValue();
		Subject subject = (Subject) ItemManager.getInstance().get(
				new Integer(subjectId), Subject.class);
		if (subject == null) {
			errors.add("errors.item.pnamenotexist", new ActionError(
					"errors.item.pnamenotexist",""+subjectId));
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
			UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");

			// max number of list size is: 30
			List subjects = cf.list(userName, categoryName);
			if (subjects != null && subjects.size() >= 30) {
				errors.add("error.number.large", new ActionError(
						"error.number.large", "30"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			cf.add(userName, categoryName, subjectId, subjectName);

			// sync
			Favorites.getInstance().initUserParents(userName);
		} catch (Exception e) {
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"errors.detail", "error:"+e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}


		request.setAttribute("removeSubjectIdShow", "true");
		return mapping.findForward("refresh");
	}

	public ActionForward customize(ActionMapping mapping, ActionForm form,
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

		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		// subject list
		String[] childids = (String[]) cmForm.get("childids");
		List subjects = new ArrayList();
		if (childids != null) {
			for (int i = 0; i < childids.length; i++) {
				int id = -1;
				try {
					id = Integer.parseInt(childids[i]);
				} catch (Exception e) {
					id = -1;
				}
				if (id > 0) {
					Subject subject = (Subject) ItemManager.getInstance().get(
							new Integer(id), Subject.class);
					subjects.add(subject);
				}
			}
		}

		try {
			UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");

			// max number of list size is: 30
			List oldSubjects = cf.list(userName);
			if (oldSubjects != null) {
				int count = oldSubjects.size() + subjects.size();
				if (count > 30)
					return mapping.findForward("failure");
			}

			cf.add(userName, subjects);

			// sync
			Favorites.getInstance().initUserParents(userName);
		} catch (Exception e) {
			LOG
					.error("ChannelFavAction add action from CFInterface add method.");
		}

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

		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		String categoryName = (String) cmForm.get("categoryName");

		if (StringUtils.isEmpty(categoryName)) {
			errors.add("error.category.notexist", new ActionError(
					"error.category.notexist"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

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

		try {
			UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");
			cf.delete(userName, categoryName, ids);
			// sync
			Favorites.getInstance().initUserParents(userName);
		} catch (Exception e) {
			LOG
					.error("ChannelFavAction delete action from CFInterface delete method.");
		}

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
		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		String categoryName = (String) cmForm.get("categoryName");

		UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");

		if (StringUtils.isNotEmpty(categoryName)) {
			try {
				List a=cf.listCategory(userName);
				if(a.size()>=5){
					errors.add("error.number.large", new ActionError(
							"error.number.large", "5"));
					saveErrors(request, errors);
					return mapping.findForward("failure");

				}
				cf.addCategory(userName, categoryName);
			} catch (RemoteException e) {
				errors.add("error.server.io.error", new ActionError(
						"error.server.io.error" + e.getMessage()));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
		}
		Favorites.getInstance().initUserParents(userName);
		processSelectSubjectId(request);
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

		// user name
		String userName = auth.getUserName();
		if (userName == null || userName.trim().length() == 0)
			return mapping.findForward("failure");

		String[] catenames = (String[]) cmForm.get("catenames");
		List names = new ArrayList();
		if (catenames != null) {
			for (int i = 0; i < catenames.length; i++) {
				if (StringUtils.isNotEmpty(catenames[i])) {
					names.add(catenames[i]);
				}
			}
		}

		UFInterface cf = (UFInterface) ClientUtil.renewRMI("UserFav");
		try {
			cf.deleteCategory(userName, names);
		} catch (RemoteException e) {
			errors.add("error.server.io.error", new ActionError(
					"error.server.io.error" + e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		Favorites.getInstance().initUserParents(userName);
		return mapping.findForward("refresh");
	}
}
