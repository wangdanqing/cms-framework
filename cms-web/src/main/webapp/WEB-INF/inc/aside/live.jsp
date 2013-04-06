<%--
  直播碎片, 出现在首页发布区的右上角
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<p id="original" class="ad_gov <c:if test="${live == null || live == ''}">ad_gov_act</c:if>"><em><c:if test="${live != null && live != ''}">${live}</c:if></em><q>请文明发言</q></p>