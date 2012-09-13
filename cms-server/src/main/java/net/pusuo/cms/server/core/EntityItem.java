package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Global;
import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.ItemProxy;
import net.pusuo.cms.server.file.EntityStoreRule;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * EntityItem所有Entity的基类
 *
 * @author xulin
 * @version 2.0
 * @hibernate.class table="cms_entity"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @see EntityManager
 * @since CMS1.0
 */
public class EntityItem implements Item, Lifecycle {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(EntityItem.class);

    public static final int SUBJECT_TYPE = 1;
    public static final int NEWS_TYPE = 2;
    public static final int PICTURE_TYPE = 3;
    public static final int HOMEPAGE_TYPE = 5;
    public static final int VIDEO_TYPE = 17;
    public static final int IMPORT_STATUS = -1;
    public static final int ENABLE_STATUS = 2;
    public static final int RESERVE_STATUS = 1;
    public static final int DISABLE_STATUS = 0;

    int entity_id = -1;
    String entity_name = "";
    String entity_desc = "";
    int entity_type = -1;
    int entity_subtype = 0;  //5:magazine
    int entity_pid = -1;
    Timestamp entity_time = null;
    String entity_time_string = "";
    int entity_priority = 0;
    int entity_status = 2;
    int entity_channel = -1;
    int entity_editor = -1;
    String entity_template = "";
    String entity_url = "";
    String entity_category = "";
    String entity_param = "";
    String entity_shortname = "";

    int entity_oldpid = -1;
    int entity_oldstatus = -1;
    int entity_oldpriority = -1;

    //为生成URL使用
    String ext = "html";

    EntityItem() {
    }

    EntityItem(int entityType) {
        this.entity_type = entityType;
    }

    EntityItem(int entityType, int entitySubtype) {
        this.entity_type = entityType;
        this.entity_subtype = entitySubtype;
    }

    /**
     * @hibernate.id column="entity_id" unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="sq_entity"
     */
    public int getId() {
        return this.entity_id;
    }

    /**
     * @hibernate.property column="entity_name"
     * not-null="true"
     */
    public String getName() {
        return entity_name;
    }

    /**
     * @hibernate.property column="entity_desc"
     */
    public String getDesc() {
        return entity_desc;
    }

    /**
     * @hibernate.property column="entity_type"
     * not-null="true"
     */
    public int getType() {
        return entity_type;
    }

    /**
     * @hibernate.property column="entity_subtype"
     */
    public int getSubtype() {
        return entity_subtype;
    }

    /**
     * @hibernate.property column="entity_pid"
     */
    public int getPid() {
        return entity_pid;
    }

    /**
     * @hibernate.property column="entity_time"
     */
    public Timestamp getTime() {
        return entity_time;
    }

    /**
     * @hibernate.property column="entity_priority"
     */
    public int getPriority() {
        return entity_priority;
    }

    /**
     * @hibernate.property column="entity_status"
     */
    public int getStatus() {
        return entity_status;
    }

    /**
     * @hibernate.property column="entity_channel"
     */
    public int getChannel() {
        return entity_channel;
    }

    /**
     * @hibernate.property column="entity_editor"
     */
    public int getEditor() {
        return entity_editor;
    }

    /**
     * @hibernate.property column="entity_template"
     */
    public String getTemplate() {
        return entity_template;
    }

    /**
     * @hibernate.property column="entity_url"
     */
    public String getUrl() {
        if (entity_url == null) return "";
        return entity_url;
    }

    /**
     * @hibernate.property column="entity_category"
     */
    public String getCategory() {
        if (entity_category == null) return "";
        return entity_category;
    }

    /**
     * @hibernate.property column="entity_param"
     */
    public String getParam() {
        return entity_param;
    }

    /**
     * @hibernate.property column="entity_shortname"
     */
    public String getShortname() {
        if (entity_shortname == null) return "";
        return entity_shortname;
    }

    /**
     * 生成URL使用的方法
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    public int getOldpid() {
        return entity_oldpid;
    }

    public void setOldpid(int oldpid) {
        this.entity_oldpid = oldpid;
    }

    public int getOldstatus() {
        return entity_oldstatus;
    }

    public void setOldstatus(int oldstatus) {
        this.entity_oldstatus = oldstatus;
    }

    public int getOldpriority() {
        return entity_oldpriority;
    }

    /**
     * 让原值的保持有足够的灵活性.
     */
    public void setOldpriority(int oldpriority) {
        this.entity_oldpriority = oldpriority;
    }

    public void setId(int id) {
        this.entity_id = id;
    }

    public void setName(String name) {
        this.entity_name = name;
    }

    public void setDesc(String description) {
        this.entity_desc = description;
    }

    public void setType(int typeid) {
        this.entity_type = typeid;
    }

    public void setSubtype(int subtype) {
        this.entity_subtype = subtype;
    }

    public void setPid(int pid) {
        this.entity_oldpid = this.entity_pid;
        this.entity_pid = pid;
    }

    public void setTime(Timestamp createtime) {
        this.entity_time = createtime;
    }

    public void setPriority(int priority) {
        if (priority != this.entity_priority) {
            /*
                * 只在priority发生变更的情况下,记录原有的priority
                */
            this.entity_oldpriority = priority;
        }
        this.entity_priority = priority;
    }

    public void setStatus(int status) {
        this.entity_oldstatus = this.entity_status;
        this.entity_status = status;
    }

    public void setChannel(int channel) {
        this.entity_channel = channel;
    }

    public void setEditor(int editorid) {
        this.entity_editor = editorid;
    }

    public void setTemplate(String template) {
        this.entity_template = template;
    }

    public void setUrl(String url) {
        this.entity_url = url;
    }

    public void setCategory(String category) {
        this.entity_category = category;
    }

    public void setParam(String param) {
        this.entity_param = param;
    }

    public void setShortname(String shortname) {
        this.entity_shortname = shortname;
    }

    public boolean onSave(Session s)
            throws CallbackException {
        try {
            callBack();
        } catch (Exception e) {
            log.error("EntityItem.onSave exception -- ", e);
        }
        return false;
    }

    public boolean onUpdate(Session s)
            throws CallbackException {
        /*
          //如果pid改变了重新组合category
          try{
              if(entity_pid != entity_oldpid){
                  EntityItem item = (EntityItem)ItemProxy.getInstance().get( new Integer(entity_pid),EntityItem.class);
                  entity_category = item.getCategory() + Global.CMSSEP + entity_id;
              }
          }catch(Exception e){}
          return false;
          */

        // modified by wangzhigang 2006.2.27
        // 不判断pid是否更改,提交实体修改category
        try {
            if (entity_type == ItemInfo.HOMEPAGE_TYPE) {
                entity_category = entity_id + "";
            } else {
                EntityItem item = (EntityItem) ItemProxy.getInstance().get(new Integer(entity_pid), EntityItem.class);
                entity_category = item.getCategory() + Global.CMSSEP + entity_id;
            }
        } catch (Exception e) {
            log.error("EntityItem.onUpdate exception -- ", e);
        }
        return false;

    }

    public boolean onDelete(Session s)
            throws CallbackException {
        return false;
    }

    public void onLoad(Session s, Serializable id) {
    }

    protected void callBack() throws Exception {
        //实体名称
        if (entity_name.equals("")) {
            entity_name = "" + entity_id;
        }
        try {
            //组合category
            if (entity_type == ItemInfo.HOMEPAGE_TYPE) {
                entity_category = entity_id + "";
            } else {
                log.info("------magazine:-----" + this.entity_id);
                EntityItem item = (EntityItem) ItemProxy.getInstance().get(new Integer(this.entity_pid), EntityItem.class);
                this.entity_category = item.getCategory() + Global.CMSSEP + this.entity_id;
                log.info("------magazine:-----" + this.entity_category);
            }
            //存储规则
            this.entity_url = EntityStoreRule.getURL(this, ext);

            //创建时间
            //this.entity_time = new Timestamp( System.currentTimeMillis() );
        } catch (Exception e) {
            log.error("EntityItem.callBack exception -- ", e);
        }

    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }

}

