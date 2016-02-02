<!-- Ordering, Tracked/Ignored Filtering -->
<div class="gray2-bg">
  <div class="text-medium padding-20" layout="row">
    <div flex layout="column" layout-align="space-around start">
      <div flex class="margin-5">
        <label class="margin-right-10">Order By</label>
        <select ng-model="filter.orderBy" ng-change="onFilterChange()"
                ng-options="orderBy as label for (orderBy, label) in orderByLabels"></select>
      </div>
      <div flex class="margin-5" style="margin-left: 2.65em">
        <label class="margin-right-10">Sort</label>
        <select ng-model="filter.sortOrder" ng-change="onFilterChange()"
                ng-options="order as label for (order, label) in sortOrderLabels"></select>
      </div>
    </div>
    <div flex layout="column" layout-align="space-around start">
      <div flex class="margin-5">
        <label class="margin-right-10">Ignored Mismatches</label>
        <select ng-model="state.ignoreFilter" ng-change="onIgnoreChange()"
                ng-options="ignoreFilterOptions.indexOf(label) as label for label in ignoreFilterOptions | filter:'!unused'">
        </select>
      </div>
      <div flex class="margin-5">
        <label class="margin-right-10">Tracked Mismatches</label>
        <select ng-model="state.trackingFilter" ng-change="onTrackingChange()"
                ng-options="trackingFilterOptions.indexOf(label) as label for label in trackingFilterOptions.slice().reverse() | limitTo:3"></select>
      </div>
    </div>
  </div>
</div>

<!-- Mismatch Filter -->
<div>
  <md-tabs ng-if="showStatusFilter" class="md-hue-2" md-selected="iSelectedStatus">
    <md-tab ng-repeat="status in statusOptions">
      <md-tab-label>
        <span ng-if="status === 'all'">All {{total}}</span>
        <span ng-if="status !== 'all'">{{status | mismatchStatusLabel}} {{summary.mismatchStatuses[status]}}</span>
      </md-tab-label>
    </md-tab>
  </md-tabs>
  <md-tabs class="md-hue-2" md-selected="iSelectedType">
    <md-tab ng-repeat="type in typeOptions">
      <md-tab-label ng-switch="type">
        <span ng-if="type === 'all'">
          <span ng-if="showStatusFilter">All Types</span>
          <span ng-if="!showStatusFilter">All {{total}}</span>
        </span>
        <span ng-if="type !== 'all'">{{type | mismatchTypeLabel}} {{getTypeCount(type)}}</span>
      </md-tab-label>
    </md-tab>
  </md-tabs>
</div>

<!-- Mismatch List -->
<div ng-if="state.filterLoaded" class="spotcheck-mismatch-list-container">
  <div class="mismatch-loading-overlay" layout="row" layout-align="center center" ng-show="isLoading()">
    <md-progress-circular class="md-hue-2" md-mode="indeterminate" md-diameter="200"></md-progress-circular>
  </div>
  <dir-pagination-controls class="text-align-center" pagination-id="spotcheck-mismatch" boundary-links="true" max-size="10">
  </dir-pagination-controls>
  <md-list class="spotcheck-mismatch-list">
    <div dir-paginate="mismatchRow in mismatches | itemsPerPage:filter.limit"
         pagination-id="spotcheck-mismatch" current-page="state.currentPage" total-items="summary | mismatchCount:filter"
         ng-init="mismatchOpen = false">
      <md-list-item layout="row" ng-click="mismatchOpen = !mismatchOpen" tabindex="1"
           class="spotcheck-mismatch" ng-class="{'not-first': !$first, open: mismatchOpen, loading: isLoading()}">
        <p flex="15" class="mismatch-content-key">
          {{mismatchRow.refType | contentType}}
          <a ng-bind="mismatchRow.key | contentId:mismatchRow.refType"
             ng-href="{{mismatchRow.key | contentUrl:mismatchRow.refType}}"></a>
        </p>
        <p class="spotcheck-mismatch-status">
          <span class="{{mismatchRow.status}}" ng-bind="mismatchRow.status | mismatchStatusLabel"></span>
          {{mismatchRow.type | mismatchTypeLabel}}
        </p>
        <p>{{mismatchRow.observed | moment:'lll'}}</p>
        <div flex="40" layout="row" layout-align="space-between center">
          <mismatch-diff no-pre class="mismatch-snippet"
                         left="mismatchRow.obsData | limitTo:100"
                         right="mismatchRow.refData | limitTo:100"></mismatch-diff>
          <md-chips ng-model="mismatchRow.chips" readonly="true">
            <md-chip-template>
              <span ng-if="ignoreStatuses.hasOwnProperty($chip) && $chip !== 'NOT_IGNORED'">{{$chip | ignoreLabel}}</span>
              <span ng-if="$chip | number">
                Issue <a ng-href="{{getIssueUrl($chip)}}">\#{{$chip}}</a>
              </span>
            </md-chip-template>
          </md-chips>
        </div>
      </md-list-item>
      <div ng-if="mismatchOpen" class="spotcheck-mismatch-details"
           ng-init="singleLineObs = mismatchRow.obsData.indexOf('\n') < 0; singleLineRef = mismatchRow.refData.indexOf('\n') < 0; singleLineDiff = singleLineObs && singleLineRef">
        <md-divider></md-divider>
        <md-tabs class="diff-tabs md-hue-2" md-dynamic-height="true" md-selected="iDiffTab" ng-init="iDiffTab = 0">
          <md-tab label="Diff">
            <md-content>
              <diff-key></diff-key>
              <div class="mismatch-diff-box" ng-if="iDiffTab === 0" ng-class="{'padding-10': singleLineDiff}">
                <mismatch-diff class="margin-top-10" right="mismatchRow.refData" left="mismatchRow.obsData"></mismatch-diff>
              </div>
            </md-content>
          </md-tab>
          <md-tab label="Side By Side">
            <md-content>
              <div layout="row">
                <p flex class="text-align-center no-margin bold">Observed Data</p>
                <p flex class="text-align-center no-margin bold">Reference Data</p>
              </div>
              <div layout="row" class="diff-side" ng-if="sideDiffOpened || iDiffTab === 1" ng-init="sideDiffOpened=true">
                <div flex class="mismatch-diff-box" ng-class="{'padding-10': singleLineObs}">
                  <mismatch-diff right="mismatchRow.obsData" left="mismatchRow.obsData"></mismatch-diff>
                </div>
                <div flex class="mismatch-diff-box" ng-class="{'padding-10': singleLineRef}">
                  <mismatch-diff right="mismatchRow.refData" left="mismatchRow.refData"></mismatch-diff>
                </div>
              </div>
            </md-content>
          </md-tab>
          <md-tab label="Ignore Status">
            <md-content ng-init="newIgnoreStatus = mismatchRow.mismatch.ignoreStatus">
              <div layout="row" layout-align="space-between center" class="mismatch-ignore-conf">
                <md-radio-group layout="row" layout-align="space-around center" ng-model="newIgnoreStatus">
                  <label class="margin-right-20 text-medium bold">Ignore Status</label>
                  <md-radio-button ng-repeat="(ignoreStatus, label) in ignoreStatuses"
                                   ng-value="ignoreStatus" layout="column" layout-align="center start">
                    <span class="text-medium">{{label}}</span>
                    <span ng-if="ignoreStatus === mismatchRow.mismatch.ignoreStatus" class="text-medium">
                      <br>(Current)
                    </span>
                  </md-radio-button>
                </md-radio-group>
                <md-progress-circular md-mode="{{state.settingIgnoreStatus ? 'indeterminate' : ''}}"></md-progress-circular>
                <md-button class="md-raised" ng-click="setIgnoreStatus(mismatchRow, newIgnoreStatus)"
                           ng-disabled="newIgnoreStatus === mismatchRow.mismatch.ignoreStatus || state.settingIgnoreStatus">
                  Set Ignore Status
                </md-button>
              </div>
            </md-content>
          </md-tab>
          <md-tab label="Issue Ids">
            <md-content>
              <p ng-show="mismatchRow.mismatch.issueIds.items.length === 0 && !state.settingIssueId" class="bold">
                No associated issues</p>
              <md-progress-linear md-mode="{{state.settingIssueId ? 'indeterminate' : ''}}"></md-progress-linear>
              <md-list ng-show="mismatchRow.mismatch.issueIds.items.length > 0">
                <md-list-item ng-repeat="issueId in mismatchRow.mismatch.issueIds.items" layout="row" layout-align="start center">
                  <md-button class="md-raised" ng-click="removeIssueIdPrompt(mismatchRow, issueId)"
                             ng-disabled="state.settingIssueId">
                    Remove
                  </md-button>
                  <p>Issue <a ng-href="{{getIssueUrl(issueId)}}">\#{{issueId}}</a></p>
                </md-list-item>
              </md-list>
              <form layout="row" layout-align="start center">
                <md-input-container class="no-bottom-margin margin-right-20">
                  <label>New Issue Id</label>
                  <input type="number" min="0" step="1" ng-model="mismatchRow.newIssueId">
                </md-input-container>
                <md-button type="submit" class="md-raised margin-bottom-10" ng-click="addIssueId(mismatchRow)"
                           ng-disabled="state.settingIssueId">
                  Add Issue Id
                </md-button>
              </form>
            </md-content>
          </md-tab>
          <md-tab label="Additional Info">
            <md-content>
              <md-list style="max-width:30em">
                <md-list-item layout="row" layout-align="start center">
                  <p>Last Observed Report</p>
                  <a ng-href="{{ctxPath}}/admin/report/spotcheck/{{mismatchRow.refType | reportType}}/{{mismatchRow.reportDateTime}}"
                     flex ng-bind="mismatchRow.reportDateTime | moment:'lll'"></a>
                </md-list-item>
                <md-list-item layout="row" layout-align="start center">
                  <p>First Observed Report</p>
                  <a ng-href="{{ctxPath}}/admin/report/spotcheck/{{mismatchRow.refType | reportType}}/{{mismatchRow.firstOpened}}"
                     flex ng-bind="mismatchRow.firstOpened | moment:'lll'"></a>
                </md-list-item>
              </md-list>
            </md-content>
          </md-tab>
        </md-tabs>
      </div>
    </div>
  </md-list>
</div>
