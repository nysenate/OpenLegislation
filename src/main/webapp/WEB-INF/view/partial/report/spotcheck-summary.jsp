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

<section ng-controller="SpotcheckCtrl" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <section ng-controller="SpotcheckSummaryCtrl">
    <div class="gray2-bg padding-20 no-bottom-padding">
      <label class="margin-bottom-20">Show spotcheck reports run during the following date range</label>
      <div class="text-medium padding-20">
        <div layout="row" class="margin-bottom-20 text-medium">
          <div flex>
            <label class="margin-right-10">Report Type</label>
            <select ng-model="params.summaryType">
              <option value="all">All</option>
              <option ng-repeat="(type, label) in rtmap" value="{{type}}">{{type | reportTypeLabel}}</option>
            </select>
          </div>
          <div flex>
            <md-checkbox ng-model="showErrorlessReports" aria-label="glerp">Show errorless reports</md-checkbox>
          </div>
        </div>
        <div layout="row">
          <div flex>
            <label>From</label>
            <md-datepicker ng-model="params.inputStartDate" md-max-date="params.inputEndDate"></md-datepicker>
          </div>
          <div flex>
            <label>To</label>
            <md-datepicker ng-model="params.inputEndDate" md-min-date="params.inputStartDate"></md-datepicker>
          </div>
        </div>
      </div>
    </div>
    <div ng-show="!loadingSummaries && !summariesNotFound && reportSummaries.length > 0">
      <dir-pagination-controls class="text-align-center" pagination-id="spotcheck-summary" boundary-links="true" max-size="10">
      </dir-pagination-controls>
      <md-list class="spotcheck-report-list">
        <md-list-item dir-paginate="summary in filteredReportSummaries | itemsPerPage:pagination.itemsPerPage"
                      pagination-id="spotcheck-summary" current-page="pagination.currPage"
                      ng-init="reportUrl = getReportURL(summary.referenceType, summary.reportDateTime)"
                      class="md-2-line" ng-click="go(reportUrl)">
          <div class="md-list-item-text">
            <h3 ng-bind="summary.reportDateTime | moment:'lll'" class="margin-top-10 bold"></h3>
            <p class="margin-bottom-10">{{summary.referenceType | reportTypeLabel}} Report</p>
          </div>
          <div class="spotcheck-summary-notes-container md-list-item-text">
            <h3 class="spotcheck-summary-statuses">
              <span class="bold margin-right-10">{{summary | mismatchCount:{ignored: false} }} Total</span>
              <span ng-if="summary | mismatchCount:{status:'NEW', ignored: false}" class="new-error margin-right-10">
                <span class="prefix-icon icon-chevron-up new-error"></span>
                {{ summary | mismatchCount:{status:'NEW', ignored: false} }}
                New
              </span>
              <span ng-if="summary | mismatchCount:{status:'REGRESSION', ignored:false}" class="new-error margin-right-10">
                <span class="prefix-icon icon-chevron-up new-error"></span>
                {{ summary | mismatchCount:{status:'REGRESSION', ignored:false} }}
                Regression
              </span>
              <span ng-if="summary | mismatchCount:{status:'EXISTING', ignored: false}" class="margin-right-10">
                <span class="prefix-icon icon-cycle existing-error"></span>
                {{ summary | mismatchCount:{status:'EXISTING', ignored: false} }}
                Existing
              </span>
              <span ng-if="summary | mismatchCount:{status:'RESOLVED', ignored: false}" class="closed-error margin-right-10">
                <span class="prefix-icon icon-chevron-down closed-error"></span>
                {{ summary | mismatchCount:{status:'RESOLVED', ignored: false} }}
                Resolved
              </span>
            </h3>
            <p class="spotcheck-summary-notes">
              {{summary.notes || ' -- No notes -- '}}
            </p>
          </div>
          <md-divider></md-divider>
        </md-list-item>
      </md-list>
      <dir-pagination-controls class="text-align-center" pagination-id="spotcheck-summary" boundary-links="true" max-size="10">
      </dir-pagination-controls>
    </div>
    <div ng-show="loadingSummaries" class="margin-20">
      <h3>Loading Summaries...</h3>
      <md-progress-linear md-mode="indeterminate" class="md-hue-2"></md-progress-linear>
    </div>
    <div ng-show="!loadingSummaries && summariesNotFound" class="margin-20">
      <h3 style="color:#ff00ff">Err0r: Could not load summaries</h3>
    </div>
    <div ng-show="!loadingSummaries && !summariesNotFound && reportSummaries.length < 1" class="margin-20">
      <h3>No reports found for type and date range</h3>
    </div>
  </section>
</section>
<jsp:include page="../core/st-pagination-template.jsp"/>
