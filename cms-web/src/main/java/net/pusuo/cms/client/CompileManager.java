package net.pusuo.cms.client;

import java.util.List;
import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.CProxy;

public class CompileManager
{
	private static final Object lock = new Object();
	private static CompileManager instance;
	private CProxy compileproxy;
	
	private CompileManager(){
		compileproxy = (CProxy)ClientUtil.renewRMI("CompileProxy");
	}

	public static CompileManager getInstance(){
		if(instance==null){
			synchronized(lock){
				if(instance==null) instance = new CompileManager();
			}
		}
		return instance;
	}

	public List getList(int count){
		List list = null;
		try{
			list = compileproxy.getList(count);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public void delete(int count){
		try{
			compileproxy.delete(count);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
