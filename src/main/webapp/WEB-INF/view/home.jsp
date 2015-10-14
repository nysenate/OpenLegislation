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
  <script type="application/javascript" src="${ctxPath}/static/js/src/core.js"></script>
  <script type="application/javascript" src="${ctxPath}/static/js/src/app.js"></script>
  <script type="application/javascript" src="${ctxPath}/static/js/src/routes.js"></script>

  <script src="${ctxPath}/static/js/src/component/content/bill.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/law.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/agenda.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/calendar.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/agenda.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/member.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/transcript.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/spotcheck-base.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/spotcheck-detail.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/spotcheck-summary.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/spotcheck-report.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/spotcheck-mismatch.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/account.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/notification_sub.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/dashboard.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/environment.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/logout.js"></script>
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
  <section id="app-wrapper" layout="column" ng-controller="AppCtrl" layout-fill>
    <section layout="row">
      <%-- Left Nav --%>
      <section>
        <div id="left-nav-spacer" hide-sm hide-md></div>
        <md-sidenav id="left-nav" class="md-sidenav-left" md-component-id="left" md-is-locked-open="$mdMedia('gt-md')">
          <div id="logo-wrapper">
            <div id="logo">
              <a class="margin-left-10" href="${ctxPath}/">
                <img src="${ctxPath}/static/img/NYSS_seal.png"/>
                <span>Open</span> Legislation
              </a>
              <span class="beta-tag">2.0 BETA</span>
            </div>
          </div>
          <%-- Left Nav Menu Items --%>
          <div class="left-nav-menu">
            <material-menu>
              <menu-section title="Main Menu">
                <menu-item url="${ctxPath}/" icon="icon-home">Home</menu-item>
                <menu-item url="${ctxPath}/calendars" icon="icon-calendar">Senate Calendars</menu-item>
                <menu-item url="${ctxPath}/agendas" icon="icon-megaphone">Senate Agendas / Meetings</menu-item>
                <menu-item url="${ctxPath}/bills" icon="icon-documents">Bills and Resolutions</menu-item>
                <menu-item url="${ctxPath}/laws" icon="icon-bookmarks">New York State Laws</menu-item>
                <menu-item url="${ctxPath}/members" icon="icon-users">Senate / Assembly Membership</menu-item>
                <menu-item url="${ctxPath}/transcripts" icon="icon-text">Floor/Hearing Transcripts</menu-item>
              </menu-section>
              <shiro:user>
                <menu-section title="Admin">
                  <menu-item url="${ctxPath}/admin">Dashboard</menu-item>
                  <menu-item url="${ctxPath}/admin/account">Account Settings</menu-item>
                  <menu-item url="${ctxPath}/admin/logout">Logout</menu-item>
                </menu-section>
                <menu-section title="Reports">
                  <menu-item url="${ctxPath}/admin/report/spotcheck">Spotcheck Reports</menu-item>
                  <menu-item url="${ctxPath}/admin/report/spotcheck/open">Open Mismatches</menu-item>
                  <%-- --%>
                </menu-section>
              </shiro:user>
            </material-menu>
          </div>
        </md-sidenav>
      </section>
      <%-- Main Content Area --%>
        <c:forEach var="m" items="${membersList}">
          ${m}
        </c:forEach>
      <section id="content-wrapper" flex="1">
        <%-- Content Header --%>
        <md-toolbar id="content-header" class="md-primary" ng-class="{'header-visible': header.visible }">
          <h1 class="top-bar-heading md-toolbar-tools">
            <i hide-gt-md ng-click="toggleLeftNav()" class="menu-icon icon-menu"></i>
            {{header.text}}
          </h1>
        </md-toolbar>
        <section id="content" ng-view class="fade" autoscroll="true">
        </section>
      </section>
    </section>
  </section>
</open-layout:body>
<open-layout:footer>
</open-layout:footer>