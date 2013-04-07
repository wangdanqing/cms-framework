<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="well sidebar-nav">
    <ul class="nav nav-list">
        <c:forEach items="${channelList}" var="channel" varStatus="idx">
            <li class="nav-header"><c:out value="${idx.count}"/>: <c:out value="${channel.name}"/></li>
        </c:forEach>
        <%--<li class="nav-header">普索网</li>--%>
        <%--<li class="active"><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
    </ul>
</div>