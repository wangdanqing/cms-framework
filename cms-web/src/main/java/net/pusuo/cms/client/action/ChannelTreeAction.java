package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.tool.ChannelTreeManager;
import com.hexun.cms.client.tool.TreeNodeEntity;

/**
 * Title:
 * Description: �����ͱ���Ƶ����xml
 * @author 
 * @version 1.0
 */
public class ChannelTreeAction extends BaseAction {

	private static final Log LOG = LogFactory.getLog(ChannelTreeAction.class);
	
	/**
	 * ��ѯ�����ڵ��б�
	 * @autor: shijinkui
	 */
	public ActionForward getSecondNodes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String tid = request.getParameter("tid");
		String desc = request.getParameter("desc");
		Authentication auth = null;
		ActionErrors errors = new ActionErrors();
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException e) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		TreeNodeEntity tne = ChannelTreeManager.getInstance().getSecondNodes(desc);
		
		BaseForm dForm = (BaseForm) form;
		if(tne == null || tne.equals(""))
			return mapping.findForward("falure");
		dForm.set("tid", tne.getTid());
		dForm.set("pid", tne.getPid());
		dForm.set("desc", desc);
		dForm.set("snodelist", tne.getSubNodes());
		
		return mapping.findForward("list2");
	}
	
	/**
	 * ��������ڵ��б� 
	 */
	public ActionForward saveSecondNodes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String ret = "success";
		ActionErrors errors = new ActionErrors();
		Authentication auth = null;
		try{
	        auth = AuthenticationFactory.getAuthentication(request,response);
	        if (null == auth) {
	        	errors.add("auth.failure", new ActionError("auth.failure"));
			 	ret = "failure";
	        }
		}catch(UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		
		StringBuffer errorsb = new StringBuffer();
		
		String channelName = request.getParameter("desc");
		if(channelName == null || channelName.equals(""))
			return mapping.findForward("failure");
		//���ڵ���Ϣ
		TreeNodeEntity tne = new TreeNodeEntity();
		
		tne.setDesc(channelName);

		//�û���ӽڵ�
		
		String sid[] = request.getParameterValues("sid");
		String sname[] = request.getParameterValues("sname");
		String spid[] = request.getParameterValues("spid");
		String stid[] = request.getParameterValues("stid");
		List slist = new ArrayList();
		if(sid!=null && sid.length>0 && sname.length>0)
		{
			for(int i = 0; i < sid.length; i++){
				if(sname[i]==null || sname[i].equals(""))
				{
					ret = "failure";
					errorsb.append("��Ŀid����Ϊ�գ�");
					break;
				}
				TreeNodeEntity stne = new TreeNodeEntity();
				stne.setActionId(sid[i]);
				stne.setDesc(sname[i]);
				stne.setTid(stid[i]);
				stne.setPid(spid[i]);
				slist.add(stne);
			}
			tne.setSubNodes(slist);
		}
		try{
			String channeldir = ChannelTreeManager.getInstance().saveSecondNodes(tne);
			if(channeldir == null || channeldir.equals(""))
				ret = "failure";
			else
				request.setAttribute("channeltree", channeldir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			LOG.error("ItemAction save error . " + errors);
			ret = "failure";
		}
		
		return mapping.findForward(ret);
	}
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String ret = "success";
		ActionErrors errors = new ActionErrors();
		Authentication auth = null;
		try{
	        auth = AuthenticationFactory.getAuthentication(request,response);
	        if (null == auth) {
	        	errors.add("auth.failure", new ActionError("auth.failure"));
			 	ret = "failure";
	        }
		}catch(UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		
		StringBuffer errorsb = new StringBuffer();
		
		BaseForm dForm = (BaseForm) form;
		//���ڵ���Ϣ
		TreeNodeEntity tne = new TreeNodeEntity();
		tne.setActionId((String) dForm.get("id"));
		tne.setDesc((String)dForm.get("desc"));
		tne.setTid((String)dForm.get("tid"));
		tne.setPid((String) dForm.get("pid"));

		//�û���ӽڵ�
		String ceshi = (String)dForm.get("id");
		
		String sid[] = request.getParameterValues("sid");
		String sname[] = request.getParameterValues("sname");
		String spid[] = request.getParameterValues("spid");
		String stid[] = request.getParameterValues("stid");
		List slist = new ArrayList();
		if(sid!=null && sid.length>0 && sname.length>0 && sid.length==sname.length)
		{
			for(int i = 0; i < sid.length; i++){
				if(sid[i]==null || sid[i].equals(""))
				{
					ret = "failure";
					errorsb.append("��Ŀid����Ϊ�գ�");
					break;
				}
				TreeNodeEntity stne = new TreeNodeEntity();
				stne.setActionId(sid[i]);
				stne.setDesc(sname[i]);
				stne.setTid(stid[i]);
//				stne.setTid((String)dForm.get("tid"));
				stne.setPid(spid[i]);
				slist.add(stne);
			}
			tne.setSubNodes(slist);
		}

		if(errorsb==null || errorsb.equals("") || errorsb.length()<1){
			try {
//				String path = this.getServlet().getServletContext().getRealPath("/");
				String flag = ChannelTreeManager.getInstance().updateTreeItem(tne, auth.getChannelList());
				if(flag == null || flag.equals(""))
					ret = "failure";
				else
					request.setAttribute("channeltree", flag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			errors.add("errors.channeltree.save", new ActionError("errors.channeltree.save"));
			ret = "success";
		}
		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			LOG.error("ItemAction save error . " + errors);
			ret = "failure";
		}
		
		return mapping.findForward(ret);
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String tid = request.getParameter("tid");
		System.out.println("request tid: " + tid);
//		String path = this.getServlet().getServletContext().getRealPath("/");
		Authentication auth = null;
		ActionErrors errors = new ActionErrors();
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException e) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		TreeNodeEntity tne = ChannelTreeManager.getInstance().getTreeItems(tid, auth.getChannelList());
		
		BaseForm dForm = (BaseForm) form;
		if(tne == null || tne.equals(""))
			return mapping.findForward("falure");
//		if(desc!=null && desc.indexOf("��ҳ")!=-1)
//			request.setAttribute("flag", 111);

//		dForm.set("snodelist", list);
		System.out.println("tid:" + tne.getTid()+" desc:" + tne.getDesc() + " snodelist:" + tne.getSubNodes());
		dForm.set("tid", tne.getTid());
		dForm.set("id", tne.getActionId());
		dForm.set("pid", tne.getPid());
		dForm.set("desc", tne.getDesc());
		dForm.set("snodelist", tne.getSubNodes());
		
		return mapping.findForward("list");
	}
}
