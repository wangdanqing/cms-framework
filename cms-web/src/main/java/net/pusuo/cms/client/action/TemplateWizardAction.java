package net.pusuo.cms.client.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;

import com.hexun.cms.Configuration;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.UePermissionMap;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.LocalClientFile;
import com.hexun.cms.client.util.TemplateLog;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.util.Util;

public class TemplateWizardAction extends BaseAction {

	private static final Log log = LogFactory.getLog(ItemAction.class);
	private static final String tempurl = "http://cms.pusuo.net:8080";

	public String retrievePermission() {
		return "template";
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		String id = ((String) dForm.get("id")).trim();
		String itemType = ((String) dForm.get("itemtype")).trim();
		String fileType = ((String) dForm.get("filetype")).trim();

		Item item = null;

		try {
			if (null != id && !("-1".equals(id))) {
				item = ItemManager.getInstance().get(new Integer(id),
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				errors.add("errors.templatewizard", new ActionError(
						"errors.templatewizard.itemnotfound"));
			} else {
				// try to read template file(.shtml) content
				String path = null;
				if (fileType.equals("HTML")) {
					path = PageManager.FTPath(item, true);
				} else if (fileType.equals("JSP")) {
					path = PageManager.FTPath(item, false);
				}
				request.setAttribute("template_shtml_path", PageManager
						.FTWebPath(item, true));
				request.setAttribute("template_jsp_path", PageManager
						.FTWebPath(item, false));
				if (path != null) {
					try {
						String content = LocalFile.read(path);
						if (content != null) {
							//dForm.set("file", content);
							dForm.set("file",new String(content.getBytes(),"UTF-8"));
						}
					} catch (Exception ioe) {
						log.warn("view load template file[" + path
								+ "] error .");
					}
				}
				// denghua add ģ������б�
				request
						.setAttribute(
								"com.hexun.cms.client.action.templatewizardaction.logtemplateinfo",
								getTemplateLog(path));
				// denghua add end
			}
		} catch (Exception e) {
			errors.add("errors.templatewizard", new ActionError(
					"errors.templatewizard.view", e.toString()));
			log.error("TemplateWizardAction view error . ", e);
		}
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("view");
		}
		return ret;
	}

	/**
	 * ���ģ���ļ���·��,�ҵ������ļ����ļ����б�.<br/> ģ���ļ���ı��淽ʽ��: ģ����_ʱ��_�û���.��׺ ���ص�list������������.
	 * ���鰴�ļ������
	 * 
	 * @param filepath
	 * @return
	 */
	private List getTemplateLog(String filepath) {
		String[] files = TemplateLog.getTemplateLogNames(filepath);
		List result = new ArrayList();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String filename = files[i];
				// ȥ����չ��
				String fn = filename.substring(0, filename.indexOf("."));
				String[] a = fn.split("_");
				String[] b = new String[a.length + 1];
				System.arraycopy(a, 0, b, 0, a.length);
				b[a.length] = TemplateLog.getTemplateLogDir(filepath)
						+ filename;
				result.add(b);
			}
		}
		return result;
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		String id = ((String) dForm.get("id")).trim();
		String itemType = ((String) dForm.get("itemtype")).trim();
		String fileType = ((String) dForm.get("filetype")).trim();
		Item item = null;

		try {
			if (null != id && !("-1".equals(id))) {
				item = ItemManager.getInstance().get(new Integer(id),
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				errors.add("kdln,errors.templatewizard", new ActionError(
						"errors.templatewizard.itemnotfound"));
			} else {
				// try to write template file(.shtml) content
				String path = null;
				if (fileType.equals("HTML")) {
					path = PageManager.FTPath(item, true);
				} else if (fileType.equals("JSP")) {
					path = PageManager.FTPath(item, false);
				}
				request.setAttribute("template_shtml_path", PageManager
						.FTWebPath(item, true));
				request.setAttribute("template_jsp_path", PageManager
						.FTWebPath(item, false));

				// denghua add �����¼
				// �Ȱ���һ�ε����ݶ������������һ�μ�¼��Ȼ���ٱ��汾�ε�����
				Authentication auth = null;
				try {
					auth = AuthenticationFactory.getAuthentication(request,
							response);
				} catch (UnauthenticatedException ue) {
					errors.add("auth.failure", new ActionError("auth.failure"));
					saveErrors(request, errors);
					return mapping.findForward("failure");
				}
				System.out.println("writelog: path=" + path);
				TemplateLog.writeLog(path, auth.getUserName());
				// denghua add end

				if (path != null) {
					String content = (String) dForm.get("file");
					if (content != null) {
						if ( !LocalFile.write(new String(content.replaceAll(tempurl,"")).getBytes("UTF-8"),path) ) {
							throw new Exception("write file [" + path
									+ "] failure.");
						}
					}
				}
			}
		} catch (Exception e) {
			errors.add("errors.templatewizard", new ActionError(
					"errors.templatewizard.save", e.toString()));
			log.error("TemplateWizardAction save error . ", e);
		}

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("view");
		}
		return ret;
	}

	public ActionForward setup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		String id = ((String) dForm.get("id")).trim();
		String itemType = ((String) dForm.get("itemtype")).trim();
		Item item = null;

		final String bshowTAG = "<!-- show code -->";
		final String eshowTAG = "<!-- end show code -->";

		try {
			if (null != id && !("-1".equals(id))) {
				item = ItemManager.getInstance().get(new Integer(id),
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				errors.add("errors.templatewizard", new ActionError(
						"errors.templatewizard.itemnotfound"));
			} else {
				// try to read template file(.shtml) content
				String path = PageManager.FTPath(item, true);
				request.setAttribute("template_shtml_path", PageManager
						.FTWebPath(item, true));
				request.setAttribute("template_jsp_path", PageManager
						.FTWebPath(item, false));
				if (path != null) {
					try {
						String content = LocalFile.read(path);
						if (content != null) {
							StringBuffer sb = new StringBuffer();
							int index1 = content.indexOf(bshowTAG);
							int index2 = content.indexOf(eshowTAG);
							if (index1 >= 0 && index2 >= 0) {
								sb.append(content.substring(0, index1));
								sb.append(content.substring(index2
										+ eshowTAG.length()));
								content = sb.toString();
							}

							// denghua add.
							// ��Ҫ��content���д���,��ue�Ĵ������ת��ת��span��,�ٵ�ҳ����д���
							content = ConversionUeTag(content);
							// denghua add end.

							request.setAttribute("file", new String(content
									.getBytes(), "UTF-8"));
							request.setAttribute("id", id);
							request.setAttribute("itemtype", itemType);
						} else {
							errors
									.add(
											"errors.templatewizard",
											new ActionError(
													"errors.templatewizard.templatenotfind",
													path));
						}
					} catch (Exception ioe) {
						errors.add("errors.templatewizard", new ActionError(
								"errors.templatewizard.loadtemlatefile", ioe
										.toString()));
						log.error("setup load template file[" + path
								+ "] error .", ioe);
					}
				}
			}
		} catch (Exception e) {
			errors.add("errors.templatewizard", new ActionError(
					"errors.templatewizard.setup", e.toString()));
			log.error("TemplateWizardAction setup error . ", e);
		}
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("setup");
		}
		return ret;
	}

	private void save_frag(String tpath) {
		try {
			String root = Configuration.getInstance().get(
					"cms4.client.file.root");
			// String templatePage =
			// Configuration.getInstance().get("cms4.client.file.template.page");
			// String htmlPath =
			// Configuration.getInstance().get("cms4.client.file.template.page.html");
			String frag = Configuration.getInstance().get(
					"cms4.client.file.frag.page");
			int idx1 = tpath.lastIndexOf("/");
			int idx2 = tpath.lastIndexOf(".");
			String tname = tpath.substring(idx1 + 1, idx2);
			// String templatePath = rootPath + templatePage + htmlPath + "/"
			// +tname;

			String content = LocalFile.read(tpath);
			if (content == null || content.trim().equals(""))
				return;

			Parser parser = new Parser();
			parser.setInputHTML(new String(content.getBytes("ISO8859_1")));

			NodeFilter nf = new TagNameFilter("span");
			NodeList nl = parser.extractAllNodesThatMatch(nf);
			// String reg = "(<SPAN id=cms4_block.+?>)(.*?)</SPAN>";
			// Pattern p = Pattern.compile(reg, Pattern.DOTALL);
			// Matcher m = p.matcher(copycontent);
			// while (m.find()) {
			for (int i = 0; i < nl.size(); i++) {
				Span s = (Span) nl.elementAt(i);

				String span = s.toHtml();
				if (span.indexOf("frag_quotetype=\"1\"") == -1
						|| span.indexOf("frag_type=\"1\"") == -1) {
					// ���˷Ǿ�̬��Ƭ, �Ǳ�ʵ�������Ƭ
					continue;
				}

				String fragName = s.getAttribute("frag_name");
				if (StringUtils.isEmpty(fragName)) {
					continue;
				}

				String cont = s.getChildrenHTML();
				String fragPath = root + frag + "/" + tname + "/" + fragName;
				LocalFile.write(cont.replaceAll(tempurl, ""),
						fragPath);
			}
		} catch (Exception e) {
			log.error("TemplateWizardAction save_frag error . ", e);
		}
	}

	public static final String XHTML_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

	public static final String XHTML_HTML = "<html xmlns=\"http://www.w3.org/1999/xhtml\">";

	public ActionForward save_setup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		String id = ((String) dForm.get("id")).trim();
		String itemType = ((String) dForm.get("itemtype")).trim();
		Item item = null;

		final String bsetupTAG = "<!-- setup code -->";
		final String esetupTAG = "<!-- end setup code -->";
		final String bshowTAG = "<!-- show code -->";
		try {
			if (null != id && !("-1".equals(id))) {
				item = ItemManager.getInstance().get(new Integer(id),
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				errors.add("errors.templatewizard", new ActionError(
						"errors.templatewizard.itemnotfound"));
			} else {
				// try to write template file(.shtml) content
				String path = PageManager.FTPath(item, true);
				request.setAttribute("template_shtml_path", PageManager
						.FTWebPath(item, true));
				request.setAttribute("template_jsp_path", PageManager
						.FTWebPath(item, false));
				// String path =
				// "/usr/local/resin/webapps/cms/cms_page/template/mark.shtml";
				if (path != null) {
					String content = (String) dForm.get("file");
					if (content != null) {
						StringBuffer sb = new StringBuffer();
						int index1 = content.indexOf(bsetupTAG);
						int index2 = content.indexOf(esetupTAG);
						int index3 = content.indexOf(bshowTAG);
						if (index1 >= 0 && index2 >= 0) {

							// denghua modify
							// ���isxhtml��true,������xhtml��ģ�棬��Ҫ����doctype��ͷ
							if ("true".equalsIgnoreCase(request
									.getParameter("isxhtml"))) {
								sb.append(XHTML_DOC_TYPE).append(XHTML_HTML);
							} else
								sb.append("<HTML>");
							// denghua modify end;

							sb.append(content.substring(0, index1));
							sb.append(content.substring(index2
									+ esetupTAG.length()));
							if (index3 < 0) {
								sb.append("<!-- show code -->");
								sb
										.append("<!--#include virtual=\"TEMPLATEFOOT.inc\"-->");
								sb.append("<!-- end show code -->");
							}
							sb.append("</HTML>");
							content = sb.toString();
						}
						if (!LocalFile.write(new String(content.replaceAll(
								tempurl, "").getBytes("UTF-8")),
								path)) {
							throw new Exception("write file [" + path
									+ "] failure.");
						}
						// dForm.set("file",content);
						// redirect to setup page
						request.setAttribute("file", content);
						request.setAttribute("id", id);
						request.setAttribute("itemtype", itemType);
					}
					// auto save init frag
					save_frag(path);
				}
			}
		} catch (Exception e) {
			errors.add("errors.templatewizard", new ActionError(
					"errors.templatewizard.save_setup", e.toString()));
			log.error("TemplateWizardAction save error . ", e);
		}

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("setup");
		}
		return ret;
	}

	public ActionForward save_h2j(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;

		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		String id = ((String) dForm.get("id")).trim();
		String itemType = ((String) dForm.get("itemtype")).trim();
		Item item = null;


		try {
			if (null != id && !("-1".equals(id))) {
				item = ItemManager.getInstance().get(new Integer(id),
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				log.error("save_h2j --> id is null.");
				errors.add("errors.templatewizard", new ActionError(
						"errors.templatewizard.itemnotfound"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}

			// try to write template file(.jsp) content
			String path = PageManager.FTPath(item, false);
			request.setAttribute("template_shtml_path", PageManager.FTWebPath(
					item, true));
			request.setAttribute("template_jsp_path", PageManager.FTWebPath(
					item, false));
			if (path == null) {
				log.error("save_h2j --> path is null.");
				errors.add("errors.templatewizard",
						new ActionError("errors.templatewizard.invalidpath",
								String.valueOf(id)));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
			String content = (String) dForm.get("file");
			if (content != null) {
				StringBuffer sb = new StringBuffer();
				// restore special cms placehold to shtml tag
				content = content.replaceAll("\\{\\[\\(", "<");
				content = content.replaceAll("\\)\\]\\}", ">");

				// String temp = content.toUpperCase();
				// int styleIndex = content.indexOf("<STYLE");
				// if ( styleIndex==-1 )
				// {
				// log.error("save_h2j --> no style tag.");
				// errors.add("errors.templatewizard", new
				// ActionError("errors.templatewizard.nostyletag"));
				// saveErrors(request,errors);
				// return mapping.findForward("failure");
				// }
				sb
						.append("<%@ page contentType=\"text/html;charset=GBK\" language=\"java\" %>\n");

				// denghua modify
				// ��Ҫ�ж��ǲ���xhtml,�����xhtml,���ϲ�ͬ��ͷ
				if ("true".equalsIgnoreCase(request.getParameter("isxhtml"))) {
					sb.append(XHTML_DOC_TYPE).append(XHTML_HTML);
				} else
					sb.append("<html>");

				sb.append("<head>\n");
				// base ��ǩ����׼ֻ�з�����ҳ��head�м����Ч��
				sb.append("<%@ include file=\"TEMPLATEHEAD.inc\" %>\n");
				// denghua modify end

				sb
						.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=GBK\" />\n");

				sb.append("<cms4:sfrag type=\"1\" name=\"metafrag\" desc=\"");
				sb.append(Util.unicodeToGBK("metafrag"));
				sb
						.append("\" quotetype=\"1\" permission view=\"<%=view%>\"></cms4:sfrag>\n");

				sb.append("<title>");
				sb.append("<%=TITLE%>");
				sb.append("</title>\n");
				sb.append("<cms4:sfrag type=\"1\" name=\"style\" desc=\"");
				sb.append(Util.unicodeToGBK("��ʽ��"));
				sb
						.append("\" quotetype=\"1\" permission view=\"<%=view%>\"></cms4:sfrag>\n");
				sb
						.append("<cms4:sfrag type=\"1\" name=\"javascript_frag\" desc=\"");
				sb.append(Util.unicodeToGBK("javascript��Ƭ"));
				sb
						.append("\" quotetype=\"1\" permission view=\"<%=view%>\"></cms4:sfrag>\n");
				sb.append("</head>\n");

				// ֱ��ɾ������ע��TAG����Ĵ���
				// if ( index1>=0 || index2>=0 )
				// {
				// if ( index1<0 )
				// {
				// sb.append(content.substring(styleIndex,index2));
				// } else if ( index2<0 ) {
				// sb.append(content.substring(styleIndex,index1));
				// } else {
				// if ( index1>index2 )
				// {
				// sb.append(content.substring(styleIndex,index2));
				// } else {
				// sb.append(content.substring(styleIndex,index1));
				// }
				// }
				// }

				// huadeng modify ֱ��ɾ���ע��TAG�Ĵ��룬������ȷ�����Ƕ�ע�ͼ�Ĵ������ɾ���
				content = clearNoteTag(content);

				// ��</body>��ǩɾ���������Ĵ��룬��ö�����body�м�
				Pattern p = Pattern
						.compile("</body>", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(content.toString());
				if (m.find()) {
					content = content.substring(0, m.start());
				}

				// ɾ����ע�ʹ������Ҫ�����ݸ��ӵ�sb����
				// ��head��ǩ��������д��붼�ŵ�sb����
				p = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
				m = p.matcher(content);
				if (m.find()) {
					int a = m.end();
					sb.append(content.substring(a));
				}
				// huadeng modify end

				Template template = (Template) item;
				// HOMEPAGE & SUBJECT TEMPLATE, AUTO ADD ADFRAG FOR ADS
				if (template.getType() == ItemInfo.HOMEPAGE_TYPE) {
					sb
							.append("<cms4:adfrag type=\"2\" name=\"systemadfrag\" desc=\"");
					sb.append(Util.unicodeToGBK("���ù����Ƭ"));
					sb
							.append("\" quotetype=\"1\" quotefrag  permission=\"cs\" view=\"<%=view%>\"></cms4:adfrag>\n");
				}
				if (template.getType() == ItemInfo.SUBJECT_TYPE) {
					sb
							.append("<cms4:adfrag type=\"2\" name=\"systemadfragsub\" desc=\"");
					sb.append(Util.unicodeToGBK("����ר������Ƭ"));
					sb
							.append("\" quotetype=\"1\" quotefrag  permission=\"cs\" view=\"<%=view%>\"></cms4:adfrag>\n");
				}

				// NEWS TEMPLATE, AUTO ADD BIGAD FOR ADS
				if (template.getType() == ItemInfo.NEWS_TYPE) {
					sb
							.append("<cms4:adfrag type=\"2\" name=\"bigadfrag\" desc=\"");
					sb.append(Util.unicodeToGBK("���л������Ƭ"));
					sb
							.append("\" quotetype=\"1\" quotefrag permission=\"cs\" view=\"<%=view%>\"></cms4:adfrag>\n");
				}
				sb.append("<center><cms4:footer view=\"<%=view%>\"/></center>");
				sb.append("</body></html>");
				content = sb.toString();
				content = content.replaceAll("CMS4:BEAN", "cms4:bean");
				content = content.replaceAll("CMS4:NEWSRELATIVE",
						"cms4:newsrelative");
				content = content.replaceAll("CMS4:SFRAG", "cms4:sfrag");
				content = content.replaceAll("CMS4:DFRAG", "cms4:dfrag");
				content = content.replaceAll("CMS4:ADFRAG", "cms4:adfrag");

				// ȥ������ո�
				content = content.replaceAll("&nbsp;<cms4:newsList",
						"<cms4:newsList");
				content = content.replaceAll("&nbsp;</cms4:dfrag>",
						"</cms4:dfrag>");

				if (!LocalFile.write(new String(content.replaceAll(
						tempurl, "").getBytes("UTF-8")), path)) {
					errors.add("errors.templatewizard", new ActionError(
							"errors.templatewizard.writejspfile"));
					saveErrors(request, errors);
					return mapping.findForward("failure");
				}

				dForm.set("file", content);
			}
		} catch (Exception e) {
			log.error("save_h2j --> " + e.toString());
			errors.add("errors.templatewizard", new ActionError(
					"errors.templatewizard.save_h2j", e.toString()));
		}

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("view");
		}
		return ret;
	}

	/**
	 * ��UE�Ĵ�����д���
	 * 
	 * @param content
	 * @return
	 */
	public static String ConversionUeTag(String content) {
		String pattern = "<!--SAS:MT:([^:]+?):ST-->(.*?)(<(\\w+)[\\s>].*?)<!--SAS:MT:\\1:END-->";
		Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
		Matcher m = p.matcher(content);
		StringBuffer cc = new StringBuffer();
		int i = 1;
		while (m.find()) {
			StringBuffer temp = new StringBuffer();
			String permission = UePermissionMap.findPermission(m.group(1));
			m
					.appendReplacement(
							cc,
							temp
									.append(
											"<SPAN id=cms4_block onmouseover=showborder(this); frag_type=\"1\" frag_name=auto_static_")
									.append(i)
									.append(" frag_desc=auto_sfrag_")
									.append(i)
									.append(
											" frag_quotetype=\"1\" frag_permission=")
									.append(permission).append(
											" frag_quotefrag block_type=\"")
									.append(m.group(4).toUpperCase()).append(
											"\">").append(m.group(2)).append(
											m.group(3)).append("</SPAN>")
									.toString());
			i++;
		}
		m.appendTail(cc);
		content = cc.toString();

		// �ٶ�β���д���
		// content = content.replaceAll("<!--SAS:MT:([^:]+?):END-->",
		// "</SPAN>");

		// �����ݽ����滻
		// �滻��ǩ�����ǿյı�ǩ
		m = Pattern.compile("<SPAN\\s+id=cms4_block\\s+[^>]*>\\s*</SPAN>",
				Pattern.MULTILINE + Pattern.DOTALL).matcher(content);
		cc = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(cc, "");
		}
		m.appendTail(cc);
		content = cc.toString();

		// content.replaceAll("<SPAN\\s+id=cms4_block\\s+[^>]*>\\s*</SPAN>",
		// "");

		return content;

	}

	/**
	 * ���ע�ʹ���
	 * 
	 * @param content
	 * @return
	 */
	public static String clearNoteTag(String content) {
		String setup_code = "<!--\\s*setup\\s+code\\s*-->.+?<!--\\s*end\\s+setup\\s+code\\s*-->";
		Pattern p = Pattern.compile(setup_code, Pattern.DOTALL
				+ Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		StringBuffer cc = new StringBuffer();
		boolean finda = m.find();
		while (finda) {
			m.appendReplacement(cc, "");
			finda = m.find();
		}
		m.appendTail(cc);
		content = cc.toString();

		String show_code = "<!--\\s*show\\s+code\\s*-->.+?<!--\\s*end\\s+show\\s+code\\s*-->";
		p = Pattern.compile(show_code, Pattern.DOTALL
				+ Pattern.CASE_INSENSITIVE);
		m = p.matcher(content);
		cc = new StringBuffer();
		finda = m.find();
		while (finda) {
			m.appendReplacement(cc, "");
			finda = m.find();
		}
		m.appendTail(cc);
		content = cc.toString();
		return content;
	}

	public ActionForward loadTemplateLog(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String file = request.getParameter("file");
		try {
			response.resetBuffer();
			if (file != null) {
				try {
					String a = new String(
							com.hexun.cms.client.util.LocalClientFile.read(file)
									.getBytes(), "UTF-8");
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().println(a);
				} catch (Exception e) {
					response.getWriter().println("error|ClientFile����ʼ������");

				}
			} else {
				response.getWriter().println("error|�ļ�Ϊ��");

			}
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ģ�����ݻ���ѡ������
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward TemplateRecoverSelect(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		String _tid = (String) dForm.get("id");
		Integer tid = StringUtils.isNotEmpty(_tid)
				&& StringUtils.isNumeric(_tid) ? new Integer(Integer
				.parseInt(_tid)) : new Integer(-1);
		String defaultFragPath = root + frag + "/" + "template" + tid + "/";
		try {
			String[] a=LocalClientFile.getFileList(defaultFragPath);
			if(a.length>0){
				request.setAttribute("file_exist", "file_exist");
			}
		} catch (Exception e) {
		}
		return mapping.findForward("select");
	}

	String root = Configuration.getInstance().get("cms4.client.file.root");

	String frag = Configuration.getInstance().get("cms4.client.file.frag.page");

	public ActionForward TemplateRecover(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		List result = new ArrayList();
		BaseForm dForm = (BaseForm) form;
		Integer eid = (Integer) dForm.get("eid");
		String _tid = (String) dForm.get("id");

		Integer tid = StringUtils.isNotEmpty(_tid)
				&& StringUtils.isNumeric(_tid) ? new Integer(Integer
				.parseInt(_tid)) : new Integer(-1);
		String oldTemplate = "";
		if (eid != null && eid.intValue() > -1) {
			Item a = ItemManager.getInstance().get(eid, EntityItem.class);
			if (a != null && a instanceof EntityItem) {
				EntityItem homepage = (EntityItem) a;
				oldTemplate = homepage.getTemplate().trim();
			}
		} else {
			errors.add("errors.item.id.required", new ActionError(
					"errors.item.id.required"));
		}

		if (StringUtils.isNotEmpty(oldTemplate)) {
			if (oldTemplate.indexOf(tid.toString()) > -1) {
				String[] temps = oldTemplate.split(";");
				for (int i = 0; i < temps.length; i++) {
					String ttid = temps[i].split(",")[0];
					if (tid.toString().equalsIgnoreCase(ttid)) {
						Template template = (Template) ItemManager
								.getInstance().get(tid, Template.class);
						Iterator tfIter = template.getTFMaps().iterator();
						while (tfIter.hasNext()) {
							TFMap tfmap = (TFMap) tfIter.next();
							if (tfmap.getType() == 2 || tfmap.getType() == 3)
								continue;
							int tfmapid = tfmap.getId();
							String fragName = tfmap.getName();
							int quoteType = tfmap.getQuotetype();
							if (quoteType == 5)
								continue;
							try {
								String fragpath = PageManager.getFStorePath(
										(EntityItem) ItemManager.getInstance()
												.get(eid, EntityItem.class),
										tfmapid, true);
								log.info("read fragpath=" + fragpath);

								String a = ClientFile.getInstance().read(
										fragpath);

								String defaultFragPath = root + frag + "/"
										+ "template" + tid + "/" + fragName;
								log.info("write default fragpath="
										+ defaultFragPath);
								if (StringUtils.isNotEmpty(a)) {
									if (LocalClientFile.write(a,
											defaultFragPath)) {
										result.add("frag " + defaultFragPath
												+ "  write OK!");
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					}
				}
			} else {
				errors.add("error.template.notexist", new ActionError(
						"error.template.notexist"));
			}
		} else {
			errors.add("error.template.notexist", new ActionError(
					"error.template.notexist"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		request.setAttribute("result_list", result);
		return mapping.findForward("select");
	}
}
