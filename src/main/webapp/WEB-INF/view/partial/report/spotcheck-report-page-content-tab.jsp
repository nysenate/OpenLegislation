
<%-- Intended for use within SpotcheckReportCtrl --%>
<md-tab ng-cloak label="{{title}} ({{mismatchContentTypeSummary.summary.items[type]}})"
        md-on-deselect="onTabChange()">

  <div class="spotcheck-report-control-bar spotcheck-report-inner-controls">
    <select ng-model="mismatchStatusSummary.selected" ng-change="onStatusChange()">
      <option value="NEW" ng-disabled="mismatchStatusSummary.summary.items.NEW == 0">
        New Issues ({{mismatchStatusSummary.summary.items.NEW}})
      </option>
      <option value="OPEN" ng-disabled="mismatchStatusSummary.summary.items.OPEN == 0">
        Open Issues ({{mismatchStatusSummary.summary.items.OPEN}})
      </option>
      <option value="RESOLVED" ng-disabled="mismatchStatusSummary.summary.items.RESOLVED == 0">
        Resolved Issues ({{mismatchStatusSummary.summary.items.RESOLVED}})
      </option>
    </select>

    <select  ng-model="mismatchTypeSummary.selected"
             ng-change="onMismatchTypeChange()"
             ng-options="type as mismatchTypeLabel(type, count) disable when count == 0 for (type, count) in mismatchTypeSummary.summary">
    </select>
  </div>

  <md-content>
    <div class="spotcheck-table-header">
      <div class="spotcheck-col-state">State</div>

      <!-- Id column headers -->
      <div ng-repeat="col in idColumns[type]"
           ng-click="updateOrder(col.orderId, $event)"
           class="{{col.class}}"
           ng-bind="col.name"></div>

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
         pagination-id="paginationId"
         ng-show="loading === false"
         class="spotcheck-table-row">
      <div class="spotcheck-table-mismatch-row">
        <div class="spotcheck-col-state">{{mismatch.status}}</div>

        <!-- Id column data -->
        <div ng-repeat="col in idColumns[type]" class="{{col.class}}" ng-bind="mismatch[col.field]"></div>

        <div class="spotcheck-col-type">{{mismatch.mismatchType}}</div>
        <div class="spotcheck-col-date">{{mismatch.observedDate}}</div>
        <div class="spotcheck-col-issue">
          <md-input-container class="md-block">
            <input type="text"
                   title="Assign an issue id to this mismatch"
                   aria-label="Assign an issue id to this mismatch"
                   ng-model="mismatch.issueInput"
                   ng-keyup="$event.keyCode == 13 && updateIssue(mismatch)"
                   ng-blur="updateIssue(mismatch)">
            <div class="report-page-toast" ng-class="{'saved': mismatch.issueSaved}">Saved</div>
          </md-input-container>
        </div>
        <div class="spotcheck-col-source">{{mismatch.refTypeLabel}}</div>
      </div>
      <div class="spotcheck-table-buttons">
        <md-button class="md-raised rounded-corner-button"
                   ng-click="showDetailedDiff(mismatch)">
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
    <dir-pagination-controls pagination-id="paginationId" boundary-links="true"
                             on-page-change="onPageChange(newPageNumber, type)" max-size="10"
                             ng-if="loading === false">
    </dir-pagination-controls>
    <div class="spotcheck-table-goto" ng-show="showGoto">
      Go to:
      <input ng-model="pagination.currPage" ng-change="onPageChange(pagination.currPage, type)" type="text">
    </div>
  </md-content>
</md-tab>

