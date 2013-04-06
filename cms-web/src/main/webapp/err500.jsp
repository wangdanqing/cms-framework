<%@ page contentType="text/html;charset=GBK" language="java" isErrorPage="true"%><%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%
    //需要在次处输出异常堆栈,方便复现问题
    if(exception!=null){
        exception.printStackTrace();
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb18030">
    <title>服务器不在家</title>
    <t:css src="http://builder.oliv.cn/c/t2_1/sys.css" altDomain="s4.cr.itc.cn"></t:css>
</head>
<body>
<div class="Tray">
    <p class="Png"></p>

    <div class="tray">
        <div class="logo">
            <h2>
                <i class="png"></i><a title="来搜狐微博看我" href="/home">搜狐微博</a>
            </h2>
        </div>
    </div>
</div>
<div class="bdy">
    <div class="sys sys500">
        <p class="sysImg"></p>

        <p class="sysTurn">
            <a href="javascript:history.go(-1);">返回上一页</a><span class="spl">|</span><a
                href="http://t.sohu.com/">首页</a>
        </p>
    </div>
</div>
</body>
</html>