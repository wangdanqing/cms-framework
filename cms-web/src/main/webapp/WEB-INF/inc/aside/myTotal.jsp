<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: ����8:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="ap myTotal">
    <c:if test="${not empty pvCount && not empty commentCount && not empty rtCount}">
        <ul class="inf inf2">
            <li><i class="i iS iVisits"></i><em>Χ�ۣ�</em>${pvCount}</li>
            <li><i class="i iS iComments"></i><em>���ۣ�</em>${commentCount}</li>
            <li><i class="i iS iRetwis"></i><em>ת����</em>${rtCount}</li>
            <c:if test="${isPreExpr == null}">
                <li><i class="i iS iRss"></i><em><a href="http://t.sohu.com/rss/${lookedUser.id}" class="fuc" target="_blank"><b>����΢��</b></a></em></li>
            </c:if>
        </ul>
    </c:if>
    <!-- ������������ҳ��ʱ��ʾ -->
    <c:if test="${ not empty lookUser && not empty lookedUser && lookUser.id != lookedUser.id && isPreExpr == null}">
        <a href="javascript:void(1)"  onclick="kola('newt.report.Report',function() {tw.report.reportUser({uid:${lookedUser.id}});})" class="fuc"><i class="i iS iSug"></i><b>�ٱ�������Ϣ</b></a>
    </c:if>

</div>

