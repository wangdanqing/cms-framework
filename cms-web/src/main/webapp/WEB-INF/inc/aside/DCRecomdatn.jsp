<%--
  Created by IntelliJ IDEA.
  User: songh
  Date: 11-12-2
  Time: ÏÂÎç8:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>

<div id="other_recommends"></div>
<t:js point="after">
        kola("newt.app.recommends.EachotherFollowsWidget", function (EachotherFollowsWidget) {
            new EachotherFollowsWidget({
                target: document.getElementById("other_recommends")
            });
        });
</t:js>