package net.pusuo.cms.core.bean;

import java.util.List;

public class Subject extends EntityItem {

    private static final long serialVersionUID = -4707048444127417634L;
    private List<Long> templateList;

    public List<Long> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(long tid) {
        templateList.add(tid);
    }
}
