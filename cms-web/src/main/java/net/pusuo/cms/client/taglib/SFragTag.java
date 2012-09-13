package net.pusuo.cms.client.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.ServletRequest;

import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.util.ResponseUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.auth.Permission;

import com.hexun.cms.Global;
import com.hexun.cms.util.Util;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Template;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.CommonFrag;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.SEOUtil;

public class SFragTag extends TagSupport implements FragTag,Cloneable
{
	private static final Log log = LogFactory.getLog(SFragTag.class);

	// 被请求的模板
	private Template template = null;

	// 绑定实体, 若没有绑定任何实体, 则不显示碎片内容. 预编译不需要指定实体
	private EntityItem entity = null;

	//
	private int view = -1;
	private int type = FragTag.SFRAG_TYPE;
	private String name = "";
	private String desc = "";
	private String permission = "";

	// 引用类型, 范围 1--5
	// 1: 本实体使用
	// 2: 同模板不同实体共用
	// 3: 引用首页实体碎片
	// 4: 同频道不同实体共用
	// 5: HEXUN共用碎片
	private int quotetype = -1;

	// 引用类型为3,4,5时, 要指定引用的碎片
	private String quotefrag = "";

	// 用于嵌套DFragTag，SFragTag做为DFragTag的一部分, 用id做标识
	private String id = "";

        //wzg add  --user UE
        //private String wperm = "ue";

	public int doStartTag() throws JspException
	{
		try
		{
			ServletRequest request = pageContext.getRequest();

			template = (Template)request.getAttribute(FragTag.TEMPLATE_KEY);
			entity = (EntityItem)request.getAttribute(FragTag.ENTITY_KEY);
		} catch ( Exception e ) {
			log.error("SFragTag Exception : "+e.toString());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		int ret = EVAL_PAGE;

		// 当前访问的模板为null, 说明request.getAttribute()为 null
		if( template==null )
		{
			log.warn("SFragTag --> template is null.");
			return ret;
		}

		try
		{
			ServletRequest request = pageContext.getRequest();
			if( view==-1 )
			{
				// default view , display some frag info
				ResponseUtils.write(pageContext,"[sfrag ("+name+"  "+desc+")]");
				return ret;
			}

			// added by wangzhigang 2005.12.06
			// filter sfrag frag
			String filter = request.getParameter("filter");
			if( filter!=null && filter.toLowerCase().equals("nosd") )
			{
				ResponseUtils.write(pageContext,"[STATIC FRAG ("+name+"  "+desc+") ]");
				return ret;
			}

			// 预编译, 无需指定实体. 遍历到的碎片保存到request中, 由Footer Tag处理
			if ( view==FragTag.PRECOMPILE_VIEW )
			{
				// pre compile
				List fraglist = (List)request.getAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY);
				if ( fraglist==null )
				{
					fraglist = new ArrayList();
				}
				fraglist.add(this.clone());
				request.setAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY,fraglist);
				return ret;
			}

			// SFragTag嵌套在DFragTag内, 优先于本实体
			EntityItem cEntity = (EntityItem)pageContext.getAttribute(id);
			if( cEntity!=null ) entity = cEntity;

			// 除预编译的其他操作都要指定实体
			if( entity==null )
			{
				log.warn("SFragTag -- entity is null.");
				return ret;
			}

			// 实体所在的频道
			// 几乎不可能
			Channel channel = (Channel)ItemManager.getInstance().get(new Integer(entity.getChannel()), Channel.class);
			if( channel==null )
			{
				log.warn("SFragTag -- channel is null. ENTITYID:"+entity.getId());
				return ret;
			}

			// 取得当前实体所属首页实体
			// 用于commonfrag=4, 分首页存储碎片
			EntityItem hpe = ItemUtil.getHpEntity( entity );
			if( hpe==null )
			{
				log.warn("ItemUtil.getHpEntity=null @entity "+entity.getId());
				return ret;
			}

			// 当quotetype=1,2,3 时, 获取碎片信息
			//EntityItem quoteEntity = getQuoteEntity();
			//Template quoteTemplate = getQuoteTemplate( quoteEntity );
			//TFMap quoteTFMap = getQuoteTFMap( quoteTemplate );
			TFMap tfmap = null;

			// 当quotetype=4,5 时, 获取碎片信息
			CommonFrag commonFrag = getCommonFrag();

			if( quotetype==1 )
			{
				Set tfmaps = template.getTFMaps();
				if( tfmaps!=null )
				{
					for( Iterator it = tfmaps.iterator(); it.hasNext(); )
					{
						TFMap tmp = (TFMap)it.next();
						if( tmp.getName().equals(this.name) )
						{
							tfmap = tmp;
							break;
						}
					}
				}
				if( tfmap==null )
				{
					log.warn("tfmap is null, check tfmap ["+this.name+"  "+desc+"].");
					return ret;
				}
			} else if( quotetype==4 || quotetype==5 ) {
				commonFrag = getCommonFrag();
				if( commonFrag==null )
				{
					log.warn("commonFrag is null, check tfmap ["+name+"  "+desc+"].");
					return ret;
				}
			}

			// 显示碎片内容
			if ( view==FragTag.PRE_VIEW)
			{	// frag preview
				String content = null;

				// 整体测试预览
				String test = request.getParameter("test");
				if ( test!=null && test.equalsIgnoreCase("yes") )
				{
					if( quotetype==1 )
					{
						String testtfid = request.getParameter("testtfid");
						int i_testtfid = -1;
						try
						{
							if( testtfid!=null ) i_testtfid = Integer.parseInt(testtfid);
						} catch(Exception e) {}
						if( i_testtfid==tfmap.getId() )
						{
							content = request.getParameter("testcont");
							if( content==null ) content = "";
						} else {
							content = ClientFile.getInstance().read(PageManager.getFStorePath(entity, tfmap.getId(),true ));
							if( content==null ) content = "";
							else content = Util.unicodeToGBK( content );
						}
					} else if ( quotetype==4 || quotetype==5 ) {
						String testchannelid = request.getParameter("testchannelid");
						String testfragname = String.valueOf(request.getParameter("testfragname"));
						int i_testchannelid = 0;
						try
						{
							if( testchannelid!=null ) i_testchannelid = Integer.parseInt(testchannelid);
						} catch(Exception e){}
						if( commonFrag.getChannel()==i_testchannelid && commonFrag.getName().equals(testfragname) )
						{
							content = request.getParameter("testcont");

							if( content==null ) content = "";
						} else {
							if( quotetype==4 )
							{
								content = ClientFile.getInstance().read( PageManager.getFStorePath( commonFrag.getChannel(), hpe.getId(), commonFrag.getName(), true ) );
							} else if( quotetype==5 ) {
								content = ClientFile.getInstance().read( PageManager.getFStorePath( channel.getId(), commonFrag.getName(), true ) );
							}
							if( content==null ) content = "";
							else content = Util.unicodeToGBK( content );
						}
					}
				} else {
					
//					wzg add
					 String cmsdiv = request.getParameter("cmsdiv");
					 if(cmsdiv!=null&&cmsdiv.equals("yes")) {
					 String testtfid = request.getParameter("cmstfid");
					int i_testtfid = -1;
					try {
						if( testtfid!=null ) i_testtfid = Integer.parseInt(testtfid);
					}catch(Exception e) {
						i_testtfid = -1;
					}
					if(i_testtfid==tfmap.getId() ) {
					      content = "cmspre_templaetest";
					}
					else if( name.indexOf("style") >= 0 || name.indexOf("css") >= 0 || 
												desc.indexOf("样式") >= 0) {
							content = ClientFile.getInstance().read( PageManager.getFStorePath( entity, tfmap.getId(), true) );	
   				    }else {
					     content = "";
					}	 
				   	if( content==null ) content = "";
			         else content = Util.unicodeToGBK( content );
				     ResponseUtils.write(pageContext,content);
					 return ret;
				 }
                // add end
					if( quotetype==1 )
					{
						//对于本实体碎片,并且名字为metafrag的碎片,尝试使用已经有的内容填加
						log.info(this.getName());
						if(this.getName().equals("metafrag")){
							createMetaFrag(tfmap);
						}
						content = ClientFile.getInstance().read( PageManager.getFStorePath( entity, tfmap.getId(), true) );
					} else if( quotetype==4 ) {
						content = ClientFile.getInstance().read( PageManager.getFStorePath( commonFrag.getChannel(), hpe.getId(), commonFrag.getName(), true ) );
					} else if( quotetype==5 ) {
						content = ClientFile.getInstance().read( PageManager.getFStorePath( channel.getId(), commonFrag.getName(), true ) );
					}
					if( content==null ) content = "";
					else content = Util.unicodeToGBK( content );
				}
				if( content==null ) content = "";
				ResponseUtils.write(pageContext,content);
				return ret;
			}

			// 碎片调整
			if ( view==FragTag.FRAG_VIEW )
			{
				String content = null;

				// HEXUN共用碎片内容单独维护, 不在碎片调整中维护
				if( quotetype==5 )
				{
					content = ClientFile.getInstance().read( PageManager.getFStorePath( channel.getId(), commonFrag.getName(), true ) );
					if( content==null ) content = "";
					content = Util.unicodeToGBK( content );
					ResponseUtils.write(pageContext,content);
					return ret;
				}
                                //这是没有ue权限的用户的操作  --把共用碎片内容显示---不可维护--add wzg
                                if(permission!=null && !permission.equals("") && quotetype==4) {
		                                boolean ownerflag = isUEOwner((HttpServletRequest)pageContext.getRequest(),(HttpServletResponse)pageContext.getResponse());
		                                if(!ownerflag)
		                                {
		                                        content = ClientFile.getInstance().read( PageManager.getFStorePath( commonFrag.getChannel(), hpe.getId(), commonFrag.getName(), true ) );
		                                        if( content==null ) content = "";
		                                        content = Util.unicodeToGBK( content );
		                                        ResponseUtils.write(pageContext,content);
		                                        return ret;
		                                }
                                }
                                // end --wzg

				StringBuffer ctt = new StringBuffer();
				ctt.append("<span id=\"cms4_template_frag\" ");
				ctt.append(" name=\"cms4_template_frag\"");
				ctt.append(" frag_type="+FragTag.SFRAG_TYPE);
				ctt.append(" frag_name=\""+name+"\"");
				ctt.append(" frag_desc=\""+desc+"\"");
				
				ctt.append(" frag_template_id=\""+template.getId()+"\" ");
				ctt.append(" frag_entity_id=\""+entity.getId()+"\" ");
				
				if( quotetype==1 )
				{
					ctt.append(" frag_id=\""+tfmap.getId()+"\"");
					ctt.append(" frag_quotetype="+quotetype);
					if( quotefrag==null || quotefrag.equals("") )
					{
						ctt.append(" frag_quotefrag " );
					} else {
						ctt.append(" frag_quotefrag=\""+quotefrag+"\"");
					}
					if( tfmap.getPermission()==null || tfmap.getPermission().equals("") )
					{
						ctt.append(" frag_permission " );
					} else {
						ctt.append(" frag_permission=\""+tfmap.getPermission()+"\"");
					}
					content = ClientFile.getInstance().read( PageManager.getFStorePath(entity, tfmap.getId() ,true) );
				} else if( quotetype==4 ) {
					//这是由ue权限的用户的操作
                    String perm = commonFrag.getPermission()==null?"":commonFrag.getPermission();
					String channelDesc = channel==null?"":channel.getDesc();
					// quotetype=4 的情况. 这种碎片的存储位置不依赖于任何实体
					// entity.getId(), template.getId() 也是用于整体测试
					ctt.append(" frag_channel_desc=\""+channelDesc+"\" ");
					ctt.append(" frag_channel_id=\""+commonFrag.getChannel()+"\" ");
					ctt.append(" frag_quotetype=\""+quotetype+"\" ");
					ctt.append(" frag_quotefrag=\""+quotefrag+"\"");
					if( commonFrag.getPermission()==null || commonFrag.getPermission().equals("") )
					{
						ctt.append(" frag_permission" );
					} else {
						ctt.append(" frag_permission=\""+perm+"\"");
					}
					String path=PageManager.getFStorePath( commonFrag.getChannel(), hpe.getId(), commonFrag.getName(), true ) ;
					content = ClientFile.getInstance().read( path);
				}
				ctt.append(" >");

				if( content==null || content.trim().equals("") ) {
					content = Util.unicodeToGBK("【碎片名称】")+name+"<br>";
					content += Util.unicodeToGBK("【碎片描述】")+desc+"<br>";
				} else {
					content = Util.unicodeToGBK(content).trim();
				}
				ctt.append(content);
				ctt.append("</span>");
				ResponseUtils.write(pageContext,ctt.toString());
				return ret;
			}

			// 用于模板编译
			if ( view==FragTag.COMPILE_VIEW || view==FragTag.LIST_VIEW )
			{
				// frag compile
				if ( quotetype==1 )
				{
					//对于本实体碎片,并且名字为metafrag的碎片,尝试使用已经有的内容填加
					if(this.getName().equals("metafrag")){
						createMetaFrag(tfmap);
					}
ResponseUtils.write(pageContext,"<!--#include virtual=\""+PageManager.getFStorePath( entity, tfmap.getId(), false)+"\" -->");
				} else if ( quotetype==4 ) {
ResponseUtils.write(pageContext,"<!--#include virtual=\""+PageManager.getFStorePath( entity.getChannel(), hpe.getId(), commonFrag.getName(), false )+"\" -->");
				} else if( quotetype==5 ) {
ResponseUtils.write(pageContext,"<!--#include virtual=\""+PageManager.getFStorePath( channel.getId(), commonFrag.getName(), false )+"\" -->");
				}
				return ret;
			}

			// invalid view
			//log.error("sorry , there are not information for the view mode["+view+"]");
			//ResponseUtils.write(pageContext,"");
		} catch ( Exception e ) {
			log.error("SFragTag exception -- ", e);
		}
		return ret;
	}

	/**
	 * @param tfmap
	 * @throws Exception
	 */
	private void createMetaFrag(TFMap tfmap) throws Exception {
		SEOUtil.SEOMeta meta = SEOUtil.getKeyDescription(entity.getId());
		if(meta != null){
			String metaKey = meta.keyword;
			String metaDesc = meta.description;
			metaKey = (metaKey!=null)?metaKey.trim():null;
			metaDesc = (metaDesc!=null)?metaDesc.trim():null;
			if(metaKey != null &&  metaDesc != null && metaKey.length()>0 && metaDesc.length()>0){
				/*构造meta部分,并将其写入到碎片中*/
				String desc_content = "<meta name=\"description\" content=\""+metaDesc+"\" />\n";
				String key_content = "<meta name=\"keywords\" content=\""+metaKey+"\" />\n<meta name=\"robots\" content=\"all\" />";
				String fragPath = PageManager.getFStorePath( entity, tfmap.getId(), true);
				//String metaContent = Util.GBKToUnicode(desc_content+key_content);  //old
				//String metaContent = new String((desc_content+key_content).getBytes("UTF-8"));
				//String metaContent = new String((desc_content+key_content).getBytes(),"UTF-8");
				//log.info("metafrag path:"+fragPath);
				//log.info("metafrag content:"+metaContent);
				ClientFile.getInstance().write(desc_content+key_content,fragPath,true);
			}
		}
	}

	/**
	 *	返回给定实体所在树的首页实体
	 *	@param 给定实体
	 *	该方法依赖于实体的category字段
	 */
	private EntityItem getHpEntity( EntityItem eItem )
	{
		String category = eItem.getCategory();
		if( category==null || category.equals("") )
		{
			return null;
		}
		String hpId = category.split(Global.CMSSEP)[0];
		return (EntityItem)ItemManager.getInstance().get(new Integer(hpId), EntityItem.class);
	}
	/**
	 *	返回引用实体
	 *	适用于quotetype为1,2或3的情况
	 *	实体决定碎片存储位置
	 *	quotetype=1 碎片应该存储在当前绑定的实体下面
	 *	quotetype=2或3 碎片应该存储在首页实体下面
	 */
	private EntityItem getQuoteEntity()
	{
		EntityItem ret = null;
		switch( quotetype )
		{
			case 1:
				ret = entity;
				break;
			case 2:
				ret = getHpEntity( entity );
				break;
			case 3:
				ret = getHpEntity( entity );
				break;
		}
		return ret;
	}
	/**
	 *	返回实体实现的模板
	 *	@param eItem 实体
	 *	@return
	 *	在quotetype=3时有用, 用于返回首页实体实现的主模板
	 *	当quotetype=1,2时, 返回当前被请求的模板
	 */
	private Template getQuoteTemplate( EntityItem eItem )
	{
		Template ret = null;

		switch( quotetype )
		{
			case 1:
			case 2:
				ret = template;
				break;
			case 3:
				if( eItem==null ) return null;
				String templateStr = eItem.getTemplate();
				if( templateStr!=null && !templateStr.equals("") )
				{
					String templates = templateStr.split(Global.CMSSEP)[0];
					String templateid = templates.split(Global.CMSCOMMA)[0];
					ret = (Template)ItemManager.getInstance().get(new Integer(templateid), Template.class);
				}
				break;
		}
		return ret;
	}

	/**
	 *	返回碎片信息
	 *	@param t 碎片所属模板
	 *	@return 返回碎片信息. 若模板为null, 或模板中不存在给定碎片名称,则返回null
	 *	quotetype=1,2 碎片应从属于当前被请求的模板
	 *	quotetype=3 因为是引用首页页面中的碎片, 所以要指定引用碎片quotefrag
	 *	引用首页实体实现的主模板的碎片
	 */
	private TFMap getQuoteTFMap( Template t )
	{
		if( t==null ) return null;
		Set tfmaps = t.getTFMaps();
		if( tfmaps==null ) return null;

		TFMap ret = null;

		switch( quotetype )
		{
			case 1:
			case 2:
				for( Iterator it = tfmaps.iterator(); it.hasNext(); )
				{
					TFMap tfmap = (TFMap)it.next();
					if( tfmap.getName().equals(name) )
					{
						ret = tfmap;
						break;
					}
				}
				break;
			case 3:
				for( Iterator it = tfmaps.iterator(); it.hasNext(); )
				{
					TFMap tfmap = (TFMap)it.next();
					if( tfmap.getName().equals(quotefrag) )
					{
						ret = tfmap;
						break;
					}
				}
				break;
		}
		return ret;
	}

	/**
	 *
	 *
	 */
	private CommonFrag getCommonFrag()
	{
		int channelId = -1;
		if( quotetype==4 )
		{
			channelId = entity.getChannel();
		}
		if( quotetype==5 )
		{
			channelId = -1;
		}

		List list = ItemManager.getInstance().getList( CommonFrag.class );
		for(int i=0; list!=null && i<list.size(); i++)
		{
			CommonFrag cf = (CommonFrag)list.get(i);
			if( cf.getChannel()==channelId && cf.getName().equals(quotefrag) )
			{
				return cf;
			}
		}
		return null;
	}

        /**
         * isUEOwner
         * @return  boolean
         */
        private boolean isUEOwner(HttpServletRequest request,
            HttpServletResponse response) {
          boolean owner = false;
          Authentication auth = null;
          try {
             auth = AuthenticationFactory.getAuthentication(request,
                response);
          }catch(Exception e) {
            log.error("the get auth is error----" + e);
            return true;
          }
          Permission permission2 = auth.getUserPermission();
          if(permission2.isOwner(permission,Permission.DEPARTMENT))
            owner = true;
          if(permission2.isOwner(permission,Permission.CHANNEL))
            owner = true;
          return owner;
        }

	public String getName()
	{
		return this.name;
	}

	public int getType()
	{
		return this.type;
	}

	public String getDesc()
	{
		return this.desc;
	}

	public int getView()
	{
		return this.view;
	}

	public int getQuotetype()
	{
		return this.quotetype;
	}
	public String getQuotefrag()
	{
		return this.quotefrag;
	}
	public String getPermission()
	{
		return this.permission;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public void setType( int type )
	{
		this.type = type;
	}

	public void setDesc( String desc )
	{
		this.desc = desc;
	}

	public void setView( int view )
	{
		this.view = view;
	}

	public void setQuotetype( int quotetype )
	{
		this.quotetype = quotetype;
	}
	public void setQuotefrag( String quotefrag )
	{
		this.quotefrag = quotefrag;
	}
	public void setPermission( String permission )
	{
		this.permission = permission;
	}

	public int getTemplateid()
	{
		return -1;
	}
	public void setTemplateid( int templateid )
	{
	}
	public int getEntityid()
	{
		return -1;
	}
	public void setEntityid( int entityid )
	{
	}

	public String getId()
	{
		return this.id;
	}
	public void setId( String id )
	{
		this.id = id;
	}

	public String toString()
	{
		return "【碎片类型】静态碎片<br>【碎片名称】"+name+"<br>【碎片描述】"+desc+"<br>【引用类型】"+quotetype+"<br>【引用碎片】"+quotefrag+"<br>【碎片权限】"+permission;
	}
}
