package net.pusuo.cms.client.taglib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.ResponseUtils;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;

public class DFragTag extends BodyTagSupport implements FragTag, Cloneable {
	private static final Log log = LogFactory.getLog(DFragTag.class);

	public final static int UET_TYPE_SELF_ENTITY = 1; // ��ʵ�����

	public final static int UET_TYPE_INTERNAL_ENTITY = 2; // ����ʵ��

	public final static int UET_TYPE_SUB_ENTITIES = 3; // ��ʵ���б�

	public final static int UET_TYPE_EXTERNAL_ENTITY = 4; // �����ⲿʵ��

	private Template template = null;

	private TFMap tfmap = null;

	private int templateid = -1;

	private int tfid = -1;

	private String id = null;

	private String name = "";

	private int type = FragTag.DFRAG_TYPE;

	private String desc = "";

	private int view = -1;

	private int entityid = -1;

	private String otherIds = "";

	private int ut = -1;

	private int uet = -1;

	// ר��Ȩ�ط�Χ
	// Ĭ�Ϸ�Χ��ȫ����Чר�� 0--100
	private String range = "";

	private int range1 = 0;

	private int range2 = 100;

	/**
	 * 6: ����groovy��Ƭ,��SFragTag��quotetype��ͬ
	 */
	private int quotetype = -1;

	public final static int GROOVY_QUOTETYPE = 6;

	// ��������Ϊ6ʱ, Ҫָ�����õ�Groovy��Ƭ
	private String quotefrag = "";

	public static final String TFID_KEY = "tfid_key";

	private Iterator iterator = null;

	public int doStartTag() throws JspException {
		// default to do bodycontent
		// int ret = EVAL_BODY_TAG;
		int ret = EVAL_PAGE;
		try {
			ServletRequest request = pageContext.getRequest();

			template = (Template) request.getAttribute(FragTag.TEMPLATE_KEY);
			if (template != null && template.getTFMaps() != null) {
				Iterator it = template.getTFMaps().iterator();
				while (it.hasNext()) {
					TFMap tmp = (TFMap) it.next();
					if (tmp != null && tmp.getName().equals(name)) {
						tfmap = tmp;
						tfid = tfmap.getId();
						break;
					}
				}
			}

			if (template == null) {
				log.error("DFragTag -- template is null");
				return ret;
			}

			if (view == -1) {
				// default view , display some frag info
				ResponseUtils.write(pageContext, "[dfrag(" + name + "   "
						+ desc + "]");
				return ret;
			}

			// added by wangzhigang 2005.12.06
			// filter sfrag frag
			String filter = request.getParameter("filter");
			if (filter != null && filter.toLowerCase().equals("nosd")) {
				ResponseUtils.write(pageContext, "[DYNAMIC FRAG (" + name
						+ "  " + desc + ") ]");
				return ret;
			}

			if (view == FragTag.PRECOMPILE_VIEW) {
				// Ԥ����
				List fraglist = (List) request
						.getAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY);
				if (fraglist == null) {
					fraglist = new ArrayList();
				}
				fraglist.add(this.clone());
				request.setAttribute(FragTag.PRECOMPILE_FRAGLIST_KEY, fraglist);
				return ret;
			}

			if (tfmap == null) {
				log.error("DFragTag -- tfmap is null");
				return ret;
			}

			// �ֽ�ר��Ȩ�ط�Χ, range1 range2
			// ����������������Ŀ�б�
			if (range != null && range.length() > 0) {
				String[] rangeStr = range.split("-");
				if (rangeStr.length == 2) {
					range1 = Integer.parseInt(rangeStr[0]);
					range2 = Integer.parseInt(rangeStr[1]);
				}
			}

			EntityItem entity = (EntityItem) request
					.getAttribute(FragTag.ENTITY_KEY);
			if (entity == null) {
				log.error("DFragTag -- entity is null");
				return ret;
			}

			// Ƕ�׵������б�NewsListTag��ʹ��
			pageContext.setAttribute(TFID_KEY, tfmap);

			if (view == FragTag.PRE_VIEW || view == FragTag.FRAG_VIEW
					|| view == FragTag.COMPILE_VIEW
					|| view == FragTag.LIST_VIEW) {
				// frag compile,preview,frag modify
				// set entity to context to be used bodycontent(bean:write)
				// pageContext.setAttribute(name, entity);
				if (uet == UET_TYPE_SELF_ENTITY) // ��ʵ��
				{
					// for this entity
					// set entity to context to be used NewsListTag
					pageContext.setAttribute(id, entity);
				} else if (uet == UET_TYPE_INTERNAL_ENTITY
						|| uet == UET_TYPE_EXTERNAL_ENTITY) {
					// for special entity
					// 2�����ʵ���ڱ�ʵ���������(wangzhigang)
					// 4�����ʵ���ڱ�����У����ڿ�������(Alfred.Yuan)
					// ��pageContextȡʵ��, ��̬ȡʵ��, ������ͨ��Ԥ����õ���ʵ��ID
					EntityItem item = (EntityItem) ItemManager.getInstance()
							.get(new Integer(this.entityid),
									ItemInfo.getEntityClass());

					// set entity to context to be used NewsListTag
					pageContext.setAttribute(id, item);
				} else if (uet == UET_TYPE_SUB_ENTITIES) {
					// "��ʵ���б�"��"(�ⲿ)ʵ������"�������͵Ľ��(Alfred.Yuan)
					if (this.entityid > 0) {
						log
								.info("UET_TYPE_SUB_ENTITIES: combine sub with internal/external type.(entityid="
										+ entityid + ")");
						entity = (EntityItem) ItemManager.getInstance().get(
								new Integer(this.entityid),
								ItemInfo.getEntityClass());
						if (entity == null) {
							log
									.error("UET_TYPE_SUB_ENTITIES error: entityid is invalid!");
							return ret;
						}
					}

					List children = ItemUtil.getEntityChildrenByPriority(entity
							.getId(), ItemInfo.SUBJECT_TYPE, range1, range2);
					if (children == null) {
						log.error("DFragTag -- getChildren is null");
					}
					iterator = children.iterator();
					if (iterator.hasNext()) {
						Object element = iterator.next();
						if (element == null) {
							log.error("element is null "
									+ ((EntityItem) element).getId());
							pageContext.removeAttribute(id);
						} else {
							pageContext.setAttribute(id, element);
						}
					} else {
						ret = SKIP_BODY;
					}
				}
				return ret;
			}

			// invalid view
			// ResponseUtils.write(pageContext,"sorry , there are not
			// information for the view mode["+view+"]");
		} catch (Exception e) {
			log.error("DFragTag Exception : " + e.getMessage());
			// throw new JspTagException("DFragTag Exception :
			// "+e.getMessage());
		}
		return ret;
	}

	public int doEndTag() throws JspException {
		// clean up
		iterator = null;
		return (EVAL_PAGE);
	}

	public int doAfterBody() throws JspException {
		if (uet == UET_TYPE_SUB_ENTITIES && iterator != null) {
			if (iterator.hasNext()) {
				Object element = iterator.next();
				if (element == null) {
					log.error("element is null " + id);
					pageContext.removeAttribute(id);
				} else {
					pageContext.setAttribute(id, element);
					return EVAL_BODY_AGAIN;
				}
			}
		}
		return SKIP_BODY;
	}

	public int getTfid() {
		return this.tfid;
	}

	public String getId() {
		return this.id;
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

	public int getEntityid() {
		return this.entityid;
	}

	public int getUt() {
		return this.ut;
	}

	public int getUet() {
		return this.uet;
	}

	public void setTfid(int tfid) {
		this.tfid = tfid;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setEntityid(int entityid) {
		this.entityid = entityid;
	}

	public String getOtherIds() {
		return this.otherIds;
	}

	public void setOtherIds(String otherIds) {
		this.otherIds = otherIds;
	}

	public void setUt(int ut) {
		this.ut = ut;
	}

	public void setUet(int uet) {
		this.uet = uet;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getRange() {
		return this.range;
	}

	public void setTemplateid(int templateid) {
		this.templateid = templateid;
	}

	public int getTemplateid() {
		return this.templateid;
	}

	public String toString() {
		return "����Ƭ���͡���̬��Ƭ<br>����Ƭ��ơ�" + name + "<br>����Ƭ������" + desc
				+ "<br>������ʵ�塿" + entityid + "<br>���������͡�" + ut + "<br>��UET���͡�";
	}

	public String getQuotefrag() {
		return quotefrag;
	}

	public void setQuotefrag(String quotefrag) {
		this.quotefrag = quotefrag;
	}

	public int getQuotetype() {
		return quotetype;
	}

	public void setQuotetype(int quotetype) {
		this.quotetype = quotetype;
	}
}
