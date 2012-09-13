package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Global;
import net.pusuo.cms.server.ItemProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @hibernate.joined-subclass table="cms_video" dynamic-update="true"
 * dynamic-insert="true"
 * @hibernate.joined-subclass-key column="entity_id"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Video extends EntityItem {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(Video.class);
    /**
     * 视频的长度
     */
    int length = -1;

    /**
     * 是否加密
     */
    int encrypt = -1;

    /**
     * Video对象的大图
     */
    String big_pic = null;

    /**
     * Video对象的小图
     */
    String small_pic = null;

    Video() {
        super(EntityItem.VIDEO_TYPE);
    }

    /**
     * @hibernate.property column="video_encrypt"
     */
    public int getEncrypt() {
        return encrypt;
    }


    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * @hibernate.property column="video_length"
     */

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @hibernate.property column="video_bigpic" length="512"
     */
    public String getBigpic() {
        return this.big_pic;
    }

    public void setBigpic(String big_pic) {
        this.big_pic = big_pic;
    }

    /**
     * @hibernate.property column="video_smallpic" length="512"
     */
    public String getSmallpic() {
        return this.small_pic;
    }

    public void setSmallpic(String small_pic) {
        this.small_pic = small_pic;
    }

    protected void callBack() throws Exception {
        //实体名称
        if (entity_name.equals("")) {
            entity_name = "" + entity_id;
        }
        try {
            //组合category
            EntityItem item = (EntityItem) ItemProxy.getInstance().get(new Integer(this.entity_pid), EntityItem.class);
            this.entity_category = item.getCategory() + Global.CMSSEP + this.entity_id;

        } catch (Exception e) {
            log.error("EntityItem.callBack exception -- ", e);
        }

    }
}
