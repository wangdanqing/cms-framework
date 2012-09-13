/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.core.Template;
import com.hexun.cms.client.util.CookieUtil;
import com.hexun.cms.client.util.CategoryUtil;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TemplateFilterAction extends DispatchAction {

	private static final Log log = LogFactory.getLog(TemplateFilterAction.class);
	
	public ActionForward filter(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		try
		{
		    int category = -1;
		    String keyword = null;
		    
		    String categoryParam = request.getParameter("category");
		    String keywordParam = request.getParameter("keyword");
		    
		    // visit the page first time
		    if (categoryParam == null && keywordParam == null)
		    {
			    Cookie[] cookies = request.getCookies();
			    if (category == -1)
			    {
			        categoryParam = CookieUtil.getCookieValue(cookies, "category", "-1");
			        category = Integer.parseInt(categoryParam);
			    }
			    if (keyword == null)
			    {		 
			        keywordParam = CookieUtil.getCookieValue(cookies, "keyword", "");
			        keyword = CookieUtil.decode(keywordParam);
			    }
		    }
		    // visit the page by "query" action
		    else
		    {
			    if (categoryParam != null)
			        category = Integer.parseInt(categoryParam);
		        keyword = keywordParam;
		        if (keyword == null)
		            keyword = "";
		    }
		    
		    // handle template list
		    List templateList = new ArrayList();
			List templates = (List)request.getAttribute("list");
			if (templates != null)
			{
			    for (int i = 0; i < templates.size(); i++)
			    {
			        Template template = (Template)templates.get(i);
			        // filter invalid one
			        if(template==null)continue;
			        if (template.getStatus() == 0)
			            continue;
			        // filtered by category
			        if (category != -1) { 
			            int acategory = template.getCategory();
			            if (CategoryUtil.isSharedCategory(category)) {
			                if (category != acategory)
			                    continue;
			            }
			            else {			            
			                if (!CategoryUtil.isSharedCategory(acategory) && category != acategory)
			                    continue;
			            }
			        }
			        // filtered by keyword
			        if (keyword != null && keyword.trim().length() != 0)
			        {
			            keyword = keyword.trim();
			            if (template.getDesc().indexOf(keyword) == -1 &&
			                template.getName().indexOf(keyword) == -1)
			                continue;
			        }
			        // now it is something we are finding
			        templateList.add(template);
			    }
			}
				
			// handle category list
			List categoryList = ItemManager.getInstance()
				.getList( ItemInfo.getItemClass(ItemInfo.CATEGORY_TYPE) );
			if (categoryList == null)
			    categoryList = new ArrayList();
			
			// write parameter into request
			request.setAttribute("categorylist", categoryList);
			request.setAttribute("templatelist", templateList);
			request.setAttribute("category", "" + category);
			request.setAttribute("keyword", keyword);
			
			// write parameter into cookie
			int maxAge = 365 * 24 * 60 * 60;
			
		    Cookie cookieCategory = new Cookie("category", "" + category);
		    cookieCategory.setMaxAge(maxAge);
		    response.addCookie(cookieCategory);
		    
		    Cookie cookieKeyword = new Cookie("keyword", 
		            CookieUtil.encode(keyword == null ? "" : keyword));
		    cookieKeyword.setMaxAge(maxAge);
		    response.addCookie(cookieKeyword);
		}
		catch (Exception e)
		{
			log.error(e);
			errors.add( "errors.templatelib", new ActionError("errors.templatelib.list",e.toString()) );
			saveErrors( request, errors );
			return mapping.findForward("failure");		    
		}

	    return mapping.findForward("list");
	}
 
}
