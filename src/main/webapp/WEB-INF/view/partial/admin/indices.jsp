<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="IndicesCtrl" ng-init="init();">
    <div layout="row" layout-wrap>
        <md-card ng-repeat="index in indices" flex="30" style="dispaly: inline-block;">
            <md-toolbar>
                <h4 class="margin-left-16 text-medium" style="text-align: center;">{{(index == "ALL") && "ALL INDICES" || index}} <span class="icon-warning red1" ng-show="false"></span></h4>
            </md-toolbar>
            <div style="background-color: rgba(242,242,242,1)">
                <md-progress-linear class="md-accent" md-mode="query"ng-disabled="!rebuilding[index]"></md-progress-linear>
                <md-progress-linear class="md-warn" md-mode="query" ng-disabled="!clearing[index]"></md-progress-linear>
            </div>
            <div layout="row" layout-align="center center" class="gray3-bg">
                <md-button class="md-raised md-hue-3" ng-click="showClearConfirm(index)" ng-disabled="isProcessing()">
                    <span class="red1">Clear</span>
                </md-button>
                <md-button class="md-raised md-hue-2" ng-click="showRebuildConfirm(index)" ng-disabled="isProcessing()">
                    <span class="blue3">Rebuild</span>
                </md-button>
            </div>
        </md-card>
    </div>
</div>
