package net.pusuo.cms.client.action;

import java.io.*;
import java.text.*;
import java.sql.*;
import com.caucho.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.*;
import com.hexun.cms.file.HttpFile;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

public class NewsDeleteAction extends BaseAction {
	
	private static final Log log = LogFactory.getLog(NewsDeleteAction.class);

	private static DBPool dp1 = null;

        //private static DBPool dp2 = null;

        Connection getConnection100() throws Exception {
                if (dp1==null) dp1=new DBPool("LogView",
                                              "jdbc:mysql://192.168.132.12:3307/logview",
                                              "cms",
                                              "cms!@#$%",
                                              "com.mysql.jdbc.Driver",null,64);
                return dp1.getConnection();
        }
	
	/*
        Connection getConnection200() throws Exception {
                if (dp2==null) dp2=new DBPool("LogView",
                                              "jdbc:mysql://192.168.132.12:3307/saytwo",
                                              "bx",
                                              "goodboy",
                                              "com.mysql.jdbc.Driver",null,64);
                return dp2.getConnection();
        }
	*/

	public ActionForward view(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
		
		return mapping.findForward("page");
	}

	public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
		ActionForward ret = null;
                ActionErrors errors = new ActionErrors();

		BaseForm dForm = (BaseForm)form;
		String url = (String)dForm.get("url");
		Boolean fromnews = (Boolean)dForm.get("fromnews");
		Boolean fromcomment = (Boolean)dForm.get("fromcomment");

		if (url != null) {
			if( url.indexOf("hexun.com") <0 ) {
				errors.add("errors.delete.notsohu", new ActionError("errors.delete.notsohu"));
                        	saveErrors(request, errors);
                        	return  mapping.findForward("failure");
			}
			if (fromnews!=null && fromnews.booleanValue()) {
				Connection c = null;        
				PreparedStatement p = null;        
				ResultSet r = null;        
				try {		
					String url1 = url.substring( url.indexOf("hexun.com")+9 );		
					c = getConnection100();		
					String sql = "update PVTABLE set log_flag=-1 where log_url=?";		
					sql = sql.replaceAll("PVTABLE",formatTable( System.currentTimeMillis() ) ) ;                					    p = c.prepareStatement( sql );		
					p.setString(1,url1);		
					p.execute();		
				} catch(Exception e) {		
				} finally {        
					if( r!=null ) try { r.close(); } catch (Exception re){}        
					if( p!=null ) try { p.close(); } catch (Exception pe){}        
					if( c!=null ) try { c.close(); } catch (Exception ce){}        
				}
			}

			if (fromcomment!=null && fromcomment.booleanValue()) {
				try {		
					long id = Long.parseLong(url.substring( url.length()-15,url.length()-6));		
					String s = HttpFile.read( "http://192.168.132.12/dcs/delete_topic.jsp?id="+id );
				} catch(Exception e) {		
				}
			}
		}
		
		// Report any errors we have discovered back to the failure page                
                if (!errors.isEmpty()) {               
                        saveErrors(request, errors);
                        ret = mapping.findForward("failure");
                } else {
                        ret = mapping.findForward("success");
                }                
                return ret;     	
        }

	private String formatTable( long time )
        {
                try{
                        java.util.Date date = new java.util.Date( time );
                        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd") ;
                        String dstr = formater.format(date);
                        return "PV"+dstr;
                }
                catch(Exception e)
                {
                        return null;
                }
        }
	
}
