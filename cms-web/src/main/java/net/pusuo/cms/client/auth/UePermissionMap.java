package net.pusuo.cms.client.auth;

import java.io.IOException;
import java.util.Properties;

/**
 * <b>������Ϊ�˽��UE�ı�ǩ��Ӧcms��Ȩ������</b><br/>
 * ϵͳ��û��ue�ı�ǩ,��:<!--SAS:MT:sport:ST--> ����sportΪue��ǩ��Ȩ��.<br/>
 * ��������cmsϵͳ�ж�Ӧ��Ȩ����sports.hexun.com
 * 
 * ��ϵ����Ϊ�ļ�. ������classes���� uepermission.properties<br/>
 * ��ŷ�ʽΪue permissioni=sms permission<br/>
 * <b>�ֹ���ʽ��ά��Ȩ���ļ���</b>
 * @author denghua
 *
 */
public class UePermissionMap {
	
	static final String UE_PERM_FILE="/uepermission.properties";
	
	static Properties propertis=new Properties();
	static{
		try {
			propertis.load(UePermissionMap.class.getResourceAsStream(UE_PERM_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���UE��Ȩ��,�õ�cms��Ȩ��
	 * @param uePermission UE��Ȩ��
	 * @return cms��Ȩ���ַ� cms��Ȩ���ַ���channel_����Ĳ���<br/>
	 * 		  ����ݿ��д��Ȩ����resource_channel_china.nba.com ���ص�Ȩ���ַ���china.nba.com
	 */
	public static String findPermission(String uePermission){
		if(propertis.containsKey(uePermission)){
			return propertis.getProperty(uePermission);
		}
		return "";
	}

	
	
	public static void reload(){
		try {
			propertis.load(UePermissionMap.class.getResourceAsStream(UE_PERM_FILE));
		} catch (IOException e) {
			System.err.println("load ue primission error!");
		}
	}
		
}
