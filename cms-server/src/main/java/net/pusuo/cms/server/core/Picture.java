package net.pusuo.cms.server.core;


/**
 * @hibernate.joined-subclass table="cms_picture"
 * dynamic-update="true"
 * dynamic-insert="true"
 * @hibernate.joined-subclass-key column="entity_id"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Picture extends EntityItem {
    private static final long serialVersionUID = 1L;

    public static final int THUMB_FLAG = 1;
    public static final int MARK_FLAG = 2;

    int picture_width = -1;
    int picture_height = -1;
    String picture_comment = "";
    int picture_exflag = 0;

    Picture() {
        super(EntityItem.PICTURE_TYPE);
    }

    /**
     * @hibernate.property column="picture_width"
     */
    public int getWidth() {
        return picture_width;
    }

    /**
     * @hibernate.property column="picture_height"
     */
    public int getHeight() {
        return picture_height;
    }

    /**
     * @hibernate.property column="picture_comment"
     */
    public String getComment() {
        return picture_comment;
    }

    /**
     * @hibernate.property column="picture_exflag"
     */
    public int getExflag() {
        return picture_exflag;
    }

    public void setWidth(int width) {
        this.picture_width = width;
    }

    public void setHeight(int height) {
        this.picture_height = height;
    }

    public void setComment(String comment) {
        this.picture_comment = comment;
    }

    public void setExflag(int flag) {
        this.picture_exflag = flag;
    }

    public void addExflag(int flag) {
        this.picture_exflag |= flag;
    }

    public void clearExflag(int flag) {
        this.picture_exflag &= ~flag;
    }

    public boolean hasExflag(int flag) {
        return ((this.picture_exflag & flag) == flag);
    }

}
