package net.pusuo.cms.client.util;

import com.hexun.cms.Configuration;

public class TimeUtils {
	
	//д�������Ŀ�ģ���Ϊ���ڳ������Ժ����ȥ������Ϣ����ʡ��Դ
	public static long currentTimeMillis(){
		if(Configuration.getInstance().getBoolean("cms4.debug"))
		return System.currentTimeMillis();
		else return 0;
	}
}
