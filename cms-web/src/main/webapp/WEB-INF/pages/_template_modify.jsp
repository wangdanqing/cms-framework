<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">修改模版</h3>
	</div>
	<div class="panel-body">
		<form name="template" action="${pageContext.request.contextPath}/template/modify" method="post">
			<div class="input-group col-lg-6">
				<span class="input-group-addon">模版名</span>
				<input type="text" name="name" class="form-control" placeholder="模版名"
						value="<c:out value="${item.name}"/>">
			</div>
			<p/>
			<input type="hidden" name="id" value="<c:out value="${item.id}" />"/>

			<div class="input-group col-lg-6">
			<span class="input-group-addon">
				<input name="type" type="radio" value="1" <c:if test="${item.type == 1}">checked</c:if>>
			</span>
				<span class="form-control">首页模版</span>
			<span class="input-group-addon">
				<input name="type" type="radio" value="2" <c:if test="${item.type == 2}">checked</c:if>>
			</span>
				<span class="form-control">栏目/专题模版</span>
			<span class="input-group-addon">
				<input name="type" type="radio" value="3" <c:if test="${item.type == 3}">checked</c:if>>
			</span>
				<span class="form-control">正文页模版</span>
			</div>
			<p/>

			<p/>
			创建者：<c:out value="${item.creator}"/> createTime: <c:out value="${item.createTime}"/> updateTime: <c:out
				value="${item.uptime}"/>
			<p/>

			<div class="input-group col-lg-12">
				<div class="input-group">
					<span class="input-group-addon">模版内容</span>
					<textarea class="form-control" rows="24" cols="60" name="content"><c:out
							value="${item.content}"/></textarea>
				</div>
			</div>
			<p/>
			<p/>
			<button type="submit" class="btn btn-default">提交</button>
		</form>
	</div>
</div>