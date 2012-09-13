/*
 * Created on 2005-9-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.export;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.cache.CmsSortItem;
import com.hexun.cms.client.export.castor.CastorExportManager;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Export {
     
    public static final String NEWS_CONTENT_NULL = "<![CDATA[~!@#$%^&*()]]>";
    public static final int RECORD_COUNT_MAX = 500;
        
	private static Export export = null;
	
	private Log log = null;
	
    public ExportManager exportManager = null;

    /**
     * 
     */
    private Export() {
        
        // It`s also configured by properties file.
        log = LogFactory.getLog(Export.class);
        exportManager = new CastorExportManager();
    }
    
    public static Export getInstance() {
        
        if (export == null) {
            synchronized (Export.class) {
                if (export == null) {
                    export = new Export();
                }
            }
        }
        
        return export;
    }
    
    /**
     * 
     * @param newsIds
     * @param writer
     */
    public void exportNewses(List newsIds, Writer writer) 
    		throws ExportException {
        
        if (newsIds == null || writer == null)
            throw new ExportException();
        
        List newses = new ArrayList();
        
        for (int i = 0; i < newsIds.size(); i++) {
            Integer id = (Integer)newsIds.get(i);
            News news = (News)ItemManager.getInstance().get(id, 
                    ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
            if (news != null)
                newses.add(news);
        }
        
        exportManager.exportNewses(newses, writer);
    }
    
    /**
     * 
     * @param subjectId
     * @param maxCount: default value is 100.
     * @param order: 0(default) - desc by time; 1 - desc by power and time.
     * @param writer
     */
    public void exportNewses(int subjectId, int maxCount, int order, Writer writer)  
			throws ExportException {
        
        if (subjectId <= 0 || maxCount <= 0 || (order != 0 && order != 1) || writer == null)
            throw new ExportException();
        
        // don`t hurt my db. :)
        if (maxCount > RECORD_COUNT_MAX)
            throw new ExportException();
        
//        String hql = "from " + News.class.getName() + " news where news.pid=? and news.status=2 order by ";
//        if (order == 0) 
//            hql += "news.time desc ";
//        else
//            hql += "news.priority desc, news.time desc ";
//        
//        List parameters = new ArrayList();
//        parameters.add(new Integer(subjectId));
//        
//        List items = ItemManager.getInstance().getList(hql, parameters, 0, maxCount);
        
        List items = null;
        if (order == 0) {
        	items = ListCacheClient.getInstance().TimeFilter(subjectId, ItemInfo.NEWS_TYPE, 0, maxCount);
        }
        else {
        	items = ListCacheClient.getInstance().PrioFilter(subjectId, ItemInfo.NEWS_TYPE, 0, 100, maxCount);
        }
                
        List newses = new ArrayList();
        
        if (items != null) {
	        for (int i = 0; i < items.size(); i++) {
	        	CmsSortItem item = (CmsSortItem)items.get(i);
	        	News news = CoreFactory.getInstance().createNews();
			News newstmp = (News)ItemManager.getInstance().get(new Integer(item.getId()),News.class);
			if( newstmp !=null ){
		        	news.setId(newstmp.getId());
		        	news.setName(newstmp.getName());
	        		news.setDesc(newstmp.getDesc());
		        	news.setStatus(newstmp.getStatus());
		        	news.setUrl(newstmp.getUrl());
	        		news.setTime(newstmp.getTime());
	        		news.setPriority(newstmp.getPriority());
		        	news.setCategory(newstmp.getCategory());
		        	news.setType(newstmp.getType());
	        		news.setMedia(newstmp.getMedia());
	        		news.setText(newstmp.getText());
		        	news.setReurl(newstmp.getReurl());
		        	news.setAbstract(newstmp.getAbstract());
		        	news.setReferid(newstmp.getReferid());
			}
	            if (news != null) {
	                newses.add(news);
	            }
	        }
        }
        
        exportManager.exportNewses(newses, writer);
    }
    
    /**
     * 
     * @param subjectId
     * @param maxCount: default value is 100.
     * @param order: 0(default) - desc by time; 1 - desc by power and time.
     * @param writer
     */
    public void exportNewses(int subjectId, int maxCount, Writer writer, int minpri,int maxpri)  
			throws ExportException {
        
        if (subjectId <= 0 || maxCount <= 0 || writer == null)
            throw new ExportException();
        
        // don`t hurt my db. :)
        if (maxCount > RECORD_COUNT_MAX)
            throw new ExportException();
        
        List items = null;

       	items = ListCacheClient.getInstance().PrioFilter(subjectId, ItemInfo.NEWS_TYPE, minpri, maxpri, maxCount);
                
        List newses = new ArrayList();
        
        if (items != null) {
	        for (int i = 0; i < items.size(); i++) {
	        	CmsSortItem item = (CmsSortItem)items.get(i);
	        	News news = CoreFactory.getInstance().createNews();
			News newstmp = (News)ItemManager.getInstance().get(new Integer(item.getId()),News.class);
			if( newstmp !=null ){
		        	news.setId(newstmp.getId());
		        	news.setName(newstmp.getName());
	        		news.setDesc(newstmp.getDesc());
		        	news.setStatus(newstmp.getStatus());
		        	news.setUrl(newstmp.getUrl());
	        		news.setTime(newstmp.getTime());
	        		news.setPriority(newstmp.getPriority());
		        	news.setCategory(newstmp.getCategory());
		        	news.setType(newstmp.getType());
	        		news.setMedia(newstmp.getMedia());
	        		news.setText(newstmp.getText());
		        	news.setReurl(newstmp.getReurl());
		        	news.setAbstract(newstmp.getAbstract());
			}
	            if (news != null) {
	                newses.add(news);
	            }
	        }
        }
        
        exportManager.exportNewses(newses, writer);
    }
    
    /**
     * exported subjects ordered by id asc.
     * @param itemId: news or subject id.
     * @param type: 0 - direct parent; 1 - all parents.
     */
    public void exportParentSubjects(int itemId, int type, Writer writer)  
			throws ExportException {
        
        if (itemId <= 0 || (type != 0 && type != 1) || writer == null)
            throw new ExportException();
        
        EntityItem item = (EntityItem)ItemManager.getInstance()
        		.get(new Integer(itemId), EntityItem.class);
        
        List subjects = new ArrayList();
        
        if (item != null) {
            String category = item.getCategory();
            if (category == null) 
                throw new ExportException();
            else
                category = category.trim();
            
            String[] parents = category.split(Global.CMSSEP);
            
            if (parents == null || parents.length == 0) 
                throw new ExportException();
            // if length of parents equals 1, it`s a homepage.
            else if (parents.length > 1) {
	            if (type == 0) {
                    Integer id = new Integer(parents[parents.length - 2]);
                    Item aitem = ItemManager.getInstance().get(id, EntityItem.class);
                    Subject subject = createSubject(aitem);
                    if (subject != null)
                        subjects.add(subject);
	        	}
	            else if (type == 1) {
	                for (int i = 0; i < parents.length - 1; i++) {
	                    Integer id = new Integer(parents[i]);
	                    Item aitem = ItemManager.getInstance().get(id, EntityItem.class);
	                    Subject subject = createSubject(aitem);
	                    if (subject != null)
	                        subjects.add(subject);
	                }
	            }    
            }
        }
        
        exportManager.exportSubjects(subjects, writer);
    }
    
    /**
     * exported direct sub-subjects ordered by id asc.
     * @param subjectId
     */
    public void exportSubSubjects(int subjectId, Writer writer)  
			throws ExportException {
        
//        String hql = "from " + Subject.class.getName() + " subject where subject.pid=? ";
//        
//        List parameters = new ArrayList();
//        parameters.add(new Integer(subjectId));
//        
//        List items = ItemManager.getInstance().getList(hql, parameters, 0, Integer.MAX_VALUE);
    	
    	List items = ListCacheClient.getInstance().TimeFilter(subjectId, ItemInfo.SUBJECT_TYPE, 0, 50000);
        
        List subjects = new ArrayList();
        
        if (items != null) {
	        for (int i = 0; i < items.size(); i++) {
	        	CmsSortItem item = (CmsSortItem)items.get(i);
	        	Subject subject = CoreFactory.getInstance().createSubject();
	        	subject.setId(item.getId());
	        	subject.setName(item.getName());
	        	subject.setDesc(item.getDesc());
	        	subject.setCategory(item.getCategory());
	        	subject.setType(item.getType());
	        	subject.setTime(item.getTime());
	        	subject.setPriority(item.getPriority());
	        	subject.setStatus(item.getStatus());
	        	subject.setUrl(item.getUrl());
	            if (subject != null)
	                subjects.add(subject);
	        }
        }
        
        exportManager.exportNewses(subjects, writer);
    }
    
    /**
     * export items
     * @param writer
     * @throws ExportException
     */
    public void exportMaxIDItems(Writer writer)
    		throws ExportException {
        
        List result = new ArrayList();
        
        String hql = "from " + News.class.getName() + " news1 where news1.id="
        	+ "(select max(news2.id) from " + News.class.getName() + " news2)";
        
        List items = ItemManager.getInstance().getList(hql, null, 0, 1);
        
        if (items != null && items.size() > 0)
            result.add(items.get(0));
        
        hql = "from " + Subject.class.getName() + " subject1 where subject1.id="
    		+ "(select max(subject2.id) from " + Subject.class.getName() + " subject2)";
           
        items = ItemManager.getInstance().getList(hql, null, 0, 1);

        if (items != null && items.size() > 0)
            result.add(items.get(0));
        
        exportManager.exportItems(result, writer);
    }
     
    /**
     * export newses and subjects at the same time
     * @param from
     * @param to
     * @param writer
     */
    public void exportItems(int from, int to, Writer writer)
    		throws ExportException {
        
        if (from < 0 || to < 0 || from >= to  || writer == null)
            throw new ExportException();
        
        List result = new ArrayList();
        
        String hql = "from " + News.class.getName() + " news where news.id>=? and news.id<=? order by news.time desc";
         
        List parameters = new ArrayList();
        parameters.add(new Integer(from));
        parameters.add(new Integer(to));
        
        List items = ItemManager.getInstance().getList(hql, parameters,  0, RECORD_COUNT_MAX);
        
        if (items != null) {
	        for (int i = 0; i < items.size(); i++) {
	            News news = (News)items.get(i);
	            if (news != null)
	                result.add(news);
	        }
        }
        
        hql = "from " + Subject.class.getName() + " subject where subject.id>=? and subject.id<=? order by news.time desc";
        
        items = ItemManager.getInstance().getList(hql, parameters, 0, RECORD_COUNT_MAX);
        
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Subject subject = (Subject)items.get(i);
                if (subject != null)
                    result.add(subject);
            }
        }
        
        exportManager.exportItems(result, writer);       
    }
 
    /**
     * export newses and subjects at the same time
     * @param from
     * @param to
     * @param maxCount
     * @param writer
     * @throws ExportException
     */
    public void exportItems(String fromParam, String toParam, Writer writer)
			throws ExportException {
        
        if (fromParam == null || toParam == null || writer == null)
            throw new ExportException();
        
        String from = parseTime(fromParam);
        String to = parseTime(toParam);
        if (from == null || to == null)
            throw new ExportException();
        
        List result = new ArrayList();
        
        String hql = "from " + News.class.getName() + " news where news.time between to_date(?,'yyyy-mm-dd hh24:mi:ss') and to_date(?,'yyyy-mm-dd hh24:mi:ss') order by news.time desc";
         
        List parameters = new ArrayList();
        parameters.add(from);
        parameters.add(to);
        
        List items = ItemManager.getInstance().getList(hql, parameters, 0, RECORD_COUNT_MAX);
        
        if (items != null) {
	        for (int i = 0; i < items.size(); i++) {
	            News news = (News)items.get(i);
	            if (news != null)
	                result.add(news);
	        }
        }
        
        hql = "from " + Subject.class.getName() + " subject where subject.time between to_date(?,'yyyy-mm-dd hh24:mi:ss') and to_date(?,'yyyy-mm-dd hh24:mi:ss') order by subject.time desc";
        
        items = ItemManager.getInstance().getList(hql, parameters, 0, RECORD_COUNT_MAX);
        
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Subject subject = (Subject)items.get(i);
                if (subject != null)
                    result.add(subject);
            }
        }
        
        exportManager.exportItems(result, writer);               
    }
    
    /**
     * parse time
     * @param timeParam
     * @return
     */
    private String parseTime(String timeParam) {
        
        if (timeParam == null)
            return null;
        
        String[] times = timeParam.split("-");
        if (times == null || times.length != 6)
            return null;
        
        String time = times[0] + "-" + times[1] + "-" + times[2] + " " 
        			+ times[3] + ":" + times[4] + ":" + times[5];
        
        return time;
    }
    
    /**
     * create subject object from item object
     * @param item
     * @return
     */
    private Subject createSubject(Item item) {
        if (item == null)
            return null;
        
        Subject subject = null;
        
        if (item instanceof EntityItem) {
            EntityItem entity = (EntityItem)item;
            
            subject = CoreFactory.getInstance().createSubject();
	        subject.setId(entity.getId());
	        subject.setPid(entity.getPid());
	        subject.setTime(entity.getTime());
	        subject.setStatus(entity.getStatus());
	        subject.setUrl(entity.getUrl());
	        subject.setName(entity.getName());
	        subject.setDesc(entity.getDesc());
        }
        
        return subject;
    }

}
