package net.pusuo.cms.client.action;

import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.util.LabelValueBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ItemRefresher;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.auth.Role;
import com.hexun.cms.auth.Perm;
import com.hexun.cms.client.auth.AuthenticationFactory;


public class PermAction extends ItemAction {
	
	private static final Log LOG = LogFactory.getLog(PermAction.class);
	
	public ActionForward save(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
                ActionForward ret = null;

                ActionErrors errors = new ActionErrors();
                BaseForm dForm = (BaseForm)form;

                Integer id = (Integer)dForm.get("id");
                int _id = id.intValue();
                String itemType = ((String)dForm.get("itemtype")).trim();
                Item item = null;

                try{
                        /*
			item = ItemManager.getInstance().getItemByName((String)dForm.get("name"),ItemInfo.getItemClass(itemType));
                        if ( item != null) {
                                if ( (_id==-1) || !(_id==item.getId()) ) {
                                        errors.add("errors.item.existed",new ActionError("errors.item.existed"));
                                        saveErrors(request, errors);
                                        return mapping.findForward("failure");
                                }
                        }
			*/

                        if ( _id!=-1 ) { // update
                                item = ItemManager.getInstance().get(id,ItemInfo.getItemClass(itemType));
                                ItemUtil.setItemValues(dForm,item);
                                item = ItemRefresher.update(item);;
                        } else { // new
                                item = ItemInfo.getItemByType(itemType);
                                ItemUtil.setItemValues(dForm,item);
                                item = ItemManager.getInstance().update(item);;
                        }
                        dForm.initialize(mapping);
                }catch(Exception e){
                        errors.add("errors.item.save", new ActionError("errors.item.save"));
                        LOG.error("ItemAction save error . "+e.toString());
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

	public ActionForward remove(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
				ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
                BaseForm dForm = (BaseForm)form;
		Integer id = (Integer)dForm.get("id");
		int _id = id.intValue();

		String itemType = ((String)dForm.get("itemtype")).trim();
		Item item = null;

		try {
			if ( _id!=-1 ) {
				item = ItemManager.getInstance().get(id,ItemInfo.getItemClass(itemType));
				if(item==null){
					errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				} else {
					ItemRefresher.remove(item);
					request.getSession().removeAttribute(AuthenticationFactory.SESSION_AUTHENTICATION);
				}
			} else {
				errors.add("errors.item.id.required", new ActionError("errors.item.id.required"));
			}
		}catch(Exception e){
			errors.add("errors.item.remove", new ActionError("errors.item.remove"));
			LOG.error("PermAction remove error . "+e.toString());
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
