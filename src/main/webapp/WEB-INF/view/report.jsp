<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Open 2.0 | Reports">
    <script src="${ctxPath}/static/bower_components/highcharts/highcharts.js"></script>
    <script src="${ctxPath}/static/js/app.js"></script>
    <script src="${ctxPath}/static/js/component/report/daybreak/daybreak.js"></script>
</open-layout:head>
<open-layout:body pageId="report">
    <jsp:body>
        <div class="row" style="margin-top:1.5em">
            <div class="large-2 columns side-menu-bg">
                <nav>
                    <ul class="side-nav">
                        <li class='heading'>Report Types</li>
                        <li class="active"><a href="${ctxPath}/report/daybreak">LBDC Daybreak</a></li>
                        <li><a href="${ctxPath}/report/agenda-cal">Agenda/Calendar Check</a></li>
                        <li><a href="${ctxPath}/report/memo">Memo Dump</a></li>
                    </ul>
                </nav>
            </div>
            <div class="large-10 columns content-column">
                <div ng-view></div>
            </div>
        </div>
    </jsp:body>
</open-layout:body>
<open-layout:footer/>