<%--
  ��ͬ��ע��ҳ����Ƭ
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: ����3:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${sameFollowingCount>0}">
    <div class="ap">
        <h4><span class="opt"><a class="fuc" href="/following?uid=${lookedUser.id}&type=1" target="_self" data-ca="recmnd_profile_same_follows_avatar"><b>����</b>&gt;&gt;
        </a></span><c:choose><c:when test="${profilePage}"><em>���ǹ�ͬ�Ĺ�ע<q class="q">(${sameFollowingCount})</q></em></c:when><c:otherwise><em>���ǹ�ͬ�Ĺ�ע<%--<q class="q">(${sameFollowingCount})</q>--%></em></c:otherwise></c:choose></h4>
         <!--  S ѭ����ʾ��ͬ��ע���� -->
        <div class="face">
            <t:userList list="${sameUserList}" type="light" curUid="${lookUser.id}"/>
        </div>
         <!--  E ѭ����ʾ��ͬ��ע���� -->
    </div>
</c:if>