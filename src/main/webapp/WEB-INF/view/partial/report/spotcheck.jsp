<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + mismatchMap;
%>

<section ng-controller="DaybreakCtrl" id="daybreak-page" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <md-tabs md-dynamic-height="false" md-selected="selectedIndex">

    <!-- Summary Tab -->
    <md-tab>
      <md-tab-label>Summaries</md-tab-label>
      <md-tab-body>
        <md-card class="content-card">
          <md-card-content ng-controller="DaybreakSummaryCtrl" style="padding-left: 10px">
            <div>
              <form>
                <h4>
                  <span class="icon-statistics blue-title-icon"></span>
                  <md-select ng-model="params.summaryType">
                    <md-option value="all">All</md-option>
                    <md-option ng-repeat="(type, label) in rtmap" value="{{type}}">{{type}}</md-option>
                  </md-select>
                  Spotcheck Reports
                </h4>
                <h4>
                  from
                  <input type="date" ng-model="params.inputStartDate">
                  to
                  <input type="date" ng-model="params.inputEndDate">
                </h4>
              </form>
            </div>
            <table id='daybreak-summary-table' st-table="displaySummaries" st-safe-src="reportSummaries"
                   ng-show="!loadingSummaries && !summariesNotFound && reportSummaries.length > 0" class="table table-striped">
              <thead>
                <tr>
                  <th rowspan="2" st-sort="reportDateTime" style="max-width:11em">Report Date/Time</th>
                  <th rowspan="2" st-sort="referenceType" style="max-width:12.5em">Report Type</th>
                  <th rowspan="2" st-sort="notes" style="width:6em">Notes</th>
                  <th class="th-section"  colspan="5">Mismatch Statuses</th>
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
                    <a href="#" ng-click="openReportDetail(summary.referenceType, summary.reportDateTime.toString())">
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
      </md-tab-body>
    </md-tab>

    <!-- Detail Tab -->

    <md-tab ng-disabled="openReportDateTime == null || openReportType == null">
      <md-tab-label>
          {{openReportType | reportTypeLabel}} {{openReportDateTime | moment:'lll'}}
      </md-tab-label>
      <md-tab-body>
        <section ng-controller="DaybreakDetailCtrl">
          <md-card>
            <md-card-content ng-show="!loadingReport">
              <!--Title-->
              <h4>
                <span class="icon-graph blue-title-icon"></span>
                {{openReportType | reportType}} {{referenceDateTime | moment:'ll'}} | Report Date: {{reportDateTime | moment:'lll'}}
              </h4>
              <p ng-if="report.details.notes">Notes: {{report.details.notes}}</p>

              <hr style="margin-top:.5em;"/>

              <!--Error summary/filter-->
              <form style="margin-bottom: 5px">
                <md-button ng-init="showMismatchFilter=false" ng-click="showMismatchFilter=!showMismatchFilter"
                           aria-label="show mismatch filter"
                           ng-switch on="showMismatchFilter" class="md-raised">
                  <span ng-switch-when="false"><span class="icon-arrow-right prefix-icon"/>Filter mismatches</span>
                  <span ng-switch-when="true"><span class="icon-arrow-up prefix-icon"/>Hide filter</span>
                </md-button>
                <br/>
                <div ng-show="showMismatchFilter" style="padding-top: 10px">
                  <div layout="row" style="margin-bottom:10px;">
                    <md-checkbox ng-model="errorFilter.all" aria-label="Show all mismatches">
                      Total<br/>{{ totals.total }}
                    </md-checkbox>
                    <md-checkbox ng-model="errorFilter.none" aria-label="Show no mismatches">
                      None<br/>&nbsp;
                    </md-checkbox>
                    <md-checkbox ng-model="errorFilter.statuses[status]"
                                  ng-repeat="(status, total) in totals.statuses"
                                  aria-label="Show {{status | mismatchStatusLabel}} mismatches"
                                  ng-if="total > 0">
                      {{status | mismatchStatusLabel}}<br/>{{totals.statuses[status]}}
                    </md-checkbox>
                  </div>

                  <div layout="row" class="row button-group panel minimal">
                    <md-checkbox ng-model="errorFilter.allTypes" aria-label="Show all mismatch types">
                      All<br/>Types
                    </md-checkbox>
                    <md-checkbox ng-model="errorFilter.noTypes" aria-label="Show no mismatch types">
                      No<br/>Types
                    </md-checkbox>
                    <md-checkbox ng-model="errorFilter.types[type]"
                                  ng-repeat="(type, total) in filteredTypeTotals"
                                  aria-label="Show {{type | mismatchTypeLabel}} mismatches">
                      {{type | mismatchTypeLabel}}<br/>{{total}}
                    </md-checkbox>
                  </div>
                </div>

                <div class="row collapse"></div>
              </form>

              <!--Observation Table-->
              <table st-table="displayData" st-safe-src="filteredTableData" class="table table-striped report-detail-table">
                <thead>
                  <tr>
                    <td colspan="6">
                      <span st-pagination="" st-items-by-page="resultsPerPage"
                            st-displayed-pages="5"></span>
                      <span class="rpp-selector">
                        Displayed:
                        <a href="#" ng-repeat="number in rppOptions" ng-bind="number" ng-click="setRpp(number)"
                                   aria-label="Display {{number}} mismatches per page"
                                   ng-class='{"inactive-link": resultsPerPage === number}'></a>
                      </span>
                    </td>
                  </tr>
                  <tr>
                    <th st-sort="printNo">{{reportType | reportType | contentType}} Id</th>
                    <th st-sort="type">Mismatch Type</th>
                    <th st-sort="status">Status</th>
                    <th st-sort="firstOpened">Opened At</th>
                    <th>Snippet</th>
                    <th>Details</th>
                  </tr>
                </thead>
                <tbody>
                  <tr ng-repeat="row in displayData">
                    <td>
                      <a ng-href="{{row.contentUrl}}" target="_blank">{{row.contentId}}</a>
                    </td>
                    <td>{{row.type | mismatchTypeLabel}}</td>
                    <td>{{row.status | mismatchStatusLabel}}</td>
                    <td>
                      <a href="#" ng-click="openReportDetail(openReportType, row.firstOpened)"
                         ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                        {{row.firstOpened | moment:'lll'}}
                      </a>
                      <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                        {{row.firstOpened | moment:'lll'}}
                      </span>
                    </td>
                    <td><div class="report-table-snippet"><mismatch-diff diff="row.diff"></mismatch-diff></div></td>
                    <td><a href='#' ng-click='openDetailWindow(row.mismatchId)'>Details</a></td>
                  </tr>
                </tbody>
                <tfoot>
                </tfoot>
              </table>
            </md-card-content>
            <md-card-content ng-show="loadingReport">
              <h3>Loading Report...</h3>
              <md-progress-linear md-mode="indeterminate"></md-progress-linear>
            </md-card-content>
          </md-card>
        </section>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</section>

<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail">
    <md-content class="padding-20 mismatch-dialog">
      <div>
        <h5 style="display: inline-block">
          {{reportType | contentType}}: <a ng-href="{{contentUrl}}" target="_blank">{{contentId}}</a><br/>
          Mismatch: {{currentMismatch.mismatchType | mismatchTypeLabel}}<br/>
          Status: {{currentMismatch.status | mismatchStatusLabel}}
        </h5>
        <h5 style="display: inline-block; text-align: right">
          Opened on: {{firstOpened.reportDateTime | moment:'lll'}}<br/>
          First Reference: {{firstOpened.referenceDateTime | moment:'lll'}}<br/>
          Current Reference: {{observation.refDateTime | moment:'lll'}}
        </h5>
      </div>
      <md-tabs  md-dynamic-height="true" class="mismatch-dialog-tabs">
        <md-tab label="Summary" ng-disabled="!multiLine">
          <md-content>
            <diff-summary diff="currentMismatch.diff"></diff-summary>
          </md-content>
        </md-tab>
        <md-tab label="Full Diff">
          <md-content>
            <mismatch-diff pre="{{multiLine}}" diff="currentMismatch.diff"></mismatch-diff>
          </md-content>
        </md-tab>
        <md-tab label="LBDC">
          <md-content>
            <span ng-class="{preformatted: multiLine, 'word-wrap': !multiLine}" ng-bind="currentMismatch.referenceData"></span>
          </md-content>
        </md-tab>
        <md-tab label="Openleg">
          <md-content>
            <span ng-class="{preformatted: multiLine, 'word-wrap': !multiLine}" ng-bind="currentMismatch.observedData"></span>
          </md-content>
        </md-tab>
        <md-tab label="Prior Occurrences">
          <md-content>
            <toggle-panel ng-repeat="priorMismatch in currentMismatch.prior.items"
                label="{{priorMismatch.reportId.reportDateTime | moment:'lll'}}">
              <div layout="row" layout-align="space-around">
                <h5 class="no-margin">Reference Date: {{priorMismatch.reportId.referenceDateTime | moment:'lll'}}</h5>
                <h5 class="no-margin">Status: {{priorMismatch.status | mismatchStatusLabel}}</h5>
              </div>
              <md-divider></md-divider>
              <p>
                <mismatch-diff diff="priorMismatch.diff"></mismatch-diff>
              </p>
            </toggle-panel>
          <md-content>
        </md-tab>
        <md-tab label="Other Mismatches">
          <md-content>
            <ul ng-show="allMismatches.length > 1" style="list-style-type: none">
              <li ng-repeat="mismatch in allMismatches">
                <span ng-show="mismatch.mismatchType==currentMismatch.mismatchType">
                  {{mismatch.mismatchType | mismatchTypeLabel}} - {{mismatch.status | mismatchStatusLabel}}
                </span>
                <a href="#" ng-show="mismatch.mismatchType!=currentMismatch.mismatchType"
                   ng-click="openNewDetail(getMismatchId(details.observation, mismatch))">
                    {{mismatch.mismatchType | mismatchTypeLabel}} - {{mismatch.status | mismatchStatusLabel}}
                </a>
              </li>
            </ul>
            <h4 ng-show="allMismatches.length <= 1" class="text-align-center">
              No other mismatches for {{contentId}}
            </h4>
          <md-content>
        </md-tab>
      </md-tabs>
    </md-content>
  </md-dialog>
</script>