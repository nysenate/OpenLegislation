<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail" class="detail-diff-dialog">
    <div layout="row">
      <span flex></span>
      <span ng-click="cancel()" class="icon-cross" style="font-size: 24px; text-align: right;"></span>
    </div>
    <md-content>
      <md-content>
        <div layout="row" layout-align="space-between start">
          <md-card class="padding-5 mismatch-diff-info-border-radius mismatch-diff-info-background-color white-text-color">
            <p>
              {{reportType | contentType}} Number:
              <a class="white-text-color" id="no-text-decoration" ng-href="{{mismatchRow.key | contentUrl:reportType}}" target="_blank">
                {{mismatchRow.key | contentId:reportType}}
              </a>
            </p>
            <p>
              Session Year: {{}}
            </p>
            <p>
              Error Type: {{currentMismatch.mismatchType | mismatchTypeLabel}}
            </p>
          </md-card>
          <md-card class="padding-5 mismatch-diff-info-border-radius mismatch-diff-info-background-color-border">
            <select ng-model="textControls.whitespace" ng-change="formatDisplayData()"
                    ng-options="value as label for (value, label) in whitespaceOptions"></select>

            <md-checkbox ng-model="textControls.removeLinePageNums" ng-change="formatDisplayData()">
              Strip Line/Page Numbers
            </md-checkbox>
            <md-checkbox ng-model="textControls.capitalize" ng-change="formatDisplayData()">
              All Caps
            </md-checkbox>
            <md-checkbox ng-model="sideScrollJoin" ng-disabled="iDiffTab !== 1">
              Side-By-Side: Single Scrollbar
            </md-checkbox>
          </md-card>
        </div>
      </md-content>

      <md-content>
        <div layout="row">
          <md-toolbar>
            <div class="md-toolbar-tools white-text-color align-text-hor-vert-center" layout-align="space-between none">
              <div flex="45" class="mismatch-diff-info-background-color">
                <span>{{mismatchRow.refType | reportDataProvider}}</span>
              </div>

              <div flex="45" class="mismatch-diff-info-background-color">
                <span>{{mismatchRow.refType | reportReferenceProvider}}</span>
              </div>
            </div>
          </md-toolbar>
        </div>

        <div layout="row" class="mismatch-detail-diff-container" ng-class="{'mismatch-diff-box': sideScrollJoin}">
          <div class="mismatch-diff-box mismatch-diff-source"
               ng-class="{'multi-line': obsMultiLine, 'scroll-join': sideScrollJoin}">
            <mismatch-diff left="observedData" right="referenceData"></mismatch-diff>
          </div>
          <div class="mismatch-diff-box mismatch-diff-data"
               ng-class="{'multi-line': refMultiLine, 'scroll-join': sideScrollJoin}">
            <mismatch-diff left="observedData" right="referenceData"></mismatch-diff>
          </div>
        </div>
      </md-content>
    </md-content>
  </md-dialog>
</script>
