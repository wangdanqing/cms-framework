package net.pusuo.cms.tools;

/**
 * $Id: VelocityUtil.java 11204 2011-11-03 10:09:54Z nimin $
 * All Rights Reserved.
 */

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.*;

/**
 * Velocity模板工具类
 *
 * @author <a href="mailto:minni@sohu-inc.com">NiMin</a>
 * @version 1.0 2011-10-28 16:04:20
 */
public class VelocityUtil {

    private SmcVelocityEngine engine = SmcVelocityEngine.getInstance();

    /**
     * Velocity容器
     */
    private VelocityContext context;
    /**
     * 模板
     */
    private Template template;

    public VelocityUtil() {
        this.context = new VelocityContext();
    }

    /**
     * 往容器中添加内容
     *
     * @param key
     * @param value
     */
    public void setContextValue(String key, Object value) {
        context.put(key, value);
    }

    public void clearContext() {
        Object[] keys = context.getKeys();
        for (Object obj : keys) {
            context.remove(obj);
        }
    }

    /**
     * 设置Velocity模板
     *
     * @param tplName
     */
    public void setTemplate(String tplName) {
        try {
            if (!tplName.endsWith(".vm")) {
                tplName += ".vm";
            }
            template = engine.getTemplate(tplName);
        } catch (ResourceNotFoundException e1) {
            throw new RuntimeException("[" + tplName + "] can not found.");
        } catch (ParseErrorException e2) {
            throw new RuntimeException("template parse exception.");
        } catch (Exception e3) {
            throw new RuntimeException("velocity set template exception.");
        }
    }

    /**
     * 合并数据到模板，以字符串形式返回
     */
    public String mergeToString() {
        String result = "";

        Writer writer = new StringWriter();
        try {
            template.merge(context, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 合并数据到模板并写回文件
     *
     * @param filePathName
     */
    public void mergeToFile(String filePathName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePathName));
            template.merge(context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
