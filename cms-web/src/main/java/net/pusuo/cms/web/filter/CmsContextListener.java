package net.pusuo.cms.web.filter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class CmsContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {

		//  加载property
		Properties prop = new Properties();
		InputStream inputStream = CmsContextListener.class.getClassLoader().getResourceAsStream("log.properties");
		try {
			prop.load(inputStream);

		} catch (IOException e) {
			System.err.println("load log.properties from classpath err");
			e.printStackTrace();
		}

		String logbackConfigLocation = event.getServletContext().getInitParameter("logbackConfigLocation");
		String fn = event.getServletContext().getRealPath(logbackConfigLocation);
		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

			//  put property
			if (prop.size() > 0) {
				for (Map.Entry<Object, Object> entry : prop.entrySet()) {
					loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
				}
			}
			loggerContext.reset();
			JoranConfigurator joranConfigurator = new JoranConfigurator();
			joranConfigurator.setContext(loggerContext);
			joranConfigurator.doConfigure(fn);
		} catch (JoranException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}
}
