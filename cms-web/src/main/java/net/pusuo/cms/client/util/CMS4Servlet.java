package net.pusuo.cms.client.util;

import org.apache.struts.action.ActionServlet;

import java.net.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.hexun.cms.client.tool.ChannelTreeThread;
import com.hexun.cms.client.compile.CompileMain;

import com.hexun.cms.Configuration;

public class CMS4Servlet extends ActionServlet implements ServletContextListener
{
	private static final long serialVersionUID = 1L;
	
	protected void process(
		javax.servlet.http.HttpServletRequest request, 
		javax.servlet.http.HttpServletResponse response)
	throws java.io.IOException,javax.servlet.ServletException {

		request.setCharacterEncoding("utf-8");
		super.process(request, response);

	}

	public void contextInitialized( ServletContextEvent event )
	{
		// start compile
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			String hostName = localhost.getHostAddress();

			System.out.println("localhost hostaddress: "+hostName);

			String compileMachine = Configuration.getInstance().get("cms4.client.compile.machine");
			System.out.println("compile machine: "+compileMachine);
			
			if( compileMachine!=null && compileMachine.trim().length()>0 )
			{
				//if( compileMachine.indexOf(hostName)>=0 )
				//{
					CompileMain.getInstance().startTask();
					System.out.println("Starting compile.........");
				//}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// context
		ContextUtil.setRootPath(event.getServletContext().getRealPath("/"));
	
		if("true".equals(Configuration.getInstance().get("cms4.channeltreethread.switch"))){
			ChannelTreeThread ct = new ChannelTreeThread();
			ct.start();
		}
		
		// stat.
		if("true".equals(Configuration.getInstance().get("cms4.statisticthread.switch")))
		{ 
			StatisticThread statThread = new StatisticThread();
			statThread.start();
		}
	
	}

	public void contextDestroyed( ServletContextEvent event )
	{
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			String hostName = localhost.getHostName();
			

			String compileMachine = Configuration.getInstance().get("cms4.client.compile.machine");
			if( compileMachine!=null && compileMachine.trim().length()>0 )
			{
				if( compileMachine.indexOf(hostName)>=0 )
				{
					CompileMain.getInstance().stopTask();
					event.getServletContext().log("CMS4 Compiler destroyed.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
}


