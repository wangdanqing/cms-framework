/*
 * 
 * @author chenqj
 * Created on 2004-8-25
 *
 */
package net.pusuo.cms.client.util;

import com.hexun.cms.Configuration;

/**
 * @author chenqj
 *
 */
public class ConfigUtils {
	
	public static int getIntValue(String name, int defaultValue){
		return getIntValue(Configuration.getInstance(), name, defaultValue);
	}
	
	public static int getIntValue(Configuration config, String name, int defaultValue){
		String sValue = config.get(name);
		int iValue = 0;
		try{
			iValue = Integer.parseInt(sValue);
		}catch(NumberFormatException e){
			iValue = defaultValue;
		}
		return iValue;
	}

}
