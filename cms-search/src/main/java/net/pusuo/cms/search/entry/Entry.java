/*
 * Created on 2005-12-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.entry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.hexun.cms.search.SearchConfig;
 
/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Entry implements Serializable  {
	 
	/**
	 * Types of fields for index.
	 */
	public static final String FIELD_TYPE_KEYWORD = "Keyword"; 
	public static final String FIELD_TYPE_UNINDEXED = "UnIndexed"; 
	public static final String FIELD_TYPE_UNSTORED = "UnStored"; 
	public static final String FIELD_TYPE_TEXT = "Text"; 
 
	/**
	 * Key for entry.
	 */
	public static final String FIELD_NAME_ID = "id";
	
	/**
	 * Specify types of fields of properties for index. 
	 */
	private static Map fieldTypes = new HashMap();
	
	static {
	    registerFieldType(FIELD_NAME_ID, Entry.FIELD_TYPE_KEYWORD);
	}
	
	/**
	 * Sub-Entry class name.
	 */
	private static String entryClassName = null;
	
	static {
	    entryClassName = SearchConfig.getProperty("search.entry");
	    if (entryClassName == null)
	        entryClassName = "com.hexun.cms.search.entry.CmsEntry";
	}
	
    /**
     * id property.
     */
	private int id = -1;
	
	/**
	 * Default constructor.
	 *
	 */
	protected Entry() {	    
	}
	
	/**
	 * Create a sub-entry.
	 * @return
	 */
	public static Entry createEntry() {
	    
	    Entry entry = null;
	    try {
	        entry = (Entry)Class.forName(entryClassName).newInstance();
	    }
	    catch (Exception e) {
	    }
	    
	    return entry;
	}
	
	/**
	 * Register field type.
	 * @param fieldName
	 * @param fieldType
	 */
	protected static void registerFieldType(String fieldName, String fieldType) {
	    if (fieldName == null || fieldName.trim().length() == 0 ||
	            fieldType == null || fieldType.trim().length() == 0)
	        return;
	    
	    if (!fieldType.equals(FIELD_TYPE_KEYWORD) 
	            && !fieldType.equals(FIELD_TYPE_UNINDEXED)
	            && !fieldType.equals(FIELD_TYPE_UNSTORED)
	            && !fieldType.equals(FIELD_TYPE_TEXT))
	        return;
	    
	    fieldTypes.put(fieldName, fieldType);
	}
	
	/**
	 * Get field types.
	 * @return
	 */
	public Map getFieldTypes() {
	    return fieldTypes;
	}
	
	/**
	 * Decide whether the property is suited to index.
	 * @param propertyName
	 * @return
	 */
	public boolean suitIndex(String propertyName) {
	    if (propertyName == null || propertyName.trim().length() == 0)
	        return false;
	    
	    propertyName = propertyName.trim();
	    
	    if (!PropertyUtils.isReadable(this, propertyName))
	        return false;
	    
	    boolean result = false;
	    
	    try {
	        Object propertyValue = PropertyUtils.getProperty(this, propertyName);
	        Class propertyClass = PropertyUtils.getPropertyType(this, propertyName);
	        if (propertyValue == null || propertyClass == null)
	            return false;
	        String className = propertyClass.getName();
	        if (className.equalsIgnoreCase("int")) {
	            int value = ((Integer)propertyValue).intValue();
	            if (value > -1)
	                result = true;
	        }
	        else if (className.equalsIgnoreCase("java.lang.String")) {
	            String value = (String)propertyValue;
	            if (value.trim().length() > 0)
	                result = true;
	        }
	        else if (className.equalsIgnoreCase("java.sql.Timestamp")) {
	            if (propertyValue != null)
	                result = true;
	        }
	    }
	    catch (Exception e) {
	        result = false;
	    }
	    
	    return result;
	}
	
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

}
