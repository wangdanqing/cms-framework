<%--
  Created by IntelliJ IDEA.
  User: 国昊
  Date: 12-4-23
  Time: 下午12:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${lookedUser.id == lookUser.id}">
    <c:if test="${lookUser.vip == true}">
        <div class="ap">
            <h4><em>在线客服</em></h4>

            <p><i class="i iS i159" id="oneOnOne"></i><a class="fuc" href="javascript:void(0);"
                                           onclick="kola('newt.profile.PersonalAssistant',function(PerAssistantWin){ PerAssistantWin();})"><b>联系在线客服</b></a>

            <p>认证用户专属客服通道，您的问题我们将第一时间解决。</p>
        </div>
    </c:if>
</c:if>