<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<t:frag>
    <c:if test="${not empty jsonData}">
        ${jsonData}
    </c:if>
</t:frag>