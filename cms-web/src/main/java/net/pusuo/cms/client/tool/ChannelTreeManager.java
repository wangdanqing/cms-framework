package net.pusuo.cms.client.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.client.util.ContextUtil;

public class ChannelTreeManager
{	
	//private static HashMap ctm = new HashMap();
	private static ChannelTreeManager ctm = null;
	
	private static HashMap channelInfo = new HashMap();
	
	private static Log log = LogFactory.getLog(ChannelTreeManager.class);
	
	private static final Object lock = new Object();
	//xml base file
	private static String basepath = null;//"/opt/hexun/cms/web/js/xloadtree/data/";

	
	public static ChannelTreeManager getInstance()
	{
		try {
			if ( ctm==null ){
				synchronized(lock){
					if ( ctm == null ){
						basepath = ContextUtil.getRootPath()+ "js"+File.separator+"xloadtree"+ File.separator +"data" + File.separator;
						ctm = new ChannelTreeManager();
					}
				}
			}
			return ctm;
		} catch ( Exception e ) {
			log.error("Unable to create ChannelTreeManager instance . "+e.toString());
			throw new IllegalStateException("Unable to create ChannelTreeManager instance.");
		}
	}
	public ChannelTreeManager()
	{
		loadAll();
	}
	//��������ڵ�
	public String saveSecondNodes(TreeNodeEntity te)
	{
		if(te == null || te.equals(""))
			return null;
		List hpList = ItemUtil.getEntityChildren(-1,EntityItem.HOMEPAGE_TYPE);
		EntityItem ei = null;
		for(int i = 0; i< hpList.size(); i++)
		{
			ei = (EntityItem) hpList.get(i);
			if(ei.getName().equals(te.getDesc()))
				break;
			else
				ei = null;
		}
		if(ei == null) return null;
		
		Channel ch = (Channel) ItemManager.getInstance().get(new Integer(ei.getChannel()), Channel.class);
		if(ch == null || ch.equals("")) return null;
		String channelDir = ch.getDir();
		//update
		Document doc = (Document) channelInfo.get(channelDir);
		Element root = doc.getRootElement();

		root = mergeSubNodes(root, te.getSubNodes());
		save(channelDir, doc);
		
		return channelDir;
	}
	//��ѯ������Ŀ
	public TreeNodeEntity getSecondNodes(String channelname)
	{
		if(channelname==null || channelname.equals(""))
			return null;
		List hpList = ItemUtil.getEntityChildren(-1,EntityItem.HOMEPAGE_TYPE);
		EntityItem ei = null;
		for(int i = 0; i< hpList.size(); i++)
		{
			ei = (EntityItem) hpList.get(i);
			if(ei.getName().equals(channelname))
				break;
			else
				ei = null;
		}
		if(ei == null) return null;
		Document doc = null;
		Channel ch = (Channel) ItemManager.getInstance().get(new Integer(ei.getChannel()), Channel.class);
		doc = (Document) channelInfo.get(ch.getDir());
		Element root = doc.getRootElement();
		TreeNodeEntity tne = new TreeNodeEntity();
		tne.setDesc("channelname");
		if(root.nodeCount() > 0)
		{
			//װ��TreeNodeEntity
			Iterator it = root.elementIterator();
			Element snode = null;
			List list = new ArrayList();
			while(it.hasNext())
			{
				TreeNodeEntity stne = new TreeNodeEntity();
				snode = (Element) it.next();
				if(StringUtils.isNotEmpty(snode.attributeValue("action")))
					stne.setActionId(snode.attributeValue("action"));
				if(StringUtils.isNotEmpty(snode.attributeValue("tid")))
					stne.setTid(snode.attributeValue("tid"));
				if(StringUtils.isNotEmpty(snode.attributeValue("pid")))
					stne.setPid(snode.attributeValue("pid"));
				if(StringUtils.isNotEmpty(snode.attributeValue("text")))
						stne.setDesc(snode.attributeValue("text"));
				list.add(stne);
			}
			tne.setSubNodes(list);
		}

		return tne;
	}
	
	
	private void loadAll()
	{
		//���ϵͳ����������ΪHOMEPAGE��ʵ��
		List hpList = ItemUtil.getEntityChildren(-1, 5);
		EntityItem hItem = null;
		Iterator hpi = hpList.iterator();
		while (hpi.hasNext()) {
			hItem = (EntityItem)hpi.next();
			if(hItem != null && hItem.getId() > 0){
				Channel c = (Channel)ItemManager.getInstance().get(new Integer(hItem.getChannel()), Channel.class);
				try {
					load(c.getDir(), hItem.getName());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		//��ʱ������
		saveAllTrees();
	}
	/**
	 * ����һ�����������ӽڵ�
	 * @param channelDir
	 * @throws java.rmi.RemoteException
	 */
	public void load(String channelDir, String channelName) throws RemoteException{
		
		Document doc = DocumentHelper.createDocument();
		try{
			//ʹ�� SAXReader ���� XML �ĵ�
			SAXReader saxReader = new SAXReader();
			File file = new File(basepath + channelDir + ".xml");
			Element root = null;
			if(!file.exists())
			{
				try {
					file.createNewFile();
//					doc = saxReader.read(file);
					Element st = DocumentHelper.createElement("tree");
					doc.setRootElement(st);
					root = doc.getRootElement();
					root.addElement("tree");				
					
				} catch (IOException e1) {
					log.error("create file error file=" + file.getPath());
				}
			}else
			{
				try {
					System.out.println("�����ļ�:" + channelDir + ".xml");
					doc = saxReader.read(file);
					root = doc.getRootElement();
				} catch (Exception e) {
					log.error("���أ�"+ channelDir+".xml���?"+ e);
					e.printStackTrace();
				}
			}
			
//			if(root.attributeCount() < 1 && channelName!=null && !channelName.equals(""))
//			{
//				root.addAttribute("tid", String.valueOf(getRandomId()));
//				root.addAttribute("text", channelName);
//				//pid == 0 ��ʾƵ����ڵ�
//				root.addAttribute("pid", "0");
//				root.addAttribute("id", "0");
//			}
			root = iterateNodes(root);
						
			channelInfo.put(channelDir, doc);
			save(channelDir, doc);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * �ݹ����нڵ�
	 * ����Ƿ��нڵ��tidΪ��
	 * @autor: shijinkui
	 */
	private Element iterateNodes(Element r)
	{
		Element root = r;
		Iterator it = root.elementIterator();
		Element tmp = null;
		//����idΪ�յĽڵ�
		while(it.hasNext())
		{
			tmp = (Element)it.next();
			if(tmp.attributeCount()<0)
				tmp.addAttribute("tid", String.valueOf(getRandomId()));
			else
			{
				if(tmp.attributeValue("tid") == null || tmp.attributeValue("tid").equals(""))
					tmp.addAttribute("tid", String.valueOf(getRandomId()));
			}
			if(tmp.nodeCount() > 0)
				iterateNodes(tmp);
		}
		
		return root;
	}
	
	synchronized private int getRandomId()
	{
		String t = LocalFile.read(basepath + "increamentSychnize");
		if(t == null || t.equals(""))
		{
			log.error("�����ļ�[" + basepath + "]increamentSychnize");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int in = Integer.parseInt(t);
		in++;
		LocalFile.write(String.valueOf(in), basepath + "increamentSychnize");
		return in;
	}
	

	
	/**
	 * �������е���
	 * @autor: shijinkui
	 */
	public void saveAllTrees(){

		Iterator it = channelInfo.keySet().iterator();
		String key = null;
		Document value = null;
		while (it.hasNext()) {
			key = (String) it.next();
			value = (Document) channelInfo.get(key);
			//saveing  channeldir <==>  id
			save(key, value);
		}
	}
	
	/**
	 * �������channelTree
	 */
	private void save(String channeldir, Document doc){
		if(doc == null || doc.equals(""))
			return;
		Element root = doc.getRootElement();
		try{

		    String fileName = basepath + channeldir +".xml";
	    
			// ����
		    FileOutputStream foStream = new FileOutputStream(fileName);
		    OutputStreamWriter osWriter = new OutputStreamWriter(foStream, "UTF-8");

			// ��ʽ
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");

			// ���
			XMLWriter writer = new XMLWriter(osWriter, format);
			writer.write(doc);
			writer.close();
	    }catch(IOException e){
	    	e.printStackTrace();
	    	log.error("дxml���?"+e);
	    }
	}
	
	//ҵ��ӿ�
	
	public TreeNodeEntity getTreeItems(String tid, List channelList)
	{
		TreeNodeEntity tne = new TreeNodeEntity();
		Document doc = null;
		Iterator it = channelList.iterator();
		String channelDir = new String();
		while(it.hasNext())
		{
			channelDir = ((Channel) it.next()).getDir();
			doc = (Document) channelInfo.get(channelDir);
			Element el = doc.getRootElement();
			List list = el.selectNodes("//tree[./@tid = '"+tid.trim()+"']");
			if(list.size() > 0){
			
				Element tmp = (Element)list.get(0);
				tne.setActionId(tmp.attributeValue("action"));
				tne.setDesc(tmp.attributeValue("text"));
				tne.setPid(tmp.attributeValue("pid"));
				if(tmp.attributeValue("tid")!=null && !tmp.attributeValue("tid").equals(""))
					tne.setTid(tmp.attributeValue("tid"));
				else
					tne.setTid(String.valueOf(getRandomId()));
				
				//װ���ӽڵ�
				if(tmp.nodeCount()>0)
				{
					List sl = new ArrayList();
					Element sel = null;
					Iterator sit = tmp.elementIterator();
					while(sit.hasNext())
					{
						TreeNodeEntity stne = new TreeNodeEntity();
						sel = (Element) sit.next();
						stne.setActionId(sel.attributeValue("action"));
						stne.setTid(sel.attributeValue("tid"));
						stne.setPid(sel.attributeValue("pid"));
						stne.setDesc(sel.attributeValue("text"));
						sl.add(stne);
					}
					tne.setSubNodes(sl);
				}
				break;
			}
		}
		return tne;
	}
	public String updateTreeItem(TreeNodeEntity tne, List channelList)
	{
		String flag = null;
		if(StringUtils.isEmpty(tne.getTid()))
			return flag;	
		//update
		Document doc = null;
		Iterator it = channelList.iterator();
		String channelDir = new String();
		while(it.hasNext())
		{
			channelDir = ((Channel) it.next()).getDir();
			doc = (Document) channelInfo.get(channelDir);
			Element el = doc.getRootElement();
			List list = el.selectNodes("//tree[./@tid = '"+tne.getTid()+"']");
			if(list.size() > 0)
			{
				Element tmp = (Element)list.get(0);
				//���?�ڵ�
				if(tmp.attributeValue("action")!=null && !tmp.attributeValue("action").equals(tne.getActionId()))
					tmp.attribute("action").setValue(tne.getActionId());
				if(tmp.attributeValue("text")!=null && !tmp.attributeValue("text").equals(tne.getDesc()))
					tmp.attribute("text").setValue(tne.getDesc());
				if(tne.getTid()==null || tne.getTid().equals("") || tne.getTid().equals("-1"))
					tmp.attribute("tid").setValue(String.valueOf(getRandomId()));
				// �����ӽڵ�
				tmp = mergeSubNodes(tmp, tne.getSubNodes());
				flag = channelDir;
				save(channelDir, doc);
				break;
			}
		}
		return flag;
	}
	
	private Element mergeSubNodes(Element elt, List list)
	{
		Element el = elt;
		TreeNodeEntity tne = null;
		//�����ҳ���ɾ��Ԫ��
		List l1 = new ArrayList(),l2 = new ArrayList();
		for(int i = 0; list!=null && i < list.size(); i++)
		{
			tne = (TreeNodeEntity) list.get(i);
			if(StringUtils.isNotEmpty(tne.getTid()) && !tne.getTid().equals("-1"))
			{
				//modify exit element
				List sl = el.selectNodes("//tree[./@tid = '" + tne.getTid() + "']");//�鲻����û����
				if(sl.size() > 0)
				{
					Element tmp = (Element) sl.get(0);
					if(tmp.attributeValue("tid").equals(tne.getTid()))
					{
						if(StringUtils.isNotEmpty(tmp.attributeValue("action")) && !tmp.attributeValue("action").equals(tne.getActionId()))
							tmp.attribute("action").setValue(tne.getActionId());
						if(StringUtils.isNotEmpty(tmp.attributeValue("text")) && !tmp.attributeValue("text").equals(tne.getDesc()))
							tmp.attribute("text").setValue(tne.getDesc());
						if(StringUtils.isNotEmpty(tmp.attributeValue("pid")) && !tmp.attributeValue("pid").equals(tne.getPid()))
							tmp.attribute("pid").setValue(tne.getPid());
						
						l1.add(tmp.attributeValue("tid"));
					}
				}
			}else{
				//add new element
				Element n = el.addElement("tree");
				n.addAttribute("action", tne.getActionId());
				n.addAttribute("text", tne.getDesc());
				if(StringUtils.isNotEmpty(tne.getPid()))
					n.addAttribute("pid", tne.getPid());
				else
					n.addAttribute("pid", "-1");
				n.addAttribute("tid", String.valueOf(getRandomId()));
				l1.add(n.attributeValue("tid"));
			}
		}
		
		//ɾ��ڵ�
		Iterator it = el.elementIterator();
		while(it.hasNext())
		{
			l2.add(((Element) it.next()).attributeValue("tid"));
		}
		if(l2.size() > l1.size())
		{
			l2.removeAll(l1);
			List l3 = new ArrayList();
			for(int i = 0; i < l2.size(); i++)
			{
				l3 = el.selectNodes("//tree[./@tid = '"+l2.get(i) + "']");
				if(l3!=null && !l3.equals("") && l3.size() > 0)
					el.remove((Element)l3.get(0));
			}
		}
		return el;
	}
	
	
}
