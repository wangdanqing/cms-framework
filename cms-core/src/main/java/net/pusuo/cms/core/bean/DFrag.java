package net.pusuo.cms.core.bean;

public class DFrag extends Item {

    private static final long serialVersionUID = -5717489624330883110L;
    public final int type = TYPE_DFRAG;
    private long id;
    private String name;
    private String desc;
    private String referIds;//引用id
    private int status = STATUS_ENABLE;

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

