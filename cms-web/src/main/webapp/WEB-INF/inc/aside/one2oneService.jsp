<%--
  Created by IntelliJ IDEA.
  User: ���
  Date: 12-4-23
  Time: ����12:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${lookedUser.id == lookUser.id}">
    <c:if test="${lookUser.vip == true}">
        <div class="ap">
            <h4><em>���߿ͷ�</em></h4>

            <p><i class="i iS i159" id="oneOnOne"></i><a class="fuc" href="javascript:void(0);"
                                           onclick="kola('newt.profile.PersonalAssistant',function(PerAssistantWin){ PerAssistantWin();})"><b>��ϵ���߿ͷ�</b></a>

            <p>��֤�û�ר���ͷ�ͨ���������������ǽ���һʱ������</p>
        </div>
    </c:if>
</c:if>