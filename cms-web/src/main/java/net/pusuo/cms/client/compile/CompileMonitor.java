package net.pusuo.cms.client.compile;

import java.io.*;
import java.net.*;

public class CompileMonitor extends Thread
{
	public CompileMonitor()
	{
	}
	private String getUrl( String url )
	{
                String ret = "";
                InputStream in = null;
                ByteArrayOutputStream baos = null;
                try {
                        URL rURL = new URL(url);
                        in = rURL.openConnection().getInputStream();
                        baos = new ByteArrayOutputStream();
                        int len = 0;
                        final int MAXLEN = 1000;
                        byte[] b = new byte[MAXLEN];
                        while ((len = in.read(b)) > 0) {
                                baos.write(b, 0, len);
                        }
                        ret = new String(baos.toString());
                } catch (MalformedURLException me) {
                } catch (IOException ie) {
                } catch (Exception e) {
                } finally {
                        try {
                                in.close();
                                baos.close();
                        } catch (Exception fe) {
                        }
                }
                return ret;
	}

	public void run()
	{
		try{
			getUrl("http://192.168.132.47/compile/compile.jsp?method=100");
                }
		catch(Exception e)
		{
			System.out.println("CompilerMonitor->main(): " + e);
		}
	}

	public static void main(String[] arg)
	{
		try
		{
			CompileMonitor a = new CompileMonitor();
			a.start();
		}
		catch(Exception e)
		{
			System.out.println("CompilerMonitor->main(): " + e);
		}
	}
}
