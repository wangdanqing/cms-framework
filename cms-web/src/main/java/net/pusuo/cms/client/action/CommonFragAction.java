package net.pusuo.cms.client.action;

import java.util.List;
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
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.CommonFrag;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.file.ClientFile;

public class CommonFragAction extends ItemAction {

	private static final Log log = LogFactory.getLog(CommonFragAction.class);

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
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			String name = (String)dForm.get("name");
			int channel = ((Integer)dForm.get("channel")).intValue();

			int count = 0;
			for(int i=0; list!=null && i<list.size(); i++)
			{
				CommonFrag cf = (CommonFrag)list.get(i);
				if( cf.getId()==_id ) continue;

				if( cf.getChannel()==channel && cf.getName().equals(name) )
				{
					++count;
				}
			}
			if( count>0 )
			{
				errors.add("errors.commonfrag", new ActionError("errors.commonfrag.repeat"));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			
			if (_id != -1) { // update
				item = ItemManager.getInstance().get(id, ItemInfo.getItemClass(itemType));
				Item itemCopy = (Item) ItemUtil.deepCopy(item);
				// Item itemCopy = item;
				ItemUtil.setItemValues(dForm, itemCopy);
				item = ItemManager.getInstance().update(itemCopy);
			} else { // new
				item = ItemInfo.getItemByType(itemType);
				ItemUtil.setItemValues(dForm, item);
				item = ItemManager.getInstance().update(item);
				//refresh listcache
				ItemManager.getInstance().refreshItemListCache(ItemInfo.getItemClass(itemType));
			}
			if(item == null) throw new Exception("ItemManager.update return null");
			dForm.initialize(mapping);
		} catch (Exception e) {
			errors.add("errors.commonfrag", new ActionError("errors.commonfrag.save",e.toString()));
			log.error("save common frag exception. ", e);
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
				if( item==null )
				{
					errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				} else {
					int channel = ((Integer)dForm.get("channel")).intValue();
					String name = (String)dForm.get("name");
					String storepath = null;

					List homepageList = ItemUtil.getEntityChildren(-1, ItemInfo.HOMEPAGE_TYPE);
					if( channel==-1 )
					{
						// hexun������Ƭ
						storepath = PageManager.getFStorePath( name );
						String content = ClientFile.getInstance().read( storepath );
						if( content!=null )
						{
							errors.add("errors.commonfrag", new ActionError("errors.commonfrag.existfragcontent"));
							saveErrors( request, errors );
							return mapping.findForward("failure");
						} else {
							ItemManager.getInstance().remove(item);
						}
					} else {
						// Ƶ����ҳ������Ƭ
						boolean hascontent = false;
						for(int i=0; homepageList!=null && i<homepageList.size(); i++)
						{
							EntityItem hp = (EntityItem)homepageList.get(i);
							log.debug("hp.name: "+hp.getName());
							if( hp.getChannel()==channel )
							{
								storepath = PageManager.getFStorePath(channel,hp.getId(),name,true);
							log.debug("storepath:"+storepath );
								String content = ClientFile.getInstance().read( storepath );
								if( content!=null )
								{
									hascontent = true;
									break;
								}
							}
						}
						if( hascontent )
						{
							errors.add("errors.commonfrag", new ActionError("errors.commonfrag.existfragcontent"));
							saveErrors( request, errors );
							return mapping.findForward("failure");
						} else {
							ItemManager.getInstance().remove(item);
						}
					}
				//refresh listcache
				ItemManager.getInstance().refreshItemListCache(ItemInfo.getItemClass(itemType));
				}
			} else {
				errors.add("errors.item", new ActionError("errors.item.id.required"));
			}
			if(item == null) throw new Exception("ItemManager.update return null");
			dForm.initialize(mapping);
		} catch (Exception e) {
			errors.add("errors.commonfrag", new ActionError("errors.commonfrag.remove", e.toString()));
			log.error("remove commonfrag exception. ", e);
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
