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

<section ng-controller="SpotcheckCtrl" id="daybreak-page" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <section ng-controller="SpotcheckReportCtrl" ng-init="init()">
    <md-card>
      <md-card-content ng-show="!state.loadingReport">
        <!--Title-->
        <h1>
          <span class="icon-line-graph blue-title-icon"></span>
          {{state.reportType | reportType | reportTypeLabel}} Report
        </h1>
        <div layout="row">
          <h3 flex class="no-margin">
            Run: {{state.reportDateTime | moment:'lll'}}
          </h3>
          <h3 flex class="no-margin">
            Reference Date: {{state.referenceDateTime | moment:'lll'}}
          </h3>
        </div>

        <hr style="margin-top:.5em;"/>

        <div ng-if="state.report.notes" ng-click="notesExpanded = !notesExpanded">
          <h5 class="no-bottom-margin">Notes <span ng-show="!notesExpanded">(click to expand)</span></h5>
          <p ng-bind="state.report.notes" class="spotcheck-report-notes" ng-class="{closed: !notesExpanded}"></p>
          <md-divider></md-divider>
        </div>

        <!--Error summary/filter-->
        <mismatch-view mismatches="state.filteredMismatches" summary="state.summary" filter="state.filter"></mismatch-view>
      </md-card-content>
      <md-card-content ng-show="state.loadingReport">
        <h3>Loading Report...</h3>
        <md-progress-linear md-mode="indeterminate" class="md-hue-2"></md-progress-linear>
      </md-card-content>
    </md-card>
  </section>
</section>
