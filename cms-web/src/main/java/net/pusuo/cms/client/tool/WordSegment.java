/*
 * Created on 2004-11-11
 * Author: sohu
 */
package net.pusuo.cms.client.tool;

/**
 * @author sohu
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WordSegment {
	int obj;

	static void initLib() {
		System.loadLibrary("segment");	
	}
	static {
		initLib();
	}
	
	public WordSegment(DicTree tree) {
		open(tree.obj);
	}
	
	private native boolean open(int obj);
	
	
	/**
	*	semgment
	*		text ԭʼ�ַ�Ҫ����gchar_t��
	*		len  gchar_t�ĳ��ȡ�ע�����ﲻ��byte����ĳ��ȣ�����˫�ֽ��ֵĳ���
	*		results	�ִʽ��ִʳ�����ÿ��token�������ո������ע��resultsҪ���ȷ���
	*	����ֵ��results�����ʵ�ʳ��ȡ�
	*		
	*/
	public native int segment(byte text[],int len, byte results[]);
	
	public native void deinit();
	
	public void finalnize() {
		deinit();
	}
	
	public static void main(String args[]) {
		DicTree dic = new DicTree();
		WordSegment seg = new WordSegment(dic);
		String text = "�л����񹲺͹���������������";
		byte res[]=new byte[65536];
		byte src[] = text.getBytes();
		int len = seg.segment(src,src.length/2,res);
		
		System.out.println(text+"("+len+")="+new String(res,0,len));
	}

}
