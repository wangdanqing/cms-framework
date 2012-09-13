package net.pusuo.cms.client.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.ResponseUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.hexun.cms.ItemInfo;
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
import com.hexun.cms.client.ad.ADManager;
import com.hexun.cms.client.ad.ADPManager;

public class ADFragTag extends TagSupport implements FragTag, Cloneable {
	private static final Log log = LogFactory.getLog(ADFragTag.class);

	// �������ģ��
	private Template template = null;

	// ��ʵ��, ��û�а��κ�ʵ��, ����ʾ��Ƭ����. Ԥ���벻��Ҫָ��ʵ��
	private EntityItem entity = null;

	// 
	private int view = -1;

	private int type = FragTag.ADFRAG_TYPE;

	private String name = "";

	private String desc = "";

	private String permission = "";

	// hua.deng add at 2006.12.28
	// Ϊ�˰Ѷ�Ӧ�Ĺ����٣�
	// ���������ǩ�������ô��ǩΪtrue,������Ӧ�Ĺ��λֻ����һ�����ص�����λ<span id="xxx"></span>
	// ���б�ǩidΪ��־ID����ǩ�����ڸ�ҳ����󲿷����,Ȼ����JS������������
	private boolean speedup = false;

	// hua.deng add end

	// ��������, ��Χ 1--5
	// 1: ��ʵ��ʹ��
	// 2: ͬģ�岻ͬʵ�干��
	// 3: ������ҳʵ����Ƭ
	// 4: ͬƵ����ͬʵ�干��
	// 5: HEXUN������Ƭ
	private int quotetype = -1;

	// ��������Ϊ3,4,5ʱ, Ҫָ�����õ���Ƭ
	private String quotefrag = "";

	private static final int AD_INCLUDE = 1;

	public int doStartTag() throws JspException {
		try {
			ServletRequest request = pageContext.getRequest();

			template = (Template) request.getAttribute(FragTag.TEMPLATE_KEY);
			entity = (EntityItem) request.getAttribute(FragTag.ENTITY_KEY);

		} catch (Exception e) {
			log.error("ADFragTag Exception : " + e.toString());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		int ret = EVAL_PAGE;

		// ��ǰ���ʵ�ģ��Ϊnull, ˵��request.getAttribute()Ϊ null
		if (template == null) {
			log.warn("ADFragTag --> template is null.");
			return ret;
		}

		try {
			ServletRequest request = pageContext.getRequest();
			if (view == -1) {
				// default view , display some frag info
				ResponseUtils.write(pageContext, "[adfrag(" + name + "  " + desc + ")]");
				return ret;
			}

			// Ԥ����, ����ָ��ʵ��. �������Ƭ���浽request��, ��Footer Tag����
			if (view == FragTag.PRECOMPILE_VIEW) {
				// pre compile
				List fraglist = (List) request.getAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY);
				if (fraglist == null) {
					fraglist = new ArrayList();
				}
				fraglist.add(this.clone());
				request.setAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY, fraglist);
				return ret;
			}

			// ��Ԥ��������������Ҫָ��ʵ��
			if (entity == null) {
				log.warn("ADFragTag --> entity is null.");
				return ret;
			}

			EntityItem hpe = ItemUtil.getHpEntity(entity);
			if (hpe == null) {
				log.warn("ADFragTag --> hpe is null.");
				return ret;
			}

			TFMap tfmap = null;
			Set tfmaps = template.getTFMaps();
			if (tfmaps != null) {
				Iterator itr = tfmaps.iterator();
				while (itr.hasNext()) {
					TFMap tmp = (TFMap) itr.next();
					if (tmp.getName().equals(this.name)) {
						tfmap = tmp;
						break;
					}
				}
			}

			// �����?�л����, �������д���
			if (entity.getType() == ItemInfo.NEWS_TYPE && tfmap != null
					&& (tfmap.getName().equals("bigadfrag") || tfmap.getName().equals("bigadfrag2"))) {
				return ret;
			}

			// ��quotetype=4,5 ʱ, ��ȡ��Ƭ��Ϣ
			CommonFrag commonFrag = getCommonFrag();
			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(entity.getChannel()), Channel.class);
			if (quotetype == 1) {
				if (tfmap == null) {
					log.warn("tfmap is null, check tfmap [" + name + "  " + desc + "].");
					return ret;
				}
			} else if (quotetype == 4) {
				commonFrag = getCommonFrag();
				if (commonFrag == null) {
					log.warn("commonFrag is null, check tfmap [" + name + "  " + desc + "].");
					return ret;
				}
			}

			// ��ʾ��Ƭ����
			if (view == FragTag.PRE_VIEW) { // frag preview
				String content = null;
				String test = request.getParameter("test");
				if (test != null && test.equalsIgnoreCase("yes")) {
					if (quotetype == 1) {
						String testtfid = request.getParameter("testtfid");
						int i_testtfid = -1;
						try {
							if (testtfid != null)
								i_testtfid = Integer.parseInt(testtfid);
						} catch (Exception e) {
						}
						if (i_testtfid == tfmap.getId()) {
							content = request.getParameter("testcont");

							if (content == null)
								content = "";
							// content = Util.unicodeToGB2312(content);
							ResponseUtils.write(pageContext, content);
							return ret;
						} else {
							// get mapping entity
							EntityItem mEntity = getMappingEntity(tfmap, entity);
							if (mEntity == null) {
								log.warn("getMappingEntity is null.");
								return ret;
							}
							content = ClientFile.getInstance().read(
									PageManager.getFStorePath(mEntity, tfmap.getId(), true));
						}
					} else if (quotetype == 4) {
						content = ClientFile.getInstance().read(
								PageManager.getFStorePath(commonFrag.getChannel(), hpe.getId(), commonFrag.getName(),
										true));
					}
				} else {
					if (quotetype == 1) {
						// get mapping entity
						EntityItem mEntity = getMappingEntity(tfmap, entity);
						if (mEntity == null) {
							log.warn("getMappingEntity is null.");
							return ret;
						}
						content = ClientFile.getInstance()
								.read(PageManager.getFStorePath(mEntity, tfmap.getId(), true));
					} else if (quotetype == 4) {
						content = ClientFile.getInstance().read(
								PageManager.getFStorePath(commonFrag.getChannel(), hpe.getId(), commonFrag.getName(),
										true));
					}
				}
				if (content == null)
					content = "";
				else
					content = Util.unicodeToGBK(content);
				ResponseUtils.write(pageContext, content);
				return ret;
			}

			// ��Ƭ����
			if (view == FragTag.FRAG_VIEW) {
				// added by wangzhigang 2005.12.06
				// filter ad frag
				String filter = request.getParameter("filter");
				if (filter != null && filter.toLowerCase().equals("noad")) {
					ResponseUtils.write(pageContext, "[AD FRAG (" + name + "  " + desc + ") ]");
					return ret;
				}

				StringBuffer ctt = new StringBuffer();
				String content = null;

				ctt.append("<span id=\"cms4_template_frag\" ");
				ctt.append(" name=\"cms4_template_frag\"");
				ctt.append(" frag_type=" + FragTag.ADFRAG_TYPE);
				ctt.append(" frag_name=\"" + name + "\"");
				ctt.append(" frag_desc=\"" + desc + "\"");

				if (quotetype == 1) {
					// mEntity.getId() ����ȷ����Ƭ�洢λ��
					// entity.getId() ��ǰ�󶨵�ʵ��, �����������
					EntityItem mEntity = getMappingEntity(tfmap, entity);
					if (mEntity == null) {
						log.warn("getMappingEntity is null.");
						return ret;
					}
					// ctt.append("
					// onmouseover=gotoads(this,"+mEntity.getId()+","+template.getId()+","+tfmap.getId()+");");
					ctt.append(" frag_id=\""+tfmap.getId()+"\"");
					ctt.append(" frag_template_id=\""+template.getId()+"\" ");
					ctt.append(" frag_entity_id=\""+entity.getId()+"\" ");
					
					ctt.append(" frag_quotetype=" + quotetype);
					if (quotefrag == null || quotefrag.equals("")) {
						ctt.append(" frag_quotefrag ");
					} else {
						ctt.append(" frag_quotefrag=\"" + quotefrag + "\"");
					}
					if (tfmap.getPermission() == null || tfmap.getPermission().equals("")) {
						ctt.append(" frag_permission ");
					} else {
						ctt.append(" frag_permission=\"" + tfmap.getPermission() + "\"");
					}
					// ///////// get path
					content = ClientFile.getInstance().read(PageManager.getFStorePath(mEntity, tfmap.getId(), true));
				} else if (quotetype == 4) {
					String perm = commonFrag.getPermission() == null ? "" : commonFrag.getPermission();
					String channelDesc = channel == null ? "" : channel.getDesc();
					// quotetype=4�����. ������Ƭ�Ĵ洢λ�ò��������κ�ʵ��
					// entity.getId() template.getId() Ҳ�������������
					
					ctt.append(" frag_channel_desc=\""+channelDesc+"\" ");
					ctt.append(" frag_channel_id=\""+commonFrag.getChannel()+"\" ");
					
					ctt.append(" frag_id=\""+tfmap.getId()+"\"");
					ctt.append(" frag_template_id=\""+template.getId()+"\" ");
					ctt.append(" frag_entity_id=\""+entity.getId()+"\" ");
					
					ctt.append(" frag_quotetype=" + quotetype);
					ctt.append(" frag_quotefrag=\"" + quotefrag + "\"");
					if (commonFrag.getPermission() == null || commonFrag.getPermission().equals("")) {
						ctt.append(" frag_permission");
					} else {
						ctt.append(" frag_permission=\"" + commonFrag.getPermission() + "\"");
					}
					if (quotetype == 4) {
						content = ClientFile.getInstance().read(
								PageManager.getFStorePath(commonFrag.getChannel(), hpe.getId(), commonFrag.getName(),
										true));
					}
				}
				ctt.append(" >");

				if (content == null || content.trim().equals("")) {
					content = Util.unicodeToGBK("����Ƭ��ơ�") + name + "<br>";
					content += Util.unicodeToGBK("����Ƭ������") + desc + "<br>";
				} else {
					content = Util.unicodeToGBK(content).trim();
				}
				ctt.append(content);
				ctt.append("</span>");

				/*
				 * if( entity.getType()==ItemInfo.NEWS_TYPE ) {
				 * ResponseUtils.write(pageContext,content); } else {
				 * ResponseUtils.write(pageContext,ctt.toString()); }
				 */
				ResponseUtils.write(pageContext, ctt.toString());
				return ret;
			}

			// ����ģ�����
			if (view == FragTag.COMPILE_VIEW || view == FragTag.LIST_VIEW) {
				String content = "";
				// frag compile
				if (quotetype == 1) {
					EntityItem mEntity = getMappingEntity(tfmap, entity);
					if (mEntity == null) {
						log.warn("getMappingEntity is null.");
						return ret;
					}
					// ����ģ��,ֱ��д��Ƭ����,д���ļ�
					if (entity.getType() == ItemInfo.NEWS_TYPE) {
						log.debug("entity.getId()==" + entity.getId() + "entity.getChannel()==" + entity.getChannel()
								+ "getBelongParentEntity(entity)==" + getBelongParentEntity(entity));
						if (getBelongParentEntity(entity)) {
							content = "<!--#include virtual=\""
									+ PageManager.getFStorePath(mEntity, tfmap.getId(), false) + "\" -->";
						} else {
							content = ClientFile.getInstance().read(
									PageManager.getFStorePath(mEntity, tfmap.getId(), true));
							if (StringUtils.isBlank(content))
								content = "";
							else
								content = outputAdContent(content);
						}
					} else {
						content = "<!--#include virtual=\""
								+ PageManager.getFStorePath(mEntity, tfmap.getId(), false) + "\" -->";
					}
				} else if (quotetype == 4) {
					// ����ģ��,ֱ��д��Ƭ����,д���ļ�
					if (entity.getType() == ItemInfo.NEWS_TYPE) {
						content = ClientFile.getInstance().read(
								PageManager.getFStorePath(commonFrag.getChannel(), hpe.getId(), commonFrag.getName(),
										true));
						if (content == null)
							content = "";
						else{
							content = outputAdContent(content);
						}
					} else {
						content = "<!--#include virtual=\""
								+ PageManager.getFStorePath(commonFrag.getChannel(), hpe.getId(), commonFrag.getName(),
										false) + "\" -->";
					}
				}
				ResponseUtils.write(pageContext, content);
				return ret;
			}

			// invalid view
			// log.error("sorry , there are not information for the view
			// mode["+view+"]");
			// ResponseUtils.write(pageContext,"");
		} catch (Exception e) {
			log.error("ADFragTag Exception : " + e.toString());
		}
		return ret;
	}

	/**
	 * hua.deng add at 2006.12.28
	 * �ж�speedup���ԣ�������ľ������ݣ����������־λ
	 * @param request
	 * @param content
	 * @return
	 */
	private String outputAdContent(String content) {
		if(speedup==true&&StringUtils.isNotBlank(content)){
			List  speed_content_list=(List) pageContext.getAttribute("speed_content_list");
			if(speed_content_list==null)speed_content_list=new ArrayList();
			speed_content_list.add(content =  Util.unicodeToGBK(content) );
			pageContext.setAttribute("speed_content_list", speed_content_list);
			content="<span id=\"hexun_ad_dst"+(speed_content_list.size()-1)+"\" style=\"display:none\"></span>";
		}else{
			content =  Util.unicodeToGBK(content);
		}
		return content;
	}

	private EntityItem getMappingEntity(TFMap tfmap, EntityItem eItem) {
		if (eItem.getType() == ItemInfo.HOMEPAGE_TYPE)
			return eItem;

		List list = ItemUtil.getEntityParents(eItem);
		EntityItem ret = null;

		for (int i = 0; list != null && i < list.size(); i++) {
			EntityItem entity = (EntityItem) list.get(i);
			if (ADManager.getInstance().belong(tfmap.getId(), entity.getId())) {
				ret = entity;
				break;
			}
		}
		// �˹�治�Ƿ�ר��Ͷ��, ����ʵ�����Ƭ��Ͷ����ҳʵ������, ר����Ͷ�ڱ�ר��ʵ������
		if (ret == null) {
			if (eItem.getType() == ItemInfo.NEWS_TYPE) {
				return getHpEntity(eItem);
			} else {
				return eItem;
			}
		} else {
			return ret;
		}
	}

	/**
	 * 
	 * @name getBelongParentEntity
	 * @param EntityItem
	 * @return boolean
	 */
	private boolean getBelongParentEntity(EntityItem eItem) {

		List list = ItemUtil.getEntityParents(eItem);
		EntityItem ret = null;
		boolean eflag = false;
		for (int i = 0; list != null && i < list.size(); i++) {
			EntityItem entity = (EntityItem) list.get(i);
			if (ADPManager.getInstance().belong(entity.getId(), AD_INCLUDE)) {
				eflag = true;
				break;
			}
		}
		return eflag;
	}

	/**
	 * ���ظ�ʵ������������ҳʵ��
	 * 
	 * @param ��ʵ��
	 *            �÷���������ʵ���category�ֶ�
	 */
	private EntityItem getHpEntity(EntityItem eItem) {
		String category = eItem.getCategory();
		if (category == null || category.equals("")) {
			return null;
		}
		String hpId = category.split(Global.CMSSEP)[0];
		return (EntityItem) ItemManager.getInstance().get(new Integer(hpId), EntityItem.class);
	}

	/**
	 * ��������ʵ�� ������quotetypeΪ1,2��3����� ʵ�������Ƭ�洢λ�� quotetype=1 ��ƬӦ�ô洢�ڵ�ǰ�󶨵�ʵ������
	 * quotetype=2��3 ��ƬӦ�ô洢����ҳʵ������
	 */
	private EntityItem getQuoteEntity() {
		EntityItem ret = null;
		switch (quotetype) {
		case 1:
			ret = entity;
			break;
		case 2:
			ret = getHpEntity(entity);
			break;
		case 3:
			ret = getHpEntity(entity);
			break;
		}
		return ret;
	}

	/**
	 * ����ʵ��ʵ�ֵ�ģ��
	 * 
	 * @param eItem
	 *            ʵ��
	 * @return ��quotetype=3ʱ����, ���ڷ�����ҳʵ��ʵ�ֵ���ģ�� ��quotetype=1,2ʱ, ���ص�ǰ�������ģ��
	 */
	private Template getQuoteTemplate(EntityItem eItem) {
		Template ret = null;

		switch (quotetype) {
		case 1:
		case 2:
			ret = template;
			break;
		case 3:
			if (eItem == null)
				return null;
			String templateStr = eItem.getTemplate();
			if (templateStr != null && !templateStr.equals("")) {
				String templates = templateStr.split(Global.CMSSEP)[0];
				String templateid = templates.split(Global.CMSCOMMA)[0];
				ret = (Template) ItemManager.getInstance().get(new Integer(templateid), Template.class);
			}
			break;
		}
		return ret;
	}

	/**
	 * ������Ƭ��Ϣ
	 * 
	 * @param t
	 *            ��Ƭ����ģ��
	 * @return ������Ƭ��Ϣ. ��ģ��Ϊnull, ��ģ���в����ڸ���Ƭ���,�򷵻�null quotetype=1,2
	 *         ��ƬӦ�����ڵ�ǰ�������ģ�� quotetype=3 ��Ϊ��������ҳҳ���е���Ƭ, ����Ҫָ��������Ƭquotefrag
	 *         ������ҳʵ��ʵ�ֵ���ģ�����Ƭ
	 */
	private TFMap getQuoteTFMap(Template t) {
		if (t == null)
			return null;
		Set tfmaps = t.getTFMaps();
		if (tfmaps == null)
			return null;

		TFMap ret = null;

		switch (quotetype) {
		case 1:
		case 2:
			for (Iterator it = tfmaps.iterator(); it.hasNext();) {
				TFMap tfmap = (TFMap) it.next();
				if (tfmap.getName().equals(name)) {
					ret = tfmap;
					break;
				}
			}
			break;
		case 3:
			for (Iterator it = tfmaps.iterator(); it.hasNext();) {
				TFMap tfmap = (TFMap) it.next();
				if (tfmap.getName().equals(quotefrag)) {
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
	private CommonFrag getCommonFrag() {
		int channelId = -1;
		if (quotetype == 4) {
			channelId = entity.getChannel();
		}
		if (quotetype == 5) {
			channelId = -1;
		}

		List list = ItemManager.getInstance().getList(CommonFrag.class);
		for (int i = 0; list != null && i < list.size(); i++) {
			CommonFrag cf = (CommonFrag) list.get(i);
			if (cf.getChannel() == channelId && cf.getName().equals(quotefrag)) {
				return cf;
			}
		}
		return null;
	}

	public String getName() {
		return this.name;
	}

	public int getType() {
		return this.type;
	}

	public String getDesc() {
		return this.desc;
	}

	public int getView() {
		return this.view;
	}

	public int getQuotetype() {
		return this.quotetype;
	}

	public String getQuotefrag() {
		return this.quotefrag;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setView(int view) {
		this.view = view;
	}

	public void setQuotetype(int quotetype) {
		this.quotetype = quotetype;
	}

	public void setQuotefrag(String quotefrag) {
		this.quotefrag = quotefrag;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getTemplateid() {
		return -1;
	}

	public void setTemplateid(int templateid) {
	}

	public int getEntityid() {
		return -1;
	}

	public void setEntityid(int entityid) {
	}

	public String toString() {
		return "����Ƭ���͡������Ƭ<br>����Ƭ��ơ�" + name + "<br>����Ƭ������" + desc + "<br>���������͡�" + quotetype + "<br>��������Ƭ��"
				+ quotefrag + "<br>����ƬȨ�ޡ�" + permission;
	}

	public void setSpeedup(boolean speedup) {
		this.speedup = speedup;
	}

	public boolean getSpeedup() {
		return this.speedup;
	}
}
