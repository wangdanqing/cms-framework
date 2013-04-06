<%@ page import="com.chinaren.twitter.rpc.client.TwitterHttpClient" %>
<%@ page import="org.json.JSONObject" %>
<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-4-27
  Time: 下午5:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>JavaVM</title></head>
<body>

<%
try{
	long Uptime = System.currentTimeMillis() - com.chinaren.twitter.dao.list.DaoUtil.SysStartTime;
	long Uh = Uptime / 3600000;
	long Um = (Uptime % 3600000)/60000;
	long Us = (Uptime % 60000)/1000;
	long Ums = (Uptime % 1000)/10;
    out.print("<B style='color:red'>Uptime = "+Uh+"hour "+Um+"min "+Us+"."+Ums+"second</b> ");
    out.println("JVM Free mem: " + Runtime.getRuntime().freeMemory()/1024/1024 + "M ... Total mem: " + Runtime.getRuntime().totalMemory()/1024/1024 + "M<br>");
}
catch(Exception E)
{
	out.println(E.getMessage());
}
%>

</body>
</html>