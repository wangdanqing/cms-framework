<%--
  Created by IntelliJ IDEA.
  User: songh
  Date: 11-5-9
  Time: 下午8:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="ap">
	<h4><em>人气推荐</em></h4>
		<div class="face">
            <c:forEach var="u" items="${hotuserlist}" varStatus="varStatus">
                <p class="avt avt32"><a href="/people?uid=${u.uid}" title="${u.uname}"><i class="img" style="background-image: url(&quot;${u.uicon}&quot;);" data-content='{"type":"nick", "nick":"${u.uname}"}'></i></a></p>
            </c:forEach>
        </div>
</div>