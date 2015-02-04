<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<open-layout:head title="Open 2.0">
  <script src="${ctxPath}/static/js/src/component/content/bill.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/law.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/daybreak.js"></script>
</open-layout:head>

<body ng-app="open">
  <section layout="column" ng-controller="AppCtrl" layout-fill>
    <section layout="row">

      <%-- Left Nav --%>
      <section>
        <div id="left-nav-spacer" hide-sm hide-md></div>
        <md-sidenav id="left-nav" class="md-sidenav-left" md-component-id="left" md-is-locked-open="$media('gt-md')">
          <md-toolbar id="logo-wrapper">
            <h1 id="logo">
              <img src="${ctxPath}/static/img/NYSS_seal_transp.png"/>
              <a class="gray-2-blue" href="${ctxPath}/">
                <span class="blue3">Open </span>Legislation
              </a>
              <small class="gray6">2.0</small>
            </h1>
          </md-toolbar>

          <%-- Left Nav Menu Items --%>
          <div class="left-nav-menu">
            <material-menu>
              <menu-section title="Welcome">
                <menu-item url="${ctxPath}">About</menu-item>
              </menu-section>
              <menu-section title="NYS Legislative Data">
                <menu-item url="${ctxPath}/calendars">Senate Calendar</menu-item>
                <menu-item url="${ctxPath}/agendas">Senate Agenda</menu-item>
                <menu-item url="${ctxPath}/bills">Bills and Resolutions</menu-item>
                <menu-item url="${ctxPath}/laws">NYS Laws</menu-item>
                <menu-item url="${ctxPath}/members">Senate Membership</menu-item>
                <menu-item url="${ctxPath}/transcripts">Floor/Hearing Transcripts</menu-item>
              </menu-section>
              <menu-section title="Manage">
                <menu-item url="${ctxPath}/manage/processes">Data Processes</menu-item>
                <menu-item url="${ctxPath}/manage/tools">ElasticSearch/Cache Tools</menu-item>
                <menu-item url="${ctxPath}/manage/notifications">Notification Settings</menu-item>
              </menu-section>
              <menu-section title="Reports">
                <menu-item url="${ctxPath}/admin/report/daybreak">Daybreak Report</menu-item>
                <%-- --%>
              </menu-section>
              <menu-section title="API Documentation">
              </menu-section>
            </material-menu>
          </div>
        </md-sidenav>
      </section>
        
      <%-- Main Content Area --%>
      <section id="content-wrapper" flex="1">

        <%-- Content Header --%>
        <md-toolbar id="content-header">
          <h1 class="top-bar-heading md-toolbar-tools">
            <i hide-gt-md ng-click="toggleLeftNav()" class="menu-icon icon-list2"></i>
            {{header.text}}
          </h1>
        </md-toolbar>
        <section id="content" ng-view>
        </section>
      </section>
    </section>
  </section>
</body>
</html>