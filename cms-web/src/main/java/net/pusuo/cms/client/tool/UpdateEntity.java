package net.pusuo.cms.client.tool;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.core.EntityItem;

public class UpdateEntity {

	public void updateList(String filename) {
		String filen = filename;
		filen = filen == null?"ids.txt":filen;
		
		String[] ids;
		try {
			ids = ClientFile.getInstance().read("/tmp/" + filen).split(";");
			if(ids.length<1)return;
			EntityItem item = null;
			for (int i = 0; i < ids.length; i++) {
				item = (EntityItem) ItemManager.getInstance().get(new Integer(Integer.parseInt(ids[i].toString().trim())), EntityItem.class);
				ItemManager.getInstance().update(item);
				ItemManager.getInstance().refreshItemCache(item);
				Thread.currentThread().sleep(2000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
