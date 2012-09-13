package net.pusuo.cms.client.util;

import java.io.*;

import com.hexun.cms.file.LocalFile;

public class HtmlTagFilter {
	public static void main(String[] args)
	{
		new HtmlTagFilter().execute();
	}
	public void execute()
	{
		long t1 = ct();
		String content = getContent( "/tmp/health.html" );
		long t2 = ct();
		//System.out.println( "Read content from disk: "+(t2-t1) );
		
		content = tag2Lower( content );
		
		long t3 = ct();
		exportContent( content, "/tmp/health.low.html" );
		long t4 = ct();
		//System.out.println( "Write content to disk: "+(t4-t3) );
	}
	public static String tag2Lower( String content )
	{
		long t1 = ct();
		StringBuffer chs = new StringBuffer(content);
		int bidx = -1, eidx = -1;
		for(int i=0; chs!=null&&i<chs.length(); i++)
		{
			char c = chs.charAt(i);
			if( bidx==-1 && c=='<' )
			{
				bidx = i;
			}
			if( bidx>-1 && (c=='>' || c==' ') )
			{
				eidx = i;
			}
				
			if( bidx>-1 && eidx>bidx )
			{
				//System.out.print( "bidx="+bidx+", eidx="+eidx );
				//System.out.print( "    "+ chs.substring(bidx,eidx+1) );
				replaceLower( chs, bidx, eidx );
				bidx = -1; eidx = -1;
			}
		}
		long t2 = ct();
		LocalFile.write("tag2Lower"+(t2-t1)+"\n", "/tmp/log/log_template", true );
		//System.out.println( "String to char[]: "+(t2-t1) );

		return new String( chs );
	}
	private static void replaceLower( StringBuffer chs, int bidx, int eidx )
	{
		String str = chs.substring(bidx,eidx+1);
		chs.replace( bidx, eidx+1, str.toLowerCase() );
	}

	private String getContent( String filename )
	{
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		String ret = null;
		try
		{
			bis = new BufferedInputStream( new FileInputStream(filename) );
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while( (len=bis.read(buf))>0 )
			{
				baos.write( buf,0,len );
			}
			ret = baos.toString();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}finally {
			try
			{
				if( baos!=null ) baos.close();
				if( bis!=null ) bis.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		return ret;
	}
	private void exportContent( String content, String filename )
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( filename );
			byte[] buf = content.getBytes();
			fos.write( buf,0, buf.length );
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			try
			{
				if( fos!=null )fos.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}

	private static final long ct()
	{
		return System.currentTimeMillis();
	}
}
