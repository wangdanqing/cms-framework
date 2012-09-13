package net.pusuo.cms.client;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Item;
import com.hexun.cms.client.ItemManager;

public class ItemRefresher {
	
	private static final Log LOG = LogFactory.getLog(ItemRefresher.class);
	
	public static Item update(Item value) {
		
		try {
			/*
			com.hexun.cms.auth.Perm perm = (com.hexun.cms.auth.Perm)value;
			Set _items = perm.getRoles();
			LOG.debug("========== size 1 ======== " + _items.size());
			*/
	
			value = ItemManager.getInstance().update(value);
			Class clazz = value.getClass();
			Method[] methods = clazz.getMethods();
			for (int i=0; i< methods.length; i++) {
				LOG.debug("========== refresher update ==========");
				if ( methods[i].getReturnType().isAssignableFrom(Set.class) ) {
					LOG.debug("========== found collection =========");
					LOG.debug("========== value ========== " + value.getId());
					Set items =  (Set)methods[i].invoke(value,new Object[0]);
					LOG.debug("========== size ======== " + items.size());
                                	Iterator iterator = items.iterator();
                                	while ( iterator.hasNext() ) {
						LOG.debug("========== refreshing... =========");
                                        	ItemManager.getInstance().refreshItemCache((Item)iterator.next());
                                	}
                         	}
                	}
		}catch(Exception e){
			LOG.error("ItemRefresher update error . "+e.toString());
		}
		return value;
	}
	
	public static Item remove(Item value) {
		
		try {
			value = ItemManager.getInstance().remove(value); 
			LOG.debug("============== refresher : ======= " + value.getId());
			Class clazz = value.getClass();
			Method[] methods = clazz.getMethods();
			for (int i=0; i< methods.length; i++) {
				LOG.debug("========== refresher remove ==========");
				if ( methods[i].getReturnType().isAssignableFrom(Set.class) ) {
					LOG.debug("========== found collection =========");
					Set items =  (Set)methods[i].invoke(value,new Object[0]);
					Iterator iterator = items.iterator();
                                	while ( iterator.hasNext() ) {
						LOG.debug("========== refreshing... =========");
                                        	ItemManager.getInstance().refreshItemCache((Item)iterator.next());
                                	}
                         	}
                	}
		}catch(Exception e){
			LOG.error("ItemRefresher remove error . "+e.toString());
		}
		return value;
	}
	
}
