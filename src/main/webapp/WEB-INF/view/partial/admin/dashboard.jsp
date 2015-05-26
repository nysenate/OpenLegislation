<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="DashboardCtrl" ng-init="init()" class="content-section">
  <md-tabs md-dynamic-height="false" md-selected="activeIndex">
    <md-tab label="Environment">
      <md-tab-body>
        <jsp:include page="environment.jsp"/>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</section>