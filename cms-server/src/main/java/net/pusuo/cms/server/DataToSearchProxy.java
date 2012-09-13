package net.pusuo.cms.server;

import net.pusuo.cms.server.util.HibernateUtil;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataToSearchProxy extends UnicastRemoteObject implements DTSProxy
{
	private static final Log LOG = LogFactory.getLog(DataToSearchProxy.class);
	private static final Object lock = new Object();
	private static DataToSearchProxy instance;

	private DataToSearchProxy()
	throws RemoteException
	{
	}

	public static DataToSearchProxy getInstance()
	throws RemoteException
	{
		if(instance==null){
			synchronized(lock){
				if(instance==null) instance = new DataToSearchProxy();
			}
		}
		return instance;
	}
	
	public List getList(int count)
	throws RemoteException
	{
		List list = new ArrayList();
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select id,action from(select id,action from interface_search order by timestamp asc) where rownum<=?";
			Session session = HibernateUtil.currentSession();
			c = session.connection();
			ps = c.prepareStatement(sql);
			ps.setInt(1,count);
			rs = ps.executeQuery();
			while(rs!=null && rs.next()){
				String data = rs.getString("id")+Global.CMSSEP+rs.getString("action");
				list.add(data);
			}
		}catch(Exception e){
			LOG.error("DataToSearchProxy error: "+e.toString());
		}finally{
			try{
				HibernateUtil.closeSession();
			}catch(Exception e){
				LOG.error("DataToSearchProxy getList error: "+e.toString());
			}
		}
		return list;
	}


	public void delete(int count)
	throws RemoteException
	{
		Connection c = null;
		PreparedStatement ps = null;
		try{
			Session session = HibernateUtil.currentSession();
			c = session.connection();
			String sql = "delete from interface_search where id in (select id from (select id,action from interface_search order by timestamp asc) where rownum<=?)";
			ps = c.prepareStatement(sql);
			ps.setInt(1,count);
			ps.executeUpdate();
			c.commit();
		}catch(SQLException se){
			try{
				c.rollback();
			}catch(Exception see){
				LOG.error("DataToSearchProxy delete error sqlexception: "+se.toString());
			}
		}catch(Exception e){
			LOG.error("DataToSearchProxy delete error:"+e.toString());
		}finally{
			try{
				HibernateUtil.closeSession();
			}catch(Exception e){
				LOG.error("DataToSearchProxy delete error:"+e.toString());
			}
		}
	}

}

