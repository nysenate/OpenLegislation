<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<open-layout:head title="Open 2.0">
    <script src="${ctxPath}/static/js/src/component/content/content.js"></script>
    <script src="${ctxPath}/static/js/src/component/content/bill.js"></script>
</open-layout:head>
<open-layout:body>
    <open-component:top-nav/>
    <div id="content" ng-view class="animate-repeat row" autoscroll="true"></div>
</open-layout:body>
<open-layout:footer/>