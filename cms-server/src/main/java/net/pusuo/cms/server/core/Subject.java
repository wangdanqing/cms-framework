package net.pusuo.cms.server.core;

import net.sf.hibernate.Session;
import net.sf.hibernate.CallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @hibernate.joined-subclass table="cms_subject" dynamic-update="true"
 *                            dynamic-insert="true"
 * @hibernate.joined-subclass-key column="entity_id"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Subject extends EntityItem {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(Subject.class);

	public static final int SUBTYPE_SUBJECT = 1;

	public static final int SUBTYPE_SUBSUBJECT = 2;

	public static final int SUBTYPE_COLUMN = 3;

	public static final int SUBTYPE_SUBCOLUMN = 4;

	int subject_discussable = 0;

	int subject_defaulttemplate = 0;
	
	String subject_weakreference = "";

	Subject() {
		super(EntityItem.SUBJECT_TYPE, SUBTYPE_SUBSUBJECT);
	}

	/**
	 * @hibernate.property column="subject_discussable"
	 */
	public int getDiscussable() {
		return subject_discussable;
	}

	public void setDiscussable(int discussable) {
		this.subject_discussable = discussable;
	}

	/**
	 * @hibernate.property column="subject_deftemp"
	 */
	public int getDefaulttemplate() {
		return this.subject_defaulttemplate;
	}

	public void setDefaulttemplate(int subject_defaulttemplate) {
		this.subject_defaulttemplate = subject_defaulttemplate;
	}

	/**
     * @hibernate.property column="subject_weakreference"
     */
	public String getWeakReference() {
		return this.subject_weakreference;
	}

	public void setWeakReference(String weakReference) {
		this.subject_weakreference = weakReference;
	}

	/*
	*���ڻ�ȡ�����URL���Ǿ��URL
	*/
	public String getUrl(boolean b) throws Exception
	{
		try{
			if( b ){//���URL������ԭ״
				return super.entity_url;
			} else {//���URL
				boolean tf = entity_url.startsWith("http://");
				if(tf){
					int pos = entity_url.indexOf("/",7 );//http://�ĳ�����7
					return  entity_url.substring(pos);	
				}else{
					return super.entity_url;
				}
			}
		}catch(Exception e){
			log.error("Subject.getUrl(boolean) exception -- ",e);	
		}
		
		return super.entity_url;
	}
	
	public boolean onSave(Session s) throws CallbackException {


		try {
			callBack();
		} catch (Exception e) {
		}
		
		if (entity_subtype == SUBTYPE_SUBSUBJECT) {//��ר��
			entity_shortname = "" + entity_id;
			entity_template += "," + entity_shortname;
		}

		return false;
	}
}
