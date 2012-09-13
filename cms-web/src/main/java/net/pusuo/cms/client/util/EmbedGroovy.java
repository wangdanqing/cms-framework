/*
 * Created on 2006-3-21
 * Updated on 2007-3-16
 */
package net.pusuo.cms.client.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

/**
 * ��ִ̬��Groovy�ű�
 * 
 * @author agilewang
 */
public class EmbedGroovy {
	private static Log log = LogFactory.getLog(EmbedGroovy.class);

	/**
	 * �����ӦGroovy�ű�Ŀ¼�µ�GroovyScriptEngine
	 */
	private static final ConcurrentHashMap groovyEngines = new ConcurrentHashMap();

	private String groovyDir;

	private String groovyScript;

	private Binding binding = new Binding();

	private GroovyScriptEngine engine = null;

	public void setParameters(Map params) {
		if (params == null || params.size() == 0) {
			return;
		}
		Set paramsSet = params.entrySet();
		Iterator psi = paramsSet.iterator();
		while (psi.hasNext()) {
			Map.Entry entry = (Map.Entry) psi.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			binding.setProperty(key, value);
		}
	}

	public Object getProperty(String name) {
		try{
			return binding.getProperty(name);
		}catch(Throwable te){
			//ignore
		}
		return null;
	}

	public void initial(String scriptName) {
		File scriptFile = new File(scriptName);
		if (!scriptFile.isFile()) {
			throw new IllegalArgumentException(scriptName
					+ " is not a script file.");
		}
		groovyDir = scriptFile.getParent();
		groovyScript = scriptFile.getName();

		if (groovyDir == null) {
			throw new IllegalArgumentException(scriptName
					+ "'s parent is null.");
		}
		engine = (GroovyScriptEngine) groovyEngines.get(groovyDir);
		if (engine == null) {
			if (log.isInfoEnabled()) {
				log.info("Init GroovyScriptEngine for %s" + groovyDir);
			}
			try {
				engine = new GroovyScriptEngine(new String[] { groovyDir });
				groovyEngines.putIfAbsent(groovyDir, engine);
				GroovyScriptEngine old = (GroovyScriptEngine) groovyEngines
						.get(groovyDir);
				if (old != null) {
					engine = old;
				}
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error("Init GroovyScriptEngine for " + groovyDir
							+ " error", e);
				}
				throw new RuntimeException("Init GroovyScriptEngine for "
						+ groovyDir + " error", e);
			}
		}
	}

	public Object run() {
		Object result = null;
		try {
			result = this.engine.run(this.groovyScript, this.binding);
		} catch (Throwable te) {
			if (log.isErrorEnabled()) {
				log.error("Run script " + this.groovyScript + " in dir "
						+ this.groovyDir + " error.", te);
			}
		}
		if (result == null) {
			if (log.isWarnEnabled()) {
				log.warn("Run script " + this.groovyScript + " in dir "
						+ this.groovyDir + " result is null.");
			}
			try {
				result = this.binding.getProperty("result");
			} catch (Throwable te) {
				// ignore
			}
		}
		return result;
	}
}
