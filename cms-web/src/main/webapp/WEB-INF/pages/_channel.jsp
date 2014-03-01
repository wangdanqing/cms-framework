<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${pageContext.request.contextPath}/js/angular.js" type="text/javascript"></script>
<div class="bs-docs-example" ng-controller="channelCtrl">
	<table class="table">
		<thead>
		<tr>
			<th>ID</th>
			<th>名称</th>
			<th>目录</th>
			<th>操作</th>
		</tr>
		</thead>
		<tbody>
		<div id="ng_channel">
			<form ng-submit="addChannel()">
				<input type="text" ng-model="name" size="30" placeholder="填写频道名字, 如: 普索网"/> <input type="text"
																								   ng-model="dir"
																								   size="30"
																								   placeholder="填写频道目录名, 如: pusuo"/>
				<input class="btn-primary" type="submit" value="add">
			</form>
		</div>
		<div class="alert alert-info">频道面板。 {{msg}} {{error}}</div>
		<form action="/channel/delete" method="post">
			<tr ng-repeat="ch in channels">
				<td>{{ch.id}}</td>
				<td>{{ch.name}}</td>
				<td>{{ch.dir}}</td>
				<td>
					<button type="button" class="btn btn-default" ng-click="deleteChannel(ch)">删除</button>
				</td>
			</tr>
		</form>

		</tbody>
	</table>
</div>
<script type="text/javascript">
	function channelCtrl($scope, $http) {
		var contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
		$scope.error = "";
		$scope.msg = "";
		$scope.channels = <%=request.getAttribute("list")%>;

		$scope.addChannel = function () {
			$http.post("/channel/create", {'name': $scope.name, 'dir': $scope.dir}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.channels.push(data);
						$scope.name = '';
						$scope.dir = '';
						$scope.msg = status + ' => 创建频道[' + $scope.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};


		$scope.deleteChannel = function (channel) {
			$http.post("/channel/delete", {'id': channel.id}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.channels.splice($scope.channels.indexOf(channel), 1);
						$scope.msg = status + ' => 删除频道[' + channel.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};

	}
</script>
<style>
	#ng_channel input {
		margin-right: 15px;
	}
</style>
