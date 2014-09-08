<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Sign in &middot; Twitter Bootstrap</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">

	<!-- Le styles -->
	<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
	<style type="text/css">
		body {
			padding-top: 40px;
			padding-bottom: 40px;
			9 background-color: #f5f5f5;
		}

		.form-signin {
			max-width: 300px;
			padding: 19px 29px 29px;
			margin: 0 auto 20px;
			background-color: #fff;
			border: 1px solid #e5e5e5;
			-webkit-border-radius: 5px;
			-moz-border-radius: 5px;
			border-radius: 5px;
			-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
			-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
			box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
		}

		.form-signin .form-signin-heading,
		.form-signin .checkbox {
			margin-bottom: 10px;
		}

		.form-signin input[type="text"],
		.form-signin input[type="password"] {
			font-size: 16px;
			height: auto;
			margin-bottom: 15px;
			padding: 7px 9px;
		}

	</style>
	<link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
</head>

<body>

<div class="container">
	<form class="form-signin" action="/user/loginin" method="post">
		<c:if test="${! empty login_error}">
			<span class="label label-danger"><c:out value="${login_error}" escapeXml="false"/></span>
		</c:if>
		<h2 class="form-signin-heading">登陆</h2>
		<input type="text" name="username" id="username" autofocus class="input-block-level" placeholder="用户名">
		<input type="password" name="password" id="password" class="input-block-level" placeholder="密码" autocomplete="off">
		<input type="text" name="captcha" id="captcha" class="input-block-level" placeholder="输入验证码" autocomplete="off">
		<img src="/user/getCaptcha" title="验证码" style="margin:5px 0 10px 0"/> <br/>
		<button class="btn btn-large btn-primary" type="submit">登陆</button>
	</form>
</div>
<!-- /container -->
</body>
</html>
