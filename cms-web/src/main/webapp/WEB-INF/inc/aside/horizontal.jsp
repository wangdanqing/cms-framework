<%--
  人气推荐
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div id="homeapp_hotrecommends"></div>
<c:choose>
    <c:when test="${userNum.followingNum<100}">
        <t:js point="after">
            kola("newt.app.recommends.HotRecommends", function (HotRecommends) {
                new HotRecommends({
                    target: document.getElementById("homeapp_hotrecommends")
                });
            });
        </t:js>
    </c:when>
    <c:otherwise>
        <t:js point="after">
            kola("newt.app.recommends.HotRecommendsSimple", function (HotRecommendsSimple) {
                new HotRecommendsSimple({
                    target: document.getElementById("homeapp_hotrecommends")
                });
            });
        </t:js>
    </c:otherwise>
</c:choose>