<%
    String user = (String) session.getAttribute("user");
    if (user == null || user.equals("null")) {
        System.out.println("未登录跳转====>" + user);
//        response.sendRedirect("/user/login");
//        return;
    }
%><%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="inc/meta.jsp" />
</head>

<body>

<jsp:include page="inc/header.jsp" flush="true"/>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <jsp:include page="inc/menu.jsp" flush="true"/>
        </div>
        <!--/span-->
        <div class="span9">
            <jsp:include page="${include_page}"/>
        </div>
        <!--/span-->
    </div>
    <!--/row-->

    <hr>

    <footer>
        <p>&copy; Company 2013</p>
    </footer>

</div>
<!--/.fluid-container-->

<!-- Le javascript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/js/bootstrap-transition.js"></script>
<script src="/js/bootstrap-alert.js"></script>
<script src="/js/bootstrap-modal.js"></script>
<script src="/js/bootstrap-dropdown.js"></script>
<script src="/js/bootstrap-scrollspy.js"></script>
<script src="/js/bootstrap-tab.js"></script>
<script src="/js/bootstrap-tooltip.js"></script>
<script src="/js/bootstrap-popover.js"></script>
<script src="/js/bootstrap-button.js"></script>
<script src="/js/bootstrap-collapse.js"></script>
<script src="/js/bootstrap-carousel.js"></script>
<script src="/js/bootstrap-typeahead.js"></script>
</body>
</html>
