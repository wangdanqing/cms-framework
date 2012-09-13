package net.pusuo.cms.client.action;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.jar.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.action.DynaActionForm;

import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.core.*;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

public class UploadAction extends BaseAction {
	
	private static final Log LOG = LogFactory.getLog(UploadAction.class);
	private static String UPLOAD_PATH = "upload";

	public ActionForward view(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
		
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		
		List channellist = new ArrayList();
		
		Authentication auth = null;
                try{
                        auth = AuthenticationFactory.getAuthentication(request,response);
		}catch(UnauthenticatedException ue){
                        errors.add("auth.failure", new ActionError("auth.failure"));
                        saveErrors(request, errors);
                        return mapping.findForward("failure");
		}
		channellist = auth.getChannelList();

		List filelist = new ArrayList();
		/*Iterator aa = channellist.iterator();
		while (aa.hasNext()) {
			LOG.info("eeeeeeeeeee:"+((Channel)aa.next()).getDesc());
		}*/
		request.setAttribute("channellist",channellist);
		request.setAttribute("filelist",filelist);
		ret =  mapping.findForward("page");
	
		return ret;
	}
	
	public ActionForward upload(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

                List channellist = new ArrayList();

                Authentication auth = null;
                try{
                        auth = AuthenticationFactory.getAuthentication(request,response);
                }catch(UnauthenticatedException ue){
                        errors.add("auth.failure", new ActionError("auth.failure"));
                        saveErrors(request, errors);
                        return mapping.findForward("failure");
                }

                channellist = auth.getChannelList();

		List filelist = new ArrayList();
		try{
			String contentType = request.getContentType();
			if (contentType == null || contentType.indexOf("multipart/form-data") == -1){
				errors.add("upload.failure", new ActionError("upload.failure"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
			
			DynaActionForm fm = (DynaActionForm) form;
			Integer channelID = (Integer)fm.get("channel");

			MultipartRequestHandler mrh=(MultipartRequestHandler) fm.getMultipartRequestHandler();
			Hashtable ht = (Hashtable)mrh.getFileElements();
			Enumeration keys = null;
			
			if(ht.size() <= 0){
				errors.add("upload.failure", new ActionError("upload.failure"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
				
			keys = ht.keys();	
			if(keys.hasMoreElements()){
				String keyStr = (String)keys.nextElement();
				FormFile temp =(FormFile)ht.get(keyStr);
				String fileName = temp.getFileName();
				if(fileName !=null || !fileName.equals("")){
					byte[] b_content = temp.getFileData();
					if( b_content.length > 0 ){
						Channel channel = (Channel)ItemManager.getInstance().get( channelID,Channel.class );
						filelist = uploadZip(b_content,"http://"+channel.getName()+"/"+UPLOAD_PATH,channel.getDir()+"/"+UPLOAD_PATH);
						request.setAttribute("filelist",filelist);
					}
				}
			}
		}catch(Exception e){
			errors.add("upload.failure", new ActionError("upload.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		request.setAttribute("channellist",channellist);
		request.setAttribute("filelist",filelist);
		ret = mapping.findForward("page");
		
		return ret;
	}


	private List uploadZip(byte[] content,String domain, String destfile)
	{
		List ret = new ArrayList();
		try{
			JarInputStream jins = new JarInputStream( new ByteArrayInputStream(content) );
			ZipEntry       jarentry = null;
			long datasize = 0;
			String filename = null;
			int ava = 0,readbyte = 0;
			long allready_read = 0;
			String destFileName = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while((ava = jins.available())>0)
			{
				baos.reset();
				jarentry = jins.getNextEntry();
				//agilewang fix bug begin:dead circle at 2005-7-25
				if(jarentry==null){
					break;
				}
				if(jarentry.isDirectory()){
                                        continue;
				}	
				//agilewang fix bug end
				datasize = jarentry.getSize();
				filename = jarentry.getName();
				destFileName = "/"+destfile + "/" +filename;
				if(datasize>0)
				{
					byte[] databuff = new byte[2048];
					allready_read = 0;
					while(allready_read<datasize){
						readbyte = jins.read(databuff,0,2048);
						allready_read+=readbyte;
						baos.write( databuff , 0 , readbyte);
					}
					ClientFile.getInstance().write(baos.toByteArray(),destFileName,true);
					ret.add(domain+"/"+filename);
				}

			}

			return ret;
		}catch(Exception e){
			LOG.error("error:"+e.toString());
			//e.printStackTrace();
			return ret;
		}
	}
	
}
