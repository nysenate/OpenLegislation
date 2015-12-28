<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<md-card ng-controller="EnvironmentCtrl" ng-init="init()">
  <md-card-content>
    <md-subheader>Mutable Variables</md-subheader>
    <md-list class="env-list">
      <md-list-item ng-repeat="variable in vars | filter:{mutable:true}"
                    ng-init="variable.newValue = variable.value">
        <p ng-bind="variable.name | unCamelCase" class="capitalize"></p>
        <md-checkbox class="md-secondary" ng-model="variable.newValue" ng-change="setVariable(variable.name)"></md-checkbox>
        <md-progress-circular ng-show="variable.setting" md-mode="indeterminate" md-diameter="20"></md-progress-circular>
      </md-list-item>
    </md-list>
    <md-subheader>Other Variables</md-subheader>
    <md-list class="env-list">
      <md-list-item ng-repeat="variable in vars | filter:{mutable:false}">
        <p ng-bind="variable.name |unCamelCase | titleCaps"></p>
        <span ng-bind="variable.value"></span>
      </md-list-item>
    </md-list>
  </md-card-content>
</md-card>
