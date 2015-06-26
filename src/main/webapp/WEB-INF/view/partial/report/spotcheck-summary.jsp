<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckCtrl" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <md-card class="content-card">
    <md-card-content ng-controller="SpotcheckSummaryCtrl" style="padding-left: 10px">
      <div class="margin-bottom-10">
        <form>
          <h4 class="no-margin">
            <span class="icon-statistics blue-title-icon"></span>
            <md-select ng-model="params.summaryType">
              <md-option value="all">All</md-option>
              <md-option ng-repeat="(type, label) in rtmap" value="{{type}}">{{type}}</md-option>
            </md-select>
            Spotcheck Reports
          </h4>
          <h4 class="no-margin" layout="row" layout-align="space-around center">
            <label>
              from
              <input type="date" ng-model="params.inputStartDate">
            </label>
            <label>
              to
              <input type="date" ng-model="params.inputEndDate">
            </label>
            <md-checkbox ng-model="hideErrorlessReports" aria-label="glerp">Hide errorless reports</md-checkbox>
          </h4>
        </form>
      </div>
      <div ng-show="!loadingSummaries && !summariesNotFound && reportSummaries.length > 0">
        <table id='daybreak-summary-table' st-table="displaySummaries" st-safe-src="filteredReportSummaries"
                class="table table-striped">
          <thead>
            <tr ng-if="filteredReportSummaries.length > resultsPerPage">
              <td colspan="8" style="border-bottom:1px solid #ccc">
                <div layout="row" layout-align="space-between center">
                  <label>
                    <span st-pagination="" st-items-by-page="resultsPerPage" st-displayed-pages="5"
                        st-template="st-pagination-template"></span>
                  </label>
                  <label>
                    Displayed:
                    <input type="number" ng-model="resultsPerPage" min="1" placeholder="20">
                  </label>
                </div>
              </td>
            </tr>
            <tr>
              <th rowspan="2" st-sort="reportDateTime" style="min-width:11em">Report Date/Time</th>
              <th rowspan="2" st-sort="referenceType" style="max-width:12.5em">Report Type</th>
              <th rowspan="2" st-sort="notes" style="width:6em">Notes</th>
              <th class="th-section" colspan="5">Mismatch Statuses</th>
            </tr>
            <tr>
              <th st-sort="openMismatches" style="border-left:1px solid #ccc; width: 3em">Total Open</th>
              <th st-sort="mismatchStatuses['NEW']" class="delta-column">New</th>
              <th st-sort="mismatchStatuses['REGRESSION']" class="delta-column">Regress</th>
              <th st-sort="mismatchStatuses['EXISTING']" class="delta-column">Existing</th>
              <th st-sort="mismatchStatuses['RESOLVED']" class="delta-column">Resolved</th>
            </tr>
          </thead>
          <tbody>
            <tr ng-repeat="summary in displaySummaries">
              <td>
                <a ng-href="{{getReportURL(summary.referenceType, summary.reportDateTime)}}" target="_blank">
                  {{summary.reportDateTime | moment:'lll'}}
                </a>
              </td>
              <td ng-bind="summary.referenceType"></td>
              <td ng-bind="summary.notes" style="max-width:10em; overflow:hidden; text-overflow:ellipsis"></td>
              <td style="border-left:1px solid #ccc; font-weight:bold">{{summary.openMismatches}}</td>
              <td>
                <span class="prefix-icon icon-chevron-up new-error"></span>
                {{ (summary.mismatchStatuses['NEW'] | default:0) }}
              </td>
              <td>
                <span class="prefix-icon icon-chevron-up new-error"></span>
                {{ (summary.mismatchStatuses['REGRESSION'] | default:0) }}
              </td>
              <td>
                <span class="prefix-icon icon-cycle existing-error"></span>
                {{ summary.mismatchStatuses['EXISTING'] | default:0 }}
              </td>
              <td>
                <span class="prefix-icon icon-chevron-down closed-error"></span>
                {{ summary.mismatchStatuses['RESOLVED'] | default:0 }}
              </td>
            </tr>
          </tbody>
        </table>
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
    </md-card-content>
  </md-card>
</section>
<jsp:include page="../core/st-pagination-template.jsp"/>
