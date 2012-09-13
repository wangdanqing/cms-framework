package net.pusuo.cms.client.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Subject;
import com.hexun.cms.tool.CFInterface;
import com.hexun.cms.tool.UFInterface;
import com.hexun.cms.util.Util;

public class Favorites {

	// private static final Log log = LogFactory.getLog(Favorites.class);

	private static Favorites instance = new Favorites();;

	private Map channelMap = new HashMap();

	private Object channelMapLock = new Object();

	private Map userMap = new HashMap();

	private Object userMapLock = new Object();

	private Favorites() {
	}

	public static Favorites getInstance() {
		return instance;
	}

	public List listUserParents(String userName) {
		if (userName == null || userName.trim().length() == 0)
			return null;

		if (userMap.containsKey(userName))
			return (List) userMap.get(userName);
		else {
			return initUserParents(userName);
		}
	}

	public List getChannelParents(String channels) {
		List result = new ArrayList();
		if (channelMap.containsKey(channels)) {
			List a = (List) channelMap.get(channels);
			if (a != null && a.size() > 0)
				return a;
			else
				return initChannelParents(channels);
		} else {
			return initChannelParents(channels);
		}
	}

	// //////////////////////////////////////////////////////////////////////////

	public List initUserParents(String userName) {

		UFInterface uf = (UFInterface) ClientUtil.renewRMI("UserFav");
		List aaa = new ArrayList();
		try {
			List a = uf.listCategory(userName);
			for (int i = 0; i < a.size(); i++) {
				Subject s = (Subject) a.get(i);
				String categoryName = s.getName();
				List f = uf.list(userName, categoryName);
				if (f.size() > 0) {
					CategoryEntity ce = new CategoryEntity();
					ce.setName(categoryName);
					ce.setFavorite(f);
					aaa.add(ce);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		synchronized (userMapLock) {
			userMap.put(userName, aaa);
		}
		return aaa;

	}

	public List initChannelParents(String channelName) {
		CFInterface cf = (CFInterface) ClientUtil.renewRMI("ChannelFav");
		List aaa = new ArrayList();
		try {
			List a = cf.listCategory(channelName);
			for (int i = 0; i < a.size(); i++) {
				Subject s = (Subject) a.get(i);
				String categoryName = s.getName();
				List f = cf.list(channelName, categoryName);
				if (f.size() > 0) {
					CategoryEntity ce = new CategoryEntity();
					ce.setName(categoryName);
					ce.setFavorite(f);
					aaa.add(ce);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		synchronized (channelMapLock) {
			channelMap.put(channelName, aaa);
		}
		return aaa;
	}

	public String getHTMLString(Authentication auth, String menuName) {
		String result ="";
		List channels = auth.getChannelList();
		for (int i = 0; i < channels.size(); i++) {
			Channel item = (Channel) channels.get(i);
			List a = Favorites.getInstance().getChannelParents(item.getDir());
			if (a != null && a.size() > 0) {
				result += "var tt" + i + "=new WebFXMenu;";
				for (int k = 0; k < a.size(); k++) {
					CategoryEntity entity = (CategoryEntity) a.get(k);
					String categoryName = entity.getName();
					List favList = entity.getFavorite();
					if (favList != null && favList.size() > 0) {
						result += "var cc" + k + "=new WebFXMenu;";
						for (int j = 0; j < favList.size(); j++) {
							Subject ff = (Subject) favList.get(j);
							result += "cc" + k + ".add(new WebFXMenuItem('"
									+ ff.getName()
									+ "',\"javascript:showMark('"
									+ ff.getName() + "',null,null,"+ff.getId()+");\"));";
						}
						result += "tt" + i + ".add(new WebFXMenuItem('"
								+ categoryName + "',null,null,cc" + k + "));";
					}
				}
				result += menuName + ".add(new WebFXMenuItem('"
						+ item.getDesc() + "' , null, null, tt" + i + "));";
			}
		}
		
		//����Ϊծȯ �ڻ����������� ծȯ �ڻ�Ƶ����������ʾ�Ĳ�ͬ�ĸ������б������
		for (int i = 0; i < channels.size(); i++) {
			Channel item = (Channel) channels.get(i);
			List a = null;
			if( item.getId() == 116 ){//�ڻ�Ƶ��
				a=Favorites.getInstance().getChannelParents( "push_futures" );
			}else if( item.getId() == 118 ){//��ָ�ڻ�Ƶ��
				a=Favorites.getInstance().getChannelParents( "push_qizhi" );
			}else if( item.getId() == 121 ){//����Ƶ��
				a=Favorites.getInstance().getChannelParents( "push_bank" );
			}else if( item.getId() == 122 ){//���Ƶ��
				a=Favorites.getInstance().getChannelParents( "push_funds" );
			}
		
			if(a!=null&&a.size()>0){
				result += "var ptt"+i+"=new WebFXMenu;";
				for(int k=0;k<a.size();k++){
					CategoryEntity entity=(CategoryEntity)a.get(k);
					String categoryName=entity.getName();
					List favList=entity.getFavorite();
					if(favList!=null&&favList.size()>0){
						result += "var pcc"+k+"=new WebFXMenu;";
						for(int j=0;j<favList.size();j++){
							Subject ff=(Subject)favList.get(j);
							result += "pcc"+k+".add(new WebFXMenuItem('"+ff.getName()+"',\"javascript:showMark('"+ff.getName()+"');\"));";
						}
						result += "ptt"+i+".add(new WebFXMenuItem('"+categoryName+"',null,null,pcc"+k+"));";
					}
				}
				result += "myMenu.add(new WebFXMenuItem('"+item.getDesc()+Util.unicodeToGBK("����")+"' , null, null, ptt"+i+"));";
			}
		}
		//����Ϊծȯ �ڻ�����������END
	
	/*���ṩ�����ղؼУ�comment by xulin 2008.03.12
		List uList = Favorites.getInstance()
				.listUserParents(auth.getUserName());
		if (uList != null && uList.size() > 0) {
			result += "var ucc=new WebFXMenu;";
			for (int i = 0; i < uList.size(); i++) {
				CategoryEntity entity = (CategoryEntity) uList.get(i);
				String categoryName = entity.getName();
				List uf = entity.getFavorite();
				if (uf != null && uf.size() > 0) {
					result += "var uc" + i + "=new WebFXMenu;";
					for (int j = 0; j < uf.size(); j++) {
						Subject ff = (Subject) uf.get(j);
						result += "uc" + i + ".add(new WebFXMenuItem('"
								+ ff.getName() + "',\"javascript:showMark('"
								+ ff.getName() + "',null,null,"+ff.getId()+");\"));";
					}
					result += "ucc.add(new WebFXMenuItem('" + categoryName
							+ "',null,null,uc" + i + "));";
				}
			}
			result += menuName
					+ ".add(new WebFXMenuItem('"+Util.unicodeToGBK("�Զ���")+"' , null, null, ucc));";
		}
	*/
		return result;
	}
}
