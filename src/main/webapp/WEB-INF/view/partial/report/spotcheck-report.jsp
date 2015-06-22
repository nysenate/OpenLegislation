<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckCtrl" id="daybreak-page" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <section ng-controller="SpotcheckDetailCtrl" ng-init="init()">
    <md-card>
      <md-card-content ng-show="!loadingReport">
        <!--Title-->
        <h4>
          <span class="icon-graph blue-title-icon"></span>
          {{reportType | reportType}} {{referenceDateTime | moment:'lll'}} | Report Run: {{reportDateTime | moment:'lll'}}
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

            <div layout="row" layout-wrap>
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
          <tr ng-if="filteredTableData.length > resultsPerPage">
            <td colspan="6" style="border-bottom:1px solid #ccc">
              <div layout="row" layout-align="space-between center">
                <label>
                  Page:
                    <span st-pagination="" st-items-by-page="resultsPerPage" st-displayed-pages="5"
                          st-template="st-pagination-template"></span>
                </label>
                <label>
                  Displayed:
                  <input type="number" ng-model="resultsPerPage" placeholder="20">
                </label>
              </div>
            </td>
          </tr>
          <tr>
            <th st-sort="printNo">{{reportType | reportType | contentType}} Id</th>
            <th st-sort="type">Mismatch Type</th>
            <th st-sort="status">Status</th>
            <th st-sort="firstOpened" style="min-width: 8em">Opened At</th>
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
              <a ng-href="{{getReportURL(openReportType, row.firstOpened)}}"
                 ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                {{row.firstOpened | moment:'lll'}}
              </a>
                <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                  {{row.firstOpened | moment:'lll'}}
                </span>
            </td>
            <td>
              <div class="report-table-snippet">
                <mismatch-diff no-pre left="row.refData" right="row.obsData"></mismatch-diff>
              </div>
            </td>
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
</section>

<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail">
    <md-content class="padding-20 mismatch-dialog">
      <div layout="row" layout-align="space-around center" layout-wrap>
        <h5>
          {{reportType | contentType}}: <a ng-href="{{contentUrl}}" target="_blank">{{contentId}}</a><br/>
          Mismatch: {{currentMismatch.mismatchType | mismatchTypeLabel}}<br/>
          Status: {{currentMismatch.status | mismatchStatusLabel}}
        </h5>
        <h5>
          Opened on: {{firstOpened.reportDateTime | moment:'lll'}}<br/>
          First Reference: {{firstOpened.referenceDateTime | moment:'lll'}}<br/>
          Current Reference: {{observation.refDateTime | moment:'lll'}}
        </h5>
        <div ng-if="isBillTextMismatch()" layout="row" layout-align="space-around center">
          <div layout="column" layout-align="center start">
            <md-checkbox ng-model="billTextCtrls.normalizeSpaces" ng-disabled="billTextCtrls.removeNonAlphaNum"
                ng-change="formatDisplayData()">
              Normalize Spaces
            </md-checkbox>
            <md-checkbox ng-model="billTextCtrls.removeNonAlphaNum" ng-change="formatDisplayData()">
              Strip Non-Alphanumeric
            </md-checkbox>
          </div>
          <md-checkbox ng-model="billTextCtrls.removeLinePageNums" ng-change="formatDisplayData()"
              >Remove Line/Page Numbers</md-checkbox>
        </div>
      </div>
      <md-tabs md-dynamic-height="true" class="mismatch-dialog-tabs">
        <md-tab label="Diff">
          <md-content>
            <mismatch-diff left="lbdcData" right="openlegData"></mismatch-diff>
          </md-content>
        </md-tab>
        <md-tab label="LBDC">
          <md-content>
            <span ng-class="{preformatted: multiLine, 'word-wrap': !multiLine}" ng-bind="lbdcData"></span>
          </md-content>
        </md-tab>
        <md-tab label="Openleg">
          <md-content>
            <span ng-class="{preformatted: multiLine, 'word-wrap': !multiLine}" ng-bind="openlegData"></span>
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
                <diff-summary full-diff="priorMismatch.diff"></diff-summary>
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
                <a ng-show="mismatch.mismatchType!=currentMismatch.mismatchType"
                   ng-href="openNewDetail(getMismatchId(details.observation, mismatch))">
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

<jsp:include page="../core/st-pagination-template.jsp"/>
