/*
 * Created on 2005-7-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;

import com.hexun.cms.file.HttpFile;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StockUtil {
	
	/**
	 * make content canonical
	 * @param content
	 * @return
	 */
	public static String canonicalizeContent(String content)
	{
	    if(content == null || content.trim().equals(""))
	        return "";
	    else
	    	content = content.trim();
	    content = java.net.URLEncoder.encode(content);
	    String inputData = "VTI-GROUP=0&text=" + content;
	    try {
	    	String formatURL = "http://192.168.105.97/publish/reg.php";
	    	content = HttpFile.getPost(formatURL, inputData);
	    } 	
	    catch (Exception e) 
		{ 
	    }
	    
	    //content = ReplaceSBCcase(content);
	    content = convert2html(content, true);
	
	    return content;
	}
	
	/**
	 * �����е����֣���ȫ��תΪ���
	 * @param content
	 * @return
	 */
	private static String ReplaceSBCcase(String content)
	{
		Map sbc2dbc = new HashMap();
		sbc2dbc.put("��", "1");
		sbc2dbc.put("��", "2");
		sbc2dbc.put("��", "3");
		sbc2dbc.put("��", "4");
		sbc2dbc.put("��", "5");
		sbc2dbc.put("��", "6");
		sbc2dbc.put("��", "7");
		sbc2dbc.put("��", "8");
		sbc2dbc.put("��", "9");
		sbc2dbc.put("��", "0");
		
		Iterator iter = sbc2dbc.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			content = replaceAll(content, key, value);
		}
		
		return content;
	}
	
	/**
	 * 
	 * @param content
	 * @param to
	 * @return
	 */
	private static String convert2html(String content, boolean flag)
	{
		if (content == null)
			return content;
	    if(content.toLowerCase().indexOf("<br>") > -1) 
	    	return content;
	    if(content.toLowerCase().indexOf("<p>") > -1) 
	    	return content;
		
	    content = clearPreBlank(content);
	    content = clearParagraphBlank(content);
	    
	    if (!flag)
	    {
	        if(content.indexOf("����") > -1)
	        	content = replaceAll(content, "����", "");
	        content = "����" + replaceAll(content, "\n", "<br>����");
	        return content;	    	
	    }
	    
	    //content = replaceAll(content, "��\r\n", "��\r\n\r\n");
	    //content = replaceAll(content, "��\r\n", "��\r\n\r\n");
	    //content = replaceAll(content, "��\r\n", "��\r\n\r\n");
	    //content = replaceAll(content, "��\r\n", "��\r\n\r\n");
	    
	    if(content.indexOf("����") > -1)
	    	content = replaceAll(content, "����", "");
	    
	    content = replaceAll(content, "\r\n\r\n\r\n\r\n", "</p><p>����");
	    content = replaceAll(content, "\r\n\r\n\r\n", "</p><p>����");
	    
	    if(hasSingleEnter(content))
	    {
	    	content = replaceAll(content, "\r\n\r\n", "</p><p>����");
	    	content = "<p>����" + replaceAll(content, "\r\n", "</p><p>����") + "</p>";
	    }
	    else
	    {
	    	content = "<p>����" + replaceAll(content, "\r\n\r\n", "</p>\r\n<p>����") + "</p>";
	    }
	    
		return content;
	}
	
	/**
	 * ɾ���ı�ͷ�Ŀո�(����ȫ�ǺͰ��)�Լ��س����з�
	 * @param content
	 * @return
	 */
	private static String clearPreBlank(String content)
	{
        String dde = content;
        
        while(dde.indexOf("\r\n") == 0 || dde.indexOf(" ") == 0 || dde.indexOf("��") == 0)
        {
	        if(dde.indexOf("\r\n") == 0)      
	        	dde = replaceAll(dde, "\r\n", "");
	        if(dde.indexOf(" ") == 0)         
	        	dde = replaceAll(dde, " ", "");
	        if(dde.indexOf("��") == 0)        
	        	dde = replaceAll(dde, "��", "");
        }
        
        return dde;
	}
	
	/**
	 * �����׵Ŀո�(����ȫ�ǺͰ��)
	 * @param content
	 * @return
	 */
	private static String clearParagraphBlank(String content)
	{
        String dde = content;
        
        while(dde.indexOf(" \r\n") > -1 || dde.indexOf("��\r\n") > -1)
        {
	        if(dde.indexOf(" \r\n") > -1)     
	        	dde = replaceAll(dde, " \r\n", "\r\n");
	        if(dde.indexOf("��\r\n") > -1)    
	        	dde = replaceAll(dde, "��\r\n", "\r\n");
        }
        
        dde = replaceAll(dde, "\r\n ", "\r\n");
        
        return dde;
	}
	
	/**
	 * �ж��ı��ǲ����е��س����з�ͬʱ���ֲ���Ӳ�س��ı�
	 * @param content
	 * @return
	 */
	private static boolean hasSingleEnter(String content)
	{
        String dde = content;
        int mlen = 0;
        int start = 0;
        int mstart = dde.indexOf("\r", start);
        while (mstart > -1)
        {
        	int sublen = dde.substring(start, start + mstart).length();
	        if(sublen > mlen)
	        	mlen = sublen;
	        start = start + mstart;
	        mstart = dde.indexOf("\r", start);
        }
        
        boolean ret = false;
        if (mlen > 100)
        	ret = true;
        
		return ret;
	}
	
	/**
	 * Replaces each substring of text that matches the given 
	 * regular expression with the given replacement.
	 * Method java.lang.String.replaceAll(...) has some problem. 
	 * @param text
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static String replaceAll(String text, String regex, String replacement)
	{
		if (text == null || regex == null || replacement == null)
			return text;
		
	    Pattern pattern = null;
	    try 
		{
		    PatternCompiler compiler = new Perl5Compiler();	
		    int caseMask = Perl5Compiler.CASE_INSENSITIVE_MASK;	
	        pattern = compiler.compile(regex, caseMask);
	    } 
	    catch (MalformedPatternException e)
		{
	        return text;
	    }
		
	    PatternMatcher matcher = new Perl5Matcher();
	    Substitution sub = new Perl5Substitution(replacement);
	    
	    String result = Util.substitute(matcher, pattern, sub, text, Util.SUBSTITUTE_ALL);
	    
		return result;
	}

	public static void main(String[] args)
	{
		String content = null;
		content = "�������飺Ш���������β���Ʊ����:2A��41����ӹ������ײ�ȷ�������ڼ���";
		content = canonicalizeContent(content);
		System.out.println(content);
	}
}
