<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>
<%--<section ng-controller="MemberSearchCtrl">--%>

<section ng-controller="MemberViewCtrl">

  <md-toolbar>
    <div class="md-toolbar-tools">
      New York State {{memberView.result.chamber}}: {{memberView.result.sessionYear}}
    </div>
  </md-toolbar>

  <md-card flex>
    <md-item>
      <md-item-content>
        <div style="width:180px;">
          <img class="padding-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/369_john_l._sampson.jpg"
               style="width:80%;">
        </div>
        <div class="md-tile-content">
          <h2 class="no-margin">{{memberView.result.fullName}}</h2>
          <h3 class="gray10 no-margin">New York State {{memberView.result.chamber}}</h3>
          <h5 class="gray7 no-margin">District {{memberView.result.districtCode}}</h5>
        </div>

      </md-item-content>
    </md-item>
  </md-card>
</section>
<%--</section>--%>