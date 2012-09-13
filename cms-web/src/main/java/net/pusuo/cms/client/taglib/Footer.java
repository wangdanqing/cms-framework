package net.pusuo.cms.client.taglib;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.ResponseUtils;

import com.hexun.cms.Global;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.TimeUtils;
import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.util.Util;

public class Footer extends TagSupport {
	private static final Log log = LogFactory.getLog(Footer.class);

	private int view = -1;

	private Template template = null;

	// reuse frag modify view javascript code
	private static StringBuffer ctt = null;

	private static String script_code = null;

	static {

		ctt = new StringBuffer();
		// hua.deng add at 2007.1.9
		/*
		 * ctt.append("<style>\n"); ctt.append(".clearfix:after {\n");
		 * ctt.append("content: \".\";\n"); ctt.append("display: block;\n");
		 * ctt.append("height: 0;\n"); ctt.append("clear: both;\n");
		 * ctt.append("visibility: hidden;\n"); ctt.append("}\n"); ctt.append("*
		 * html>body .clearfix {\n"); ctt.append("display: inline-block;\n");
		 * ctt.append("width: 100%;\n"); ctt.append("}\n"); ctt.append("* html
		 * .clearfix {\n"); ctt.append("height: 1%;\n"); ctt.append("}\n");
		 * ctt.append("*+html .clearfix {\n"); ctt.append("min-height: 1%;\n");
		 * ctt.append("}\n"); ctt.append("#frag_modify{\n");
		 * ctt.append("display:block;\n"); ctt.append("}\n"); ctt.append("</style>\n");
		 * ctt.append("\n");
		 */

		// hua.deng add end
		ctt
				.append("<DIV align=left id=block style=\"FONT-SIZE: 9pt; Z-INDEX: 2; FILTER: Alpha(opacity=80); VISIBILITY: hidden; OVERFLOW: visible; CURSOR: hand; POSITION: absolute; BACKGROUND-COLOR: #ffffcc\" onmouseout=hideborder(this);  onclick=clickborder();></DIV>\n");
		ctt.append("<SCRIPT language=JavaScript>\n");
		ctt.append(" var cms_url;\n");
		ctt.append(" function showborder(area, entityid, templateid, tfmapid) {\n");
		ctt.append("	var ibody = document.documentElement;\n");
		ctt.append("	if(ibody.scrollTop==0){\n");
		ctt.append("	ibody=document.body; }\n");
		ctt.append("	var obj = area.getBoundingClientRect();\n");
		ctt.append("	var block = document.all.block;\n");
		ctt.append("	block.style.visibility = \"visible\";\n");
		ctt.append("	block.style.pixelTop = obj.top + ibody.scrollTop-3;\n");
		ctt.append("	block.style.pixelLeft = obj.left + ibody.scrollLeft-2;\n");
		ctt.append("	block.style.width = obj.right-obj.left;\n");
		ctt.append("	block.style.height = obj.bottom-obj.top+1;\n");
		ctt.append("	block.style.border = \"1 solid #ff0000\";\n");
		ctt.append("	block.style.cursor = \"hand\";\n");
		ctt.append("	block.style.overflow = \"auto\";\n");
		ctt.append("	var frag_desc;\n");
		ctt.append("	var frag_type = area.getAttribute(\"frag_type\");\n");
		ctt.append("	var splitperm = area.frag_permission.replace(/\\|/g,'<br>');");
		ctt.append("	if ( frag_type!=null )\n");
		ctt.append("	{\n");
		ctt.append("		if ( frag_type=='1' )\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】静态碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用类型】'+area.frag_quotetype+'<br>【引用碎片】'+area.frag_quotefrag+'<br>【碎片权限】'+splitperm;\n");
		ctt.append("		}\n");
		ctt.append("		else if ( frag_type=='2' )\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】广告碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用类型】'+area.frag_quotetype+'<br>【引用碎片】'+area.frag_quotefrag+'<br>【碎片权限】'+splitperm;\n");
		ctt.append("		}\n");
		ctt.append("		else if( frag_type=='3')\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】动态碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用实体】'+area.frag_entityid;\n");
		ctt.append("		}\n");
		ctt.append("	}\n");
		ctt.append("	block.innerHTML = frag_desc;\n");

		ctt
				.append(" cms_url=\'/fragedit.do?method=view&fragtype=1&entityid=\'+entityid+\'&templateid=\'+templateid+\'&tfmapid=\'+tfmapid;\n");
		ctt.append("}\n");

		// common frag
		ctt
				.append(" function showbordercommon(area, channelid, channeldesc, fragname, permission, entityid, templateid) {\n");
		ctt.append("	var ibody = document.documentElement;\n");
		ctt.append("	if(ibody.scrollTop==0){\n");
		ctt.append("	ibody=document.body; }\n");
		ctt.append("	var obj = area.getBoundingClientRect();\n");
		ctt.append("	var block = document.all.block;\n");
		ctt.append("	block.style.visibility = \"visible\";\n");
		ctt.append("	block.style.pixelTop = obj.top + ibody.scrollTop-3;\n");
		ctt.append("	block.style.pixelLeft = obj.left + ibody.scrollLeft-2;\n");
		ctt.append("	block.style.width = obj.right-obj.left;\n");
		ctt.append("	block.style.height = obj.bottom-obj.top+1;\n");
		ctt.append("	block.style.border = \"1 solid #ff0000\";\n");
		ctt.append("	block.style.cursor = \"hand\";\n");
		ctt.append("	block.style.overflow = \"auto\";\n");
		ctt.append("	var frag_desc;\n");
		ctt.append("	var frag_type = area.getAttribute(\"frag_type\");\n");
		ctt.append("	var splitperm = area.frag_permission.replace(/\\|/g,'<br>');");
		ctt.append("	if ( frag_type!=null )\n");
		ctt.append("	{\n");
		ctt.append("		if ( frag_type=='1' )\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】静态碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用类型】'+area.frag_quotetype+'<br>【引用碎片】'+area.frag_quotefrag+'<br>【所属频道】'+channeldesc+'<br>【碎片权限】'+splitperm;\n");
		ctt.append("		}\n");
		ctt.append("	}\n");
		ctt.append("	block.innerHTML = frag_desc;\n");

		ctt
				.append(" cms_url=\'/fragedit.do?method=view&fragtype=2&channelid=\'+channelid+\'&fragname=\'+fragname+\'&entityid=\'+entityid+\'&templateid=\'+templateid;\n");
		ctt.append("}\n");

		// ADS
		ctt.append(" function gotoads(area, entityid, templateid, tfmapid) {\n");
		ctt.append("	var ibody = document.documentElement;\n");
		ctt.append("	if(ibody.scrollTop==0){\n");
		ctt.append("	ibody=document.body; }\n");
		ctt.append("	var obj = area.getBoundingClientRect();\n");
		ctt.append("	var block = document.all.block;\n");
		ctt.append("	block.style.visibility = \"visible\";\n");
		ctt.append("	block.style.pixelTop = obj.top + ibody.scrollTop-3;\n");
		ctt.append("	block.style.pixelLeft = obj.left + ibody.scrollLeft-2;\n");
		ctt.append("	block.style.width = obj.right-obj.left;\n");
		ctt.append("	block.style.height = obj.bottom-obj.top+1;\n");
		ctt.append("	block.style.border = \"1 solid #ff0000\";\n");
		ctt.append("	block.style.cursor = \"hand\";\n");
		ctt.append("	block.style.overflow = \"auto\";\n");
		ctt.append("	var frag_desc;\n");
		ctt.append("	var frag_type = area.getAttribute(\"frag_type\");\n");
		ctt.append("	var splitperm = area.frag_permission.replace(/\\|/g,'<br>');");
		ctt.append("	if ( frag_type!=null )\n");
		ctt.append("	{\n");
		ctt.append("		if ( frag_type=='2' )\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】广告碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用类型】'+area.frag_quotetype+'<br>【引用碎片】'+area.frag_quotefrag+'<br>【碎片权限】'+splitperm;\n");
		ctt.append("		}\n");
		ctt.append("	}\n");
		ctt.append("	block.innerHTML = frag_desc;\n");

		ctt
				.append(" cms_url=\'/fragedit.do?method=ads&fragtype=1&entityid=\'+entityid+\'&templateid=\'+templateid+\'&tfmapid=\'+tfmapid;\n");
		ctt.append("}\n");

		ctt
				.append(" function gotoadscommon(area, channelid, channeldesc, fragname, permission, entityid, templateid) {\n");
		ctt.append("	var ibody = document.documentElement;\n");
		ctt.append("	if(ibody.scrollTop==0){\n");
		ctt.append("	ibody=document.body; }\n");
		ctt.append("	var obj = area.getBoundingClientRect();\n");
		ctt.append("	var block = document.all.block;\n");
		ctt.append("	block.style.visibility = \"visible\";\n");
		ctt.append("	block.style.pixelTop = obj.top + ibody.scrollTop-3;\n");
		ctt.append("	block.style.pixelLeft = obj.left + ibody.scrollLeft-2;\n");
		ctt.append("	block.style.width = obj.right-obj.left;\n");
		ctt.append("	block.style.height = obj.bottom-obj.top+1;\n");
		ctt.append("	block.style.border = \"1 solid #ff0000\";\n");
		ctt.append("	block.style.cursor = \"hand\";\n");
		ctt.append("	block.style.overflow = \"auto\";\n");
		ctt.append("	var frag_desc;\n");
		ctt.append("	var frag_type = area.getAttribute(\"frag_type\");\n");
		ctt.append("	var splitperm = area.frag_permission.replace(/\\|/g,'<br>');");
		ctt.append("	if ( frag_type!=null )\n");
		ctt.append("	{\n");
		ctt.append("		if ( frag_type=='2' )\n");
		ctt.append("		{\n");
		ctt
				.append("			frag_desc = '【碎片类型】广告碎片<br>【碎片名称】'+area.frag_name+'<br>【碎片描述】'+area.frag_desc+'<br>【引用类型】'+area.frag_quotetype+'<br>【引用碎片】'+area.frag_quotefrag+'<br>【所属频道】'+channeldesc+'<br>【碎片权限】'+splitperm;\n");
		ctt.append("		}\n");
		ctt.append("	}\n");
		ctt.append("	block.innerHTML = frag_desc;\n");

		ctt
				.append(" cms_url=\'/fragedit.do?method=ads&fragtype=2&channelid=\'+channelid+\'&fragname=\'+fragname+\'&entityid=\'+entityid+\'&templateid=\'+templateid;\n");
		ctt.append("}\n");

		ctt.append("function hideborder(area) {\n");
		ctt.append("	area.style.visibility = \"hidden\";\n");
		ctt.append("}\n");

		ctt.append("function clickborder(url) {\n");
		ctt.append("	window.open(cms_url,'','width=600,height=600,resizable=yes,scrollbars=yes');");
		ctt.append("}\n");

		ctt.append("</SCRIPT>\n");
		try {
			// script_code = new String(ctt.toString().getBytes(),"GB2312");
			script_code = Util.changeCode(ctt.toString());
		} catch (Exception uee) {
			log.error(uee.toString());
		}
	}

	public int doEndTag() throws JspException {
		int ret = EVAL_PAGE;
		try {
			ServletRequest request = pageContext.getRequest();
			template = (Template) request.getAttribute(FragTag.TEMPLATE_KEY);
			if (template == null) {
				log.warn("template is null.");
				return ret;
			}

			if (view == -1) { // default view
				ResponseUtils.write(pageContext, "[footer here]");
				return ret;
			}
			if (view == FragTag.FRAG_VIEW) { // 显示碎片
				String entityId = request.getParameter("ENTITYID");
				boolean isMore = false;
//				if (StringUtils.isNotBlank(entityId)) {
				if(false){
					EntityItem entity = (EntityItem) ItemManager.getInstance().get(new Integer(entityId),
							EntityItem.class);
					int channel = entity.getChannel();
					
//					Context channelcontext = new VelocityContext();
//					String channelPerms = VelocityTemplate.getInstance().merge2String(
//							"test_for_fragedit_channel_permision.vm", channelcontext);
					
					ViewContext context = new ViewContext();
					String channelPerms = ViewEngine.getViewManager()
							.getContent("template/frag-edit-permision.vm", context);
					
					if (StringUtils.isNotBlank(channelPerms)) {
						String[] _perms = channelPerms.trim().split(";");
						for (int i = 0; i < _perms.length; i++) {
							if (StringUtils.isNumeric(_perms[i])) {
								int curr_channel = Integer.parseInt(_perms[i]);
								if (channel == curr_channel) {
									isMore = true;
									break;
								}
							}
						}
					}
				}
//				if (isMore) {
				if(true){
					
//					Context context = new VelocityContext();
//					String scriptCode = VelocityTemplate.getInstance().merge2String("FragEdit_script.vm",
//							context);
					
					ViewContext context = new ViewContext();
					String scriptCode = ViewEngine.getViewManager()
							.getContent("template/frag-edit-script.vm", context);

					ResponseUtils.write(pageContext, scriptCode);
				} else {
					ResponseUtils.write(pageContext, script_code);
				}
				return ret;
			}
			if (view == FragTag.PRECOMPILE_VIEW) { // 预编译
				long start = TimeUtils.currentTimeMillis();

				List fraglist = (List) request.getAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY);
				if (fraglist == null) {
					String info =Util.changeCode("没有执行预编译,模板中不存在任何碎片.");
					ResponseUtils.write(pageContext, info);
					return ret;
				}
				// 
				boolean isrepeatfrag = false;
				for (int i = 0; i < fraglist.size(); i++) {
					if (fraglist.get(i) instanceof NewsListTag)
						continue;
					FragTag ft = (FragTag) fraglist.get(i);
					for (int j = 0; j < fraglist.size(); j++) {
						if (fraglist.get(j) instanceof NewsListTag)
							continue;
						if (j == i)
							continue;
						FragTag ft1 = (FragTag) fraglist.get(j);
						if (ft.getName().equalsIgnoreCase(ft1.getName())) {
							String info = Util.changeCode("repeat frag [" + ft.getName() + "] in template"
									+ template.getId() + ".jsp");
							ResponseUtils.write(pageContext, info);
							isrepeatfrag = true;
							break;
						}
						if (isrepeatfrag)
							break;
					}
				}
				if (isrepeatfrag)
					return ret;

				FragTag fragtag;
				Set newfrags = new HashSet();
				Set oldfrags = new HashSet();

				StringBuffer sb = new StringBuffer();
				sb.append("<TABLE cellSpacing=0 cellPadding=0 width=760 border=0>");
				sb.append("<TR><TD align=left>");

				Set tfmapSet = template.getTFMaps();
				if (tfmapSet == null) {
					log.debug("tfmapSet is null, new a HashSet set to template");
					template.setTFMaps(new HashSet());
				}
				tfmapSet = template.getTFMaps();
				Object[] tfms = null;
				if (tfmapSet != null) {
					tfms = tfmapSet.toArray();
					if (tfms == null) {
						log.debug("tfms is null");
					}
				} else {
					log.debug("tfmapSet is null.");
				}

				// 保留原来的碎片信息(Alfred.Yuan)
				Object[] tfmsOrigin = deepCopyTFMaps(tfms);

				log.debug("fraglist.size: " + fraglist.size());

				long p1 = TimeUtils.currentTimeMillis();
				log.debug("Footer-doEndTag: to check.(templateId=" + template.getId() + ")(cost="
						+ (p1 - start) + ")");

				for (int i = 0; i < fraglist.size(); i++) {
					// 过滤NewsListTag,它没有继承自FragTag, NewsListTag属于DFragTag的一部分,
					// 所以用Tag引用
					Tag tag = (Tag) fraglist.get(i);
					if (tag == null) {
						throw new JspTagException("tag [" + i + "] is null.");
					}
					if (tag instanceof NewsListTag)
						continue;

					// NewsListTag之外的碎片都继承自FragTag
					// 包括SFragTag, ADFragTag, DFragTag
					fragtag = (FragTag) fraglist.get(i);
					if (fragtag == null) {
						throw new JspTagException("fragtag [" + i + "] is null.");
					}

					// normal
					TFMap map = null;
					int repeatCount = 0;
					for (int j = 0; tfms != null && j < tfms.length; j++) {
						if (((TFMap) tfms[j]).getName().equalsIgnoreCase(fragtag.getName())) {
							map = (TFMap) tfms[j];
							oldfrags.add(map);
							repeatCount++;
						}
					}
					// added by wangzhigang for verify
					if (repeatCount > 1) {
						log.warn("FRAGNAME " + fragtag.getName() + " repeat " + repeatCount
								+ " times in template " + template.getId()+"fragid:"+map.getId());
						StringBuffer debug=new StringBuffer();
						
						debug.append( Util.changeCode("没有执行预编译,因为在模板 " + template.getName() + " 中碎片 "
								+ fragtag.getName() + " 重复出现 " + repeatCount + " 次."));
						
						debug.append("<br>以下是调试信息：<br>oldfrags:");
						TFMap maptmp = null;
						if(oldfrags!=null){
							for (Iterator it=oldfrags.iterator(); it.hasNext(); ) {
								maptmp = (TFMap) it.next();
								debug.append("id:"+maptmp.getId()+"   name:"+maptmp.getName()+"<br>");
							}
						}
						debug.append("以下是新数据<br>");
						if(newfrags!=null){
							for (Iterator it=newfrags.iterator(); it.hasNext(); ) {
								maptmp = (TFMap) it.next();
								debug.append("id:"+maptmp.getId()+"   name:"+maptmp.getName()+"<br>");
							}
						}						
					
						ResponseUtils.write(pageContext, debug.toString());
						return ret;
					}
					// 若当前模板类型是首页的, 则对动态碎片来说, 引用实体应为专题实体(wangzhigang)
					// 专题实体有两种类型：(1)本树子实体引用;(2)跨树子实体引用(Alfred.Yuan)
					if (fragtag.getType() == FragTag.DFRAG_TYPE) {
						DFragTag dtag = (DFragTag) fragtag;
						if (template.getType() == ItemInfo.HOMEPAGE_TYPE
								&& (dtag.getUet() != DFragTag.UET_TYPE_INTERNAL_ENTITY && dtag.getUet() != DFragTag.UET_TYPE_EXTERNAL_ENTITY)) {
							sb.append("<br><br>" + Util.changeCode("首页类型模板中,动态碎片") + " ["
									+ fragtag.getName() + " : " + fragtag.getDesc() + "] "
									+ Util.changeCode("应为引用(外部)实体类型,且应该引用专题实体") + ".");
							i++;
							continue;
						}
						if (dtag.getUet() == DFragTag.UET_TYPE_INTERNAL_ENTITY
								|| dtag.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY) {
							int entityId = fragtag.getEntityid();
							if (entityId <= 0) {
								sb.append("<br><br>" + Util.changeCode("动态碎片") + " [" + fragtag.getName()
										+ " : " + fragtag.getDesc() + "] " + Util.changeCode("引用了无效的实体")
										+ " [" + entityId + "].");
								i++;
								continue;
							} else {
								EntityItem quoteEitem = (EntityItem) ItemManager.getInstance().get(
										new Integer(entityId), EntityItem.class);
								if (quoteEitem == null) {
									sb.append("<br><br>" + Util.changeCode("动态碎片") + " ["
											+ fragtag.getName() + " : " + fragtag.getDesc() + "] "
											+ Util.changeCode("引用了无效的实体") + " [" + entityId + "].");
									i++;
									continue;
								} else if (quoteEitem.getType() != ItemInfo.SUBJECT_TYPE) {
								//	sb.append("<br><br>" + Util.changeCode("动态碎片") + " ["
								//			+ fragtag.getName() + " : " + fragtag.getDesc() + "] "
								//			+ Util.changeCode("引用的实体") + " [" + entityId + "] "
								//			+ Util.changeCode("不是专题实体") + ".");
								//	i++;
									//continue;
								}
							}
						}
						// 验证多ID的有效性:必须是(子)专题/栏目的ID组合,分隔符是";"(Alfred.Yuan)
						String otherIds = dtag.getOtherIds();
						if (otherIds != null && otherIds.trim().length() > 0) {
							otherIds = otherIds.trim();
							try {
								List idList = EntityParamUtil.getIdList(otherIds, Global.CMSSEP);
								if (idList != null && idList.size() > 0) {
									for (int j = 0; j < idList.size(); j++) {
										Integer otherId = (Integer) idList.get(j);
										EntityItem mutilEntity = (EntityItem) ItemManager.getInstance().get(
												otherId, EntityItem.class);
										if (mutilEntity == null
												|| mutilEntity.getType() != ItemInfo.SUBJECT_TYPE) {
											throw new Exception();
										}
									}
								}
							} catch (Exception e) {
								sb.append("<br><br>" + Util.changeCode("动态碎片") + " [" + fragtag.getName()
										+ " : " + fragtag.getDesc() + "] "
										+ Util.changeCode("的属性(otherIds)非法.格式:subjectId;subjectId;..."));
								i++;
								continue;
							}
						}
					}

					if (map == null) { // 新加碎片
						sb.append("<br><br>" + Util.changeCode("新增碎片") + " [" + fragtag.getName() + " : "
								+ fragtag.getDesc() + "].");
						map = CoreFactory.getInstance().createTFMap();
						newfrags.add(map);
					}
					if (map == null)
						throw new JspTagException("not found tfmap item[" + fragtag.getName() + "]");

					map.setTemplate(template);
					map.setName(fragtag.getName());
					map.setDesc(fragtag.getDesc());
					map.setType(fragtag.getType());
					map.setEntityid(fragtag.getEntityid());

					if (fragtag.getType() == FragTag.SFRAG_TYPE) {
						SFragTag stag = (SFragTag) fragtag;
						map.setQuotetype(stag.getQuotetype());
						map.setQuotefrag(stag.getQuotefrag());
						map.setPermission(stag.getPermission());
					} else if (fragtag.getType() == FragTag.ADFRAG_TYPE) {
						ADFragTag adtag = (ADFragTag) fragtag;
						map.setQuotetype(adtag.getQuotetype());
						map.setQuotefrag(adtag.getQuotefrag());
						map.setPermission(adtag.getPermission());
					} else if (fragtag.getType() == FragTag.DFRAG_TYPE) {
						DFragTag dtag = (DFragTag) fragtag;

						map.setOtherIds(dtag.getOtherIds()); // Alfred.Yuan
						map.setUt(dtag.getUt());
						map.setUet(dtag.getUet());
						map.setRange(dtag.getRange());
						// 为了处理Groovy类型的动态碎片,动态碎片现在也有了quotetype和quotefrag
						map.setQuotetype(dtag.getQuotetype());
						map.setQuotefrag(dtag.getQuotefrag());

						// 处理DFragTag的嵌套碎片
						// modified by wangzhigang 2005.03.11
						// DFragTag内可能嵌套SFragTag,
						// SFragTag会在大循环中处理,此处只处理NewsListTag
						for (int k = i + 1; k < fraglist.size(); k++) {
							Tag newslisttag = (Tag) fraglist.get(k);
							if (newslisttag == null)
								throw new JspTagException("NewsListTag is null. DFragTag[" + dtag.getName()
										+ "].");
							// 此处只处理NewsListTag
							if (!(newslisttag instanceof NewsListTag))
								continue;
							NewsListTag nltag = (NewsListTag) newslisttag;

							map.setListcount(nltag.getListcount());
							map.setTimetype(nltag.getTimetype());
							map.setSorttype(nltag.getSorttype());
							map.setPrio(nltag.getPrio());
							break;
						}
					}
				}

				long p2 = TimeUtils.currentTimeMillis();
				log.debug("Footer-doEndTag: to loop.(templateId=" + template.getId() + ")(cost=" + (p2 - p1)
						+ ")");

				// re-set template tfmap
				if (template == null) {
					log.warn("template is null. can not retainAll.....");
					return ret;
				}
				if (oldfrags == null) {
					log.warn("oldfrags is null. can not retainAll.....");
					return ret;
				}
				if (template.getTFMaps() == null) {
					log.warn("tfmaps is null. can not retainAll.....");
					return ret;
				}
				log.debug("oldfrags.size: " + oldfrags.size() + "  newfrags.size: " + newfrags.size());

				// "引用外部实体"类型:更新被引用的专题(Alfred.Yuan)
				for (int i = 0; tfmsOrigin != null && i < tfmsOrigin.length; i++) {
					TFMap tfMapOfOrigin = (TFMap) tfmsOrigin[i]; // 原先的
					TFMap tfMapOfRetain = null; // 保留的

					Iterator iter4External = oldfrags.iterator();
					while (iter4External.hasNext()) {
						TFMap tfMapOfOld = (TFMap) iter4External.next();
						if (tfMapOfOrigin.getName().equalsIgnoreCase(tfMapOfOld.getName())) {
							tfMapOfRetain = tfMapOfOld;
							break;
						}
					}

					// 该碎片被保留
					if (tfMapOfRetain != null) {
						// "外部实体引用"类型被删除
						if (tfMapOfOrigin.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY
								&& tfMapOfRetain.getUet() != DFragTag.UET_TYPE_EXTERNAL_ENTITY) {
							boolean successDelete = EntityParamUtil.updateSubject4DeleteWeakReference(
									tfMapOfOrigin.getEntityid(), template.getId());
							if (!successDelete) {
								sb.append("<br><br>" + Util.changeCode("删除外部引用碎片失败") + " ["
										+ tfMapOfOrigin.getName() + " : " + tfMapOfOrigin.getDesc() + "].");
							}
							log
									.info("UET_TYPE_EXTERNAL_ENTITY: delete a weak refernce(entityId="
											+ tfMapOfOrigin.getEntityid() + ") (templateId="
											+ template.getId() + ")");
						}

						// 新增"外部实体引用"类型
						if (tfMapOfOrigin.getUet() != DFragTag.UET_TYPE_EXTERNAL_ENTITY
								&& tfMapOfRetain.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY) {
							boolean successAdd = EntityParamUtil.updateSubject4AddWeakReference(tfMapOfRetain
									.getEntityid(), template.getId());
							if (!successAdd) {
								sb.append("<br><br>" + Util.changeCode("新增外部引用碎片失败") + " ["
										+ tfMapOfRetain.getName() + " : " + tfMapOfRetain.getDesc() + "].");
							}
							log
									.info("UET_TYPE_EXTERNAL_ENTITY: add a weak refernce(entityId="
											+ tfMapOfRetain.getEntityid() + ") (templateId="
											+ template.getId() + ")");
						}
					}
					// 该碎片被删除
					else {
						if (tfMapOfOrigin.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY) {
							boolean successDelete = EntityParamUtil.updateSubject4DeleteWeakReference(
									tfMapOfOrigin.getEntityid(), template.getId());
							if (!successDelete) {
								sb.append("<br><br>" + Util.changeCode("删除外部引用碎片失败") + " ["
										+ tfMapOfOrigin.getName() + " : " + tfMapOfOrigin.getDesc() + "].");
							}
							log
									.info("UET_TYPE_EXTERNAL_ENTITY: delete a weak refernce(entityId="
											+ tfMapOfOrigin.getEntityid() + ") (templateId="
											+ template.getId() + ")");
						}
					}
				}
				Iterator iter4External = newfrags.iterator();
				while (iter4External.hasNext()) {
					TFMap tfMapOfNew = (TFMap) iter4External.next(); // 新增的
					if (tfMapOfNew.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY) { // 新增
						boolean successAdd = EntityParamUtil.updateSubject4AddWeakReference(tfMapOfNew
								.getEntityid(), template.getId());
						if (!successAdd) {
							sb.append("<br><br>" + Util.changeCode("新增外部引用碎片失败") + " ["
									+ tfMapOfNew.getName() + " : " + tfMapOfNew.getDesc() + "].");
						}
						log.info("UET_TYPE_EXTERNAL_ENTITY: add a weak refernce(entityId="
								+ tfMapOfNew.getEntityid() + ") (templateId=" + template.getId() + ")");
					}
				}

				long p3 = TimeUtils.currentTimeMillis();
				log.debug("Footer-doEndTag: to external reference.(templateId=" + template.getId()
						+ ")(cost=" + (p3 - p2) + ")");

				// 更新模板和相关碎片(级联更新)(Alfred.Yuan)
				template.getTFMaps().retainAll(oldfrags);
				Iterator it = newfrags.iterator();
				while (it.hasNext()) {
					template.getTFMaps().add(it.next());
				}
				sb.append("</TD></TR></TABLE>");
				ItemManager.getInstance().update(template);
				sb.insert(0, "<br>precompile success.<br>");
				ResponseUtils.write(pageContext, sb.toString());

				long p4 = TimeUtils.currentTimeMillis();
				log.debug("Footer-doEndTag: to update.(templateId=" + template.getId() + ")(cost="
						+ (p4 - p3) + ")");
				log.debug("Footer-doEndTag: to end.(templateId=" + template.getId() + ")(cost="
						+ (p4 - start) + ")");

				return ret;
			}
			// hua.deng add at 2006.12.28
			// 在编译的时候需要输出广告内容
			if (view == FragTag.COMPILE_VIEW || view == FragTag.LIST_VIEW) {
				EntityItem entity = (EntityItem) request.getAttribute(FragTag.ENTITY_KEY);
				if (entity.getType() == ItemInfo.NEWS_TYPE) {
					List speed_content_list = (List) pageContext.getAttribute("speed_content_list");
					if (speed_content_list != null) {
						StringBuffer ad_output = new StringBuffer();
						for (int i = 0; i < speed_content_list.size(); i++) {
							ad_output.append("<div style='display:none' id='hexun_ad_src" + i + "'>\n");
							ad_output.append(speed_content_list.get(i));
							ad_output.append("</div>\n");
						}
						ad_output
								.append("<script>for(var adi=0;adi<"
										+ speed_content_list.size()
										+ ";adi++){document.getElementById('hexun_ad_dst'+adi).innerHTML=document.getElementById('hexun_ad_src'+adi).innerHTML;document.getElementById('hexun_ad_dst'+adi).style.display='';}</script>\n");
						ResponseUtils.write(pageContext, ad_output.toString());
					}
				}
			}
			// hua.deng add end

		} catch (Exception e) {
			log.error("Footer Exception ", e);
		}
		return ret;
	}

	public int getView() {
		return this.view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public static Object[] deepCopyTFMaps(Object[] sources) {

		if (sources == null || sources.length == 0 || !(sources[0] instanceof TFMap)) {
			return null;
		}

		Object[] targets = new Object[sources.length];

		try {
			for (int i = 0; i < sources.length; i++) {
				TFMap source = (TFMap) sources[i];
				TFMap target = CoreFactory.getInstance().createTFMap();
				PropertyUtils.copyProperties(target, source);
				targets[i] = target;
			}
		} catch (Exception e) {
			targets = null;
		}

		return targets;
	}

}
