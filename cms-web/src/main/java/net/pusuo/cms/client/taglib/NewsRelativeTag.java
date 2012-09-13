package net.pusuo.cms.client.taglib;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.ResponseUtils;

import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

public class NewsRelativeTag extends TagSupport
{
	private static final Log log = LogFactory.getLog(NewsRelativeTag.class);

	private int view = -1;


	public int doStartTag() throws JspException
	{
		try
		{
			ServletRequest request = pageContext.getRequest();
			if( view<0 )
			{
				// do nothing
			}
			
			if( view==FragTag.PRE_VIEW || view==FragTag.FRAG_VIEW || view==FragTag.COMPILE_VIEW )
			{
				EntityItem entity = (EntityItem)request.getAttribute( FragTag.ENTITY_KEY );
				News news = (News)entity;
				String rela = news.getRelativenews();
				if( rela==null || rela.equals("") )
				{
					return SKIP_BODY;
				}
				StringBuffer sb = new StringBuffer();
				String[] relativeNews = rela.split(Global.CMSSEP);
				for(int i=0; relativeNews!=null && i<relativeNews.length; i++)
				{
					int i_id = -1;
					try
					{
						i_id = Integer.parseInt( relativeNews[i].trim() );
					} catch(Exception e) {
						log.error("invalid relative news.id "+String.valueOf(relativeNews[i].trim()));
						continue;
					}
					EntityItem eItem = (EntityItem)ItemManager.getInstance().get(new Integer(i_id), EntityItem.class);
					if( eItem!=null )
					{
						sb.append("<h2>")
						  .append("<a href=" + eItem.getUrl())
						  .append(" title=\"" + Util.RemoveHTML(eItem.getDesc()).replaceAll("\"|“|”|'|‘|’|＂", " "))
						  .append("\">" + eItem.getDesc() + "</a></h2>\n" );
					}
				}
				if( sb.length()>0 )
				{
					ResponseUtils.write( pageContext, sb.toString() );
				}
			}
		} catch(Exception e) {
			log.error("newsrelative doStart exception "+e.toString());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}

	public void setView( int view )
	{
		this.view = view;
	}
	public int getView()
	{
		return this.view;
	}
}

