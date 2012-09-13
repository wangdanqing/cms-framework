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
public class POJOSubject {

    private String id = "";
    private String pid = "";
    private String createtime = "";
    private String status = "";
    
    private String url = "";
    private String name = "";
    private String desc = "";

    /**
     * 
     */
    public POJOSubject() {
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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

}
