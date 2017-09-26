<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl" id="spotcheck-report-page">
  <md-content class="spotcheck-report-control-bar spotcheck-report-top-controls">
    <h2>Report Date:</h2>
    <md-datepicker ng-model="pickedDate"
                   datepicker-popup="YYYY-MM-DD"
                   md-placeholder="Select Report Date"
                   md-max-date="maxDate"
                   ng-change="onDateChange()">
    </md-datepicker>
    <h2>Report Type:</h2>
    <select ng-model="datasource.selected" ng-change="onDatasourceChange()"
            ng-options="datasource as datasource.label for datasource in datasource.values"></select>
  </md-content>

  <md-card class="content-card spotcheck-report-content-tabs">
    <md-tabs md-selected="selectedTab" class="md-hue-2" md-dynamic-height=md-border-bottom>
      <spotcheck-report-content-tab title="Bills" type="BILL"></spotcheck-report-content-tab>
      <spotcheck-report-content-tab title="Calendars" type="CALENDAR"></spotcheck-report-content-tab>
      <spotcheck-report-content-tab title="Agendas" type="AGENDA"></spotcheck-report-content-tab>
    </md-tabs>
  </md-card>
</section>
<jsp:include page="spotcheck-detail-window.jsp"/>
