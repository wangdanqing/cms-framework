package net.pusuo.cms.core.bean;

public class Subject{

    private int id;
    private int pid;
    private String fullpath;//父对象的全路径，以";"分割
    private String name;    //发布英文名称
    private String desc;    //描述
    private int ctime;      //create time
    private int priority = 60;
    private int status = 0;
    private int channelId;
    private int editorId;   //编辑
    private int templateId;//当前模板Id
    private String bakTemplateList;    //备用模板id列表, 以";"分割
    private int type = 0;   //类型，0:栏目   1:专题

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getEditorId() {
        return editorId;
    }

    public void setEditorId(int editorId) {
        this.editorId = editorId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getBakTemplateList() {
        return bakTemplateList;
    }

    public void setBakTemplateList(String bakTemplateList) {
        this.bakTemplateList = bakTemplateList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
