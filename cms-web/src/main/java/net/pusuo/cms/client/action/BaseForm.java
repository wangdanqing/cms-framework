package net.pusuo.cms.client.action;

import java.io.Serializable;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResults;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.action.ActionMapping;

/**
 * <p>This class extends <strong>DynaValidatorForm</strong> and provides
 * basic field validation based on an XML file.  The key passed into the
 * validator is the action element's 'name' attribute from the
 * struts-config.xml which should match the form element's name attribute
 * in the validation.xml.</p>
 */
public class BaseForm extends DynaValidatorForm {

    /**
      * Validate the properties that have been set from this HTTP request,
      * and return an <code>ActionErrors</code> object that encapsulates any
      * validation errors that have been found.  If no errors are found, return
      * <code>null</code> or an <code>ActionErrors</code> object with no
      * recorded error messages.
      *
      * @param mapping The mapping used to select this instance.
      * @param request The servlet request we are processing.
      * @return <code>ActionErrors</code> object that encapsulates any validation errors.
      */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
	ActionErrors ret = null;
        if (null != method && !method.equals("view") && !method.equals("list") && !method.equals("lib")) {
		ret = super.validate(mapping,request);
        }
	return ret;
    }
   
}

