<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<% request.setAttribute("today", LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)); %>

<open-layout:head title="Open 2.0">
  <!-- Application Scripts -->
  <script type="application/javascript" src="${ctxPath}/static/js/dest/app.min.js"></script>
</open-layout:head>
<!--
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\   Hello.
     / .-. \          {  =    Y}= Hope you're having a nice ${today}.
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
-->
<open-layout:body appName="open">
  <div id="app-wrapper" layout="row" ng-controller="AppCtrl">
    <%-- Left Nav --%>
    <md-sidenav id="left-nav" md-component-id="left" class="md-sidenav-left" md-is-locked-open="$mdMedia('gt-md')">
      <md-toolbar>
        <div id="logo">
          <a href="${ctxPath}/">
            <img src="${ctxPath}/static/img/NYSS_seal.png"/>
            <span>Open</span> Legislation
          </a>
        </div>
      </md-toolbar>
      <%-- Left Nav Menu Items --%>
      <md-content class="left-nav-menu">
        <material-menu>
          <menu-section title="Explore">
            <%--<menu-item url="${ctxPath}/" icon="icon-home">Dashboard</menu-item>--%>
            <menu-item url="${ctxPath}/calendars" icon="icon-calendar">Senate Calendars</menu-item>
            <menu-item url="${ctxPath}/agendas" icon="icon-megaphone">Senate Agendas / Meetings</menu-item>
            <menu-item url="${ctxPath}/bills" icon="icon-documents">Bills and Resolutions</menu-item>
            <menu-item url="${ctxPath}/laws" icon="icon-bookmarks">New York State Laws</menu-item>
            <menu-item url="${ctxPath}/transcripts" icon="icon-text">Session/Hearing Transcripts</menu-item>
            <menu-item url="${ctxPath}/docs" target="_self" icon="icon-code">JSON API Docs</menu-item>
            <shiro:user>
              <menu-item url="${ctxPath}/logout" target="_self" icon="icon-log-out">Logout</menu-item>
            </shiro:user>
          </menu-section>

          <shiro:hasPermission name="admin:view">
            <menu-section title="Admin">
              <menu-item url="${ctxPath}/admin">Configuration</menu-item>
              <menu-item url="${ctxPath}/admin/logs">Logs</menu-item>
              <menu-item url="${ctxPath}/admin/account">Account Settings</menu-item>
            </menu-section>
            <menu-section title="Reports">
              <menu-item url="${ctxPath}/admin/report/spotcheck/open">Open Mismatches</menu-item>
              <menu-item url="${ctxPath}/admin/report/spotcheck">Report Log</menu-item>
            </menu-section>
          </shiro:hasPermission>
        </material-menu>
      </md-content>
    </md-sidenav>


    <div layout="column" role="main" flex>
        <%-- Content Header --%>
        <md-toolbar id="content-header" ng-class="{'header-visible': header.visible }">
          <div class="md-toolbar-tools">
            <h1 class="top-bar-heading">
              <i hide-gt-md ng-click="toggleLeftNav()" class="menu-icon icon-menu"></i>
              {{header.text}}
            </h1>
          </div>
        </md-toolbar>
        <%-- Main Content Area --%>
        <md-content id="content" flex md-scroll-y ng-view class="fade" autoscroll="true">
            <%-- Content from the templates are injected here --%>
        </md-content>
    </div>
  </div>
</open-layout:body>
<open-layout:footer>
</open-layout:footer>