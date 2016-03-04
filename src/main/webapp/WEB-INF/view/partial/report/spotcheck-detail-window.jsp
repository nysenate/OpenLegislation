
<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail" class="detail-diff-dialog">
    <div layout="row">
      <span flex></span>
      <span ng-click="cancel()" class="icon-cross" style="font-size: 24px; text-align: right;"></span>
    </div>
    <md-content>
      <div id="mismatch-detail-top-section">
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
      </div>
      <md-tabs class="md-hue-2" md-selected="iDiffTab">
        <md-tab label="Diff">
          <md-content>
            <div layout="row" layout-align="space-around center">
              <div>
                <span class="diff-key-color del"></span>
                {{mismatchRow.refType | reportDataProvider}} Data
              </div>
              <div>
                <span class="diff-key-color ins"></span>
                {{mismatchRow.refType | reportReferenceProvider}} Data
              </div>
            </div>
            <div class="mismatch-detail-diff-container mismatch-diff-box" ng-class="{'padding-10': !multiLine}" ng-if="iDiffTab === 0">
              <mismatch-diff left="observedData" right="referenceData"></mismatch-diff>
            </div>
          </md-content>
        </md-tab>
        <md-tab label="Side By Side">
          <md-content>
            <div layout="row">
              <p flex class="text-align-center no-margin bold">{{mismatchRow.refType | reportDataProvider}} Data</p>
              <p flex class="text-align-center no-margin bold">{{mismatchRow.refType | reportReferenceProvider}} Data</p>
            </div>
            <div layout="row" ng-if="iDiffTab === 1"
                 class="mismatch-detail-diff-container" ng-class="{'mismatch-diff-box': sideScrollJoin}">
              <div flex class="mismatch-diff-box" ng-class="{'multi-line': obsMultiLine, 'scroll-join': sideScrollJoin}">
                <mismatch-diff right="observedData" left="observedData"></mismatch-diff>
              </div>
              <div flex class="mismatch-diff-box" ng-class="{'multi-line': refMultiLine, 'scroll-join': sideScrollJoin}">
                <mismatch-diff right="referenceData" left="referenceData"></mismatch-diff>
              </div>
            </div>
          </md-content>
        </md-tab>
      </md-tabs>
    </md-content>
  </md-dialog>
</script>
