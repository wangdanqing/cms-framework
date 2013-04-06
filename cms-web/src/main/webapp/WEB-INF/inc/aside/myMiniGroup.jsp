<%--
  微群的页面碎片
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: 下午8:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${fn:length(groupList)>0}">
    <div class="ap js_minigroup">
        <h4>
            <!--  微群最多显示10个 -->
            <c:if test="${fn:length(groupList)>10}">
                <span class="opt">
                    <a href="javascript:void(0);" class="fuc"><b>展开</b><i class="sign signArr7"><q>◇</q><q
                            class="k2">×</q></i></a>
                    <a href="javascript:void(0);" class="fuc noDis"><b>收起</b><i class="sign signArr7_act"><q>◇</q><q
                            class="k2">×</q></i></a>
                </span>
            </c:if>
            <!--  判断页面显示的用户的性别 -->
            <em><c:if test="${lookedUser.sex==0}">她</c:if><c:if test="${lookedUser.sex!=0}">他</c:if>的微群</em>
        </h4>
        <!--  S 循环显示微群部分 -->
        <ul class="lis lisCol2">
            <t:linkList list="${groupList}" maxLength="10"/>
        </ul>
        <!--  E 循环显示微群部分 -->
    </div>
</c:if>