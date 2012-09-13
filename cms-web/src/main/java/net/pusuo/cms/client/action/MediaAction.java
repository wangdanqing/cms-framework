package net.pusuo.cms.client.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.client.util.ChannelMediaUtil;
import com.hexun.cms.core.Media;

public class MediaAction extends ItemAction {
	private static final Log LOG = LogFactory.getLog(MediaAction.class);

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward af = super.list(mapping, form, request, response);
		List list = (List) request.getAttribute("list");
		if(list != null){
				// sort
				Collections.sort(list, new Comparator(){
				public int compare(Object arg0, Object arg1) {
					Media m1 = (Media)arg0;
					Media m2 = (Media)arg1;
					if(m1 != null && m2 != null){
						return m2.getTime().compareTo(m1.getTime());
					}
					return 0;
				}
				});
		}
		return af;
	}
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		ChannelMediaUtil.remove(_id);
		return super.remove(mapping,form,request,response);
	}
}
