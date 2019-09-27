<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl" id="spotcheck-report-page">
  <md-content class="spotcheck-report-control-bar spotcheck-report-top-controls">
    <h2>Comparison:</h2>
    <select ng-model="datasource.selected" ng-change="onDatasourceChange()"
            ng-options="datasource as datasource.label for datasource in datasource.values"></select>
    <h2>Date:</h2>
    <md-datepicker ng-model="pickedDate"
                   datepicker-popup="YYYY-MM-DD"
                   md-placeholder="Select Report Date"
                   md-max-date="maxDate"
                   ng-change="onDateChange()">
    </md-datepicker>
    <md-button class="md-raised md-accent rounded-corner-button spotcheck-jump-button"
               ng-click="jumpToToday()"
               disabled="disabled"
               ng-disabled="currentDay()">
      Jump to Today
    </md-button>
  </md-content>

  <md-card class="content-card spotcheck-report-content-tabs">
    <md-tabs md-selected="selectedTab" class="md-hue-2" md-dynamic-height=md-border-bottom>
      <spotcheck-report-content-tab ng-repeat="contentType in dataSourceContentTypeMap[datasource.selected.value]"
                                    type="{{contentType}}"
                                    title="{{contentType | contentType}}">
      </spotcheck-report-content-tab>
    </md-tabs>
  </md-card>
</section>
<jsp:include page="spotcheck-detail-window.jsp"/>

