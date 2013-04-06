<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/inc/globalConfig.jsp" %>

<!DOCTYPE html>

<%--<html lang="zh-CN">--%>
<html lang="zh-CN"
      class="DomUpgrade ${viewMode} ${sClearType} <c:if test='${sFontType == null}'>FontSys</c:if><c:if test='${sFontType == "0"}'>FontSimsun</c:if><c:if test='${sFontType == "1"}'>FontYahei</c:if>">
<head>
    <title>帮助 - 搜狐微博</title>
    <t:css src="http://builder.oliv.cn/c/t2_1/g.css" altDomain="s4.cr.itc.cn"></t:css>
    <t:css src="http://builder.oliv.cn/c/t2_1/help.css" altDomain="s4.cr.itc.cn"></t:css>

    <%@include file="/WEB-INF/pages/common/js/seed.jsp" %>
    <%@include file="/WEB-INF/pages/common/js/pageview.jsp" %>
</head>

<body class="help">
<div class="SKY">
    <!--S 托盘(inc)-->
    <%@include file="/WEB-INF/pages/common/toolbar.jsp" %>
    <!--E 托盘(inc)-->
    <!--S 浮层-->
    <div class="Lay">

        <!--S 回顶部-->
        <p class="gotop" style="display:none" id="gotoTop"><a title="回顶部'/>" href="javascript:void(0);"
                                                              data-ca="newt_common_backToTop"><i class="ii"></i></a></p>
        <!--E 回顶部-->


    </div>
    <!--E 浮层-->
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
                                <h4><em>新手入门</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_001'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_001" target="helpifr">什么是搜狐微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_002'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_002">如何拥有搜狐微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_003'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_003">如何快速使用搜狐微博</a></li>
                                </ul>

                                <h4><em>主要功能介绍</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_004'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_004">注册和登陆</a></li>
                                    <li
                                            <c:if test="${key eq 'help_005'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_005">基本操作/发微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_006'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_006">关注和粉丝</a></li>
                                    <li
                                            <c:if test="${key eq 'help_007'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_007">转发和评论</a></li>
                                    <li <c:if test="${key eq 'help_008'}">class="on"</c:if>><a
                                            href="/help/domain?key=help_008">发私信</a></li>
                                    <li
                                            <c:if test="${key eq 'help_009'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_009">搜索功能</a></li>
                                    <li
                                            <c:if test="${key eq 'help_010'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_010">@ 功能</a></li>
                                    <!--多图上传 -->
                                    <li
                                            <c:if test="${key eq 'help_0101'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_0101">多图上传</a></li>
                                    <li
                                            <c:if test="${key eq 'help_01012'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_01012">转评带图</a></li>
                                    <li
                                            <c:if test="${key eq 'help_011'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_011">常见问题</a></li>
                                </ul>

                                <h4><em>如何用手机发微博</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_012'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_012">如何使用短彩信发微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_013'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_013">如何使用手机Wap版微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_0131'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_0131">搜狐微博手机客户端</a></li>
                                </ul>

                                <h4><em>搜狐微群</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_025'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_025">什么是搜狐微群</a></li>
                                    <li
                                            <c:if test="${key eq 'help_026'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_026">如何加入/退出微群</a></li>
                                    <li
                                            <c:if test="${key eq 'help_021'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_021">如何创建微群</a></li>
                                    <li
                                            <c:if test="${key eq 'help_022'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_022">如何在微群中发微博</a></li>
                                    <li
                                            <c:if test="${key eq 'help_023'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_023">如何管理微群</a></li>
                                    <li
                                            <c:if test="${key eq 'help_024'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_024">常见问题</a></li>
                                </ul>

                                <h4><em>个人设置</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_014'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_014">如何修改登陆密码</a></li>
                                    <li
                                            <c:if test="${key eq 'help_015'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_015">如何更换个人头像</a></li>
                                    <li
                                            <c:if test="${key eq 'help_016'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_016">如何绑定博客</a></li>
                                    <li
                                            <c:if test="${key eq 'help_017'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_017">如何设置个性域名</a></li>
                                     <li
                                            <c:if test="${key eq 'help_01701'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_01701">如何设置自定义模版</a></li>
                                    <li
                                            <c:if test="${key eq 'help_027'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_027">如何设置字体</a></li>
                                </ul>

                                <h4><em>其他信息</em></h4>
                                <ul>
                                    <li
                                            <c:if test="${key eq 'help_018'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_018">举报不良信息</a></li>
                                    <li
                                            <c:if test="${key eq 'help_019'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_019">如何申请认证</a></li>
                                    <li
                                            <c:if test="${key eq 'help_020'}">class="on"</c:if> ><a
                                            href="/help/domain?key=help_020">联系我们</a></li>
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
        //处理导航条
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
