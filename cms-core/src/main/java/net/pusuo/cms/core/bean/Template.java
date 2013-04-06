package net.pusuo.cms.core.bean;

import java.util.List;

public class Template extends Item {

    private static final long serialVersionUID = 8611274019599353263L;
    private long id;
    private String name;
    private String desc;
    private int status = STATUS_ENABLE;
    private String reference;
    private List<SFrag> frags;
    private String content; //jsp template content

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<SFrag> getFrags() {
        return frags;
    }

    public void setFrags(List<SFrag> frags) {
        this.frags = frags;
    }
}

