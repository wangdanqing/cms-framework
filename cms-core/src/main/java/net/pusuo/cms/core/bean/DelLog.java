package net.pusuo.cms.core.bean;


import java.sql.Timestamp;

/**
 * @deprecated
 */
public class DelLog extends Item {

    private static final long serialVersionUID = 4304055467887009555L;
    private long id;
    private String input;
    private String ids;
    private String titles;
    private String reason;
    private String initiator;
    private Timestamp time;
    private long operator;
    private int opid;
    private int redo = 0;
    private Timestamp redotime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getOperator() {
        return operator;
    }

    public void setOperator(long operator) {
        this.operator = operator;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getOpid() {
        return opid;
    }

    public void setOpid(int opid) {
        this.opid = opid;
    }

    public int getRedo() {
        return redo;
    }

    public void setRedo(int redo) {
        this.redo = redo;
    }

    public Timestamp getRedotime() {
        return redotime;
    }

    public void setRedotime(Timestamp redotime) {
        this.redotime = redotime;
    }
}
