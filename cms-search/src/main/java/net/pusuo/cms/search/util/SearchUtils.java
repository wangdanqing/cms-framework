/*
 * Created on 2006-2-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.util;

import net.pusuo.cms.search.entry.Entry;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchUtils {
    
    private static Log log = LogFactory.getLog(SearchUtils.class);
    
    /**
     * Locks for synchronization.
     */
    public static final Object updateLock = new Object();
    public static final Object removeLock = new Object();
    public static final Object addLock = new Object();
    public static final Object searcherLock = new Object();
    
    /**
     * Names for searching result.
     */
    public static final String SEARCH_RESULT_COST = "cost";
    public static final String SEARCH_RESULT_COUNT = "count";
    public static final String SEARCH_RESULT_LIST = "list";
   
    /**
     * The format of data field for index.
     */
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    
    /**
     * Convert a Entry to a Document.
     * @param entry
     * @return
     */
    public static synchronized Document entry2Document(Entry entry) {
        
        if (entry == null || entry.getId() <= 0)
            return null;
        
        Document doc = new Document();
        
        try {
            Entry tempEntry = Entry.createEntry();
	        Map fieldTypes = tempEntry.getFieldTypes();
	        Iterator iterator = fieldTypes.keySet().iterator();
	        while (iterator.hasNext()) {
	            String propertyName = (String)iterator.next();
	            String propertyFieldType = (String)fieldTypes.get(propertyName);
	            Object propertyValue = PropertyUtils.getProperty(entry, propertyName);
	            
	            if (propertyValue == null || !entry.suitIndex(propertyName)) 
	                continue;
	            
	            String value = propertyValue.toString();
	            Class propertyClass = PropertyUtils.getPropertyType(entry, propertyName);
	            String className = propertyClass.getName();
	            if (className.equalsIgnoreCase("java.sql.Timestamp")) {
	                value = formatter.format((Timestamp)propertyValue);
	            }
	            
	            Field field = null;
	            if (propertyFieldType.equals(Entry.FIELD_TYPE_KEYWORD)) {
	                field = Field.Keyword(propertyName, value);
	            }
	            else if (propertyFieldType.equals(Entry.FIELD_TYPE_UNINDEXED)) {
	                field = Field.UnIndexed(propertyName, value);
	            }
	            else if (propertyFieldType.equals(Entry.FIELD_TYPE_UNSTORED)) {
	                field = Field.UnStored(propertyName, value);
	            }
	            else if (propertyFieldType.equals(Entry.FIELD_TYPE_TEXT)) {
	                field = Field.Text(propertyName, value);
	            }
	            
	            if (field != null)
	                doc.add(field);
	        }
	        tempEntry = null;
        }
        catch (Exception e) {
            doc = null;
            log.error(e);
        }
        
        return doc;
    }
    
    public static synchronized Entry doc2entry(Document doc) {
	    if (doc == null)
	        return null;
	    
	    Entry entry = null;
	    
	    try {
	        entry = Entry.createEntry();
	        Iterator iter = entry.getFieldTypes().keySet().iterator();
	        while (iter.hasNext()) {
	            String propertyName = (String)iter.next();
	            String propertyValue = doc.get(propertyName);
	            if (propertyValue != null && 
	                propertyValue.trim().length() != 0 &&
	                PropertyUtils.isWriteable(entry, propertyName)) {
	                propertyValue = propertyValue.trim();
	                Object value = propertyValue;
	                
	                Class propertyClass = PropertyUtils.getPropertyType(entry, propertyName);
	                String className = propertyClass.getName();
	                if (className.equalsIgnoreCase("int")) {
	                    value = new Integer(propertyValue);
	                }
	                else if (className.equalsIgnoreCase("java.sql.Timestamp")) {
	                    if (propertyValue.length() == 14) {
	                        int year = new Integer(propertyValue.substring(0, 4)).intValue();
	                        int month = new Integer(propertyValue.substring(4, 6)).intValue() - 1;
	                        int day = new Integer(propertyValue.substring(6, 8)).intValue();
	                        int hour = new Integer(propertyValue.substring(8, 10)).intValue();
	                        int minute = new Integer(propertyValue.substring(10, 12)).intValue();
	                        int second = new Integer(propertyValue.substring(12, 14)).intValue();
	                        Calendar calendar = Calendar.getInstance();
	                        calendar.clear();
	                        calendar.set(year, month, day, hour, minute, second);
	                        value = new Timestamp(calendar.getTimeInMillis());
	                    }
	                }
	                
	                PropertyUtils.setProperty(entry, propertyName, value);
	            } 
	        }  
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
	    
	    return entry;
	}
    
    /**
     * Delete all files under a path.
     * @param directory
     */
    public static synchronized void deleteFilesOfDir(String directory) {
        
        if (directory == null || directory.trim().length() == 0)
            return;
        
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int j = 0; j < files.length; j++) {
                    File file = files[j];
                    if (file.isFile())
                        file.delete();
                }
            }
        } 
    }
    
    /**
     * Get sub-directories count of a directory.
     * @param directory
     */
    public static synchronized int getSubdirCount(String directory) {
    	
        if (directory == null || directory.trim().length() == 0)
            return -1;
        
        int count = 0;
        
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int j = 0; j < files.length; j++) {
                    File file = files[j];
                    if (file.isDirectory())
                        count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Copy files from source dir to destination dir.
     * @param fromDir
     * @param toDir
     */
    public static synchronized void copyFilesOfDir(String fromDir, String toDir) {
    	
    	if (fromDir == null || fromDir.trim().length() == 0 ||
    			toDir == null || toDir.trim().length() == 0) 
    		return;
    	
    	File source = new File(fromDir);
    	File destination = new File(toDir);
    	if (!source.exists() || !source.isDirectory() || 
    			!destination.exists() || !destination.isDirectory())
    		return;
    	
    	File[] files = source.listFiles();
    	if (files != null) {
    		for (int i = 0; i < files.length; i++) {
    			File file = files[i];
    			if (file.isFile()) {
    				String name = file.getName();
    				File fileAnother = new File(toDir, name);
    				
    				try {
    					FileInputStream in = new FileInputStream(file);
    					FileOutputStream  out = new FileOutputStream (fileAnother);
    					
    					byte[] bytes = new byte[1024];
    					int c = -1;
    					while ((c = in.read(bytes)) != -1) {
    						out.write(bytes, 0, c);
    					}
    					
    					in.close();
    					out.close();
    				}
    				catch (Exception e) {}
    			}
    		}
    	}
    }
    
}
