/*
 * Created on 2008-01-11
 */
package net.pusuo.cms.client.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import net.paoding.analysis.analyzer.PaodingTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.hexun.cms.util.Util;

/**
 * 为新闻页面提供关键词
 * 
 * @author xulin
 */
public class KeyWordUtil {
	private static final Log LOG = LogFactory.getLog(KeyWordUtil.class);

	/**
	 * keyword interface:http://dic.tool.hexun.com/cgi-bin/mmseg/mmseg_cpp.cgi
	 */
	//private static final String RD_KEYWORD = Configuration.getInstance().get("cms4.rd.keyword");
	private static final String RD_KEYWORD = "http://dic.tool.caing.com/cgi-bin/mmseg/mmseg_cpp.cgi";

	private static final int RD_INTERFACE_TIMEOUT = 2000;
	static {
		if (LOG.isInfoEnabled()) {
			LOG.info("RD_KEYWORD:" + RD_KEYWORD);
		}
	}

	public static String getKeyword(String newsDesc, String newsText) {
		return rdHttp(newsDesc, newsText);
	}
	
	/**
	 * 庖丁分词
	 * @param newsDesc
	 * @param newsText
	 * @return
	 */
	public String getKeywordPoading(String newsDesc, String newsText) {
		String result = "";
		try {
			List<KeyWord> list = analyzer(newsDesc+"  "+newsText);
			for(int i = 0; i<list.size()&&i<6; i++){
				result+=list.get(i).getKeyword()+" ";
				System.out.println("paoding analyzer:"+list.get(i).getKeyword()+"--"+ list.get(i).getCount());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 分词
	 */
	List<KeyWord> analyzer(String text) throws IOException{
		List<KeyWord> list  = new ArrayList<KeyWord>();
		if(text==null || text.equals(""))return null;
		Analyzer analyzer = new PaodingAnalyzer();
		PaodingTokenizer pt = (PaodingTokenizer) analyzer.tokenStream("", new StringReader(text));
		Token to = null;
		List<String> tmpl = new ArrayList<String>();
		while ((to = pt.next()) != null) {
			if (to.termText().length()<2) continue;
			if(tmpl.contains(to.termText())){
				for(int i = 0; i< list.size(); i++){
					if(list.get(i).getKeyword().equals(to.termText())){
						list.get(i).count();
						break;
					}
				}
			}else{
				tmpl.add(to.termText());
				list.add(new KeyWord(to.termText().trim()));
			}
		}
		return list;
	}
	
	private class KeyWord{
		private String str = null;
		private int count = 0;
		KeyWord(String str){
			this.str = str;
			this.count++;
		}
		KeyWord(){super();}
		
		public String getKeyword(){
			return str;
		}
		
		public void setKeyword(String str){
			this.str = str;
		}
		
		public int getCount(){
			return this.count;
		}
		
		public void count(){
			this.count++;
		}
	}

	/**
	 * @param newsDesc
	 * @param newsText
	 * @return
	 */
	private static String rdHttp(String newsDesc, String newsText) {
		String newsKeyword = null;
		try {
			Map params = new HashMap();
			params.put("title", Util.GBKToUnicode(newsDesc));
			params.put("content", Util.GBKToUnicode(newsText));
			params.put("max_count", "2");
			long timeStart = System.currentTimeMillis();
			String xmlText = com.hexun.cms.client.util.ClientHttpFile.wgetIfcString(RD_KEYWORD, params,RD_INTERFACE_TIMEOUT);
			long timeEnd = System.currentTimeMillis();
			LOG.info( "get keyword from rd is: " + (timeEnd - timeStart) );
			newsKeyword = parseKeywordByDom4j(xmlText);
			if (newsKeyword != null && newsKeyword.trim().length() != 0) {
				newsKeyword = newsKeyword.replaceAll("\"", "");
				newsKeyword = newsKeyword.replaceAll("'", "");
			}
		} catch (Throwable te) {
			if (LOG.isErrorEnabled()) {
				LOG.error("get KeyWord from http error:", te);
			}
		}
		return newsKeyword;
	}

	private static String parseKeywordByDom4j(String xmlText) {
		if (xmlText == null)
			return null;
		String ret = "";
		try {
			Document doc = DocumentHelper.parseText(xmlText);
			Element root = doc.getRootElement();
			List kwNodeList = root.selectNodes("/data/keyword");
			/*
			for (int i = 0; kwNodeList != null && i < kwNodeList.size(); i++) {
				ret +=  ((Node)kwNodeList.get(i)).getText()+" ";
			}
			*/
			//只用一个
			if ( kwNodeList != null && kwNodeList.size()>0 ) {
				ret =  ((Node)kwNodeList.get(0)).getText();
			}
			
			
			return ret.trim();
		} catch (DocumentException e) {
			LOG.error("keyword DocumentException is:"+e.toString());
			return null;
		} catch (Exception e) {
			LOG.error("keyword Exception is:"+e.toString());
			return null;
		}
	}
}
