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
            <span ng-switch-when="false"><span class="icon-arrow-right prefix-icon"></span>Filter mismatches</span>
            <span ng-switch-when="true"><span class="icon-arrow-up prefix-icon"></span>Hide filter</span>
          </md-button>
          <br/>
          <div ng-show="showMismatchFilter" style="padding-top: 10px">
            <div layout="row" style="margin-bottom:10px;">
              <md-checkbox ng-model="errorFilter.all" class="md-primary" aria-label="Show all mismatches">
                Total<br/>{{ totals.total }}
              </md-checkbox>
              <md-checkbox ng-model="errorFilter.none" class="md-primary" aria-label="Show no mismatches">
                None<br/>&nbsp;
              </md-checkbox>
              <md-checkbox ng-model="errorFilter.statuses[status]"
                           ng-repeat="(status, total) in totals.statuses"
                           aria-label="Show {{status | mismatchStatusLabel}} mismatches"
                           ng-if="total > 0" class="md-primary">
                {{status | mismatchStatusLabel}}<br/>{{totals.statuses[status]}}
              </md-checkbox>
            </div>

            <div layout="row" layout-wrap>
              <md-checkbox ng-model="errorFilter.allTypes" class="md-primary" aria-label="Show all mismatch types">
                All<br/>Types
              </md-checkbox>
              <md-checkbox ng-model="errorFilter.noTypes" class="md-primary" aria-label="Show no mismatch types">
                No<br/>Types
              </md-checkbox>
              <md-checkbox ng-model="errorFilter.types[type]"
                           ng-repeat="(type, total) in filteredTypeTotals"
                           class="md-primary" aria-label="Show {{type | mismatchTypeLabel}} mismatches">
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
              <a ng-href="{{getContentUrl(reportType, row.key)}}" target="_blank">{{getContentId(reportType, row.key)}}</a>
            </td>
            <td>{{row.type | mismatchTypeLabel}}</td>
            <td>{{row.status | mismatchStatusLabel}}</td>
            <td>
              <a ng-href="{{getReportURL(reportType, row.firstOpened)}}"
                 ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                {{row.firstOpened | moment:'lll'}}
              </a>
              <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                {{row.firstOpened | moment:'lll'}}
              </span>
            </td>
            <td>
              <div class="report-table-snippet">
                <mismatch-diff no-pre left="row.refData | limitTo:100" right="row.obsData | limitTo:100"></mismatch-diff>
              </div>
            </td>
            <td><a href='#' ng-click='openDetailWindow(row)'>Details</a></td>
          </tr>
          </tbody>
        </table>
      </md-card-content>
      <md-card-content ng-show="loadingReport">
        <h3>Loading Report...</h3>
        <md-progress-linear md-mode="indeterminate"></md-progress-linear>
      </md-card-content>
    </md-card>
  </section>
</section>

<jsp:include page="spotcheck-detail-window.jsp"/>
<jsp:include page="../core/st-pagination-template.jsp"/>
