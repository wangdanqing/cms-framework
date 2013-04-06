<%--
  Created by IntelliJ IDEA.
  User: chenxiaojian
  Date: 11-12-11
  Time: 上午10:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${followingFollowsCount>0}">
    <div class="ap">
        <h4><span class="opt"><a class="fuc" href="/followers?uid=${lookedUser.id}&type=1"><b>更多</b>&gt;&gt;
        </a></span><c:choose><c:when test="${profilePage}"><em>这些人也关注${ta}<q class="q">(${followingFollowsCount})</q></em></c:when><c:otherwise><em>${ta}的粉丝中，你的关注<%--<q class="q">(${followingFollowsCount})</q>--%></em></c:otherwise></c:choose></h4>
         <!--  S 循环显示 他的粉丝中，你的关注 部分 -->
        <div class="face">
            <t:userList list="${followingFollowsList}" type="light" curUid="${lookUser.id}"/>
        </div>
         <!--  E 循环显示 他的粉丝中，你的关注 部分 -->
    </div>
</c:if>