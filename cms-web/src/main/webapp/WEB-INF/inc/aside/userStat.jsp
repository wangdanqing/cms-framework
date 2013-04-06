<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="aps usrStat">

    <!--  S 显示用户头像和名称的部分 -->
    <c:if test="${not empty lookedUser }">
        <div class="usr">
            <b id="username" class="nm"><t:userName user="${lookedUser}"/><t:userVip user="${lookedUser}"/>
                <c:if test="${not empty olympic}">
                    <a target="_blank" href="http://t.sohu.com/event/33603"><i class="i i161" title="奥运火炬传递手"></i></a>
                </c:if>
            </b>
        </div>
    </c:if>
    <!--  E 显示用户头像和名称的部分 -->

    <%--显示vip描述的部分vipDesc--%>
    <c:if test="${not empty vipDescfull }">
        <div class="bio">${vipDescfull}
            <p class="fucs2"><a class="fuc" data-ca="vipImproveClick" href="http://t.sohu.com/news/vinfo"
                                target="_safe"><b>申请认证</b></a></p>
        </div>
    </c:if>


    <!--  S 显示用户关注数, 粉丝数和微博数的部分 -->
    <c:if test="${not empty userNum}">
        <div class="nexus">
            <c:if test="${lookedUser.id == lookUser.id }">
                <t:userNum userNum="${userNum}" profile="true" cur="${cur}"/>
            </c:if>
            <c:if test="${lookedUser.id != lookUser.id }">
                <c:choose>
                    <c:when test='${isPreExpr != null}'>
                        <t:userNum userNum="${userNum}" profile="false" cur="${cur}" isPreExpr="true"/>
                    </c:when>
                    <c:otherwise>
                        <t:userNum userNum="${userNum}" profile="false" cur="${cur}" isPreExpr="false"/>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </c:if>
    <!--  E 显示用户关注数, 粉丝数和微博数的部分 -->

    <!--  S 显示用户勋章的部分 -->

    <!--
    <div class="honor" id="js_usericon">
        <t:icon iconTypeList="${iconList}"/>
    </div>
    -->
    <!--  E 显示用户勋章的部分 -->
</div>