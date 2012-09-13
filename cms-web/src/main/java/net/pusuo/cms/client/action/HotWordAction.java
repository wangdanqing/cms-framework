package net.pusuo.cms.client.action;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;

import com.hexun.cms.tool.*;
import com.hexun.cms.client.tool.HWClient;
import com.hexun.cms.util.Util;

public class HotWordAction extends BaseAction {

	private static final int MAX_HOT_WORDS = 3000;
	private static final Log LOG = LogFactory.getLog(HotWordAction.class);
	
	private static final int GLOBAL_HW_CHANNEL = 109;

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer channel = (Integer) dForm.get("channel");
		int channelID = channel.intValue();
		List list = new ArrayList();
		try {
			if( channelID>0 ){
				list = (List)HWClient.getInstance().list( channelID );
			}
		} catch (Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			saveErrors(request, errors);
			LOG.error("HotWordAction list error . " + e.toString());
		}
		if(list!=null){
			request.setAttribute("list", list);
		}else{
			request.setAttribute("list", new ArrayList());
		}
		return mapping.findForward("list");
	}

	public ActionForward autoComplete(ActionMapping mapping, 
			ActionForm form, 
			HttpServletRequest request,
			HttpServletResponse response) {
		
		ActionErrors errors = new ActionErrors();
		
		BaseForm dForm = (BaseForm) form;
		Integer channel = (Integer) dForm.get("channel");
		int channelID = channel.intValue();
		String hotword = (String)dForm.get("hotword");
		boolean blogChecked = ((Boolean)dForm.get("blog")).booleanValue();
		boolean newsChecked = ((Boolean)dForm.get("news")).booleanValue();
		boolean musicChecked = ((Boolean)dForm.get("music")).booleanValue();
		boolean speakChecked = ((Boolean)dForm.get("speak")).booleanValue();
		
		List list = new ArrayList();
		try {
			if( channelID>0 ){
				list = (List)HWClient.getInstance().list( channelID );
			}
			
			if (hotword != null && hotword.trim().length() > 0) {
				hotword = hotword.trim();
				String encodedHotword = java.net.URLEncoder.encode(hotword, "UTF-8");
				
				StringBuffer content = new StringBuffer();		
				content.append("<span class=articleLink>(");
				
				boolean checked = false;			
				if (blogChecked) {
					content.append("<a href=\"http://www.sogou.com/web?query=" + 
							encodedHotword + java.net.URLEncoder.encode(Util.unicodeToGBK(" �Ѻ��"), "GBK") + 
							"\" target=_blank>" + hotword + Util.unicodeToGBK("����") + "</a>,");
					checked = true;
				}
				if (newsChecked) {
					content.append("<a href=\"http://news.sogou.com/news?query=" + 
							encodedHotword + "&sort=0\" target=_blank>" + hotword + Util.unicodeToGBK("����") + "</a>,");
					checked = true;
				}
				if (musicChecked) {
					content.append("<a href=\"http://d.sogou.com/music.so?query=" + 
							encodedHotword + "\" target=_blank>" + hotword + Util.unicodeToGBK("����") + "</a>,");
					checked = true;
				}
				if (speakChecked) {
					content.append("<a href=\"http://s.sogou.com/say?md=listTopics&name=" + 
							encodedHotword + "\" target=_blank>" + hotword + Util.unicodeToGBK("˵��") + "</a>,");
					checked = true;
				}			
				if (checked) {
					content.delete(content.length() - 1, content.length());
				}
				
				content.append(")</span>");
				
				dForm.set("other", content.toString());
			}
		} 
		catch (Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			saveErrors(request, errors);
			LOG.error("HotWordAction list error . " + e.toString());
		}
		
		if(list != null){
			request.setAttribute("list", list);
		}
		else{
			request.setAttribute("list", new ArrayList());
		}
		
		return mapping.findForward("list");
	}	

	public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		try {
			Integer channel = (Integer) dForm.get("channel");
			int channelID = channel.intValue();
			String hotword = (String) dForm.get("hotword");
			String url = (String) dForm.get("url");
			String other = (String) dForm.get("other");			
			Collection hwList = HWClient.getInstance().list(channelID);
			
			//�жϵ�ǰ���ȴ������Ƿ��Ѿ���������
			if(hwList!=null && hwList.size()>MAX_HOT_WORDS){
				errors.add("errors.item.toomany", new ActionError("errors.item.toomany"));
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
				return ret;
			}
			hwList = null;
			//���ж�ȫƵ���ȴ��Ƿ��Ѿ�������ȴ���
			HotWordItem ehwi = HWClient.getInstance().get( hotword,GLOBAL_HW_CHANNEL);			
			
			if(ehwi == null){
				//���ȫƵ���ȴ�û������ȴ�,���жϱ�Ƶ���Ƿ��Ѿ�������ȴ���
				ehwi = HWClient.getInstance().get(hotword,channelID);
			}			
			if( ehwi!=null ){
				errors.add("errors.item.existed", new ActionError("errors.item.existed"));
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			}else{
				if(channelID>0){
					HotWordItem hwi = new HotWordItem();
					hwi.setKw( hotword.replaceAll("&","&#38;") );
					hwi.setUrl( url.replaceAll("&","&#38;") );
					hwi.setOther( other.replaceAll("&","&#38;") );
					HWClient.getInstance().add(hwi,channelID);
				}

				response.sendRedirect("hotword.do?method=list&channel="+channelID);
			}
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
			LOG.error("HotWord Action save error . " + e.toString());
		}
		
		return ret;
	}

	public ActionForward modify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
				HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		try {
			Integer channel = (Integer) dForm.get("channel");
			int channelID = channel.intValue();
			String hotwordtag = (String) dForm.get("hotwordtag");
			HotWordItem hwi = HWClient.getInstance().get( hotwordtag,channelID );
			//if(hwi!=null&&!(hwi.getKw()).equals("")){
			if(hwi!=null){
				dForm.set("hotwordtag",hotwordtag);
				dForm.set("channel",channel);
				dForm.set("hotword",hwi.getKw());
				dForm.set("url",hwi.getUrl());
				dForm.set("other",hwi.getOther());
			
				ret = mapping.findForward("modify");
			}else{
				errors.add("errors.item.view", new ActionError("errors.item.view"));
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			}

		} catch (Exception e) {
				errors.add("errors.item.list", new ActionError("errors.item.list"));
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
				LOG.error("ItemAction modify error . " + e.toString());
		}

		return ret;
	}

	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
				HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		try {
			Integer channel = (Integer) dForm.get("channel");
			int channelID = channel.intValue();
			String oldhotword = (String) dForm.get("hotwordtag");
			String hotword = (String) dForm.get("hotword");
			String url = (String) dForm.get("url");
			String other = (String) dForm.get("other");
			if( !oldhotword.equals(hotword) ){
				HotWordItem ehwi = HWClient.getInstance().get( hotword,GLOBAL_HW_CHANNEL);			
				if(ehwi == null){
					ehwi = HWClient.getInstance().get( hotword,channelID );
				}
				if( ehwi!=null&&!(ehwi.getKw()).equals("") ){
					errors.add("errors.item.existed", new ActionError("errors.item.existed"));
					saveErrors(request, errors);
					ret = mapping.findForward("failure");
					return ret;
				}
			}
			
			HotWordItem ohwi = HWClient.getInstance().get( oldhotword,channelID );
			if(ohwi!=null){
				HotWordItem nhwi = new HotWordItem();
				nhwi.setKw( hotword.replaceAll("&","&#38;") );
				nhwi.setUrl( url.replaceAll("&","&#38;") );
				nhwi.setOther( other.replaceAll("&","&#38;") );
				
				HWClient.getInstance().update( ohwi,nhwi,channelID );
			}
					
			response.sendRedirect("hotword.do?method=list&channel="+channelID);

		} catch (Exception e) {
				errors.add("errors.item.save", new ActionError("errors.item.save"));
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
				LOG.error("ItemAction save error . " + e.toString());
		}

		return ret;
	}
	
	public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer channel = (Integer) dForm.get("channel");
		int channelID = channel.intValue();
		String hotwordtag = (String) dForm.get("hotwordtag");

		try {
			HotWordItem hwi = HWClient.getInstance().get( hotwordtag,channelID );
			if( hwi!=null ){
				HWClient.getInstance().delete( hwi,channelID );
			}

			response.sendRedirect("hotword.do?method=list&channel="+channelID);

		} catch (Exception e) {
			errors.add("errors.item.remove", new ActionError("errors.item.remove"));
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
			LOG.error("HotWordAction delete error . " + e.toString());
		}

		return ret;
	}
	
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		String keyword = (String) dForm.get("keyword");
		Integer channel = (Integer) dForm.get("channel");
		int channelID = channel.intValue();
		dForm.set("channel",channel);
		List list = new ArrayList();
		try {
			if( channelID>0 ){
				List clist = (List)HWClient.getInstance().list( channelID );
				Iterator it = clist.iterator();
				HotWordItem hw = new HotWordItem();
				while (it.hasNext()) {
					hw = (HotWordItem)it.next();
					if( hw.getKw().indexOf(keyword)>=0 ){
						list.add( hw );
					}
				}
			}
		} catch (Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			saveErrors(request, errors);
			LOG.error("HotWordAction list error . " + e.toString());
		}
		if(list!=null){
			request.setAttribute("list", list);
		}else{
			request.setAttribute("list", new ArrayList());
		}
		return mapping.findForward("searchlist");
	}
		
}
