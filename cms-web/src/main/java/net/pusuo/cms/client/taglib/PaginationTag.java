package net.pusuo.cms.client.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.ServletRequest;
import org.apache.struts.util.ResponseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import com.hexun.cms.Global;
import com.hexun.cms.Configuration;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.util.Util;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Template;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.ExTFMap;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.client.compile.CompileTask;
import com.hexun.cms.client.util.EmbedGroovy;

public class PaginationTag extends TagSupport
{
	private static final Log log = LogFactory.getLog(PaginationTag.class);

	private Template template;
	private EntityItem entity;

	private int view = -1;

	private static int MAX_SIZE_OF_LISTCACHE = 1000;

	static {
		String maxsize = Configuration.getInstance().get("cache.object.list.maxsize");
		if( maxsize!=null && maxsize.length()>0 )
		{
			try
			{
				MAX_SIZE_OF_LISTCACHE = Integer.parseInt( maxsize );
			}catch(NumberFormatException e) {
				log.error( "invalid cache.object.list.maxsize -- "+maxsize );
			}
		}
	}

	public int doStartTag() throws JspException
	{
		try
		{
			ServletRequest request = pageContext.getRequest();

			template = (Template)request.getAttribute(FragTag.TEMPLATE_KEY);
			entity = (EntityItem)request.getAttribute(FragTag.ENTITY_KEY);
		} catch ( Exception e ) {
			log.error("PaginationTag Exception : ", e);
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		int ret = EVAL_PAGE;

		// ��ǰ���ʵ�ģ��Ϊnull, ˵��request.getAttribute()Ϊ null
		if( template==null )
		{
			log.warn("template is null.");
			return ret;
		}
		if( template.getMpage()!=2 )
		{
			log.warn("template ["+template.getName()+"] mpage is not 2");
			return ret;
		}

		try
		{
			ServletRequest request = pageContext.getRequest();

			// default view , display template name & desc
			if( view==-1 )
			{
				ResponseUtils.write(pageContext,"[paginationfrag ("+template.getName()+" , "+template.getDesc()+")]");
				return ret;
			}

			// ��Ԥ��������������Ҫָ��ʵ��
			if( entity==null )
			{
				log.warn("PaginationTag -- entity is null.");
				return ret;
			}

			// 206507857 for test
			//if( entity.getId()!=206507857 ) return ret;

			Set tfmapSet = template.getTFMaps();
			Iterator tfmapItr = tfmapSet.iterator();
			TFMap tfmap = null;
			while( tfmapItr.hasNext() )
			{
				TFMap l_tfmap = (TFMap)tfmapItr.next();
				if( l_tfmap.getType()!=3 ) continue;
				if( l_tfmap.getUet()!=1 ) continue;
				tfmap = l_tfmap;
				//break;
			}
			if( tfmap==null )
			{
				log.warn("template "+template.getName()+" has no dynamic frag of uet=1");
				return ret;
			}
			int listCount = 0;
			listCount = getExlistcount( tfmap.getId(), entity.getId() );
			if( listCount<=0 ) listCount = tfmap.getListcount();

			// ��ģ���ﴫ�Ĳ���,ÿҳ��ʾ������
			if( listCount<=0 )
			{
				log.warn("template ["+template.getName()+"] listCount less than 0.");
				return ret;
			}

			//if (entity.getId() == CompileTask.TEST_ID) {
				//��ɷ�ҳ����
				String groovyPath = CompileTask.GROOVY_ROOT + CompileTask.PAGING_GROOVY_NAV_SCRIPT
						+ ".groovy";
				if (log.isInfoEnabled()) {
					log.info("run groovy script '" + groovyPath
							+ "' for entity id:" + entity.getId());
				}
				try {
					EmbedGroovy embedGroovy = new EmbedGroovy();
					Map params = new HashMap();					
					params.put("entity", entity);
					params.put("listCount", new Integer(listCount));
					params.put("tfmap", tfmap);
					params.put("log",log);
					params.put("template",template);
					params.put("request",pageContext.getRequest());
					embedGroovy.initial(groovyPath);
					embedGroovy.setParameters(params);
					Object result = embedGroovy.run();
					if (result == null) {
						result = embedGroovy.getProperty("result");
					}
					ResponseUtils.write(pageContext,result.toString());
					return ret;
				} catch (Exception e) {
					log.error(
							"execute groovy script[" + groovyPath + "] error",
							e);
				}
			//}
				
			// ��ʵ�����湲�ж�������ʵ��
			/*			 
			List newsList = ListCacheClient.getInstance().TimeFilter(entity.getId(), ItemInfo.NEWS_TYPE, 0, MAX_SIZE_OF_LISTCACHE);
			int newslistSize = newsList.size();
			if( newslistSize<=0 )
			{
				log.warn("ENTITY ["+entity.getId()+"] newslistSize is 0.");
				return ret;
			}

			// ����ҳ��
			int pageCount = 0;
			pageCount = (newslistSize%listCount==0)?newslistSize/listCount:newslistSize/listCount+1;
	
			// ��ȡ��ǰҳ
			// ���δȡ��ָ����ǰҳ,����Ϊ�ǵ�һҳ
			int page = 0;
			String pageStr = request.getParameter("page");
			if( pageStr!=null && pageStr.trim().length()>0 )
			{
				try
				{
					page = Integer.parseInt(pageStr);
				}catch(NumberFormatException e) {
					page = 0;
					log.error("invalid page "+pageStr+" , reset page=1");
				}
			}

			// ���������ʲ����ڵ�ҳ
			if( page<0 || page>=pageCount )
			{
				log.warn( "invalid page "+page+" , max page "+pageCount );
				return ret;
			}

			// 
			StringBuffer sb = new StringBuffer();

			int i=Math.max(page-10,0);
			int endPage = Math.min(page+10,pageCount);

			// ��ǰҳ����1,�� [��һҳ]
			if( page>0 )
			{
				String url = getPaginationUrl( entity.getUrl(), (page-1) );
				sb.append( "<a href="+url+">" );
				sb.append( Util.unicodeToGBK("��һҳ") );
				sb.append( "</a>&nbsp;" );
			}
			
			for(; i<endPage; i++)
			{
				String url = getPaginationUrl( entity.getUrl(), i );

				if( i==page )
				{
					sb.append( "<b>"+(i+1)+"</b>&nbsp;" );
				} else {
					sb.append( "<a href="+url+">" );
					sb.append( ""+(i+1) );
					sb.append( "</a>&nbsp;" );
				}
			}

			// ��ǰҳС��pageCount,�� [��һҳ]
			if( page<(pageCount-1) )
			{
				String url = getPaginationUrl( entity.getUrl(), (page+1) );
				sb.append( "<a href="+url+">" );
				sb.append( Util.unicodeToGBK("��һҳ") );
				sb.append( "</a>" );
			}

			ResponseUtils.write(pageContext,sb.toString());
			*/
			return ret;
		} catch ( Exception e ) {
			log.error("doEndTag exception -- ", e);
			return ret;
		}
	}

	// �����ҳurl
	private static String getPaginationUrl( String url, int page )
	{
		try
		{
			if( page==0 ) return url;

			int idx = url.lastIndexOf(".html");
			if( idx<0 ) return url;

			return url.substring(0,idx)+"_"+page+".html";

		}catch(Exception e) {
			log.error("getPaginationUrl exception -- ",e);
			return url;
		}
	}
    private int getExlistcount( int tfid, int entityid )
    {
        int ret = -1;
        List list = ItemManager.getInstance().getList( ExTFMap.class );
        for(int i=0; list!=null && i<list.size(); i++ )
        {
            ExTFMap extfmap = (ExTFMap)list.get(i);
            if( extfmap.getTfid()==tfid && extfmap.getEntityid()==entityid )
            {
                ret = extfmap.getListcount();
                break;
            }
        }
        return ret;
    }

	public int getView()
	{
		return this.view;
	}

	public void setView( int view )
	{
		this.view = view;
	}

}
