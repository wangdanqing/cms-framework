<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: ����3:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${fn:length(childOrgModelList) > 0}">
    <div class="ap">
        <h4><em>${organize.childTitle}</em></h4>
        <!--S usrLi_usr2(inc)-->
        <div class="usrLi">
            <t:userList list="${childOrgModelList}" type="childOrgs" curUid="${lookUser.id}"/>
        </div>
        <!--E usrLi_usr2(inc)-->
        <!-- �������»������ڵ���9��ʱ��ʾ���� -->
        <c:if test="${fn:length(childOrgModelList) >= 9}">
            <!--<div class="apb"><p class="fucs2"><a class="fuc" href="/follow/org?uid=${lookedUser.id}"><b>����</b>&gt;&gt;</a></p></div>-->
             <div class="apb"><p class="fucs2"><a class="fuc" href="http://t.sohu.com/news/org_topic.jsp?uid=<c:out value="${lookedUser.id}"/>" target="_blank"><b>����</b>&gt;&gt;</a></p></div>
        </c:if>

    </div>
</c:if>