package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

/**
 * @hibernate.class table="cms_magazine" package="com.hexun.cms.core"
 * dynamic-update="true" dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Magazine implements Item {

    private static final long serialVersionUID = 1L;

    //序列id
    private int maga_id = -1;
    //对应的文章id
    private int news_id = -1;

    //杂志期刊id
    private String mz_id = "";
    /**
     * 画中画 视频 图片 音频 手动相关的顺序 默认3;1;4;2
     */
    private String maga_itemorder = null;

    /**
     * 画中画  图片
     */
    private String maga_pics = "";

    /**
     * 画中画 视频
     */
    private String maga_video = "";

    /**
     * 画中画 手动相关文章
     */
    private String maga_releatenews = "";

    /**
     * 画中画 音频
     */
    private String maga_audio = "";

    /**
     * 是否显示正文画中画,0:否  1:是
     */
    private int maga_showPicInPic = 0;

    /**
     * @hibernate.id column="maga_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_magazine"
     */
    public int getId() {
        return maga_id;
    }

    /**
     * @hibernate.property column="maga_showPicInPic"
     */
    public int getShowPicInPic() {
        return maga_showPicInPic;
    }

    /**
     * @hibernate.property column="maga_pics"
     */
    public String getPics() {
        return maga_pics;
    }

    /**
     * @hibernate.property column="maga_video"
     */
    public String getVideo() {
        return maga_video;
    }

    /**
     * @hibernate.property column="maga_releatenews"
     */
    public String getReleatenews() {
        return maga_releatenews;
    }

    /**
     * @hibernate.property column="maga_audio"
     */
    public String getAudio() {
        return maga_audio;
    }

    /**
     * @hibernate.property column="news_id"
     */
    public int getNewsId() {
        return news_id;
    }

    /**
     * @hibernate.property column="mz_id"
     */
    public String getMzid() {
        return mz_id;
    }

    /**
     * @hibernate.property column="maga_itemorder"
     */
    public String getItemorder() {
        return maga_itemorder;
    }

    public void setItemorder(String maga_itemorder) {
        this.maga_itemorder = maga_itemorder;
    }

    public String getDesc() {
        return null;
    }

    public String getName() {
        return null;
    }

    public void setDesc(String desc) {

    }

    public void setId(int id) {
        this.maga_id = id;
    }

    public void setName(String name) {
    }

    public void setNewsId(int news_id) {
        this.news_id = news_id;
    }

    public void setMzid(String mz_id) {
        this.mz_id = mz_id;
    }

    public void setPics(String maga_pics) {
        this.maga_pics = maga_pics;
    }

    public void setVideo(String maga_video) {
        this.maga_video = maga_video;
    }

    public void setReleatenews(String maga_releatenews) {
        this.maga_releatenews = maga_releatenews;
    }

    public void setAudio(String maga_audio) {
        this.maga_audio = maga_audio;
    }

    public void setShowPicInPic(int maga_showPicInPic) {
        this.maga_showPicInPic = maga_showPicInPic;
    }

}
