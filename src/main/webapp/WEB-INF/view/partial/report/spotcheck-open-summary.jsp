<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getRefJsonMap();
  String refTypeDisplayMap = SpotCheckRefType.getDisplayJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + refTypeDisplayMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckCtrl" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <section ng-controller="SpotcheckOpenSummaryCtrl" ng-init="init()">
    <div ng-show="loadingSummaries" layout="column" layout-align="space-around center">
      <h1 md-hue>Loading Open Mismatch Summaries ... </h1>
      <md-progress-circular md-mode="indeterminate" class="md-hue-2" md-diameter="200"></md-progress-circular>
    </div>
    <div ng-show="!loadingSummaries">
      <h3 class="margin-left-20">Select a report type to see mismatch details</h3>
      <md-list>
        <md-list-item ng-href="{{ctxPath}}/admin/report/spotcheck/open/{{reportType}}"
                      ng-repeat="(reportType, summary) in summaries"
                      ng-init="mmTypes = ['all'].concat(keys(summary.mismatchCounts))"
                      ng-mouseover="mouselover = true" ng-mouseleave="mouselover = false"
                      class="md-2-line open-mismatch-summary-item" ng-class="{'not-first': !$first}">
          <div>
            <div class="md-list-item-text">
              <h3 class="bold margin-right-10">{{reportType | reportTypeLabel}}</h3>
              <p>
                {{reportType | reportDataProvider}} {{reportType | contentType}}s checked against
                {{reportType | reportReferenceProvider}} {{reportType | refTypeLabel}}s
              </p>
            </div>
            <md-chips ng-model="mmTypes" readonly="true">
              <md-chip-template ng-class="{bold: $chip === 'all'}">
                {{summary | mismatchCount:{type: $chip, ignored: false} }}
                <span ng-if="$chip === 'all'">Total</span>
                <span ng-if="$chip !== 'all'">{{$chip | mismatchTypeLabel}}</span>
              </md-chip-template>
            </md-chips>
          </div>
        </md-list-item>
      </md-list>
    </div>
  </section>
</section>
