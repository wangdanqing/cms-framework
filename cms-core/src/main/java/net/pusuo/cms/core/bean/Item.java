package net.pusuo.cms.core.bean;

import java.io.Serializable;

public class Item implements Serializable {

	public final static int TYPE_SFRAG = 7;
	public final static int TYPE_DFRAG = 8;
	public final static int TYPE_COMMON_FRAG = 9;
	public final static int TYPE_CHANNEL_FRAG = 10;

	public final static int STATUS_ENABLE = 1;
	public final static int STATUS_DISABLE = -1; //删除html文件，删除db数据
	public final static int STATUS_PROTECT = 0;  //删除html文件，不删除数据
}
