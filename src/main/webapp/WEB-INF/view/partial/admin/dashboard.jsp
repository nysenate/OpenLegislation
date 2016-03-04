<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="DashboardCtrl" class="content-section">
  <md-tabs md-dynamic-height="true" class="md-hue-2" md-selected="activeIndex">
    <md-tab label="Environment">
      <md-tab-body>
        <jsp:include page="environment.jsp"/>
      </md-tab-body>
    </md-tab>
    <md-tab label="Caches">
      <md-tab-body>
        <jsp:include page="cache.jsp"/>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</section>