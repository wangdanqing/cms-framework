package net.pusuo.cms.core.bean;


public class Channel extends Item {

    private static final long serialVersionUID = -72762959500782694L;
    private long id;
    private String name;
    private String dir;

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

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

