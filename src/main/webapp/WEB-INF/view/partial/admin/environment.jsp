<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<md-card ng-controller="EnvironmentCtrl" ng-init="init()">
  <md-card-content>
    <md-subheader class="md-primary">Mutable Variables</md-subheader>
    <md-list class="env-list">
      <md-list-item ng-repeat="variable in vars | filter:{mutable:true}" ng-init="variable.newValue = variable.value">
        <p ng-bind="variable.name | unCamelCase" class="capitalize"></p>
        <md-switch class="md-secondary" ng-model="variable.newValue" ng-change="setVariable(variable.name)"></md-switch>
        <%--<md-input-container ng-if="variable.type !== 'boolean'" md-no-float>--%>
          <%--<md-input ng-model="variable.newValue" placeholder="{{variable.value}}"></md-input>--%>
        <%--</md-input-container>--%>
        <md-progress-circular ng-show="variable.setting" md-mode="indeterminate" md-diameter="20"></md-progress-circular>
      </md-list-item>
    </md-list>
    <md-subheader class="md-primary">Other Variables</md-subheader>
    <md-list class="env-list">
      <md-list-item ng-repeat="variable in vars | filter:{mutable:false}">
        <p ng-bind="variable.name |unCamelCase | titleCaps"></p>
        <span ng-bind="variable.value"></span>
      </md-list-item>
    </md-list>
  </md-card-content>
</md-card>
