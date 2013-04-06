<%--
  人气推荐
  Created by IntelliJ IDEA.
  User: chenxiaojian
  Date: 11-12-11
  Time: 下午8:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div id="app_hotrecommends"></div>
<script type="text/javascript">
    kola("newt.app.recommends.HotRecommendsSimple", function (HotRecommendsSimple) {
        new HotRecommendsSimple({
            target: document.getElementById("app_hotrecommends")
        });
    });
</script>