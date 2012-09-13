package net.pusuo.cms.client.util;

import javax.servlet.http.*;

import com.hexun.cms.Global;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;

import com.hexun.cms.client.ItemManager;

public class History {

    private static final int HISTORY_LEN = 20;
	public static void addRecord( HttpServletRequest request, HttpServletResponse response, String userName,int entityID ) throws Exception
    {
    	Cookie cookies[] = request.getCookies();
        // Return null if there are no cookies or the name is invalid.
        if(cookies == null || userName == null || userName.length() == 0 || entityID<=0) {
            return;
        }
        
        Cookie cookie = null;
        
        // Otherwise, we  do a linear scan for the cookie.
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals(userName) ) {
                cookie = cookies[i];
                break;
            }
        }

		String action="";
		EntityItem item = null;
        
        if(cookie==null){//还没有记录
        	item = (EntityItem)ItemManager.getInstance().get( new Integer(entityID),ItemInfo.getEntityClass());
        	if(item.getType()==ItemInfo.SUBJECT_TYPE){
        		action = "/subject.do?method=view&id="+item.getId();	
        	}else if(item.getType()==ItemInfo.NEWS_TYPE){
			action = "/news.do?method=view&id="+item.getId();
		}else if(item.getType()==ItemInfo.PICTURE_TYPE){
			action = "/picture.do?method=view&id="+item.getId();
		}else if(item.getType()==ItemInfo.HOMEPAGE_TYPE){
			action = "/homepage.do?method=view&id="+item.getId();
		}else if(item.getType()==ItemInfo.VIDEO_TYPE){
			action = "/video.do?method=view&id="+item.getId();
		}
        	cookie = new Cookie( userName,java.net.URLEncoder.encode( new String((item.getDesc()+Global.CMSCOLON+action).getBytes("UTF-8")) ) );
        }else{
        	String val = cookie.getValue();
        	val = new String( java.net.URLDecoder.decode(val).getBytes("ISO_8859_1"),"UTF-8" );
        	item = (EntityItem)ItemManager.getInstance().get( new Integer(entityID),ItemInfo.getEntityClass());
        	if(item.getType()==ItemInfo.SUBJECT_TYPE){
                        action = "/subject.do?method=view&id="+item.getId();
                }else if(item.getType()==ItemInfo.NEWS_TYPE){
                        action = "/news.do?method=view&id="+item.getId();
                }else if(item.getType()==ItemInfo.PICTURE_TYPE){
                        action = "/picture.do?method=view&id="+item.getId();
                }else if(item.getType()==ItemInfo.HOMEPAGE_TYPE){
                        action = "/homepage.do?method=view&id="+item.getId();
                }else if(item.getType()==ItemInfo.VIDEO_TYPE){
					action = "/video.do?method=view&id="+item.getId();
				}	
		

        	val = item.getDesc() + Global.CMSCOLON + action + Global.CMSVER + val;

        	String[] valArray = val.split( Global.CMSVER );
        	//排除重复的数据
        	valArray = exclude(valArray);
        	
        	StringBuffer newValue = new StringBuffer();
        	
        	for( int i=0;i<valArray.length&&i<=HISTORY_LEN;i++ ){
        		if( i==0 ){
				newValue.append( valArray[i] );
			}else{
				newValue.append( Global.CMSVER + valArray[i]);
			}
        	}
        	cookie.setValue( java.net.URLEncoder.encode( new String((newValue.toString()).getBytes("UTF-8")) ) );	
        }
        cookie.setMaxAge(60*60*24*365);
        cookie.setPath("/");
		cookie.setDomain(".cms.pusuo.net");
        response.addCookie(cookie);
    }

    public static String getRecord(HttpServletRequest request, String userName) throws Exception {
    	
        Cookie cookies[] = request.getCookies();

        if(cookies == null || userName == null || userName.length() == 0) {
            return null;
        }
        // Otherwise, we  do a linear scan for the cookie.
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals(userName) ) {
		return new String( java.net.URLDecoder.decode( cookies[i].getValue() ).getBytes("ISO_8859_1"),"UTF-8" );
            }
        }
        return "";
    }
    
    private static String[] exclude( String[] valArray ){
    
		for( int i=0;i<valArray.length;i++ ){
			String iv = valArray[i];
			for( int j=i+1;j<valArray.length;j++ ){
				String jv = valArray[j];
				if( iv.equals(jv) ){
					valArray[j] = "-1";
				}
			}
		}
		int count = 0;
		for( int i=0;i<valArray.length;i++ ){
			if( !valArray[i].equals("-1") ){
				count ++;
			}
		}

		String[] ret = new String[count];
		for( int i=valArray.length-1;i>=0;i-- ){
			if( !valArray[i].equals("-1") ){
				ret[--count] = valArray[i];
			}
		}

		return ret;
    }
    
}