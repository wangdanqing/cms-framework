/**
 * 
 */
package net.pusuo.cms.client.xport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.xport.db.DbConnection;
import com.hexun.cms.client.xport.db.DbConnectionManager;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;

/**
 * @author shijinkui 090818
 * 
 */
public class ExportWorker {

	private static Log log = LogFactory.getLog(ExportWorker.class);

	public List exportData(SrcDbBean bean) {
		log.info("bean: " + bean.getId() + "||" + bean.getMediaId()+ "||" + bean.getNewsPriority()+ "||" +bean.getPid()+ "||" +bean.getTemplateId() + "||" + bean.getSql());
		if(bean.getId()==null || bean.getMediaId()==null || bean.getNewsPriority()==null || bean.getPid()==null || bean.getTemplateId()==null || bean.getSql()==null)
			return null;
		List result = new ArrayList();
		String _pid = bean.getPid(), 
			_templateid = bean.getTemplateId(),
			_sql = bean.getSql().trim(),
			_newsPriority = bean.getNewsPriority(),
			_mediaId = bean.getMediaId(),
			_id = bean.getId();
		// parent
		
		EntityItem entity = (EntityItem) ItemManager.getInstance().get(
				new Integer(_pid.trim()), EntityItem.class);
		log.info(entity);
		if (entity == null || !(entity instanceof Subject)) {
			log.error("错误：ExportWorker类中: type of entity(id=" + _pid + ") isn`t Subject.");
			return null;
		}
		Subject subject = (Subject) entity;
		int subjectId = subject.getId();
		int subjectChannel = subject.getChannel();

		// news list
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			log.info("connection: " + conn);
			log.info("数据连接源:" + conn.getMetaData().getURL());
			// 在sqlserver2000的jdbc中只能get一次一个字段，否则报错
			pst = conn.prepareStatement(_sql);
			// 区分标签提取和其他数据提取形式
			SpecXportUtil sxu = new SpecXportUtil();
			String flag = sxu.getPropValue("/opt/hexun/xport/xport_db_lastrecords.properties", "id-"+ _id + "-" + _pid);
			int _flag = -1, _cusor = -1;
			if(StringUtils.isNotEmpty(flag))
			{
				pst.setInt(1, Integer.parseInt(flag.trim()));
				_flag = Integer.parseInt(flag.trim());
			}else
				pst.setInt(1, 0);
			
			rs = pst.executeQuery();
			int fff = 0;
			while (rs.next()) {
				News news = CoreFactory.getInstance().createNews();
				news.setPid(subjectId);
				news.setChannel(subjectChannel);
				news.setTemplate("" + _templateid);
				news.setEditor(90);
				news.setMedia(Integer.parseInt(_mediaId));
				
				if(StringUtils.isEmpty(_newsPriority))
					news.setPriority(70);
				else
					news.setPriority(Integer.parseInt(_newsPriority));
				
				news.setExt("html");
				
				// fetch data from db.
				int infoId = rs.getInt("id");
				news.setDesc(rs.getString("title"));
				news.setText(transCharsToHtmlTag(rs.getString("content")));
				news.setTime(new Timestamp(System.currentTimeMillis()));
				
//				String authors = rs.getString("Author");
//				String keywords = rs.getString("KeyWord");
//				if (keywords != null && !keywords.equals(""))
//					news.setKeyword(keywords);
//				if (authors != null && !authors.equals(""))
//					news.setAuthor(authors);

//				String sourcename = rs.getString("SourceName");
//				if (sourcename != null && !sourcename.equals(""))
//					news.setParam(sourcename);

				result.add(news);
				_cusor = infoId;
				
			}
			
			// config
			SpecXportUtil sxu2 = new SpecXportUtil();
			if(_cusor > -1)
				sxu2.setPropKey("/opt/hexun/xport/xport_db_lastrecords.properties", "id-"+ _id + "-" + _pid, String.valueOf(_cusor));
			
		} catch (Exception e) {
			log.info("exception  xxxxxxxxxxxx2" + e.toString());
			log.info("sql:" + _sql);
			e.printStackTrace();
		} finally {
			DbConnection.close(rs, pst, conn);
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////////////

	static Map replacementMap = new LinkedHashMap();
	static {
		replacementMap.put(">  ", ">");
		replacementMap.put("> ", ">");
		replacementMap.put(">\r\n\r\n", ">");
		replacementMap.put(">\r\n", ">");
		replacementMap.put("\r\n  ", "\r\n&nbsp;&nbsp;");
		replacementMap.put("\r\n\r\n", "<br><br>");
		replacementMap.put("\r\n\r\n", "<br><br>");
		replacementMap.put("\r\n", "<br><br>");
	}

	public static String transCharsToHtmlTag(String content) {

		String result = content;

		Iterator iter = replacementMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = (String) replacementMap.get(key);

			result = result.replaceAll(key, value);
		}

		return result;
	}
}
