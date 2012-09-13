/*
 * Created on 2005-7-11
 */
package net.pusuo.cms.client.action;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.auth.Group;
import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.tool.Cms4ClientDb;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;


/**
 * @author agilewang �ṩ��ѯ�༭ͳ����Ϣ�Ľӿ�
 *  
 */
public class EditorSearchAction extends BaseAction {

	private static final String CHANNL2GROUP = "channl2group.properties";

	private static final int ONE_DAY_MILLS = (1000 * 60 * 60 * 24);

	private static final int DAYS = 2;

	private static final int HOURS = 1;

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String DATE_SHOTR_FORMAT = "yyyy-MM-dd";

	public static final int ENTITY_PAGE_MAX = 20;

	private static final int ENTITY_PAGE_MAX_PLUS = ENTITY_PAGE_MAX + 1;

	private static final String STARTID_FIELD = "startID";

	private static final String DAYSNUMBER_FIELD = "daysNumber";

	private static final String DAY_FIELD = "day";

	private static final String MONTH_FIELD = "month";

	private static final String YEAR_FIELD = "year";

	private static final String ENTITY_FIELD = "entity";
	
	private static final String QUERYT_FIELE = "queryt";

	private static final String MEDIAID_FIELD = "mediaID";

	private static final String EDITOR_FIELD = "editor";

	private static final String CHANNEL_FIELD = "channel";

	private static final int INVALID_INT_VALUE = -1;

	private static final String IVALID_STR_VALUE = String
			.valueOf(INVALID_INT_VALUE);

	private static final String LIST_PREFIX = "List";

	private static final String YEAR_LIST = "yearList";

	private static final String MONTH_LIST = "monthList";

	private static final String DAY_LIST = "dayList";

	private static final String EDITOR_LIST = "editorList";

	private static final ArrayList MONTH_LIST_VALUE = new ArrayList();

	private static final ArrayList DAY_LIST_VALUE = new ArrayList();

	private static final ArrayList ENTITY_LIST_VALUE = new ArrayList();

	private static final Object NOPARAM = new Object();

	private static final String STATIC_QUERY_PRE = "select entity_editor,count(*) cnt from cms_entity e,cms_news n where  ";

	private static final String ENTITY_QUERY_PRE = "select e.entity_id  from cms_entity e,cms_news n where ";

	private static final String ENTITY_QUERY_COUNT_PRE = "select count(e.entity_id) from cms_entity e,cms_news n where ";

	private static final String STATIC_QUERY_DAY = "select e.entity_id,e.entity_time from cms_entity e,cms_news n where ";

	public static final int ENTITY_QUERY = 1;

	public static final int STATIC_QUERY = 2;
	
	public static final int CHANNEL_QUERY = 3;
	
	private static final Map C2G_MAP = new HashMap();

	static {
		//�����µ��б�
		for (int i = 1; i <= 12; i++) {
			String monthStr = String.valueOf(i);
			MONTH_LIST_VALUE.add(new LabelValueBean(monthStr, monthStr));
		}
		//�����յ��б�
		for (int i = 1; i <= 31; i++) {
			String dayStr = String.valueOf(i);
			DAY_LIST_VALUE.add(new LabelValueBean(dayStr, dayStr));
		}
		//����ʵ�������б�
		ENTITY_LIST_VALUE.add(new LabelValueBean("News", String
				.valueOf(EntityItem.NEWS_TYPE)));

	}

	private static final Log log = LogFactory.getLog(EditorSearchAction.class);

	private Map getChannelMapGroup() {
		Properties pros = new Properties();
		InputStream channelMap = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(CHANNL2GROUP);
		System.out.println(channelMap);
		if (channelMap != null) {
			try {
				pros.load(channelMap);
			} catch (IOException ioe) {
			} finally {
				try {
					channelMap.close();
				} catch (IOException e) {

				}
			}
		}
		return pros;
	}

	/**
	 * ���������Ĺ���
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer channelObj = (Integer) dForm.get(CHANNEL_FIELD);
		Integer editorObj = (Integer) dForm.get(EDITOR_FIELD);
		Integer mediaObj = (Integer) dForm.get(MEDIAID_FIELD);
		Integer entityObj = (Integer) dForm.get(ENTITY_FIELD);
		Integer yearObj = (Integer) dForm.get(YEAR_FIELD);
		Integer monthObj = (Integer) dForm.get(MONTH_FIELD);
		Integer dayObj = (Integer) dForm.get(DAY_FIELD);
		Integer daysObj = (Integer) dForm.get(DAYSNUMBER_FIELD);
		Integer startIDObj = (Integer) dForm.get(STARTID_FIELD);
		String queryT  = request.getParameter(QUERYT_FIELE);
		if(queryT==null || queryT.equals("")) queryT = "0";
		//(String) dForm.get(QUERYT_FIELE);

		int channel = channelObj.intValue();
		int editor = editorObj.intValue();
		int mediaID = mediaObj.intValue();
		int entityType = entityObj.intValue();
		int startID = startIDObj.intValue();

		int year = yearObj.intValue();
		int month = monthObj.intValue();
		int day = dayObj.intValue();
		int days = daysObj.intValue();

		Calendar beginTime = getCalendar(year, month, day, 0, 0, 0, 0);
		Calendar endTime = getCalendar(year, month, day, 23, 59, 59, 999);
        
		if(days >= 10 ) days = 10;
		if(queryT.equals("5"))  {
			if(days >= 3 ) days = 3;
		}
		if (days >= 1) {
			endTime.add(Calendar.DAY_OF_MONTH, days - 1);
		}

		Timestamp beginTs = new Timestamp(beginTime.getTimeInMillis());
		Timestamp endTs = new Timestamp(endTime.getTimeInMillis());
		List queryList = new ArrayList();
		List queryCountList = null;
		String sql = null;
		String entity_count_sql = null;
		StringBuffer enity_count_sql_Buffer = new StringBuffer();
		String sqlExt = null;
		StringBuffer sqlBuffer = new StringBuffer();
		int queryType = -1;
		//�����ѯ���
		if(queryT.equals("5")) {
			
			//���ý���Ƶ����ѯ
			queryType = CHANNEL_QUERY;
			sql = ENTITY_QUERY_PRE;
			enity_count_sql_Buffer.append(ENTITY_QUERY_COUNT_PRE);
			queryCountList = new ArrayList();

			//��ѯʵ���Sql
			addParameter("e.entity_channel=?", channelObj, queryList);
			addParameter("e.entity_status=2", NOPARAM, queryList);
			addParameter("n.news_reurl is null", NOPARAM, queryList);
			addParameter("e.entity_id=n.entity_id", NOPARAM, queryList);
			addParameter("e.entity_time>=?", beginTs, queryList);
			addParameter("e.entity_time<=?", endTs, queryList);

			//��ѯʵ�������sql
			addParameter("e.entity_channel=?", channelObj, queryCountList);
			addParameter("e.entity_status=2", NOPARAM, queryCountList);
			addParameter("n.news_reurl is null", NOPARAM, queryCountList);
			addParameter("e.entity_id=n.entity_id", NOPARAM, queryCountList);
			addParameter("e.entity_time>=?", beginTs, queryCountList);
			addParameter("e.entity_time<=?", endTs, queryCountList);

			if (mediaID > INVALID_INT_VALUE) {
				addParameter("n.news_media=?", mediaObj, queryList);

				addParameter("n.news_media=?", mediaObj, queryCountList);
			}
			if (startID > INVALID_INT_VALUE) {
				//��ѯʵ������Ĳ���Ҫ�Ƚ�entity_id
				addParameter("e.entity_id>=?", startIDObj, queryList);
			}

			addParameter("rownum<=?", new Integer(ENTITY_PAGE_MAX_PLUS),
					queryList);
			sqlExt = "order by e.entity_id ";

			//add ͳ��ʵ������
			getSql(queryCountList, enity_count_sql_Buffer);
			entity_count_sql = enity_count_sql_Buffer.toString();
			enity_count_sql_Buffer = null;
			
		} else {
			
			if (mediaID > INVALID_INT_VALUE || editor > INVALID_INT_VALUE) {
				//���ý��ͱ༭��ѯ
				queryType = ENTITY_QUERY;
				sql = ENTITY_QUERY_PRE;
				enity_count_sql_Buffer.append(ENTITY_QUERY_COUNT_PRE);
				queryCountList = new ArrayList();
	
				//��ѯʵ���Sql
				addParameter("e.entity_channel=?", channelObj, queryList);
				addParameter("e.entity_status=2", NOPARAM, queryList);
				addParameter("n.news_reurl is null", NOPARAM, queryList);
				addParameter("e.entity_id=n.entity_id", NOPARAM, queryList);
				addParameter("e.entity_time>=?", beginTs, queryList);
				addParameter("e.entity_time<=?", endTs, queryList);
	
				//��ѯʵ�������sql
				addParameter("e.entity_channel=?", channelObj, queryCountList);
				addParameter("e.entity_status=2", NOPARAM, queryCountList);
				addParameter("n.news_reurl is null", NOPARAM, queryCountList);
				addParameter("e.entity_id=n.entity_id", NOPARAM, queryCountList);
				addParameter("e.entity_time>=?", beginTs, queryCountList);
				addParameter("e.entity_time<=?", endTs, queryCountList);
	
				if (mediaID > INVALID_INT_VALUE) {
					addParameter("n.news_media=?", mediaObj, queryList);
	
					addParameter("n.news_media=?", mediaObj, queryCountList);
				}
				if (editor > INVALID_INT_VALUE) {
					addParameter("e.entity_editor=?", editorObj, queryList);
	
					addParameter("e.entity_editor=?", editorObj, queryCountList);
				}
				if (startID > INVALID_INT_VALUE) {
					//��ѯʵ������Ĳ���Ҫ�Ƚ�entity_id
					addParameter("e.entity_id>=?", startIDObj, queryList);
				}
	
				addParameter("rownum<=?", new Integer(ENTITY_PAGE_MAX_PLUS),
						queryList);
				sqlExt = "order by e.entity_id ";
	
				//add ͳ��ʵ������
				getSql(queryCountList, enity_count_sql_Buffer);
				entity_count_sql = enity_count_sql_Buffer.toString();
				enity_count_sql_Buffer = null;
			} else {
				//ͳ��ĳ��Ƶ�����������б༭�ķ������,������ֻ��ѡ��ĳ��Ƶ���µ������û�
				queryType = STATIC_QUERY;
				sql = STATIC_QUERY_PRE;
				sqlExt = "group by entity_editor order by cnt desc";
				addParameter("e.entity_channel=?", channelObj, queryList);
				addParameter("e.entity_status=2", NOPARAM, queryList);
				addParameter("n.news_reurl is null", NOPARAM, queryList);
				addParameter("e.entity_id=n.entity_id", NOPARAM, queryList);
				addParameter("e.entity_time>=?", beginTs, queryList);
				addParameter("e.entity_time<=?", endTs, queryList);
			}
		}
		sqlBuffer.append(sql);
		getSql(queryList, sqlBuffer);
		if (sqlExt != null) {
			sqlBuffer.append(" " + sqlExt);
		}
		String realSql = sqlBuffer.toString();
		sqlBuffer = null;
		if (log.isInfoEnabled()) {
			log.info("EditorSearchAction@realSql:" + realSql);
		}

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement count_ps = null;
		ResultSet rs = null;
		List pageList = new ArrayList();
		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			conn = Cms4ClientDb.GetCMS4DBPool().getConnection();
			ps = conn.prepareStatement(realSql);
			setParam(queryList, ps);
			rs = ps.executeQuery();
			if (queryType == ENTITY_QUERY || queryType == CHANNEL_QUERY) {
				int entity_ID_count = 0;
				int entity_ID = -1;
				int sum = 0;
				//��ͳ���ܹ�������
				count_ps = conn.prepareStatement(entity_count_sql);
				setParam(queryCountList, count_ps);
				ResultSet cuRs = count_ps.executeQuery();
				if (cuRs.next()) {
					sum = cuRs.getInt(1);
				}

				cuRs.close();
				count_ps.close();
				cuRs = null;
				count_ps = null;

				while (rs.next()) {
					entity_ID = rs.getInt(1);
					News news = (News) ItemManager.getInstance().get(
							new Integer(entity_ID), News.class);
					if (news == null) {
						continue;
					}
					entity_ID_count++;
					String desc = news.getDesc();
					int prior   = news.getPriority();
					Timestamp entity_time = news.getTime();
					
					User user = (User) ItemManager.getInstance().get(
							new Integer(news.getEditor()), User.class);
					if (user == null) {
						continue;
					}
					String editorName = user.getName();
					String mediaName = null;
					if (news.getMedia() != INVALID_INT_VALUE) {
						Media media = (Media) ItemManager.getInstance().get(
								new Integer(news.getMedia()), Media.class);
						
						if (media != null) {
						   mediaName = media.getName();
					  }else {
					  	 mediaName = "\u65e0";
					  }
						
					} else {
						//��
						mediaName = "\u65e0";
					}
					if (entity_ID_count <= ENTITY_PAGE_MAX) {
						pageList.add(new String[] { String.valueOf(entity_ID),
								desc, editorName, mediaName,
								dataFormat.format(entity_time),
								String.valueOf(prior) });
					}
				}
				if (entity_ID_count > ENTITY_PAGE_MAX) {
					dForm.set(STARTID_FIELD, new Integer(entity_ID));
					request.setAttribute("hasMore", Boolean.TRUE);
				}
				if (startID > 0) {
					request.setAttribute("mutiPages", Boolean.TRUE);
				}
				request.setAttribute("sum", new Integer(sum));
			} else {
				int sum = 0;
				String bt = dataFormat.format(beginTs);
				String et = dataFormat.format(endTs);
				while (rs.next()) {
					int editor_ID = rs.getInt(1);
					int count = rs.getInt(2);
					sum += count;
					User user = (User) ItemManager.getInstance().get(
							new Integer(editor_ID), User.class);
					pageList.add(new String[] {
							String.valueOf(editor_ID),
							user.getName()
									+ (user.getDesc() != null ? "-["
											+ user.getDesc() + "]" : ""),
							String.valueOf(count), bt, et,
							String.valueOf(channel) });

				}
				request.setAttribute("sum", new Integer(sum));
			}
			request.setAttribute("queryType", new Integer(queryType));
			request.setAttribute("pageList", pageList);
			request.setAttribute("queryt", queryT);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("EditorSearchAction error", e);
			}
			errors.add("errors.item.view", new ActionError("errors.item.view"));
		} finally {
			Cms4ClientDb.close(rs);
			Cms4ClientDb.close(ps);
			Cms4ClientDb.close(conn);
		}
		initForm(errors, dForm, request, response);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("input");
		}
		return ret;
	}

	/**
	 * ������ʾ��ѯҳ��
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;

		//���������б�
		Calendar rightNow = Calendar.getInstance();
		int year = rightNow.get(Calendar.YEAR);
		int month = rightNow.get(Calendar.MONTH) + 1;
		int day = rightNow.get(Calendar.DAY_OF_MONTH);
		dForm.set(YEAR_FIELD, new Integer(year));
		dForm.set(MONTH_FIELD, new Integer(month));
		dForm.set(DAY_FIELD, new Integer(day));
		dForm.set(DAYSNUMBER_FIELD, new Integer(1));
		String queryT  = request.getParameter(QUERYT_FIELE);
		if(queryT==null || queryT.equals("")) queryT = "0";
 		request.setAttribute("queryt", queryT);
		//dForm.set(QUERYT_FIELE, "0");
		
		
		initForm(errors, dForm, request, response);
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("input");
		}

		return ret;
	}

	/**
	 * �༭������ͳ��
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward editorStatic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer editorObj = (Integer) dForm.get(EDITOR_FIELD);
		Integer channelObj = (Integer) dForm.get(CHANNEL_FIELD);
		String stBegin = (String) dForm.get("stBegin");
		String stEnd = (String) dForm.get("stEnd");

		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
		Date begin = null;
		Date end = null;

		do {
			try {
				begin = dataFormat.parse(stBegin);
				end = dataFormat.parse(stEnd);
			} catch (ParseException e) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"date.format.error"));
				break;
			}
			if (editorObj.intValue() <= INVALID_INT_VALUE) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"editor.id.error"));
				break;
			}
			if (channelObj.intValue() <= INVALID_INT_VALUE) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"channel.id.error"));
				break;
			}

			User user = (User) ItemManager.getInstance().get(editorObj,
					User.class);
			if (user == null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"editor.obj.error"));
				break;
			}
			//����ͼ�������
			int columns = days(begin.getTime(), end.getTime());
			request.setAttribute("user", user);
			request.setAttribute("begin", begin);
			request.setAttribute("end", end);
			request.setAttribute("channel", channelObj);
			//�����һ��,����СʱΪ�̶ȣ�Ҳ����24Сʱ
			request.setAttribute("columns", new Integer((columns == 1) ? 24
					: columns));
		} while (false);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("static");
		}
		return ret;
	}

	public ActionForward editorStaticDay(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer editorObj = (Integer) dForm.get(EDITOR_FIELD);
		Integer channelObj = (Integer) dForm.get(CHANNEL_FIELD);
		String stBegin = (String) dForm.get("stBegin");
		String stEnd = (String) dForm.get("stEnd");

		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_SHOTR_FORMAT);
		Date begin = null;
		Date end = null;

		do {
			try {
				begin = dataFormat.parse(stBegin);
				end = dataFormat.parse(stEnd);
			} catch (ParseException e) {
				log.error("date.format.error", e);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"date.format.error"));
				break;
			}

			if (end.before(begin)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"date.format.error"));
				break;
			}
			if (editorObj.intValue() <= INVALID_INT_VALUE) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"editor.id.error"));
				if (log.isWarnEnabled()) {
					log.warn("editor.id.error");
				}
				break;
			}
			if (channelObj.intValue() <= INVALID_INT_VALUE) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"channel.id.error"));
				if (log.isWarnEnabled()) {
					log.warn("channel.id.error");
				}
				break;
			}

			Calendar sc = Calendar.getInstance();
			Calendar ec = Calendar.getInstance();
			sc.clear();
			ec.clear();
			sc.setTime(begin);
			ec.setTime(end);

			sc.set(Calendar.HOUR_OF_DAY, 0);
			sc.set(Calendar.MINUTE, 0);
			sc.set(Calendar.SECOND, 0);
			sc.set(Calendar.MILLISECOND, 0);

			ec.set(Calendar.HOUR_OF_DAY, 23);
			ec.set(Calendar.MINUTE, 59);
			ec.set(Calendar.SECOND, 59);
			ec.set(Calendar.MILLISECOND, 999);

			Timestamp bs = new Timestamp(sc.getTimeInMillis());
			Timestamp es = new Timestamp(ec.getTimeInMillis());

			long scl = sc.getTimeInMillis();
			long ecl = ec.getTimeInMillis();

			int day = days(scl, ecl);
			if (log.isInfoEnabled()) {
				log.info("day is " + day);
			}

			int data[] = null;
			String[] title = null;
			int dataType = 0;

			if (day == 1) {
				data = new int[24];
				title = new String[24];
				dataType = HOURS;
				for (int i = 0; i < title.length; i++) {
					title[i] = String.valueOf(i);
				}
			} else {
				data = new int[day];
				title = new String[day];
				dataType = DAYS;
				SimpleDateFormat format = new SimpleDateFormat("[yyyy-MM-dd]");
				Calendar scClone = (Calendar) sc.clone();
				for (int i = 0; i < day; i++) {
					title[i] = format
							.format(new Date(scClone.getTimeInMillis()));
					scClone.add(Calendar.DAY_OF_MONTH, 1);
				}
				//scClone.getTimeInMillis()
				scClone = null;
				format = null;

			}

			List queryList = new ArrayList();
			String sql = null;
			String sqlExt = null;
			StringBuffer sqlBuffer = new StringBuffer();
			int queryType = -1;

			//�����ѯ���
			sql = STATIC_QUERY_DAY;
			addParameter("e.entity_channel=?", channelObj, queryList);
			addParameter("e.entity_status=2", NOPARAM, queryList);
			addParameter("e.entity_id=n.entity_id", NOPARAM, queryList);
			addParameter("e.entity_editor=?", editorObj, queryList);
			addParameter("e.entity_time>=?", bs, queryList);
			addParameter("e.entity_time<=?", es, queryList);
			sqlBuffer.append(sql);
			getSql(queryList, sqlBuffer);
			String realSql = sqlBuffer.toString();
			if (log.isInfoEnabled()) {
				log.info("editorStaticDay@realSql:" + realSql);
			}

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			List pageList = new ArrayList();
			try {
				conn = Cms4ClientDb.GetCMS4DBPool().getConnection();
				ps = conn.prepareStatement(realSql);
				setParam(queryList, ps);
				rs = ps.executeQuery();
				while (rs.next()) {
					Timestamp time = rs.getTimestamp(2);
					if (dataType == HOURS) {
						data[time.getHours()]++;
					} else {
						data[(int) ((time.getTime() - scl) / ONE_DAY_MILLS)]++;
					}
				}
				request.setAttribute("data", data);
				request.setAttribute("title", title);
			} catch (Exception se) {
				log.error("db errro", se);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"db errro"));
			} finally {
				Cms4ClientDb.close(rs);
				Cms4ClientDb.close(ps);
				Cms4ClientDb.close(conn);
			}

		} while (false);

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("statics");
		}
		return ret;

	}

	/**
	 * @param scl
	 * @param ecl
	 * @return
	 */
	private int days(long scl, long ecl) {
		int day = (int) ((ecl - scl) / ONE_DAY_MILLS + 1);
		return day;
	}

	/**
	 * ��ʼ��Form
	 * 
	 * @param errors
	 * @param dForm
	 */
	private void initForm(ActionErrors errors, BaseForm dForm,
			HttpServletRequest request, HttpServletResponse response) {
		//����û�
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);

		} catch (UnauthenticatedException ue) {
			log.error("autho error", ue);
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"user auth error"));
			return;
		}

		//����Ƶ��
		Integer channelObj = (Integer) dForm.get(CHANNEL_FIELD);
		int channel = channelObj.intValue();
		//fix bug:
		List channelList = new ArrayList();
		channelList.addAll(auth.getChannelList());

		if (channelList == null || channelList.size() == 0) {
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"channel.empty"));
			return;
		}

		Map channelMap = getChannelMapGroup();

		//����Ƶ��
		Iterator channelIr = channelList.iterator();
		while (channelIr.hasNext()) {
			Channel ct = (Channel) channelIr.next();
			//System.out.println("channel name:" + ct.getName() + " id:"
			//+ ct.getId());
			if (channelMap.get(String.valueOf(ct.getId())) == null) {
				channelIr.remove();
			}
		}

		List cahnnel_value = new ArrayList();
		//��ѡ��Ƶ��
		cahnnel_value.add(new LabelValueBean("\u8bf7\u9009\u62e9\u9891\u9053",
				String.valueOf(INVALID_INT_VALUE)));
		ItemUtil.ListToLVB(channelList, cahnnel_value);
		dForm.set("channelList", cahnnel_value);

		//ӳ��Ƶ����Ƶ�����е��û�
		if (channel != INVALID_INT_VALUE) {
			String mapGroup = (String) channelMap.get(String.valueOf(channel));
			if (mapGroup == null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"map.channel.error.null"));
				return;
			} else {
				Group group = (Group) ItemManager.getInstance().get(
						new Integer(mapGroup), Group.class);
				if (group == null) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
							"map.channel.error.group.null"));
					return;
				} else {
				    String user_sql = "from "+User.class.getName()+" user where user.group="+mapGroup;
				    List users = ItemManager.getInstance().getList(user_sql,null,-1,-1);					
					List userList = new ArrayList();
					Iterator userI = users.iterator();
					//���б༭
					userList.add(new LabelValueBean("\u6240\u6709\u7f16\u8f91",
							IVALID_STR_VALUE));
					//Ϊÿ���û�����ϳ����û���һѡ��,����ϵͳ��cms���ID��40

					//userList.add(new LabelValueBean("root", "90"));

					while (userI.hasNext()) {
						User user = (User) userI.next();
						userList.add(new LabelValueBean(user.getName()
								+ "-["
								+ ((user.getDesc() == null) ? "" : user
										.getDesc()) + "]", String.valueOf(user
								.getId())));
					}
					dForm.set(EDITOR_LIST, userList);
				}
			}
		}

		//���������б�
		Calendar rightNow = Calendar.getInstance();
		int year = rightNow.get(Calendar.YEAR);
		int month = rightNow.get(Calendar.MONTH);
		int day = rightNow.get(Calendar.DAY_OF_MONTH);

		List yearList = new ArrayList();
		for (int i = year; i >= 2001; i--) {
			String yearStr = String.valueOf(i);
			yearList.add(new LabelValueBean(yearStr, yearStr));
		}

		dForm.set(YEAR_LIST, yearList);
		dForm.set(MONTH_LIST, MONTH_LIST_VALUE);
		dForm.set(DAY_LIST, DAY_LIST_VALUE);

		//����List�б�
		Map formMap = dForm.getMap();
		Set entrySet = formMap.entrySet();
		Iterator entryI = entrySet.iterator();
		try {
			while (entryI.hasNext()) {
				Map.Entry entry = (Map.Entry) entryI.next();
				String key = (String) entry.getKey();
				if (key.startsWith(LIST_PREFIX)) {
					String name = key.substring(LIST_PREFIX.length());
					List list = ItemManager.getInstance().getList(
							ItemInfo.getItemClass(name));
					if (list != null) {
						List key_value = new ArrayList();
						if (name.equals(String.valueOf(ItemInfo.MEDIA_TYPE))) {
							//û��ý��
							key_value.add(new LabelValueBean(
									"\u6ca1\u6709\u5a92\u4f53",
									IVALID_STR_VALUE));
						}
						ItemUtil.ListToLVB(list, key_value);
						dForm.set(key, key_value);
					}
				}
			}
		} catch (Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			log.error("EditorSearchAction", e);
		}
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	private Calendar getCalendar(int year, int month, int day, int hour,
			int minute, int second, int mill) {
		Calendar calendar = Calendar.getInstance();
		//calendar.setLenient(true);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, mill);
		return calendar;
	}

	/**
	 * @param queryList
	 * @param sqlBuffer
	 */
	private void getSql(List queryList, StringBuffer sqlBuffer) {
		for (int i = 0; i < queryList.size(); i++) {
			Object[] param = (Object[]) queryList.get(i);
			if (i != 0) {
				sqlBuffer.append(" and ");
			}
			sqlBuffer.append(param[0]);
		}
		log.info("get sql is " + sqlBuffer.toString());
	}

	private void setParam(List queryList, PreparedStatement ps)
			throws SQLException {
		int index = 1;
		for (int i = 0; i < queryList.size(); i++) {
			Object[] param = (Object[]) queryList.get(i);
			if (param[1] != NOPARAM) {
				if (log.isInfoEnabled()) {
					log.info("set param " + (index) + " " + param[1]);
				}
				ps.setObject(index++, param[1]);
			}
		}
		log.info("get queryList is " + queryList);
	}

	/**
	 * @param groupObj
	 * @param queryList
	 */
	private void addParameter(String param, Object groupObj, List queryList) {
		queryList.add(new Object[] { param, groupObj });
	}
	
	

}
