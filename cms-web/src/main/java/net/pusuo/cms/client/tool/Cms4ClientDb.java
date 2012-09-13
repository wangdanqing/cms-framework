package net.pusuo.cms.client.tool;
import com.caucho.sql.DBPool;
import java.sql.*;
public class Cms4ClientDb{
	private static DBPool cms4_dp=null;
    private static final Object cms4_dp_lock = new Object(); 
    
    private static DBPool stock_dp = null;
    private static final Object stcok_dp_lock = new Object(); 
    
	/**
	ȡ��cms4�����ӳ�
	**/   
	public static  DBPool GetCMS4DBPool() throws Exception{
		synchronized(cms4_dp_lock){
        if (cms4_dp==null){
/*
                "CMS4Pool",
            "jdbc:oracle:thin:@192.168.1.170:1525:cms4",
                "cms4_test",
                "cms170",
            "oracle.jdbc.driver.OracleDriver",null,64);
*/
         cms4_dp=new DBPool(
           	"CMS4Pool",
            "jdbc:oracle:thin:@192.168.1.170:1521:cms",
         	"cms4_test",
           	"cms170",
            "oracle.jdbc.driver.OracleDriver",null,64);
         }         
        } 
        return cms4_dp;
    }  
       
    /**
    ȡ�ù�Ʊ�����ӳ�
    **/
  	public static  DBPool GetStockDBPool() throws Exception {
  		synchronized(stcok_dp_lock){
        	if (stock_dp == null)
          		stock_dp = new DBPool(
              		"stockPool",
              		"jdbc:oracle:thin:@192.168.41.201:1521:stock",
              		"stock",
              		"contentdev",
              		"oracle.jdbc.driver.OracleDriver", null, 64);
        }
    	return stock_dp;
  }
  
  public static void close(ResultSet rs){
  	try{
  		if(rs!=null){
  			rs.close();
  		}  		
	}catch(SQLException se){}
  }
  
  public static void close(PreparedStatement stmt){
	try{
		if (stmt != null){
          stmt.close();
        }
      }catch(SQLException se){}
   }

  public static void close(Connection conn){
      try{
        if (conn != null){
          conn.close();
        }
      }catch(SQLException se) {}
    }
}
