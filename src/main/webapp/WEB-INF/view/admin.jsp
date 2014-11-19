<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Open 2.0 Admin">
    <script src="${ctxPath}/static/js/src/component/report/daybreak/report-summary.js"></script>
    <script src="${ctxPath}/static/js/src/component/report/daybreak/report-error.js"></script>
</open-layout:head>
<open-layout:body>
    <open-component:top-nav-admin/>
    <div id="content" ng-view class="row content-view" autoscroll="true"></div>
</open-layout:body>
<open-layout:footer/>