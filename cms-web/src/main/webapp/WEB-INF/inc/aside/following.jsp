<%--
  关注和粉丝的页面碎片
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="aps">
    <div class="ap">
        <h4>
            <span class="opt"><a href="/following?uid=${lookUser.id}" class="fuc"><b>更多</b>&gt;&gt;</a></span>
            <em><c:choose><c:when test="${not empty userNum }"><c:if test="${lookedUser.sex==0}">她</c:if><c:if
                    test="${lookedUser.sex!=0}">他</c:if></c:when><c:otherwise>我</c:otherwise></c:choose>的关注<q class="q"><a
                    href="/following?uid=${lookUser.id}">(${userNum.followingNum})</a></q></em></h4>
        <!--  S 循环当前页面的用户的关注部分 -->
        <div class="face">
            <t:userList list="${followingList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--  E 循环当前页面的用户的关注部分 -->
    </div>
    <div class="ap">
        <h4>
            <span class="opt"><a href="/follow/followed?uid=${lookUser.id}" class="fuc"><b>更多</b>&gt;&gt;
                        </a></span>
            <em><c:choose><c:when test="${not empty userNum }"><c:if test="${lookedUser.sex==0}">她</c:if><c:if
                    test="${lookedUser.sex!=0}">他</c:if></c:when><c:otherwise>我</c:otherwise></c:choose>的粉丝<q class="q"><a
                    href="/followers?uid=${lookUser.id}">(${userNum.followedNum})</a></q></em>
        </h4>
        <!--  S 循环当前页面的用户的粉丝部分 -->
        <div class="face">
            <t:userList list="${followedList}" type="light" curUid="${lookUser.id}"/>
        </div>
        <!--  E 循环当前页面的用户的粉丝部分 -->
    </div>
</div>