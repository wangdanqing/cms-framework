package net.pusuo.cms.core.bean;

/**
 * 公共碎片
 */
public class CommonFrag extends Item {

    private static final long serialVersionUID = -7049335274368234401L;
    public final int type = TYPE_COMMON_FRAG;
    private long id;
    private String name;
    private String desc;
    private String permission;
    private int status = STATUS_ENABLE;
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }
}

