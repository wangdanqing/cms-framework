<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@include file="/WEB-INF/inc/globalConfig.jsp" %>

<!DOCTYPE html>

<html lang="zh-CN">
<head>

    <title>用户认证 - 搜狐微博</title>
    <t:css src="http://builder.oliv.cn/c/t2_1/g.css" altDomain="s4.cr.itc.cn"></t:css>
    <t:css src="http://builder.oliv.cn/c/t2_1/help.css" altDomain="s4.cr.itc.cn"></t:css>
    <%@include file="/WEB-INF/pages/common/js/seed.jsp" %>
    <%@include file="/WEB-INF/pages/common/js/pageview.jsp" %>
</head>

<body class="verify">
<div class="SKY">
    <!--S 托盘(inc)-->
    <%@include file="/WEB-INF/pages/common/toolbar.jsp" %>
    <!--E 托盘(inc)-->
    <!--S 浮层-->
    <div class="Lay">
        <!--S 回顶部-->
       <!-- <p class="gotop" style="display:none" id="gotoTop"><a title="回顶部'/>" href="javascript:void(0);" data-ca="newt_common_backToTop"><i
                class="ii"></i></a></p> -->
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
							<!-- s verify -->
							<div class="ap verifyc">
								<div class="bnr">
									<div class="cnt">
										<p>凡获得搜狐微博认证的用户，可以在用户名后面看见<i class="p pVer" title="认证用户"><i class="png"></i></i>标识。 它意味着搜狐微博已经核实过这位用户的真实身份。当TA在搜狐微博上发言，即代表这是其本人或其授权人代表TA在对公众说话。</p>
										<p class="p_1">搜狐微博在进行认证的同时，会对该用户添加认证描述。此后，鼠标划过相关标识时，都会自动显示TA的认证描述。</p>

									</div>
								</div>
								<div class="ap condition">
									<h4><em>认证条件</em></h4>
									<ul class="condi_ul">
										<li><var>1、</var>大众名人：指演艺、体育、文化等领域内的知名人士或团体；</li>
										<li><var>2、</var>行业名人：指在某一领域或行业内具有一定影响力的知名人士；</li>

										<li><var>3、</var>知名媒体、栏目及核心从业人员，包括制片人、编导、编辑、记者及以上人员；</li>
										<li><var>4、</var>政府、学校、知名企业、机构、网站和应用；</li>
										<li><var>5、</var>知名草根、新闻当事人、网络红人。</li>
									</ul>
								</div>
								<div class="ap verifystep">

									<h4><em>认证流程</em></h4>
									<div class="verify_steps">
										<div class="verify_tit"><i class="i i114"></i>个人认证流程</div>
										<ul class="verify_step">
											<li class="step1"><div class="stepcontainer"><div class="stepcon"><var>1</var><p class="stepc">首先确定您符合搜狐微博认证的标准；</p></div></div></li>
											<li class="step2"><div class="stepcontainer"><div class="stepcon"><var>2</var><p class="stepc">须确保您已开通搜狐微博，有清晰头像，并有准确详实的个人介绍。同时，您的微博需要符合以下标准：绑定手机、至少关注30人、发布多于30条内容、粉丝数超过50；</p></div></div></li>

											<li class="step3"><div class="stepcontainer"><div class="stepcon"><var>3</var><p class="stepc">下载 <a href="http://zt.blog.sohu.com/upload/sohuweibovip2011/vip-new.doc">搜狐微博认证申请表</a>，按照要求填写内容；<br>提交内容必须包含：准确详实的个人介绍、可核实的联系方式，身份证明和工作证明的扫描件（可附在表内），一并发至：<a href="mailto:tsohu@vip.sohu.com">tsohu@vip.sohu.com</a>；</p></div></div></li>
											<li class="step4"><div class="stepcontainer"><div class="stepcon"><var>4</var><p class="stepc">搜狐微博工作人员会对您的身份进行核实，通过认证后，您将在个人页面看到新增的认证标识。</p></div></div></li>
										</ul>
									</div>

									<div class="verify_steps verify_steps2">
										<div class="verify_tit"><i class="i i115"></i>机构认证流程</div>
										<ul class="verify_step">
											<li class="step1"><div class="stepcontainer"><div class="stepcon"><var>1</var><p class="stepc">必须使用合法拥有的品牌、铭牌，或大众所熟知的名称注册，认证后不能自由更改；</p></div></div></li>
											<li class="step2"><div class="stepcontainer"><div class="stepcon"><var>2</var><p class="stepc">账号资料完整、真实；账号维护责任人获得机构合法授权；</p></div></div></li>
											<li class="step3"><div class="stepcontainer"><div class="stepcon"><var>3</var><p class="stepc">

												机构账号需在确保一定活跃度的前提下，认真填写相应申请表格，发送至：<a href="mailto:tsohu@vip.sohu.com">tsohu@vip.sohu.com</a><br>
												媒体申请认证请下载：<a href="http://zt.blog.sohu.com/upload/sohuweibovip2011/meiti-vip.doc">媒体认证申请表</a><br>
												企业申请认证请下载：<a href="http://zt.blog.sohu.com/upload/sohuweibovip2011/qiye-vip.doc">企业认证申请表</a><br>
												学校申请认证请下载：<a href="http://zt.blog.sohu.com/upload/sohuweibovip2011/xuexiao-vip.doc">学校认证申请表</a><br>
												团体组织申请认证请下载：<a href="http://zt.blog.sohu.com/upload/sohuweibovip2011/tuanti-vip.doc">团体组织认证申请表</a></p></div>

											</div></li>
											<li class="step4"><div class="stepcontainer"><div class="stepcon"><var>4</var><p class="stepc">搜狐微博工作人员会向机构进行核实，通过认证后，您将在机构微博页面看到新增的认证标识。</p></div></div></li>
										</ul>
									</div>
								</div>
								<div class="ap contact">
									<h4><em>常见问题</em></h4>

									<dl class="dl">
										<dt class="dt">搜狐认证用户身份的作用是什么？</dt>
										<dd class="dd">搜狐微博认证仅作为标识使用，认证用户在功能上与无实名认证用户完全一样，不享有任何特权。</dd>
										<dt class="dt">什么是认证描述？如何看认证用户的官方资料？</dt>
										<dd class="dd">为避免身份混淆，搜狐微博在进行认证的同时也为名博进行了认证描述。当鼠标划过认证标识处，会显示认证用户的认证描述。</dd>
										<dt class="dt">发现有人冒用认证标志，怎么举报？</dt>

										<dd class="dd">搜狐微博主张真实身份的认证。如果你发现某个名人账户是其他人盗用的，可以通过他的主页工具箱里的"举报"按钮举报，也可以通过邮件与我们直接联系：<a href="mailto:tsohu@vip.sohu.com"> tsohu@vip.sohu.com</a>
                                        </dd>
										<dt class="dt">如何与我们联系？</dt>
										<dd class="dd">搜狐微博客户服务电话：010-58511234-按1键<br>搜狐微博客户服务邮箱：<a href="mailto:tsohu@vip.sohu.com">tsohu@vip.sohu.com </a></dd>
									</dl>
								</div>

							</div>
							<!-- e verify -->
                        </div>
                    </div>
                </div>
            </div>
            <!--S copr(inc)-->
            <%@include file="/WEB-INF/inc/footer.jsp" %>
            <!--E copr(inc)-->
        </div>
    </div>
</div>


<script type="text/javascript">

    kola('newt.component.NavBar',function(Bar){
        new Bar('#crjs_headbar', {
            search:'.jumper'
        });
    });
</script>
</body>
</html>
