<%--
  ΢Ⱥ��ҳ����Ƭ
  Created by IntelliJ IDEA.
  User: junrao
  Date: 11-5-9
  Time: ����8:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<c:if test="${fn:length(groupList)>0}">
    <div class="ap js_minigroup">
        <h4>
            <!--  ΢Ⱥ�����ʾ10�� -->
            <c:if test="${fn:length(groupList)>10}">
                <span class="opt">
                    <a href="javascript:void(0);" class="fuc"><b>չ��</b><i class="sign signArr7"><q>��</q><q
                            class="k2">��</q></i></a>
                    <a href="javascript:void(0);" class="fuc noDis"><b>����</b><i class="sign signArr7_act"><q>��</q><q
                            class="k2">��</q></i></a>
                </span>
            </c:if>
            <!--  �ж�ҳ����ʾ���û����Ա� -->
            <em><c:if test="${lookedUser.sex==0}">��</c:if><c:if test="${lookedUser.sex!=0}">��</c:if>��΢Ⱥ</em>
        </h4>
        <!--  S ѭ����ʾ΢Ⱥ���� -->
        <ul class="lis lisCol2">
            <t:linkList list="${groupList}" maxLength="10"/>
        </ul>
        <!--  E ѭ����ʾ΢Ⱥ���� -->
    </div>
</c:if>