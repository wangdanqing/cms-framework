<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: ����3:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="aps">
    <div class="ap">
        <h4>
            <span class="opt"><a href="/following?uid=${lookedUser.id}" class="fuc"><b>����</b>&gt;&gt;</a></span>
            <em><c:if test="${lookedUser.sex==1}">��</c:if><c:if test="${lookedUser.sex!=1}">��</c:if>�Ĺ�ע<q
                    class="q"><a href="/following?uid=${lookedUser.id}">(${userNum.followingNum})</a></q></em>
        </h4>
        <!--S face(inc)-->
        <div class="face">
            <t:userList list="${followingList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--E face(inc)-->
    </div>
    <div class="ap">
        <h4>
            <span class="opt"><a href="/followers?uid=${lookedUser.id}" class="fuc"><b>����</b>&gt;&gt;</a></span>
            <em><c:if test="${lookedUser.sex==1}">��</c:if><c:if test="${lookedUser.sex!=1}">��</c:if>�ķ�˿<q
                    class="q"><a href="/followers?uid=${lookedUser.id}">(${userNum.followedNum})</a></q></em>
        </h4>
        <!--S face(inc)-->
        <div class="face">
            <t:userList list="${followedList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--E face(inc)-->
    </div>
</div>