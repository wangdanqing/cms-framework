<%--
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div id="ap_footprint" class="ap <c:if test="${fn:length(lastVisitorList) <= 0}">noDis</c:if>">
    <h4>
		<em>最近访客</em>
	</h4>
    <!--S usrLi_usr2(inc)-->
    <div class="face">
        <t:userList type="horizontal" list="${lastVisitorList}" curUid="${lookUser.id}" addAttribute="{'isOthersProfile':${isOthersProfile}}"/>
    </div>
</div>