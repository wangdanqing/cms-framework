package net.pusuo.cms.client.tool;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.tool.HWInterface;
import com.hexun.cms.tool.HotWordItem;

public class HWClient
{
	private static HWInterface hwi = null;
	private static HWClient hwc = null;
	private static Log log = LogFactory.getLog(HWClient.class);


	public static HWClient getInstance()
	{
		try {
			if ( hwc==null ){
				synchronized ( HWClient.class ){
					if ( hwc==null ){
						hwc = new HWClient();
					}
				}
			}
			return hwc;
		} catch ( Exception e ) {
			log.error("Unable to create HWClient instance . "+e.toString());
			throw new IllegalStateException("Unable to create HWClient instance.");
		}
	}

	private HWClient() {
		hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
	}

	public void loadAll( ) {

		try	{
			hwi.loadAll();
		}catch ( RemoteException re ){
			log.error("unable to load all from server ."+re.toString());
			re.printStackTrace();
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
	}

	public void load( int channelID ){
		try{
			hwi.load( channelID );
		}catch ( RemoteException re ){
			log.error("unable to load() from server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
	}

	public Collection list( int channelID )
	{
		try	{
			return hwi.list( channelID );
		}catch ( RemoteException re ){
			log.error("unable to list() from server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		
		return null;
	}
	
	public Hashtable listAll( )	{
		try	{
			return hwi.listAll( );
		}catch ( RemoteException re ){
			log.error("unable to listAll from server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		
		return null;
	}
	
	public HotWordItem get( String keyword,int channelID ){
		try{
			return hwi.get( keyword,channelID );
		}catch(RemoteException re){ 
			log.error("unable get from server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		return null;
	}

	public boolean add( HotWordItem hw,int channelID ){
		try{
			return hwi.add( hw,channelID );
		}catch(RemoteException re){
			log.error("unable add to server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		return false;
	}
	
	public boolean update( HotWordItem ohwi,HotWordItem nhwi,int channelID ){
		try{
			return hwi.update( ohwi,nhwi,channelID );
		}catch(RemoteException re){
			log.error("unable update to server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		return false;
	}
	
	public boolean delete( HotWordItem hw,int channelID ){
		try{
			return hwi.delete( hw,channelID );
		}catch(RemoteException re){
			log.error("unable delete from to server ."+re.toString());
			hwi = (HWInterface)ClientUtil.renewRMI("HWProxy");
		}
		return false;
	}
	
	public String hotWordReplace( String content,List hotList,boolean isSimple ){
	
		String regExp = "";
		StringBuffer sb = new StringBuffer();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcher matcher = new Perl5Matcher();
		Pattern pattern = null;
		Pattern linkPattern = null;
		PatternMatcherInput input = null;
		String g1="",g2="",g3="",kw="",url="",other="";
		int g1p=0,g2p=0,g3p=0;
		Iterator it = hotList.iterator();
		try{
			while (it.hasNext()) {
				HotWordItem h = (HotWordItem)it.next();
				kw = h.getKw();
				boolean isEng = false;//�ж��ȴ����Ƿ��Ӣ��
				for(int i=0;i<kw.length();i++){
					if(java.util.regex.Pattern.matches("[A-Za-z]+",kw.charAt(i)+"")){
						isEng = true;
						break;
					}
				}
				if(isEng){
					continue;
				}
				
				url = h.getUrl();
				other = h.getOther();
				if(url.equals("")&&other.equals("")){
					continue;//url��other��Ϊ���򲻴���
				}
				regExp = "([^>]{1})(\\s*"+kw+"\\s*)([^<]{1})";
				pattern = compiler.compile(regExp,Perl5Compiler.CASE_INSENSITIVE_MASK);
				input = new PatternMatcherInput(content);
				while (matcher.contains(input, pattern)) {
					g1 = matcher.getMatch().group(1);
					g1p = matcher.getMatch().beginOffset(1);
					g2 = matcher.getMatch().group(2);
					g2p = matcher.getMatch().beginOffset(2);
					g3 = matcher.getMatch().group(3);
					g3p = matcher.getMatch().beginOffset(3);
					if( g1.equals("'")||g1.equals("\"")||g1.equals("=") ){
						sb.append( content.substring(0,g1p+1) + g2 + content.substring(g2p+g2.length(),g3p) );
					}else{
						if(other!=null&&!other.equals("")){
							kw = kw.replace("\\","");
							if( isSimple ){//�Ǽ�ģʽ�����ǲ���������ѡ���
								sb.append( content.substring(0,g1p+1) + "<span class=articleLink><a href='"+url+"' target=_blank>" + g2 + "</a></span>" );
							}else{//����ģʽ������ԭ������ʾ
							if(url.equals(null)||url.trim().equals("")){
								sb.append( content.substring(0,g1p+1) + "<span class=articleLink>" + g2 + "</span>" + other + content.substring(g2p+g2.length(),g3p) );
								}else{
								sb.append( content.substring(0,g1p+1) + "<span class=articleLink><a href='"+url+"' target=_blank>" + g2 + "</a></span>" + other + content.substring(g2p+g2.length(),g3p) );
							}
						}
						}else{
							sb.append( content.substring(0,g1p+1) + "<span class=articleLink><a href='"+url+"' target=_blank>" + kw + "</a></span>" + content.substring(g2p+g2.length(),g3p) );
						}
					}
					break;
				}
				sb.append( content.substring(g3p) );
				g1p = 0;
				g2p = 0;
				g3p = 0;
				content = sb.toString();
				sb.delete(0,sb.length());
			}
			
		}catch(Exception e){
			log.error("hotWordReplace error: "+e.toString());
		}

		return content;
	}

}
