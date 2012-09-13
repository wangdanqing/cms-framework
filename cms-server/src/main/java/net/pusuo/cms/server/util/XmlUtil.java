package net.pusuo.cms.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * TODO
 * 
 * @author zhuzhangsuo
 * @version 1.0, 2008-3-8
 * @since CMS1.0
 */
public class XmlUtil {

	private static final Log log = LogFactory.getLog(XmlUtil.class);
	public static final String SEPERATOR = System.getProperty("file.separator");

	/**
	 * @author zhuzhangsuo
	 * @param doc
	 * @param output
	 * @param encoding
	 */
	public static boolean write(Document doc, OutputStream output,
			String encoding) {
		OutputFormat style = OutputFormat.createPrettyPrint();
		style.setEncoding(encoding);
		XMLWriter write;
		try {
			write = new XMLWriter(output, style);
			write.write(doc);
			write.close();
			return true;
		} catch (IOException e) {
			log.error(e);
			return false;
		}

	}

	public static boolean write(Document doc, String file, String encoding) {
		OutputFormat style = OutputFormat.createPrettyPrint();
		style.setEncoding(encoding);
		XMLWriter write;
		try {

			write = new XMLWriter(new FileOutputStream(file), style);
			write.write(doc);
			write.close();
			return true;
		} catch (IOException e) {
			log.error(e);
			return false;
		}

	}

	public static boolean write(String str, String file, String encoding) {
		OutputFormat style = OutputFormat.createPrettyPrint();
		style.setEncoding(encoding);
		XMLWriter write;
		FileOutputStream fos = null;
		try {
			int end = file.lastIndexOf(SEPERATOR);
			mkdir(file.substring(0, end));
			fos = new FileOutputStream(file);
		
			write = new XMLWriter(fos, style);
			Document doc=org.dom4j.DocumentHelper.parseText(str);
			write.write(doc);
			write.close();
			return true;
		} catch (IOException e) {
			log.error(e);
			return false;
		} catch (DocumentException e) {
			log.error(e);
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					log.error(e);
				}

			}
		}
	}

	public static boolean write(String str, String file) {

		return write(str, file, "utf-8");
	}

	public static boolean write(Document doc, String file) {
		return write(doc, file, "utf-8");
	}

	public static Document getDocument(String filename) {
		SAXReader r = new SAXReader();
		Document doc = null;

		try {

			doc = r.read(new FileInputStream(filename));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}

		return doc;

	}

	public static void mkdir(String filepath) {
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
