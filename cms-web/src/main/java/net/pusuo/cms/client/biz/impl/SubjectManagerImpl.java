/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.SubjectManager;
import com.hexun.cms.client.biz.event.impl.DefaultSubjectEventListener;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.exception.RelationException;
import com.hexun.cms.client.biz.exception.ShortnameException;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.util.Util;

/**
 * @author Alfred.Yuan
 *
 */
public class SubjectManagerImpl extends CmsManagerImpl implements SubjectManager {
	
	private static final Log log = LogFactory.getLog(SubjectManagerImpl.class);

	public SubjectManagerImpl() {
		
		this.addListener(new DefaultSubjectEventListener());
	}

	public Subject getSubject(int subjectId) throws DaoException {
		
		if (subjectId < 0)
			return null;
		
		Subject result = null;
		try {
			Item item = ItemManager.getInstance().get(new Integer(subjectId), Subject.class);
			if (item != null)
				result = (Subject)item;
		}
		catch (Throwable t) {
			log.error("getSubject: get subject from server.");
			throw new DaoException();
		}
		
		return result;
	}

	public Subject addSubject(Subject subjectConfig, Map extend)
			throws PropertyException, ParentNameException, RelationException,
			UnauthenticatedException, DaoException, ShortnameException {
		
		if (subjectConfig == null)
			return null;
		
		// 设置id
		subjectConfig.setId(-1);
		
		// 设置编辑信息
		if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
			Authentication auth = (Authentication)extend.get(PROPERTY_NAME_AUTH);
			subjectConfig.setEditor(auth.getUserID());
		}
		
		// 设置时间
		if (subjectConfig.getTime() == null) {
			subjectConfig.setTime(new Timestamp(System.currentTimeMillis()));
		}
		
		// 设置扩展名
		subjectConfig.setExt(EXTEND_NAME_WEB_PAGE);
		
		return saveOrUpdateSubject(subjectConfig, extend);
	}
	
	public Subject updateSubject(Subject subjectConfig, Map extend)
			throws PropertyException, ParentNameException, RelationException,
			UnauthenticatedException, DaoException, ShortnameException{
		
		if (subjectConfig == null)
			return null;
		
		int subjectId = subjectConfig.getId();
		if (subjectId < 0) 
			throw new PropertyException();
		
		Subject subjectOld = getSubject(subjectId);
		if (subjectOld == null)
			throw new PropertyException();
		
		// 刷新nameCache中的内容(实际上是删除)
		String nameOld = subjectOld.getName();
		if (nameOld != null && nameOld.trim().length() > 0) {
			ItemManager.getInstance().refreshItemByName(nameOld, EntityItem.class);
		}
		String nameNew = subjectConfig.getName();
		if (nameNew != null && nameNew.trim().length() > 0) {
			ItemManager.getInstance().refreshItemByName(nameNew, EntityItem.class);
		}

		// 保留原先的编辑信息
		subjectConfig.setEditor(subjectOld.getEditor());
		
		return saveOrUpdateSubject(subjectConfig, extend);
	}

	public boolean deleteSubject(Subject subject) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private Subject saveOrUpdateSubject(Subject subjectConfig, Map extend) 
		throws PropertyException, ParentNameException, RelationException,
				UnauthenticatedException, DaoException, ShortnameException {
		
		boolean isSaveAction = subjectConfig.getId() < 0 ? true : false;
		
		// 更新专题的父对象:name优先,id次之
		Item parentItem = null;
		if (extend != null && extend.containsKey(PROPERTY_NAME_PNAME)) {
			String parentName = (String)extend.get(PROPERTY_NAME_PNAME);
			if (parentName != null && parentName.trim().length() > 0) {
				parentItem = ItemManager.getInstance().getItemByName(parentName, EntityItem.class);
				if (parentItem != null && parentItem.getId() > 0) {
					subjectConfig.setPid(parentItem.getId());
				}
			}
		}
		if (subjectConfig.getPid() < 0) {
			throw new ParentNameException();
		}
		
		// 验证父对象的有效性
		if (parentItem == null)
			parentItem = ItemManager.getInstance().get(new Integer(subjectConfig.getPid()), EntityItem.class);
		if (parentItem == null)
			throw new PropertyException();
		EntityItem parent = (EntityItem)parentItem;
		
		// 根据父对象设置频道
		subjectConfig.setChannel(parent.getChannel());
		
		// 标题不能为空
		String desc = subjectConfig.getDesc();
		if (desc == null || desc.trim().length() == 0)
			throw new PropertyException(); 
		
		// 将发布名称转化为小写(子专题的发布名称由系统自动生成)
		boolean isSubSubject = subjectConfig.getSubtype() == Subject.SUBTYPE_SUBSUBJECT ? true : false;
		if (!isSubSubject) {
			String shortname = subjectConfig.getShortname();
			if (shortname == null || shortname.trim().length() == 0)
				throw new PropertyException();
			shortname = shortname.toLowerCase();
			subjectConfig.setShortname(shortname);
		}
		
		// 同一个频道下,专题的发布名称不能重复(只针对新建专题,而且不是子专题的情况)
		if (isSaveAction && !isSubSubject 
				&&	hasRepeatedShortname(subjectConfig.getChannel(), subjectConfig.getShortname()))
			throw new ShortnameException();
		
		// 父对象和子对象之间的关系是否符合规范?
		if (!validateRelation(parent, subjectConfig))
			throw new RelationException();
		
		String oldTemplate = null;
		
		// 保留旧的状态值:(只针对新建专题)
		if (!isSaveAction) {
			Subject subjectOrigin = ManagerFacade.getSubjectManager().getSubject(subjectConfig.getId());
			if (subjectOrigin == null)
				throw new DaoException();
			subjectConfig.setOldpid(subjectOrigin.getPid());
			subjectConfig.setOldpriority(subjectOrigin.getPriority());
			subjectConfig.setOldstatus(subjectOrigin.getStatus());
			
			oldTemplate = subjectOrigin.getTemplate();
		}
		
		// 更新专题
		Subject subject = null;
		try {
			Item item = ItemManager.getInstance().update(subjectConfig);
			if (item != null)
				subject = (Subject)item;
			else 
				throw new DaoException();
		}
		catch (Throwable t) {
			log.error("saveOrUpdateSubject: update subject to server.");
			throw new DaoException();
		}
		
		// 处理模板
		processTemplate(subject, extend, oldTemplate);
		
		return subject;
	}
	
	private void processTemplate(Subject subject, Map extend, String oldTemplate) {
		
		// 新增加的模板id列表
		String newTemplate = subject.getTemplate();
		List addedIdList =  EntityParamUtil.getAddedTemplateIdList(oldTemplate,	newTemplate);
		if (addedIdList == null || addedIdList.size() == 0)
			return;
		
		// 更新模板的反向引用
		EntityParamUtil.updateTemplate4Reference(subject.getId(), oldTemplate, newTemplate);
		
		// 需要处理的模板列表
		if (extend == null || !extend.containsKey(PROPERTY_NAME_NEED_HANDLED_TEMPLATE))
			return;
		String needHandledTemplate = (String)extend.get(PROPERTY_NAME_NEED_HANDLED_TEMPLATE);
		
		// 碎片目的地(专题)
		int toId = subject.getId();
		
		for (int i = 0; i < addedIdList.size(); i++) {
			int templateId = ((Integer)addedIdList.get(i)).intValue();
			String templateIdParam = String.valueOf(templateId);
			
			// 碎片源(专题)
			String fromIdParam = getFromId(templateIdParam, needHandledTemplate);
			
			// none: 不使用模版
			// default: 表示使用默认模版
			// id: copy这个专题的对应模板的的碎片
			try {
				if ("none".equalsIgnoreCase(fromIdParam)) {
					continue;
				} else if ("default".equalsIgnoreCase(fromIdParam)) {
					copyTemplateFrag(toId, templateId, -1);
				} else if (StringUtils.isNotEmpty(fromIdParam) && StringUtils.isNumeric(fromIdParam)) {
					int fromId = Integer.parseInt(fromIdParam);
					copyTemplateFrag(toId, templateId, fromId);
				}
			}
			catch (Exception e) {
				continue;
			}
		}
	}
	
	private static String getFromId(String templateId, String needProcessTemplate) {
		
		String fromId = null;
		
		String[] mix_array = needProcessTemplate.split(Global.CMSSEP);
		for (int j = 0; j < mix_array.length; j++) {
			String[] temp = mix_array[j].split(Global.CMSCOMMA);
			if (templateId.equals(temp[0])) {
				fromId = temp[1];
				break;
			}
		}
		
		return fromId;
	}
	
	private static void copyTemplateFrag(int toId, int templateId, int fromId) throws Exception {
		
		Item toItem = ItemManager.getInstance().get(new Integer(toId), EntityItem.class);
		if (toItem == null)
			return;
		EntityItem toEntity = (EntityItem)toItem;
		
		EntityItem fromEntity = null;
		if (fromId > 0) {
			Item fromItem = ItemManager.getInstance().get(new Integer(fromId), EntityItem.class);
			if (fromItem == null)
				return;
			fromEntity = (EntityItem)fromItem;
		}
		
		String rootPath = Configuration.getInstance().get("cms4.client.file.root");
		String fragPath = Configuration.getInstance().get("cms4.client.file.frag.page");
		if (rootPath == null || rootPath.trim().length() == 0 || 
				fragPath == null || fragPath.trim().length() == 0) 
			return;
	
		Item itemTemplate = ItemManager.getInstance().get(new Integer(templateId), Template.class);
		if (itemTemplate == null)
			return;
		Template template = (Template)itemTemplate;
		
		Iterator iter = template.getTFMaps().iterator();
		while (iter.hasNext()) {
			TFMap tfmap = (TFMap)iter.next();
			if (tfmap.getType() == 2 || tfmap.getType() == 3) // 动态碎片和广告碎片
				continue;
			int tfmapId = tfmap.getId();
			String fragName = tfmap.getName();
			int quoteType = tfmap.getQuotetype();
			if (quoteType == 5) // hexun公用碎片
				continue;

			String fromFragPath = null;
			if (fromId < 0) {
				fromFragPath = rootPath + fragPath + File.separator 
					+ "template" + templateId + File.separator + fragName;
			} else {
				fromFragPath = PageManager.getFStorePath(fromEntity, tfmapId, true);
			}
			String fromFrag = ClientFile.getInstance().read(fromFragPath);

			fromFrag = new String(fromFrag.getBytes(),"UTF-8");//���׳�������
			//fromFrag = new String(fromFrag.getBytes(),"ISO-8859-1");//���׳�������
			//fromFrag = Util.unicodeToGBK(fromFrag);

	
			String toFragPath = PageManager.getFStorePath(toEntity, tfmapId, true);
			String toFrag = ClientFile.getInstance().read(toFragPath);
			System.out.println("fromFrag: " + fromFrag.length() + "|| toFrag:" + toFrag.length() + "||templateId: " + templateId);
			if (StringUtils.isNotEmpty(fromFrag) && StringUtils.isEmpty(toFrag)) {
				if (ClientFile.getInstance().write(fromFrag, toFragPath))
					log.debug("");
				else
					log.error("save frag: from (" + fromFrag + ") to (" + toFrag + ")");
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static boolean hasRepeatedShortname(int channelId, String shortname)	
		throws PropertyException, ShortnameException {
		
		if (channelId < 0 || shortname == null || shortname.trim().length() == 0)
			throw new PropertyException();
		
		String sql = "select item.id from "	+ EntityItem.class.getName()
			+ " as item where item.channel=? and item.type=1 and " 
			+ " item.shortname is not null and item.shortname=? ";
		
		List params = new ArrayList();
		params.add(String.valueOf(channelId));
		params.add(shortname);
		
		List repeatedList = ItemManager.getInstance().getList(sql, params, -1, -1);
		
		if (repeatedList != null && repeatedList.size() > 0)
			throw new ShortnameException();
		
		return false;
	}
	
	public static boolean validateRelation(EntityItem parent, Subject child) 
		throws PropertyException, RelationException {
		
		if (parent == null || child == null)
			throw new PropertyException();
		
		// 修改专题：父对象不能是自己
		if (child.getId() > 0 && child.getId() == parent.getId())
			throw new PropertyException();
		
		int parentType = parent.getType();
		int childType = child.getSubtype();
		
		// 兼容CMS3的专题类型
		if (childType == 0)
			return true;
		
		// 首页下只能建立栏目和专题
		if (parentType == ItemInfo.HOMEPAGE_TYPE) {
		if (childType == Subject.SUBTYPE_SUBCOLUMN || childType == Subject.SUBTYPE_SUBSUBJECT) {
				throw new RelationException();
			}
		}
		
		if (parentType == ItemInfo.SUBJECT_TYPE) {
			Subject parentSubject = (Subject)parent;
			parentType = parentSubject.getSubtype();

			// 兼容CMS3的专题类型
			if (parentType == 0)
				return true;

			// (子)专题下只能建立子专题
			if (parentType == Subject.SUBTYPE_SUBJECT || parentType == Subject.SUBTYPE_SUBSUBJECT) {
				if (childType != Subject.SUBTYPE_SUBSUBJECT) {
					throw new RelationException();
				}
			}
			
			// (子)栏目下只能建立子栏目和专题
			if (parentType == Subject.SUBTYPE_COLUMN || parentType == Subject.SUBTYPE_SUBCOLUMN) {
				if (childType != Subject.SUBTYPE_SUBJECT && childType != Subject.SUBTYPE_SUBCOLUMN) {
					throw new RelationException();
				}
			}
		}
		
		return true;
	}

}
