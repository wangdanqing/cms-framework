<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-10
  Time: ����2:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${fn:length(followTopicList)>0}">
<div class="ap">
    <h4><em>�ҹ�ע�Ļ���</em></h4>
    <!--S lis(inc)-->
    <ul>
        <t:linkList list="${followTopicList}" />
    </ul>
    <!--E lis(inc)-->
</div>
</c:if>