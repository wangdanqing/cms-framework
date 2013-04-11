<%
    String user = (String) session.getAttribute("user");
    if (user == null || user.equals("null")) {
        System.out.println("====>" + user);
        response.sendRedirect("/user/login");
        return;
    }
%><%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Bootstrap, from Twitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
        body {
            padding-top: 60px;
            padding-bottom: 40px;
        }

        .sidebar-nav {
            padding: 9px 0;
        }

        @media (max-width: 980px) {
            /* Enable use of floated navbar text */
            .navbar-text.pull-right {
                float: none;
                padding-left: 5px;
                padding-right: 5px;
            }
        }
    </style>
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144"
          href="/images/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114"
          href="/images/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72"
          href="/images/ico/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="/images/ico/ico/apple-touch-icon-57-precomposed.png">
    <link rel="shortcut icon" href="/favicon.png">
</head>

<body>

<jsp:include page="inc/header.jsp" flush="true"/>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <jsp:include page="inc/channel.jsp" flush="true"/>
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
<script src="/js/jquery.js"></script>
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
