<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="ap">
    <h4><em>相关用户</em></h4>
    <div class="usrLi">
        <c:if test="${not empty relateUserList}">
            <c:forEach items="${relateUserList}" var="usr">
                <div class="usr3">
                    <p class="avt avt32"><a href="${usr.fullUrl}"><i
                            data-content='{"type":"nick","nick":"${usr.userName}"}' class="img"
                            style="background-image:url(${usr.middleIcon})"></i></a></p>
                    <b class="nm">
                        <a href="${usr.fullUrl}"
                           data-content='{"type":"nick","nick":"${usr.userName}"}'>${usr.userName}
                        </a></b>

                    <p class="fucs" id="follow7_btn_${usr.userid}">
                        <c:if test="${usr.followstat eq '2'}">
                            <a title="+关注" href="javascript:void(1)"
                               onclick="tw.build({type:'follow7',uid:${usr.userid}})"
                               class="fuc fucAdd"><i class="add"><q class="k1"></q><q class="k2"></q></i><b>关注</b></a>
                        </c:if>
                        <c:if test="${usr.followstat eq '1'}">
                            <a title="+关注" href="javascript:void(1)" class="fuc fucAdd dis"><b>已关注</b></a>
                        </c:if>
                    </p>
                    <q class="relat"></q>
                </div>
            </c:forEach>
        </c:if>
        <p class="opt"><a href="/twsearch/userSearch?key=${key}">查看更多用户&gt;&gt;</a></p>
    </div>
</div>
