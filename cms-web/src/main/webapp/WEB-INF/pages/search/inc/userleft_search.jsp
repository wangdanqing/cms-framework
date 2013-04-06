<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://www.sohu.com/twitter" %>
<%@ page import="com.sohu.twitter.util.ProvCity" %>

<script>
    var QUERY_WORD = "${key}";
    document.domain = "sohu.com";
</script>


<div class="ToolBar">
    <div class="toolbara"><a onclick="kola('newt.search.pagelogic.Main', function () {tw.search.toolBarBtn();});" href="javascript: void(0);" title="隐藏"></a></div>
    <div class="toolbar">
        <div class="toolb">
            <!--S toolbar(inc)-->

            <div class="nav nav4" id="tw_search_selectBar">
                <ul class="navG" data-key="user_type">

                    <li class="navL"><a href="javascript:void(1)" data-key="0" <c:if test="${userType == 0}">class="on"</c:if>>全部结果</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="2" <c:if test="${userType==2}">class="on"</c:if>>认证用户</a></li>
                    <li class="navL"><a href="javascript:void(1)" data-key="1" <c:if test="${userType==1}">class="on"</c:if>>普通用户</a></li>
                </ul>
                <ul class="navG" data-key="nearby">
                        <li class="navL"><a href="javascript:void(1)" no-value="true" <c:if test="${empty nearby}">class="on"</c:if>>全部地点</a></li>
                        <li class="navL"><a href="javascript:void(1)" data-key="proc" <c:if test="${not empty nearby && nearby eq 'proc'}">class="on"</c:if>>${pro_city}</a></li>
                        <li class="navL"><a href="javascript:void(1)" data-key="<c:if test="${not empty nearby && nearby eq 'custom'}">nearby=custom&geo_proc=${proc}&geo_city=${city}</c:if>" id="tw_search_custom_loc" class="-complex-params <c:if test="${not empty nearby && nearby eq 'custom'}">on</c:if>">其他</a></li>
                </ul>
                <div class="form formB crJs_loc <c:if test="${empty nearby || nearby !='custom'}">nodis</c:if>">
                    <form>
                            <div class="frm">
                                <div class="frmC">
                                <select val="<c:choose><c:when test="${not empty nearby && nearby eq 'custom'}">${proc}</c:when><c:otherwise>${myProv}</c:otherwise></c:choose>">
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

            <ul class="navG" data-key="sex">
                <li class="navL"><a href="javascript:void(1)" data-key="0" <c:if test="${sex eq '0'}"> class="on" </c:if>>不限性别</a></li>
                <li class="navL"><a href="javascript:void(1)" data-key="1" <c:if test="${sex eq '1'}"> class="on" </c:if>>男</a></li>
                <li class="navL"><a href="javascript:void(1)" data-key="2" <c:if test="${sex eq '2'}"> class="on" </c:if>>女</a></li>
            </ul>
            <div class="form formB form4">
                <form>
                    <div class="frm frmschool">
                        <p class="frmT"><em>学校</em></p>

                        <div class="frmC">
                            <i class="txt txt">
                                <input name="school" class="-customsearch-value" id="search_school_value" value="<c:if test="${not empty school}">${school}</c:if>" type="text">
                            </i>
                        </div>
                    </div>
                    <div class="frm frmSubmit">
                        <p class="btns"><a class="btn btnM4 -startsearch" id="search_school" href="javascript: void(0);"><b>确定</b></a></p>
                    </div>
                </form>
            </div>
            <div class="form formB form4">
                <form>
                    <div class="frm frmschool">
                        <p class="frmT"><em>单位</em></p>

                        <div class="frmC">
                            <i class="txt txt">
                                <input name="company" class="-customsearch-value" id="search_company_value" value="<c:if test="${not empty company}">${company}</c:if>" type="text">
                            </i>
                        </div>
                    </div>
                    <div class="frm frmSubmit">
                        <p class="btns"><a class="btn btnM4 -startsearch" id="search_company" href="javascript: void(0);"><b>确定</b></a></p>
                    </div>
                </form>
            </div>
            <div class="form formB form4">
                <form>
                    <div class="frm frmschool">
                        <p class="frmT"><em>标签</em></p>

                        <div class="frmC"><i class="txt txt">
                            <input name="tag" type="text" class="-customsearch-value"  id="search_tag_value" value="<c:if test="${not empty tag}">${tag}</c:if>"></i>
                        </div>
                    </div>
                    <div class="frm frmSubmit">
                        <p class="btns"><a class="btn btnM4 -startsearch" id="search_tag" href="javascript: void(0);"><b>确定</b></a></p>
                    </div>
                </form>
            </div>
            <ul class="navG" data-key="birth_year">

                <li class="navL"><a href="javascript:void(1)"
                                    data-key="birth_year_min=${minYear}&birth_year_max=${maxYear}"
                                    class="-complex-params <c:if test="${birth_year_min eq minYear && (birth_year_max eq maxYear || birth_year_max eq 0)}">on</c:if>">不限年龄</a>
                </li>

                <li class="navL"><a href="javascript:void(1)"
                                    class="-complex-params <c:if test="${birth_year_min eq (year-18) && birth_year_max eq year}">on</c:if>"
                                    data-key="birth_year_min=${year-18}&birth_year_max=${year}">18岁以下</a>
                </li>

                <li class="navL"><a href="javascript:void(1)"
                                    class="-complex-params <c:if test="${birth_year_min eq (year - 22) && birth_year_max eq (year - 19)}">on</c:if>"
                                    data-key="birth_year_min=${year-22}&birth_year_max=${year-19}">19~22岁</a>
                </li>

                <li class="navL"><a href="javascript:void(1)"
                                    class="-complex-params <c:if test="${birth_year_min eq (year - 29) && birth_year_max eq (year - 23)}">on</c:if>"
                                    data-key="birth_year_min=${year-29}&birth_year_max=${year-23}">23~29岁</a>
                </li>

                <li class="navL"><a href="javascript:void(1)"
                                    class="-complex-params <c:if test="${birth_year_min eq (year - 39) && birth_year_max eq (year - 30)}">on</c:if>"
                                    data-key="birth_year_min=${year-39}&birth_year_max=${year-30}">30~39岁</a>
                </li>

                <li class="navL"><a href="javascript:void(1)"
                                    class="-complex-params <c:if test="${birth_year_min eq 0 && birth_year_max eq (year - 40)}">on</c:if>"
                                    data-key="birth_year_min=0&birth_year_max=${year-40}">40岁以上</a>
                </li>
            </ul>
        </div>


        <div id="tw_search_history" class="ap history" data-type="user">
            <h4><em>搜索历史</em></h4>

            <div class="hist">
                <ul class="histlis">
                    <c:if test="${not empty userSearchList}">
                        <c:forEach items="${userSearchList}" var="userSearch">
                            <li class="crJs_li"><a href="/twsearch/userSearch?key=${userSearch.keyword}"><b>${userSearch.keyword}
                            </b><i class="ii" data-id="${userSearch.keyid}"></i></a></li>
                        </c:forEach>
                    </c:if>
                </ul>

                <p><a href="javascript:void(1)" onclick="tw.search.del_his_all({type:'user'})" class="fuc">全部清除</a>
                </p>
            </div>
        </div>


        <!--E toolbar(inc)-->
    </div>
</div>
</div>
