<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail" class="detail-diff-dialog">
    <span ng-click="cancel()" class="icon-cross mismatch-diff-view-exit"></span>
    <md-content>
      <md-content class="mismatch-diff-view-top-half">
        <div layout="row" layout-align="space-between center">

          <md-card class="mismatch-diff-info-card">
            <p>Last Reported: {{currentMismatch.observedDate}}</p>

            <p ng-repeat="col in idCols">
              {{col.name}}: {{currentMismatch[col.field]}}
            </p>

            <p>Error Type: {{currentMismatch.mismatchType | mismatchType:currentMismatch.datasource}}</p>
          </md-card>
          <div layout="row" layout-align="space-between center">
            <md-button class="md-raised rounded-corner-button"
                       title="View the previous mismatch (left arrow key)"
                       ng-disabled="!prevMismatchExists()"
                       ng-click="loadPrevMismatch()">
              <
            </md-button>
            <md-button class="md-raised rounded-corner-button"
                       title="View the next mismatch (right arrow key)"
                       ng-disabled="!nextMismatchExists()"
                       ng-click="loadNextMismatch()">
              >
            </md-button>
          </div>
          <md-card class="mismatch-diff-text-controls">
            <select ng-model="textControls.whitespace" ng-change="formatDisplayData()"
                    ng-options="value as label for (value, label) in whitespaceOptions"></select>
            <md-checkbox ng-model="textControls.removeLinePageNums" ng-change="formatDisplayData()">
              Strip Line/PageWrapper Numbers
            </md-checkbox>
            <md-checkbox ng-model="textControls.capitalize" ng-change="formatDisplayData()">All Caps</md-checkbox>
          </md-card>
        </div>
      </md-content>

      <md-content>
        <div class="mismatch-diff-side">
           <div class="mismatch-diff-source-label">
             <a ng-href="{{currentMismatch.key | referenceUrl:currentMismatch.datasource:currentMismatch.contentType}}"
                target="_blank">
               <span ng-bind="currentMismatch.datasource | dataSourceRef"></span>
             </a>
          </div>

          <div id="mismatch-diff-reference" class="mismatch-diff-container">
            <mismatch-diff show-lines="false" left="observedData" right="referenceData"></mismatch-diff>
          </div>
        </div>

        <div class="mismatch-diff-side">
          <div class="mismatch-diff-source-label">
            <a ng-href="{{currentMismatch.key | contentUrl:currentMismatch.datasource:currentMismatch.contentType}}"
               target="_blank">
              <span ng-bind="currentMismatch.datasource | dataSourceData"></span>
            </a>
          </div>

          <div id="mismatch-diff-observed" class="mismatch-diff-container">
            <mismatch-diff show-lines="false" left="observedData" right="referenceData"></mismatch-diff>
          </div>
        </div>
      </md-content>
    </md-content>
  </md-dialog>
</script>