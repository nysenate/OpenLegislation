<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<open-layout:head title="Open 2.0">
  <script src="${ctxPath}/static/js/src/component/content/bill.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/law.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/agenda.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/calendar.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/agenda.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/member.js"></script>
  <script src="${ctxPath}/static/js/src/component/content/transcript.js"></script>
  <script src="${ctxPath}/static/js/src/component/report/daybreak.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/account.js"></script>
  <script src="${ctxPath}/static/js/src/component/admin/notification_sub.js"></script>
</open-layout:head>

<body ng-app="open">
  <section id="app-wrapper" layout="column" ng-controller="AppCtrl" layout-fill>
    <section layout="row">

      <%-- Left Nav --%>
      <section>
        <div id="left-nav-spacer" hide-sm hide-md></div>
        <md-sidenav id="left-nav" class="md-sidenav-left" md-component-id="left" md-is-locked-open="$media('gt-md')">
          <div id="logo-wrapper">
            <div id="logo">
              <%--<img src="${ctxPath}/static/img/NYSS_seal_transp.png"/>--%>
              <a class="margin-left-10 gray2" href="${ctxPath}/">
                <img src="${ctxPath}/static/img/NYSS_seal_inv.png"/>
                Open Legislation
              </a>
            </div>
            <md-divider></md-divider>
          </div>

          <%-- Left Nav Menu Items --%>
          <div class="left-nav-menu">
            <material-menu>
              <menu-section title="Main Menu">
                <menu-item url="${ctxPath}/">Home</menu-item>
                <menu-item url="${ctxPath}/calendars">Senate Calendars</menu-item>
                <menu-item url="${ctxPath}/agendas">Senate Agendas</menu-item>
                <menu-item url="${ctxPath}/bills">Bills and Resolutions</menu-item>
                <menu-item url="${ctxPath}/laws">NYS Laws</menu-item>
                <menu-item url="${ctxPath}/members">Senate Membership</menu-item>
                <menu-item url="${ctxPath}/transcripts">Floor/Hearing Transcripts</menu-item>
              </menu-section>
              <shiro:user>
              <menu-section title="Admin">
                <menu-item url="${ctxPath}/admin/account">Account Settings</menu-item>
              </menu-section>
              <menu-section title="Reports">
                <menu-item url="${ctxPath}/admin/report/daybreak">Daybreak Report</menu-item>
                <%-- --%>
              </menu-section>
              </shiro:user>
              <menu-section title="API Documentation">
              </menu-section>
            </material-menu>
          </div>
        </md-sidenav>
      </section>
        
      <%-- Main Content Area --%>
      <section id="content-wrapper" flex="1">

        <%-- Content Header --%>
        <md-toolbar id="content-header" class="md-primary hide" ng-show="header.visible">
          <h1 class="top-bar-heading md-toolbar-tools">
            <i hide-gt-md ng-click="toggleLeftNav()" class="menu-icon icon-list2"></i>
            {{header.text}}
          </h1>
        </md-toolbar>
        <section id="content" ng-view autoscroll="true">
        </section>
      </section>
    </section>
  </section>
  <input type="hidden" id="uikey" value="${uiKey}"/>
</body>
</html>