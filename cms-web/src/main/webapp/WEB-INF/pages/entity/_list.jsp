<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel panel-default">
	<div class="panel-heading">实体列表</div>
	<div class="panel-body">
		<form action="${pageContext.request.contextPath}/template/delete" method="post">
			<ul class="list-group">
				<c:forEach items="${list}" var="entity">
					<li class="list-group-item">
						<span class="badge">查看</span>
						<span class="badge">修改</span>
						<span class="badge">删除</span>
						<c:out value="${entity.title}"/>
					</li>
				</c:forEach>
			</ul>
		</form>
	</div>
</div>
