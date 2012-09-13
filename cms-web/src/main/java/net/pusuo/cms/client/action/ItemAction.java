package net.pusuo.cms.client.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;

public class ItemAction extends BaseAction {

	private static final Log LOG = LogFactory.getLog(ItemAction.class);

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		String itemType = ((String) dForm.get("itemtype")).trim();

		try {
			String refresh = request.getParameter("refresh");
			if (refresh != null && refresh.equals("true")) {
				ItemManager.getInstance().refreshItemListCache(ItemInfo.getItemClass(itemType));
			}
			java.util.List list = ItemManager.getInstance().getList(ItemInfo.getItemClass(itemType));
			System.out.println("commonfrag: "+list.size());
			request.setAttribute("list", list);
		} catch (Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			LOG.error("ItemAction list error . " + e.toString());
			return mapping.findForward("failure");
		}
		return mapping.findForward("list");
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();

		String itemType = ((String) dForm.get("itemtype")).trim();

		Item item = null;

		try {
			if (_id != -1) { // save remove
				item = ItemManager.getInstance().get(id, ItemInfo.getItemClass(itemType));
			} else { // new item
				item = ItemInfo.getItemByType(itemType);
			}
			if (item == null) {
				errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
			} else {
				ItemUtil.putItemValues(dForm, item);
			}
		} catch (Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("ItemAction view error . " + e.toString());
		}
		// Report any errors we have discovered to the failure page 
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			// Go so far, forward to next page
			ret = mapping.findForward("item");
		}
		return ret;
	}

	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		String itemType = ((String) dForm.get("itemtype")).trim();
		Item item = null;

		try {
			/* disabled by Mark 2004.10.19
			 item = ItemManager.getInstance().getItemByName((String)dForm.get("name"),ItemInfo.getItemClass(itemType));
			 if ( item != null) {
			 if ( (_id==-1) || !(_id==item.getId()) ) {
			 errors.add("errors.item.existed",new ActionError("errors.item.existed"));
			 saveErrors(request, errors);
			 return mapping.findForward("failure");
			 }
			 }
			 */

			if (_id != -1) { // update
				/*item = ItemManager.getInstance().getItemByName((String)dForm.get("name"),ItemInfo.getItemClass(itemType));
				 if ( item != null) {
				 if ( (_id==-1) || !(_id==item.getId()) ) {
				 errors.add("errors.item.existed",new ActionError("errors.item.existed"));
				 saveErrors(request, errors);
				 return mapping.findForward("failure");
				 }
				 }*/
				item = ItemManager.getInstance().get(id, ItemInfo.getItemClass(itemType));
				Item itemCopy = (Item) ItemUtil.deepCopy(item);
				// Item itemCopy = item;
				ItemUtil.setItemValues(dForm, itemCopy);
				item = ItemManager.getInstance().update(itemCopy);
			} else { // new
				String name = (String)dForm.get("name");
				String desc = (String)dForm.get("desc");

				item = ItemInfo.getItemByType(itemType);
				ItemUtil.setItemValues(dForm, item);
				item = ItemManager.getInstance().update(item);
			}
			if(item == null)
				throw new Exception("ItemManager.update return null");
			dForm.initialize(mapping);
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			LOG.error("ItemAction save error . " , e);
		}

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("success");
		}
		return ret;
	}

	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();

		String itemType = ((String) dForm.get("itemtype")).trim();
		Item item = null;

		try {
			if (_id != -1) {
				item = ItemManager.getInstance().get(id, ItemInfo.getItemClass(itemType));
				if (item == null) {
					errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				} else {
					ItemManager.getInstance().remove(item);
				}
			} else {
				errors.add("errors.item.id.required", new ActionError("errors.item.id.required"));
			}
		} catch (Exception e) {
			errors.add("errors.item.remove", new ActionError("errors.item.remove"));
			LOG.error("ItemAction remove error . " + e.toString());
		}
		// Report any errors we have discovered to the failure page 
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("success");
		}

		return ret;
	}
}
