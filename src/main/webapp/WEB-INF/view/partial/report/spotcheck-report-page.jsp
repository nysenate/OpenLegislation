<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="SpotcheckReportCtrl"
         class="padding-20" style="overflow: scroll">
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

  <div style="min-width: 960px;">
    <md-card class="content-card">
      <md-tabs md-selected="selectedTab" class="md-hue-2" md-dynamic-height=md-border-bottom>
        <md-tab ng-cloak label="Bills ({{mismatchContentTypeSummary.summary.items.BILL}})" md-on-deselect="onTabChange()"
                ng-disabled="mismatchContentTypeSummary.summary.items.BILL == 0">

          <spotcheck-report-inner-controls></spotcheck-report-inner-controls>

          <md-content>
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px; ">
              <div flex="10" class="bold">State</div>
              <div ng-click="updateOrder('PRINT_NO',$event)" flex="15" class="bold">Bill</div>
              <div ng-click="updateOrder('MISMATCH_TYPE',$event)" flex="15" class="bold">Error</div>
              <div ng-click="updateOrder('OBSERVED_DATE',$event)"  flex="15" class="bold">Date</div>
              <div ng-click="updateOrder('ISSUE',$event)" flex="10" class="bold">Issue</div>
              <div ng-click="updateOrder('REFERENCE_TYPE',$event)" flex="10" class="bold">Source</div>
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
                <div flex="10"  class="columnWord">{{mismatch.status}}</div>
                <div flex="15"  class="columnWord">{{mismatch.bill}}-{{mismatch.session.year}}</div>
                <div flex="15" class="columnWord">{{mismatch.mismatchType}}</div>
                <div flex="15"  class="columnWord">{{mismatch.observedDate}}</div>
                <div flex="10" class="columnWord"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div></div>
                <div flex="10"  class="columnWord">{{mismatch.refTypeLabel}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)" ng-disabled ="mismatch.disableDiff">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                  <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button"  style="background-color: #3E4F62" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls pagination-id="bill-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'BILL')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
            <div style="float: right; margin-top: -35px;margin-right: 300px;" ng-show="showGoto">
              Go to: <input ng-model="currentPage" ng-change="onGotoChange()" type="text" style="width: 30px; text-align: center" >
            </div>
          </md-content>
        </md-tab>

        <md-tab label="Calendars ({{mismatchContentTypeSummary.summary.items.CALENDAR}})" md-on-deselect="onTabChange()"
          ng-disabled="mismatchContentTypeSummary.summary.items.CALENDAR == 0">
          <spotcheck-report-inner-controls></spotcheck-report-inner-controls>
          <md-content>
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px">
              <div flex="5" class="bold">State</div>
              <div ng-click="updateOrder('CAL_NO',$event)" flex="5" class="bold">Num</div>
              <div ng-click="updateOrder('MISMATCH_TYPE',$event)" flex="15" class="bold">Error</div>
              <div ng-click="updateOrder('CAL_TYPE',$event)" flex="10" class="bold">Type</div>
              <div ng-click="updateOrder('OBSERVED_DATE',$event)" flex="15" class="bold">Date</div>
              <div ng-click="updateOrder('ISSUE',$event)" flex="10" class="bold">Issue</div>
              <div ng-click="updateOrder('REFERENCE_TYPE',$event)"  flex="15" class="bold">Source</div>
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
                <div flex="5"  class="columnWord">{{mismatch.status}}</div>
                <div flex="5"  class="columnWord">{{mismatch.calNo}}</div>
                <div flex="15" class="columnWord">{{mismatch.mismatchType}}</div>
                <div flex="10"  class="columnWord">{{mismatch.calType}}</div>
                <div flex="15"  class="columnWord">{{mismatch.observedDate}}</div>
                <div flex="10"  class="columnWord"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}"  class="report-page-toast">Saved</div></div>
                <div flex="15"  class="columnWord">{{mismatch.refTypeLabel}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)" ng-disabled ="mismatch.disableDiff">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                    <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                  </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button"   style="background-color: #3E4F62" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls pagination-id="calendar-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'CALENDAR')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
            <div style="float: right; margin-top: -35px;margin-right: 300px;" ng-show="showGoto">
              Go to: <input ng-model="currentPage" ng-change="onGotoChange()" type="text" style="width: 30px; text-align: center" >
            </div>
          </md-content>
        </md-tab>

        <md-tab label="Agendas ({{mismatchContentTypeSummary.summary.items.AGENDA}})" md-on-deselect="onTabChange()"
          ng-disabled="mismatchContentTypeSummary.summary.items.AGENDA == 0">
          <spotcheck-report-inner-controls></spotcheck-report-inner-controls>
          <md-content class="md-padding">
            <div layout="row" layout-align="space-between center" flex="75"
                 style="padding-bottom: 10px; padding-top: 10px">
              <div flex="10" class="bold">State</div>
              <div ng-click="updateOrder('REFERENCE_DATE',$event)" flex="10" class="bold">Report Date</div>
              <div ng-click="updateOrder('MISMATCH_TYPE',$event)" flex="10" class="bold">Error</div>
              <div ng-click="updateOrder('AGENDA_NO',$event)" flex="5" class="bold">Num</div>
              <div ng-click="updateOrder('AGENDA_COMMITTEE',$event) "flex="15" class="bold">Committee</div>
              <div ng-click="updateOrder('OBSERVE_DATE',$event)" flex="15" class="bold">Date/Time</div>
              <div ng-click="updateOrder('ISSUE',$event)" flex="10" class="bold">Issue</div>
              <div ng-click="updateOrder('REFERENCE_TYPE',$event)" flex="15" class="bold">Source</div>
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
                <div flex="10" class="columnWord">{{mismatch.status}}</div>
                <div flex="10"  class="columnWord">{{mismatch.observedDate}}</div>
                <div flex="10"  class="columnWord">{{mismatch.mismatchType}}</div>
                <div flex="5"  class="columnWord">{{mismatch.agendaNo}}</div>
                <div flex="15" class="columnWord">{{mismatch.committee}}</div>
                <div flex="15"  class="columnWord">{{mismatch.referenceDate}}</div>
                <div flex="10"  class="columnWord"><md-input-container class="md-block" style="padding: 0px;margin: 0px;  margin-bottom: -25px;"><label></label><input type="text" ng-model="mismatch.issue"  ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)" ng-blur="updateIssue(mismatch)"></md-input-container><div id="report-page-toast{{mismatch.id}}" class="report-page-toast">Saved</div></div>
                <div flex="15"  class="columnWord">{{mismatch.refTypeLabel}}</div>
              </div>
              <div layout="row" layout-align="space-around center" flex="25">
                <md-button class="md-raised rounded-corner-button"  ng-click="showDetailedDiff(mismatch)" ng-disabled ="mismatch.disableDiff">
                  <div ng-show="mismatch.diffLoading === false">Diff</div>
                  <div ng-show="mismatch.diffLoading" layout="row" layout-sm="column" layout-align="space-around" >
                    <md-progress-circular  md-diameter="25px" md-mode="indeterminate" class="md-accent"></md-progress-circular>
                  </div></md-button>
                <md-button class="md-accent md-raised rounded-corner-button"   style="background-color: #3E4F62" ng-click="confirmIgnoreMismatch(mismatch)">Ignore</md-button>
              </div>
            </div>
            <dir-pagination-controls pagination-id="agenda-mismatches" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber,'AGENDA')" max-size="10"
                                     ng-show="loading === false">
            </dir-pagination-controls>
            <div style="float: right; margin-top: -35px;margin-right: 300px;" ng-show="showGoto">
              Go to: <input ng-model="currentPage" ng-change="onGotoChange()" type="text" style="width: 30px; text-align: center" >
            </div>
          </md-content>
        </md-tab>
      </md-tabs>
    </md-card>
  </div>
</section>
<jsp:include page="spotcheck-detail-window.jsp"/>
