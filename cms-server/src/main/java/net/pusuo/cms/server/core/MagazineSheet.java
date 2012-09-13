package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.sql.Timestamp;

/**
 * @hibernate.class table="cms_magazine_sheet" package="com.hexun.cms.core"
 * dynamic-update="true" dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class MagazineSheet implements Item {

    private static final long serialVersionUID = 8865889158891027644L;
    //序列id
    private int ms_id = -1;

    /**
     * 本期描述
     */
    private String ms_desc = "";

    /**
     * 发行时间
     */
    Timestamp ms_publishTime = null;

    /**
     * 总期刊号
     */
    String ms_totalNum = "";

    /**
     * 分期刊号
     */
    String ms_stageNum = "";

    /**
     * 期刊封面logo
     */
    String ms_indexLogo = "";

    /**
     * 封面文章
     */
    String ms_indexNews = "";

    /**
     * 封面文章url
     */
    String ms_indexNewsUrl = "";

    /**
     * 杂志首页URL
     */
    String ms_indexUrl = "";

    /**
     * 杂志期刊所属媒体
     */
    String ms_media = "";

    /**
     * @hibernate.id column="ms_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_magazine_sheet"
     */
    public int getId() {
        return ms_id;
    }

    /**
     * @hibernate.property column="ms_media"
     */
    public String getMedia() {
        return ms_media;
    }

    /**
     * @hibernate.property column="ms_publishTime"
     */
    public Timestamp getPublishTime() {
        return ms_publishTime;
    }

    /**
     * @hibernate.property column="ms_desc"
     */
    public String getDesc() {
        return ms_desc;
    }

    /**
     * @hibernate.property column="ms_indexLogo"
     */
    public String getIndexLogo() {
        return ms_indexLogo;
    }

    /**
     * @hibernate.property column="ms_indexNews"
     */
    public String getIndexNews() {
        return ms_indexNews;
    }

    /**
     * @hibernate.property column="ms_indexNewsUrl"
     */
    public String getIndexNewsUrl() {
        return ms_indexNewsUrl;
    }

    /**
     * @hibernate.property column="ms_indexUrl"
     */
    public String getIndexUrl() {
        return ms_indexUrl;
    }

    /**
     * @hibernate.property column="ms_totalNum"
     */
    public String getTotalNum() {
        return ms_totalNum;
    }

    /**
     * @hibernate.property column="ms_stageNum"
     */
    public String getStageNum() {
        return ms_stageNum;
    }


    public void setStageNum(String ms_stageNum) {
        this.ms_stageNum = ms_stageNum;
    }

    public void setTotalNum(String ms_totalNum) {
        this.ms_totalNum = ms_totalNum;
    }


    public void setPublishTime(Timestamp ms_publishTime) {
        this.ms_publishTime = ms_publishTime;
    }


    public String getName() {
        return null;
    }

    public void setDesc(String desc) {
        this.ms_desc = desc;
    }

    public void setId(int id) {
        this.ms_id = id;
    }

    public void setName(String name) {

    }

    public void setIndexLogo(String ms_indexLogo) {
        this.ms_indexLogo = ms_indexLogo;
    }

    public void setIndexNews(String ms_indexNews) {
        this.ms_indexNews = ms_indexNews;
    }

    public void setIndexNewsUrl(String ms_indexNewsUrl) {
        this.ms_indexNewsUrl = ms_indexNewsUrl;
    }

    public void setIndexUrl(String ms_indexUrl) {
        this.ms_indexUrl = ms_indexUrl;
    }

    public void setMedia(String ms_media) {
        this.ms_media = ms_media;
    }
}
