package net.pusuo.cms.client.compile;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.Global;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.CompileManager;
import com.hexun.cms.file.LocalFile;

import org.jdom.*;

public final class CompileTaskFactory extends TaskFactory
{
	private static final String IS_MUTIL = "m";
	private static final Log log = LogFactory.getLog( CompileTaskFactory.class );

	private static String filecache = null;
	public static String fileexception = null;

	private static String PAGINATION_QUEUE_FILE = null;
	private static Calendar calendar = null;

	private boolean loadfromfilecache = false;

	private static int getRecordNum = 30;
	private static int maxAccessDbtimes = 5;
	private static long normalInterval = 100;
	private static long relaxInterval = 5000;

	private static int emptyaccess = 0;

	private static XMLProperties prop;
	private static HashMap timeTypes;
	
	/**��ҳ��������com.hexun.cms.client.action.EntityAction����*/
	public static final Object PQUEUE_LOCK = new Object();
	/**�Ƿ����з�ҳ����*/
	private static boolean  PQUEUE_RUN = true;

	static {
		init();
	}

	public CompileTaskFactory( CompileMain main )
	{
		super( main );
	}

	private static void initConf() throws Exception
	{
		InputStream in = null;
		try
		{
			in = CompileMain.class.getResourceAsStream( "/compileconfig.xml" );
			prop = new XMLProperties( in );
		}catch(Exception e) {
			throw new Exception( "required compileconfig.xml.",e );
		}finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e) {
				throw new Exception( "close inputstream exception -- ",e );
			}
		}
	}


	private static void init()
	{
		try
		{
			timeTypes = new HashMap();

			filecache = Configuration.getInstance().get( "cms4.client.compile.filecache" );
			fileexception = Configuration.getInstance().get( "cms4.client.compile.fileexception" );

			// �ύ��ʵ��
			PAGINATION_QUEUE_FILE = Configuration.getInstance().get("cms4.client.pagination.queue");
			calendar = new GregorianCalendar();
			initConf();

			Element[] elements = prop.getProperties("skin.timetype");
                	for(int i=0; i<elements.length; i++)
                	{
                		Element e = elements[i];
                        	String name = e.getChild("name").getText();
                        	String pattern = e.getChild("pattern").getText();
				timeTypes.put( name, pattern );
                	}
		} catch(Exception e) {
			log.error("init time type in compileconfig.xml exception. ", e);
		}
	}
	public static String getTimetype( int ttid )
	{
		if( timeTypes.get(Global.CMSTIMETYPE+ttid)==null )
		{
			return null;
		} else {
			return (String)timeTypes.get( Global.CMSTIMETYPE+ttid );
		}
	}
	public static void setGetRecordNum( int _getRecordNum )
	{
		getRecordNum = _getRecordNum;
	}
	public static int getGetRecordNum()
	{
		return getRecordNum;
	}
	public static void setMaxAccessDbtimes( int _maxAccessDbtimes )
	{
		maxAccessDbtimes = _maxAccessDbtimes;
	}
	public static int getMaxAccessDbtimes()
	{
		return maxAccessDbtimes;
	}
	public static void setNormalInterval( long _normalInterval )
	{
		normalInterval = _normalInterval;
	}
	public static long getNormalInterval()
	{
		return normalInterval;
	}
	public static void setRelaxInterval( long _relaxInterval )
	{
		relaxInterval = _relaxInterval;
	}
	public static long getRelaxInterval()
	{
		return relaxInterval;
	}

	public void getTask()
	{
		long t1 = ct();
		try
		{
			int count=0, validcount=0;
			StringBuffer sb = new StringBuffer();

			// get entity info from database
			List dblist = null;

			long t2 = ct();
			if( !loadfromfilecache )
			{
				// ��file cache �ж�ȡ����ʵ����Ϣ
				dblist = getListFromFile();
				loadfromfilecache = true;
			} else {
				// ����ݿ��ȡ����ʵ����Ϣ
				dblist = getListFromDb( getRecordNum );
				if(dblist==null)
				{
					log.error("dblist is null [getListFromDb]");
					Thread.sleep( 5*1000 );
					return;
				}
				//log.info("dblist size from db:"+dblist.size());
				if( dblist.size()==0 )
				{
					++emptyaccess;
				} else {
					emptyaccess = 0;
				}

				// ����ݿ�������û��ʵ����Ҫ����,�����ҳ�ļ�����
				if( dblist.size()==0 )
				{
					calendar.setTimeInMillis( System.currentTimeMillis() );
					getListFromPaginationQueue( dblist );
					if( dblist.size()==0 )
					{
						//log.info("pagination queue is empty");
					} else {
						//log.info( "get to dblist from pagination queue "+dblist );
					}
				}

				if( dblist.size()==0 )
				{
					//log.info("relax access, sleep "+relaxInterval+"ms...");
					Thread.sleep( relaxInterval );
					return;
				}
			}
			long t3 = ct();

			List qList = new ArrayList();
			for(int i=0; dblist!=null && i<dblist.size(); i++)
			{
				String[] idaction = ((String)dblist.get(i)).split(Global.CMSSEP);
				/*
				 *���idaction�ĳ����Ƿ����3�͵����Ԫ���Ƿ��'m'���ж��Ƿ��Ƿ�ҳ���� 
				 */
				boolean isMutil = ((idaction.length == 3) && IS_MUTIL.equals(idaction[2]));
				int id = Integer.parseInt( idaction[0].trim() );
				int action = Integer.parseInt( idaction[1].trim() );
				++count;

				long t52 = ct();
				EntityItem eItem = (EntityItem)ItemManager.getInstance().get( new Integer(id), EntityItem.class );
				long t53 = ct();				

				if( eItem==null )
				{
					log.warn("ENTITY "+id+" is null. ");
				} else {
					if( eItem.getType()!=ItemInfo.NEWS_TYPE	&& eItem.getType()!=ItemInfo.SUBJECT_TYPE && eItem.getType()!=ItemInfo.HOMEPAGE_TYPE )
					{
						log.warn("ENTITY "+id+" is not NEWS OR SUBJECT OR HOMEPAGE.");
						continue;
					}

					long t54 = ct();

					addTemplateQueue(id, action,isMutil );
					addFragQueue( id );

					long t55 = ct();
					LocalFile.write( (t55-t54)+"\n", "/tmp/log/log_put", true);

					sb.append( id );
					sb.append( Global.CMSSEP );
					sb.append( action );
					sb.append( "\t" );
					++validcount;
				}
			}

			long t4 = ct();

			// backup entity info to file cache & delete record from database
			if( count>0 )
			{
				if( sb.length()>0 )
				{
					LocalFile.write( sb.toString(), filecache );					
				}
				if( loadfromfilecache )
				{
					deleteFromDb( count );
				}
			}			
		} catch(Exception e) {
			log.error("createTask error. "+e.toString());
			return;
		}
	}

	private void addTemplateQueue( int entityid, int action,boolean isMutil )
	{
		TemplateQItem qitem = new TemplateQItem();
		qitem.setEntityid( entityid );
		qitem.setAction( action );
		qitem.setMutil(isMutil);
		runTask( new CompileTask(qitem) );
	}
	private void addFragQueue( int entityid )
	{
		FragQItem qitem = new FragQItem();
		qitem.setEntityid( entityid );
		runTask( new CompileTask(qitem) );
	}

	/**
	 *	���쳣�����ļ������һ��ʵ����Ϣ
	 *	����cache file�д��ڵı���ʵ��,��д��
	 *	@param content ʵ����Ϣ
	 *	��ʽ entityid;0|1\t
	 */
	synchronized public static void handleError( String content )
	{
		try
		{
			String fileCont = LocalFile.read( fileexception );
			if( fileCont!=null )
			{
				if( fileCont.indexOf(content.trim())==-1 )
				{
					LocalFile.write( content, fileexception, true );
					log.error("add a exception ENTITY "+content);
				}
			}
		} catch(Exception e) {
			log.error("handleError exception. ", e);
		}
	}

	/**
	 *	���ļ�cache��ȡ����ʵ����Ϣ
	 *
	 */
	private List getListFromFile()
	{
		List list = new ArrayList();
		try
		{
			// ���ݴ��ļ��ж�ȡ����ʵ����Ϣ,д��file cache
			// ����cache file�д��ڵı���ʵ��,��д��
			String expCont = LocalFile.read( fileexception );
			String[] expConts = expCont.split("\t");

			String cacheCont = LocalFile.read( filecache );
			StringBuffer sb = new StringBuffer();
			for(int i=0; expConts!=null && i<expConts.length; i++)
			{
				if( cacheCont.indexOf(expConts[i].trim())==-1 )
				{
					sb.append( expConts[i] );
					sb.append("\t");
				}
			}
			LocalFile.write( sb.toString(), filecache, true );
			LocalFile.write( "", fileexception, false );

			String entityinfo = LocalFile.read( filecache );
			if( entityinfo==null )
			{
				log.error("get entity info from file error, check file "+filecache+".");
				return null;
			}
			String data[] = entityinfo.split("\t");
			for(int i=0; i<data.length; i++)
			{
				int idx = data[i].indexOf( Global.CMSSEP );
				if( idx>-1 && data[i].length()>idx )
				{
					int id = Integer.parseInt( data[i].substring(0,idx).trim() );
					int action = Integer.parseInt( data[i].substring(idx+1).trim() );
					list.add( id+Global.CMSSEP+action );
				}
			}
		} catch(Exception e) {
			log.error("getListFromFile exception. ", e);
		}
		return list;
	}
	
	/**     
	 *      ����ݿ�ȡ�������
	 *      @param count ��ȡ��¼��
	 */     
	private List getListFromDb( int count )
	{
		return CompileManager.getInstance().getList( count );
	}

	/**
	 *      ����ݿ�ɾ���¼
	 *      @param count ɾ���¼��
	 */
	private void deleteFromDb( int count )
        {
		try
		{
			CompileManager.getInstance().delete( count );
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static final long ct()
	{
		return System.currentTimeMillis();
	}

	// �ӷ�ҳʵ������ȡid�б�
	private void getListFromPaginationQueue( List idlist )
	{
		if(!isPQueueRun()){
			return;
		}
		
		if( PAGINATION_QUEUE_FILE==null )
		{
			log.warn( "PAGINATION_QUEUE_FILE is null." );
			return;
		}
		//Ϊ�˾����ܵü��ٶ�������Ӱ��,����ÿ�δ���ĵ�λΪ5��
		int perLen = 1;
		synchronized( CompileTaskFactory.PQUEUE_LOCK){
			String queue = LocalFile.read( PAGINATION_QUEUE_FILE );
			if( queue==null ) return;

			if( queue.trim().length()>0 )
			{
				String[] queues = queue.split(Global.CMSSEP);
				
				if(queues.length < perLen){
					//���еĳ���С��5,��ִ�б���
					return;
				}
					
				if( queues.length>0 )
				{
					int i =0;
					for(; i<perLen && i < queues.length;i++){
						//��ҳ����ĸ�ʽΪ:id;0;m m��ʾ�ôα����Ƿ�ҳ����
						idlist.add( queues[i]+Global.CMSSEP+"0"+Global.CMSSEP+IS_MUTIL );
					}
					String leftIds = "";					
					for(;i<queues.length;i++){
						//��ʣ���id�ٴ�д�뵽�ļ���,�ȴ�����һ�δ���
						if(i != perLen){
							leftIds+=Global.CMSSEP;
						}
						leftIds+=queues[i];
					}					
					LocalFile.write(leftIds, PAGINATION_QUEUE_FILE );
				}
			
			}
		}
	}
	
	public static boolean isPQueueRun(){
		return PQUEUE_RUN;
	}
	
	public static void setPQueueRun(boolean run){
		PQUEUE_RUN = run;
	}

}

