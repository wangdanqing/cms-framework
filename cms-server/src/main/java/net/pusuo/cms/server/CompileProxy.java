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

public class CompileProxy extends UnicastRemoteObject implements CProxy
{
	private static final long serialVersionUID = -1L;
	private static final Log LOG = LogFactory.getLog(CompileProxy.class);
	private static final Object lock = new Object();
	private static CompileProxy instance;

	private CompileProxy()
	throws RemoteException
	{
	}

	public static CompileProxy getInstance()
	throws RemoteException
	{
		if(instance==null){
			synchronized(lock){
				if(instance==null) instance = new CompileProxy();
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
			String sql = "select id,action from(select id,action from cms_compile_queue order by timestamp asc) where rownum<=?";
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
			LOG.error("compileproxy error: "+e.toString());
		}finally{
			try{
				HibernateUtil.closeSession();
			}catch(Exception e){
				LOG.error("getList error: "+e.toString());
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
			String sql = "delete from cms_compile_queue where id in (select id from (select id,action from cms_compile_queue order by timestamp asc) where rownum<=?)";
			ps = c.prepareStatement(sql);
			ps.setInt(1,count);
			ps.executeUpdate();
			c.commit();
		}catch(SQLException se){
			try{
				c.rollback();
			}catch(Exception see){
				LOG.error("delete error sqlexception: "+se.toString());
			}
		}catch(Exception e){
			LOG.error("delete error:"+e.toString());
		}finally{
			try{
				HibernateUtil.closeSession();
			}catch(Exception e){
				LOG.error("delete error:"+e.toString());
			}
		}
	}

}

