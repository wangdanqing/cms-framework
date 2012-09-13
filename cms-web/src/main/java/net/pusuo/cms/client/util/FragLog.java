package net.pusuo.cms.client.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.hexun.cms.client.file.ClientFile;

public class FragLog
{
	private static final Log log = LogFactory.getLog( FragLog.class );
	private static final Object lock = new Object();

	private static final String FRAGLOGROOT = "/" + "fraglog";
	private static final String FILESEP = "/";

	/**
	 *	�����Ƭ�ļ�����path, ȡ�ñ����ļ�����Ŀ¼
	 *	@param fragpath ��Ƭ�ļ�����path
	 *	@return �����ļ�����Ŀ¼
	 */
	public static String getFragLogDir( String filepath )
	{
		try
		{
			if( filepath==null || filepath.lastIndexOf(FILESEP)==-1 )
			{
				log.error("getFragLogDir --> filepath is invalid." + String.valueOf(filepath));
				return null;
			}
			return  FRAGLOGROOT + filepath.substring( 0, filepath.lastIndexOf(FILESEP) ) + FILESEP;
		}catch(Exception e)
		{
			log.error("getFragLogDir --> exception. "+e.toString());
			return null;
		}
	}

	/**
	 *	ȡ�����б����ļ�
	 *	@param fragpath ��Ƭ�ļ�����path
	 *	@return �����ļ�������
	 */
	public static String[] getFragLogNames( String filepath )
	{
		try
		{
		//	log.info("filepath:"+filepath);
			int idx1 = filepath.lastIndexOf(FILESEP);
			int idx2 = filepath.lastIndexOf(".");

			String logFragName = filepath.substring(idx1+1, idx2+1);

		//	log.info("logFragName:"+logFragName);

			String logFragDir = getFragLogDir( filepath );

		//	log.info("logFragDir:"+logFragDir);

			if( logFragDir!=null )
			{
				List list = new ArrayList();
				String[] fileList = ClientFile.getInstance().getFileList( logFragDir );
				for(int i=0; fileList!=null && i<fileList.length; i++)
				{
					if( fileList[i].indexOf(logFragName)!=-1 )
					{
						list.add( fileList[i] );
					}
				}
				Object[] temp = list.toArray();
				Arrays.sort( temp );
				String[] ret = new String[temp.length];
				System.arraycopy(temp,0,ret,0,temp.length);
				return ret;
			} else {
				log.error("getFragLogNames --> logFragDir is null.");
				return null;
			}
		} catch(Exception e) {
			log.error("getFragLogNames --> exception. "+e.toString());
			return null;
		}
	}

	/**
	 *	дlog�ļ�, ͬʱɾ����ɵ�log�ļ�
	 *	@param filepath	��Ƭ�ļ�·��
	 *	@param username	��Ƭ�޸��ߵ��û���
	 *	@deprecated
	 */
	public synchronized static void writeLog( String filepath, String username )
	{
		
	}
	
	/**
	 *	дlog�ļ�, ͬʱɾ����ɵ�log�ļ�
	 *	@param filepath	��Ƭ�ļ�·��
	 *	@param username	��Ƭ�޸��ߵ��û���
	 *	@param ip �û���ip��ַ
	 */
	public static void writeLog( String filepath, String username ,String ip){
		if(ip.split("\\.").length==4){
			//��Ҫת��IP
			String[] iparray=ip.split("\\.");
			for(int i=0;i<iparray.length;i++){
				while(iparray[i].length()<3){
					iparray[i]="0"+iparray[i];
				}
			}
			String ipStr=StringUtils.join(iparray,"");
		
			try
			{
				int idx1 = filepath.lastIndexOf(FILESEP);
				int idx2 = filepath.lastIndexOf(".");
				String logFileName = filepath.substring( idx1+1, idx2 );
				long currenttime = System.currentTimeMillis();
				logFileName += "." + currenttime +"_"+ username + "_"+ipStr+".inc";
	
				String content = ClientFile.getInstance().read( filepath );
				
				// ��Ƭ����==null, ˵���ǵ�һ�θ�����Ƭ, ���ñ���
				if( content==null )
				{
					return;
				}
				content=new String(content.getBytes(),"utf-8");
				String logFilePath = getFragLogDir( filepath ) + logFileName;
	
				boolean flag = ClientFile.getInstance().write( content, logFilePath, false );
				log.info("writeLog --> " + logFilePath + "..." + (flag==true?"OK":"FAILURE"));
	
				// ɾ����ɵ���Ƭ
				deleteLogFrag( filepath );
			} catch(Exception e) {
				log.error("writeLog --> exception. "+e.toString());
			}
		}
	}
	
	

	/**
	 *	ɾ����Ƭ�ļ�,ֻ�������10���޸ĵ��ļ�
	 *	@param filepath	��Ƭ�ļ�·��
	 */
	private static void deleteLogFrag( String filepath )
	{
		try
		{
			String logFragDir = FragLog.getFragLogDir( filepath );
			String[] logFragNames = FragLog.getFragLogNames( filepath );

			if( logFragNames.length>10 )
			{
				synchronized(lock)
				{
					if( logFragNames.length>10)
					{
						boolean ret = ClientFile.getInstance().delete( logFragDir+logFragNames[0] );
						log.info("deleteLogFrag --> delete frag log file [ "+logFragDir+logFragNames[0]+" ] ... " + (ret==true?"OK":"FAILURE"));
					}
				}
			}
		}catch(Exception e)
		{
			log.error("deleteLogFrag --> exception. " + e.toString());
		}
	}

	/*
	private static int getOldestFile(List fileNames)
	{
		long oldestFrag = System.currentTimeMillis();
		int ret = -1;

		for(int i=0; fileNames!=null && i<fileNames.size(); i++)
		{
			String fragName = (String)fileNames.get(i);
			log.info("fragName:"+fragName);
			int idx1 = fragName.indexOf(".");
			int idx2 = fragName.lastIndexOf("_");
			String fragNameTime = fragName.substring(idx1+1, idx2);
			if( Long.parseLong(fragNameTime)<oldestFrag )
			{
				oldestFrag = Long.parseLong(fragNameTime);
				ret = i;
			}
		}
		log.info("ret:"+ret);
		return ret;
	}
	*/
}


