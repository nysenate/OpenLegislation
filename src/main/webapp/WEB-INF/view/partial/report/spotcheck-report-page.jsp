<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl"
         class="padding-20">
  <md-content>
    <div>
      <h2>Report Date: {{formatDate(date)}}</h2>
    </div>
    <div layout="row" layout-align="space-between center">
      <div>
        <select ng-model="datasource.selected" ng-change="onDatasourceChange()"
                ng-options="datasource as datasource.label for datasource in datasource.values"></select>
      </div>

      <div>
        <select ng-model="status" ng-change="onStatusChange()">
          <option value="OPEN">Open Issues ({{summaryResponse.summary.OPEN || 0}})</option>
          <option value="NEW">New Issues ({{summaryResponse.summary.NEW || 0}})</option>
          <option value="RESOLVED">Resolved Issues ({{summaryResponse.summary.RESOLVED || 0}})</option>
        </select>
      </div>
    </div>
  </md-content>

  <div>
    <md-card class="content-card">
      <md-tabs md-selected="selectedTab" class="md-hue-2" md-dynamic-height=md-border-bottom>
        <md-tab ng-cloak label="Bills ({{getSummaryCountForContentType('BILL') || 0}})" md-on-select="onTabChange()">
          <md-content>
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px; ">
              <div flex="5" class="bold">Status</div>
              <div flex="10" class="bold">Bill</div>
              <div flex="15" class="bold">Error</div>
              <div flex="20" class="bold">Date</div>
              <div flex="10" class="bold">Issue</div>
              <div flex="15" class="bold">Source</div>
            </div>
            <md-divider></md-divider>
            <md-progress-linear class="md-accent md-hue-1" md-mode="query"
                                ng-show="loading === true">
            </md-progress-linear>
            <md-subheader ng-show="loading === false && pagination.totalItems === 0" class="margin-10 md-warn">
              <h3>No mismatches were found</h3>
              <h3 ng-show="mismatchResponse.error === true" class="new-error">{{mismatchResponse.errorMessage}}</h3>
            </md-subheader>
            <div dir-paginate="mismatch in mismatchResponse.mismatches | itemsPerPage: pagination.itemsPerPage"
                 total-items="pagination.totalItems" current-page="pagination.currPage"
                 pagination-id="bill-mismatches"
                 layout="row" layout-align="space-around center"
                 ng-show="loading === false">
              <div layout="row" layout-align="space-between center" flex="75">
                <div flex="5">{{mismatch.status}}</div>
                <div flex="10">{{mismatch.bill}}</div>
                <div flex="15">{{mismatch.mismatchType}}</div>
                <div flex="20">{{mismatch.observedDate}}</div>
                <div flex="10"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div></div>
                <div flex="15">{{mismatch.refType}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                  <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls class="text-align-center" pagination-id="bill-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'BILL')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
          </md-content>
        </md-tab>

        <md-tab label="Calendars ({{getSummaryCountForContentType('CALENDAR') || 0}})" md-on-select="onTabChange()">
          <md-content>
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px">
              <div flex="5" class="bold">Status</div>
              <div flex="5" class="bold">Number</div>
              <div flex="10" class="bold">Type</div>
              <div flex="15" class="bold">Error</div>
              <div flex="15" class="bold">Date</div>
              <div flex="10" class="bold">Issue</div>
              <div flex="15" class="bold">Source</div>
            </div>
            <md-divider></md-divider>
            <md-progress-linear class="md-accent md-hue-1" md-mode="query"
                                ng-show="loading === true"></md-progress-linear>
            <md-subheader ng-show="loading === false && pagination.totalItems === 0" class="margin-10 md-warn">
              <h3>No mismatches were found</h3>
              <h3 ng-show="mismatchResponse.error === true" class="new-error">{{mismatchResponse.errorMessage}}</h3>
            </md-subheader>
            <div dir-paginate="mismatch in mismatchResponse.mismatches | itemsPerPage: pagination.itemsPerPage"
                 total-items="pagination.totalItems" current-page="pagination.currPage"
                 pagination-id="calendar-mismatches"
                 layout="row" layout-align="space-around center"
                 ng-show="loading === false">
              <div layout="row" layout-align="space-between center" flex="75">
                <div flex="5">{{mismatch.status}}</div>
                <div flex="5">{{mismatch.calNo}}</div>
                <div flex="10">{{mismatch.calType}}</div>
                <div flex="15">{{mismatch.mismatchType}}</div>
                <div flex="15">{{mismatch.observedDate}}</div>
                <div flex="10"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div></div>
                <div flex="15">{{mismatch.refType}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                    <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                  </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls class="text-align-center" pagination-id="calendar-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'CALENDAR')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
          </md-content>
        </md-tab>

        <md-tab label="Agendas ({{getSummaryCountForContentType('AGENDA') || 0}})" md-on-select="onTabChange()">
          <md-content class="md-padding">
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px">
              <div flex="5" class="bold">Status</div>
              <div flex="5" class="bold">Number</div>
              <div flex="15" class="bold">Committee</div>
              <div flex="10" class="bold">Error</div>
              <div flex="15" class="bold">Date</div>
              <div flex="10" class="bold">Issue</div>
              <div flex="15" class="bold">Source</div>
            </div>
            <md-divider></md-divider>
            <md-progress-linear class="md-accent md-hue-1" md-mode="query"
                                ng-show="loading === true">
            </md-progress-linear>
            <md-subheader ng-show="loading === false && pagination.totalItems === 0" class="margin-10 md-warn">
              <h3>No mismatches were found</h3>
              <h3 ng-show="mismatchResponse.error === true" class="new-error">{{mismatchResponse.errorMessage}}</h3>
            </md-subheader>
            <div dir-paginate="mismatch in mismatchResponse.mismatches | itemsPerPage: pagination.itemsPerPage"
                 total-items="pagination.totalItems" current-page="pagination.currPage"
                 pagination-id="agenda-mismatches"
                 layout="row" layout-align="space-around center"
                 ng-show="loading === false">
              <div layout="row" layout-align="space-between center" flex="75">
                <div flex="5">{{mismatch.status}}</div>
                <div flex="5">{{mismatch.agendaNo}}</div>
                <div flex="15">{{mismatch.committee}}</div>
                <div flex="10">{{mismatch.mismatchType}}</div>
                <div flex="15">{{mismatch.observedDate}}</div>
                <div flex="10"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div></div>
                <div flex="15">{{mismatch.refType}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                    <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                  </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls class="text-align-center" pagination-id="agenda-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'AGENDA')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
          </md-content>
        </md-tab>
      </md-tabs>
    </md-card>
  </div>
</section>
<jsp:include page="spotcheck-detail-window.jsp"/>
