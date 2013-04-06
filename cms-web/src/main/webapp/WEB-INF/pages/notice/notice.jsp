<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ include file="/WEB-INF/inc/globalConfig.jsp" %>
<%@ taglib prefix="tutil" uri="http://www.sohu.com/tutil" %>

<html lang="zh-CN"
      class="DomUpgrade ${viewMode} ${sClearType} <c:if test='${sFontType == null}'>FontSys</c:if><c:if test='${sFontType == "0"}'>FontSimsun</c:if><c:if test='${sFontType == "1"}'>FontYahei</c:if>">
<head>

    <%--<t:css src="http://builder.oliv.cn/c/t2_1/g.css" altDomain="s4.cr.itc.cn"></t:css>--%>
    <link rel="stylesheet" type="text/css" href="http://builder.oliv.cn/c/t2_1/g.css">
    <script type="text/javascript">
        var _view_context = '${frontContext}';
        window.LOOKEDUSER = {theme:<c:out value="${themeInfo}" escapeXml="false"/>};
        window.LOOKUSER = {themeId:"<c:out value="${lookUser.myThemeIdT3}"/>"};
    </script>
</head>

<body>
<t:frag>
<div class="SKY">
    <!--S ����(inc)-->
    <%@include file="/WEB-INF/pages/common/toolbar.jsp" %>
    <!--E ����(inc)-->
    <!--S ����-->
    <div class="Lay">

        <!--S �ض���-->
        <p class="gotop" style="display:none" id="gotoTop"><a title="�ض���'/>" href="javascript:void(0);"
                                                              data-ca="newt_common_backToTop"><i class="ii"></i></a></p>
        <!--E �ض���-->


    </div>
    <!--E ����-->
</div>
<div class="LAND">
    <div class="land">
        <div class="Bgs"><p class="Bg1"></p>

            <p class="Bg2"></p>

            <p class="Bg3"></p></div>
        <div class="LANDBG">
            <div class="Bdy">
                <p class="B0"><b class="Bi"></b></p>

                <div class="Main">
                    <div class="main">
                        <div class="mainT">
                            <p class="B1"><b class="Bi"></b></p>

                            <div class="h3">
                                <div class="opt"><a href="/settings/personality" target="_self">֪ͨ����</a></div>
                                <h3>֪ͨ</h3>
                                <span class="tip"><i class="sum">���յ�<b>${totalCount}</b>��֪ͨ</i><a
                                        href="javascript:void(0)" class="fuc" data-mt="btn:clear-all"><i
                                        class="i iDel3"></i><b>����ռ���</b></a></span>
                            </div>
                        </div>
                        <div class="mainC">

                            <!--S streamMsg(inc)-->
                            <div class="ap stream streamNtc">
                                <!--S usrLi_usr_all(inc)-->
                                <div class="noCnt"><c:if test="${totalCount == 0}"><p class="ttl">��û���յ�֪ͨ</p></c:if>
                                </div>

                                <div class="twis">
                                    <c:if test="${not empty usrNoticeList}">
                                        <c:forEach var="usrNotice" items="${usrNoticeList}" varStatus="varStatus">
                                            <div class="twi"
                                                 data-val='{"noticeId":"${usrNotice.id}", "sendUid":"${usrNotice.sendUid}", "receiverId":"${usrNotice.receiveUid}", "nickName":"${uidToMapUsrMapping[usrNotice.sendUid].username}","isAcceptNotice":${isAcceptNotice}}'>
                                                <p class="twiStat_ud"><i class="i iNew2"></i></p>

                                                <div class="twiT">
                                                    <p class="avt"><a href="/people?uid=${usrNotice.sendUid}"
                                                                      data-content='{"type":"nick", "nick":"${uidToMapUsrMapping[usrNotice.sendUid].username}"}'>
                                                        <i class="img"
                                                           data-content='{"type":"nick", "nick":"${uidToMapUsrMapping[usrNotice.sendUid].username}"}'
                                                           style="background-image: url(${uidToMapUsrMapping[usrNotice.sendUid].middleIcon});"></i></a>
                                                    </p>
                                                    <b class="pd">
                                                        <b class="nm">
                                                            <a href="/people?uid=${usrNotice.sendUid}"
                                                               data-content='{"type":"nick", "nick":"${uidToMapUsrMapping[usrNotice.sendUid].username}"}'>${uidToMapUsrMapping[usrNotice.sendUid].username}</a>
                                                            <t:userVip user="${uidToMapUsrMapping[usrNotice.sendUid]}"/>
                                                        </b>
                                                        <b class="memo <c:if test="${ empty uidToMapUsrMemo[usrNotice.sendUid]}">nodis</c:if>">
                                                            <a class="fuc" href="javascript:void(0)" class="fuc"
                                                               onclick="kola('newt.memo.Memo','tw.setMemo.showMemoEdit({uid:\'${usrNotice.sendUid}\',memo:\'${uidToMapUsrMemo[usrNotice.sendUid]}\'})')"
                                                               title="����˴����༭��ע">��<b
                                                                    data-text="${uidToMapUsrMemo[usrNotice.sendUid]}"
                                                                    data-memoId="${usrNotice.sendUid}">${uidToMapUsrMemo[usrNotice.sendUid]}</b>��</a></b>
                                                    </b>
                                                </div>
                                                <div class="twiA"><p class="ugc">${usrNotice.content}</p></div>
                                                <div class="twiB">
                                                    <b class="tm">${tutil:format(usrNotice.cTime)}</b>
                                                    <ul class="tags">
                                                        <li class="tag"><a class="fuc" href="javascript:void(0)"
                                                                           data-mt="btn:del-notice"><b>ɾ��<q></q></b></a>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                </div>

                                <!--E usrLi_usr_all(inc)-->
                                <!--S page(inc)-->
                                <t:pager type="pageNum" pageModel="${pageModel}"/>
                                <!--E page(inc)-->
                            </div>
                            <!--E streamMsg(inc)-->
                        </div>
                    </div>
                </div>
                <div class="Aside">
                    <div class="aside">
                        <div class="ap">
                            <h4><em>֪ͨ����</em></h4>
                            <dl>
                                <dd>֪ͨ��΢���ٷ����͸��û����֪ͨ��һ����ʾ��ʽ��</dd>
                                <dd>֪ͨ�����������˽����ķ�ʽ</dd>
                            </dl>
                        </div>
                    </div>
                </div>
            </div>
            <!--S footer(inc)-->
            <%@include file="/WEB-INF/inc/footer.jsp" %>
            <!--E footer(inc)-->
        </div>
    </div>
    </t:frag>
</div>
</body>
</html>