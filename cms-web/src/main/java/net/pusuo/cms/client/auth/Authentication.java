package net.pusuo.cms.client.auth;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.auth.User;
import com.hexun.cms.auth.Role;
import com.hexun.cms.auth.Perm;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.core.Channel;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;


/**
 *  Description of the Class
 *
 *@author     feigao
 *@created    2004��5��2��
 */
public class Authentication {
	
	private static final Log LOG = LogFactory.getLog(Authentication.class);
	
	private User user;
	private Permission perm;
	private List channels;
	

	/**
	 *����Authentication���� ��װ��Ҫ��֤���û���Ϣ. 
	 *
	 *@param  id    Description of Parameter
	 */
	public Authentication(User user) throws UnauthenticatedException {
		try {
			this.user = user; 
			loadUserPermission();
                }
		catch (Exception e) {
			LOG.error("Error in Authentication:Constructor--Unable to initialize Authentication object. "+e.toString());
			throw new UnauthenticatedException();
		}

	}

	public User getUser()
	{
		return this.user;
	}
	/**
         *  �����û�ID
         *
         *@return     The user_id value
         */
        public int getUserID() {
                return this.user.getId();
        }

      	/**
	 *  �����û���
	 *
	 *@return     The user_name value
	 */
	public String getUserName() {
		return this.user.getName();
	}

	public String getPassword() {
		return this.user.getPasswd();
	}


	/**
	 *  �����û���������
	 *
	 *@return    The realm_id value
	 */
	public int getRealm() {
		return this.user.getRealm();
	}

	public Set getUserRoles() {
		return this.user.getRoles();
	}

	public void loadUserPermission() {

                this.perm = new Permission();

                //�õ���USER���еĽ�ɫ
                Set roles = this.user.getRoles();
                //�޷���������,ֱ�Ӵ�RMI��ȡ���������
                ItemManager itemManager = ItemManager.getInstance();
                Iterator i = roles.iterator();
                while (i.hasNext()) {
                	Role _role = (Role)i.next();
                        Role role = (Role)itemManager.get(new Integer(_role.getId()),ItemInfo.getItemClass(ItemInfo.ROLE_TYPE));
                        Set role_perm = role.getPerms();

                        this.perm.load(role_perm);
                }
        }

	/**
	 *  �����û����е�Ȩ�޼���
	 *
	 *@return     The User Permission value
	 */
	public Permission getUserPermission() {
		
		if (null == this.perm) {
			loadUserPermission();
		}
		return this.perm;
	}
	
	/*
	public Permission getRolePermission() {
		
		if (null == this.perm) {
			this.perm = new Permission();
			this.perm.load(this.role.getPerms());
		}
		return this.perm;
	}
	*/

	public List getChannelList() {
		
		if (null == this.channels) {
			this.channels = new ArrayList();
			Set res = this.perm.getResources(Permission.CHANNEL);
                	Iterator it = res.iterator();
                
			String channelName = "";
			Channel channelItem = null;
                	while (it.hasNext()) {
                        	channelName = (String)it.next();
                        	if (channelName!=null&&!channelName.equals("")) {
                                	channelItem = (Channel)ItemManager.getInstance().getItemByName(channelName,Channel.class);
                                	if(channelItem!=null&&channelItem.getId()>0){
                                        	channels.add( channelItem );
                                	}
                        	}
                	}
		}
                return this.channels;
	}
	
	public boolean hasChannel(int channelId) {
		
		boolean result = false;
		
		List channelList = getChannelList();
		for (int i = 0; i < channelList.size(); i++) {
			Channel channel = (Channel)channelList.get(i);
			if (channel == null)
				continue;
			if (channel.getId() == channelId) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	public boolean hasChannel(String channel) {
		return this.perm.isOwner(channel, Permission.CHANNEL);
	}

	public boolean isProduct(){
		Set roles = getUserRoles();
		Role role = null;
		String roleName = "";
		boolean isPro = true;
		Iterator i = roles.iterator();
		while (i.hasNext()) {
			roleName = "";
			role = (Role)i.next();
			roleName = role.getName();
			if(roleName.equals("HEXUN-PRODUCT")||roleName.equals("dotnet")||roleName.equals("store")||roleName.equals("sol")||roleName.equals("sol game")||roleName.equals("sms")){
				isPro = false;
				break;
			}
		}
	
		if(roles.size()==1&&isPro==false){//��Ʒ�ߵ��û�
			return true;
		}else{//���ǲ�Ʒ���û�
			return false;
		}
	}

}
