<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">普索内容管理系统beta</a>
		</div>
		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="/">首页</a></li>
				<li class="divider"></li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">系统管理<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="${pageContext.request.contextPath}/channel/list">频道管理</a></li>
						<li><a href="${pageContext.request.contextPath}/template/list">模板管理</a></li>
						<li class="divider"></li>
						<li><a href="#">全局碎片</a></li>
						<li><a href="#">频道碎片</a></li>
						<li class="divider"></li>
						<li><a href="#">媒体管理</a></li>
					</ul>
				</li>
				<li><a href="${pageContext.request.contextPath}/entity/tocreate">新建</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">系统设置<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="#">群组管理</a></li>
						<li><a href="#">权限管理</a></li>
						<li><a href="#">用户管理</a></li>
						<li class="divider"></li>
						<li><a href="#">帮助</a></li>
						<li><a href="#">关于我们</a></li>
					</ul>
				</li>
				<li><a href="#">帮助</a></li>
				<li><a href="#">关于我们</a></li>
			</ul>
		</div>
	</div>
</div>

