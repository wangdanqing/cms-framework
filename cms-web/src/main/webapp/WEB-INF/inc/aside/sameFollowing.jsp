<%--
  共同关注的页面碎片
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: 下午3:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${sameFollowingCount>0}">
    <div class="ap">
        <h4><span class="opt"><a class="fuc" href="/following?uid=${lookedUser.id}&type=1" target="_self" data-ca="recmnd_profile_same_follows_avatar"><b>更多</b>&gt;&gt;
        </a></span><c:choose><c:when test="${profilePage}"><em>你们共同的关注<q class="q">(${sameFollowingCount})</q></em></c:when><c:otherwise><em>你们共同的关注<%--<q class="q">(${sameFollowingCount})</q>--%></em></c:otherwise></c:choose></h4>
         <!--  S 循环显示共同关注部分 -->
        <div class="face">
            <t:userList list="${sameUserList}" type="light" curUid="${lookUser.id}"/>
        </div>
         <!--  E 循环显示共同关注部分 -->
    </div>
</c:if>