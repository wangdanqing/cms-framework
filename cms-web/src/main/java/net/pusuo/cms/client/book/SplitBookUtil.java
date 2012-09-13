package net.pusuo.cms.client.book;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ClientHttpFile;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.News;

public class SplitBookUtil {

	/**
	 * @param args
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws SQLException, IOException {
//		splitBook("佛本是道.txt",111);
		// StringBuffer sb = new StringBuffer("");
		// for(int i = 0 ; i < list.size(); i++){
		// Book b = (Book)list.get(i);
		// // sb.append("\n" + i + ":" + b.getTitle());
		// sb.append("\n" + b.getTitle() +"\n" + b.getContent());
		// }
		// write("佛本是道2.txt", sb.toString());
	}

	public static void splitBook(String bookName, int pid, String author, String startStr, boolean isreplaceflag) {

		if(StringUtils.isEmpty(bookName) || StringUtils.isEmpty(author)||StringUtils.isEmpty(startStr) || pid <0)return;

		String file = new String(bookName);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String tmp = "";
		StringBuffer result = new StringBuffer();

		try {
			while ((tmp = br.readLine()) != null) {
				tmp = tmp.trim().replaceAll("    ", "");
			//	if((!tmp.startsWith("第") && tmp.length() < 30 && tmp.indexOf("章") >5 && tmp.indexOf("第")>4 )||	( tmp.startsWith("第") && tmp.length() < 30 && tmp.indexOf("章") >5)){
			if (tmp.startsWith(startStr) && tmp.length() < 40 && tmp.length() >7 && tmp.indexOf("章")>2) {
					if(isreplaceflag)
						result.append("|||||||||" + tmp.trim().replaceAll(startStr, "").trim()  + "=========");
					else
						result.append("|||||||||" + tmp.trim().replaceAll("　　", "")  + "=========");
				}else if(tmp!=null && tmp.length()>1){
					tmp = tmp.trim().replace("　　", "");
                                        result.append("<p>　　" + tmp + "</p>\r\n"); 
                                }
			}
			fr.close();
			conventToNews(StringUtils.split(result.toString(), "|||||||||"), pid, author);
			result = null;
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void conventToNews(String[] chapters, int pid, String author) {

		String chapter = "", title = "", content = "";
		News pnews = null;
		for (int i = chapters.length - 1; i > -1; i--) {
			pnews = CoreFactory.getInstance().createNews();
			chapter = chapters[i];
			title = StringUtils.substring(chapter, 0, chapter
					.indexOf("========="));
			content = StringUtils.substring(chapter, chapter
					.indexOf("=========")).replaceAll("=========", "");

			pnews = CoreFactory.getInstance().createNews();
			pnews.setDesc(title.trim());
			pnews.setTemplate("" + 18314);
			pnews.setChannel(116);
			pnews.setPid(pid);
			pnews.setTime(new Timestamp(System.currentTimeMillis()));
			if(StringUtils.isNotEmpty(author))pnews.setAuthor(author);
			pnews.setText(content);
			pnews.setEditor(90);
			pnews.setExt("html");
			pnews.setMedia(4015);
			pnews.setPriority(70);

			ItemManager.getInstance().update(pnews);
			
			if (i % 20 == 0) {
				ClientHttpFile.wgetIfcString("http://cms.pusuo.net:8080/patch/pagination.jsp?pids="+ pid, 3000);
			}
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
	/**
	 * 替换回车换行为段落标记<p></p>
	 * @param text
	 * @return
	private static String addParagraphTag(String text) {
		String str[] = StringUtils.split(text, "\n");
		StringBuffer sb = new StringBuffer();
		for(Object o:str){
			sb.append("<p>　　")
			  .append(o.toString().trim())
			  .append("</p>\r\n");
		}
		return sb.toString();
	}
	 */
}
