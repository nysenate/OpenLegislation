<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Content">
    <script src="${ctxPath}/static/js/component/content/content.js"></script>
    <script src="${ctxPath}/static/js/component/content/bill/bill-home.js"></script>
</open-layout:head>
<open-layout:body pageId="content">
    <div ng-controller="ContentCtrl" class="row collapsed" style="margin-top:1.5em">
        {{title}}
        <div class="large-2 columns side-menu-bg">
            <nav>
                <ul class="side-nav">
                    <li class='heading'>Content Types</li>
                    <li ng-class="{active : selectedContent == 'agendas'}">
                        <a ng-click="selectedContent='agendas'" href="${ctxPath}/content/agendas">
                            <span class="icon-clipboard prefix-icon2"></span>Agendas</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'bills'}">
                        <a ng-click="selectedContent='bills'" href="${ctxPath}/content/bills">
                            <span class="icon-docs prefix-icon2"></span>Bills / Resolutions</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'calendars'}">
                        <a ng-click="selectedContent='calendars'" href="${ctxPath}/content/calendars">
                            <span class="icon-calendar prefix-icon2"></span>Calendars</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'committees'}">
                        <a ng-click="selectedContent='committees'" href="${ctxPath}/content/committees">
                            <span class="icon-users prefix-icon2"></span>Committees</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'members'}">
                        <a ng-click="selectedContent='members'" href="${ctxPath}/content/members">
                            <span class="icon-user prefix-icon2"></span>Members</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'laws'}">
                        <a ng-click="selectedContent='laws'" href="${ctxPath}/content/laws">
                            <span class="icon-book prefix-icon2"></span>Laws of NY</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'transcripts'}">
                        <a ng-click="selectedContent='transcripts'" href="${ctxPath}/content/transcripts">
                            <span class="icon-text prefix-icon2"></span>Transcripts</a>
                    </li>
                    <li ng-class="{active : selectedContent == 'vetos'}">
                        <a ng-click="selectedContent='vetos'" href="${ctxPath}/content/vetos">
                            <span class="icon-thumbsup prefix-icon2"></span>Vetos / Approvals</a>
                    </li>
                    <li>
                        <a href="#"></a>
                    </li>
                    <li class='heading'>Raw Data</li>
                    <li><a href="#">Sobi Documents</a></li>
                    <li><a href="#">Law Documents</a></li>
                </ul>
            </nav>
        </div>
        <div class="columns large-10">
            <div ng-view class="view-animate" autoscroll="true"></div>
        </div>
    </div>
</open-layout:body>
<open-layout:footer/>