package net.pusuo.cms.client.action;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.*;
import com.hexun.cms.Global;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

public class NewsPushAction extends EntityAction {
	
	private static final Log log = LogFactory.getLog(NewsPushAction.class);
	
	public String retrievePermission() {
		return "news";
	}

	/*
	public ActionForward save(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
                ActionForward ret = null;
                ActionErrors errors = new ActionErrors();
                Authentication auth = null;
                
		try{
                        auth = AuthenticationFactory.getAuthentication(request,response);
                }catch(UnauthenticatedException ue){
                        errors.add("auth.failure", new ActionError("auth.failure"));
                        saveErrors(request, errors);
			return mapping.findForward("failure");
                }

                try{
                        BaseForm dForm = (BaseForm)form;

			System.out.println("============ push ==============");

			//�ж��Ƿ������Ʒ�Χ��
			long t1 = System.currentTimeMillis();
			News _news = (News)ItemManager.getInstance().get( (Integer)dForm.get("id") , News.class);
			String record = _news.getPushrecord();
			if ( record != null && !"".equals(record) ) {
				String[] records = record.split(Global.CMSSEP);
                        	if (records.length >= 10) {
                                	errors.add("errors.push.limited",new ActionError("errors.push.limited"));
                                 	saveErrors(request, errors);
                                 	return mapping.findForward("failure");
                        	}
			}
			long t2 = System.currentTimeMillis();
			System.out.println(t2-t1);

			//�������
			String itemType = ((String)dForm.get("itemtype")).trim();
			News news = (News)ItemInfo.getItemByType(itemType);
			news.setType( (new Integer(itemType)).intValue() );
			news.setChannel( ((Integer)dForm.get("channel")).intValue() );
			String template = (String)dForm.get("template");
			template = template.substring( 0,template.indexOf(Global.CMSCOMMA) );
			int pid = -1;
			String pname = (String)dForm.get("pname");
			if ( null!=pname && !"".equals(pname) ){
				EntityItem item = (EntityItem)ItemManager.getInstance().getItemByName(pname,EntityItem.class);
				if ( null!=item && item.getId()>0 ){
					if (item.getType() != ItemInfo.SUBJECT_TYPE) {
						errors.add("errors.push.pid.notsubject", new ActionError("errors.push.pid.notsubject"));
                                                saveErrors(request, errors);
                                                return mapping.findForward("failure");
					}
					if ( item.getId() == _news.getPid() ) {
						errors.add("errors.push.pid.self", new ActionError("errors.push.pid.self"));
                                        	saveErrors(request, errors);
                                        	return mapping.findForward("failure");
					}
					pid = item.getId();
				} else {
					errors.add("errors.push.pid.notfound", new ActionError("errors.push.pid.notfound"));
					saveErrors(request, errors);
                        		return mapping.findForward("failure");
				}
				if ( ((EntityItem)item).getChannel() != ((Integer)dForm.get("channel")).intValue() ) {
					errors.add("errors.push.pid.notchannel",new ActionError("errors.push.pid.notchannel"));
					saveErrors(request, errors);
					return mapping.findForward("failure");
				}
				
			}
			long t3 = System.currentTimeMillis();
			System.out.println(t3-t2);
			news.setPid(pid);
			news.setDesc((String)dForm.get("desc"));
			news.setPriority( ((Integer)dForm.get("priority")).intValue() );
			news.setReferid( ((Integer)dForm.get("id")).intValue() );
			news.setEditor(auth.getUserID());
			news.setTemplate( template );
                        Item item = ItemManager.getInstance().update(news);
			long t4 = System.currentTimeMillis();
			System.out.println("t4 -t3 = " +  (t4-t3) );

			//����ʱ��
			((News)item).setTime((java.sql.Timestamp)dForm.get("time"));
			//((News)item).setText("");
			ItemManager.getInstance().update(item);

			//��¼����
			String history = String.valueOf(item.getId());
			if (null != record) {
				history = record + Global.CMSSEP + history;
			}
			System.out.println(history);
                        _news.setPushrecord(history);
                        //_news.setText("");
			_news = (News) ItemManager.getInstance().update(_news);
			long t5 = System.currentTimeMillis();
			System.out.println(t5-t4);
	
			//dForm.initialize(mapping);
			
                }catch(Exception e){
                        errors.add("errors.item.save", new ActionError("errors.item.save"));
                        log.error("NewsPushAction save error . "+e.toString());
                }

                // Report any errors we have discovered back to the failure page
                if (!errors.isEmpty()) {
                        saveErrors(request, errors);
                        ret = mapping.findForward("failure");
                } else {
                        ret = mapping.findForward("success");
                }
                return ret;
        }	

	*/

}
