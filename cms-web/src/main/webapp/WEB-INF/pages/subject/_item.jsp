<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel panel-default">
	<div class="panel-heading">
		<div class="row">
			<div class="col-md-8">栏目新建/修改 <span class="label label-danger"><c:out value="${error}"/></span></div>
			<div class="col-md-4"><a href="${pageContext.request.contextPath}/subject/list">返回列表</a></div>
		</div>
	</div>

	<div class="panel-body">
		<form action="${pageContext.request.contextPath}/subject/<c:out value="${op}"/>" method="post"
				class="form-inline">
			<input type="hidden" name="id" value="<c:out value="${item.id}"/>"/>

			<div class="row-fluid">
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>名称</strong></span>
						<input type="text" name="name" class="form-control" placeholder="必填项，栏目名字"
								value="<c:out value="${item.name}"/>">
					</div>
				</div>
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>英文缩写</strong></span>
						<input type="text" name="shortName" class="form-control" placeholder="必填项"
								value="<c:out value="${item.shortName}"/>">
					</div>
				</div>
			</div>
			<br/>

			<div class="row-fluid">
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>标签</strong></span>
						<input type="text" name="tags" class="form-control" placeholder="选填，','分割"
								value="<c:out value="${item.tags}"/>">
					</div>
				</div>
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>描述</strong></span>
						<input type="text" name="desc" class="form-control" placeholder="选填"
								value="<c:out value="${item.desc}"/>">
					</div>
				</div>
			</div>
			<br/>

			<div class="row-fluid">

				<div class="form-group">
					<label for="channelId">频道</label>
					<select id="channelId" name="channelId" class="form-control input-sm">
						<option value="-1">请选择</option>
						<c:forEach items="${channelList}" var="channel">
							<option value="${channel.id}"
									<c:if test="${item!=null && channel.id == channel.id}">selected</c:if>><c:out
									value="${channel.name}"/></option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="pid">父栏目</label>
					<select id="pid" name="pid" class="form-control input-sm">
						<option value="-1">请选择</option>
					</select>
				</div>
				<div class="form-group">
					<label for="templateId">模版</label>
					<select id="templateId" name="templateId" class="form-control input-sm">
						<option value="-1">请选择</option>
						<c:forEach items="${templateList}" var="tmp">
							<option value="<c:out value="${tmp.id}"/>"
									<c:if test="${tmp.id == item.templateId}">selected</c:if> ><c:out
									value="${tmp.name}"/></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<p></p>

			<div class="row-fluid">

				<div class="form-group">
					<label for="type">类型</label>
					<select id="type" name="type" class="form-control input-sm">
						<option value="-1">请选择</option>
						<option value="0" <c:if test="${item.type == 0}">selected</c:if>>首页</option>
						<option value="1" <c:if test="${item.type == 1}">selected</c:if>>栏目</option>
						<option value="2" <c:if test="${item.type == 2}">selected</c:if>>专题</option>
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
			</div>

			<p></p>

			<div class="row-fluid">
				<input type="submit" class="btn btn-primary" value="提交">

				<a type="button" class="btn btn-success" href="${pageContext.request.contextPath}/subject/list">返回列表</a>
			</div>
		</form>
	</div>
</div>

<style>
	.form-group {
		margin: 15px;
	}
</style>