<%@ page contentType="text/html;charset=GBK" language="java" %>

<div class="twiPost" id="tw_post2">
    <%--<p id="original" class="ad_gov">����������</p>--%>
    <%@include file="/WEB-INF/inc/aside/live.jsp" %>
    <!--S ͼƬpost-->
    <div class="post">
        <%--<h3 class="ttl"><em>��������ߵľ���</em></h3>--%>
        <p class="avt"><a href="/profile"><i class="img" style="background-image:url(${lookUser.middleIcon})"></i></a>
        </p>

        <div class="tarea tarea_min blur">
            <div class="atFake"></div>
            <textarea name="tw_compser_textarea" data-ca="tw_composer_textarea" ></textarea></div>
        <%--<p class="postSub"><a data-ca="tw_composer_sbumit" href="javascript:void(0)" class="btn  noVis crJs_submit"><b>��
            ��</b></a></p>--%>
        <p class="postSub"><a data-ca="tw_composer_sbumit" href="javascript:void(0)"
                              class="btn btnB btn_dis crJs_submit" title="��Ctrl+Enter����΢��"><b>�� ��</b></a></p>
        <ul class="postAtt">
            <li class="crJs_em"><a data-ca="tw_composer_emote" class="fuc" href="javascript:void(0)"><i
                    class="i iS iAtt i37"></i><b>����</b></a>
            </li>
            <c:if test="${picForb ==false}">
                <li class="crJs_ig" id="multiPicUploadLi">
                    <a data-ca="tw_composer_img" class="fuc" href="javascript:void(0)"><span
                            class="swfuploadbutton" id="tw_swfuploadbutton"></span><i
                            class="i iS iAtt i36"></i><b>ͼƬ</b></a>

                    <div class="dropDown dropImg noDis crJs_ig_v">
                        <div class="dropD">
                            <i class="sign signArr"><q>��</q><q class="k2">��</q><q class="k3">��</q></i>

                            <div class="dropT">
                                <p class="oper"><a class="fuc crJs_tl" href="javascript:void(1)"><i
                                        class="i iCw"></i><b>����</b></a> | <a class="fuc crJs_tr"
                                                                             href="javascript:void(1)"><i
                                        class="i iCcw"></i><b>����</b></a> | <a class="fuc crJs_del"
                                                                              href="javascript:void(1)"><b>ɾ��</b></a>
                                </p>
                            </div>
                            <div class="dropC crJs_view noDis">
                                <div class="pics"><img src="http://s1.cr.itc.cn/img/s/ico/0168.gif"></div>
                            </div>
                            <div class="dropC crJs_pend">
                                <div class="pend">
                                    <div class="pbar"><p style="width:0"></p></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
            </c:if>
            <c:if test="${videoForb ==false}">
                <li class="crJs_mv" id="videoUploadLi"><a data-ca="tw_composer_video" class="fuc crJs_btn"
                                                          href="javascript:void(0)"><i
                        class="i iS iAtt i40"></i><b>��Ƶ</b></a>

                    <div class="dropDown dropVideo noDis">
                        <div class="dropD"><a class="close" title="�ر�" href="javascript:void(0)"><i
                                class="sign signX"><q>&#215;</q><q
                                class="k2">&#215;</q></i></a><i class="sign signArr"><q>&#9670;</q><q
                                class="k2">&#9671;</q><q class="k3">&#9670;</q></i>

                            <div
                                    class="dropT">��������Ƶ����ҳ����
                            </div>
                            <div class="dropC">
                                <div class="form form3">
                                    <div class="frm frmUrl">
                                        <p class="frmT cr_input_tip" style="display:none;">������ʾ����</p>

                                        <div class="frmC">

                                            <p class="frmTip frmTip2"><i class="i"></i><b>֧���Ѻ���Ƶ, �Ѻ�����, �ſ�, ����, ��6, 56������վ</b></p>
                                            <i class="txt blur cr_input_box"><input type="text" class="blur"
                                                                                    value="http://"></i><a
                                                href="javascript:void(0)" class="btn btnM btn_dis cr_input_sBtn"><b>ȷ
                                            ��</b></a>

                                        </div>

                                            <p>
                                                <i class="i i151"></i>
                                                <a href="javascript:void(0)" data-mt="btn-upv">�Լ��ϴ���Ƶ</a>
                                            </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
            </c:if>
            <li class="crJs_tp"><a data-ca="tw_composer_topic" class="fuc" href="javascript:void(0);"><i
                    class="i iS iAtt i38"></i><b>����</b></a></li>
            <c:if test="${voteForb == false}">
                <li class="crJs_tk"><a data-ca="tw_composer_vote" class="fuc" href="javascript:void(0);"><i
                        class="i iS iAtt i54"></i><b>ͶƱ</b></a>

                    <div class="dropDown dropVote noDis">
                        <div class="dropD">
                            <a href="javascript:void(0);" title="�ر�" class="close"><i class="sign signX"><q>��</q><q
                                    class="k2">��</q></i></a><i class="sign signArr"><q>��</q><q class="k2">��</q><q
                                class="k3">��</q></i>

                            <div class="dropT"></div>
                            <div class="dropC">

                            </div>
                        </div>
                    </div>
                </li>
            </c:if>
        </ul>

    </div>
    <!--E ͼƬpost-->
</div>

