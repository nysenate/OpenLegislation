<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<open-layout:head title="Open 2.0">
    <script src="${ctxPath}/static/js/src/component/content/content.js"></script>
    <script src="${ctxPath}/static/js/src/component/content/bill.js"></script>
    <script src="${ctxPath}/static/js/src/component/content/calendar.js"></script>
    <script src="${ctxPath}/static/js/src/component/content/law.js"></script>
</open-layout:head>
<open-layout:body>
    <section layout="column" layout-fill>
        <section layout="row">
            <md-sidenav style="width:300px" class="md-sidenav-left" md-component-id="left" md-is-locked-open="$mdMedia('gt-md')">
                <div class="top-bar-wrapper">
                    <ul class="title-area">
                        <li class="name">
                            <h1>
                                <img class="nyss-seal" src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                                <a style="display:inline-block" href="${ctxPath}/"><span class="blue3">Open </span> Legislation</a>
                            </h1>
                        </li>
                    </ul>
                </div>
                <md-content class="">
                    <div style="height:600px;width:100%;background: #006b80">&nbsp;</div>
                </md-content>
            </md-sidenav>
            <div flex="1" style="background:#eee;">
            </div>
        </section>
    </section>
    <!--<div id="content" ng-view class="row" autoscroll="true"></div>-->
</open-layout:body>
<open-layout:footer/>