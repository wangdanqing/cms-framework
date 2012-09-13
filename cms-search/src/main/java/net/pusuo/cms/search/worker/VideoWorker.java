/**
 *
 */
package net.pusuo.cms.search.worker;

import net.pusuo.cms.search.SearchConfig;
import net.pusuo.cms.search.db.DbConnection;
import net.pusuo.cms.search.db.DbConnectionManager;
import net.pusuo.cms.search.entry.CmsEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alfred.Yuan
 */
public class VideoWorker extends IndexWorker {

    private static final String VIDEO_TOTAL_COUNT =
            "select count(*) " +
                    "from cms_video v " +
                    "join cms_entity e " +
                    "on v.entity_id=e.entity_id " +
                    "where v.entity_id>?";

    private static final String VIDEO_MAX_ID =
            "select max(entity_id) from cms_video";

    private static final String VIDEO_MIN_ID =
            "select min(entity_id) from cms_video";

    private static final String VIDEO_TO_ID =
            "select max(entity_id) " +
                    "from (" +
                    "select entity_id from cms_video where entity_id>? order by entity_id" +
                    ") " +
                    "where rownum<=?";

    private static final String VIDEO_BLOCK_DATA =
            "select v.entity_id,v.video_length,v.video_encrypt,e.entity_desc,e.entity_type," +
                    "e.entity_pid,e.entity_time,e.entity_priority,e.entity_status,e.entity_channel," +
                    "e.entity_editor,e.entity_url,e.entity_category " +
                    "from cms_video v " +
                    "join cms_entity e " +
                    "on v.entity_id=e.entity_id " +
                    "where v.entity_id>? and v.entity_id<=? " +
                    "order by v.entity_id";

    /**
     * Constructor for configuration.
     */
    public VideoWorker() {
        super();

        lastIndexedPropertyName = "search.lastIndexedVideo";

        String lastIndexedParam = SearchConfig.getProperty(lastIndexedPropertyName);
        try {
            lastIndexed = Integer.parseInt(lastIndexedParam);
        } catch (Exception e) {
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
            pst = conn.prepareStatement(VIDEO_TOTAL_COUNT);
            pst.setInt(1, fromId);
            rs = pst.executeQuery();
            rs.next();
            totalCount = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
            pst = conn.prepareStatement(VIDEO_MAX_ID);
            rs = pst.executeQuery();
            rs.next();
            maxId = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
            pst = conn.prepareStatement(VIDEO_MIN_ID);
            rs = pst.executeQuery();
            rs.next();
            minId = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

            pst = conn.prepareStatement(VIDEO_TO_ID);
            pst.setInt(1, fromId);
            pst.setInt(2, blockSize);
            rs = pst.executeQuery();
            rs.next();
            int toId = rs.getInt(1);
            DbConnection.close(rs, pst);

            pst = conn.prepareStatement(VIDEO_BLOCK_DATA);
            pst.setInt(1, fromId);
            pst.setInt(2, toId);
            rs = pst.executeQuery();
            while (rs.next()) {
                CmsEntry entry = new CmsEntry();
                entry.setId(rs.getInt("entity_id"));
                entry.setLength(rs.getInt("video_length"));
                entry.setEncrypt(rs.getInt("video_encrypt"));
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

                result.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbConnection.close(rs, pst, conn);
        }

        return result;
    }

}
