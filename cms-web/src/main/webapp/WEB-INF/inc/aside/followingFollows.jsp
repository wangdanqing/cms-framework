<%--
  Created by IntelliJ IDEA.
  User: chenxiaojian
  Date: 11-12-11
  Time: ����10:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${followingFollowsCount>0}">
    <div class="ap">
        <h4><span class="opt"><a class="fuc" href="/followers?uid=${lookedUser.id}&type=1"><b>����</b>&gt;&gt;
        </a></span><c:choose><c:when test="${profilePage}"><em>��Щ��Ҳ��ע${ta}<q class="q">(${followingFollowsCount})</q></em></c:when><c:otherwise><em>${ta}�ķ�˿�У���Ĺ�ע<%--<q class="q">(${followingFollowsCount})</q>--%></em></c:otherwise></c:choose></h4>
         <!--  S ѭ����ʾ ���ķ�˿�У���Ĺ�ע ���� -->
        <div class="face">
            <t:userList list="${followingFollowsList}" type="light" curUid="${lookUser.id}"/>
        </div>
         <!--  E ѭ����ʾ ���ķ�˿�У���Ĺ�ע ���� -->
    </div>
</c:if>