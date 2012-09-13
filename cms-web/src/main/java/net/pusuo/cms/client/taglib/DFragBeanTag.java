package net.pusuo.cms.client.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.ServletRequest;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;

import com.hexun.cms.core.*;
import com.hexun.cms.util.Util;

public class DFragBeanTag extends TagSupport
{
	private static final Log log = LogFactory.getLog(DFragBeanTag.class);

	private String id = "";
	private int view = -1;
	private String property = "";
	private EntityItem entity = null;

	public int doStartTag() throws JspException
	{
		int ret = SKIP_BODY;
		try
		{
			ServletRequest request = pageContext.getRequest();
			if ( view==-1 )
			{
				// default view , display some frag info
				return ret;
			}
			if ( view==FragTag.PRECOMPILE_VIEW )
			{
				// pre compile
				return ret;
			}
			if ( view==FragTag.PRE_VIEW || view==FragTag.FRAG_VIEW || view==FragTag.COMPILE_VIEW || view==FragTag.LIST_VIEW )
			{
				// DFragBeanTag ���������uet=3, ������Ŀ�б�����
				entity = (EntityItem)pageContext.getAttribute(id);
				if( entity==null )
				{
					log.warn("DFragBeanTag --> entity is null.");
					return ret;
				}

				Object obj = RequestUtils.lookup(pageContext, id, property, null);
				if ( obj!=null )
				{
					if("url".equals(property)){
						String url = (String)obj;
						if ( entity.getType() == EntityItem.SUBJECT_TYPE && url.endsWith("index.html")) {
							url = url.substring(0, url.lastIndexOf("index.html"));
							obj = url;
						}
					}
					ResponseUtils.write(pageContext,obj.toString());
				} else {
					// error
					if( view==FragTag.COMPILE_VIEW )
					{
						ResponseUtils.write(pageContext,"");
					} else {
						ResponseUtils.write(pageContext,"not found property["+property+"]");
					}
				}
			}
		} catch ( Exception e ) {
			log.error("DFragBeanTag Exception : "+e.toString());
			//throw new JspTagException("DFragBeanTag Exception : "+e.toString());
		}
		return ret;
	}

	public int doEndTag() throws JspException
	{
		// clean up
		return (EVAL_PAGE);
	}

	public String getId()
	{
		return this.id;
	}

	public int getView()
	{
		return this.view;
	}

	public String getProperty()
	{
		return this.property;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public void setView( int view )
	{
		this.view = view;
	}

	public void setProperty( String property )
	{
		this.property = property;
	}

	private static final long ct()
	{
		return System.currentTimeMillis();
	}
}
