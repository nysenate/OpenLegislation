<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EnvironmentCtrl" ng-init="init()">
  <md-card>
    <md-card-content>
      <md-list>
        <md-subheader>Mutable Variables</md-subheader>
        <md-list-item ng-repeat="variable in vars | filter:{mutable:true}" ng-init="variable.newValue = variable.value">
          <p ng-bind="variable.name"></p>
          <md-switch class="md-secondary" ng-model="variable.newValue" ng-change="setVariable(variable.name)"></md-switch>
          <%--<md-input-container ng-if="variable.type !== 'boolean'" md-no-float>--%>
            <%--<md-input ng-model="variable.newValue" placeholder="{{variable.value}}"></md-input>--%>
          <%--</md-input-container>--%>
          <md-progress-circular ng-show="variable.setting"></md-progress-circular>
        </md-list-item>
      </md-list>
    </md-card-content>
  </md-card>
</section>
