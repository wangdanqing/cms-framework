package net.pusuo.cms.client.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.Item;
import com.hexun.cms.core.Channel;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: �����½�Ƶ����ͬʱ����һ����Ӧ����ҳʵ��
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: 
 * </p>
 * 
 * @author 
 * @version 1.0
 */

public class ChannelAction extends ItemAction {

	private static final Log LOG = LogFactory.getLog(ChannelAction.class);

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer id = ((Integer) dForm.get("id"));

		String itemType = ((String) dForm.get("itemtype")).trim();
		
		try {
			Channel channelItem = null;
			Map properties = null;

			if (null != id && id.intValue() != -1) {
				// ����Channel
				channelItem = (Channel) ItemManager.getInstance().get(id,
						ItemInfo.getItemClass(itemType));

				if (channelItem == null || channelItem.getId() <= 0)
					throw new Exception();

				properties = new HashMap();
				properties.put("default_subject_templ", dForm.get("default_subject_templ"));
				properties.put("default_news_templ", dForm.get("default_news_templ"));
				properties.put("default_video_templ", dForm.get("default_video_templ"));
				properties.put("default_zutu_templ", dForm.get("default_zutu_templ"));
				dForm.set("properties", properties);
				
				Item itemCopy = (Item) ItemUtil.deepCopy(channelItem);

				ItemUtil.setItemValues(dForm, itemCopy);
				channelItem = (Channel) ItemManager.getInstance().update(itemCopy);
				
				if (channelItem == null || channelItem.getId() <= 0)
					throw new Exception("update channel failed");

			} else {
				/* �½��ȴ���Ƶ��ʵ�壻�ٴ�����ҳʵ�� */

				properties = new HashMap();
				properties.put("default_subject_templ", dForm.get("default_subject_templ"));
				properties.put("default_news_templ", dForm.get("default_news_templ"));
				properties.put("default_video_templ", dForm.get("default_video_templ"));
				properties.put("default_zutu_templ", dForm.get("default_zutu_templ"));
				dForm.set("properties", properties);

				// ����Ƶ��ʵ��
				LOG.debug("Create Channel");
				channelItem = (Channel) ItemInfo
						.getItemByType(ItemInfo.CHANNEL_TYPE);
				ItemUtil.setItemValues(dForm, channelItem);
				channelItem = (Channel) ItemManager.getInstance().update(
						channelItem);

				if (channelItem == null || channelItem.getId() <= 0)
					throw new Exception("update channel Item failed");
				LOG.info("Create Channel id=" + channelItem.getId());

				dForm.initialize(mapping);
			}
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			LOG.error("ItemAction save error . " + e);
		}

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			LOG.error("ItemAction save error . " + errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("success");
		}
		return ret;
	}
}
