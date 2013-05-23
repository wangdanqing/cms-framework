<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="/">｛普索内容管理系统V1.0-Beta｝</a>

            <div class="nav-collapse collapse">
                <p class="navbar-text pull-right">
                    登陆用户 <a href="#" class="navbar-link"><%=session.getAttribute("user")%>
                </a>
                </p>
                <ul class="nav">
                    <li class="active"><a href="#">首页</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">系统管理<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="/channel/list">频道管理</a></li>
                            <li><a href="#">模板管理</a></li>
                            <li><a href="#">全局碎片</a></li>
                            <li><a href="#">频道碎片</a></li>
                            <li class="divider"></li>
                            <%--<li class="nav-header">Nav header</li>--%>
                            <li><a href="#">媒体管理</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">用户管理<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="#">群组管理</a></li>
                            <li><a href="#">权限管理</a></li>
                            <li><a href="#">用户管理</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">网站管理<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="/subject/homepage">首页管理</a></li>
                            <li><a href="#">左侧菜单设置</a></li>
                        </ul>
                    </li>
                    <li><a href="#help">帮助</a></li>
                    <li><a href="#about">关于</a></li>
                    <li><a href="#contact">联系我们</a></li>
                </ul>

            </div>
            <!--/.nav-collapse -->
        </div>
    </div>
</div>
