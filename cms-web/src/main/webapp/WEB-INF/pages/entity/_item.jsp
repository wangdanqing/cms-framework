<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel panel-default">
	<div class="panel-heading">
		<div class="row">
			<div class="col-md-8">新建新闻</div>
			<div class="col-md-4"><a href="${pageContext.request.contextPath}/entity/list">返回列表</a></div>
		</div>
	</div>
	<div class="panel-body" style="padding-left: 30px;">
		<form name="template" action="${pageContext.request.contextPath}/entity/create" method="post"
				class="form-inline">
			<input type="hidden" name="id" value="<c:out value="${item.id}" />"/>

			<div class="row-fluid">
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>标题</strong></span>
						<input type="text" name="title" class="form-control" placeholder="必填项，内容标题"
								value="<c:out value="${item.title}"/>">
					</div>
				</div>
			</div>
			<br/>

			<div class="row-fluid">
				<div class="form-group">
					<label for="pid">父栏目</label>
					<select id="pid" name="pid" class="form-control input-sm">
						<%--<c:out value="${item.pid}" />--%>
						<c:forEach items="${pidList}" var="sub">
							<option value="0" <c:if test="${sub.id == item.pid}">selected</c:if>><c:out
									value="${sub.name}"/></option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="priority">权重</label>
					<select id="priority" name="priority" class="form-control input-sm">
						<option value="60" <c:if test="${item.priority == 60}">selected</c:if>>普通</option>
						<option value="100" <c:if test="${item.priority == 100}">selected</c:if>>最大</option>
						<option value="80" <c:if test="${item.priority == 80}">selected</c:if>>高</option>
						<option value="40" <c:if test="${item.priority == 40}">selected</c:if>>较小</option>
						<option value="0" <c:if test="${item.priority == 0}">selected</c:if>>最小</option>
					</select>
				</div>

				<div class="form-group">
					<label for="status">状态</label>
					<select id="status" name="status" class="form-control input-sm">
						<option value="1" <c:if test="${item.status == 1}">selected</c:if>>正常</option>
						<option value="-1" <c:if test="${item.status == -1}">selected</c:if>>禁用</option>
						<option value="0" <c:if test="${item.status == 0}">selected</c:if>>保护</option>
					</select>
				</div>

				<div class="form-group">
					<label for="channelId">频道</label>
					<select id="channelId" name="channelId" class="form-control input-sm">
						<c:forEach items="${channelList}" var="channel">
							<option value="${channel.id}"
									<c:if test="${item!=null && channel.id == channel.id}">selected</c:if>><c:out
									value="${channel.name}"/></option>
						</c:forEach>
					</select>
				</div>

				<div class="form-group">
					<label for="mediaId">媒体</label>
					<select id="mediaId" name="mediaId" class="form-control input-sm">
						<c:forEach items="${mediaList}" var="media">
							<option value="${media.id}"
									<c:if test="${media!=null && media.id == media.id}">selected</c:if>><c:out
									value="${media.desc}"/></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<br/>

			<div class="row-fluid">
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">shortName</span>
						<input type="text" name="shortName" value="<c:out value="${item.shortName}" />"
								class="form-control" placeholder="选填, 文件名"/>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">作者</span>
						<input type="text" name="author" <c:out value="${item.author}"/> class="form-control"
								placeholder="选填, 张三"/>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">责任编辑</span>
						<input type="text" name="dutyEditor" <c:out value="${item.dutyEditor}"/> class="form-control"
								placeholder="选填, 李四"/>
					</div>
				</div>
			</div>
			<br/>

			<div class="row-fluid">
				<div class="col-lg-12">
					<div class="input-group">
						<span class="input-group-addon">内容</span>
						<textarea class="form-control" rows="24" cols="60" name="content"><c:out
								value="${item.content}"/></textarea>
					</div>
				</div>
			</div>
			<br/>
			<button type="submit" class="btn btn-default">提交</button>
		</form>
	</div>
</div>
<style>
	.form-group {
		margin: 0 15px 15px 15px;
	}
</style>