<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach items="${subject_list}" var="subject" varStatus="idx">
	<li class="nav-header"><c:out value="${idx.count}"/>: <c:out value="${subject.name}"/></li>
</c:forEach>