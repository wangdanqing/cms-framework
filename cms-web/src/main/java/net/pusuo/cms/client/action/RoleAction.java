package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.auth.Perm;
import com.hexun.cms.auth.Role;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;


public class RoleAction extends ItemAction {
	
	private static final Log LOG = LogFactory.getLog(RoleAction.class);
	
	public ActionForward view(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
		
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm)form;
		Integer id = (Integer)dForm.get("id");
		int _id = id.intValue();
		String itemType = ((String)dForm.get("itemtype")).trim();

		Role role = null;

		try{
			if (_id != -1 ) {	// save remove
				role = (Role)ItemManager.getInstance().get(id,ItemInfo.getItemClass(itemType));
			} else { // new item
				role = (Role)ItemInfo.getItemByType(itemType);
			}
			if( role==null ) {
				errors.add("errors.role.notfound", new ActionError("errors.role.notfound"));
			} else {
				
				ItemUtil.putItemValues(dForm,role);
                                
				List list = ItemManager.getInstance().getList(ItemInfo.getItemClass("15"));
				
				List selected = new ArrayList();
				List unselected = new ArrayList();
				if ( null != role.getPerms() ) {
                        		Iterator i = role.getPerms().iterator();
                        		while (i.hasNext()) {
                                		Item item = ItemManager.getInstance().get(new Integer(((Perm)i.next()).getId()),ItemInfo.getItemClass(ItemInfo.PERM_TYPE));
                                		if(item!=null) {
                                			selected.add(new LabelValueBean(item.getDesc(),String.valueOf(item.getId())));						    //}
                        		}}
                                		
				}
				unselected = ItemUtil.ListToLVB(list,unselected);
		
				dForm.set("selected",selected);
				dForm.set("unselected",unselected);
			}
		} catch(Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("ItemAction view error . "+e.toString());
		}
		// Report any errors we have discovered to the failure page 
                if (!errors.isEmpty()) {
                	saveErrors(request, errors);
                	ret = mapping.findForward("failure");
                } else {
			// Go so far, forward to next page
			ret =  mapping.findForward("item");
		}
		return ret;
	}
	
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
				Item itemCopy = (Item) ItemUtil.deepCopy(item);
                                ItemUtil.setItemValues(dForm, itemCopy);
                                item = ItemManager.getInstance().update(itemCopy);
                        } else { // new
                                item = ItemInfo.getItemByType(itemType);
                                ItemUtil.setItemValues(dForm,item);
                                item = ItemManager.getInstance().update(item);
                        }
			if(item == null)
                                throw new Exception("ItemManager.update return null");
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

}
