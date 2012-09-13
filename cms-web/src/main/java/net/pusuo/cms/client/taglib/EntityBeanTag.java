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

import com.hexun.cms.Global;
import com.hexun.cms.core.*;
import com.hexun.cms.client.ItemManager;

public class EntityBeanTag extends TagSupport {
	private static final Log log = LogFactory.getLog(EntityBeanTag.class);

	private String id = "";

	private int view = -1;

	private String property = "";

	public int doStartTag() throws JspException {
		int ret = SKIP_BODY;
		try {
			ServletRequest request = pageContext.getRequest();
			if (view == -1) {
				// default view , display some frag info
				return ret;
			}
			if (view == FragTag.PRECOMPILE_VIEW) {
				// pre compile
				return ret;
			}
			if (view == FragTag.PRE_VIEW || view == FragTag.FRAG_VIEW
					|| view == FragTag.COMPILE_VIEW
					|| view == FragTag.LIST_VIEW) {
				// ����ģ����ֱ������ʵ��ID������
				EntityItem entity = (EntityItem) ItemManager.getInstance().get(
						new Integer(id), EntityItem.class);
				if (entity == null) {
					log.error("EntityBeanTag --> entity is null.");
					return ret;
				}

				if (property.equals("navigation")) {
					String navigation = getNavigation(entity);
					if (navigation != null)
						ResponseUtils.write(pageContext, navigation);
					return ret;
				}

				pageContext.setAttribute("entity", entity);
				Object obj = RequestUtils.lookup(pageContext, "entity",
						property, null);
				if (obj != null) {
					if("url".equals(property)){
						String url = (String)obj;
						if ( entity.getType() == EntityItem.SUBJECT_TYPE && url.endsWith("index.html")) {
							url = url.substring(0, url.lastIndexOf("index.html"));
							obj = url;
						}
					}
					ResponseUtils.write(pageContext, obj.toString());
				} else {
					// error
					if (view == FragTag.COMPILE_VIEW) {
						ResponseUtils.write(pageContext, "");
					} else {
						ResponseUtils.write(pageContext, "not found property["
								+ property + "]");
					}
				}
			}
		} catch (Exception e) {
			log.error("DFragBeanTag Exception : " + e.toString());
			//throw new JspTagException("DFragBeanTag Exception :
			// "+e.toString());
		}
		return ret;
	}

	/**
	 * ����
	 */
	private String getNavigation(EntityItem entity) {
		String category = entity.getCategory();
		if (category == null || category.equals(""))
			return null;

		String[] categorys = category.split(Global.CMSSEP);

		StringBuffer sb = new StringBuffer();
		int backCount = categorys.length - 1;
		for (int i = 0; i < backCount; i++) {
			EntityItem eItem = (EntityItem) ItemManager.getInstance().get(
					new Integer(categorys[i]), EntityItem.class);
			String desc = eItem.getDesc();
			String url = eItem.getUrl();
			
			/*ר�����Ŀʵ��,����β��index.html,��ȥ����,��Ŀ¼�ķ�ʽ��ʾ����*/
			if (eItem.getType()==EntityItem.SUBJECT_TYPE && url.endsWith("index.html")) {
				url = url.substring(0, url.lastIndexOf("index.html"));
			}
			sb.append("<a href=");
			sb.append(url);
			sb.append(">");
			sb.append(desc);
			if (i == backCount - 1) {
				sb.append("</a>");
			} else {
				sb.append("</a> &gt; ");
			}
		}
		return sb.toString();
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

	public String getProperty() {
		return this.property;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setView(int view) {
		this.view = view;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	private static final long ct() {
		return System.currentTimeMillis();
	}
}
