//==============================================================================
//file :        SpringContainer.java
//project:      poisoning
//
//last change:	date:       $Date: 2009/03/09 08:46:46 $
//           	by:         $Author: cvs $
//           	revision:   $Revision: 1.1.1.1 $
//------------------------------------------------------------------------------
//copyright:	China Java Users Group http://cnjug.dev.java.net
//License:      the Apache License, Version 2.0 (the "License")
//==============================================================================

package net.pusuo.cms.impress.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 针对Spring的包装。
 *
 * @author agilewang
 */

public class SpringContainer {

    private static Object obj = new Object();

    private static SpringContainer instance;

    private static final Log log = LogFactory.getLog(SpringContainer.class);

    /**
     * 默认情况下,Spring配置路径.*
     */
    public static final String DEFAULT_CONFIG_FILES = "classpath:/application-*.xml";

    private ClassPathXmlApplicationContext applicationContext;

    private SpringContainer() {
        initContainer();
    }

    private SpringContainer(String... config) {
        initContainer(config);
    }

    private void initContainer(String... config) {
        log.info("using config file [" + config + "]");
        if (config == null || config.length == 0) {
            log
                    .info("using default config file [" + DEFAULT_CONFIG_FILES
                            + "]");
            initContainer(DEFAULT_CONFIG_FILES);
        } else {
            this.applicationContext = new ClassPathXmlApplicationContext(config);
        }

    }

    /**
     * 得到默认的容器类.
     * <p/>
     * 默认情况下,在classpath:/config/spring/application-*.xml下面查找文件.
     *
     * @return Spring容器.
     */
    public static SpringContainer getInstance() {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    if (log.isInfoEnabled()) {
                        log.info("inital spring container.");
                    }
                    instance = new SpringContainer();
                }
            }
        }
        return instance;

    }

    /**
     * 根据给定的配置文件来生成容器..
     *
     * @param config 配置文件.
     * @return 实例化的Spring容器.
     */
    public static SpringContainer getInstance(String... config) {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {

                    if (log.isInfoEnabled()) {
                        log.info("inital spring container.");
                    }
                    instance = new SpringContainer(config);

                }
            }
        }
        return instance;

    }

    /**
     * 得到Spring的ApplicationContext。
     *
     * @return spring的context。
     */
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
