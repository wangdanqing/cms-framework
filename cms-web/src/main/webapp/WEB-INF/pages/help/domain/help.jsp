<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/inc/globalConfig.jsp" %>

<!DOCTYPE html>

<%--<html lang="zh-CN">--%>
<html lang="zh-CN"
      class="DomUpgrade ${viewMode} ${sClearType} <c:if test='${sFontType == null}'>FontSys</c:if><c:if test='${sFontType == "0"}'>FontSimsun</c:if><c:if test='${sFontType == "1"}'>FontYahei</c:if>">
<head>
    <title>���� - �Ѻ�΢��</title>
    <t:css src="http://builder.oliv.cn/c/t2_1/g.css" altDomain="s4.cr.itc.cn"></t:css>
    <t:css src="http://builder.oliv.cn/c/t2_1/help.css" altDomain="s4.cr.itc.cn"></t:css>

    <%@include file="/WEB-INF/pages/common/js/seed.jsp" %>
    <%@include file="/WEB-INF/pages/common/js/pageview.jsp" %>
</head>

<body class="help">
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
                        <div class="mainC">
                            <!-- s help -->

                            <div class="helpnav">
                                <h4><em>��������</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_001'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_001" target="helpifr">ʲô���Ѻ�΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_002'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_002">���ӵ���Ѻ�΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_003'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_003">��ο���ʹ���Ѻ�΢��</a></li>
                                </ul>

                                <h4><em>��Ҫ���ܽ���</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_004'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_004">ע��͵�½</a></li>
                                    <li
                                            <c:if test="${key eq 'help_005'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_005">��������/��΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_006'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_006">��ע�ͷ�˿</a></li>
                                    <li
                                            <c:if test="${key eq 'help_007'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_007">ת��������</a></li>
                                    <li <c:if test="${key eq 'help_008'}">class="on"</c:if>><a
                                            href="/help/domain?key=help_008">��˽��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_009'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_009">��������</a></li>
                                    <li
                                            <c:if test="${key eq 'help_010'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_010">@ ����</a></li>
                                    <!--��ͼ�ϴ� -->
                                    <li
                                            <c:if test="${key eq 'help_0101'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_0101">��ͼ�ϴ�</a></li>
                                    <li
                                            <c:if test="${key eq 'help_01012'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_01012">ת����ͼ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_011'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_011">��������</a></li>
                                </ul>

                                <h4><em>������ֻ���΢��</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_012'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_012">���ʹ�ö̲��ŷ�΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_013'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_013">���ʹ���ֻ�Wap��΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_0131'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_0131">�Ѻ�΢���ֻ��ͻ���</a></li>
                                </ul>

                                <h4><em>�Ѻ�΢Ⱥ</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_025'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_025">ʲô���Ѻ�΢Ⱥ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_026'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_026">��μ���/�˳�΢Ⱥ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_021'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_021">��δ���΢Ⱥ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_022'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_022">�����΢Ⱥ�з�΢��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_023'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_023">��ι���΢Ⱥ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_024'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_024">��������</a></li>
                                </ul>

                                <h4><em>��������</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_014'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_014">����޸ĵ�½����</a></li>
                                    <li
                                            <c:if test="${key eq 'help_015'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_015">��θ�������ͷ��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_016'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_016">��ΰ󶨲���</a></li>
                                    <li
                                            <c:if test="${key eq 'help_017'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_017">������ø�������</a></li>
                                     <li
                                            <c:if test="${key eq 'help_01701'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_01701">��������Զ���ģ��</a></li>
                                    <li
                                            <c:if test="${key eq 'help_027'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_027">�����������</a></li>
                                </ul>

                                <h4><em>������Ϣ</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_018'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_018">�ٱ�������Ϣ</a></li>
                                    <li
                                            <c:if test="${key eq 'help_019'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_019">���������֤</a></li>
                                    <li
                                            <c:if test="${key eq 'help_020'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_020">��ϵ����</a></li>
                                </ul>
                            </div>
                            <div class="mainc">
                                <div class="ap helpframe">${content}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="Aside">
                    <div class="aside">
                        <p class="B2"><b class="Bi"></b></p>

                        <!-- e help -->
                    </div>
                </div>
            </div>
            <!--S copr(inc)-->
            <%@include file="/WEB-INF/inc/footer.jsp" %>
            <!--E copr(inc)-->
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    kola("newt.left.Init", function(Init) {

        Init._initPV();
        Init.initNavBar();
        setTimeout(Init.initSysFont, 15);
        window.setTimeout(function() {
            Init.initTitleClass();
        }, 100);
        Init.initComet();
        //��������
        var cloud = $("#t-lay div.cloud");
        if (cloud.length == 1) {
            window.setTimeout(function() {
                cloud.empty();
            }, 100);
        }
        if ($("body").data("tool_Tool") == 1) return;

        $("body").bind("mousedown",
                function(e) {
                    var o = $(e.target);
                    TOOL.side(TOOL.getSide(o));
                    TOOL.target(o);
                    Init._CA_Q(e);
                }).data("tool_Tool", 1);
        if (document.all && !!document.documentMode) window.IE8 = true;

        Init._initBeforeUnloadEvent();

        kola('newt.twitter.Twitter, newt.comet.Feed', function() {
            tw.build({type:"get_new",success:tw.feed.updateMyStatus});
        });

        kola('newt.tool.Url', function() {
            var to = $.url.param('to');
            if (to != "") {
                $("#" + to)[0].scrollIntoView();
            }
        });
    });
</script>
