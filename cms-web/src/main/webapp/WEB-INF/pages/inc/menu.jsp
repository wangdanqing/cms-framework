<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul id="jstreeContainer" class="nav nav-sidebar">
</ul>
<link rel="stylesheet" href="/css/jstree/style.min.css"/>
<script src="/js/jquery-2.1.0.min.js"></script>
<script src="/js/jstree.min.js"></script>
<script>
	console.log(<c:out value="${_channel_tree_}" escapeXml="false"/>);
	$('#jstreeContainer').jstree({
		'core': {
			'themes': {
				'responsive': false,
				'dots': true,
				'variant': 'small',
				'stripes': true
			},
			'data':<c:out value="${_channel_tree_}" escapeXml="false"/>
		},
		"plugins": [
			"contextmenu", "search", "state", "types", "wholerow", 'dnd'
		]
	}).bind('click.jstree', function (e) {
		var nodeName = e.target.nodeName;
		if (nodeName == 'A') {
			console.log("link --> " + e.target.href);
			location = e.target.href;
		}
	});
</script>