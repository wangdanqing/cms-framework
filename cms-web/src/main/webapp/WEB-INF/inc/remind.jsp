<%@ page contentType="text/html;charset=GBK" language="java" %>
<div class="bubble bub2 bubDR remind_closed${lookUser.id}">
    <div class="bubC" >
        <a href="javascript:void(0)"  title="¹Ø±Õ" class="close" onclick="window.closeNotice('remind_closed${lookUser.id}','y')">
            <i class="sign signX"><q>¡Á</q><q class="k2">¡Á</q></i>
        </a>
        <i class="sign signArr"><q>¡ó</q><q class="k2">¡ô</q><q class="k3">¡ô</q></i>
		${remind}
	</div>
</div>