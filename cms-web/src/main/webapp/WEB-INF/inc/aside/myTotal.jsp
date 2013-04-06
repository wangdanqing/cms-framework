<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="ap myTotal">
    <c:if test="${not empty pvCount && not empty commentCount && not empty rtCount}">
        <ul class="inf inf2">
            <li><i class="i iS iVisits"></i><em>围观：</em>${pvCount}</li>
            <li><i class="i iS iComments"></i><em>评论：</em>${commentCount}</li>
            <li><i class="i iS iRetwis"></i><em>转发：</em>${rtCount}</li>
            <c:if test="${isPreExpr == null}">
                <li><i class="i iS iRss"></i><em><a href="http://t.sohu.com/rss/${lookedUser.id}" class="fuc" target="_blank"><b>订阅微博</b></a></em></li>
            </c:if>
        </ul>
    </c:if>
    <!-- 仅当看其他人页面时显示 -->
    <c:if test="${ not empty lookUser && not empty lookedUser && lookUser.id != lookedUser.id && isPreExpr == null}">
        <a href="javascript:void(1)"  onclick="kola('newt.report.Report',function() {tw.report.reportUser({uid:${lookedUser.id}});})" class="fuc"><i class="i iS iSug"></i><b>举报不良信息</b></a>
    </c:if>

</div>

