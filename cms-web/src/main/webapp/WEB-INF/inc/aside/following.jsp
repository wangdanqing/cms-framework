<%--
  ��ע�ͷ�˿��ҳ����Ƭ
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: ����8:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="aps">
    <div class="ap">
        <h4>
            <span class="opt"><a href="/following?uid=${lookUser.id}" class="fuc"><b>����</b>&gt;&gt;</a></span>
            <em><c:choose><c:when test="${not empty userNum }"><c:if test="${lookedUser.sex==0}">��</c:if><c:if
                    test="${lookedUser.sex!=0}">��</c:if></c:when><c:otherwise>��</c:otherwise></c:choose>�Ĺ�ע<q class="q"><a
                    href="/following?uid=${lookUser.id}">(${userNum.followingNum})</a></q></em></h4>
        <!--  S ѭ����ǰҳ����û��Ĺ�ע���� -->
        <div class="face">
            <t:userList list="${followingList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--  E ѭ����ǰҳ����û��Ĺ�ע���� -->
    </div>
    <div class="ap">
        <h4>
            <span class="opt"><a href="/follow/followed?uid=${lookUser.id}" class="fuc"><b>����</b>&gt;&gt;
                        </a></span>
            <em><c:choose><c:when test="${not empty userNum }"><c:if test="${lookedUser.sex==0}">��</c:if><c:if
                    test="${lookedUser.sex!=0}">��</c:if></c:when><c:otherwise>��</c:otherwise></c:choose>�ķ�˿<q class="q"><a
                    href="/followers?uid=${lookUser.id}">(${userNum.followedNum})</a></q></em>
        </h4>
        <!--  S ѭ����ǰҳ����û��ķ�˿���� -->
        <div class="face">
            <t:userList list="${followedList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--  E ѭ����ǰҳ����û��ķ�˿���� -->
    </div>
</div>