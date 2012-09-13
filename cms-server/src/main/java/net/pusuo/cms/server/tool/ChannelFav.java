package net.pusuo.cms.server.tool;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.core.CoreFactory;
import net.pusuo.cms.server.core.Subject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChannelFav extends UnicastRemoteObject implements CFInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3692294472812847180L;

	private static final Log LOG = LogFactory.getLog(ChannelFav.class);

	private static ChannelFav FAV = null;

	private static String PATH = null;
	static {
		PATH = Configuration.getInstance().get("cms4.channelfav.path");
		if (PATH != null) {
			if (!PATH.endsWith(File.separator)) {
				PATH += File.separator;
			}
		}
	}

	private ChannelFav() throws RemoteException {
	}

	public static ChannelFav getInstance() {

		if (PATH == null)
			throw new NullPointerException("Config err.");

		if (FAV == null) {
			try {
				synchronized (ChannelFav.class) {
					if (FAV == null) {
						FAV = new ChannelFav();
					}
				}
			} catch (RemoteException re) {
				LOG.error("unable to create ChannelFav instance ."
						+ re.toString());
				throw new IllegalStateException(
						"unable to create ChannelFav instance .");
			}
		}

		return FAV;
	}

	public List list(String channelName) throws RemoteException {

		String fileName = getFileName(channelName);
		if (fileName == null)
			return null;

		return load(fileName);
	}

	public void add(String channelName, int id, String name)
			throws RemoteException {

		String fileName = getFileName(channelName);
		if (fileName == null)
			return;

		if (id < 0 || name == null || name.trim().length() == 0)
			return;

		List subjects = load(fileName);
		if (subjects != null) {
			if (contains(subjects, id)) {
				return;
			}
		} else {
			subjects = new ArrayList();
		}

		Subject subject = CoreFactory.getInstance().createSubject();
		subject.setId(id);
		subject.setName(name);

		subjects.add(subject);

		save(fileName, subjects);
	}

	public void delete(String channelName, int id) throws RemoteException {

		String fileName = getFileName(channelName);
		if (fileName == null)
			return;

		if (id < 0)
			return;

		List subjects = load(fileName);
		if (subjects != null) {
			int index = -1;
			for (int i = 0; i < subjects.size(); i++) {
				Subject subject = (Subject) subjects.get(i);
				int subjectId = subject.getId();
				if (subjectId == id) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				subjects.remove(index);
				save(fileName, subjects);
			}
		}
	}

	public void delete(String channelName, List ids) throws RemoteException {

		String fileName = getFileName(channelName);
		if (fileName == null)
			return;

		if (ids == null || ids.size() == 0)
			return;

		List subjects = load(fileName);
		if (subjects != null) {
			List indexs = new ArrayList();
			for (int i = 0; i < subjects.size(); i++) {
				Subject subject = (Subject) subjects.get(i);
				int subjectId = subject.getId();

				for (int j = 0; j < ids.size(); j++) {
					int id = ((Integer) ids.get(j)).intValue();
					if (subjectId == id) {
						indexs.add(new Integer(i));
					}
				}
			}
			if (indexs.size() > 0) {
				for (int i = indexs.size() - 1; i > -1; i--) {
					int index = ((Integer) indexs.get(i)).intValue();
					subjects.remove(index);
				}
				save(fileName, subjects);
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////

	private boolean contains(List subjects, int id) {

		boolean result = false;

		for (int i = 0; i < subjects.size(); i++) {
			Subject subject = (Subject) subjects.get(i);
			int subjectId = subject.getId();
			if (subjectId == id) {
				result = true;
				break;
			}
		}

		return result;
	}

	private String getFileName(String channelName) {

		if (channelName == null || channelName.trim().length() == 0)
			return null;

		return PATH + channelName + ".xml";
	}

	private List load(String fileName) {

		File file = new File(fileName);
		if (!file.exists() || !file.canRead() || !file.isFile())
			return null;

		List subjects = new ArrayList();

		FileInputStream fiStream = null;
		InputStreamReader isReader = null;
		try {
			SAXReader reader = new SAXReader();

			fiStream = new FileInputStream(file);
			isReader = new InputStreamReader(fiStream, "UTF-8");
			Document document = reader.read(isReader);

			Element root = document.getRootElement();
			Iterator favoriteIter = root.elementIterator("favorite");
			while (favoriteIter.hasNext()) {
				Element favoriteElement = (Element) favoriteIter.next();
				Subject subject = CoreFactory.getInstance().createSubject();

				int id = getAttrIntValue(favoriteElement, "id", -1);
				String name = getAttrStringValue(favoriteElement, "name", null);

				subject.setId(id);
				if (name != null)
					subject.setName(name);

				subjects.add(subject);
			}
		} catch (Exception e) {
			subjects = null;
			LOG.error(e);
		} finally {
			try {
				if (isReader != null) {
					isReader.close();
					isReader = null;
				}
				if (fiStream != null) {
					fiStream.close();
					fiStream = null;
				}
				if (file != null)
					file = null;
			} catch (Exception e) {
				LOG.error(e);
			}
		}

		return subjects;
	}

	private boolean save(String fileName, List subjects) {

		boolean result = false;

		FileOutputStream foStream = null;
		OutputStreamWriter osWriter = null;
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("channel");

			if (subjects != null) {
				for (int i = 0; i < subjects.size(); i++) {
					Subject subject = (Subject) subjects.get(i);
					int id = subject.getId();
					String name = subject.getName();
					if (id < 0 || name == null || name.trim().length() == 0)
						continue;

					Element favoriteElement = root.addElement("favorite");
					favoriteElement.addAttribute("id", id + "");
					favoriteElement.addAttribute("name", name);
				}
			}

			// ����
			foStream = new FileOutputStream(fileName);
			osWriter = new OutputStreamWriter(foStream, "UTF-8");

			// ��ʽ
			OutputFormat format = OutputFormat.createPrettyPrint();

			// ���
			XMLWriter writer = new XMLWriter(osWriter, format);
			writer.write(root);
			writer.close();

			result = true;
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

	private static int getAttrIntValue(Element element, String attrName,
			int defaultValue) {

		Attribute attr = element.attribute(attrName);

		if (attr == null)
			return defaultValue;

		if (attr.getValue() != null) {
			try {
				return Integer.parseInt(attr.getValue());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}

		return defaultValue;
	}

	private static String getAttrStringValue(Element element, String attrName,
			String defaultValue) {

		Attribute attr = element.attribute(attrName);

		if (attr == null)
			return defaultValue;

		if (attr.getValue() != null) {
			return attr.getValue();
		}

		return defaultValue;
	}

	public void add(String channelName, int id, String name, String categoryName)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			fav.addFavorite(id, name, categoryName);
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}

	}

	public void delete(String channelName, int id, String categoryName)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			fav.deleteFavorite(id, categoryName);
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}

	}

	public void delete(String channelName, List ids, String categoryName)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			for (int i = 0; i < ids.size(); i++) {
				Integer a = (Integer) ids.get(i);
				fav.deleteFavorite(a.intValue(), categoryName);
			}
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}

	}

	public List list(String channelName, String category)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			return fav.listFavorite(category);
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}
	}

	public List listCategory(String channelName) throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			return fav.listCategory();
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}
	}

	public void addCategory(String channelName, String categoryName)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			fav.addCategory(categoryName);
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}

	}

	public void deleteCategory(String channelName, String categoryName)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			fav.deleteCategory(categoryName);
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}

	}

	public void deleteCategory(String channelName, List names) throws RemoteException {
		try {
			Favorite fav = new Favorite(channelName, false);
			for(int i=0;i<names.size();i++){
				String nn=(String) names.get(i);
				fav.deleteCategory(nn);
			}
		} catch (Exception e) {
			throw new RemoteException("��������", e);
		}
		
	}

}
