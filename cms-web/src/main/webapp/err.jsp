<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"><%@ page language="java" contentType="text/html;charset=gbk" pageEncoding="gbk"%><%@ page import="java.util.*, java.io.*,org.json.JSONObject,com.chinaren.common.*,com.chinaren.twitter.dto.*,com.chinaren.twitter.dao.*,com.chinaren.twitter.factory.*,com.chinaren.twitter.util.*,com.chinaren.twitter.server.*" %>
<%@ include file="/inc/getUserID.inc" %>
<%
String t = ToolKit.getStr(request, "t");
String c = ToolKit.getStr(request, "c");
if("".equals(c) && "".equals(t)) c = "��˵�����̨������������Ա˽���ˡ����ż������绰��ϵ������������...";
%><html lang="zh-CN">
<head>
<title>�����˴���</title>
<link rel="stylesheet" type="text/css" href="http://s1.cr.itc.cn/img/t/css/g.css">
<link rel="stylesheet" type="text/css" href="http://s2.cr.itc.cn/img/t/css/sys.css">
</head>

<body>
<div class="bgs"><p class="bg1"></p><p class="bg2"></p><p class="bg3"></p></div>
<!--S ����(inc)-->
<div class="tray">
	<p class="pngx"></p>
	<div class="c0">
	</div>

</div>
<!--E ����(inc)-->
<!--S ��׼ͷ(inc)-->
<div class="header">
	<div class="c0">
		<h3 class="logo"><i class="png"></i><a href="/" title="�Ѻ�΢��" accesskey="1">�Ѻ�΢��</a></h3>
	</div>
</div>
<!--E ��׼ͷ(inc)-->

<div class="bdy">
	<div class="sys syserr">
<!--S sys cnt-->
		<div class="sysinfo">
<%if(t != null && !"".equals(t)) {%><h3><%=t%></h3><%}%>
<%if(c != null && !"".equals(c)) {%><h4><%=c%></h4><%}%>

		</div>
		<div class="sysbc">
			<p class="tag"><em><a href="javascript:history.go(-1);">������һҳ</a></em><em><a href="http://t.sohu.com/">��ҳ</a></em></p>
		</div>
<!--S sys cnt-->
	</div>
</div>

</body>
</html>
