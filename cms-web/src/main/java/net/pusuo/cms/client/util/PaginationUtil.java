package net.pusuo.cms.client.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.compile.CompileTaskFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;


/**
 * @author shijinkui
 */
public class PaginationUtil {
	private static final Log log = LogFactory.getLog(PaginationUtil.class);
	private static PaginationUtil pu_queue;
	private static final Object lock = new Object();
	
	/**
	 * ��õ���ģʽ��PaginationUtilʵ��
	 */
	public static PaginationUtil getInstance() {
		try {
			if (pu_queue == null) {
				synchronized ( lock ) {
					if (pu_queue == null) {
						pu_queue = new PaginationUtil();
					}
				}
			}
			return pu_queue;
		} catch (Exception e) {
			log.error("Unable to create PaginationUtil instance . " + e.toString());
			throw new IllegalStateException("Unable to create PaginationUtil instance.");
		}
	}
	/**
	 *	added by shijinkui 2008.03.06
	 *	����ҳר��idд���ļ�������
	 */
	public static void putPaginationToQueue( EntityItem entity )
	{
		if(entity.getId()<0 || entity == null || entity.equals(""))
			return;
		EntityItem qItem = (EntityItem)entity;
		if(entity.getType()==2)
		{
			EntityItem qPItem = (EntityItem)ItemManager.getInstance().get(new Integer(qItem.getPid()), EntityItem.class );
			while(qPItem != null && qPItem.getType() == 1){
				processOneEntityPt0Q(qPItem);
				qPItem = (EntityItem)ItemManager.getInstance().get(new Integer(qPItem.getPid()), EntityItem.class );
			}
		}		
	}
	
	private static void processOneEntityPt0Q(EntityItem entity)
	{
		
		try
		{
			if( !parseTemplate(entity) )  return;

			String queue = Configuration.getInstance().get("cms4.client.pagination.queue");
			if( queue==null || queue.length()==0 )
			{
				return;
			}

			synchronized( CompileTaskFactory.PQUEUE_LOCK )
			{
				String content = LocalFile.read( queue );
				if( content==null ) content = "";
				if( content.equals("") )
				{
					content = ""+entity.getId();
					LocalFile.write( content, queue );
					log.info("PaginationUtil: added "+entity.getId()+"  to pagination queue.");
				} else {
					if( content.indexOf(entity.getId()+"")>=0 )
					{
						return;
					} else {
						content += ";"+entity.getId();
						LocalFile.write( content, queue );
					log.info("PaginationUtil: added "+entity.getId()+"  to pagination queue.");
					}
				}
			}
		}catch(Exception e) {
			log.error( "registerPagination exception -- ", e );
			return;
		}
	}
	private static boolean parseTemplate( EntityItem entity ) throws Exception
	{
		String[] templates = entity.getTemplate().split(";");
		boolean ret = false;
		for(int i=0; i<templates.length; i++)
		{
			int tid = Integer.parseInt(templates[i].split(",")[0]);
			Template template = (Template)com.hexun.cms.client.ItemManager.getInstance().get( new Integer(tid), Template.class );
			if( template.getMpage()==2 )
			{
				ret = true;break;
			}
		}
		return ret;
	}
}
