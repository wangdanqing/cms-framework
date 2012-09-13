/*
 * Created on 2005-9-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CategoryUtil {
    
	private static List sharedCategories = null;
	
	// hard code :)
	static {
	    sharedCategories = new ArrayList();
	    sharedCategories.add(new Integer(10));
	    sharedCategories.add(new Integer(11));
	    sharedCategories.add(new Integer(12));
	    sharedCategories.add(new Integer(13));
	    sharedCategories.add(new Integer(14));
	    sharedCategories.add(new Integer(15));
	    sharedCategories.add(new Integer(10430));
	}

	/**
	 * Check wheather the category is shared.
	 * @param categoryId
	 * @return
	 */
	public static boolean isSharedCategory(int categoryId) {
	    
	    if (sharedCategories == null)
	        return false;
	    
	    boolean ret = false;
	    
	    for (int i = 0; i < sharedCategories.size(); i++) {
	        Integer id = (Integer)sharedCategories.get(i);
	        if (categoryId == id.intValue()) {
	            ret = true;
	            break;
	        }
	    }
	    
	    return ret;
	}
	
	/**
	 * Check wheather the category is shared.
	 * @param categoryId
	 * @return
	 */
	public static boolean isSharedCategory(Integer categoryId) {
	    
	    if (categoryId == null)
	        return false;
	    
	    return isSharedCategory(categoryId.intValue());
	}

}
