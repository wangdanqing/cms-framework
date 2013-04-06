<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@include file="/WEB-INF/inc/globalConfig.jsp" %>


<c:if test="${not empty linkList}">
    <div class="ap">
        <h4><em>${organize.chipName}</em></h4>
        <c:forEach items="${linkList}" var="link">
            <ul>
                <li><a target="_blank" href="${link.href}" style="unicode-bidi: normal;" hidefocus="">${link.name}</a>
                </li>
            </ul>
        </c:forEach>
    </div>
</c:if>

