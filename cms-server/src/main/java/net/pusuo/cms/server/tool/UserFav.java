/**
 * @see UFInterface
 */
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

/**
 * 用户收藏夹实例类.<br/>
 * <p>
 * 生成方法:<br/> UFInterface uf=UserFav.getInstance();
 * </p>
 * 
 * @author DengHua
 * @version 1.5
 * @see UFInterface java.rmi.server.UnicastRemoteObject
 * 
 */
public class UserFav extends UnicastRemoteObject implements UFInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1653247860864636066L;

	private static final Log LOG = LogFactory.getLog(UserFav.class);

	private static UserFav FAV = null;

	private static String PATH = null;

	// 写文件的锁
	static Object FILE_LOCK = new Object();

	static {
		PATH = Configuration.getInstance().get("cms4.userfav.path");
		if (PATH != null) {
			if (!PATH.endsWith(File.separator)) {
				PATH += File.separator;
			}
		}
	}

	private UserFav() throws RemoteException {
	}

	public static UserFav getInstance() {

		if (PATH == null)
			throw new NullPointerException("Config err.");

		if (FAV == null) {
			try {

				if (FAV == null) {
					FAV = new UserFav();
				}

			} catch (RemoteException re) {
				LOG
						.error("unable to create UserFav instance ."
								+ re.toString());
				re.printStackTrace();
				throw new IllegalStateException(
						"unable to create UserFav instance .");
			}
		}

		return FAV;
	}

	/**
	 * @deprecated
	 */
	public List list(String userName) throws RemoteException {

		String fileName = getFileName(userName);
		if (fileName == null)
			return null;

		return load(fileName);
	}

	/**
	 * @deprecated
	 */
	public void add(String userName, int id, String name)
			throws RemoteException {

		String fileName = getFileName(userName);
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

	/**
	 * @deprecated
	 */
	public void add(String userName, List subjects) throws RemoteException {

		String fileName = getFileName(userName);
		if (fileName == null)
			return;

		if (subjects == null || subjects.size() == 0)
			return;

		List oldSubjects = load(fileName);
		if (oldSubjects == null) {
			oldSubjects = new ArrayList();
		}

		for (int i = 0; i < subjects.size(); i++) {
			Subject subject = (Subject) subjects.get(i);
			int subjectId = subject.getId();
			String subjectName = subject.getName();
			if (subjectId < 0 || subjectName == null
					|| subjectName.trim().length() == 0) {
				continue;
			}

			if (!contains(oldSubjects, subjectId)) {
				oldSubjects.add(subject);
			}
		}

		save(fileName, oldSubjects);
	}

	/**
	 * @deprecated
	 */
	public void delete(String userName, int id) throws RemoteException {

		String fileName = getFileName(userName);
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

	/**
	 * @deprecated
	 */
	public void delete(String userName, List ids) throws RemoteException {

		String fileName = getFileName(userName);
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

	private String getFileName(String userName) {

		if (userName == null || userName.trim().length() == 0)
			return null;

		return PATH + userName + ".xml";
	}

	/**
	 * @deprecated
	 * 
	 */
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

	/**
	 * @deprecated
	 * 
	 */
	private boolean save(String fileName, List subjects) {

		boolean result = false;

		FileOutputStream foStream = null;
		OutputStreamWriter osWriter = null;
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("user");

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

			// 编码
			foStream = new FileOutputStream(fileName);
			osWriter = new OutputStreamWriter(foStream, "UTF-8");

			// 格式
			OutputFormat format = OutputFormat.createPrettyPrint();

			// 输出
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

	public void add(String userName, String category, int id, String name)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(userName, true);
			fav.addFavorite(id, name, category);
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}

	/**
	 * @see UFInterface#addCategory(String, String)
	 */
	public void addCategory(String username, String category)
			throws RemoteException {
		if (username == null || category == null)
			throw new RemoteException("参数传递错误");
		try {
			Favorite fav = new Favorite(username, true);
			fav.addCategory(category);
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}

	public void delete(String userName, String category, List ids)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(userName, true);
			for (int i = 0; i < ids.size(); i++) {
				fav.deleteFavorite(((Integer) ids.get(i)).intValue(), category);
			}

		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}

	}

	/**
	 * 
	 * @see UFInterface#deleteCategory(String, String)
	 */
	public void deleteCategory(String username, String category)
			throws RemoteException {

		try {
			Favorite fav = new Favorite(username, true);
			fav.deleteCategory(category);
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}

	public void deleteCategory(String username, List names)
			throws RemoteException {
		try {
			Favorite fav = new Favorite(username, true);
			for (int i = 0; i < names.size(); i++) {
				fav.deleteCategory((String)names.get(i));
			}
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}

	public List list(String userName, String category) throws RemoteException {
		List result = null;
		try {
			Favorite fav = new Favorite(userName, true);
			result = fav.listFavorite(category);
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
		return result;
	}

	public List listCategory(String userName) throws RemoteException {
		List a = null;
		try {
			Favorite fav = new Favorite(userName, true);
			a = fav.listCategory();
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
		return a;

	}


}
