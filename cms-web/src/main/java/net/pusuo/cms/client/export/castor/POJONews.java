/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.export.castor;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class POJONews {

    private String id = "";
    private String pid = "";
    private String createtime = "";
    private String status = "";
    
    private String url = "";
    private String desc = "";
    private String media = "";
    private String content = "";
    private String reurl = "";
    private String abs = "";
    
    /**
     * 
     */
    public POJONews() {
    }
    /**
     * @return Returns the content.
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @return Returns the createtime.
     */
    public String getCreatetime() {
        return createtime;
    }
    /**
     * @param createtime The createtime to set.
     */
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return Returns the reurl.
     */
    public String getReurl() {
        return reurl;
    }
    /**
     * @param reurl The reurl to set.
     */
    public void setReurl(String reurl) {
        this.reurl = reurl;
    }
    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
  
    /**
     * @return Returns the pid.
     */
    public String getPid() {
        return pid;
    }
    /**
     * @param pid The pid to set.
     */
    public void setPid(String pid) {
        this.pid = pid;
    }
    /**
     * @return Returns the desc.
     */
    public String getDesc() {
        return desc;
    }
    /**
     * @param desc The desc to set.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    /**
     * @return Returns the media.
     */
    public String getMedia() {
        return media;
    }
    /**
     * @param media The media to set.
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * @return Returns the abstract.
     */
    public String getAbs() {
        return abs;
    }
    /**
     * @param abs The abs to set.
     */
    public void setAbs(String abs) {
        this.abs = abs;
    }

}
