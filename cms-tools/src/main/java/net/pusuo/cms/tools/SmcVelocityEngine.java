/**
 * $Id: SmcVelocityEngine.java 9301 2012-12-10 07:52:12Z jingxiaocai $
 * All Rights Reserved.
 */
package net.pusuo.cms.tools;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.net.URL;
import java.util.Properties;


public class SmcVelocityEngine {
    /**
     * Velocity引擎
     */
    private VelocityEngine engine;
    /**
     * 默认的模板文件目录
     */
    private static final String DEFAULT_TEMPLATE_FOLDER = "src/main/webapp/WEB-INF/vm/";

    private static SmcVelocityEngine instance = new SmcVelocityEngine();

    private SmcVelocityEngine() {
        init();
    }

    public static SmcVelocityEngine getInstance() {
        return instance;
    }

    /**
     * Velocity引擎和容器初始化
     */
    private void init() {

        engine = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty(Velocity.ENCODING_DEFAULT, "utf-8");
        props.setProperty(Velocity.INPUT_ENCODING, "utf-8");
        props.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");
        // 资源加载路径
        props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("file.resource.loader.cache", "true");
        props.setProperty("resource.manager.defaultcache.size", "0");
        props.setProperty("file.resource.loader.modificationCheckInterval", "0");
        props.setProperty("runtime.log.logsystem.system.level", "error");
        try {
            engine.init(props);
        } catch (Exception e) {
            throw new RuntimeException("Velocity Engine Initialize Exception.");
        }
    }

    /**
     * 获取模板目录的路径
     *
     * @param tplPath
     * @return
     */
    private String getTemplatePath(String tplPath) {
        String classFileName = VelocityUtil.class.getName().replaceAll("\\.", "/") + ".class";
        ClassLoader classLoader = VelocityUtil.class.getClassLoader();
        if (classLoader == null) {
            throw new RuntimeException("Can not get classloader of class :" + VelocityUtil.class);
        }
        URL url = classLoader.getResource(classFileName);
        if (url == null) {
            throw new RuntimeException("Can not get resource from url :" + classFileName);
        }

        // 当前类文件的路径
        String fileName = url.getFile();
        String[] filePath = fileName.split("!");
        /*
		 * 1. 如果当前类在jar包中，从jar包所在目录逐级往上查找模板目录，找到最近的一个 
		 * 2. 如果当前类不是在jar包中，从当前类所在目录逐级往上查找模板目录，找到最近的一个 
		 * 3. 如果1或2没有找到模板目录，则返回当前项目目录下的模板目录
		 */
        File currentPath = new File(filePath[0].replace("file:", "")).getParentFile();
        File tempFile = null;
        while (currentPath != null) {
            tempFile = new File(currentPath, tplPath);
            if (tempFile.exists()) {
                return tempFile.getAbsolutePath();
            } else {
                currentPath = currentPath.getParentFile();
            }
        }
        // 默认返回当前项目下templatePath指定的目录
        return new File("").getAbsolutePath() + tplPath;
    }

    /**
     * @param tplName
     * @return
     */
    public Template getTemplate(String tplName) {
        return engine.getTemplate(tplName);
    }
}
