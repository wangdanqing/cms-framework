package net.pusuo.cms.client.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.core.Author;

public class AuthorAction extends ItemAction {
	private static final Log LOG = LogFactory.getLog(AuthorAction.class);

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ActionForward af = super.list(mapping, form, request, response);
		List list = (List) request.getAttribute("list");
		if(list != null){
				// sort
				Collections.sort(list, new Comparator(){
				public int compare(Object arg0, Object arg1) {
					Author m1 = (Author)arg0;
					Author m2 = (Author)arg1;
					if(m1 != null && m2 != null){
						return m2.getName().compareTo(m1.getName());
					}
					return 0;
				}
				});
		}
		return af;
	}
}
