package net.pusuo.cms.client.tool;

import com.caucho.sql.DBPool;
import java.sql.*;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.core.Template;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.tool.SubjectCommand;

public class SubjectList {
	
	private static final Log LOG = LogFactory.getLog(SubjectList.class);

	private static SubjectList manager = null;

	private static DBPool dp=null;

	public static SubjectList getInstance() {
		try { 
			if (manager == null) {
				synchronized (SubjectList.class) {
					if (manager == null) {
						manager = new SubjectList();
					}
				}
			}
			return manager;
		} catch (Exception e) {
			LOG.error("Unable to create SubjectList instance . " + e.toString());
			throw new IllegalStateException("Unable to create SubjectList instance.");
		}
	}
	
	private Connection getConnection() throws Exception{
		if (dp==null) 
			dp=new DBPool(
			"cmspool",
			"jdbc:oracle:thin:@192.168.1.170:1525:cms4",
			"cms4_test",
			"cms170",
			"oracle.jdbc.driver.OracleDriver",null,64);

		return dp.getConnection();
		
		//Class.forName("oracle.jdbc.driver.OracleDriver");
		//return DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.170:1521:alumni","cms4_test","cms170");
	}

	public List getTask()
	{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();

		try{
			String sql = "select entity_id,entity_template from cms_entity where entity_type=1";
			c = getConnection();
			ps = c.prepareStatement( sql );
			rs = ps.executeQuery();
			int eid = -1;
			String template = "";
			Template titem = null;
			int templID = -1;
			while( rs.next() ){
				eid = rs.getInt(1);

				template = rs.getString(2);
				if(template==null||template.equals("")) continue;
				if(template.indexOf(Global.CMSCOMMA)==-1) continue;
				String templArray[] = template.split(Global.CMSSEP);
				for(int i=0;i<templArray.length;i++){
					String templ[] = templArray[i].split(Global.CMSCOMMA);
					templID = Integer.parseInt(templ[0]);
					titem = (Template)ItemManager.getInstance().get(new Integer(templID),Template.class);
					if(titem.getMpage()==1){//�Ƿ�ҳģ�壬�γ�һ��command bean����List��
						SubjectCommand sc = new SubjectCommand(eid,templID);
						list.add(sc);
					}
				}
			}
		}catch(Exception e) {
			LOG.error("getTask error:",e );
		}finally {
			try{
				if( rs!=null ) rs.close();
				if( ps!=null ) ps.close();
				if( c!=null ) c.close();
			}catch(Exception e) {
				LOG.error("close error:"+e.toString());	
			}
		}

		return list;
	}
}
