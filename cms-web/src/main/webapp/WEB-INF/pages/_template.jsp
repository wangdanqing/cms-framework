<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${pageContext.request.contextPath}/js/angular.js" type="text/javascript"></script>
<div class="bs-docs-example" ng-controller="templateCtrl">


	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">新建模版</h3>
		</div>
		<div class="panel-body">
			<form ng-submit="addTemplate()">
				<div class="input-group col-lg-9">
					<span class="input-group-addon">模版名</span>
					<input type="text" class="form-control" ng-model="name" size="30"/>
					<span class="input-group-addon">模版类型
						<input type="radio" ng-model="type" name="type" value="1"/> 首页模版
						<input type="radio" ng-model="type" name="type" value="2"/> 栏目/专题模版
						<input type="radio" ng-model="type" name="type" value="3"/> 正文页模版
					</span>
					<span class="input-group-btn">
						<button class="btn btn-primary" type="submit">新建</button>
					</span>
				</div>
			</form>
			{{msg}} {{error}}
		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-heading">模版列表</div>
		<table class="table">
			<thead>
			<tr>
				<th>ID</th>
				<th>名称</th>
				<th>类型</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
			</thead>
			<tbody>
			<form action="${pageContext.request.contextPath}/template/delete" method="post">
				<tr ng-repeat="item in items">
					<td>{{item.id}}</td>
					<td>{{item.name}}</td>
					<td>{{item.type}}</td>
					<td>{{item.creator}}</td>
					<td>{{item.createTime}}</td>
					<td>
						<button type="button" class="btn btn-default" ng-click="deleteTemplate(item)">删除</button>
						<a href="${pageContext.request.contextPath}/template/detail?id={{item.id}}">
							<button type="button" class="btn btn-default">修改</button>
						</a>
					</td>
				</tr>
			</form>
			</tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	function templateCtrl($scope, $http) {
		var contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
		$scope.error = "";
		$scope.msg = "";
		$scope.items = <%=request.getAttribute("list")%>;

		$scope.addTemplate = function () {
			$http.post("/template/create", {'name': $scope.name, 'type': $scope.type}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.push(data);
						$scope.name = '';
						$scope.type = '';
						$scope.msg = status + ' => 创建模版[' + $scope.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};


		$scope.deleteTemplate = function (template) {
			var tmp = template;
			$http.post("/template/delete", {'id': template.id}, {'Content-Type': contentType}
			).success(function (data, status) {
						$scope.items.splice($scope.items.indexOf(tmp), 1);
						$scope.msg = status + ' => 删除频道[' + tmp.name + ']成功';
					}).error(function (data, status) {
						$scope.error = status + ' => ' + data;
					});
		};
	}
</script>