<%@page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<html>
<head><title>内部服务器错误</title></head>
</html>
<%
	exception.printStackTrace(response.getWriter());
%>