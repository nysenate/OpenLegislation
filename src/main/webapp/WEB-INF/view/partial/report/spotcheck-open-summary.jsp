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
    <h3>Select a report type to see mismatch details</h3>
    <md-list>
      <div ng-repeat="(reportType, summary) in summaries"
           ng-init="mmTypes = ['all'].concat(keys(summary.mismatchCounts))">
        <md-list-item href="{{ctxPath}}/admin/report/spotcheck/open/{{reportType}}"
                      class="open-mismatch-summary-item" ng-class="{'not-first': !$first}" layout="row">
          <h2 flex="33" class="margin-right-20">{{reportType | reportTypeLabel}}</h2>
          <md-chips flex ng-model="mmTypes" readonly="true">
            <md-chip-template ng-class="{bold: $chip === 'all'}">
              {{summary | mismatchCount:{type: $chip, ignored: false} }}
              <span ng-if="$chip === 'all'">Total</span>
              <span ng-if="$chip !== 'all'">{{$chip | mismatchTypeLabel}}</span>
            </md-chip-template>
          </md-chips>
        </md-list-item>
      </div>
    </md-list>
  </section>
</section>
