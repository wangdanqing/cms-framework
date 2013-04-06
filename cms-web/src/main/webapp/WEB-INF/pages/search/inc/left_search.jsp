<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ page import="com.sohu.twitter.util.ProvCity" %>

<script>
    var QUERY_WORD = "${key}";
    document.domain = "sohu.com";
</script>

<!-- extattr timetype timerange -->

<div class="ToolBar">
    <div class="toolbara"><a href="javascript:void(1);" onclick="tw.search.toolBarBtn()" title="隐藏"></a></div>
    <div class="toolbar">
        <div class="toolb">
            <!--S toolbar(inc)-->
            <div class="nav nav4" id="tw_search_selectBar">
                <ul class="navG" data-key="extattr">
                    <li class="navL"><a href="javascript:void(1)" data-key="all" <c:if test="${empty extattr_str}">class="on"</c:if>>全部结果</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" data-key="pic" <c:if test="${not empty extattr_str && extattr_str eq 'pic'}">class="on"</c:if>><i class="i isch i105"></i>图片</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="video" <c:if test="${not empty extattr_str && extattr_str eq 'video'}">class="on"</c:if>><i class="i isch i106"></i>视频</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="link" <c:if test="${not empty extattr_str && extattr_str eq 'link'}">class="on"</c:if>><i class="i isch i108"></i>链接</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="vote" <c:if test="${not empty extattr_str && extattr_str eq 'vote'}">class="on"</c:if>><i class="i isch i109"></i>投票</a></li>
                </ul>

                <ul class="navG" data-key="timetype">
                    <li class="navL"><a href="javascript:void(1)" no-value="true" <c:if test="${empty timetype}">class="on"</c:if>>全部时间</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="h1" <c:if test="${not empty timetype && timetype eq 'h1'}">class="on"</c:if>>1小时内</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" data-key="h24" <c:if test="${not empty timetype && timetype eq 'h24'}">class="on"</c:if>>24小时内</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" data-key="d2" <c:if test="${not empty timetype && timetype eq 'd2'}">class="on"</c:if>>2天内</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" id="tw_search_custom_time" data-key="<c:if test="${not empty timetype && timetype eq 'custom'}">timetype=custom</c:if><c:if test="${not empty timerange}">&timerange=${timerange}</c:if>"
                                        class="-complex-params <c:if test="${not empty timetype && timetype eq 'custom'}">on</c:if>">其他</a>
                    </li>
                </ul>
                <div class="form formB crJs_time <c:if test="${empty timetype || timetype !='custom' }">nodis</c:if>">
                    <div class="frm">
                        <div class="frmC">
                            <i class="txt txt">
                                <input type="text" title="起始时间"
                                       value="<c:choose><c:when test="${timerange}"></c:when><c:otherwise>${start}</c:otherwise></c:choose>"
                                       onclick="window.WdatePicker({dateFmt:'yyyy-MM-dd',el:'tw_search_datapick_start',minDate:'2009-09-01', maxDate:'%y-%M-%d'});"
                                       id="tw_search_datapick_start" readonly="true">
                            </i>
                        </div>
                    </div>
                    <div class="frm">
                        <div class="frmC">
                            <i class="txt txt">
                                <input type="text" title="结束时间"
                                       value="<c:choose><c:when test="${timerange}"></c:when><c:otherwise>${end}</c:otherwise></c:choose>"
                                       onclick="window.WdatePicker({dateFmt:'yyyy-MM-dd',el:'tw_search_datapick_end',minDate:'2009-09-01', maxDate:'%y-%M-%d'});"
                                       id="tw_search_datapick_end" readonly="true">
                            </i>
                        </div>
                    </div>

                    <div class="frm frmSubmit">
                        <p class="btns"><a href="javascript:void(1)" class="btn btnM4"><b>确定</b></a></p>
                    </div>
                </div>

                <ul class="navG" data-key="local">
					<li class="navL"><a href="javascript:void(1)" data-key="all" <c:if test="${empty nearby}">class="on"</c:if>>全部地点</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" data-key="nearby=proc" <c:if test="${not empty nearby && nearby eq 'proc'}">class="on"</c:if>>${procity}</a></li>
					<li class="navL"><a href="javascript:void(1)" data-key="<c:if test="${not empty nearby && nearby eq 'custom'}">nearby=custom&geo_proc=${proc}&geo_city=${city}</c:if>" id="tw_search_custom_loc" class="<c:if test="${not empty nearby && nearby eq 'custom'}">on</c:if>">其他</a>
                    </li>
			    </ul>
                <div class="form formB crJs_loc <c:if test="${empty nearby || nearby !='custom'}">nodis</c:if>">
                    <form>
                        <div class="frm">
                            <div class="frmC">
                                <select val="<c:choose> <c:when test="${not empty nearby && nearby eq 'custom'}">${proc}</c:when><c:otherwise>${myProv}</c:otherwise></c:choose>">
                                <option value="0">省/直辖市</option>
                                </select>
                            </div>
                        </div>
                        <div class="frm">
                            <div class="frmC">
                                <select val="<c:choose><c:when test="${not empty nearby && nearby eq 'custom'}">${city}</c:when><c:otherwise>${myCity}</c:otherwise></c:choose>">
                                <option value="0">城市/地区</option>
                                </select>
                            </div>
                        </div>
                        <div class="frm frmSubmit">
                            <p class="btns"><a href="javascript:void(1)" class="btn btnM4"><b>确定</b></a></p>
                        </div>
                    </form>
                </div>

                <ul class="navG" data-key="uname">
                    <li class="navL"><a href="javascript:void(1)" data-key="all" <c:if test="${empty uname}">class="on"</c:if>>全部人的</a>
                    </li>
                    <li class="navL"><a href="javascript:void(1)" <c:if test="${not empty uname && uname eq userName}">class="on"</c:if>
                                        data-key="${userName}">我自己的</a></li>
                    <li class="navL"><a href="javascript:void(1)" id="tw_search_custom_user" <c:if test="${not empty uname && !uname eq userName}">class="on"</c:if>
                                        data-key="<c:choose><c:when test="${not empty uname && !uname eq userName}">${uname}</c:when><c:otherwise></c:otherwise></c:choose>">其他</a>
                    </li>
                </ul>
                <div class="form formB crJs_user <c:if test="${empty uname && !uname eq userName}">class="on"</c:if> nodis">
                 <%--<div class="form formB crJs_user <c:if test="${empty uname}">nodis</c:if>">--%>
                <div class="frm">
                    <div class="frmC">
                        <i class="txt txt">
                            <input type="text" value="<c:if test="${not empty uname}">${uname}</c:if>">
                        </i>
                    </div>
                </div>
                <div class="frm frmSubmit">
                    <p class="btns"><a href="javascript:void(1)" class="btn btnM4"><b>确定</b></a></p>
                </div>
            </div>

        </div>


        <div id="tw_search_history" class="ap history" data-type="${dataType}">
            <h4><em>搜索历史</em></h4>
            <div class="hist">
                <ul class="histlis">
                    <c:if test="${page_url eq 'searchHt'}">
                        <c:if test="${not empty topicSearchList}">
                            <c:forEach items="${topicSearchList}" var="topicSearch">
                                <li class="crJs_li"><a href="/twsearch/htSearch?key=${topicSearch.keyword}"><b>${topicSearch.keyword}</b><i class="ii" data-id="${topicSearch.keyid}"></i></a></li>
                            </c:forEach>
                        </c:if>
                    </c:if>
                    <c:if test="${page_url eq 'searchTw'}">
                           <c:if test="${not empty msgSearchList}">
                            <c:forEach items="${msgSearchList}" var="msgSearch">
                                <li class="crJs_li"><a href="/twsearch/twSearch?key=${msgSearch.keyword}"><b>${msgSearch.keyword}</b><i class="ii" data-id="${msgSearch.keyid}"></i></a></li>
                            </c:forEach>
                        </c:if>
                    </c:if>

                </ul>
                <p><a href="javascript:void(1)" onclick="tw.search.del_his_all({type:'${dataType}'})"
                      class="fuc">全部清除</a></p>
            </div>
        </div>
        <!--E toolbar(inc)-->
    </div>
</div>
</div>
