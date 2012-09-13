package net.pusuo.cms.client.taglib;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.ResponseUtils;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.cache.CmsSortItem;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.client.compile.CompileTask;
import com.hexun.cms.client.compile.CompileTaskFactory;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.EmbedGroovy;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.core.Author;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.util.Util;

public class NewsListTag extends TagSupport implements Cloneable {
	private static final Log log = LogFactory.getLog(NewsListTag.class);

	private String id = "";

	private int view = -1;

	private int listcount = -1;

	private int timetype = -1;

	private int sorttype = -1;

	private String prio = "";

	private static String dot = "";

	static {
		try {
			dot = Util.unicodeToGBK("·");
		} catch (Exception e) {
		}
	}

	public int doStartTag() throws JspException {
		int ret = SKIP_BODY;
		try {
			ServletRequest request = pageContext.getRequest();
			if (view == -1) {
				// default view , display some frag info
				ResponseUtils.write(pageContext, "[newslistfrag " + id + "]");
				return ret;
			}

			// added by wangzhigang 2005.12.06
			// filter sfrag frag
			String filter = request.getParameter("filter");
			if (filter != null && filter.toLowerCase().equals("nosd")) {
				ResponseUtils.write(pageContext, "[NEWSLIST FRAG]");
				return ret;
			}

			if (view == FragTag.PRECOMPILE_VIEW) {
				// pre compile
				List fraglist = (List) request.getAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY);
				if (fraglist != null) {
					fraglist.add(this.clone());
					request.setAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY, fraglist);
				}
				return ret;
			}
			if (view != FragTag.PRE_VIEW && view != FragTag.FRAG_VIEW
					&& view != FragTag.COMPILE_VIEW
					&& view != FragTag.LIST_VIEW) {
				return ret;
			}

			// get entity from DFrag set attribute.
			EntityItem entity = (EntityItem) pageContext.getAttribute(id);
			if (entity == null) {
				log.warn("NewsListTag -- entity is null.");
				return ret;
			}
			
			// 通过pageContext传递参数不是一种好的方式:
			// 对于嵌套tag,使用findAncestorWithClass即可获得外层的tag对象.
			// (Alfred.Yuan)
			List allIdList = null;
			DFragTag parentTag = (DFragTag)findAncestorWithClass(this, DFragTag.class);
			if (parentTag != null) {
				allIdList = EntityParamUtil.getAllIdList(parentTag.getOtherIds(), entity.getId());
				if (allIdList != null && allIdList.size() > 0) {
					log.info("NewsListTag: (fragId=" + parentTag.getId() + ") (fragName=" + parentTag.getName() 
							+ ") (entityId=" + parentTag.getEntityid() + ") (otherIds=" + parentTag.getOtherIds());
				}
			}
			
			TFMap tfmap = (TFMap) pageContext.getAttribute(DFragTag.TFID_KEY);
			if (tfmap == null) {
				log.warn("NewsListTag -- tfmap is null.");
				return ret;
			}

			if (view == FragTag.PRE_VIEW) {
				// 碎片正常显示
				String content = ClientFile.getInstance().read(
						PageManager.getFStorePath(entity, tfmap.getId(), true));
				if (content == null) {
					content = "";
				} else {
					content = Util.unicodeToGBK(content);
				}
				ResponseUtils.write(pageContext, content);
				return ret;
			}

			if (view == FragTag.FRAG_VIEW) {
				// 碎片调整
				String name = tfmap.getName();
				String desc = tfmap.getDesc();
				Template template = tfmap.getTemplate();

				String content = ClientFile.getInstance().read(
						PageManager.getFStorePath(entity, tfmap.getId(), true));
				if (content == null)
					content = "";

				StringBuffer sb = new StringBuffer();
				sb.append("<span id=\"cms4_template_frag\" name=\"cms4_template_frag\" ");
				sb.append(" frag_id=\""+tfmap.getId()+"\"");
				sb.append(" frag_template_id=\""+template.getId()+"\" ");
				sb.append(" frag_entity_id=\""+entity.getId()+"\" ");
				sb.append(" frag_type=" + FragTag.DFRAG_TYPE);
				sb.append(" frag_name=\"" + name + "\"");
				sb.append(" frag_desc=\"" + desc + "\"");
				sb.append(" frag_permission >");
				
				if (content.equals("")) {
					content = Util.unicodeToGBK("【碎片名称】") + name + "<br>";
					content += Util.unicodeToGBK("【碎片描述】") + desc + "<br>";
				} else {
					//content = Util.unicodeToGB2312( content );
					content = Util.unicodeToGBK(content);
				}
				sb.append(content);
				sb.append("</span>");
				ResponseUtils.write(pageContext, sb.toString());
				return ret;
			}

			// 预编译  
			if (view == FragTag.COMPILE_VIEW) {
				String storePath = PageManager.getFStorePath(entity, tfmap.getId(),	false);
				ResponseUtils.write(pageContext, "<!--#include virtual=\"" + storePath + "\" -->");
				return ret;
			}

			// FragTag.LIST_VIEW
			// 返回列表. 用于备份当日新闻和列表专题分页
			if (view == FragTag.LIST_VIEW) {
				// 生成新闻列表, 用于定时备份专题
				//if( tfmap.getUet()!=1 || tfmap.getSorttype()!=3 ) return ret;
				//if( tfmap.getUet()!=1 ) return ret;

				// 取动态列表方式
				HttpSession session = pageContext.getSession();
				String mode = (String) session.getAttribute("mode");
				if (mode == null || mode.equals(""))
					return ret;
				if (!mode.equals("0") && !mode.equals("1") && !mode.equals("2"))
					return ret;

				// 专题id
				String _eid = request.getParameter("ENTITYID");
				int eid = -1;

				// 动态分页
				String _page = request.getParameter("page");
				String _count = request.getParameter("count");
				String _start = request.getParameter("start");
				int page = 0;
				int count = 0;

				// 备份当日
				String _begTime = request.getParameter("begTime");
				String _endTime = request.getParameter("endTime");
				Timestamp begTime = null;
				Timestamp endTime = null;

				List list = null;
				if (mode.equals("1")) { // 备份当日
					try {
						eid = Integer.parseInt(_eid);
						begTime = Timestamp.valueOf(_begTime);
						endTime = Timestamp.valueOf(_endTime);

					} catch (Exception e) {
						log.error("NewsListTag -- invalid parameter mode 1  " + e.toString());
					}
					if (eid == -1 || begTime == null || endTime == null) {
						return ret;
					}
					list = ListCacheClient.getInstance().TimeFilter(eid, ItemInfo.NEWS_TYPE, 0, 1000);
				} 
				else if (mode.equals("2")) { // 动态分页
					try {
						eid = Integer.parseInt(_eid);
						if(_page!=null){
							page = Integer.parseInt(_page);
						}
						count = Integer.parseInt(_count);
					} 
					catch (Exception e) {
						log.error("NewsListTag -- invalid parameter mode 2 " + e.toString());
					}
					if (eid == -1)
						return ret;
					int start = 0;
					if(_start != null){
						start = Integer.parseInt(_start);
					}
					else{
						start = page*count;
					}
					
					// 根据desc返回子列表的实体的类型:News或Picture
					int entityType = ItemUtil.getListEntityTypeByDesc(tfmap.getDesc());
					if(log.isInfoEnabled()){
						log.info("list type:"+entityType+" for dfrag:"+tfmap.getId()+" desc:"+tfmap.getDesc());
					}
					list = ListCacheClient.getInstance().TimeFilter(eid,entityType, start, count);
				} 
				else {
					return ret;
				}

				StringBuffer sb = new StringBuffer();
				String timeType = null;
				int decorate = tfmap.getTimetype();

				if (tfmap.getTimetype() > 0 && tfmap.getTimetype() <= 20) {
					timeType = CompileTaskFactory.getTimetype(tfmap
							.getTimetype());
					if (timeType == null) {
						decorate = -1;
						log.warn("NewsListTag -- timeType is null.");
					}
				}
				SimpleDateFormat formatter = null;
				if (timeType != null) {
					formatter = new SimpleDateFormat(timeType);
				}

				//判断是否Groovy碎片,如果是Groovy碎片,那优先执行Groovy脚本
				if (tfmap.getQuotetype() == DFragTag.GROOVY_QUOTETYPE
						&& tfmap.getQuotefrag() != null
						&& tfmap.getQuotefrag().trim().length() > 0) {
					if (log.isInfoEnabled()) {
						log.info("dfrag is groovy frag,so execute groovy script "
										+ "tfmapid:"
										+ tfmap.getId()
										+ " tfmap quotefrag:"
										+ tfmap.getQuotefrag());
					}
					
					long timeStart = System.currentTimeMillis();

					String grooyPath = CompileTask.GROOVY_ROOT
							+ tfmap.getQuotefrag().trim() + ".groovy";
					try {
						EmbedGroovy embedGroovy = new EmbedGroovy();
						Map params = new HashMap();
						params.put("items", list);
						params.put("entityId", new Integer(eid));
						params.put("tfmap", tfmap);	
						params.put("timeformater",formatter);
						embedGroovy.initial(grooyPath);
						embedGroovy.setParameters(params);
						Object result = embedGroovy.run();
						if (result == null) {
							result = embedGroovy.getProperty("result");
						}
						if (result != null) {
							sb.append(result.toString());
						}
						
						long timeEnd = System.currentTimeMillis();
						log.info("Alfred-Compile news list by groovy.Cost is: " + (timeEnd - timeStart));
					} catch (Exception e) {
						log.error("execute groovy script[" + grooyPath
								+ "] error", e);
					}
				} else {
					long timeStart = System.currentTimeMillis();
					
					for (int k = 0; list != null && k < list.size(); k++) {
						CmsSortItem csi = (CmsSortItem) list.get(k);
						Timestamp time = csi.getTime();
						if (mode.equals("1")) {
							if (time.compareTo(begTime) < 0
									|| time.compareTo(endTime) > 0)
								continue;
						}
						if (decorate == 22) {
							EntityItem item = (EntityItem) ItemManager
									.getInstance().get(
											new Integer(csi.getId()),
											EntityItem.class);
							EntityItem pItem = (EntityItem) ItemManager
									.getInstance().get(
											new Integer(item.getPid()),
											EntityItem.class);
							sb.append("[<a href=");
							sb.append(pItem.getUrl());
							sb.append(" target=_blank><span>");
							sb.append(pItem.getDesc());
							sb.append("<span></a>]");
						}
						sb.append("<li><a href=");
						sb.append(csi.getUrl());
						sb.append(" target=_blank>");
						sb.append(csi.getDesc());
						sb.append("</a>");
						if (decorate >= 1 && decorate <= 20) {
							sb.append("<span>");
							sb.append(formatter.format(new java.util.Date(csi
									.getTime().getTime())));
							sb.append("</span>");
						} else if (decorate == 21) {
							News news = (News) ItemManager.getInstance().get(
									new Integer(csi.getId()), News.class);
							String news_author = news.getAuthor();
							if (news_author != null
									&& !news_author.trim().equals("")) {
								Author author = (Author) ItemManager
										.getInstance().getItemByName(
												news_author, Author.class);
								if (author != null
										&& !author.getDesc().equals("")
										&& !author.getUrl().equals("")) {
									sb.append(" <span>");
									sb.append("<a href=");
									sb.append(author.getUrl());
									sb.append(" target=_blank>");
									sb.append(author.getDesc());
									sb.append("</a></span>");
								} else {
									sb.append(" <span>");
									sb.append(news.getAuthor());
									sb.append("</span>");
								}
							}
						}

						sb.append("</li>\n");
					}
					
					long timeEnd = System.currentTimeMillis();
					log.info("Alfred-Compile news list by java.Cost is: " + (timeEnd - timeStart));					
				}

				ResponseUtils.write(pageContext, sb.toString());
				return ret;
			}

			// invalid view
			//ResponseUtils.write(pageContext,"sorry , there are not
			// information for the view mode["+view+"]");
		} catch (Exception e) {
			log.error("NewsListTag Exception. " + e.toString());
		}
		return ret;
	}

	public int doEndTag() throws JspException {
		// clean up
		return (EVAL_PAGE);
	}

	public String getId() {
		return this.id;
	}

	public int getView() {
		return this.view;
	}

	public int getListcount() {
		return this.listcount;
	}

	public int getTimetype() {
		return this.timetype;
	}

	public int getSorttype() {
		return this.sorttype;
	}

	public String getPrio() {
		return this.prio;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setView(int view) {
		this.view = view;
	}

	public void setListcount(int listcount) {
		if(listcount <0 || listcount>1000){
			throw new IllegalArgumentException("listcount must be between 1 and 1000");
		}
		this.listcount = listcount;
	}

	public void setTimetype(int timetype) {
		this.timetype = timetype;
	}

	public void setSorttype(int sorttype) {
		this.sorttype = sorttype;
	}

	public void setPrio(String prio) {
		this.prio = prio;
	}
}
