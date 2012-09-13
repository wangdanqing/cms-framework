/**
 * 
 */
package net.pusuo.cms.client.view;

/**
 * @author Alfred.Yuan
 *
 */
public interface ViewManager extends ViewConstants {

	/**
	 * ͨ�÷���
	 * @param fileName 
	 * @param context 
	 * @return
	 */
	public String getContent(String fileName, ViewContext context);
	
	////////////////////////////////////////////////////////////////////////////
	// �ض�����
	////////////////////////////////////////////////////////////////////////////
	
}
