<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getRefJsonMap();
  String refTypeDisplayMap = SpotCheckRefType.getDisplayJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + refTypeDisplayMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckReportCtrl" ng-init='init(<%=daybreakInitArgs%>)'
     class="padding-10">
  <div>
    <div layout-align="space-between none" layout="row">
      <md-content>
        <p>Report Date: {{date}}</p>
        <select ng-model="datasource.selected" ng-change="onDatasourceChange()"
                ng-options="datasource as datasource.label for datasource in datasource.values"></select>
      </md-content>

      <div id="spotcheck-report-page-issues-box" layout-align="space-around center" layout="column">
        <md-checkbox>Open Issues</md-checkbox>
        <md-checkbox>New Issues</md-checkbox>
        <md-checkbox>Resolved Issues</md-checkbox>
      </div>
    </div>
  </div>

  <div ng-cloak>
    <md-content>
      <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="Bills">
          <md-content>
            <md-list>
              <md-list layout="row">
                <md-list-item ng-repeat="category in billCategories" flex>{{category}}</md-list-item>

                <md-button flex></md-button>
                <md-button flex></md-button>
              </md-list>

              <md-divider></md-divider>

              <md-list layout="row">
                <md-list-item ng-repeat="mismatch in mismatches" flex>
                  {{mismatch.status}} {{mismatch.bill}}
                </md-list-item>

                <md-button class="md-raised" flex>Diff</md-button>
                <md-button class="md-accent md-raised" flex>Ignore</md-button>
              </md-list>
            </md-list>
          </md-content>
        </md-tab>
        <md-tab label="Calendars">
          <md-content class="md-padding">
            <md-list>
              <md-list layout="row">
                <md-list-item ng-repeat="data in calendarCategories" flex>{{data}}</md-list-item>
              </md-list>
            </md-list>
          </md-content>
        </md-tab>
        <md-tab label="Agendas">
          <md-content class="md-padding">
            <md-list>
              <md-list layout="row">
                <md-list-item ng-repeat="data in agendaCategories" flex>{{data}}</md-list-item>
              </md-list>
            </md-list>
          </md-content>
        </md-tab>
      </md-tabs>
    </md-content>
  </div>
</section>
