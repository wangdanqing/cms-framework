<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: ÏÂÎç3:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${fn:length(groupMap)>0}">
    <c:forEach items="${groupMap}" var="entry">
        <div class="ap">
            <h4><em>${entry.key.groupName}</em></h4>
            <!--S usrLi_usr2(inc)-->
            <div class="usrLi">
                <t:userList list="${entry.value}" type="horizontal" curUid="${lookUser.id}"/>
            </div>
            <!--E usrLi_usr2(inc)-->
        </div>
    </c:forEach>
</c:if>