/*
 * Created on 2005-12-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.worker;

//import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.hexun.cms.search.SearchConfig;
import com.hexun.cms.search.db.DbConnection;
import com.hexun.cms.search.db.DbConnectionManager;
import com.hexun.cms.search.entry.CmsEntry;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewsWorker extends IndexWorker {

    private static final String NEWS_TOTAL_COUNT = 
        "select count(*) " +
        "from cms_news n " +
        "join cms_entity e " +
        "on n.entity_id=e.entity_id " +
        "where n.entity_id>?";
    
    private static final String NEWS_MAX_ID = 
        "select max(entity_id) from cms_news";
    
    private static final String NEWS_MIN_ID = 
        "select min(entity_id) from cms_news";
    
    private static final String NEWS_TO_ID = 
        "select max(entity_id) " +
        "from (" +
        	"select entity_id from cms_news where entity_id>? order by entity_id" +
        	") " +
        "where rownum<=?";

    private static final String NEWS_BLOCK_DATA = 
    	"select n.entity_id,n.news_media,m.media_name,n.news_author,n.news_reurl," +
    			"e.entity_desc,e.entity_type,e.entity_pid,e.entity_time,e.entity_priority,e.entity_status," +
    			"e.entity_channel,e.entity_editor,e.entity_url,e.entity_category,n.news_org " +
    	"from cms_news n " +
    	"join cms_entity e " +
    	"on n.entity_id=e.entity_id " +
    	"join cms_media m " +
    	"on n.news_media=m.media_id " +
    	"where n.entity_id>? and n.entity_id<=? " +
    	"order by n.entity_id";
    /**
     * Constructor for configuration.
     */
    public NewsWorker() {
        super();
        
        lastIndexedPropertyName = "search.lastIndexedNews";
        
        String lastIndexedParam = SearchConfig.getProperty(lastIndexedPropertyName);
        try {
            lastIndexed = Integer.parseInt(lastIndexedParam);
        }
        catch (Exception e) {
            lastIndexed = 0;
        }          
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.search.IndexWorker#getTotalCount()
     */
    public int getTotalCount(int fromId) {
        
        int totalCount = 0;
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DbConnectionManager.getConnection();
            pst = conn.prepareStatement(NEWS_TOTAL_COUNT);
            pst.setInt(1, fromId);
            rs = pst.executeQuery();
            rs.next();
            totalCount = rs.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DbConnection.close(rs, pst, conn);
        }
        
        return totalCount;
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.search.IndexWorker#getMaxId()
     */
    public int getMaxId() {
        
        int maxId = 0;
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DbConnectionManager.getConnection();
            pst = conn.prepareStatement(NEWS_MAX_ID);
            rs = pst.executeQuery();
            rs.next();
            maxId = rs.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DbConnection.close(rs, pst, conn);
        }
        
        return maxId;
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.IndexWorker#getMinId()
     */
    public int getMinId() {
        
        int minId = 0;
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DbConnectionManager.getConnection();
            pst = conn.prepareStatement(NEWS_MIN_ID);
            rs = pst.executeQuery();
            rs.next();
            minId = rs.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DbConnection.close(rs, pst, conn);
        }
        
        return minId;
    }    
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.IndexWorker#getBlockData(int)
     */
    public List getBlockData(int fromId) {
        
        List result = new ArrayList();
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DbConnectionManager.getConnection();
            
            pst = conn.prepareStatement(NEWS_TO_ID);
            pst.setInt(1, fromId);
            pst.setInt(2, blockSize);
            rs = pst.executeQuery();
            rs.next();
            int toId = rs.getInt(1);
            DbConnection.close(rs, pst);
            
            pst = conn.prepareStatement(NEWS_BLOCK_DATA);
            pst.setInt(1, fromId);
            pst.setInt(2, toId);
            rs = pst.executeQuery();
            while (rs.next()) {
                CmsEntry entry = new CmsEntry();
                entry.setId(rs.getInt("entity_id"));
                
                /*Blob contentBlob = rs.getBlob("news_content");
                if (contentBlob != null) {
                    byte[] contentByte = contentBlob.getBytes(1L, (int)contentBlob.length());
                    String contentString = new String(contentByte, "GBK");
                    entry.setContent(contentString);
                }*/
                entry.setMedia(rs.getInt("news_media"));
                entry.setMedianame(rs.getString("media_name"));
                entry.setAuthor(rs.getString("news_author"));
                entry.setReurl(rs.getString("news_reurl"));
                entry.setDesc(rs.getString("entity_desc"));
                entry.setType(rs.getInt("entity_type"));
                entry.setPid(rs.getInt("entity_pid"));
                entry.setTime(rs.getTimestamp("entity_time"));
                entry.setPriority(rs.getInt("entity_priority"));
                entry.setStatus(rs.getInt("entity_status"));
                entry.setChannel(rs.getInt("entity_channel"));
                entry.setEditor(rs.getInt("entity_editor"));
                entry.setUrl(rs.getString("entity_url"));
                entry.setCategory(rs.getString("entity_category"));
                entry.setOrg(rs.getString("news_org"));
                
                result.add(entry);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DbConnection.close(rs, pst, conn);
        }
        return result;
    }

}
