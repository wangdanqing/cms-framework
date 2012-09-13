package net.pusuo.cms.client.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import com.hexun.cms.Configuration;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.Permission;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.FragLog;
import com.hexun.cms.client.util.FragZipProcessor;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.ZipExpress;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.CommonFrag;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.util.Util;

public class FragEditAction extends BaseAction {
	private static final Log log = LogFactory.getLog(FragEditAction.class);

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	private List getAuthPerms(Authentication auth) {
		Set permsSet = auth.getUserPermission().getResourcePerms();
		Iterator permsItr = permsSet.iterator();

		List ret = new ArrayList();
		while (permsItr.hasNext()) {
			String perm = (String) permsItr.next();
			if (perm.startsWith(Permission.RESOURCE + Permission.CHANNEL)
					|| perm.startsWith(Permission.RESOURCE + Permission.DEPARTMENT)) {
				int idx = perm.lastIndexOf("_");
				ret.add(perm.substring(idx + 1));
			}
		}
		return ret;
	}

	public ActionForward fragundolist(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("application/X-JSON;charset=utf-8");
		PrintWriter out = response.getWriter();
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			out.print(getJsonErrorString("û�е�¼,�����µ�¼"));
			return null;
		}

		List result = new ArrayList();
		// 1.ȡ��Ƶ�������õ�·��
		BaseForm _form = (BaseForm) form;
		// �����Ƭ�洢��ַ
		int eid = ((Integer) _form.get("entityid")).intValue();
		int tid = ((Integer) _form.get("templateid")).intValue();
		int tfmapid = ((Integer) _form.get("tfmapid")).intValue();
		boolean hasPerm = hasPermision(auth, eid, tid, tfmapid);
		if (!hasPerm) {
			out.print(getJsonErrorString("��û�б༭����Ƭ��Ȩ��!"));
			return null;
		}
		String storepath;
		try {
			storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, true);

			String channelStorePath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, false);

			Map sp = new HashMap();
			sp.put("storepath", channelStorePath);
			result.add(channelStorePath);
		} catch (Exception e) {
			log.error(e);
			out.print(getJsonErrorString("ȡ����Ƭ·������,����ϵ����Ա."));
			return null;
		}

		// 2.ȡ���޸ļ�¼��list
		List a;
		try {
			a = getLogFragMoreInfo(storepath);
			Map logInfo = new HashMap();
			logInfo.put("loglist", a);
			result.add(logInfo);
		} catch (Exception e) {
			log.error(e);
			out.print(getJsonErrorString("ȡ����Ƭ�б����,����ϵ����Ա."));
			return null;
		}

		out.print(JSONArray.fromCollection(result).toString());
		return null;
	}

	public ActionForward fragundo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("application/X-JSON;charset=utf-8");
		PrintWriter out = response.getWriter();
		String path = request.getParameter("path");
		String content;
		try {
			content = ClientFile.getInstance().read(path);
		} catch (Exception e) {
			content = "";
		}
		//content = Util.unicodeToGBK(content).trim();
		String[] result = new String[1];
	//	result[0] = content;
		result[0] = new String(content.getBytes(),"UTF-8");
		out.print(JSONArray.fromArray(result));
		return null;
	}

	/**
	 * ��ݲ���õ���Ƭ����
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.io.IOException
	 */
	public ActionForward fragget(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("application/X-JSON;charset=utf-8");
		PrintWriter out = response.getWriter();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			out.print(getJsonErrorString("û�е�¼,�����µ�¼"));
			return null;
		}

		int eid = Integer.parseInt(request.getParameter("entityid"));
		int tid = Integer.parseInt(request.getParameter("templateid"));
		int tfmapid = Integer.parseInt(request.getParameter("tfmapid"));
		boolean hasPerm = hasPermision(auth, eid, tid, tfmapid);
		if (!hasPerm) {
			out.print(getJsonErrorString("��û�б༭����Ƭ��Ȩ��!"));
			return null;
		}
		String storepath;
		try {
			storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, true);
		} catch (Exception e) {
			log.error(e);
			out.print(getJsonErrorString("ȡ����Ƭ·������,����ϵ����Ա."));
			return null;
		}

		String content;
		try {
			content = ClientFile.getInstance().read(storepath);
		} catch (Exception e) {
			log.error(e);
			out.print(getJsonErrorString("ȡ����Ƭ���ݴ���,����ϵ����Ա."));
			return null;
		}
		String[] succ = new String[2];
		succ[0] = "succ";
		succ[1] = new String(content.getBytes(),"UTF-8");
		//	succ[1] = new String(content.getBytes(),"utf-8");
		out.print(JSONArray.fromArray(succ).toString());
		return null;
	}

	/*
	 * private String getAuthPermsString( Authentication auth ) { StringBuffer
	 * sb = new StringBuffer(); List list = getAuthPerms(auth); for(int i=0; i<list.size();
	 * i++) { if( i==0 ) { sb.append((String)list.get(i)); } else {
	 * sb.append("|"); sb.append((String)list.get(i)); } } return sb.toString(); }
	 */
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// 1: ��ͨ��Ƭ 4: Ƶ������ 5: hexun ����
		int fragType = ((Integer) _form.get("fragtype")).intValue();
		if (fragType == 1) {
			return fragView(mapping, form, request, response, auth);
		} else if (fragType == 2) {
			return channelFragView(mapping, form, request, response, auth);
		} else if (fragType == 3) {
			return sohuFragView(mapping, form, request, response, auth);
		} else {
			errors.add("errors.fragedit", new ActionError("errors.fragedit.invalidfragtype"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * ������ͨ��Ƭ
	 * 
	 */
	private ActionForward fragView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		try {
			// �����Ƭ�洢��ַ
			int eid = ((Integer) _form.get("entityid")).intValue();
			int tid = ((Integer) _form.get("templateid")).intValue();
			int tfmapid = ((Integer) _form.get("tfmapid")).intValue();

			boolean hasPerm = hasPermision(auth, eid, tid, tfmapid);

			String storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, true);
			log.info("=--= storepath: " + storepath);

			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			// ��ȡ��Ƭ����
			String content = null;
			try {
				content = ClientFile.getInstance().read(storepath);
			} catch (Exception e) {
				log.error("get content exception, maybe " + storepath + " is not exist. " + e.toString());
			}
			if (content == null) {
				_form.set("content", "");
			} else {
			//	content = Util.unicodeToGBK(content).trim();
				content=new String(content.getBytes(),"UTF-8");
				_form.set("content", content);
			}
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			return mapping.findForward("fragedit");
		} catch (Exception e) {
			log.error("fragedit -- save frag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.fragview", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * @param auth
	 * @param entityId
	 * @param templateId
	 * @param tfmapid
	 * @return
	 */
	private boolean hasPermision(Authentication auth, int entityId, int templateId, int tfmapid) {
		EntityItem eItem = (EntityItem) ItemManager.getInstance()
				.get(new Integer(entityId), EntityItem.class);
		Template titem = (Template) ItemManager.getInstance().get(new Integer(templateId), Template.class);
		Iterator itr = titem.getTFMaps().iterator();
		TFMap tfmap = null;
		while (itr.hasNext()) {
			TFMap tfm = (TFMap) itr.next();
			if (tfm.getId() == tfmapid) {
				tfmap = tfm;
				break;
			}
		}

		List authPerms = getAuthPerms(auth);
		Channel channel = (Channel) ItemManager.getInstance().get(new Integer(eItem.getChannel()),
				Channel.class);
		boolean hasPerm = false;
		if (tfmap.getPermission() == null || tfmap.getPermission().equals("")) {
			hasPerm = hasPerm(authPerms, tfmap.getPermission() + "|" + channel.getName());
		} else {
			hasPerm = hasPerm(authPerms, tfmap.getPermission());
		}
		return hasPerm;
	}

	/**
	 * ��ʾƵ��������Ƭ
	 */
	private ActionForward channelFragView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		try {
			// �����Ƭ�洢��ַ
			int channelId = ((Integer) _form.get("channelid")).intValue();
			int entityId = ((Integer) _form.get("entityid")).intValue();
			String fragName = (String) _form.get("fragname");

			int hpId = ItemUtil.getHpEntity(entityId).getId();

			CommonFrag cf = null;
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			for (int i = 0; list != null && i < list.size(); i++) {
				CommonFrag temp = (CommonFrag) list.get(i);
				if (temp.getChannel() == channelId && temp.getName().equals(fragName)) {
					cf = temp;
					break;
				}
			}
			if (cf == null) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.nocommonfrag", fragName));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			List authPerms = getAuthPerms(auth);
			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(channelId), Channel.class);
			boolean hasPerm = hasPerm(authPerms, cf.getPermission() + "|" + channel.getName());

			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� List authPerms = getAuthPerms( auth );
			 * boolean hasPerm = false; // ��ƬȨ�� = ����Ƶ��+����Ƶ��/���� Channel channel =
			 * (Channel)ItemManager.getInstance().get(new
			 * Integer(channelId),Channel.class); String permissions =
			 * channel.getName()+"|"+String.valueOf(cf.getPermission()); for(int
			 * i=0; authPerms!=null && i<authPerms.size(); i++) { if(
			 * permissions.indexOf( (String)authPerms.get(i) )!=-1 ) { hasPerm =
			 * true; break; } }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String storepath = PageManager.getFStorePath(channelId, hpId, fragName, true);
			// ȡ��Ƭ�ļ�����
			String content = null;
			try {
				content = ClientFile.getInstance().read(storepath);
			} catch (Exception e) {
				log.error("get content exception, maybe " + storepath + " is not exist. " + e.toString());
			}
			if (content == null) {
				_form.set("content", "");
			} else {
				//content = Util.unicodeToGBK(content).trim();
				content=new String(content.getBytes(),"UTF-8");
				_form.set("content", content);
			}

			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			request.setAttribute("com.hexun.cms.client.action.frageditaction.commonfrag", cf);
			return mapping.findForward("commonfragedit");
		} catch (Exception e) {
			log.error("fragedit view commonfrag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.commonfragview", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * ��ʾhexun������Ƭ
	 */
	private ActionForward sohuFragView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		try {
			// �����Ƭ�洢��ַ
			String fragName = (String) _form.get("fragname");

			CommonFrag cf = null;
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			for (int i = 0; list != null && i < list.size(); i++) {
				CommonFrag temp = (CommonFrag) list.get(i);
				if (temp.getChannel() == -1 && temp.getName().equals(fragName)) {
					cf = temp;
					break;
				}
			}
			if (cf == null) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.nocommonfrag", fragName));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			/*
			 * Channel channel = (Channel)ItemManager.getInstance().get( new
			 * Integer(cf.getChannel()), Channel.class ); if( channel==null ) {
			 * errors.add("errors.fragedit", new
			 * ActionError("errors.fragedit.nocommonfrag", fragName));
			 * saveErrors( request, errors ); return
			 * mapping.findForward("failure"); }
			 */

			List authPerms = getAuthPerms(auth);
			boolean hasPerm = hasPerm(authPerms, cf.getPermission());
			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� List authPerms = getAuthPerms( auth );
			 * boolean hasPerm = false;
			 * 
			 * String permissions = String.valueOf(cf.getPermission());
			 * 
			 * for(int i=0; authPerms!=null && i<authPerms.size(); i++) { if(
			 * permissions.indexOf( (String)authPerms.get(i) )!=-1 ) { hasPerm =
			 * true; //break; } }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String storepath = PageManager.getFStorePath(fragName);
			// ȡ��Ƭ�ļ�����
			String content = null;
			try {
				content = ClientFile.getInstance().read(storepath);
			} catch (Exception e) {
				log.error("get content exception, maybe " + storepath + " is not exist. " + e.toString());
			}
			if (content == null) {
				_form.set("content", "");
			} else {
			//	content = Util.unicodeToGBK(content).trim();
				content=new String(content.getBytes(),"UTF-8");
				_form.set("content", content);
			}

			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			request.setAttribute("com.hexun.cms.client.action.frageditaction.commonfrag", cf);
			return mapping.findForward("sohufragedit");
		} catch (Exception e) {
			log.error("fragedit view commonfrag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.commonfragview", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	public ActionForward fragContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		response.reset();
		response.setContentType("application/X-JSON;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			out.print(getJsonErrorString("ERROR!"));
			return null;
		}
		String fragId = request.getParameter("tfmapid");
		String entityId = request.getParameter("eid");
		if (StringUtils.isBlank(fragId) || StringUtils.isBlank(entityId) || !StringUtils.isNumeric(entityId)
				|| !StringUtils.isNumeric(fragId)) {
			out.print(getJsonErrorString("�������"));
			return null;
		}
		String storepath = null;
		try {
			storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(entityId), EntityItem.class), Integer.parseInt(fragId), true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log.error("get frag store path error", e);
			out.print(getJsonErrorString("store path ERROR!"));
			return null;
		}

		if(storepath==null){
			log.error("store path null ERROR");
			out.print(getJsonErrorString("store path null ERROR!"));
			return null;
		}
		String content = null;
		try {
			content = ClientFile.getInstance().read(storepath);
		} catch (Exception e) {
			log.error("get content exception, maybe " + storepath + " is not exist. " + e.toString());
		}
		
	//	content = Util.unicodeToGBK(content).trim();
		String[] result = new String[1];
		try {
			//result[0] = new String(content.getBytes("utf-8"));
			result[0]= new String(content.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			result[0]="";
		}
		out.print(JSONArray.fromArray(result));
		
		return null;

	}

	public ActionForward saveNew(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.reset();
		response.setContentType("application/X-JSON;charset=utf-8");
		PrintWriter out = response.getWriter();
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			out.print(getJsonErrorString("û�е�¼,�����µ�¼"));
			return null;
		}
		BaseForm _form = (BaseForm) form;
		// �����Ƭ�洢��ַ
		int eid = ((Integer) _form.get("entityid")).intValue();
		int tid = ((Integer) _form.get("templateid")).intValue();
		int tfmapid = ((Integer) _form.get("tfmapid")).intValue();

		boolean hasPerm = hasPermision(auth, eid, tid, tfmapid);

		if (!hasPerm) {
			out.print(getJsonErrorString("��û�б༭����Ƭ��Ȩ��!"));
			return null;
		}

		String storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
				new Integer(eid), EntityItem.class), tfmapid, true);

		String content = URLDecoder.decode(request.getParameter("content"), "UTF-8");
		//
		// content = HtmlTagFilter.tag2Lower( content );
	//	content = Util.GBKToUnicode(content);
		if (content.equals(""))
			content = " ";

		// ������Ƭ
		String userName = auth.getUserName();
		FragLog.writeLog(storepath, userName, request.getRemoteAddr());

		// ������Ƭ����
		ClientFile.getInstance().write(content, storepath);

		String[] success = new String[2];
		success[0] = "succ";
		//success[1] =new String("����ɹ�".getBytes(),"utf-8");
		//success[1] =new String("����ɹ�".getBytes("ISO_8859_1"),"utf-8");
		//success[1] =new String("����ɹ�".getBytes("ISO_8859_1"),"gbk");
		success[1] =new String(" Saved Successfully! ".getBytes("ISO_8859_1"),"UTF-8");
		out.println(JSONArray.fromArray(success).toString());

		return null;
	}   

	/**
	 * @param errorString
	 * @return
	 */
	private String getJsonErrorString(String errorString) {
		String[] error = new String[2];
		error[0] = "error";
		error[1] = Util.unicodeToGBK(errorString);
		String a = JSONArray.fromArray(error).toString();
		log.debug("json error string:" + a);
		return a;
	}

	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// 1: ��ͨ��Ƭ 4: Ƶ������ 5: hexun ����
		int fragType = ((Integer) _form.get("fragtype")).intValue();
		if (fragType == 1) {
			return saveFrag(mapping, form, request, response, auth);
		} else if (fragType == 2) {
			return saveChannelFrag(mapping, form, request, response, auth);
		} else if (fragType == 3) {
			return saveSohuFrag(mapping, form, request, response, auth);
		} else {
			errors.add("errors.fragedit", new ActionError("errors.fragedit.invalidfragtype"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * ������ͨ��Ƭ
	 */
	private ActionForward saveFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		try {
			// �����Ƭ�洢��ַ
			int eid = ((Integer) _form.get("entityid")).intValue();
			int tid = ((Integer) _form.get("templateid")).intValue();
			int tfmapid = ((Integer) _form.get("tfmapid")).intValue();

			boolean hasPerm = hasPermision(auth, eid, tid, tfmapid);

			String storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, true);

			String ip = request.getRemoteAddr();

			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String content = (String) _form.get("content");

			//
			// content = HtmlTagFilter.tag2Lower( content );
		//	content = Util.GBKToUnicode(content);
			if (content.equals(""))
				content = " ";

			// ������Ƭ
			String userName = auth.getUserName();
			FragLog.writeLog(storepath, userName, request.getRemoteAddr());

			// ������Ƭ����
			ClientFile.getInstance().write(content, storepath);

			// ȡ������Ƭ�ļ��б�
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			return mapping.findForward("fragedit");
		} catch (Exception e) {
			log.error("fragedit -- savefrag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.savefrag", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * ����Ƶ��������Ƭ
	 */
	private ActionForward saveChannelFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		try {
			// �����Ƭ�洢��ַ
			int channelId = ((Integer) _form.get("channelid")).intValue();
			int entityId = ((Integer) _form.get("entityid")).intValue();
			String fragName = (String) _form.get("fragname");

			int hpId = ItemUtil.getHpEntity(entityId).getId();

			CommonFrag cf = null;
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			for (int i = 0; list != null && i < list.size(); i++) {
				CommonFrag temp = (CommonFrag) list.get(i);
				if (temp.getChannel() == channelId && temp.getName().equals(fragName)) {
					cf = temp;
					break;
				}
			}
			if (cf == null) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.nofrag", fragName));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			List authPerms = getAuthPerms(auth);
			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(channelId), Channel.class);
			boolean hasPerm = hasPerm(authPerms, cf.getPermission() + "|" + channel.getName());
			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� List authPerms = getAuthPerms( auth );
			 * boolean hasPerm = false; // ��ƬȨ�� = ����Ƶ��+����Ƶ��/���� Channel channel =
			 * (Channel)ItemManager.getInstance().get(new
			 * Integer(channelId),Channel.class); String permissions =
			 * channel.getName()+"|"+String.valueOf(cf.getPermission()); for(int
			 * i=0; authPerms!=null && i<authPerms.size(); i++) { if(
			 * permissions.indexOf( (String)authPerms.get(i) ) !=-1 ) { hasPerm =
			 * true; break; } }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String content = (String) _form.get("content");
			// content = HtmlTagFilter.tag2Lower( content );
		//	content = Util.GBKToUnicode(content);
			if (content.equals(""))
				content = " ";

			String storepath = PageManager.getFStorePath(channelId, hpId, fragName, true);
			// ������Ƭ
			String userName = auth.getUserName();
			FragLog.writeLog(storepath, userName, request.getRemoteAddr());

			// ������Ƭ����
			ClientFile.getInstance().write(content, storepath);

			// ȡ������Ƭ�ļ��б�
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			request.setAttribute("com.hexun.cms.client.action.frageditaction.commonfrag", cf);

			return mapping.findForward("commonfragedit");
		} catch (Exception e) {
			log.error("fragedit -- savecommonfrag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.savecommonfrag", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * ����hexun������Ƭ
	 */
	private ActionForward saveSohuFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		try {
			// �����Ƭ�洢��ַ
			String fragName = (String) _form.get("fragname");

			CommonFrag cf = null;
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			for (int i = 0; list != null && i < list.size(); i++) {
				CommonFrag temp = (CommonFrag) list.get(i);
				if (temp.getChannel() == -1 && temp.getName().equals(fragName)) {
					cf = temp;
					break;
				}
			}
			if (cf == null) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.nofrag"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			List authPerms = getAuthPerms(auth);
			boolean hasPerm = hasPerm(authPerms, cf.getPermission());
			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� List authPerms = getAuthPerms( auth );
			 * boolean hasPerm = false; // ��ƬȨ�� = Ƶ��/���� String permissions =
			 * String.valueOf(cf.getPermission()); for(int i=0; authPerms!=null &&
			 * i<authPerms.size(); i++) { if( permissions.indexOf(
			 * (String)authPerms.get(i) ) !=-1 ) { hasPerm = true; break; } }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String content = (String) _form.get("content");
			// content = HtmlTagFilter.tag2Lower( content );
			//content = Util.GBKToUnicode(content);
			if (content.equals(""))
				content = " ";

			// д��Ƭ�ļ���������
			String storepath = PageManager.getFStorePath(fragName);
			// ������Ƭ
			String userName = auth.getUserName();
			FragLog.writeLog(storepath, userName, request.getRemoteAddr());
			ClientFile.getInstance().write(content, storepath);

			// ��ÿ��Ƶ���ڱ���һ����Ƭ����
			List channelList = ItemManager.getInstance().getList(Channel.class);
			for (int i = 0; channelList != null && i < channelList.size(); i++) {
				Channel c = (Channel) channelList.get(i);
				String spath = PageManager.getFStorePath(c.getId(), fragName, true);
				ClientFile.getInstance().write(content, spath);
			}

			// ȡ������Ƭ�ļ��б�
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			request.setAttribute("com.hexun.cms.client.action.frageditaction.commonfrag", cf);

			return mapping.findForward("sohufragedit");
		} catch (Exception e) {
			log.error("saveSohuFrag exception -- ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.savesohufrag", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	private String[] parseLogFragName(String fragname) {
		int idx1 = fragname.indexOf(".");
		int idx2 = fragname.lastIndexOf(".inc");
		String tmp = fragname.substring(idx1 + 1, idx2);
		return tmp.split("_");
	}

	private List getLogFragInfo(String storepath) {
		// ȡ��Ƭ��־�ļ�
		String[] fragLogNames = FragLog.getFragLogNames(storepath);

		List list = new ArrayList();
		for (int i = 0; fragLogNames != null && i < fragLogNames.length; i++) {
			String[] info = parseLogFragName(fragLogNames[i]);
			String[] info1 = new String[info.length + 1];

			System.arraycopy(info, 0, info1, 0, info.length);
			info1[2] = FragLog.getFragLogDir(storepath) + fragLogNames[i];
			list.add(info1);
		}
		return list;
	}

	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private List getLogFragMoreInfo(String storepath) {
		// ���ض�����
		int count = 10;
		// ȡ��Ƭ��־�ļ�
		String[] fragLogNames = FragLog.getFragLogNames(storepath);
		List list = new ArrayList();
		if (fragLogNames != null) {
			int tempCount = fragLogNames.length - count;
			for (int i = fragLogNames.length - 1; i > 0 && i > tempCount; i--) {
				String[] info = parseLogFragName(fragLogNames[i]);
				Map aa = new HashMap();
				aa.put("date", formater.format(new java.util.Date(Long.parseLong(info[0]))));
				aa.put("editor", info[1]);
				String path = FragLog.getFragLogDir(storepath) + fragLogNames[i];
				aa.put("path", path);

				list.add(aa);
			}
		}
		return list;
	}

	/**
	 * ��ʾ��Ƭ����
	 */
	public ActionForward rollbackview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		try {
			int fragType = ((Integer) _form.get("fragtype")).intValue();

			String logFragPath = request.getParameter("logfragpath");

			if (logFragPath == null) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.rollback.invalidlogfragpath",
						logFragPath));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			// ȡ��Ƭ�ļ�����
			String content = null;
			try {
				content = ClientFile.getInstance().read(logFragPath);
			} catch (Exception e) {
				log.error("rollbackview --> get content exception, maybe " + logFragPath + " is not exist. ",
						e);
			}
			if (content == null) {
				_form.set("content", "");
			} else {
				content = Util.unicodeToGBK(content);
				_form.set("content", content);
			}

			String logFragName = logFragPath.substring(logFragPath.lastIndexOf(File.separator));
			String[] info = parseLogFragName(logFragName);
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo", info);

			if (fragType == 1) {
				return mapping.findForward("fragrollback");
			} else if (fragType == 2) {
				return mapping.findForward("commonfragrollback");
			} else if (fragType == 3) {
				return mapping.findForward("sohufragrollback");
			} else {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.invalidfragtype"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
		} catch (Exception e) {
			log.error("rollbackview --> exception. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.rollback"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * �����Ƭ
	 */
	public ActionForward ads(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		int fragType = ((Integer) _form.get("fragtype")).intValue();
		if (fragType == 1) {
			return adsFrag(mapping, form, request, response, auth);
		} else if (fragType == 2) {
			return adsCommonFrag(mapping, form, request, response, auth);
		} else {
			return mapping.findForward("failure");
		}
	}

	private ActionForward adsFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		try {
			String username = auth.getUserName();
			String password = auth.getPassword();

			// entityId, tfmapId ���ڻ�ȡ�洢��ַ
			int entityId = ((Integer) _form.get("entityid")).intValue();
			int tfmapId = ((Integer) _form.get("tfmapid")).intValue();

			// bindentityId, templateId �����������
			// String bindentityId = request.getParameter("bindentityid");
			int templateId = ((Integer) _form.get("templateid")).intValue();

			EntityItem entity = (EntityItem) ItemManager.getInstance().get(new Integer(entityId),
					EntityItem.class);
			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(entity.getChannel()),
					Channel.class);
			String channelName = channel.getName();
			String channelDesc = channel.getDesc();

			String storepath = PageManager.getFStorePath(entity, tfmapId, true);

			Template template = (Template) ItemManager.getInstance().get(new Integer(templateId),
					Template.class);
			TFMap tfmap = null;
			Iterator itr = template.getTFMaps().iterator();
			while (itr.hasNext()) {
				TFMap tmp = (TFMap) itr.next();
				if (tmp.getId() == tfmapId) {
					tfmap = tmp;
					break;
				}
			}

			// У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ��
			List authPerms = getAuthPerms(auth);
			boolean hasPerm = hasPerm(authPerms, tfmap.getPermission());

			/*
			 * boolean hasPerm = false; if( tfmap.getType()==2 ) { String
			 * fragPerm = String.valueOf(tfmap.getPermission()); String[]
			 * fragPerms = fragPerm.split("|");
			 * 
			 * for(int i=0; i<authPerms.size(); i++) { if( fragPerm.indexOf(
			 * (String)authPerms.get(i) )!=-1 ) { hasPerm = true; break; } } }
			 * else { hasPerm = false; }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String fragName = tfmap.getName();
			String fragDesc = tfmap.getDesc();

			String testurl = PageManager.FTWebPath(template, false) + "?ENTITYID=" + entityId;
			StringBuffer adtree = new StringBuffer();

			/*
			 * List list = ItemUtil.getEntityParents( entity ); if( list!=null ) {
			 * if( list.size()>0 ) { int size = list.size() - 1; for(int i=size;
			 * i>=0; i--) { EntityItem eItem = (EntityItem)list.get(i);
			 * adtree.append( eItem.getId()+"|"+eItem.getDesc() ); if( i>0 )
			 * adtree.append("~"); } } else { adtree.append(
			 * entity.getId()+"|"+entity.getDesc().replaceAll("<[^>]*>","") ); } }
			 */
			String[] categorys = entity.getCategory().split(";");
			for (int i = 0; i < categorys.length; i++) {
				if (i > 0)
					adtree.append("~");
				EntityItem eItem = (EntityItem) ItemManager.getInstance().get(new Integer(categorys[i]),
						EntityItem.class);
				adtree.append(eItem.getId() + "|" + eItem.getDesc().replaceAll("<[^>]*>", ""));
			}

			// get HPEntity
			int adsEntityId = entity.getId();
			if (entity instanceof News) {
				EntityItem hpEntity = ItemUtil.getHpEntity(entity.getId());
				adsEntityId = hpEntity.getId();
			}

			StringBuffer url = new StringBuffer();
			url.append("http://ads.cms.hexun.com/retrieveCms.jsp");
			url.append("?user=" + URLEncoder.encode(username, "ISO_8859_1"));
			url.append("&password=" + URLEncoder.encode(password, "ISO_8859_1"));
			url.append("&domainname="
					+ URLEncoder.encode(channelName + "|" + Util.GBKToUnicode(channelDesc), "ISO_8859_1"));
			url.append("&entityid=" + String.valueOf(adsEntityId));
			url.append("&fragid=" + String.valueOf(tfmapId));
			url.append("&fragname=" + URLEncoder.encode(fragName, "ISO_8859_1"));
			url.append("&fragdescription=" + URLEncoder.encode(Util.GBKToUnicode(fragDesc), "ISO_8859_1"));
			url.append("&inc=" + URLEncoder.encode(storepath, "ISO_8859_1"));
			url.append("&testurl=" + URLEncoder.encode(testurl, "ISO_8859_1"));
			url.append("&adtree=" + URLEncoder.encode(Util.GBKToUnicode(adtree.toString()), "ISO_8859_1"));

			log.debug(" ==== ADS adtree ==== " + Util.GBKToUnicode(adtree.toString()));
			log.debug(" ==== ADS ==== " + url.toString());
			log.debug(" ==== ADS decode ==== " + URLDecoder.decode(url.toString()));
			response.sendRedirect(url.toString());

			return null;
		} catch (Exception e) {
			log.error("adsFrag exception -- ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.adsfrag", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	private ActionForward adsCommonFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;

		try {
			String username = auth.getUserName();
			String password = auth.getPassword();

			int channelId = ((Integer) _form.get("entityid")).intValue();
			String fragname = (String) _form.get("fragname");
			int templateId = ((Integer) _form.get("templateid")).intValue();
			String entityId = request.getParameter("entityid");

			EntityItem entity = (EntityItem) ItemManager.getInstance().get(new Integer(entityId),
					EntityItem.class);
			EntityItem hpe = ItemUtil.getHpEntity(entity);

			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(channelId), Channel.class);
			String channelName = channel.getName();
			String channelDesc = channel.getDesc();

			Template template = (Template) ItemManager.getInstance().get(new Integer(templateId),
					Template.class);

			CommonFrag cf = null;
			List list = ItemManager.getInstance().getList(CommonFrag.class);
			for (int i = 0; list != null && i < list.size(); i++) {
				CommonFrag temp = (CommonFrag) list.get(i);
				if (temp.getChannel() == channelId && temp.getName().equals(fragname)) {
					cf = temp;
					break;
				}
			}

			List authPerms = getAuthPerms(auth);
			boolean hasPerm = hasPerm(authPerms, cf.getPermission());
			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� boolean hasPerm = false; // ��ƬȨ�� = Ƶ��/����
			 * String permissions = String.valueOf(cf.getPermission()); for(int
			 * i=0; authPerms!=null && i<authPerms.size(); i++) { if(
			 * permissions.indexOf( (String)authPerms.get(i) ) !=-1 ) { hasPerm =
			 * true; break; } }
			 */
			if (!hasPerm) {
				errors.add("errors.fragedit", new ActionError("errors.fragedit.noperm"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			String fragName = fragname;
			String fragDesc = cf.getDesc();
			String storepath = PageManager.getFStorePath(channelId, hpe.getId(), fragname, true);

			String testurl = PageManager.FTWebPath(template, false) + "?ENTITYID=" + entityId;

			StringBuffer url = new StringBuffer();
			url.append("http://ads.cms.hexun.com/retrieveCms.jsp");
			url.append("?user=" + URLEncoder.encode(username, "ISO_8859_1"));
			url.append("&password=" + URLEncoder.encode(password, "ISO_8859_1"));
			url.append("&domainname=" + URLEncoder.encode(channelName + "|" + channelDesc, "ISO_8859_1"));
			url.append("&entityid=" + String.valueOf(entity.getId()));
			url.append("&fragid=");
			url.append("&fragname=" + URLEncoder.encode(fragName, "ISO_8859_1"));
			url.append("&fragdescription=" + URLEncoder.encode(fragDesc, "ISO_8859_1"));
			url.append("&inc=" + URLEncoder.encode(storepath, "ISO_8859_1"));
			url.append("&testurl=" + URLEncoder.encode(testurl, "ISO_8859_1"));
			url.append("&adtree=");

			log.debug(" ==== ADS ==== " + url.toString());
			response.sendRedirect(url.toString());
			return null;
		} catch (Exception e) {
			log.error("adsCommonFrag exception -- ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.adsfrag", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	private boolean hasPerm(List authPerms, String fragPerm) {
		// ��Ƭ���Բ���Ȩ��,���ǵ�ǰƵ��Ȩ��
		if (fragPerm == null || fragPerm.equals(""))
			return false;

		// ��Ƭ��������Ȩ��, ���������:
		// 1: �ƹ㲿��Ȩ�޻�CS
		// 2: ����Ƶ���ڵ�ǰƵ����Ȩ�޸����Ƭ����, for example: chinaren
		boolean ret = false;
		String[] fragPerms = fragPerm.split("\\|");

		authflag: for (int i = 0; i < authPerms.size(); i++) {
			for (int j = 0; fragPerms != null && j < fragPerms.length; j++) {
				if (fragPerms[j].equalsIgnoreCase(((String) authPerms.get(i)))) {
					ret = true;
					break authflag;
				}
			}
		}
		return ret;
	}

	/**
	 * Ԥ����Ƭ����
	 */
	public ActionForward prefrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		try {
			// �����Ƭ�洢��ַ
			int eid = ((Integer) _form.get("entityid")).intValue();
			int tid = ((Integer) _form.get("templateid")).intValue();
			int tfmapid = ((Integer) _form.get("tfmapid")).intValue();

			EntityItem eItem = (EntityItem) ItemManager.getInstance().get(new Integer(eid), EntityItem.class);
			Template titem = (Template) ItemManager.getInstance().get(new Integer(tid), Template.class);
			Iterator itr = titem.getTFMaps().iterator();
			TFMap tfmap = null;
			while (itr.hasNext()) {
				TFMap tfm = (TFMap) itr.next();
				if (tfm.getId() == tfmapid) {
					tfmap = tfm;
					break;
				}
			}
			String storepath = PageManager.getFStorePath((EntityItem) ItemManager.getInstance().get(
					new Integer(eid), EntityItem.class), tfmapid, true);

			/*
			 * // У�鵱ǰ�û��Ƿ����޸������Ƭ��Ȩ�� List channels = auth.getChannelList();
			 * boolean hasPerm = false; if( tfmap.getType()==1 ) { Channel
			 * channel = (Channel)ItemManager.getInstance().get(new
			 * Integer(eItem.getChannel()), Channel.class); String permissions =
			 * String.valueOf(tfmap.getPermission())+"|"+channel.getName();
			 * for(int i=0; i<channels.size(); i++) { if(
			 * permissions.indexOf(((Channel)channels.get(i)).getName())!=-1 ) {
			 * hasPerm = true; break; } } } else if( tfmap.getType()==2 ) {
			 * hasPerm = false; } else if ( tfmap.getType()==3 ) { hasPerm =
			 * true; }
			 * 
			 * if( !hasPerm ) { errors.add("errors.fragedit", new
			 * ActionError("errors.fragedit.noperm")); saveErrors(request,
			 * errors); return mapping.findForward("failure"); }
			 */

			// ��ȡ��Ƭ����
			String content = null;
			try {
				content = ClientFile.getInstance().read(storepath);
			} catch (Exception e) {
				log.error("get content exception, maybe " + storepath + " is not exist. " + e.toString());
			}
			if (content == null) {
				// get pre frag content
				String root = Configuration.getInstance().get("cms4.client.file.root");
				String frag = Configuration.getInstance().get("cms4.client.file.frag.page");
				String fragPath = root + frag + "/template" + tid + "/" + tfmap.getName();
				content = LocalFile.read(fragPath);
				_form.set("prefrag", "yes");
			} else {
				_form.set("prefrag", "no");
			}
			if (content == null) {
				_form.set("content", "");
			} else {
				//content = Util.unicodeToGBK(content);
				_form.set("content", content);
			}
			request.setAttribute("com.hexun.cms.client.action.frageditaction.logfraginfo",
					getLogFragInfo(storepath));
			return mapping.findForward("fragedit");
		} catch (Exception e) {
			log.error("fragedit -- save frag error. ", e);
			errors.add("errors.fragedit", new ActionError("errors.fragedit.fragview", e.toString()));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
	}

	/**
	 * �ϴ�������Ƭ
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward uploadFrag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm) form;
		String newsPictures = "";

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		try {
			String contentType = request.getContentType();
			if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
				log.error("uploadFrag  contentType is [" + contentType + "]");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("contentType.error"));
				return mapping.findForward("failure");
			}

			DynaActionForm fm = (DynaActionForm) form;
			MultipartRequestHandler mrh = (MultipartRequestHandler) fm.getMultipartRequestHandler();
			Hashtable ht = (Hashtable) mrh.getFileElements();
			Enumeration keys = null;

			/*
			 * ��ʼ�ϴ��ļ�,�ϴ�����:
			 * ֻ��������һ���ļ�,���ұ�����zip��β(Ŀǰͨ�����ַ���������ȷ���ϴ����ļ�����zip�ļ�),�����ļ����ݲ���Ϊ��
			 */
			if (ht.size() != 1) {
				log.error("uploadFrag  file size is [" + ht.size() + "]");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("file.size.error"));
				return mapping.findForward("failure");
			}

			keys = ht.keys();
			String keyStr = (String) keys.nextElement();
			FormFile temp = (FormFile) ht.get(keyStr);
			String fileName = temp.getFileName(); // �ϴ����ļ���

			if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
				log.error("uploadFrag  file type is [" + fileName + "],and we only accept zip file");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("file.type.error"));
				return mapping.findForward("failure");
			}

			if (temp.getFileSize() == 0) {
				log.error("uploadFrag  file size  is [" + temp.getFileSize() + "]");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("file.size.error"));
				return mapping.findForward("failure");
			}

			/* �ļ��ϴ�ͨ�������֤,��ʼȡ����ʵ����� */
			InputStream fileIn = temp.getInputStream();
			ZipExpress zip = null;
			try {
				zip = new ZipExpress(TMP_DIR, fileIn);
				FragZipProcessor zpr = new FragZipProcessor(zip, "http://img.pusuo.net");
				String content = zpr.getContent();
				if (content == null) {
					throw new RuntimeException("frag cnontent is null");
				} else {
					// �þ�����������Ƭ�����滻ԭ��������
					_form.set("content", content);
				}

				// 1: ��ͨ��Ƭ 4: Ƶ������ 5: hexun ����
				int fragType = ((Integer) _form.get("fragtype")).intValue();
				if (fragType == 1) {
					return saveFrag(mapping, form, request, response, auth);
				} else if (fragType == 2) {
					return saveChannelFrag(mapping, form, request, response, auth);
				} else if (fragType == 3) {
					return saveSohuFrag(mapping, form, request, response, auth);
				} else {
					errors.add("errors.fragedit", new ActionError("errors.fragedit.invalidfragtype"));
					saveErrors(request, errors);
					return mapping.findForward("failure");
				}
			} finally {
				zip.clearDestDir();
				log.info("clear destDir");
			}
		} catch (Exception e) {
			log.error("NewsAction.upload error. " + e.toString());
		}

		return mapping.findForward("failure");

	}

}
