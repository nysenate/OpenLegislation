<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Open 2.0 | Reports">
    <script src="${ctxPath}/static/bower_components/highcharts/highcharts.js"></script>
    <script src="${ctxPath}/static/js/app.js"></script>
    <script src="${ctxPath}/static/js/common.js"></script>
    <script src="${ctxPath}/static/js/component/report/daybreak/daybreak.js"></script>
    <script src="${ctxPath}/static/js/component/report/daybreak/daybreak-report-error.js"></script>
</open-layout:head>
<open-layout:body pageId="report">
    <jsp:body>
        <div class="row" style="margin-top:1.5em">
            <div class="large-12 columns content-column">
                <!--<dl style="margin-bottom:0;" class="sub-nav">
                    <dt>Select Report</dt>
                    <dd class="active"><a href="#">LBDC Daybreak</a></dd>
                </dl>
                <hr style="margin-top:0;"/>-->
                <div ng-view autoscroll="true"></div>
            </div>
        </div>
    </jsp:body>
</open-layout:body>
<open-layout:footer/>