/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.List;
import java.util.Map;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Subject;

/**
 * @author Alfred.Yuan
 *
 */
public class TemplateUtil {
	
	private static final int DEFAULT_NEWS_TEMPLATE = 18059;
	
	private static final String TYPE_TEMPLATE_NEWS = "default_news_templ";
	private static final String TYPE_TEMPLATE_VIDEO = "default_video_templ";
	private static final String TYPE_TEMPLATE_ZUTU = "default_zutu_templ";

	public static int processNewsTemplate(Subject parent, String editorTemplate) {
		return processTemplate(parent, editorTemplate, TYPE_TEMPLATE_NEWS);
	}

	public static int processVideoTemplate(Subject parent, String editorTemplate) {
		return processTemplate(parent, editorTemplate, TYPE_TEMPLATE_VIDEO);	
	}

	public static int processZutuTemplate(Subject parent, String editorTemplate) {
		return processTemplate(parent, editorTemplate, TYPE_TEMPLATE_ZUTU);	
	}
	
	/**
	 * ����ģ��
	 * ����:
	 *  1. �༭ѡ�����ȼ����
	 *  2. ���,�𼶱���ר�����͸�����,��ѯĬ��ģ��
	 *  3. Ƶ��Ĭ������
	 *  4. ���,��Ϊ102,ͳһ����ģ��
	 */
	private static int processTemplate(Subject parent, String editorTemplate, String typeTemplate) {
		int newsTemplate = 0;

		// 1.
		if (newsTemplate == 0) {
			if (editorTemplate != null && !editorTemplate.equals("") && !editorTemplate.equals("-1")) {
				newsTemplate = Integer.parseInt(editorTemplate);
			}
		}
		// 2.
		if (newsTemplate == 0) {
			List list = ItemUtil.getEntityParents(parent);
			list.add(0, parent);
			for (int i = 0; i < list.size(); i++) {
				EntityItem e = (EntityItem) list.get(i);
				if (e.getType() != ItemInfo.SUBJECT_TYPE)
					continue;
				Subject s = (Subject) e;
				if (s.getDefaulttemplate() > 0) {
					newsTemplate = s.getDefaulttemplate();
					break;
				}
			}
		}
		// 3.
		if (newsTemplate == 0) {
			Channel c = (Channel) ItemManager.getInstance().get(new Integer(parent.getChannel()), Channel.class);
			Map m = c.getProperties();
			if (m != null) {
				String value = (String) m.get(typeTemplate);
				if (value != null) {
					newsTemplate = Integer.parseInt(value);
				}
			}
		}
		// 4.
		if (newsTemplate == 0)
			newsTemplate = DEFAULT_NEWS_TEMPLATE;
		
		return newsTemplate;
	}

}
