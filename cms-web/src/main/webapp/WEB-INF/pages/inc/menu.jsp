<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="well sidebar-nav">
    <ul class="nav nav-list">
        <c:forEach items="${channelList}" var="channel" varStatus="idx">
            <li class="nav-header"><c:out value="${idx.count}"/>: <c:out value="${channel.name}"/></li>
        </c:forEach>
        <%--<li class="nav-header">普索网</li>--%>
        <%--<li class="active"><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
        <%--<li><a href="#">Link</a></li>--%>
    </ul>
</div>

<script src="/js/jquery.js"></script>
<%--
<script>
$(function(){
       $.get(
                "menu/getall",
                function(data, status){
                    alert(data)
                    $("#menu-list").append(data);
                },
                'json'
       );
});

function parse(jsonObject) {
        $.each(jsonObject, function(key, values){
            alert(key+"||"=values);
            html += "<li class='nav-header' module-name='"+module+"'>" + key + "</li>";
            $.each(values, function(key1, value1){
                var keyStr = key + "|" + key1;
                html += '<li isOPen=0 module-name="'+module+'"><a href="javascript:void(0);"  onclick="getChild(this,\''+keyStr+'\',2)">' + key1 + '</a></li>';
            });
        });
}
</script>             --%>
