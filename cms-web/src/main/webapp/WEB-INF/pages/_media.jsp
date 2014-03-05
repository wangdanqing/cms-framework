<%--
  Created by IntelliJ IDEA.
  User: shijinkui
  Date: 14-3-5
  Time: 下午10:11
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/js/angular.js" type="text/javascript"></script>
<div class="panel panel-default" ng-controller="mediaCtrl">
	<div class="panel-heading">媒体列表 {{msg}} {{error}}</div>

	<div class="panel-body">

		<form ng-submit="addMedia()">
			<input type="text" ng-model="desc" size="30" placeholder="媒体名"/>
			<input type="text" ng-model="siteurl" size="30" placeholder="媒体链接"/>
			<input type="text" ng-model="logourl" size="40" placeholder="媒体LOGO图链接"/>
			<input class="btn-primary" type="submit" value="add">
		</form>


		<table class="table">
			<thead>
			<tr>
				<th>ID</th>
				<th>名称</th>
				<th>site url</th>
				<th>logo url</th>
			</tr>
			</thead>
			<tbody>
			<form action="" method="post">
				<tr ng-repeat="item in items">
					<td>{{item.id}}</td>
					<td>{{item.desc}}</td>
					<td>{{item.siteurl}}</td>
					<td>{{item.logourl}}</td>
					<td>
						<button type="button" class="btn btn-default" ng-click="deleteMedia(item)">删除</button>
						<button type="button" class="btn btn-default" ng-click="modifyMedia(item)">修改</button>
					</td>
				</tr>
			</form>
			</tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	function mediaCtrl($scope, $http) {
		var contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
		$scope.error = "";
		$scope.msg = "";
		$scope.items = <%=request.getAttribute("list")%>;

		$scope.addMedia = function () {
			$http.post("/media/create", {'desc': $scope.desc, 'siteurl': $scope.siteurl, 'logourl': $scope.logourl}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.push(data);
						$scope.desc = '';
						$scope.logourl = '';
						$scope.siteurl = '';
						$scope.msg = status + ' => 添加媒体[' + $scope.desc + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};

		$scope.modifyMedia = function () {
			$http.post("/media/modify", {'desc': $scope.desc, 'siteurl': $scope.siteurl, 'logourl': $scope.logourl}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.update(data);
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};


		$scope.deleteMedia = function (template) {
			var tmp = template;
			$http.post("/media/delete", {'id': template.id}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.splice($scope.items.indexOf(tmp), 1);
						$scope.msg = status + ' => 删除媒体[' + tmp.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};
	}
</script>