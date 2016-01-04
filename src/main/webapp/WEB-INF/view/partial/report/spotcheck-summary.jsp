<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
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
            <md-checkbox ng-model="hideErrorlessReports" aria-label="glerp">Hide errorless reports</md-checkbox>
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
                      class="md-2-line" ng-click="goNewTab(reportUrl)">
          <div class="md-list-item-text">
            <h3 ng-bind="summary.reportDateTime | moment:'lll'" class="margin-top-10"></h3>
            <p class="margin-bottom-10">{{summary.referenceType | reportTypeLabel}} Report</p>
          </div>
          <div flex="60" layout="row">
            <div flex="nogrow" layout="column" class="margin-right-20 open-mismatch-total">
              <div ng-bind="summary.openMismatches" class="open-mismatch-total-count"></div>
              <div class="open-mismatch-total-label">
                {{summary.openMismatches === 1 ? 'Mismatch' : 'Mismatches'}}
              </div>
            </div>
            <div flex class="spotcheck-summary-info">
              <p class="spotcheck-summary-notes">
                {{summary.notes ? summary.notes : ' -- No notes -- '}}
              </p>
              <div class="spotcheck-summary-statuses">
                <span ng-if="summary.mismatchStatuses['NEW'] > 0" class="new-error margin-right-10">
                  <span class="prefix-icon icon-chevron-up new-error"></span>
                  {{ (summary.mismatchStatuses['NEW'] | default:0) }}
                  New
                </span>
                <span ng-if="summary.mismatchStatuses['REGRESSION'] > 0" class="new-error margin-right-10">
                  <span class="prefix-icon icon-chevron-up new-error"></span>
                  {{ (summary.mismatchStatuses['REGRESSION'] | default:0) }}
                  Regression
                </span>
                <span ng-if="summary.mismatchStatuses['EXISTING'] > 0" class="margin-right-10">
                  <span class="prefix-icon icon-cycle existing-error"></span>
                  {{ summary.mismatchStatuses['EXISTING'] | default:0 }}
                  Existing
                </span>
                <span ng-if="summary.mismatchStatuses['RESOLVED'] > 0" class="closed-error margin-right-10">
                  <span class="prefix-icon icon-chevron-down closed-error"></span>
                  {{ summary.mismatchStatuses['RESOLVED'] | default:0 }}
                  Resolved
                </span>
              </div>
            </div>
          </div>
          <div flex="10">
            <md-button class="md-secondary" ng-click="showSummaryDetails(summary)">More Info</md-button>
          </div>
          <md-divider></md-divider>
        </md-list-item>
      </md-list>
      <dir-pagination-controls class="text-align-center" pagination-id="spotcheck-summary" boundary-links="true" max-size="10">
      </dir-pagination-controls>
      <%--<table id='daybreak-summary-table' st-table="displaySummaries" st-safe-src="filteredReportSummaries" class="table table-striped">--%>
        <%--<thead>--%>
          <%--<tr ng-if="filteredReportSummaries.length > resultsPerPage">--%>
            <%--<td colspan="8" style="border-bottom:1px solid #ccc">--%>
              <%--<div layout="row" layout-align="space-between center">--%>
                <%--<label>--%>
                  <%--<span st-pagination="" st-items-by-page="resultsPerPage" st-displayed-pages="5"--%>
                      <%--st-template="st-pagination-template"></span>--%>
                <%--</label>--%>
                <%--<label>--%>
                  <%--Displayed:--%>
                  <%--<input type="number" ng-model="resultsPerPage" min="1" placeholder="20">--%>
                <%--</label>--%>
              <%--</div>--%>
            <%--</td>--%>
          <%--</tr>--%>
          <%--<tr>--%>
            <%--<th rowspan="2" st-sort="reportDateTime" style="min-width:11em">Report Date/Time</th>--%>
            <%--<th rowspan="2" st-sort="referenceType" style="max-width:12.5em">Report Type</th>--%>
            <%--<th rowspan="2" st-sort="notes" style="width:6em">Notes</th>--%>
            <%--<th class="th-section" colspan="5">Mismatch Statuses</th>--%>
          <%--</tr>--%>
          <%--<tr>--%>
            <%--<th st-sort="openMismatches" style="border-left:1px solid #ccc; width: 3em">Total Open</th>--%>
            <%--<th st-sort="mismatchStatuses['NEW']" class="delta-column">New</th>--%>
            <%--<th st-sort="mismatchStatuses['REGRESSION']" class="delta-column">Regress</th>--%>
            <%--<th st-sort="mismatchStatuses['EXISTING']" class="delta-column">Existing</th>--%>
            <%--<th st-sort="mismatchStatuses['RESOLVED']" class="delta-column">Resolved</th>--%>
          <%--</tr>--%>
        <%--</thead>--%>
        <%--<tbody>--%>
          <%--<tr ng-repeat="summary in displaySummaries" style="max-height:5em;">--%>
            <%--<td>--%>
              <%--<a ng-href="{{getReportURL(summary.referenceType, summary.reportDateTime)}}" target="_blank">--%>
                <%--{{summary.reportDateTime | moment:'lll'}}--%>
              <%--</a>--%>
            <%--</td>--%>
            <%--<td ng-bind="summary.referenceType"></td>--%>
            <%--<td>--%>
              <%--<p ng-bind="summary.notes" style="max-height:5em; max-width:10em; overflow:hidden; text-overflow:ellipsis"></p>--%>
            <%--</td>--%>
            <%--<td style="border-left:1px solid #ccc; font-weight:bold">{{summary.openMismatches}}</td>--%>
            <%--<td>--%>
              <%--<span class="prefix-icon icon-chevron-up new-error"></span>--%>
              <%--{{ (summary.mismatchStatuses['NEW'] | default:0) }}--%>
            <%--</td>--%>
            <%--<td>--%>
              <%--<span class="prefix-icon icon-chevron-up new-error"></span>--%>
              <%--{{ (summary.mismatchStatuses['REGRESSION'] | default:0) }}--%>
            <%--</td>--%>
            <%--<td>--%>
              <%--<span class="prefix-icon icon-cycle existing-error"></span>--%>
              <%--{{ summary.mismatchStatuses['EXISTING'] | default:0 }}--%>
            <%--</td>--%>
            <%--<td>--%>
              <%--<span class="prefix-icon icon-chevron-down closed-error"></span>--%>
              <%--{{ summary.mismatchStatuses['RESOLVED'] | default:0 }}--%>
            <%--</td>--%>
          <%--</tr>--%>
        <%--</tbody>--%>
      <%--</table>--%>
    </div>
    <div ng-show="loadingSummaries">
      <md-divider></md-divider>
      <h3>Loading Summaries...</h3>
      <md-progress-linear md-mode="indeterminate"></md-progress-linear>
    </div>
    <div ng-show="!loadingSummaries && summariesNotFound">
      <md-divider></md-divider>
      <h3 style="color:#ff00ff">Err0r: Could not load summaries</h3>
    </div>
    <div ng-show="!loadingSummaries && !summariesNotFound && reportSummaries.length < 1">
      <md-divider></md-divider>
      <h3>No reports found for type and date range</h3>
    </div>
  </section>
</section>
<jsp:include page="../core/st-pagination-template.jsp"/>
