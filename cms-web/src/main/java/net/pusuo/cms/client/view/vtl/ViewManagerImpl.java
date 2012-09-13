/**
 * 
 */
package net.pusuo.cms.client.view.vtl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewManager;

/**
 * @author Alfred.Yuan
 *
 */
public class ViewManagerImpl implements ViewManager {
	
	private static final Log log = LogFactory.getLog(ViewManagerImpl.class);
	
	private static final String PROPERTIES_FILE_NAME = "/velocity.properties";
	
	public ViewManagerImpl() {
		
		InputStream in = null;
		try {
			in = ViewManagerImpl.class.getResourceAsStream(PROPERTIES_FILE_NAME);
			Properties properties = new Properties();
			properties.load(in);
			
			Velocity.init(properties);
		} catch (Exception e) {
			log.error("Velocity init error." + e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch ( IOException ioe ) {
				log.error("could not close stream on config file ", ioe);
			}
		}
	}

	public String getContent(String fileName, ViewContext context) {
		
		long timeStart = System.currentTimeMillis();
		
		if (!Velocity.resourceExists(fileName))
			return null;
		if (context == null)
			return null;
		
		VelocityContext velocityContext = context.getContext();

		StringWriter writer = new StringWriter();
		try {
			Template template = Velocity.getTemplate(fileName);
			template.merge(velocityContext, writer);
		} catch (ResourceNotFoundException rnfe) {
			log.error("couldn't find the template.(fileName=" + fileName + ")");
		} catch (ParseErrorException pee) {
			log.error("problem parsing the template.(fileName=" + fileName + ")");
		} catch (MethodInvocationException mie) {
			log.error("something invoked in the template.(fileName=" + fileName + ")");
		} catch (Exception e) {
			log.error("handle template error.(fileName=" + fileName + ")");
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("Invoking velocity: (cost=" + (timeEnd - timeStart) + ")(file=" + fileName + ")");
		
		return writer.toString();
	}

}
