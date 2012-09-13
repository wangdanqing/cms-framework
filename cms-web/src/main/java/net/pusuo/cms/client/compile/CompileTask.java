package net.pusuo.cms.client.compile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.cache.CmsSortItem;
import com.hexun.cms.cache.Query;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.taglib.DFragTag;
import com.hexun.cms.client.taglib.FragTag;
import com.hexun.cms.client.util.EmbedGroovy;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.HtmlTagFilter;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Author;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.ExTFMap;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.util.Util;

public class CompileTask implements Task {
	
	
	private static final Log log = LogFactory.getLog(CompileTask.class);
	/**
	 * 有效的模板状态
	 */
	private static final int TEMPLATE_ENABLE_STATUS = 1;
	/**
	 * 无效的模板状态
	 */
	private static final int TEMPLATE_DISABLE_STATUS = 0;
	/**
	 * 分页模板的类型定义为2
	 */
	private static final int MUTIL_TEMPLATE = 2;

	private static String dot = null;
	private static String tempUrl = "http://cms.pusuo.net:8080";

	private QItem qitem;

	// default
	private static int MAX_SIZE_OF_LISTCACHE = 1000;

	public static String GROOVY_ROOT = null;

	public static String PAGING_GROOVY_SCRIPT = null;
	
	public static String PAGING_GROOVY_NAV_SCRIPT = null;
	
	static {
		String maxsize = Configuration.getInstance().get("cache.object.list.maxsize");
		GROOVY_ROOT = Configuration.getInstance().get("compile.grooveroot");
		PAGING_GROOVY_SCRIPT = Configuration.getInstance().get("compile.paging.script");
		PAGING_GROOVY_NAV_SCRIPT = Configuration.getInstance().get("compile.paging.nav.script");

		try {
			dot = new String("·".getBytes("ISO_8859_1"), "UTF-8");

			if (maxsize != null && maxsize.length() > 0) {
				MAX_SIZE_OF_LISTCACHE = Integer.parseInt(maxsize);
			}
		} catch (java.io.UnsupportedEncodingException e) {
			log.error("encoding error.");
		} catch (NumberFormatException e) {
			log.error("invalid cache.object.list.maxsize -- " + maxsize);
			MAX_SIZE_OF_LISTCACHE = 1000;
		}
	}

	public CompileTask(QItem _qitem) {
		qitem = _qitem;
	}

	public void run() {
		if (qitem instanceof TemplateQItem) {
			TemplateQItem tq = (TemplateQItem)qitem;
			//log.info("Compile template Entity:"+tq.getEntityid()+ " action:"+tq.getAction());
			/*
			 * action is 0 ==> entity status is 连接,备用
			 * action is 1 ==> entity status is 无效
			 */
			if (((TemplateQItem) qitem).getAction() == 0) {
				long t11 = ct();
				handleTemplate((TemplateQItem) qitem);
				long t12 = ct();
				LocalFile.write((t12-t11)+"\n" , "/tmp/log/log_handletemplate", true );
			} 
			else {
				deleteTemplate((TemplateQItem) qitem);
			}
		} 
		else if (qitem instanceof FragQItem) {
			FragQItem fq = (FragQItem)qitem;
			//log.info("Compile frag Entity:"+fq.getEntityid());
			handleFrag((FragQItem) qitem);
		} 
		else {
			log.warn("invalid compile qitem, qitem should be TemplateQItem or FragQItem");
		}
	}

	/**
	 * get entity list from cache get frag content by analyze tfmap properties
	 * generate frag file
	 */
	private void handleFrag(FragQItem fqitem) {
		try {
			//long t1 = ct();
			// 触发编译的实体
			EntityItem ueItem = (EntityItem) ItemManager.getInstance().get(
					new Integer(fqitem.getEntityid()), EntityItem.class);
			String idtree = ueItem.getCategory();
			if (idtree == null || idtree.equals("")) {
				log.warn("handleFrag --> entity " + ueItem.getId() + " category format is invalid. skiped...");
				return;
			}
			String[] ids = idtree.split(Global.CMSSEP);

			// 回溯实体树
			for (int i = ids.length - 1; i >= 0; i--) {
				EntityItem eItem = (EntityItem) ItemManager.getInstance().get(
						new Integer(ids[i]), EntityItem.class);
				
				if (eItem.getType() == ItemInfo.NEWS_TYPE){
					//新闻实体无动态碎片,不需要编译
					continue;
				}
				else if(eItem.getStatus() == EntityItem.DISABLE_STATUS){
					//agilewang add:无效的实体不需要编译
					//log.warn(ids[i]+"'s status is disable,so skip compile template");
					if (ueItem.getType() != ItemInfo.NEWS_TYPE) {
						//触发编译的实体是专题/首页类型,则只编译该专题/首页的碎片,不回溯父实体,就止停止编译
						break;
					}else{
						//略过无效实体,继续回溯父实体
						continue;
					}
				}				
				// 多模板字段校验
				if (eItem.getTemplate() == null	|| eItem.getTemplate().equals("")) {
					log.warn("hanleFrag --> entity " + eItem.getId() + " no template. skiped...");
					continue;
				}				
				String[] templates = eItem.getTemplate().split(Global.CMSSEP);
				// 开始处理多模板
				for (int k = 0; k < templates.length; k++) {
					if (templates[k].indexOf(Global.CMSCOMMA) == -1) {
						log.warn("handleFrag --> template format is invalid  "	+ templates[k] + "  skiped...");
						continue;
					}
					int templateid = Integer.parseInt(templates[k].split(Global.CMSCOMMA)[0]);
					Template template = (Template) ItemManager.getInstance()
							.get(new Integer(templateid), Template.class);
					Set tfmaps = template.getTFMaps();

					if (tfmaps == null) {
						log	.error("template has not tfmaps "+ template.getId());
						continue;
					}else if(template.getStatus()!=TEMPLATE_ENABLE_STATUS){
						//agilewang add:状态无效的模板不编译它的动态碎片
						continue;						
					}

					Iterator tfmapItr = tfmaps.iterator();
					while (tfmapItr.hasNext()) {
						TFMap tfmap = (TFMap) tfmapItr.next();
						// 过滤非动态碎片
						if (tfmap.getType() != 3)
							continue;

						// 注释: 提交新闻/专题/首页, 编译所有动态碎片
						// if( tfmap.getUt()!=ueItem.getType() ) continue;

						int uet = tfmap.getUet();
						int entityId = -1;
						switch (uet) {
						case 1: // 本实体生成
							entityId = eItem.getId();
							break;
						case 2: // 指定实体
							entityId = tfmap.getEntityid();
							break;
						case 3: // 子专题列表
							// "子实体列表"和"(外部)实体引用"三种类型的结合(Alfred.Yuan)
							String category = eItem.getCategory();
							if (tfmap.getEntityid() > 0) {
								int internalEntityId = tfmap.getEntityid();
								EntityItem internalEntity = (EntityItem)ItemManager.getInstance()
									.get(new Integer(internalEntityId), EntityItem.class);
								if (internalEntity == null) {
									break;
								}
								category = internalEntity.getCategory();
								//log.info("UET_TYPE_SUB_ENTITIES: real category is " + category);
							}
							int idx = idtree.indexOf(category);
							if (idx != -1) {
								int idx1 = idtree.indexOf(Global.CMSSEP, idx + category.length() + 1);
								if (idx1 > idx) {
									String subId = idtree.substring(idx + category.length() + 1, idx1);
									entityId = Integer.parseInt(subId);
								}
							}
							break;
						}
						if (entityId == -1) {
							// uet=3,但触发编译的新闻实体不在当前回溯实体的子实体列表中
							continue;
						}

						// 支持多ID动态碎片(Alfred.Yuan)
						List allIdList = EntityParamUtil.getAllIdList(tfmap.getOtherIds(), entityId);
						if (allIdList != null && allIdList.size() > 0) {
							boolean willCompiled = false;
							for (int q = 0; q < allIdList.size(); q++) {
								Integer allId = (Integer)allIdList.get(q);
								if (ueItem.getCategory().indexOf(allId.toString()) > -1) {
									willCompiled = true;
									break;
								}
							}
							// 所有的专题/栏目都不在触发编译的新闻的路径上
							if (!willCompiled) {
								continue;
							}
							log.info("CompileTask: (fragId=" + tfmap.getId() + ") (fragName=" + tfmap.getName() 
									+ ") (entityId=" + tfmap.getEntityid() + ") (otherIds=" + tfmap.getOtherIds());
						}
						else if (ueItem.getCategory().indexOf(String.valueOf(entityId)) == -1) {
							// 引用的实体不在触发编译的实体树上
							continue;
						}

						EntityItem ceItem = (EntityItem) ItemManager.getInstance().get(
								new Integer(entityId), EntityItem.class);
						if (ceItem == null) {
							log.error("ceItem is null ids[i]:" + ids[i]	+ " entityid:" + entityId);
							continue;
						}

						//long t2 = ct();
						generateFrag(ueItem, ceItem, tfmap);
						//long t3 = ct();
						//LocalFile.write( (t3-t2)+"\n" , log_frag, true );
					}
				}

				// 触发编译的实体是专题/首页类型,则只编译该专题/首页的碎片,不回溯父实体
				if (ueItem.getType() != ItemInfo.NEWS_TYPE) {
					break;
				}
				
				// 级联编译
				if (eItem.getType() == ItemInfo.SUBJECT_TYPE && eItem instanceof Subject) {
					Subject sourceSubject = (Subject)eItem;
					String weakReference = sourceSubject.getWeakReference();
					if (weakReference != null && weakReference.trim().length() > 0) {
						handleCascadedFrag(ueItem, sourceSubject);
					}
				}
			}
		} catch (Exception e) {
			log.error("handleFragRequest error: ", e);
		}
	}

	private int getExlistcount(int tfid, int entityid) {
		int ret = -1;
		List list = ItemManager.getInstance().getList(ExTFMap.class);
		for (int i = 0; list != null && i < list.size(); i++) {
			ExTFMap extfmap = (ExTFMap) list.get(i);
			if (extfmap.getTfid() == tfid && extfmap.getEntityid() == entityid) {
				ret = extfmap.getListcount();
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 级联编译.
	 * 说明:
	 * 1.通过"专题"->"外部引用的模板列表"->"应用模板的专题/首页列表",找到级联的碎片.
	 * 2.一个专题的内容可以被多个模板的动态碎片进行"外部引用";(一对多)
	 *   但是,这种模板不具有通用性，也就是说,应用该模板的专题最多只有一个.(一对一)
	 *   为了防止性能问题,只需要限制该专题被外部引用的次数就可以了.
	 * 3.如果是跨频道引用,那么需要修改存储规则:
	 *   举例:娱乐频道的专题"《天眼》_搜狐电影"(220961613)的一个模版引用了IT频道的
	 *       一个专题"通信"(203663245)的内容,那么原先的存储规则应该是-
	 *       /it/frag/203663245/48628_203663245.inc
	 *       由于apache(html)不允许跨频道目录引用碎片,所以新的存储规则应该是-
	 *       /it/frag/203663245/48628_203663245.inc
	 *       /yule/frag/203663245/48628_203663245.inc
	 *       也就是,在目标频道和源频道中同时保存该碎片的拷贝
	 * @param ueItem 触发级联编译的实体(新闻)
	 * @param source 引起级联编译的专题
	 * @author Alfred.Yuan
	 */
	private void handleCascadedFrag(EntityItem ueItem, Subject source) {
		
		long timeStart = System.currentTimeMillis();
		
		if (ueItem == null || source == null)
			return;
		
		// "外部引用"过该专题内容的模板参数
		String weakReference = source.getWeakReference(); 
		if (weakReference == null || weakReference.trim().length() == 0)
			return;
		weakReference = weakReference.trim();
		
		// 模板列表
		String[] templateIdList = weakReference.split(Global.CMSSEP); 
		if (templateIdList == null)
			return;
		// 如果跨树引用的模板超过一定数量,系统会出现性能问题
		int maxLength4Cascaded = 20;
		if (templateIdList.length > maxLength4Cascaded) {
			log.warn("Cascaded Compile：externally-referenced number(source=" + source.getId() 
					+ ") is too large(length=" + templateIdList.length + ")");
			return;
		}
		for (int i = 0; i < templateIdList.length; i++) {
			
			// 对模板id进行有效性分析
			String templateIdParam = templateIdList[i];
			if (templateIdParam != null && templateIdParam.trim().length() > 0) {
				templateIdParam = templateIdParam.trim();
				try {
					// 模板实体
					Integer templateId = new Integer(templateIdParam);
					Template template = (Template)ItemManager.getInstance().get(templateId, Template.class); 
					// 验证该模板的有效性:专题/首页类型,有效,不分页
					if (template != null && (template.getType() == ItemInfo.SUBJECT_TYPE
							|| template.getType() == ItemInfo.HOMEPAGE_TYPE) 
							&& template.getStatus() == 1 && template.getMpage() == 0) {
						
						// 应用过该模板的专题/首页参数
						String reference = template.getReference(); 
						if (reference == null || reference.trim().length() == 0)
							continue;
						reference = reference.trim();
						
						// 专题/首页列表
						String[] subjectIdList = reference.split(Global.CMSSEP); 
						if (subjectIdList == null)
							continue;
						// 重要:有"级联编译"特性的模板不具有通用性.
						// (模版可能会被误用为测试)
						if (subjectIdList.length > 3)
							continue;
						for (int j = 0; j < subjectIdList.length; j++) {
							
							// 对专题/首页id进行有效性分析
							String subjectIdParam = subjectIdList[j];
							if (subjectIdParam != null && subjectIdParam.trim().length() > 0) {
								subjectIdParam = subjectIdParam.trim();
								
								// 专题/首页实体
								Integer subjectId = new Integer(subjectIdParam);
								EntityItem target = (EntityItem)ItemManager.getInstance().get(subjectId, EntityItem.class);
								
								// 验证该实体的有效性:专题/首页类型,有效
								if (target != null && (target.getType() == ItemInfo.SUBJECT_TYPE
										|| target.getType() == ItemInfo.HOMEPAGE_TYPE)
										&& target.getStatus() != EntityItem.DISABLE_STATUS) {
									
									// 验证该专题确实应用了对应模板
									// ......
									
									// 编译该专题/首页对应模板的对应碎片
									Set tfmaps = template.getTFMaps();
									if (tfmaps == null)
										continue;
									Iterator iter = tfmaps.iterator();
									while (iter.hasNext()) {
										TFMap tfmap = (TFMap)iter.next();
										// 必须是"外部引用实体"类型
										if (tfmap.getUet() == DFragTag.UET_TYPE_EXTERNAL_ENTITY) {
											int entityId = tfmap.getEntityid();
											// 必须是引用了原先专题的实体
											if (entityId == source.getId()) {
												
												// 跨频道引用,需要同时在目标频道目录中也保存一份拷贝
												if(source.getChannel() > -1 
														&& source.getChannel() != target.getChannel()) {
													// 修改频道目录
													Subject sourceProxy = CoreFactory.getInstance().createSubject();
													PropertyUtils.copyProperties(sourceProxy, source);
													sourceProxy.setChannel(target.getChannel());
													
													// 编译该碎片,将碎片保存在目标频道中
													generateFrag(ueItem, sourceProxy, tfmap);
												}
												
												// 编译该碎片,将碎片保存在源频道中
												generateFrag(ueItem, source, tfmap);
												
												log.info("Cascaded Compile: (source=" + source.getId() 
														+ ") (template=" + template.getId() 
														+ ") (target=" + subjectId 
														+ ") (tfmap=" + tfmap.getId() + ":" + tfmap.getName() 
														+ ") (ueItem=" + ueItem.getId() 
														+ ") (channel:" + source.getChannel() + " -> " 
														+ target.getChannel() + ")" );
											}
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e) {
					continue;
				}
			}
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("Cascaded Compile：cost is: " + (timeEnd - timeStart) + " and caused by (ueItem=" 
				+ ueItem.getId() + ") (eItem=" + source.getId() + ") (template=" + weakReference + ")");
	}

	/**
	 * 生成碎片文件
	 * 
	 * @param ueItem
	 *            触发编译的实体
	 * @param eItem
	 *            新闻列表的父实体
	 * @param tfmap
	 *            模板碎片对应关系
	 */
	private void generateFrag(EntityItem ueItem, EntityItem eItem, TFMap tfmap) {
		try {
			long t1 = ct();
			int entityId = eItem.getId();
			int listCount = tfmap.getListcount();
			
			
			String prio = tfmap.getPrio();
			int idx = prio.indexOf("-");
			int prio1 = Integer.parseInt(prio.substring(0, idx));
			int prio2 = Integer.parseInt(prio.substring(idx + 1));

			// 55: 在权重基础上加2
			prio1 = 50 + (prio1 - 1) * 10;
			prio2 = 55 + (prio2 - 1) * 10;	
			int ueItemPrio = ueItem.getPriority();
			int ueItemOldPrio = ueItem.getOldpriority();
			
			if((ueItem.getType() == EntityItem.NEWS_TYPE) && tfmap.getSorttype()!=3 &&  ueItem.getStatus()== EntityItem.ENABLE_STATUS){
				/*
				 * agilewang add:
				 * 如果触发编译的是新闻,并且碎片的排序类型要求权重,那么检查它的状态和权重的范围
				 * 1.只有在新闻的状态是在有效的时候,才进一步检查权重的范围(主要是为了防止影响某些"重要新闻"从队列中去掉,例如打成无效或备用的新闻)
				 * 2.如果新闻有效,检查新闻的权重是否在碎片所要求的权重范围之内,如果不在这个范围,那么忽略对这个碎片的编译
				 */
				if(!((ueItemPrio >=prio1 && ueItemPrio<= prio2) || (ueItemOldPrio >= prio1 && ueItemOldPrio <= prio2))){
//					if(log.isWarnEnabled()){
//						log.warn("Skip frag "+tfmap.getId()+"["+tfmap.getPrio()+"] compile,Entity "+ueItem.getId()
//								+" ueItemPrio:"+ueItemPrio+" uteItemOldPril:"+ueItemOldPrio);
//					}
					return;
				}				
			}

			// get extfmap listcount
			int _listcount = getExlistcount(tfmap.getId(), eItem.getId());
			if (_listcount > 0)
				listCount = _listcount;

			long t00 = ct();
			LocalFile.write(Thread.currentThread()+"\t"+entityId+"\t"+listCount+"\t"+getCtTimeStr() + "\tr0\t" + (t00 - t1) + "\n",
					"/tmp/log/log_frag", true);
			int sortType = tfmap.getSorttype();
			int styleType = 1;
			String timeType = null;

			// 新闻列表样式
			// 1-20:时间 21:作者 22: 前缀父实体分类
			int decorate = tfmap.getTimetype();

			// 时间样式
			if (tfmap.getTimetype() > 0 && tfmap.getTimetype() <= 20) {
				timeType = CompileTaskFactory.getTimetype(tfmap.getTimetype());
				if (timeType == null) {
					// 时间样式无效
					decorate = -1;
					log.error("Frag TimeType is null. " + Global.CMSTIMETYPE + tfmap.getTimetype());
				}
			}
			if (tfmap.getTimetype() == 22) {
				timeType = CompileTaskFactory.getTimetype(1);
				if (timeType == null) {
					// 时间样式无效
					decorate = -1;
					log.error("Frag TimeType is null. " + Global.CMSTIMETYPE + tfmap.getTimetype());
				}
			}				

			/**
			 * 根据desc返回子列表的实体的类型:News或Picture
			 */
			int entityType = ItemUtil.getListEntityTypeByDesc(tfmap.getDesc());
			if(log.isDebugEnabled()){
				log.debug("list type:"+entityType+" for dfrag:"+tfmap.getId()+" desc:"+tfmap.getDesc());
			}
			
			// get frag content
			long t2 = ct();
			LocalFile.write(Thread.currentThread()+"\t"+entityId+"\t"+listCount+"\t"
					+getCtTimeStr() + "\tr1\t" + (t2 - t00) + "\n",	"/tmp/log/log_frag", true);
			
			// 支持多ID动态碎片(Alfred.Yuan)
			List allIdList = EntityParamUtil.getAllIdList(tfmap.getOtherIds(), entityId);
			if (allIdList != null && allIdList.size() > 0) {
				log.info("CompileTask: (fragId=" + tfmap.getId() + ") (fragName=" + tfmap.getName() 
						+ ") (entityId=" + tfmap.getEntityid() + ") (otherIds=" + tfmap.getOtherIds());
			}
			
			List items = null;
			if (sortType == 1) { // 在权重范围内,按时间排序
				if (allIdList != null && allIdList.size() > 0) {
					Query query = new Query();
					query.setIdList(allIdList);
					query.setType(entityType);
					query.setMinPriority(prio1);
					query.setMaxPriority(prio2);
					query.setCount(listCount);
					
					query.setSortType(Query.SORT_TYPE_PRIORITY_AND_TIME);
					
					items = ListCacheClient.getInstance().filter(query);
				}
				else {
					items = ListCacheClient.getInstance().TimeFilter(entityId, entityType, prio1, prio2, listCount);
				}
			} 
			else if (sortType == 2) { // 按权重排序
				if (allIdList != null && allIdList.size() > 0) {
					Query query = new Query();
					query.setIdList(allIdList);
					query.setType(entityType);
					query.setMinPriority(prio1);
					query.setMaxPriority(prio2);
					query.setCount(listCount);
					
					query.setSortType(Query.SORT_TYPE_PRIORITY);

					items = ListCacheClient.getInstance().filter(query);
				}
				else {
					items = ListCacheClient.getInstance().PrioFilter(entityId, entityType, prio1, prio2, listCount);
				}
			} 
			else if (sortType == 3) { // 按时间排序
				if (allIdList != null && allIdList.size() > 0) {
					Query query = new Query();
					query.setIdList(allIdList);
					query.setType(entityType);
					query.setStart(0);
					query.setCount(listCount);
					
					query.setSortType(Query.SORT_TYPE_TIME);
					
					items = ListCacheClient.getInstance().filter(query);
				}
				else {
					items = ListCacheClient.getInstance().TimeFilter(entityId, entityType, 0, listCount);
				}
			}

			if (items == null) {
				if(log.isErrorEnabled()){
					log.error("handleFrag --> items is null");
				}
				//fix by agilewang:如果items为空,那么会覆盖碎片原有的新闻,暂时忽略处理
				return;
			}

			long t11 = ct();			
			LocalFile.write(Thread.currentThread()+"\t"+entityId+"\t"+sortType+":"
					+listCount+"\t"+getCtTimeStr() + "\tr\t" + (t11 - t2) + "\n",
					"/tmp/log/log_frag", true);

			StringBuffer sb = new StringBuffer(1024);
			SimpleDateFormat formatter = null;
			if (timeType != null) {
				formatter = new SimpleDateFormat(timeType);
			}
			//判断是否Groovy碎片,如果是Groovy碎片,那优先执行Groovy脚本
			if (tfmap.getQuotetype() == DFragTag.GROOVY_QUOTETYPE
					&& tfmap.getQuotefrag() != null
					&& tfmap.getQuotefrag().trim().length() > 0) {
//				if (log.isInfoEnabled()) {
//					log.info("dfrag is groovy frag,so execute groovy script "
//							+ "tfmapid:" + tfmap.getId() + " tfmap quotefrag:"
//							+ tfmap.getQuotefrag());
//				}

				String groovyPath = GROOVY_ROOT + tfmap.getQuotefrag().trim()
						+ ".groovy";
				try {
					EmbedGroovy embedGroovy = new EmbedGroovy();
					Map params = new HashMap();
					params.put("items", items);
					params.put("entityId", new Integer(entityId));
					params.put("tfmap", tfmap);
					params.put("timeformater",formatter);
					embedGroovy.initial(groovyPath);
					embedGroovy.setParameters(params);
					Object result = embedGroovy.run();
					if (result == null) {
						result = embedGroovy.getProperty("result");
					}
					if (result != null) {
						sb.append(result.toString());
					}
				} catch (Exception e) {
					log.error("execute groovy script[" + groovyPath + "] error", e);
				}
			} else {
				CmsSortItem sortitem = null;
				for (int i = 0; items != null && i < items.size(); i++) {
					sortitem = (CmsSortItem) items.get(i);

					sb.append("<li>");
					
					if (formatter != null) {
                                                sb.append("<span>");
                                                sb.append(formatter.format(sortitem.getTime()));
                                                sb.append("</span>");
                                        }
                                        //标题加alt属性，过滤特殊字符
                                        sb.append("<a href=")
                                          .append(sortitem.getUrl())
                                          .append(" target=_blank")
                                          .append(" title=\"" + Util.RemoveHTML(sortitem.getDesc()).replaceAll("\"|“|”|'|‘|’|＂", " "))
                                          .append("\">" + sortitem.getDesc())
                                          .append("</a>")
                                          .append("</li>");
/*
					sb.append("<a href=");
					sb.append(sortitem.getUrl());
					sb.append(" target=_blank>");
					sb.append(sortitem.getDesc());
					sb.append("</a>");

					sb.append("</li>");
*/
				}
			}
			if (sb.length() == 0)
				sb.append(" ");

			String fStorePath = PageManager.getFStorePath(eItem, tfmap.getId(),	true);
			boolean flag = ClientFile.getInstance().write(sb.toString(), fStorePath);

			long t12 = ct();			
			LocalFile.write(getCtTimeStr() + "\tw\t" + (t12 - t11) + "\n", "/tmp/log/log_frag", true);
		} catch (Exception e) {
			log.error("handleFrag --> write frag error. ", e);
		}
	}

	/**
	 * 模板编译
	 * 
	 * @param tqitem
	 *            编译信息
	 */
	private void handleTemplate(TemplateQItem tqitem) {
		try {
			//log.debug("handleTemplate ... "+tqitem.getEntityid());

			long t1 = ct();
			tqitem = (TemplateQItem) tqitem;

			EntityItem eItem = (EntityItem) ItemManager.getInstance().get(
					new Integer(tqitem.getEntityid()), EntityItem.class);
			// 切换到cms4的专题实体模板需要设置, 在碎片填充之前, 提交后会导致页面空白
			// 暂时改为备用不重编译静态页面, 待系统稳定后再删除此段
			//if( eItem.getType()==ItemInfo.SUBJECT_TYPE )
			//{
			//if( eItem.getStatus()!=2 ) return;
			//}

			if (eItem.getType() == ItemInfo.NEWS_TYPE) {
				// 跳转链接, 不产生新页面
				String reurl = ((News) eItem).getReurl();
				if (reurl != null && !reurl.equals("")) {
					//log.warn("handleTemplate " + tqitem.getEntityid() + " is reurl. " + reurl);
					return;
				}
				// 标题为空不编译
				if (eItem.getDesc() == null	|| eItem.getDesc().trim().equals("")) {
					log.warn("handleTemplate " + tqitem.getEntityid() + " desc is null.");
					return;
				}

				// 分页新闻
				News news = (News) eItem;
				String content = news.getText();
					if (content.indexOf("<HEXUNMPCODE>") != -1
							&& content.indexOf("</HEXUNMPCODE>") != -1) {
						handleMultiPage(tqitem);
						return;
					}
			}

			// 多模板校验
			if (eItem.getTemplate() == null || eItem.getTemplate().equals("")) {
				log.warn("handleTemplate -- entity " + eItem.getId() + " no template");
				return;
			}
			String[] templates = eItem.getTemplate().split(Global.CMSSEP);

			// 开始处理多模板
			for (int k = 0; k < templates.length; k++) {
				if (templates[k].indexOf(Global.CMSCOMMA) == -1) {
					log.warn("handleTemplate -- template format is invalid  " + templates[k] + "  skiped...");
					continue;
				}

				int templateid = Integer.parseInt(templates[k].split(Global.CMSCOMMA)[0]);

				//if (templateid == 100) {
					// 从cms3导入到cms4的专题模板默认为100,该模板不存在
				//	continue;
				//}
				long templateS = ct();
				Template template = (Template) ItemManager.getInstance().get(
						new Integer(templateid), Template.class);
				
				if(tqitem.isMutil() && template.getMpage()!=MUTIL_TEMPLATE){
					/*
					 *如果是分页编译,那么对于其它的模板不进行编译,一方面防止编辑在修改模板的时候错误的编译实体,
					 *另一方面可以避免多余的编译
					 */
//					if(log.isWarnEnabled()){
//						log.warn("Skip compile template "+template.getId()+",it's not a mutilpage template");
//					}
					continue;
				}
				String url = tempUrl 
						+ PageManager.FTWebPath(template, false) + "?ENTITYID="
						+ eItem.getId() + "&view=" + FragTag.COMPILE_VIEW;
				String content = Util.httpRequest(url);
				long templateE = ct();
				LocalFile.write(getCtTimeStr() + "\t\t" + url + "\tr\t"
						+ (templateE - templateS) + "\n",
						"/tmp/log/log_template_httpget", true);
				long t11 = ct();				
				LocalFile.write(getCtTimeStr() + "\t\t" + eItem.getId()
						+ "\tr\t" + (t11 - t1) + "\n", "/tmp/log/log_template",
						true);

				if (content == null) {
					String expCont = tqitem.getEntityid() + Global.CMSSEP
							+ tqitem.getAction() + "\t";
					// 请求模板异常
					handleError(expCont);
					continue;
				}
				// 若content为空,若内容很少,说明模板本身有问题
				if (content != null
						&& content.indexOf("#include virtual") == -1) {
					log.error("handleTemplate -- templateid: " + templateid
							+ " fewer content, donot generate html.");
					continue;
				}

				// 处理多页专题
				if (template.getMpage() == MUTIL_TEMPLATE) {
					handlePagination(template, eItem);
				}

				// convert UPPERCASE to lowercase of tags
				//if (eItem.getType() == 1) {
				//	content = HtmlTagFilter.tag2Lower(content);
				//}
				String storePath = PageManager.getTStorePath((EntityItem) eItem, template.getId());
				boolean flag = ClientFile.getInstance().write(content, storePath);
				//log.info("create template " + storePath + "..." + ((flag == true) ? "OK" : "FAILURE"));

				long t12 = ct();				
				LocalFile.write(getCtTimeStr() + "\t\t" + eItem.getId()
						+ "\tw\t" + (t12 - t11) + "\n",
						"/tmp/log/log_template", true);
			}
		} catch (Exception e) {
			log.error("handleTemplate error: ", e);
		}
	}

	/**
	 * @return
	 */
	private String getCtTimeStr() {
		return (new Timestamp(ct())).toString();
	}

	/**
	 * 向异常处理文件中添加一个实体信息
	 * 
	 * @param content
	 *            实体信息 content 格式 entityid;0|1\t
	 */
	private void handleError(String content) {
		CompileTaskFactory.handleError(content);
	}

	/**
	 * 删除静态文件
	 */
	private void deleteTemplate(TemplateQItem tqitem) {
		try {
			tqitem = (TemplateQItem) tqitem;

			EntityItem eItem = (EntityItem) ItemManager.getInstance().get(
					new Integer(tqitem.getEntityid()), EntityItem.class);
			// 多模板校验
			if (eItem.getTemplate() == null || eItem.getTemplate().equals("")) {
				log.warn("deleteTemplate -- entity " + eItem.getId() + " no template");
				return;
			}
			String[] templates = eItem.getTemplate().split(Global.CMSSEP);

			// 开始处理多模板
			for (int k = 0; k < templates.length; k++) {
				if (templates[k].indexOf(Global.CMSCOMMA) == -1) {
					log.warn("deleteTemplate --> template format is invalid " + templates[k] + "  skiped...");
					continue;
				}

				int templateid = Integer.parseInt(templates[k].split(Global.CMSCOMMA)[0]);
				Template template = (Template) ItemManager.getInstance().get(
						new Integer(templateid), Template.class);
				String storePath = PageManager.getTStorePath((EntityItem) eItem, template.getId());				
				boolean flag = ClientFile.getInstance().delete(storePath);
				//log.info("delete template " + storePath + " ... " + ((flag == true) ? "OK" : "FAILURE"));
				
				// delete multi page
				if (eItem instanceof News) {
					int totalPage = getPage((News) eItem);
					for (int j = 1; j < totalPage; j++) {
						String multiPath = getStorePathByPage(storePath, j);
						ClientFile.getInstance().delete(multiPath);
					}
					//文章无效清除单页阅读或收费阅读xml文件 added by shijinkui 09-04-14
					String path = "/cmsdata/fullcxml/" + eItem.getChannel() + "/"
					+ Util.formatTime(eItem.getTime(), "yyyy-MM-dd") + "/"
					+ eItem.getId() + ".xml";
					try {
						if(StringUtils.isNotEmpty(ClientFile.getInstance().read(path))) ClientFile.getInstance().delete(path);
					} catch (Exception e) {
						log.error("clear xml file error: " + e.getMessage());
						e.printStackTrace();
					}					
				}
				else if (eItem instanceof Subject && template.getMpage()==2){
					//如果是专题类型,并且模板的分页型是2,需要把生成的分页内容也删除掉
					String pagingDataFile = "/root/h/WEB-INF/paging_data/"+eItem.getId()+"_"+template.getId();
					String pagingData = LocalFile.read(pagingDataFile);
					if(pagingData != null && pagingData.trim().length()>0){						
					    String [] pds = pagingData.trim().split("/");
					    if(pds != null && pds.length >= 2){					    
					    	try{
					    		int maxPage = Integer.parseInt(pds[0]);			
					    		for(int j =1;j<maxPage;j++){
					    			String multiPath = getStorePathByPage(storePath, j);
					    			ClientFile.getInstance().delete(multiPath);
					    			//log.info("Delete paging page "+storePath+" for entityId:"+eItem.getId());
					    		}
					    		//删除分页记录文件
					    		LocalFile.delete(pagingDataFile);
					    	}
					    	catch(Exception e){
					    		log.error("parse paging data error",e);
					    	}
					    }
					}

				}
			}
		} catch (Exception e) {
			log.error("deleteTemplate error: " + e.toString());
		}
	}

	/**
	 * 处理多页专题
	 */
	private void handlePagination(Template template, EntityItem entity) {
		try {

			//log.debug("handlePagination "+entity.getId()+" begin ... ");

			Iterator itr = template.getTFMaps().iterator();
			TFMap tfmap = null;
			while (itr != null && itr.hasNext()) {
				TFMap l_tfmap = (TFMap) itr.next();
				if (l_tfmap.getType() != 3)
					continue;
				if (l_tfmap.getUet() != 1)
					continue;
				tfmap = l_tfmap;
			}
			if (tfmap == null) {
				log.warn("handlePagination template [" + template.getName()
						+ "] has no dynamic frag of uet=1");
				return;
			}
			
			int listCount = 0;
			listCount = getExlistcount(tfmap.getId(), entity.getId());
			if (listCount <= 0)
				listCount = tfmap.getListcount();
			if (listCount <= 0) {
				log.warn("template [" + template.getName() + "] listCount=0.");
				return;
			}
			//log.info("pagination ENTITY[" + entity.getId() + "] listCount[" + listCount + "]");
			
			int entityType = ItemUtil.getListEntityTypeByDesc(tfmap.getDesc());
			//log.info("list type:"+entityType+" for dfrag:"+tfmap.getId()+" desc:"+tfmap.getDesc());
			List newsList = ListCacheClient.getInstance().TimeFilter(
					entity.getId(), entityType, 0,
					MAX_SIZE_OF_LISTCACHE);
			int newslistSize = newsList.size();
			if (newslistSize <= 0) {
				log.warn("ENTITY [" + entity.getId() + "] newslistSize is 0.");
				return;
			}

			String groovyPath = GROOVY_ROOT + PAGING_GROOVY_SCRIPT+ ".groovy";
			log.info("run groovy script '" + groovyPath	+ "' for entity id:" + entity.getId());
			try {
				EmbedGroovy embedGroovy = new EmbedGroovy();
				Map params = new HashMap();
				params.put("items", newsList);
				params.put("entity",entity);
				params.put("listCount", new Integer(listCount));
				params.put("tfmap", tfmap);
				params.put("log",log);
				params.put("template",template);
				embedGroovy.initial(groovyPath);
				embedGroovy.setParameters(params);
				Object result = embedGroovy.run();
				if (result == null) {
					result = embedGroovy.getProperty("result");
				}
				//log.info("run paging groovy result:" + result);
			} catch (Exception e) {
				log.error("execute groovy script[" + groovyPath + "] error",e);
			}
			return;			
		} catch (Exception e) {
			log.error("handlePagination exception -- ", e);
		}
	}

	private static String getPaginationPath(String path, int page) {
		try {
			if (page == 0)
				return null;

			int idx = path.lastIndexOf(".html");
			if (idx < 0)
				return null;

			return path.substring(0, idx) + "_" + page + ".html";

		} catch (Exception e) {
			log.error("getPaginationPath exception -- ", e);
			return null;
		}
	}

	/**
	 * 处理分页新闻
	 */
	private void handleMultiPage(TemplateQItem tqitem) {
		try {

			News eItem = (News) ItemManager.getInstance().get(
					new Integer(tqitem.getEntityid()), News.class);

			// 多模板校验
			if (eItem.getTemplate() == null || eItem.getTemplate().equals("")) {
				log.error("handleMultiPage -- entity " + eItem.getId() + " no template");
				return;
			}
			String[] templates = eItem.getTemplate().split(Global.CMSSEP);
			if (templates.length == 0) {
				log.error("handleMultiPage -- entity " + eItem.getId() + " no template");
				return;
			}

			int totalPage = getPage(eItem);
			for (int k = 0; k < totalPage; k++) {
				if (templates[0].indexOf(Global.CMSCOMMA) == -1) {
					log.error("handleMultiPage -- template format is invalid " + templates[0] + "  skiped...");
					continue;
				}

				int templateid = Integer.parseInt(templates[0].split(Global.CMSCOMMA)[0]);
				Template template = (Template) ItemManager.getInstance().get(
						new Integer(templateid), Template.class);
				String url = tempUrl 
						+ PageManager.FTWebPath(template, false) + "?ENTITYID="
						+ eItem.getId() + "&view=" + FragTag.COMPILE_VIEW
						+ "&mpage=" + k;
				//log.info("Template --> request: "+url);
				String content = Util.httpRequest(url);
				if (content == null) {
					String expCont = tqitem.getEntityid() + Global.CMSSEP + tqitem.getAction() + "\t";
					// 请求模板异常
					LocalFile.write(expCont, CompileTaskFactory.fileexception, true);
					//log.info("template file exception. ");
					continue;
				}
				long t3 = ct();
				String storePath = PageManager.getTStorePath((EntityItem) eItem, template.getId());
				storePath = getStorePathByPage(storePath, k);
				boolean flag = ClientFile.getInstance().write(content, storePath);
				//if( tcounter%100==0 )
				//{
				//log.info("handleMultiPage -- create " + storePath + " ... "	
				//    + ((flag == true) ? "OK" : "FAILURE"));
				//}
				long t4 = ct();
				//LocalFile.write( (t3-t1)+"\n", "/tmp/log/log_httptemplate",
				// true );
				//LocalFile.write( (t4-t3)+"\n", "/tmp/log/log_writetemplate",
				// true );
				//tcounter++;
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	private int getPage(News news) {
		String content = news.getText();
		int idx = 0;
		int count = 0;
		while (true) {
			idx = content.indexOf("<HEXUNMPCODE>", idx);
			if (idx == -1) {
				break;
			} else {
				count++;
			}
			idx = idx + "<HEXUNMPCODE>".length();
		}
		return count;
	}

	private String getStorePathByPage(String path, int page) {
		String ret = path;
		if (page > 0) {
			ret = path.substring(0, path.indexOf(".html"));
			ret += "_" + page + ".html";
		}
		return ret;
	}

	private static long ct() {
		return System.currentTimeMillis();
	}
}
