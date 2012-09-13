/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.export.castor;

import java.util.ArrayList;



/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class POJODoc {
  
    private ArrayList newses; // grammar error... wuwu 
    private ArrayList subjects;
    
    /**
     * 
     */
    public POJODoc() {
        newses = null;
        subjects = null;
    }
    /**
     * @return Returns the news.
     */
    public ArrayList getNewses() {
        return newses;
    }
    /**
     * @return Returns the subjects.
     */
    public ArrayList getSubjects() {
        return subjects;
    }
    
    public void addNews(POJONews news) {
        if (this.newses == null)
            this.newses = new ArrayList();
        this.newses.add(news);
    }
    
    public void addSubject(POJOSubject subject) {
        if (this.subjects == null)
            this.subjects = new ArrayList();
        this.subjects.add(subject);
    }
}
