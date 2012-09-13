package net.pusuo.cms.client.auth;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.auth.Perm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.ItemInfo;

/**
 *  Ȩ�޵�������,ͬʱ��Ȩ�޶����װ�ɿɹ������ļ��ϡ�
 *
 *@author     feigao
 *@created    2004��5��4��
 */
public class Permission {
	
	private static final Log LOG = LogFactory.getLog(Permission.class);

	public static final String RESOURCE = "resource_";

	public static final String CHANNEL = "channel_";

	public static final String DEPARTMENT = "department_";

	/**
	 *  Ȩ����Ƶļ���
	 */
	private Set perms = new HashSet();

	/**
	 *  Constructor for the Permission object
	 */
	public Permission() {
	}

	/**
	 *  Constructor for the Permission object
	 *
	 *@param  perms  Description of Parameter
	 */
	public void load(Set perms) {
		Iterator i = perms.iterator();
		while (i.hasNext()) {
			Perm perm = (Perm)i.next();
			if (!this.get(perm.getName())) {
				this.add(perm);
			}
		}
	}

	/**
	 *  ���Ȩ��
	 *
	 *@param  perm  Description of Parameter
	 */
	public void add(Perm perm) {
		this.perms.add(perm.getName());
	}


	/**
	 *  ����Ƿ�ӵ�в���Ȩ��
	 *
	 *@param  perm  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	public boolean get(String perm) {
		return this.perms.contains(perm);
	}

	
        /**
         * ����Ƿ�ӵ����ԴȨ�� 
         *
         *@param  perm  Description of Parameter
         *@return       Description of the Returned Value
         */
        public boolean isOwner(String perm,String type) {
                return this.perms.contains(RESOURCE+type+perm);
        }


	/**
         * ����Ȩ����Ϊ�����ļ��� 
         *
         */	
	public Set getActions() {
                HashSet actions = new HashSet();

                Iterator i = this.perms.iterator();
                while (i.hasNext()) {
                        String perm = (String)i.next();
                        if (!perm.startsWith(RESOURCE)) {
                                actions.add(perm);
                        }
                }
                return actions;
        }

	public Set getResourcePerms() {
                HashSet actions = new HashSet();

                Iterator i = this.perms.iterator();
                while (i.hasNext()) {
                        String perm = (String)i.next();
                        if (perm.startsWith(RESOURCE)) {
                                actions.add(perm);
                        }
                }
                return actions;
        }

	/**
         * ����Ȩ����Ϊ��Դ�ļ��� 
         *
         */
	public Set getResources(String type) {
        	HashSet resources = new HashSet();

                Iterator i = this.perms.iterator();
                while (i.hasNext()) {
                        String perm = (String)i.next();
                	if (perm.startsWith(RESOURCE+type)) {
				int index = perm.lastIndexOf("_");
				resources.add(perm.substring(index+1));
			}
		}
                return resources;
	}

	public static List getResources() {
		ArrayList resources = new ArrayList();
		List list = ItemManager.getInstance().getList(ItemInfo.getItemClass(ItemInfo.PERM_TYPE));
		Iterator i = list.iterator();
		while (i.hasNext()) {
			Perm perm = (Perm)i.next();
			String name = perm.getName();
			if (name.startsWith(RESOURCE)) {
				resources.add(perm);
			}
		}
		return resources;
		
	}

	
	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		Iterator i = this.perms.iterator();
		while (i.hasNext()) {
			String perm = (String)i.next();
			buf.append(perm).append(",");
		}
		return buf.toString();
	}
}
