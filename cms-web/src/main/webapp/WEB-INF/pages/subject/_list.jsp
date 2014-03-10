<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${pageContext.request.contextPath}/js/angular.js" type="text/javascript"></script>
<div class="table-responsive" ng-controller="itmeCtrl">
	<div class="well well-sm">栏目列表    {{msg}}   {{error}}</div>

	<table class="table table-striped table-hover">
		<thead>
		<tr>
			<th>#</th>
			<th>栏目名</th>
			<th>缩写</th>
			<th>频道</th>
			<th>父栏目</th>
			<th>状态</th>
			<th>编辑</th>
			<th>操作</th>
		</tr>
		</thead>
		<tbody>

		<tr ng-repeat="item in items">
			<td><c:out value="${idx.count}"/></td>
			<td>{{item.name}}</td>
			<td>{{item.shortName}}</td>
			<td>{{item.channelId}}</td>
			<td>{{item.pid}}</td>
			<td>{{item.status}}</td>
			<td>{{item.editorId}}</td>
			<td>
				<a href="${pageContext.request.contextPath}/subject/toitem?op=update&id={{item.id}}">修改</a> |
				<a href="#" ng-click="deleteItem(item)">删除</a></td>
		</tr>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	function itmeCtrl($scope, $http) {
		var contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
		$scope.error = "";
		$scope.msg = "";
		$scope.items = <%=request.getAttribute("list")%>;

		$scope.deleteItem = function (item) {
			var tmp = item;
			$http.post("/subject/delete", {'id': item.id}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.splice($scope.items.indexOf(tmp), 1);
						$scope.msg = status + ' => 删除专题[' + tmp.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};
	}
</script>