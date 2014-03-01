<% String user = (String) session.getAttribute("user");
	if (user == null || user.equals("null")) {
		System.out.println("未登录跳转====>" + user);
//        response.sendRedirect("/user/login");
//        return;
	}%>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" ng-app>
<head>
	<jsp:include page="inc/meta.jsp"/>
</head>
<body>
<jsp:include page="inc/header.jsp" flush="true"/>
<div class="container-fluid">
	<div class="row">
		<div class="col-sm-3 col-md-2 sidebar">
			<jsp:include page="inc/menu.jsp" flush="true"/>
		</div>
		<div class="span9">
			<jsp:include page="${include_page}"/>
		</div>
	</div>
	<hr>
	<footer>
		<jsp:include page="inc/footer.jsp"/>
	</footer>
</div>
</body>
</html>
