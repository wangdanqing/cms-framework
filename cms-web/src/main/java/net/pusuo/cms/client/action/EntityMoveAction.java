package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.search.util.SearchUtils;

public class EntityMoveAction extends BaseAction
{
	private static final Log log = LogFactory.getLog( EntityMoveAction.class );

	public static final String NEWS_LIST = "com.hexun.cms.client.action.NEWSLIST";
	public static final String NEWS_COUNT = "com.hexun.cms.client.action.NEWSCOUNT";
	public static final String NEWS_COST = "com.hexun.cms.client.action.NEWSCOST";
	private static final String ENTITY_MOVE_REPORT = "com.hexun.cms.client.action.ENTITYMOVEREPORT";
	
	public ActionForward view(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
	{
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm)form;

		Authentication auth = null;
		try
		{
			auth = AuthenticationFactory.getAuthentication(request, response);
		}catch(UnauthenticatedException ue)
		{
                        errors.add("auth.failure", new ActionError("auth.failure"));
                        saveErrors(request, errors);
                        return mapping.findForward("failure");
		}
	
		return mapping.findForward("view");
	}

	public ActionForward list(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
	{
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm)form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		}
		catch(UnauthenticatedException ue) {
            errors.add("auth.failure", new ActionError("auth.failure"));
            saveErrors(request, errors);
            return mapping.findForward("failure");
		}
	
		String keyword = ((String)_form.get("keyword")).trim();
		int subjectId = -1;
		String pname = ((String)_form.get("pname")).trim();
		if(!StringUtils.isEmpty(pname)&&!StringUtils.isEmpty(pname))
		{
			EntityItem pitem = (EntityItem)ItemManager.getInstance().getItemByName(pname, EntityItem.class);
			subjectId = pitem.getId();
		}
		int type = ((Integer)_form.get("type")).intValue();
		int page = ((Integer)_form.get("page")).intValue();
		int range = ((Integer)_form.get("range")).intValue();
		int media = -1;//((Integer)_form.get("media")).intValue();
		
		String medianame = ((String)_form.get("medianame")).trim();
		if(!StringUtils.isEmpty(medianame)&& !StringUtils.isBlank(medianame) && !medianame.equals("-1")&&media==-1)
		{
			Media mitem = (Media)ItemManager.getInstance().getItemByName(medianame, Media.class);
			if(mitem!=null && !mitem.equals(""))
			{
				media = mitem.getId();
			}
		}
		String author = ((String)_form.get("author")).trim();
		
		Long cost = new Long(0);
		Integer count = new Integer(0);
        List list = new ArrayList();
        
		try
		{
	        Query queryUser = null;
	        if (keyword != null && keyword.trim().length() != 0 && !keyword.trim().startsWith("*")) {
	            QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_DESC, new StandardAnalyzer());
	            queryParser.setOperator(QueryParser.AND);
	            queryUser = queryParser.parse(keyword);
	        }
	        
	        TermQuery querySubject = null;
	        if (subjectId > 0) {
	            Term termSubject = new Term(CmsEntry.FIELD_NAME_PID, subjectId + "");
	            querySubject = new TermQuery(termSubject);
	        }
	        
			TermQuery queryType = null;
			if (type > 0) {
			    Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, type + "");
			    queryType = new TermQuery(termType);
			}
			
			TermQuery queryMedia = null;
			if (media > 0) {
			    Term termType = new Term(CmsEntry.FIELD_NAME_MEDIA, media + "");
			    queryMedia = new TermQuery(termType);
			}
			
//			TermQuery queryAuthor = null;
//			if (author!=null && !author.equals("")) {
//			    Term termType = new Term(CmsEntry.FIELD_NAME_AUTHOR, author + "");
//			    queryAuthor = new TermQuery(termType);
//			}
			Query queryAuthor = null;
	        if (author != null && author.trim().length() != 0) {
	            QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_AUTHOR, new StandardAnalyzer());
	            queryParser.setOperator(QueryParser.AND);
	            queryAuthor = queryParser.parse(author);
	        }
	        
	        BooleanQuery query = new BooleanQuery();
	        if (queryUser != null)
	            query.add(queryUser, true, false);
	        if (querySubject != null)
	            query.add(querySubject, true, false);
	        if (queryType != null)
	            query.add(queryType, true, false);
	        if(queryMedia!=null)
	        	query.add(queryMedia,true,false);
	        if(queryAuthor!=null)
	        	query.add(queryAuthor,true,false);
	        
	        
		    Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_TIME, SortField.FLOAT, true));
		    
            int fromId = (page - 1) * range;
            
            Map result = SearchClient.getInstance().getSearchManager().search(query, sort, fromId, range);
            
            if (result != null) {
                if (result.containsKey(SearchUtils.SEARCH_RESULT_COST))
                    cost = (Long)result.get(SearchUtils.SEARCH_RESULT_COST);
                
                if (result.containsKey(SearchUtils.SEARCH_RESULT_COUNT))
                    count = (Integer)result.get(SearchUtils.SEARCH_RESULT_COUNT);
                
                if (result.containsKey(SearchUtils.SEARCH_RESULT_LIST))
                    list = (List)result.get(SearchUtils.SEARCH_RESULT_LIST);
            }
		}catch(Exception e) {
			log.error("entitymove view exception. ", e);
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
		
		request.setAttribute(NEWS_COST, cost); 
		request.setAttribute(NEWS_COUNT, count);
		request.setAttribute(NEWS_LIST, list);
		request.setAttribute("newpname", _form.get("newpname").toString());
		
		return mapping.findForward("view");
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
	{
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();

		BaseForm _form = (BaseForm)form;
		
		Authentication auth = null;
		try
		{
			auth = AuthenticationFactory.getAuthentication(request, response);
		}catch(UnauthenticatedException ue)
		{
                        errors.add("auth.failure", new ActionError("auth.failure"));
                        saveErrors(request, errors);
                        return mapping.findForward("failure"); 
		}

		String newPName = (String)_form.get("newpname");

		try
		{
			EntityItem newPItem = (EntityItem) ItemManager.getInstance().getItemByName(newPName, EntityItem.class);
			if( newPItem==null )
			{
				log.error("entity not found by "+newPName+".");
				errors.add("errors.entitymove", new ActionError("errors.entitymove.itemnotfoundbyname", newPName));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			if( newPItem.getType()!=ItemInfo.SUBJECT_TYPE )
			{
				log.error("invalid pid. "+newPName);
				errors.add("errors.entitymove", new ActionError("errors.entitymove.invalidpid", newPName));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			int newPid = newPItem.getId();

			String[] eIds = (String[])_form.get("eids");

			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			String pushMode = request.getParameter("pushMode");
			Map extend = new HashMap();
			//Ĭ�������ӷ�ʽ
			int themode = NewsManager.PROPERTY_NAME_PUSH_MODE_LINK;
			if (!StringUtils.isBlank(pushMode) && !StringUtils.isEmpty(pushMode) && "1".equalsIgnoreCase(pushMode)) {
				themode = NewsManager.PROPERTY_NAME_PUSH_MODE_COPY;
			}				
			extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);
			extend.put(NewsManager.PROPERTY_NAME_PUSH_MODE,	new Integer(themode));
			
			int count1 = 0, count2 = 0;
			List pushedList = new ArrayList();
			for(int i=0; i<eIds.length; i++)
			{
				EntityItem eItem = (EntityItem)ItemManager.getInstance().get(new Integer(eIds[i]), EntityItem.class);
				if( eItem==null )
				{
					++count2;
					sb2.append( eIds[i]+"<br>" );
					log.error("entity not found "+eIds[i]+".");
					errors.add( "errors.entitymove", new ActionError("errors.entitymove.itemnotfoundbyid", ""+eIds[i]) );
					continue;
				}
				
				if (eItem instanceof News)				
				{
					News pushednews = ManagerFacade.getNewsManager().pushNews((News)eItem, newPid, extend);
					pushedList.add(pushednews);
					System.out.println("title:"+ pushednews.getDesc()+"||id:"+ pushednews.getId()+"||pid:"+pushednews.getPid());
				    ++count1;				
					sb1.append( eIds[i]+"<br>" );			
				}
			}
			if(pushedList.size()>0)
				request.setAttribute("pushedList", pushedList);
			saveErrors(request, errors);
			saveMessages(request, messages);
			if( !errors.empty() )
			{
				return mapping.findForward("failure");
			}

			// report
			if( sb1.length()>0 )
			{
				sb1.insert(0, count1 + " entity moved success.<br>");
			}
			if( sb2.length()>0 )
			{
				sb2.insert(0, count2 + " entity moved failure.<br>");
			}

			sb1.append("<br><br>");
			sb1.append(sb2);
			request.setAttribute("com.hexun.cms.client.action.ENTITYMOVEREPORT", sb1.toString());
		} catch(Exception e) {
			log.error("entitymove save exception. ", e);
			errors.add("errors.entitymove", new ActionError("errors.entitymove.save", newPName, e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("view");
		}
		return mapping.findForward("report");
	}
}
