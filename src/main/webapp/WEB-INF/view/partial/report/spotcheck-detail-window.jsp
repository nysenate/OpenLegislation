
<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail" class="detail-diff-dialog">
    <div layout="row">
      <span flex></span>
      <span ng-click="cancel()" class="icon-cross" style="font-size: 24px; text-align: right;"></span>
    </div>
    <md-content>
      <!--<div id="mismatch-detail-top-section">
        <div layout="row" layout-align="space-around center">
          <h2 class="no-margin">
            {{reportType | contentType}}
            <a ng-href="{{mismatchRow.key | contentUrl:reportType}}" target="_blank">
              {{mismatchRow.key | contentId:reportType}}
            </a>
          </h2>
          <h2 class="spotcheck-mismatch-status no-margin">
            <span class="{{currentMismatch.status}}">
              {{currentMismatch.status | mismatchStatusLabel}}
            </span>
            {{currentMismatch.mismatchType | mismatchTypeLabel}} Mismatch
          </h2>
        </div>
        <md-divider></md-divider>
        <div layout="row" layout-align="space-around center" layout-wrap class="detail-diff-text-controls">
          <label>
            <span class="margin-right-10">Whitespace Formatting</span>
            <select ng-model="textControls.whitespace" ng-change="formatDisplayData()"
                    ng-options="value as label for (value, label) in whitespaceOptions"></select>
          </label>
          <md-checkbox ng-model="textControls.removeLinePageNums" ng-change="formatDisplayData()" >
            Strip Line/Page Numbers
          </md-checkbox>
          <md-checkbox ng-model="textControls.capitalize" ng-change="formatDisplayData()" >
            All Caps
          </md-checkbox>
          <md-checkbox ng-model="sideScrollJoin" ng-disabled="iDiffTab !== 1">
            Side-By-Side: Single Scrollbar
          </md-checkbox>
        </div>
      </div> -->

      <md-content>
        <div layout="row" layout-align="space-between start">
          <md-card class="padding-5 mismatch-diff-info-border-radius mismatch-diff-info-background-color white-text-color">
            <p>
              {{reportType | contentType}} Number:
              <a ng-href="{{mismatchRow.key | contentUrl:reportType}}" target="_blank">
                {{mismatchRow.key | contentId:reportType}}
              </a>
            </p>
            <p>
              Error Type: {{currentMismatch.mismatchType | mismatchTypeLabel}}
            </p>
          </md-card>
          <md-card class="padding-5 mismatch-diff-info-border-radius mismatch-diff-info-background-color-border">
            <select ng-model="textControls.whitespace" ng-change="formatDisplayData()"
                    ng-options="value as label for (value, label) in whitespaceOptions"></select>

            <md-checkbox ng-model="textControls.removeLinePageNums" ng-change="formatDisplayData()" >
              Strip Line/Page Numbers
            </md-checkbox>
            <md-checkbox ng-model="textControls.capitalize" ng-change="formatDisplayData()" >
              All Caps
            </md-checkbox>
            <md-checkbox ng-model="sideScrollJoin" ng-disabled="iDiffTab !== 1">
              Side-By-Side: Single Scrollbar
            </md-checkbox>
          </md-card>
        </div>
      </md-content>

      <md-content>
        <div layout="row" layout-align="space-around none" class="white-text-color mismatch-diff-info-background-color">
          <div>
            <p flex class="text-align-center no-margin bold">{{mismatchRow.refType | reportDataProvider}}</p>
          </div>
          <div>
            <p flex class="text-align-center no-margin bold">{{mismatchRow.refType | reportReferenceProvider}}</p>
          </div>
        </div>
        <div layout="row" class="mismatch-detail-diff-container" ng-class="{'mismatch-diff-box': sideScrollJoin}">
          <div flex class="mismatch-diff-box mismatch-diff-source" ng-class="{'multi-line': obsMultiLine, 'scroll-join': sideScrollJoin}">
            <mismatch-diff left="observedData" right="referenceData"></mismatch-diff>
          </div>
          <div flex class="mismatch-diff-box mismatch-diff-data" ng-class="{'multi-line': refMultiLine, 'scroll-join': sideScrollJoin}">
            <mismatch-diff left="observedData" right="referenceData"></mismatch-diff>
          </div>
        </div>
      </md-content>
    </md-content>
  </md-dialog>
</script>
