<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="bs-docs-example">
    <div class="btn-group">
        <button class="btn"><a href="/channel/list" id="addnew">刷新</a></button>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th>#</th>
            <th>ID</th>
            <th>名称</th>
            <th>目录</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>

        <form action="/channel/delete" method="post">
            <c:forEach items="${list}" var="channel" varStatus="idx">
                <tr>
                    <td><c:out value="${idx.count}"/></td>
                    <td><c:out value="${channel.id}"/></td>
                    <td><c:out value="${channel.name}"/></td>
                    <td><c:out value="${channel.dir}"/></td>
                    <td>修改 <label>删除 <input type="radio" name="id" value="<c:out value="${idx.count}"/>"/> </label></td>
                </tr>
            </c:forEach>

            <tr>
                <td colspan="4">
                </td>
                <td>
                    <button type="submit" id="deletesubmit" class="btn">删除</button>
                </td>

            </tr>
        </form>

        </tbody>
    </table>

    <div id="addNews">
        <form action="/channel/add" method="post">
            <fieldset>
                <legend>新增频道</legend>
                <label>名称</label>
                <input type="text" name="name" placeholder="name...">
                <span class="help-block">频道名称，如：一苇阅读网</span>

                <label>目录</label>
                <input type="text" name="dir" placeholder="dir...">
                <span class="help-block">网站存储目录，如：txt7.com.cn则为:txt7</span>
                <button type="submit" class="btn">提交</button>
            </fieldset>
        </form>
    </div>
</div>