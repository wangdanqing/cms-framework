package net.pusuo.cms.impress.sync.img;

import net.pusuo.cms.impress.sync.task.ITaskSource;
import net.pusuo.cms.impress.sync.task.TaskDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 从数据库读取图片数据
 *
 * @author agilewang
 */
public class ThumbnailDao implements TaskDao {

    private static final Log log = LogFactory.getLog(ThumbnailDao.class);

    private JdbcTemplate jdbcTemplate = null;

    private long lastId = 0;

    private int rownumber = 100;

    private int thumbWidth = 168;

    private int thumbHeigth = 146;

    private boolean keep = true;

    private String picdir = null;

    private String picUrlDomain = null;

    private static final String readSql = "select id,url,timestamp from (select id,url,timestamp from ss_pictures  order by timestamp asc) where rownum <= ? ";

    private static final String delSql = "delete from ss_pictures where id = ?";

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        if (picUrlDomain == null || picUrlDomain.trim().length() == 0) {
            throw new IllegalArgumentException("picUrlDomain must be set.");
        }

        if (picdir == null || picdir.trim().length() == 0) {
            throw new IllegalArgumentException("picdir must be set.");
        }

        if (rownumber <= 0) {
            throw new IllegalArgumentException(
                    "rownumber must be greater than zero.");
        }

        if (thumbHeigth < 0 || thumbWidth < 0) {
            throw new IllegalArgumentException(
                    "thumbHeigth and thumbWidth must be greater than zero.");

        }

        picUrlDomain = picUrlDomain.trim();
        picdir = picdir.trim();

        // 检查picDir 是否存在
        File picdirF = new File(picdir);
        if (!picdirF.isDirectory() || !picdirF.isAbsolute()) {
            throw new IllegalArgumentException(
                    "picdir must be a directory and absolute path.");
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.img.TaskDao#readThumbnailList()
      */
    public List<ITaskSource> readThumbnailList() {
        final List<ITaskSource> result = new LinkedList<ITaskSource>();

        JdbcTemplate jt = this.getJdbcTemplate();
        jt.query(readSql, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pst) throws SQLException {
                        pst.setInt(1, rownumber);
                    }
                }, new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        int id = rs.getInt(1);
                        String url = rs.getString(2);
                        lastId = rs.getTimestamp(3).getTime();
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Get picture from DB: %d %s", id,
                                    url));
                        }
                        if (url.startsWith(picUrlDomain)) {
                            String tempUrl = url.substring(picUrlDomain.length());
                            tempUrl = tempUrl.replace('/', File.separatorChar);
                            String src = picdir + File.separator + tempUrl;
                            SyncThumbnail thumbnail = new SyncThumbnail(src);
                            thumbnail.setId(id);
                            thumbnail.setUrl(url);
                            result.add(thumbnail);
                            if (log.isDebugEnabled()) {
                                log.debug(String
                                        .format("Add picture to result list DB: %d %s",
                                                id, url));
                            }
                        }
                    }
                }
        );
        if (log.isInfoEnabled()) {
            log.info(String.format("Get pictures from DB:%s.", result.size()));
        }
        return result;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.img.TaskDao#delete(com.hexun.slideshow.img.ITaskSource)
      */
    public void delete(final ITaskSource tb) {
        try {
            JdbcTemplate jt = this.getJdbcTemplate();
            jt.update(delSql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement pst)
                        throws SQLException {
                    pst.setInt(1, tb.getId());
                }
            });
            if (log.isInfoEnabled()) {
                log.info(String.format(
                        "Delete picture from DB result:id %d . ", tb.getId()));
            }
        } catch (Throwable te) {
            if (log.isErrorEnabled()) {
                log.error("delete error.", te);
            }
        }
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public int getRownumber() {
        return rownumber;
    }

    public void setRownumber(int rownumber) {
        this.rownumber = rownumber;
    }

    public String getPicdir() {
        return picdir;
    }

    public void setPicdir(String picdir) {
        this.picdir = picdir;
    }

    public String getPicUrlDomain() {
        return picUrlDomain;
    }

    public void setPicUrlDomain(String picUrlDomain) {
        this.picUrlDomain = picUrlDomain;
    }

    public int getThumbHeigth() {
        return thumbHeigth;
    }

    public void setThumbHeigth(int thumbHeigth) {
        this.thumbHeigth = thumbHeigth;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }
}
