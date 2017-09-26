<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl" id="spotcheck-report-page">
  <md-content class="spotcheck-report-control-bar spotcheck-report-top-controls">
    <h2>Report Date:</h2>
    <md-datepicker ng-model="pickedDate"
                   datepicker-popup="YYYY-MM-DD"
                   md-placeholder="Select Report Date"
                   md-max-date="maxDate"
                   ng-change="onDateChange()">
    </md-datepicker>
    <h2>Report Type:</h2>
    <select ng-model="datasource.selected" ng-change="onDatasourceChange()"
            ng-options="datasource as datasource.label for datasource in datasource.values"></select>
  </md-content>

  <md-card class="content-card spotcheck-report-content-tabs">
    <md-tabs md-selected="selectedTab" class="md-hue-2" md-dynamic-height=md-border-bottom>
      <md-tab ng-cloak label="Bills ({{mismatchContentTypeSummary.summary.items.BILL}})" md-on-deselect="onTabChange()"
              ng-disabled="mismatchContentTypeSummary.summary.items.BILL == 0">

        <spotcheck-report-inner-controls></spotcheck-report-inner-controls>

        <md-content>
          <div class="spotcheck-table-header">
            <div class="spotcheck-col-state">State</div>
            <div ng-click="updateOrder('PRINT_NO',$event)" class="spotcheck-col-id">Bill</div>
            <div ng-click="updateOrder('MISMATCH_TYPE',$event)" class="spotcheck-col-type">Error</div>
            <div ng-click="updateOrder('OBSERVED_DATE',$event)"  class="spotcheck-col-date">Date</div>
            <div ng-click="updateOrder('ISSUE',$event)" class="spotcheck-col-issue">Issue</div>
            <div ng-click="updateOrder('REFERENCE_TYPE',$event)" class="spotcheck-col-source">Source</div>
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
               ng-show="loading === false"
               class="spotcheck-table-row">
            <div class="spotcheck-table-mismatch-row">
              <div class="spotcheck-col-state">{{mismatch.status}}</div>
              <div class="spotcheck-col-id">{{mismatch.bill}}-{{mismatch.session.year}}</div>
              <div class="spotcheck-col-type">{{mismatch.mismatchType}}</div>
              <div class="spotcheck-col-date">{{mismatch.observedDate}}</div>
              <div class="spotcheck-col-issue">
                <md-input-container class="md-block">
                  <input type="text"
                         title="Assign an issue id to this mismatch" aria-label
                         ng-model="mismatch.issue"
                         ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)"
                         ng-blur="updateIssue(mismatch)">
                </md-input-container>
                <div ng-attr-id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div>
              </div>
              <div class="spotcheck-col-source">{{mismatch.refTypeLabel}}</div>
            </div>
            <div class="spotcheck-table-buttons">
              <md-button class="md-raised rounded-corner-button"
                         ng-click="showDetailedDiff(mismatch)"
                         ng-disabled ="mismatch.disableDiff">
                <div ng-show="mismatch.diffLoading === false">Diff</div>
                <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around">
                  <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                </div>
              </md-button>
              <md-button class="md-accent md-raised rounded-corner-button ignore-button"
                         ng-click="confirmIgnoreMismatch(mismatch)">Ignore
              </md-button>
            </div>
          </div>
          <dir-pagination-controls pagination-id="bill-mismatches" boundary-links="true"
                                   on-page-change="onPageChange(newPageNumber,'BILL')" max-size="10"
                                   ng-show="loading === false">
          </dir-pagination-controls>
          <div class="spotcheck-table-goto" ng-show="showGoto">
            Go to:
            <input ng-model="currentPage" ng-change="onGotoChange()" type="text">
          </div>
        </md-content>
      </md-tab>

      <md-tab label="Calendars ({{mismatchContentTypeSummary.summary.items.CALENDAR}})" md-on-deselect="onTabChange()"
        ng-disabled="mismatchContentTypeSummary.summary.items.CALENDAR == 0">
        <spotcheck-report-inner-controls></spotcheck-report-inner-controls>
        <md-content>
          <div class="spotcheck-table-header">
            <div class="spotcheck-col-cal-state">State</div>
            <div ng-click="updateOrder('CAL_NO',$event)" class="spotcheck-col-cal-no">Num</div>
            <div ng-click="updateOrder('CAL_TYPE',$event)" class="spotcheck-col-cal-type">Type</div>
            <div ng-click="updateOrder('MISMATCH_TYPE',$event)" class="spotcheck-col-type">Error</div>
            <div ng-click="updateOrder('OBSERVED_DATE',$event)"  class="spotcheck-col-date">Date</div>
            <div ng-click="updateOrder('ISSUE',$event)" class="spotcheck-col-issue">Issue</div>
            <div ng-click="updateOrder('REFERENCE_TYPE',$event)" class="spotcheck-col-source">Source</div>
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
               class="spotcheck-table-row"
               ng-show="loading === false">
            <div class="spotcheck-table-mismatch-row">
              <div class="spotcheck-col-cal-state">{{mismatch.status}}</div>
              <div class="spotcheck-col-cal-no">{{mismatch.calNo}}</div>
              <div class="spotcheck-col-cal-type">{{mismatch.calType}}</div>
              <div class="spotcheck-col-type">{{mismatch.mismatchType}}</div>
              <div class="spotcheck-col-date">{{mismatch.observedDate}}</div>
              <div class="spotcheck-col-issue">
                <md-input-container class="md-block">
                  <input type="text"
                         title="Assign an issue id to this mismatch" aria-label
                         ng-model="mismatch.issue"
                         ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)"
                         ng-blur="updateIssue(mismatch)">
                </md-input-container>
                <div ng-attr-id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div>
              </div>
              <div class="spotcheck-col-source">{{mismatch.refTypeLabel}}</div>
            </div>
            <div class="spotcheck-table-buttons">
              <md-button class="md-raised rounded-corner-button"
                         ng-click="showDetailedDiff(mismatch)"
                         ng-disabled ="mismatch.disableDiff">
                <div ng-show="mismatch.diffLoading === false">Diff</div>
                <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around">
                  <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                </div>
              </md-button>
              <md-button class="md-accent md-raised rounded-corner-button ignore-button"
                         ng-click="confirmIgnoreMismatch(mismatch)">Ignore
              </md-button>
            </div>
          </div>
          <dir-pagination-controls pagination-id="calendar-mismatches" boundary-links="true"
                                   on-page-change="onPageChange(newPageNumber,'CALENDAR')" max-size="10"
                                   ng-show="loading === false">
          </dir-pagination-controls>
          <div class="spotcheck-table-goto" ng-show="showGoto">
            Go to:
            <input ng-model="currentPage" ng-change="onGotoChange()" type="text">
          </div>
        </md-content>
      </md-tab>

      <md-tab label="Agendas ({{mismatchContentTypeSummary.summary.items.AGENDA}})" md-on-deselect="onTabChange()"
        ng-disabled="mismatchContentTypeSummary.summary.items.AGENDA == 0">
        <spotcheck-report-inner-controls></spotcheck-report-inner-controls>
        <md-content class="md-padding">
          <div class="spotcheck-table-header">
            <div class="spotcheck-col-state">State</div>
            <div ng-click="updateOrder('REFERENCE_DATE',$event)" class="spotcheck-col-agenda-obs-date">Report Date</div>
            <div ng-click="updateOrder('AGENDA_NO',$event)" class="spotcheck-col-agenda-no">Num</div>
            <div ng-click="updateOrder('AGENDA_COMMITTEE',$event)" class="spotcheck-agenda-comm">Committee</div>
            <div ng-click="updateOrder('MISMATCH_TYPE',$event)" class="spotcheck-col-type">Error</div>
            <div ng-click="updateOrder('OBSERVED_DATE',$event)"  class="spotcheck-col-date">Date</div>
            <div ng-click="updateOrder('ISSUE',$event)" class="spotcheck-col-issue">Issue</div>
            <div ng-click="updateOrder('REFERENCE_TYPE',$event)" class="spotcheck-col-source">Source</div>
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
               class="spotcheck-table-row"
               ng-show="loading === false">
            <div class="spotcheck-table-mismatch-row">
              <div class="spotcheck-col-state">{{mismatch.status}}</div>
              <div class="spotcheck-col-agenda-obs-date">{{mismatch.observedDate}}</div>
              <div class="spotcheck-col-agenda-no">{{mismatch.agendaNo}}</div>
              <div class="spotcheck-col-agenda-comm">{{mismatch.committee}}</div>
              <div class="spotcheck-col-type">{{mismatch.mismatchType}}</div>
              <div class="spotcheck-col-date">{{mismatch.observedDate}}</div>
              <div class="spotcheck-col-issue">
                <md-input-container class="md-block">
                  <input type="text"
                         title="Assign an issue id to this mismatch" aria-label
                         ng-model="mismatch.issue"
                         ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)"
                         ng-blur="updateIssue(mismatch)">
                </md-input-container>
                <div ng-attr-id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div>
              </div>
              <div class="spotcheck-col-source">{{mismatch.refTypeLabel}}</div>
            </div>
            <div class="spotcheck-table-buttons">
              <md-button class="md-raised rounded-corner-button"
                         ng-click="showDetailedDiff(mismatch)"
                         ng-disabled ="mismatch.disableDiff">
                <div ng-show="mismatch.diffLoading === false">Diff</div>
                <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around">
                  <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                </div>
              </md-button>
              <md-button class="md-accent md-raised rounded-corner-button ignore-button"
                         ng-click="confirmIgnoreMismatch(mismatch)">Ignore
              </md-button>
            </div>
          </div>
          <dir-pagination-controls pagination-id="agenda-mismatches" boundary-links="true"
                                   on-page-change="onPageChange(newPageNumber,'AGENDA')" max-size="10"
                                   ng-show="loading === false">
          </dir-pagination-controls>
          <div class="spotcheck-table-goto" ng-show="showGoto">
            Go to:
            <input ng-model="currentPage" ng-change="onGotoChange()" type="text">
          </div>
        </md-content>
      </md-tab>
    </md-tabs>
  </md-card>
</section>
<jsp:include page="spotcheck-detail-window.jsp"/>
