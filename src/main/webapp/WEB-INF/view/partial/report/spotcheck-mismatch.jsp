<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + mismatchMap;
  String refTypeMismatchMap = SpotCheckMismatchType.getJsonReftypeMismatchMap();
%>

<section ng-controller="SpotcheckCtrl" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <md-card ng-controller="SpotcheckMismatchCtrl" ng-init='init(<%=refTypeMismatchMap%>)' class="content-card">
    <md-card-content>
      <div class="margin-bottom-10">
        <form>
          <h4 class="no-margin">
            <span class="icon-statistics blue-title-icon"></span>
            <md-select ng-model="params.reportType" aria-label="report type">
              <md-option value="unselected" ng-if="lastReceived === 0">-- Select Report Type --</md-option>
              <md-option ng-repeat="(type, label) in rtmap" value="{{type}}">{{type}}</md-option>
            </md-select>
            Open Spotcheck Mismatches
          </h4>
          <h5 class="no-margin" layout="row" layout-align="space-around center" layout-wrap>
            <md-button ng-click="showTypeFilter = !showTypeFilter"
                       class="md-raised" aria-label="toggle mismatch type filter" >
              {{showTypeFilter ? 'Hide' : 'Show'}} Type Filter
            </md-button>
            <label layout="row" layout-align="space-around center">
              <md-checkbox ng-model="params.useObservedAfter" class="md-primary" aria-label="use observed after">
                observed after
              </md-checkbox>
              <input type="date" ng-model="params.observedAfter" ng-disabled="!params.useObservedAfter">
            </label>
            <md-checkbox ng-model="params.resolvedShown" class="md-primary" aria-label="glerp">Show Resolved</md-checkbox>
            <md-checkbox ng-model="params.ignoredShown" aria-label="glerp" class="md-primary">Show Ignored</md-checkbox>
          </h5>
          <div ng-show="showTypeFilter" layout="row" layout-align="space-around center" layout-wrap>
            <md-checkbox ng-model="typeFilter.all" ng-change="applyTypeFilterAll()"
                         class="md-primary" aria-label="glerp">All Types</md-checkbox>
            <md-checkbox ng-repeat="(type, value) in typeFilter.types" ng-model="typeFilter.types[type]"
                         class="md-primary" aria-label="glerp">
              {{type | mismatchTypeLabel}}
            </md-checkbox>
          </div>
        </form>
      </div>
      <md-divider class="margin-bottom-10"></md-divider>
      <div ng-show="lastReceived > 0 && total > 0">
        <md-progress-linear  md-mode="indeterminate" ng-class="{'vis-hidden': lastReceived >= requestCount}"
                             class="margin-bottom-10"></md-progress-linear>
        <table id="daybreak-summary-table" st-table="tableData" st-pipe="mismatchTablePipe" class="table table-striped">
          <thead>
            <tr>
              <td colspan="8" style="border-bottom:1px solid #ccc">
                <div layout="row" layout-align="space-between center">
                  <label>
                      <span st-pagination="" st-items-by-page="resultsPerPage" st-displayed-pages="5"
                            st-template="st-pagination-template"></span>
                  </label>
                  <span>
                    Total: {{total}}
                  </span>
                  <label>
                    Displayed:
                    <input type="number" ng-model="resultsPerPage" min="1" ng-model-options="{debounce:100}">
                  </label>
                </div>
              </td>
            </tr>
            <tr>
              <th st-sort="key">{{params.reportType | reportType | contentType}} Id</th>
              <th st-sort="type">Mismatch Type</th>
              <th st-sort="status">Status</th>
              <th st-sort="observed">Observed</th>
              <th>First Opened</th>
              <th>Snippet</th>
              <th>Details</th>
            </tr>
          </thead>
          <tbody>
            <tr ng-repeat="row in tableData | orderBy:mismatchCompareVal:reverseOrder">
              <td class="padding-right-10">
                <a ng-href="{{getContentUrl(params.reportType, row.key)}}" target="_blank">
                  {{getContentId(params.reportType, row.key)}}</a>
              </td>
              <td class="padding-right-10">{{row.type | mismatchTypeLabel}}</td>
              <td class="padding-right-10">{{row.status | mismatchStatusLabel}}</td>
              <td class="padding-right-10">
                <span>
                  {{row.observed | moment:'lll'}}
                </span>
              </td>
              <td class="padding-right-10">
                <a ng-href="{{getReportURL(reportType, row.firstOpened)}}"
                   ng-show="row.firstOpened!=row.observed && row.firstOpened!='Unknown'">
                  {{row.firstOpened | moment:'lll'}}
                </a>
                <span ng-show="row.firstOpened==row.observed || row.firstOpened=='Unknown'">
                  {{row.firstOpened | moment:'lll'}}
                </span>
              </td>
              <td class="padding-right-10">
                <div class="report-table-snippet">
                  <mismatch-diff no-pre left="row.refData | limitTo:100" right="row.obsData | limitTo:100"></mismatch-diff>
                </div>
              </td>
              <td><a href='#' ng-click="openDetailWindow(row)">Details</a></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div ng-show="lastReceived === 0 && requestCount > 0">
        <h3>Loading Mismatches</h3>
        <md-progress-linear md-mode="indeterminate"></md-progress-linear>
      </div>
      <div ng-show="parameterError" style="color: red">
        Invalid parameter: {{parameterErrorVal}}
      </div>
      <div ng-show="requestError" style="color: red">
        There was an error while retrieving the requested mismatches
      </div>
      <div ng-show="lastReceived > 0 && total <= 0">
        <h3>No Mismatches Found</h3>
      </div>
    </md-card-content>
  </md-card>
</section>

<jsp:include page="../core/st-pagination-template.jsp"/>
<jsp:include page="spotcheck-detail-window.jsp"/>
