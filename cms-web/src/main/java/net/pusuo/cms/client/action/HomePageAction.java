package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.Permission;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;

public class HomePageAction extends HomePageAdminAction {

	private static final Log HP_LOG = LogFactory.getLog(HomePageAction.class);

	public ActionForward list(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) {


		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		Authentication auth = null;
		try{
			auth = AuthenticationFactory.getAuthentication(request,response);
		}catch(UnauthenticatedException ue){
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		try {
			//���ϵͳ����������ΪHOMEPAGE��ʵ��
			List hpList = ItemUtil.getEntityChildren(-1, ItemInfo.HOMEPAGE_TYPE);

			List channels = auth.getChannelList();//�û�����Ƶ��

			// ��Chanel��id���������У��Ա����
			Channel citem = null;
			int cId = 0;
			int idx = -1;
			int[] channelIds = new int[channels.size()];
			Iterator hpc = channels.iterator();
			
			for(int i = 0; hpc.hasNext(); i++){
				citem = (Channel)hpc.next();
				if(citem != null && (cId = citem.getId()) > 0)
					channelIds[i] = cId;
			}

			Arrays.sort(channelIds);

			List list = new ArrayList();//��view��ʹ��

			Iterator hpi = hpList.iterator();
			EntityItem hItem = null;

            		while (hpi.hasNext()) {
		                hItem = (EntityItem)hpi.next();
                		if(hItem != null && hItem.getId() > 0){
                        		list.add(hItem);
                		}
            		}

			request.setAttribute("list",list);

		} catch(Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			HP_LOG.error("ItemAction list error . " + e.toString());
		}
		return mapping.findForward("list");	
	}

	
	public ActionForward view(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		Integer id = (Integer)dForm.get("id");
		int _id = id.intValue();

		if (_id <= 0) {	
			// this action have not create perm
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		return super.view(mapping, form, request, response);
	}

}
