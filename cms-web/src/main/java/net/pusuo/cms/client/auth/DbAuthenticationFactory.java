package net.pusuo.cms.client.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

/**
 * AuthenticationFactory������ʵ�֣�
 * ��Ҫʵ��ͨ��ItemManager�ӿڵ�Cache����ݲ��ȡ�û���Ϣ��
 * ���û��ƥ����û���Ϣ���׳�����UnauthenticatedException��
 *
 * @author Fei Gao
 */
public class DbAuthenticationFactory extends AuthenticationFactory {

    private static final Log LOG = LogFactory.getLog(DbAuthenticationFactory.class);
    
    /**
     * ����û��������ȡ��֤�ࡣ
     *
     * @param username the username to create an Authentication with.
     * @param password the password to create an Authentication with.
     * @return an Authentication token if the username and password are correct.
     * @throws UnauthenticatedException if the username and password do not match
     *         any existing user.
     */
    public Authentication createAuthentication(String username, String password)
            throws UnauthenticatedException
    {
        if (username == null || password == null) {
            throw new UnauthenticatedException();
        }
       
	//Get the user
	ItemManager itemManager = ItemManager.getInstance();
	User user = (User)itemManager.getItemByName(username,User.class);
        
	//Get password for comparison.
        if (null != user) {
        	String _password = user.getPasswd();
        	if (!password.equals(_password)) {
        		throw new UnauthenticatedException();
        	}
        } else {
        	throw new UnauthenticatedException();
        }
        
        return new Authentication(user);
     }
}
