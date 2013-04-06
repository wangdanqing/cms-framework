<%--
  Created by IntelliJ IDEA. ²ÂÄãÈÏÊ¶
  User: junrao
  Date: 11-5-9
  Time: ÏÂÎç8:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>
<div id="homeapp_guess"></div>
<t:js point="after">
    kola("newt.app.recommends.Guess", function (Guess) {
        new Guess({
            target: document.getElementById("homeapp_guess")
        });
    });
</t:js>