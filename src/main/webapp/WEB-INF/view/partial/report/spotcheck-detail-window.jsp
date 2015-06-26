
<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail">
    <span ng-click="cancel()" class="icon-cross" style="float:left;"></span>
    <md-content class="mismatch-dialog">
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
        <md-tab label="Prior Occurrences" ng-disabled="currentMismatch.prior.items.length < 1">
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
        <md-tab label="Other Mismatches" ng-disabled="getDetails === null">
          <md-content>
            <ul ng-show="allMismatches.length > 1" style="list-style-type: none">
              <li ng-repeat="mismatch in allMismatches">
                <span ng-show="mismatch.mismatchType==currentMismatch.mismatchType">
                  {{mismatch.mismatchType | mismatchTypeLabel}} - {{mismatch.status | mismatchStatusLabel}}
                </span>
                <a ng-show="mismatch.mismatchType!=currentMismatch.mismatchType" ng-href="#"
                   ng-click="openNewDetail(getMismatchId(details.observation, mismatch))">
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
