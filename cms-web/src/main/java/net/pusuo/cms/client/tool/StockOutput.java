package net.pusuo.cms.client.tool;
import org.jdom.input.SAXBuilder;
import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;  
import java.util.List;
import com.caucho.sql.DBPool; 
import org.jdom.Document;
import org.jdom.Element;
/**
���ƾ�Ƶ���Ĺ�Ʊ�������͵���Ʊ,�ṩ����������һЩ�����Խӿ�
@author agilewang
@date 2005-7-8
**/
public class StockOutput {
	//��Ʊ�ṩ�Ľӿ�
	static final String STOCK_I_PHP = "http://stock.business.hexun.com/publish/reg.php";

    public StockOutput() {
    }
           
    private static final String sql = " insert into sf_cms_article_test (cms_seq,op_stat,articleid,articletitle,articlelink,publishdate,keywords,pcolumn,industry,modidate ) values(sf_cms_article_test_seq.nextval,-1,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,sysdate)";
    
  
    private static Connection getConnection() throws Exception {
    	return Cms4ClientDb.GetStockDBPool().getConnection();
    }
  
 	/**
  	POST��ʽ������ҳ
  	**/
	public static String getPost(String weburl, String inputData) throws Exception {
        String returnCode = "";
        StringBuffer sb = new StringBuffer();    
        HttpURLConnection conn = null; 
        BufferedOutputStream out = null;
        BufferedReader in = null; 
        //BufferedInputStream in = null;
        try {
          URL url = new URL(weburl);    
          conn = (HttpURLConnection)url.openConnection();
          conn.setDoInput(true);
          conn.setDoOutput(true);
          conn.setRequestMethod("POST");
          conn.setAllowUserInteraction(false);
          conn.setUseCaches(false);    
          out = new BufferedOutputStream(conn.getOutputStream());
          //ʹ��gbk����
          byte[] bdat = inputData.getBytes("GBK");
          out.write(bdat, 0, bdat.length);
          out.flush();
          out.close();    
          out = null;
			
		  //Ĭ�϶Է�ʹ�õ���gbk			      
          in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"GBK"));    
          String data = null;
          while ((data = in.readLine())!=null) {
            sb.append(data);
          }          
          in.close();
          in = null;
	      returnCode = sb.toString();
        }catch (Exception e) {
          System.out.println("Exception:" + sb.toString());
        }finally{           
        	try{         	
        		if(out!=null){
        			out.close();
        		}
        	}catch(Exception e){}
        	
	       	try{         	
        		if(in!=null){
        			in.close();
        		}
        	}catch(Exception e){} 
        	
        	if(conn != null){
        		conn.disconnect();
        	}
        }    
        return returnCode;
	}                                 

    
    /**
    ������stock.business.hexun.com/publish/reg.php���صĽ��,������"/"�ָ��Ĺ�Ʊ����
    **/
  	public static String extractStockCodes(final String article){
        
        if(article == null || article.trim().equals(""))
          return "";
          
        /*
        ���ù�Ʊ��������ӿ�
        �����ŵ����ݷ�������stock.business.hexun.com/publish/reg.php�õ���Ʊ�Ľ������
        */
        String inputdata = new String ("VTI-GROUP=0&text=" + article.trim ());
        String content = null;
        try {              
          content = getPost (STOCK_I_PHP, inputdata);
        } catch (Exception e){
        	throw new RuntimeException("resultFromStcok Error",e);
        }    
         
    
        String codes = "";
        String STOCK_CODE_REGEX = "code=";
        int STOCK_CODE_LEN = 6;
        String SEPARATOR = "/";
    
        String[] segments = content.split(STOCK_CODE_REGEX);
        for(int i = 1; i < segments.length; ++i){    
        	// ��ù�Ʊ���룬�����뵽�ַ���
          	String code = segments[i].substring(0, STOCK_CODE_LEN);
          	if(codes.indexOf(code) != -1){
            	continue;                 
            }
          	codes += code;
          	codes += SEPARATOR;
        }
    
        if(codes.length() > 0)
          return codes.substring(0, codes.length() - SEPARATOR.length());
        else
          return codes;
  }

  
    public static  void toDB(int articleId,String articleTitle,String articleLink,String publishDate,String keywords,String pcolumn, String industry) {
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet r = null;
  
      try {      
        conn = getConnection();
        ps = conn.prepareStatement(sql);      
        ps.setInt(1, articleId);
        ps.setString(2, articleTitle);
        ps.setString(3, articleLink);
        ps.setString(4, publishDate);
        ps.setString(5, keywords);
        ps.setString(6, pcolumn);
        ps.setString(7, industry);
        ps.executeUpdate();
      }catch (Exception ex) {
  		throw new RuntimeException("toDB Error",ex);
      }finally{
          Cms4ClientDb.close(ps);
          Cms4ClientDb.close(conn);
      }
    }     
  
  	/**   
	���ļ���ȡ��maxID
	**/
	public static int getMaxIDFromFile(String localFile){   
    	int maxID = -1;    	
      	BufferedReader br = null;	    	
    	try{		
    		br = new BufferedReader(new FileReader(localFile));
    		String s = br.readLine();		
    		if(s!=null && !s.equals("") ){    			
    			maxID = Integer.parseInt(s);    			
    		}    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{			
    			if(br!=null) br.close();						
    		}catch(Exception e){}           		
    	} 
    	return maxID;   	
	}                     
	       
	/**
	����ݿ���ȡ��maxID
	@param c Ϊ�˱����ظ��Ļ������,��������ڴ˷����ﲻ�ᱻ�ر�
	**/
	public static int getMaxIDFromDB(Connection c){		
        PreparedStatement p = null;
        ResultSet r = null;
    	int maxID = -1;
        try{
           String sql = "select max(id) from entityitem";    	   
           p = c.prepareStatement(sql);
           r = p.executeQuery();
           if(r.next()){
    	   		maxID = r.getInt(1);
           } 
           return maxID;
        }catch(SQLException e){               
        	throw new RuntimeException("getMaxIDFromDB error",e);        	
        }finally{
        	Cms4ClientDb.close(r);
        	Cms4ClientDb.close(p);   
        }
	}              
	
	/**
		��ȡ�¹�Ʊ�������ļ�,����һ��Map(key:id,value:stock name)
	**/
	public static Map getStcokConfig(String stockXml){                
		InputStream in = null;
		Map idMap = new HashMap();
		try{
    		in = new FileInputStream(stockXml);
    		SAXBuilder builder = new SAXBuilder();                         		
    		Document document = builder.build(in);
    		Element root = document.getRootElement();
            List stockList = root.getChildren("stock");
    		for(int k1=0;k1<stockList.size();k1++){
					String stockName = (String) ((Element)stockList.get(k1)).getAttributeValue("name");
					List idList = ((Element)stockList.get(k1)).getChildren("pname");
					for(int k2=0;k2<idList.size();k2++){
						String id = ((Element)idList.get(k2)).getAttributeValue("id");
						idMap.put(id,stockName);						
					}
    		}                                                  
    		return idMap;
    	}catch(Exception e){
    		throw new RuntimeException("getStcokConfig error",e);
    	}finally{
    		try{
    			if(in!=null){
    				in.close();
    			}
    		}catch(Exception e){}
    	}
	}
  
  
}
