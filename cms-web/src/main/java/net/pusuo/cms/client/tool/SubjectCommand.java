package net.pusuo.cms.client.tool;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Template;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.taglib.FragTag;
import com.hexun.cms.util.Util;

/**
 * @author Xulin
 * Created on 2004-12-06
 */
public class SubjectCommand implements CommandBean {

	private static final Log LOG = LogFactory.getLog(SubjectCommand.class);
	
	private int eid = -1;
	private int templateID = -1;
	private String backupdate = "";
	
	public SubjectCommand( int eid,int templateID ) {
		this.eid = eid;
		this.templateID = templateID;
	}

	public int getEid(){
		return this.eid;
	}
	public void setEid( int eid )
	{
		this.eid = eid;
	}

	public int getTid(){
		return this.templateID;
	}
	public void setTid( int tid )
	{
		this.templateID = tid;
	}

	public void setBackupdate( String backupdate )
	{
		this.backupdate = backupdate;
	}
	public String getBackupdate()
	{
		return this.backupdate;
	}

	/**
	 *����ģ��
	 */
	private void handleTemplate(){
		try{
			if( eid==-1 || templateID==-1 || backupdate==null || backupdate.equals("") )
			{
				LOG.warn("oldnews invalid parameters - "+eid+"  "+templateID+"  "+backupdate);
				return;
			}

			EntityItem eItem = (EntityItem)ItemManager.getInstance().get(new Integer(eid),EntityItem.class);
			Template template = (Template)ItemManager.getInstance().get(new Integer(templateID),Template.class);
			/*
			//���ݵ�����������ģ��
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add( Calendar.DAY_OF_MONTH, -1 );
			java.util.Date date = calendar.getTime();
			long ct = date.getTime();
			String yesterday = Util.formatDatetime( ct ,"yyyy-MM-dd");
			String tsb = yesterday+"%2000:00:00";
			String tse = yesterday+"%2023:59:59";
			*/
			String tsb = backupdate+"%2000:00:00";
			String tse = backupdate+"%2023:59:59";

			String url = "http://cms.pusuo.net:8080"+PageManager.FTWebPath(template,false)+"?ENTITYID="+eid+"&view="+FragTag.LIST_VIEW;
			url += "&begTime="+tsb+"&endTime="+tse;

			String content = Util.httpRequest( url );

			if( content==null ){
				// ����ģ���쳣
				LOG.error("oldnews error -- template file exception. ");
			}else{
				//д�ļ�
				String storePath = PageManager.getTStorePath( (EntityItem)eItem,templateID );
				int pos = storePath.indexOf("/",2);
				String backupdir = backupdate.replaceAll("-","");
				backupdir = backupdate.replaceAll("-","");
				storePath = storePath.substring( 0,pos )+"/oldnews/"+backupdir+storePath.substring(pos);
				//storePath = "/oldnews/"+Util.formatDatetime( ct,"yyyyMMdd" )+storePath;
				boolean flag = ClientFile.getInstance().write(content, storePath, true);
				LOG.debug( "backup oldnews -- "+storePath+"  ..."+(flag==true?"OK":"FAILURE") );
			}
		}catch(Exception e){
			LOG.error("oldnews exception -- "+e.toString());
		}
	 }
	 
	public void execute( ) throws Exception {
		try{
			handleTemplate();
		}catch(Exception e){
			LOG.error("oldnews excute exception -- "+e.toString());
		}
	}
}
