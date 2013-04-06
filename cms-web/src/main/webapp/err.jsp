<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"><%@ page language="java" contentType="text/html;charset=gbk" pageEncoding="gbk"%><%@ page import="java.util.*, java.io.*,org.json.JSONObject,com.chinaren.common.*,com.chinaren.twitter.dto.*,com.chinaren.twitter.dao.*,com.chinaren.twitter.factory.*,com.chinaren.twitter.util.*,com.chinaren.twitter.server.*" %>
<%@ include file="/inc/getUserID.inc" %>
<%
String t = ToolKit.getStr(request, "t");
String c = ToolKit.getStr(request, "c");
if("".equals(c) && "".equals(t)) c = "您说像话嘛？这台服务器跟管理员私奔了。别着急，正电话联系其他服务器呢...";
%><html lang="zh-CN">
<head>
<title>发生了错误</title>
<link rel="stylesheet" type="text/css" href="http://s1.cr.itc.cn/img/t/css/g.css">
<link rel="stylesheet" type="text/css" href="http://s2.cr.itc.cn/img/t/css/sys.css">
</head>

<body>
<div class="bgs"><p class="bg1"></p><p class="bg2"></p><p class="bg3"></p></div>
<!--S 托盘(inc)-->
<div class="tray">
	<p class="pngx"></p>
	<div class="c0">
	</div>

</div>
<!--E 托盘(inc)-->
<!--S 标准头(inc)-->
<div class="header">
	<div class="c0">
		<h3 class="logo"><i class="png"></i><a href="/" title="搜狐微博" accesskey="1">搜狐微博</a></h3>
	</div>
</div>
<!--E 标准头(inc)-->

<div class="bdy">
	<div class="sys syserr">
<!--S sys cnt-->
		<div class="sysinfo">
<%if(t != null && !"".equals(t)) {%><h3><%=t%></h3><%}%>
<%if(c != null && !"".equals(c)) {%><h4><%=c%></h4><%}%>

		</div>
		<div class="sysbc">
			<p class="tag"><em><a href="javascript:history.go(-1);">返回上一页</a></em><em><a href="http://t.sohu.com/">首页</a></em></p>
		</div>
<!--S sys cnt-->
	</div>
</div>

</body>
</html>
