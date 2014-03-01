<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach items="${channel_homepage}" var="cp" varStatus="idx">
	<fieldset>
		<legend><%--<c:out value="${idx.count}"/>: --%><c:out value="${cp.key.name}"/></legend>
		<c:if test="${ not empty cp.value }">
			<table class="table table-hover">
				<tr>
					<td><c:out value="${cp.value.name}" escapeXml="false"/></td>
					<td><c:out value="${cp.value.desc}" escapeXml="false"/></td>
					<td><c:out value="${cp.value.status}" escapeXml="false"/></td>
					<td><c:out value="${cp.value.templateId}" escapeXml="false"/></td>
				</tr>
			</table>
		</c:if>
		<c:if test="${cp.value == null or empty cp.value}">
			<form id="<c:out value="${cp.key.id}" escapeXml="false"/>" class="form-horizontal"
			      action="/subject/createHomepage" method="post">
				<div class="control-group">
					<label class="control-label" for="name">发布英文名称</label>

					<div class="controls">
						<input type="text" id="name" name="name" placeholder="发布英文名称">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="desc">首页描述</label>

					<div class="controls">
						<input type="text" id="desc" name="desc" placeholder="首页描述">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="templateId">首页模板</label>

					<div class="controls">
						<input type="text" id="templateId" name="templateId" placeholder="首页模板">
					</div>
				</div>
				<input type="hidden" id="channelId" name="channelId"
				       value="<c:out value="${cp.key.id}" escapeXml="false"/> ">

				<div class="control-group">
					<div class="controls">
						<button id="<c:out value="${channel.id}" escapeXml="false"/>" type="submit" class="btn">
							新建首页
						</button>
					</div>
				</div>
			</form>
		</c:if>

	</fieldset>
</c:forEach>
