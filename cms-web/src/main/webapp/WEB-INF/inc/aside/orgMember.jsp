<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: 下午3:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>

<c:forEach items="${groupMap}" var="group">

    <c:if test="${fn:length(group.value) > 0}">
    <div class="ap">
        <h4><em>${group.key.groupName}</em></h4>
        <!--S usrLi_usr2(inc)-->
        <div class="usrLi">
            <t:userList list="${group.value}" type="childOrgs" curUid="${lookUser.id}"/>
        </div>
        <!--E usrLi_usr2(inc)-->
        <!-- 仅当分组下成员大于等于6个时显示更多 -->
        <c:if test="${fn:length(group.value) >= 6}">
           <!-- <div class="apb"><p class="fucs2"><a class="fuc" href="/follow/member?uid=${lookedUser.id}"><b>更多</b>&gt;&gt;</a></p></div> -->
            <div class="apb"><p class="fucs2"><a class="fuc" href="http://t.sohu.com/news/org_topic.jsp?uid=<c:out value="${lookedUser.id}"/>" target="_blank"><b>更多</b>&gt;&gt;</a></p></div>
        </c:if>

    </div>
</c:if>

</c:forEach>

