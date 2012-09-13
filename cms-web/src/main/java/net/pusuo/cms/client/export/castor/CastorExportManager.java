/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.export.castor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.export.Export;
import com.hexun.cms.client.export.ExportException;
import com.hexun.cms.client.export.ExportManager;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CastorExportManager implements ExportManager {
    
	private static Log log = LogFactory.getLog(CastorExportManager.class);

	private static final String encoding = "GBK";
	
	//private static String WAP_MAPPING_DIR = "D:\\resin-2.1.16\\webapps\\CmsClient\\WEB-INF";
	private static String WAP_MAPPING_DIR = "/opt/hexun/cms/web/WEB-INF";
    private static String WAP_MAPPING_FILE = "export-mapping.xml";  

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    /**
     * 
     */
    public CastorExportManager() {
        
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportNewses(java.util.List, java.lang.String)
     */
    public void exportNewses(List newses, String fileName)
            throws ExportException {
        exportItems(newses, fileName);
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportNewses(java.util.List, java.io.Writer)
     */
    public void exportNewses(List newses, Writer writer) 
    		throws ExportException {
        exportItems(newses, writer);
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportSubjects(java.util.List, java.lang.String)
     */
    public void exportSubjects(List subjects, String fileName)
            throws ExportException {
        exportItems(subjects, fileName);
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportSubjects(java.util.List, java.io.Writer)
     */
    public void exportSubjects(List subjects, Writer writer)
            throws ExportException {
        exportItems(subjects, writer);
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportItems(java.util.List, java.lang.String)
     */
    public void exportItems(List items, String fileName) 
    		throws ExportException {
        
		if (fileName == null)
			throw new ExportException();
		else {
		    File file = new File(fileName);
		    if (file.exists())
		        file.delete();
		}

		FileOutputStream fos = null;
        OutputStreamWriter writer = null;

        try {
            fos = new FileOutputStream(fileName);
            writer = new OutputStreamWriter(fos, encoding); 
            
            exportNewses(items, writer);
        }
        catch (Exception e) {
        	throw new ExportException();
		}      
        finally {
            try {
                if (writer != null)
                    writer.close();
            }
            catch (Exception e) {
                throw new ExportException();
            }
            
            try {
                if (fos != null)
                    fos.close();
            }
            catch (Exception e) {
                throw new ExportException();
            }
        }        
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.client.export.ExportManager#exportItems(java.util.List, java.io.Writer)
     */
    public void exportItems(List items, Writer writer) 
    		throws ExportException {
        
        if (writer == null)
            throw new ExportException();
        
        try {
    		// 1. Build doc
    		POJODoc doc = buildDoc(items);	
    		if (doc == null)
    		    throw new ExportException();
    			
            // 2. Load the mapping information from the file
            Mapping mapping = new Mapping();
            mapping.loadMapping(WAP_MAPPING_DIR + File.separator + WAP_MAPPING_FILE);

            // 3. Create the serializer output format
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(4);
            format.setLineWidth(0);
            format.setEncoding(encoding);
            
            // 4. Define format of CDATA node. It`s hard code.
            String[] cdata = {"title", "media", "content", "name", "description", "url", "reurl"};
            format.setCDataElements(cdata);
            format.setNonEscapingElements(cdata);
            
            // 5. Marshal the data
            Serializer serializer = new XMLSerializer(writer, format);
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(mapping);
            marshaller.marshal(doc); 
        }
        catch (Exception e) {
            log.error(e);
            throw new ExportException();
        }       
    }
    
    /**
     * Build doc.
     * @param items
     * @return
     */
    private POJODoc buildDoc(List items) {
              
        POJODoc doc = new POJODoc();
        
        if (items == null)
            return doc;
        
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            
            if (item instanceof News) {
                News news = (News)item;	                 
                POJONews pojonews = createPOJONews(news);               
                doc.addNews(pojonews);
            }
            else if (item instanceof Subject) {
                Subject subject = (Subject)item;
                POJOSubject pojosubject = createPOJOSubject(subject);               
                doc.addSubject(pojosubject);
            }	                
        }
        
        return doc;
    }
    
    /**
     * Create POJONews object.
     * @param news
     * @return
     */
    private POJONews createPOJONews(News news) {
        
        if (news == null)
            return null;

        POJONews pojonews = new POJONews();
        
        if (news.getId() > 0)
            pojonews.setId("" + news.getId());
log.info("news id is:"+news.getId());        
        if (news.getPid() > 0)
            pojonews.setPid("" + news.getPid());
        
        if (news.getTime() != null)
            pojonews.setCreatetime(dateFormatter.format(news.getTime()));
        
        if (news.getStatus() >= 0)
            pojonews.setStatus("" + news.getStatus());
        
        if (news.getUrl() != null)
            pojonews.setUrl(news.getUrl());
        
        if (news.getDesc() != null)
            pojonews.setDesc(news.getDesc());
        
        String mediaName = null;
        int mediaId = news.getMedia();
        if (mediaId > 0) {
            Media media = (Media)ItemManager
            				.getInstance()
            				.get(new Integer(mediaId), 
            				        ItemInfo.getItemClass(ItemInfo.MEDIA_TYPE));
            if (media != null)
                mediaName = media.getName();
            else
                mediaName = "";
        }
        if (mediaName != null)
            pojonews.setMedia(mediaName);
        
        String text = news.getText();
	if(news.getReferid()>-1){
		News refNews = (News)ItemManager.getInstance().get(new Integer(news.getReferid()),ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
		if(refNews != null){
			text = refNews.getText();
		}
	}
        if (text != null && !Export.NEWS_CONTENT_NULL.equalsIgnoreCase(text)) { 	// dirty code
            String validText = filterInvalidChars(text); 
            pojonews.setContent(validText);
        }
        
        if (news.getReurl() != null)
            pojonews.setReurl(news.getReurl());
        
	if (news.getAbstract() != null)
            pojonews.setAbs(news.getAbstract());

        return pojonews;
    }
    
    /**
     * 
     * @param text
     * @return
     */
    private String filterInvalidChars(String text) {
        if (text == null)
            return null;
        
        StringBuffer ret = new StringBuffer();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 0xFFFD 													// Invalid Unicode Character
                || (c < 0x20 && c != '\t' && c != '\n' && c != '\r')) {		// Invalid Xml Character  
                continue;
            }
            else {
                ret.append(c);
            }
        }
            
        return ret.toString();
    }
    
    /**
     * Create POJOSubject object.
     * @param subject
     * @return
     */
    private POJOSubject createPOJOSubject(Subject subject) {
        
        if (subject == null)
            return null;

        POJOSubject pojosubject = new POJOSubject();
        
        if (subject.getId() > 0)
            pojosubject.setId("" + subject.getId());
        
        if (subject.getPid() > 0)
            pojosubject.setPid("" + subject.getPid());
        
        if (subject.getTime() != null)
            pojosubject.setCreatetime(dateFormatter.format(subject.getTime()));
        
        if (subject.getStatus() >= 0)
            pojosubject.setStatus("" + subject.getStatus());
        
        if (subject.getUrl() != null)
            pojosubject.setUrl(subject.getUrl());
        
        if (subject.getName() != null)
            pojosubject.setName(subject.getName());
        
        if (subject.getDesc() != null)
            pojosubject.setDesc(subject.getDesc());

        return pojosubject;
    }

}
