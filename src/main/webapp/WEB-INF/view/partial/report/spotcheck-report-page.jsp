<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl"
         class="padding-20">
  <md-content>
    <div>
      <h2>Report Date: {{date}}</h2>
    </div>
    <div layout="row" layout-align="space-between center">
      <div>
        <select ng-model="datasource" ng-change="onDatasourceChange()">
          <option value="OPENLEG">LBDC - OpenLegislation</option>
          <option value="NYSENATE_DOT_GOV">OpenLegislation - NYSenate.gov</option>
        </select>
      </div>

      <div>
        <select ng-model="status" ng-change="onStatusChange()">
          <option value="OPEN">Open Issues ({{mismatchSummary.openCount}})</option>
          <option value="NEW">New Issues ({{mismatchSummary.newCount}})</option>
          <option value="RESOLVED">Resolved Issues ({{mismatchSummary.resolvedCount}})</option>
        </select>
      </div>
    </div>
  </md-content>

  <div>
    <md-card class="content-card">
      <md-tabs class="md-hue-2" md-dynamic-height=md-border-bottom>
        <md-tab label="Bills ({{mismatchSummary.billCount}})">
          <md-content>
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px">
              <div flex="10" class="bold">Status</div>
              <div flex="10" class="bold">Bill</div>
              <div flex="15" class="bold">Error</div>
              <div flex="15" class="bold">Date</div>
              <div flex="10" class="bold">Issue</div>
              <div flex="15" class="bold">Source</div>
            </div>
            <md-divider></md-divider>
            <div ng-repeat="mismatch in billMismatches.filtered" layout="row" layout-align="space-around center">
              <div layout="row" layout-align="space-between center" flex="75">
                <div flex="10">{{mismatch.status}}</div>
                <div flex="10">{{mismatch.bill}}</div>
                <div flex="15">{{mismatch.mismatchType}}</div>
                <div flex="15">{{mismatch.date}}</div>
                <div flex="10">{{mismatch.issue}}</div>
                <div flex="15">{{mismatch.source}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised">Diff</md-button>
                <md-button class="md-accent md-raised">Ignore</md-button>
              </div>
            </div>
          </md-content>
        </md-tab>

        <md-tab label="Calendars ({{mismatchSummary.calendarCount}})">
          <md-content class="md-padding">
            <md-list>
              <md-list layout="row">
                <md-list-item ng-repeat="data in calendarCategories" flex>{{data}}</md-list-item>
              </md-list>
            </md-list>
          </md-content>
        </md-tab>

        <md-tab label="Agendas ({{mismatchSummary.agendaCount}})">
          <md-content class="md-padding">
            <md-list>
              <md-list layout="row">
                <md-list-item ng-repeat="data in agendaCategories" flex>{{data}}</md-list-item>
              </md-list>
            </md-list>
          </md-content>
        </md-tab>
      </md-tabs>
    </md-card>
  </div>
</section>
