package net.pusuo.cms.client.compile;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.util.Util;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.client.ItemManager;

public class PublishTask implements Task
{
	private static final Log log = LogFactory.getLog(PublishTask.class);
	private QItem qitem;

	public PublishTask(QItem _qitem)
	{
		qitem = _qitem;
	}

	public void run()
	{
		if(qitem instanceof PublishQItem)
		{
			publish( (PublishQItem)qitem );
		}
		else
		{
			log.warn("invalid compile qitem, qitem should be TemplateQItem or FragQItem");
		}
	}

	/**
	 *	��������
	 *	
	 */
	private void publish(PublishQItem pqitem)
	{
		try
		{
			int pid = pqitem.getPid();

			EntityItem pitem = (EntityItem)ItemManager.getInstance().get( new Integer(pid), EntityItem.class );
			if( pitem==null )
			{
				log.error("pitem is null.");
				return;
			}

			News item = (News)ItemInfo.getItemByType( ItemInfo.NEWS_TYPE );

			String content = "��־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־����־��";
			item.setDesc( "AUTO NEWS �Ѻ�55555");
			item.setText( content );
			item.setPid( pid );
			item.setTime( new Timestamp(System.currentTimeMillis()) );
			item.setPriority( 70 );
			item.setStatus( EntityItem.ENABLE_STATUS );
			//item.setChannel( 169 );		// ����Ƶ��
			item.setChannel( 131 );		// ����Ƶ��
			item.setEditor( 90 );		// root
			item.setTemplate( "1131" );	// ��־��-����-����

			ItemManager.getInstance().update( item );
			log.info(" create a news. ");

		}catch(Exception e)
		{
			log.error("publish error. "+e.toString());
		}
	}


}

