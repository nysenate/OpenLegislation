<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="IndicesCtrl" ng-init="init();">
  <div layout="row" layout-wrap>
    <md-card ng-repeat="index in indices | filter:{primaryStore:false}" flex="30" style="display: inline-block;">
      <md-toolbar>
        <h4 class="margin-left-16 text-medium" style="text-align: center;">
          {{(index.name == "ALL") && "ALL INDICES" || index.name}}
          <span class="icon-warning red1" ng-show="false"></span>
        </h4>
      </md-toolbar>
      <div style="background-color: rgba(242,242,242,1)">
        <md-progress-linear class="md-accent" md-mode="query"
                            ng-disabled="!rebuilding[index.name]"></md-progress-linear>
        <md-progress-linear class="md-warn" md-mode="query" ng-disabled="!clearing[index.name]"></md-progress-linear>
      </div>
      <div layout="row" layout-align="center center" class="gray3-bg">
        <md-button class="md-raised md-hue-3" ng-click="showClearConfirm(index.name)" ng-disabled="isProcessing()">
          <span class="red1">Clear</span>
        </md-button>
        <md-button class="md-raised md-hue-2" ng-click="showRebuildConfirm(index.name)" ng-disabled="isProcessing()">
          <span class="blue3">Rebuild</span>
        </md-button>
      </div>
    </md-card>
  </div>
</div>
