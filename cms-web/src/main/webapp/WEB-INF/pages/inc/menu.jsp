<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul class="nav nav-sidebar">
	<li class="active"><a href="#">频道</a></li>
	<c:forEach items="${_channel_tree_}" var="channel" varStatus="idx">
		<li><a href="/channel/get/<c:out value="${channel.id}"/>"><c:out value="${idx.count}"/>: <c:out value="${channel.name}"/></a></li>
	</c:forEach>
</ul>