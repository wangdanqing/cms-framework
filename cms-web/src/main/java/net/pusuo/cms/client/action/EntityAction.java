package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.util.TemplateUtil;
import com.hexun.cms.client.compile.CompileTaskFactory;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.History;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.TimeUtils;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.search.entry.Entry;
import com.hexun.cms.search.util.SearchUtils;

public class EntityAction extends BaseAction {

	private static final Log LOG = LogFactory.getLog(EntityAction.class);

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		String itemType = ((String) dForm.get("itemtype")).trim();
		Integer pid = (Integer) dForm.get("pid");

		Integer subtype = null;
		if (Integer.parseInt(itemType) == ItemInfo.SUBJECT_TYPE) {
			subtype = (Integer) dForm.get("subtype");
		}

		Item item = null;
		try {
			if (_id == -1) {// �½�
				item = ItemInfo.getItemByType(itemType);
				ItemUtil.setItemValues(dForm, item);
			} else {// �޸�
				item = ItemManager.getInstance().get(id,
						ItemInfo.getItemClass(itemType));
			}
			if (item == null) {
				errors.add("errors.item.notfound", new ActionError(
						"errors.item.notfound"));
			} else {
				ItemUtil.putItemValues(dForm, item);
				if (((EntityItem) item).getTime() == null) {
					dForm.set("time", new java.sql.Timestamp(System
							.currentTimeMillis()));
				}

				// add by xulin 2005.09.14 for show editor name
				String editorName = auth.getUser().getDesc();
				if (_id == -1) {
					editorName = auth.getUser().getDesc();
				} else {
					User user = (User) ItemManager.getInstance().get(
							new Integer(((EntityItem) item).getEditor()),
							ItemInfo.getItemClass(ItemInfo.USER_TYPE));
					editorName = user.getDesc();
				}
				dForm.set("editorName", editorName);

				// added by wangzhigang 2005.03.21
				// for more than priority for news
				if (item instanceof News) {
					int priority = ((EntityItem) item).getPriority();
					for (int i = 50; i <= 90;) {
						if (priority == (i + 2)) {
							dForm.set("priority", new Integer(i));
							dForm.set("addprio", "on");
							break;
						}
						i += 10;
					}

					// customize media list for each channel (added by
					// huaiwenyuan)
					Map mediaMap = ItemUtil.getMediaMap(auth);
					List mediaList = Collections.list(Collections
							.enumeration(mediaMap.values()));
					dForm.set("List4", ItemUtil.ListToLVB(mediaList));

					// decode entity param (added by huaiwenyuan)
					if (auth.hasChannel(EntityParamUtil.CHANNEL_NAME_BUSINESS)) {
						String param = (String) PropertyUtils.getProperty(item,
								"param");
						if (param != null && param.trim().length() != 0) {
							dForm
									.set(
											"stockOrg",
											EntityParamUtil
													.getEntityParamItem(
															param,
															EntityParamUtil.ENTITY_PARAM_STOCK_ORG));
							dForm
									.set(
											"stockCode",
											EntityParamUtil
													.getEntityParamItem(
															param,
															EntityParamUtil.ENTITY_PARAM_STOCK_CODE));
						}
					}
				}

				// �½����⴦��
				if (_id == -1) {

					// ֻΪsubjectʹ��
					if (Integer.parseInt(itemType) == ItemInfo.SUBJECT_TYPE) {
						dForm.set("subtype", subtype);
					}

					if (pid.intValue() > -1) {// ��ʵ�����½�������

						dForm.set("pname", getName(pid.intValue()));
						dForm.set("pid", pid);

						EntityItem pItem = (EntityItem) ItemManager
								.getInstance().get(pid, EntityItem.class);
						if (pItem != null) {
							int pChannel = pItem.getChannel();
							Channel channel = (Channel) ItemManager
									.getInstance().get(new Integer(pChannel),
											Channel.class);
							if (channel != null) {
								dForm.set("channel", new Integer(pChannel));
								if (Integer.parseInt(itemType) == ItemInfo.SUBJECT_TYPE) {
									String subjectDefautTempl = (String) channel
											.getProperties().get(
													"default_subject_templ");
									if (subjectDefautTempl != null) {
										dForm.set("template",
												subjectDefautTempl);
									}
								}

								if (Integer.parseInt(itemType) == ItemInfo.NEWS_TYPE) {
									String newsDefautTempl = (String) channel
											.getProperties().get(
													"default_news_templ");
									if (newsDefautTempl != null) {
										dForm.set("template", newsDefautTempl);
									}
								}
							}
						}
					}
					List navigater = new ArrayList();
					request.setAttribute("navigater", navigater);
				} else {// �޸�

					int ppid = ((EntityItem) item).getPid();
					if (ppid > -1) {// ���Ҹ�����
						dForm.set("pname", getName(ppid));
						dForm.set("pid", new Integer(ppid));
					}
					// add visit history
					History.addRecord(request, response, auth.getUserName(),
							_id);
					request.setAttribute("navigater", nav((EntityItem) item));
				}
			}
		} catch (Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("EntityAction view error . ", e);
		}
		// Report any errors we have discovered to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			// Go so far, forward to next page
			ret = mapping.findForward("item");
		}
		return ret;
	}


	// remove entity item
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		String itemType = ((String) dForm.get("itemtype")).trim();

		Item item = null;

		try {
			if (_id != -1) {
				item = ItemManager.getInstance().get(id,
						ItemInfo.getItemClass(itemType));
				if (item == null) {
					errors.add("errors.item.notfound", new ActionError(
							"errors.item.notfound"));
				} else {
					ItemManager.getInstance().remove(item);
				}
			} else {
				errors.add("errors.item.id.required", new ActionError(
						"errors.item.id.required"));
			}
		} catch (Exception e) {
			errors.add("errors.item.remove", new ActionError(
					"errors.item.remove"));
			LOG.error("ItemAction remove error . ", e);
		}

		// Report any errors we have discovered to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("success");
		}

		return ret;
	}

	// save entity item
	protected Object[] saveProcesser(BaseForm form, String ext) {
		
		
		Object[] obj = new Object[2];

		ActionErrors errors = new ActionErrors();

		BaseForm dForm = form;

		Item item = null;

		try {
			Integer id = (Integer) dForm.get("id");
			int _id = id.intValue();

			String itemType = ((String) dForm.get("itemtype")).trim();
			int entityType = -1;
			if (itemType != null && !itemType.equals("")) {
				entityType = Integer.parseInt(itemType);
			}

			long start =TimeUtils.currentTimeMillis();
			
			String name = ((String) dForm.get("name")).trim();
			String pname = ((String) dForm.get("pname")).trim();
			Integer channel = (Integer) dForm.get("channel");
			Integer ppid = (Integer) dForm.get("pid");

			if (entityType == ItemInfo.NEWS_TYPE) {
				Integer priority = (Integer) dForm.get("priority");
				String addprio = (String) dForm.get("addprio");
				if (addprio != null && addprio.equalsIgnoreCase("on")) {
					priority = new Integer(priority.intValue() + 2);
					dForm.set("priority", priority);
				}
			}

			if (entityType == ItemInfo.SUBJECT_TYPE) {
				String shortName = (String) dForm.get("shortname");
				if (shortName != null && shortName.trim().length() > 0) {
					dForm.set("shortname", shortName.toLowerCase());
				}
			}

			if (name != null && !name.equals("")) {
				dForm.set("name", name);
			}
			
			long p1=TimeUtils.currentTimeMillis();
			
			// �жϸ��������
			if (entityType != ItemInfo.HOMEPAGE_TYPE) {
				if (pname != null && !pname.equals("")) {
					int pid = getId(pname);
					if (pid > 0 && pid != _id) {// pname����
						dForm.set("pid", new Integer(pid));
					} else {
						errors.add("errors.item.pnamenotexist",
								new ActionError("errors.item.pnamenotexist"));// ����������Ѿ�����
						obj[0] = item;
						obj[1] = errors;
						return obj;
					}
				} else if (pname == null || (pname != null && pname.equals(""))) {// pnameû��
					if (ppid.intValue() > -1) {
						dForm.set("pid", ppid);
					} else {
						LOG.error("update item :" + item.getId() + " " + ppid);
						errors.add("errors.item.save", new ActionError(
								"errors.item.save"));// ���ֲ�������?
						obj[0] = item;
						obj[1] = errors;
						return obj;
					}
				}
			} else if (entityType == ItemInfo.HOMEPAGE_TYPE) {// homepage
				dForm.set("pid", new Integer(-1));
			}

			long p2=TimeUtils.currentTimeMillis();
			
			if (_id > 0) {// update
				item = ItemManager.getInstance().get(id, ItemInfo.getItemClass(itemType));
				
				long p3=TimeUtils.currentTimeMillis();
				if (entityType == ItemInfo.SUBJECT_TYPE || entityType==ItemInfo.HOMEPAGE_TYPE && _id > 0) {
					LOG.debug("EntityAction-saveProcesser: to get. (id=" + id + ")(cost=" + (p3 - p2) + ")");
				}				
				
				// for comment
				EntityItem eItem = (EntityItem) item;
//commented by xulin 2008.02.28
/*
				boolean exe = false;
				if (exe	&& (eItem.getType() == ItemInfo.SUBJECT_TYPE 
						|| eItem.getType() == ItemInfo.NEWS_TYPE)) {
					String url = "http://admin.comment.hexun.com/service/updateTopicStatus.action";
					Map params = new HashMap();
					params.put("id", eItem.getId() + "");
					params.put("category", eItem.getCategory());
					if (eItem.getType() == ItemInfo.SUBJECT_TYPE) {
						Subject subject = (Subject) item;
						params.put("gather", subject.getDiscussable() + "");
					}

					ClientHttpFile.wgetString(url, params);
				}
*/
				
				long p4=TimeUtils.currentTimeMillis();

				// refresh name cache
				if (name != null && !name.equals("")) {
					ItemManager.getInstance().refreshItemByName(name, EntityItem.class);
				}
								
				ItemManager.getInstance().refreshItemByName(eItem.getName(), EntityItem.class);
				
				long p5=TimeUtils.currentTimeMillis();
				if (entityType == ItemInfo.SUBJECT_TYPE || entityType==ItemInfo.HOMEPAGE_TYPE && _id > 0) {
					LOG.debug("EntityAction-saveProcesser: to refreshItemByName. (id=" 
							+ id + ")(cost=" + (p5 - p4) + ")");
				}
			}
			else {// new
				dForm.set("time", new java.sql.Timestamp(System.currentTimeMillis()));
				item = ItemInfo.getItemByType(itemType);
			}
			
			long p6=TimeUtils.currentTimeMillis();
			
			Integer truepid = (Integer) dForm.get("pid");

			// �ж�������Ƶ�����Ƿ���������������
			if ( truepid.intValue() > 0 && entityType != ItemInfo.NEWS_TYPE ) {
				errors = isExistP(channel, truepid);
				if (!errors.isEmpty()) {
					obj[0] = item;
					obj[1] = errors;
					return obj;
				}
			}
			
			long p7=TimeUtils.currentTimeMillis();
			if (entityType == ItemInfo.SUBJECT_TYPE || entityType==ItemInfo.HOMEPAGE_TYPE && _id > 0) {
				LOG.debug("EntityAction-saveProcesser: to isExistP. (id=" + id + ")(cost=" + (p7 - p6) + ")");
			}

			// ���ǰʵ����NEWS�������½��ģ����ҹؼ������ˣ������������
			/*
			if (_id == -1 && entityType == ItemInfo.NEWS_TYPE) {
				String keyword = ((String) dForm.get("keyword")).trim();
				if (!keyword.equals("")) {// ����
					int pid = getId(pname);
					EntityItem subject = (EntityItem) ItemManager.getInstance().get(new Integer(pid), EntityItem.class);
					String relativenews = ItemUtil.searchRelativenewsC(keyword, subject.getChannel(), 8);
					((News) item).setRelativenews(relativenews);
				}
			}
			*/
			// ���õ�ǰʵ�����չ��
			if (_id == -1) {
				((EntityItem) item).setExt(ext);
			}

			// added by xulin
			// ��ݸ���������Ƶ��,����ģ��
			// ���½�����ʱ
			if (_id == -1 && Integer.parseInt(itemType) == ItemInfo.NEWS_TYPE) {

				// ��ݸ���������Ƶ��,��������ʵ��Ƶ��
				int pid = getId(pname);
				Subject subject = (Subject) ItemManager.getInstance().get(
						new Integer(pid), Subject.class);
				dForm.set("channel", new Integer(subject.getChannel()));
				
				long p8=TimeUtils.currentTimeMillis();
				if (entityType == ItemInfo.SUBJECT_TYPE||entityType==ItemInfo.HOMEPAGE_TYPE) {
					LOG.debug("get subject,spend time:"+(p8-p7));
				}
				
				String editorTemplate = (String) dForm.get("template");
				int newsTemplate = TemplateUtil.processNewsTemplate(subject, editorTemplate);
				//added by shijinkui. Zutu template 
				if(Integer.parseInt(itemType) == News.SUBTYPE_ZUTU)
					newsTemplate = TemplateUtil.processZutuTemplate(subject, editorTemplate);
				
				long p9=TimeUtils.currentTimeMillis();
				if (entityType == ItemInfo.SUBJECT_TYPE||entityType==ItemInfo.HOMEPAGE_TYPE) {
					LOG.debug("proecssTemplate,spend time:"+(p9-p8));
					p7=p9;
				}
				
				dForm.set("template", "" + newsTemplate);
			}

			// map
			ItemUtil.setItemValues(dForm, item);
			
			long p10=TimeUtils.currentTimeMillis();
			
			// ����ʵ��
			try {
				item = ItemManager.getInstance().update(item);
			} 
			catch (Exception se) {
				LOG.error("update item:" + item.getId(), se);
				errors.add("errors.item.save", new ActionError(
						"errors.item.save"));
				obj[0] = item;
				obj[1] = errors;
				return obj;
			}
			
			long p11=TimeUtils.currentTimeMillis();
			if (entityType == ItemInfo.SUBJECT_TYPE || entityType==ItemInfo.HOMEPAGE_TYPE && _id > 0) {
				LOG.debug("EntityAction-saveProcesser: to update. (id=" + id + ")(cost=" + (p11 - p10) + ")");
			}
			
			// added by wangzhigang 2005.12.29
			// ����ҳר��idд���ļ�������
			// ��ϵͳѹ������,��insertһ������ʱ,��������ģ��
			if (_id == -1) {
				EntityItem qItem = (EntityItem) item;
				if (qItem.getType() == 2) {
					EntityItem qPItem = (EntityItem) ItemManager.getInstance()
							.get(new Integer(qItem.getPid()), EntityItem.class);
					while (qPItem != null && qPItem.getType() == 1) {
						putPaginationQueue(qPItem);
						qPItem = (EntityItem) ItemManager.getInstance().get(
								new Integer(qPItem.getPid()), EntityItem.class);
					}
				}
			}
			
			long p12=TimeUtils.currentTimeMillis();
			if (entityType == ItemInfo.SUBJECT_TYPE || entityType==ItemInfo.HOMEPAGE_TYPE && _id > 0) {
				LOG.debug("EntityAction-saveProcesser: to putPaginationQueue. (id=" 
						+ id + ")(cost=" + (p12 - p11) + ")");
			}
		} 
		catch (Exception e) {
			LOG.error("EntityAction save error . ", e);
		}
		
		obj[0] = item;
		obj[1] = errors;
		
		
		return obj;
	}



	protected int getId(String name) {
		// ���pname����pid
		if (name != null && !name.equals("")) {
			Item item = ItemManager.getInstance().getItemByName(name,
					EntityItem.class);
			if (item != null && item.getId() > 0) {
				return item.getId();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	protected String getName(int id) {
		// ���pid����pname
		if (id > -1) {
			Item item = ItemManager.getInstance().get(new Integer(id),
					EntityItem.class);
			if (item != null && item.getId() > 0) {
				return item.getName();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * �ж�������Ƶ�����Ƿ���������������
	 */
	protected ActionErrors isExistP(Integer channel, Integer PID) {

		ActionErrors errors = new ActionErrors();

		try {
			if (!ItemUtil.isPExistChannel(channel, PID)) {
				errors.add("errors.item.relative.pnotc", new ActionError(
						"errors.item.relative.pnotc"));
			}
		} catch (Exception e) {
			LOG.error("EntityAction.isExistP error . " + e.toString());
		}

		return errors;
	}

	/**
	 * �ж϶�����(�������)�Ƿ��ظ�
	 * 
	 */
	protected ActionErrors checkShortname(String shortName, Integer channel) {
		ActionErrors errors = new ActionErrors();
		try {
			List shortNameList = ItemUtil.getShortnames(shortName, channel);
			if (shortNameList != null && shortNameList.size() > 1) {
				errors.add("errors.item.shortname.repeat", new ActionError(
						"errors.item.shortname.repeat"));
				LOG
						.error("EntityAction.checkShortname repeat enitty shortname:"
								+ shortNameList);
			}
		} catch (Exception e) {
			LOG.error("EntityAction.checkShortname exception -- ", e);
		}
		return errors;
	}

	/*
	 * �ж��Ƿ�ʵ��������ȷ��λ����
	 */
	protected ActionErrors isCorrect(EntityItem item, Integer PID) {

		ActionErrors errors = new ActionErrors();

		try {

			if (item.getType() == ItemInfo.SUBJECT_TYPE) {// SUBJECT
				EntityItem pItem = (EntityItem) ItemManager.getInstance().get(
						PID, EntityItem.class);
				if (pItem.getType() == ItemInfo.NEWS_TYPE) {// SUBJECT ���ܽ���NEWS��
					errors.add("errors.item.relative.snotn", new ActionError(
							"errors.item.relative.snotn"));
					return errors;
				} else if (pItem.getType() == ItemInfo.PICTURE_TYPE) {// SUBJECT���ܽ���PICTURE��
					errors.add("errors.item.relative.snotp", new ActionError(
							"errors.item.relative.snotp"));
					return errors;
				}
			} else if (item.getType() == ItemInfo.NEWS_TYPE) {// NEWS
				EntityItem pItem = (EntityItem) ItemManager.getInstance().get(
						PID, EntityItem.class);
				if (pItem.getType() == ItemInfo.NEWS_TYPE) {// NEWS ���ܽ���NEWS��
					errors.add("errors.item.relative.nnotn", new ActionError(
							"errors.item.relative.nnotn"));
					return errors;
				} else if (pItem.getType() == ItemInfo.PICTURE_TYPE) {// NEWS���ܽ���PICTURE��
					errors.add("errors.item.relative.nnotp", new ActionError(
							"errors.item.relative.nnotp"));
					return errors;
				} else if (pItem.getType() == ItemInfo.HOMEPAGE_TYPE) {// NEWS���ܽ���HOMEPAGE��
					errors.add("errors.item.relative.nnoth", new ActionError(
							"errors.item.relative.nnoth"));
					return errors;
				}
			} else if (item.getType() == ItemInfo.PICTURE_TYPE) {// PICTURE
				EntityItem pItem = (EntityItem) ItemManager.getInstance().get(
						PID, EntityItem.class);
				if (pItem.getType() == ItemInfo.PICTURE_TYPE) {// PICTURE
					// ���ܽ���PICTURE��
					errors.add("errors.item.relative.pnotp", new ActionError(
							"errors.item.relative.pnotp"));
					return errors;
				}
			}
		} catch (Exception e) {
			LOG.error("EntityAction.isCorrect error . " + e.toString());
		}

		return errors;
	}

	/**
	 * ���pid��װcategory
	 */
	protected String category(Integer pid) {
		String categoryStr = "";
		if (pid.intValue() > 0) {
			EntityItem ceItem = (EntityItem) ItemManager.getInstance().get(pid,
					EntityItem.class);
			List items = ItemUtil.getEntityParents(ceItem);
			items.add(0, ceItem);
			Iterator litems = items.iterator();
			EntityItem nItem = null;
			while (litems.hasNext()) {
				nItem = (EntityItem) litems.next();
				categoryStr = nItem.getId() + Global.CMSSEP + categoryStr;
			}
		}

		return categoryStr;
	}

	/**
	 * ������Ϣ
	 */
	protected List nav(EntityItem item) {

		List navigater = new ArrayList();
		List navItem = ItemUtil.getEntityParents(item);
		navItem.add(0, item);
		EntityItem nItem = null;
		String action = "";
		Iterator iNavItem = navItem.iterator();

		while (iNavItem.hasNext()) {
			nItem = (EntityItem) iNavItem.next();
			if (nItem.getType() == ItemInfo.SUBJECT_TYPE) {
				action = "subject.do?method=view&id=" + nItem.getId();
			} else if (nItem.getType() == ItemInfo.NEWS_TYPE) {
				action = "news.do?method=view&id=" + nItem.getId();
			} else if (nItem.getType() == ItemInfo.PICTURE_TYPE) {
				action = "picture.do?method=view&id=" + nItem.getId();
			} else if (nItem.getType() == ItemInfo.HOMEPAGE_TYPE) {
				action = "homepage.do?method=view&id=" + nItem.getId();
			} else if (nItem.getType() == ItemInfo.VIDEO_TYPE) {
				action = "video.do?method=view&id=" + nItem.getId();
			}

			if (nItem == null) {
				LOG.info("nItem is null!");
			} else {
				navigater.add(0, new LabelValueBean(nItem.getDesc(), action));
			}
		}

		return navigater;
	}

	/**
	 * added by wangzhigang 2005.12.29 ����ҳר��idд���ļ�������
	 */
	private void putPaginationQueue(EntityItem entity) {
		if (entity.getType() != 1)
			return;

		try {
			if (!parseTemplate(entity))
				return;

			String queue = Configuration.getInstance().get(
					"cms4.client.pagination.queue");
			if (queue == null || queue.length() == 0) {
				LOG
						.warn("cms4.client.pagination.entity.queue is null, canot register pagination SUBJECT to pagination queue.");
				return;
			}

			synchronized (CompileTaskFactory.PQUEUE_LOCK) {
				String content = LocalFile.read(queue);
				if (content == null)
					content = "";
				if (content.equals("")) {
					content = "" + entity.getId();
					LocalFile.write(content, queue);
					// LOG.info("added "+entity.getId()+" to pagination
					// queue.");
				} else {
					if (content.indexOf(entity.getId() + "") >= 0) {
						return;
					} else {
						content += ";" + entity.getId();
						LocalFile.write(content, queue);
						// LOG.info("added "+entity.getId()+" to pagination
						// queue.");
					}
				}
			}
		} catch (Exception e) {
			LOG.error("registerPagination exception -- ", e);
			return;
		}
	}

	private boolean parseTemplate(EntityItem entity) throws Exception {
		String[] templates = entity.getTemplate().split(";");
		boolean ret = false;
		for (int i = 0; i < templates.length; i++) {
			int tid = Integer.parseInt(templates[i].split(",")[0]);
			Template template = (Template) ItemManager.getInstance().get(
					new Integer(tid), Template.class);
			if (template.getMpage() == 2) {
				ret = true;
				break;
			}
		}
		return ret;
	}
}
