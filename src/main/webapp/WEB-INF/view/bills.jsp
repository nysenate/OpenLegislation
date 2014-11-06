<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Content">
    <script src="${ctxPath}/static/js/component/content/content.js"></script>
    <script src="${ctxPath}/static/js/component/content/bill/bill-home.js"></script>
</open-layout:head>
<open-layout:body pageId="bills">
    <div ng-controller="ContentCtrl" class="row" style="margin-top:1.5em">

    </div>
</open-layout:body>
<open-layout:footer/>