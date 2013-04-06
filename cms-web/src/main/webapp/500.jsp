<%@ page language="java" contentType="text/html;charset=gbk" pageEncoding="gbk"%>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@page isErrorPage="true" %>
<%
     response.setStatus(HttpServletResponse.SC_OK);
%>
<!DOCTYPE html><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb18030">
<meta http-equiv="imagetoolbar" content="no">
<title>500</title>
 <t:css src="http://builder.oliv.cn/c/t2/sys.css"></t:css>
</head>
<body>
<div class="tray">
	<p class="png"></p>
	<div class="c0">
		<div class="logo"><h2><i class="png"></i><a href="http://t.sohu.com" title="搜狐微博">搜狐微博</a></h2></div>

	</div>
</div>
<div class="bdy">
	<div class="sys sys500">
		<p class="sysImg"></p>
		<p class="sysTurn"><a href="#">返回上一页</a><span class="spl">|</span><a href="#">首页</a></p>
	</div>
</div>

<!-- div>
<p>An exception was thrown:
<b> <%=exception.getClass()%>:<br/>
    <%=exception.getMessage().replaceAll("<","&lt;").replaceAll(">","&gt;")%><%//exception.printStackTrace();%></b></p>
</div -->
<%
    System.err.println(exception.toString());
%>
</body>

</html>
