/*
 * Created on 2006-1-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.entry;

import java.sql.Timestamp;


/**
 * @author Alfred.Yuan
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CmsEntry extends Entry {

    /**
     * EntityItem properties
     */
    private String name = "";                 // this property is only for Subject
    private String desc = "";
    private int type = -1;
    private int pid = -1;
    private Timestamp time = null;
    private int priority = -1;
    private int status = -1;
    private int channel = -1;
    private int editor = -1;
    private String template = "";            //
    private String url = "";
    private String category = "";

    /**
     * Subject properties
     */
    private int subtype = -1;

    /**
     * News properties
     */
    private String content = "";            //
    private int media = -1;
    private String medianame = "";
    private String author = "";
    private String reurl = "";
    private String org = "";

    /**
     * Picture properties
     */
    private String comment = "";

    /**
     *
     */
    private int length = -1;
    private int encrypt = -1;

    /**
     * Field names
     */
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_DESC = "desc";
    public static final String FIELD_NAME_TYPE = "type";
    public static final String FIELD_NAME_PID = "pid";
    public static final String FIELD_NAME_TIME = "time";
    public static final String FIELD_NAME_PRIORITY = "priority";
    public static final String FIELD_NAME_STATUS = "status";
    public static final String FIELD_NAME_CHANNEL = "channel";
    public static final String FIELD_NAME_EDITOR = "editor";
    public static final String FIELD_NAME_TEMPLATE = "template";
    public static final String FIELD_NAME_URL = "url";
    public static final String FIELD_NAME_CATEGORY = "category";

    public static final String FIELD_NAME_SUBTYPE = "subtype";

    public static final String FIELD_NAME_CONTENT = "content";
    public static final String FIELD_NAME_MEDIA = "media";
    public static final String FIELD_NAME_MEDIANAME = "medianame";
    public static final String FIELD_NAME_AUTHOR = "author";
    public static final String FIELD_NAME_REURL = "reurl";
    public static final String FIELD_NAME_ORG = "org";

    public static final String FIELD_NAME_COMMENT = "comment";

    public static final String FIELD_NAME_LENGTH = "length";
    public static final String FIELD_NAME_ENCRYPT = "encrypt";

    /**
     * Register field type.
     */
    static {
        registerFieldType(FIELD_NAME_NAME, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_DESC, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_TYPE, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_PID, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_TIME, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_PRIORITY, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_STATUS, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_CHANNEL, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_EDITOR, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_TEMPLATE, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_URL, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_CATEGORY, Entry.FIELD_TYPE_TEXT);

        registerFieldType(FIELD_NAME_SUBTYPE, Entry.FIELD_TYPE_KEYWORD);

        registerFieldType(FIELD_NAME_CONTENT, Entry.FIELD_TYPE_UNSTORED);
        registerFieldType(FIELD_NAME_MEDIA, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_MEDIANAME, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_AUTHOR, Entry.FIELD_TYPE_TEXT);
        registerFieldType(FIELD_NAME_REURL, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_ORG, Entry.FIELD_TYPE_TEXT);

        registerFieldType(FIELD_NAME_COMMENT, Entry.FIELD_TYPE_TEXT);

        registerFieldType(FIELD_NAME_LENGTH, Entry.FIELD_TYPE_KEYWORD);
        registerFieldType(FIELD_NAME_ENCRYPT, Entry.FIELD_TYPE_KEYWORD);
    }

    /**
     * Default constructor.
     */
    public CmsEntry() {
    }

    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return Returns the category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return Returns the channel.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @param channel The channel to set.
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     * @return Returns the description.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc The description to set.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return Returns the editor.
     */
    public int getEditor() {
        return editor;
    }

    /**
     * @param editor The editor to set.
     */
    public void setEditor(int editor) {
        this.editor = editor;
    }

    /**
     * @return Returns the media.
     */
    public int getMedia() {
        return media;
    }

    /**
     * @param media The media to set.
     */
    public void setMedia(int media) {
        this.media = media;
    }

    /**
     * @return Returns the medianame.
     */
    public String getMedianame() {
        return medianame;
    }

    /**
     * @param medianame The medianame to set.
     */
    public void setMedianame(String medianame) {
        this.medianame = medianame;
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
    public int getPid() {
        return pid;
    }

    /**
     * @param pid The pid to set.
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(int priority) {
        this.priority = priority;
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
     * @return Returns the org.
     */
    public String getOrg() {
        return org;
    }

    /**
     * @param org The org to set.
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return Returns the subtype.
     */
    public int getSubtype() {
        return subtype;
    }

    /**
     * @param subtype The subtype to set.
     */
    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    /**
     * @return Returns the template.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @param template The template to set.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @return Returns the time.
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * @param time The time to set.
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
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
     * @return Returns the encrypt.
     */
    public int getEncrypt() {
        return this.encrypt;
    }

    /**
     * @param encrypt The encrypt to set.
     */
    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * @return Returns the length.
     */
    public int getLength() {
        return this.length;
    }

    /**
     * @param length The length to set.
     */
    public void setLength(int length) {
        this.length = length;
    }

}
