<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${pageContext.request.contextPath}/js/angular.js" type="text/javascript"></script>

<div class="panel panel-default">
	<div class="panel-heading"><c:out value="${item.name}" escapeXml="false"/> <c:out value="${item.dir}"
			escapeXml="false"/></div>
	<div class="panel-body">
		Panel content
		<c:out value="${item.id}" escapeXml="false"/>
	</div>
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
