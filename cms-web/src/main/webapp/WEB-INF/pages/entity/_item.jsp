<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">新建内容实体</h3>
	</div>
	<div class="panel-body" style="padding-left: 30px;">
		<form name="template" action="${pageContext.request.contextPath}/entity/create" method="post">
			<input type="hidden" name="id" value="<c:out value="${item.id}" />"/>
			<input type="hidden" name="pid" value="<c:out value="${item.pid}" />"/>
			<input type="hidden" name="channelId" value="<c:out value="${item.channelId}" />"/>

			<div class="row">
				<div class="col-lg-6">
					<div class="input-group">
						<span class="input-group-addon"><strong>标题</strong></span>
						<input type="text" name="title" class="form-control" placeholder="必填项，内容标题"
								value="<c:out value="${item.title}"/>">
					</div>
				</div>
			</div>
			<br/>

			<div class="row">
				<div class="col-lg-2">
					<div class="input-group">
						<span class="input-group-addon">权重</span>
						<select name="priority" class="form-control">
							<option value="60" <c:if test="${item.priority == 60}">selected</c:if>>普通</option>
							<option value="100" <c:if test="${item.priority == 100}">selected</c:if>>最大</option>
							<option value="80" <c:if test="${item.priority == 80}">selected</c:if>>高</option>
							<option value="40" <c:if test="${item.priority == 40}">selected</c:if>>较小</option>
							<option value="0" <c:if test="${item.priority == 0}">selected</c:if>>最小</option>
						</select>
					</div>
				</div>

				<div class="col-lg-2">
					<div class="input-group">
						<span class="input-group-addon">状态</span>
						<select name="status" class="form-control">
							<option value="1" <c:if test="${item.status == 1}">selected</c:if>>发布</option>
							<option value="-1" <c:if test="${item.status == -1}">selected</c:if>>禁用</option>
							<option value="0" <c:if test="${item.status == 0}">selected</c:if> title="不出现在列表中">保护</option>
						</select>
					</div>
				</div>
			</div>
			<br/>

			<div class="row">
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">shortName</span>
						<input type="text" name="shortName" value="<c:out value="${item.shortName}" />" class="form-control" placeholder="选填, 文件名"/>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">作者</span>
						<input type="text" name="author" <c:out value="${item.author}" /> class="form-control" placeholder="选填, 张三"/>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="input-group">
						<span class="input-group-addon">责任编辑</span>
						<input type="text" name="dutyEditor" <c:out value="${item.dutyEditor}" /> class="form-control" placeholder="选填, 李四"/>
					</div>
				</div>
			</div>
			<br/>

			<div class="row">
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