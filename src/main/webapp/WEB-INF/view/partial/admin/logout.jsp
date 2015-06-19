<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="LogoutCtrl">
  <md-divider></md-divider>
  <span layout="row" layout-align="space-around end">
    <div class="logout-rect"></div>
    <div style="height:10vh"></div>
    <div class="logout-rect"></div>
  </span>
  <span layout="row" layout-align="space-around center">
    <span class="logout-circle">
      <md-progress-circular md-mode="indeterminate" md-diameter="200"></md-progress-circular>
    </span>
    <div style="height: 50vh"></div>
    <span class="logout-circle">
      <md-progress-circular md-mode="indeterminate" md-diameter="200"></md-progress-circular>
    </span>
  </span>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
  <md-progress-linear md-mode="indeterminate"></md-progress-linear>
</section>
