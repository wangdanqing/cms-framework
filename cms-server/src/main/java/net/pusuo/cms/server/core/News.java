package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Global;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.ItemProxy;
import net.pusuo.cms.server.file.EntityStoreRule;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @hibernate.joined-subclass table="cms_news" dynamic-update="true"
 * dynamic-insert="true"
 * @hibernate.joined-subclass-key column="entity_id"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class News extends EntityItem {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(News.class);

    public static final int SUBTYPE_DEFAULT = 0; // 不区分(兼容)
    public static final int SUBTYPE_TEXT = 1; // 文本新闻
    public static final int SUBTYPE_PICTURE = 2; // 图片新闻
    public static final int SUBTYPE_ZUTU = 3; // 组图新闻
    public static final int SUBTYPE_VIDEO = 4; // 视频新闻
    public static final int SUBTYPE_MAGAZINE = 5; //杂志新闻

    byte[] news_content = "".getBytes();

    int news_media = -1;
    int news_magazineId = -1;
    int news_magazineSheetId = -1;

    String news_keyword = "";
    String news_author = "";
    String news_abstract = "";
    String news_subhead = "";
    String news_relativenews = "";
    String news_pictures = "";
    String news_videos = "";
    String news_pushrecord = "";
    String news_dutyEditor = "";

    int news_referid = -1;
    int news_oldreferid = -1;
    int news_attr = -1;

    String news_reurl = "";
    String news_org = "";
    String news_stockcode = "";
    String news_medianame = "";
    String news_sourceurl = "";

    String news_tag = "";

    Integer news_sourceflag = 0;

    News() {
        super(EntityItem.NEWS_TYPE);
    }


    /**
     * @hibernate.property column="news_tag"
     */
    public String getTag() {
        return news_tag;
    }

    /**
     * @hibernate.property column="news_magazineSheetId"
     */
    public int getMagazineSheetId() {
        return news_magazineSheetId;
    }

    /**
     * @hibernate.property column="news_magazineId"
     */
    public int getMagazineId() {
        return news_magazineId;
    }

    /**
     * @hibernate.property column="news_sourceurl"
     */
    public String getSourceurl() {
        return news_sourceurl;
    }

    /**
     * @hibernate.property column="news_sourceflag"
     */
    public Integer getSourceflag() {
        return news_sourceflag;
    }

    /**
     * @hibernate.property column="news_attr"
     */
    public int getAttr() {
        return news_attr;
    }

    /**
     * @hibernate.property column="news_media"
     */
    public int getMedia() {
        return news_media;
    }

    /**
     * @hibernate.property column="news_keyword"
     */
    public String getKeyword() {
        return news_keyword;
    }

    /**
     * @hibernate.property column="news_author"
     */
    public String getAuthor() {
        return news_author;
    }

    /**
     * @hibernate.property column="news_abstract"
     */
    public String getAbstract() {
        return news_abstract;
    }

    /**
     * @hibernate.property column="news_subhead"
     */
    public String getSubhead() {
        return news_subhead;
    }

    /**
     * @hibernate.property column="news_pictures"
     */
    public String getPictures() {
        return news_pictures;
    }

    /**
     * @hibernate.property column="news_videos"
     */
    public String getVideos() {
        return news_videos;
    }

    /**
     * @hibernate.property column="news_relativenews"
     */
    public String getRelativenews() {
        return news_relativenews;
    }

    /**
     * @hibernate.property column="news_pushrecord"
     */
    public String getPushrecord() {
        return news_pushrecord;
    }

    /**
     * @hibernate.property column="news_referid"
     */
    public int getReferid() {
        return news_referid;
    }

    public int getOldreferid() {
        return news_oldreferid;
    }

    /**
     * @hibernate.property column="news_dutyEditor"
     */
    public String getDutyEditor() {
        return news_dutyEditor;
    }

    public void setDutyEditor(String news_dutyEditor) {
        this.news_dutyEditor = news_dutyEditor;
    }

    /*
      * ���ڻ�ȡ�����URL���Ǿ��URL
      */
    public String getUrl(boolean b) throws Exception {
        try {
            if (b) {// ���URL������ԭ״
                return super.entity_url;
            } else {// ���URL
                if (this.news_reurl != null && !this.news_reurl.equals("")) {// ��ת�Ĳ���Ҫ��ȡ
                    return super.entity_url;
                } else {// ������ת�����н�ȡ����
                    boolean tf = entity_url.startsWith("http://");
                    if (tf) {
                        int pos = entity_url.indexOf("/", 7);// http://�ĳ�����7
                        return entity_url.substring(pos);
                    } else {
                        return super.entity_url;
                    }
                }
            }
        } catch (Exception e) {
            log.error("News.getUrl(boolean) exception -- ", e);
        }

        return super.entity_url;
    }

    /**
     * @hibernate.property column="news_reurl"
     */
    public String getReurl() {
        return this.news_reurl;
    }

    public void setReurl(String news_reurl) {
        this.news_reurl = news_reurl;
    }

    public String getText() {
        // return this.text;
        String ret = "";
        try {
            ret = new String(getContent(), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @hibernate.property column="news_org"
     */
    public String getOrg() {
        return news_org;
    }

    /**
     * @hibernate.property column="news_stockcode"
     */
    public String getStockcode() {
        return news_stockcode;
    }

    public void setText(String text) {
        try {
            if (text != null) {
                // this.text = text;
                // this.news_content = text.getBytes("GBK");
                // this.news_content = (new
                // String(text.getBytes("GBK"))).getBytes("ISO_8859_1");
                setContent(text.getBytes("UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @hibernate.property column="news_content"
     * type="com.hexun.cms.util.BinaryBlobType"
     */
    public byte[] getContent() {
        return this.news_content;
    }

    public void setContent(byte[] news_content) {
        try {
            if (news_content != null) {
                this.news_content = news_content;
                // this.text = new String(news_content,"GBK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMedia(int media) {
        this.news_media = media;
    }

    public void setKeyword(String keyword) {
        this.news_keyword = keyword;
    }

    public void setAuthor(String author) {
        this.news_author = author;
    }

    public void setAbstract(String abst) {
        this.news_abstract = abst;
    }

    public void setSubhead(String subhead) {
        this.news_subhead = subhead;
    }

    public void setPictures(String pictures) {
        this.news_pictures = pictures;
    }

    public void setVideos(String videos) {
        this.news_videos = videos;
    }

    public void setRelativenews(String relativenews) {
        this.news_relativenews = relativenews;
    }

    public void setPushrecord(String pushrecord) {
        this.news_pushrecord = pushrecord;
    }

    public void setReferid(int referid) {
        this.news_oldreferid = news_referid;
        this.news_referid = referid;
    }

    public void setOrg(String org) {
        this.news_org = org;
    }

    public void setStockcode(String stockcode) {
        this.news_stockcode = stockcode;
    }

    public String getMedianame() {
        return this.news_medianame;
    }

    public void setMedianame(String medianame) {
        this.news_medianame = medianame;
    }

    public void setAttr(int news_attr) {
        this.news_attr = news_attr;
    }

    public void setMagazineId(int magazineId) {
        this.news_magazineId = magazineId;
    }

    public void setMagazineSheetId(int news_magazineSheetId) {
        this.news_magazineSheetId = news_magazineSheetId;
    }

    public void setSourceurl(String sourceurl) {
        this.news_sourceurl = sourceurl;
    }

    public void setSourceflag(Integer sourceflag) {
        this.news_sourceflag = sourceflag;
    }

    public void setTag(String tag) {
        this.news_tag = tag;
    }

    public boolean onSave(Session s) throws CallbackException {
        try {
            callBack();

            /*
                * if(news_referid>0){ EntityItem item =
                * (EntityItem)ItemProxy.getInstance().get( new
                * Integer(news_referid),EntityItem.class); entity_url =
                * EntityStoreRule.getURL( item, "html" ); }
                */
            if (this.news_reurl != null && !this.news_reurl.equals("")) {
                this.entity_url = this.news_reurl;
            }
            Media media = (Media) ItemProxy.getInstance().get(
                    Integer.valueOf(this.news_media), Media.class);
            if (media != null) {
                this.news_medianame = media.getName();
            } else {
                log.warn("News.onSave - Get Media Null");
            }
        } catch (Exception e) {
            log.error("News.onUpdate exception -- ", e);
        }
        entity_shortname = "" + entity_id;
        entity_template += "," + entity_shortname;

        return false;
    }

    public boolean onUpdate(Session s) throws CallbackException {
        try {
            /*
                * //���pid�ı����������category if(entity_pid != entity_oldpid){
                * EntityItem item = (EntityItem)ItemProxy.getInstance().get( new
                * Integer(entity_pid),EntityItem.class); entity_category =
                * item.getCategory() + Global.CMSSEP + entity_id; }
                */

            // modified by wangzhigang 2006.2.27
            if (entity_type == ItemInfo.HOMEPAGE_TYPE) {
                entity_category = entity_id + "";
            } else {
                EntityItem item = (EntityItem) ItemProxy.getInstance().get(
                        new Integer(entity_pid), EntityItem.class);
                entity_category = item.getCategory() + Global.CMSSEP
                        + entity_id;
            }

            /*
                * if(news_referid>0) { if(news_oldreferid<0||(news_oldreferid>0&&news_referid!=news_oldreferid)){
                * EntityItem item = (EntityItem)ItemProxy.getInstance().get( new
                * Integer(news_referid),EntityItem.class); entity_url =
                * EntityStoreRule.getURL( item, "html" ); } }
                *
                * if(news_referid<0) { // modified by wangzhigang 2005.05.19
                * //if(news_oldreferid>0){ this.entity_url =
                * EntityStoreRule.getURL( this, "html" ); //} }
                */

            if (this.news_reurl != null && !this.news_reurl.equals("")) {
                this.entity_url = this.news_reurl;
            } else {
                this.entity_url = EntityStoreRule.getURL(this, "html");
            }

            Media media = (Media) ItemProxy.getInstance().get(
                    Integer.valueOf(this.news_media), Media.class);
            if (media != null) {
                this.news_medianame = media.getName();
            } else {
                log.warn("News.onSave - Get Media Null");
            }
        } catch (Exception e) {
            log.error("News.onUpdate exception -- ", e);
        }
        return false;
    }
}
