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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.Global;
import com.hexun.cms.core.*;
import com.hexun.cms.client.file.*;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.*;
import com.hexun.cms.search.util.SearchUtils;

public class NewsRelativeAction extends BaseAction {
	
	private static final Log LOG = LogFactory.getLog(NewsRelativeAction.class);
	
	public String retrievePermission() {
                return "news";
        }

	//���������ҳ��
	public ActionForward view(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		Integer id = ((Integer)dForm.get("id"));
		News item = null;

		try{
			if ( id != null ) {
				//������ҳ������ȡ����������
				
                                item =(News)ItemManager.getInstance().get(id,ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));

				String newsRelative = item.getRelativenews();
				request.setAttribute("newsresults",newsRelative);
				ret =  mapping.findForward("relativeNews");
			} else { // error
				errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				ret = mapping.findForward("failure");
			}
		}catch(Exception e){
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("NewsRelativeAction view error . "+e.toString());
		}

		return ret;
	}	
/**
*****************************
*  save relative news col	
*****************************
*/

	public ActionForward save (ActionMapping mapping,
				   ActionForm form,
				   HttpServletRequest request,
				   HttpServletResponse response) 
	{
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		Authentication auth = null;
		BaseForm relativeForm = (BaseForm)form;
		News item = null;

		try {
                	auth = AuthenticationFactory.getAuthentication(request, response);
		}catch (UnauthenticatedException ue){
                	errors.add("auth.failure", new ActionError("auth.failure"));
                	return mapping.findForward("failure");
        	}		

		try {

			int newsId = -1;
			newsId = ((Integer)relativeForm.get("id")).intValue();
			String relationContent = (String)relativeForm.get("relationContent");
			if (newsId > 0) {
				// get The NewsItem
                        	item =(News)ItemManager.getInstance().get(new Integer(newsId),ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
			}
			String relative_news = (String)relativeForm.get("relationContent");
			relative_news = toID(relative_news);
			//relative_news = Content;
			item.setRelativenews(relative_news);
			item = (News)ItemManager.getInstance().update(item);
			
			request.setAttribute("newsresults",relative_news);

		}catch(Exception e)
		{
                	errors.add("errors.item.save", new ActionError("errors.item.save"));
                	LOG.error("PictureAction save error . " + e.toString());		
		}
		return ret=mapping.findForward("relativeNews");


	}
	//���ҳ�洫�ݵĲ��������������
	public ActionForward searchRelative(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		
		BaseForm dForm = (BaseForm)form;
		Integer id = ((Integer)dForm.get("id"));
		String keyword = ((String)dForm.get("keyword")).trim();
		int listNum = ((Integer)dForm.get("listNum")).intValue();

		try{
		    String results = "";
		    
		    if (keyword != null && keyword.trim().length() > 0 && !keyword.trim().startsWith("*")) {
			    keyword = keyword.trim();
			    
			    News item = (News)ItemManager.getInstance().get(id,News.class);
			    int channel = item.getChannel();
			    
		        TermQuery queryChannel = null;
		        if (channel > 0) {
		            Term termChannel = new Term(CmsEntry.FIELD_NAME_CHANNEL, channel + "");
		            queryChannel = new TermQuery(termChannel);
		        }
		        
		        Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, "2");
		        TermQuery queryType = new TermQuery(termType);
		        
		        Term termStatus = new Term(CmsEntry.FIELD_NAME_STATUS, "2");
		        TermQuery queryStatus = new TermQuery(termStatus);
		        
		        QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_DESC, new StandardAnalyzer());
		        queryParser.setOperator(QueryParser.AND);
		        Query queryUser = queryParser.parse(keyword); 
		        
		        BooleanQuery query = new BooleanQuery();
		        query.add(queryUser, true, false);
			query.add(queryChannel, true, false);
		        query.add(queryType, true, false);
		        query.add(queryStatus, true, false);
		        
	            	Map result = SearchClient.getInstance().getSearchManager().search(query, 0, listNum);

				List list = null;
				
				if (result != null && result.containsKey(SearchUtils.SEARCH_RESULT_LIST)) {
				    list = (List)result.get(SearchUtils.SEARCH_RESULT_LIST);
				}
				
				if (list != null) {
					for (int i  = 0; i < list.size(); i++) {
						Entry entry = (Entry)list.get(i);
						if (entry != null)
						    results += entry.getId() + ";";				        
					}
				}		        
		    }
			    	
			results = ItemUtil.toHref(results);
			
			dForm.set("id",id);
			dForm.set("keyword",keyword);
			dForm.set("listNum",new Integer(listNum));
			request.setAttribute("searchresults",results);

			String newsRelative = ((String)dForm.get("relationContent")).trim();
            		request.setAttribute("newsresults",newsRelative);
			
			ret =  mapping.findForward("relativeNews");
		}catch(Exception e){
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("NewsRelativeAction search error . "+e.toString());
		}

		return ret;
	}	


	private  String toID(String idStr) throws Exception
        {
                StringBuffer retHref =new StringBuffer();
                if(idStr != null && !"".equals(idStr))
                {
                        StringTokenizer st = new StringTokenizer(idStr,"::");
                        while(st.hasMoreElements())
                        {
                                String ee = st.nextToken();
                                if(ee.indexOf("||") >0)
                                        retHref.append(ee.substring(0,ee.indexOf("||"))).append(";");
                        }
                }
                String ret = retHref.toString();
                if(ret.length() >0)
                        ret = ret.substring(0,ret.length()-1);
                return ret;

        }
	
	//�������������д������ҳ��
	public ActionForward writeRelative(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		String id = ((String)dForm.get("id")).trim();
		String relationContent = ((String)dForm.get("relationContent")).trim();

		try{
			if (null != id && !("-1".equals(id))) {
				//������ҳ������ȡ����������
				//String newsContent = ClientFile.read();
				String newsContent = "<NewsRelativeTag><li class=relationNews><A href=\"http://it.sohu.com/2004/06/14/45/article220524520.shtml\" target=_blank>aaaaaaaa</A><FONT color=#828282 size=1>(06/14 12:19)</FONT></li><li class=relationNews><A href=\"http://it.sohu.com/2004/06/14/45/article220524519.shtml\" target=_blank>bbbbbbb</A><FONT color=#828282 size=1>(06/14 12:19)</FONT></li></NewsRelativeTag>xiangujanafter";
				int beingPos = newsContent.indexOf("<NewsRelativeTag>");
				int endPos = newsContent.indexOf("</NewsRelativeTag>");
				if(beingPos>0&&endPos>0){
					newsContent = newsContent.substring(0,beingPos+17)+relationContent+newsContent.substring(endPos);
					//ClientFile.wirte();
				}
				
				response.sendRedirect("newsRelative.do?method=view&id="+id);
			} else { // error
				errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				ret = mapping.findForward("failure");
			}
		}catch(Exception e){
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("NewsRelativeAction view error . "+e.toString());
		}

		return ret;
	}
	
}
