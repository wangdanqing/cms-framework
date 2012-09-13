package net.pusuo.cms.client.taglib;

public interface FragTag 
{
	public static final int SFRAG_TYPE = 1;
	public static final int ADFRAG_TYPE = 2;
	public static final int DFRAG_TYPE = 3;
	
	public static final int COMPILE_VIEW = 1;
	public static final int PRE_VIEW = 2;
	public static final int FRAG_VIEW = 3;
	public static final int PRECOMPILE_VIEW = 4;

	// added by wangzhigang  for ��ҳģ�嶨ʱ����
	// LIST_VIEW ��ҪĿ�ľ��ǰ��ض���������Զ��б�, NewsListTag
	public static final int LIST_VIEW = 5;
	
	public static final String ENTITY_KEY = "com.hexun.cms.core.EntityItem";
	public static final String TEMPLATE_KEY = "com.hexun.cms.core.Template";

	public static final String PRECOMPILE_FRAGLIST_KEY = "pre_compile.frag_list";

	public String getName();

	public int getType();

	public String getDesc();

	public int getView();

	public int getEntityid();

	public int getTemplateid();

	public void setName(String name);

	public void setType(int type);

	public void setDesc(String desc);

	public void setView(int view);

	public void setEntityid(int entityid);

	public void setTemplateid(int templateid);

	public String toString();
}
