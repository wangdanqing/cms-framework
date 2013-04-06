package net.pusuo.cms.core.bean.auth;


import java.io.Serializable;

public class Perm implements Serializable {

    private static final long serialVersionUID = 240851801580398469L;
    private int id;
    private String name;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
