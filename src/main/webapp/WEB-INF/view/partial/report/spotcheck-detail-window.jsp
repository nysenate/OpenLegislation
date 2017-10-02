<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
  <md-dialog aria-label="Mismatch Detail" class="detail-diff-dialog">
    <div layout="row">
      <span flex></span>
      <span ng-click="cancel()" class="icon-cross" style="font-size: 24px; text-align: right;"></span>
    </div>
    <md-content>
      <md-content class="mismatch-diff-view-top-half">
        <div layout="row" layout-align="space-between center">


          <md-card  ng-if="contentType == 'BILL' " class="mismatch-diff-info-card mismatch-diff-info-border-radius mismatch-diff-info-background-color white-text-color">
            <p>Date Reported: {{date}}</p>

            <p> {{reportType | contentType}} Number:
              <a class="white-2-blue inactive-link" target="_blank">
                {{currentMismatch.bill}}
              </a>
            </p>
            <p>Session Year: {{currentMismatch.session.year}}</p>
            <p>Error Type: {{currentMismatch.mismatchType}}</p>
          </md-card>

          <md-card ng-if="contentType == 'CALENDAR' " class="mismatch-diff-info-card mismatch-diff-info-border-radius mismatch-diff-info-background-color white-text-color">
            <p>Date Reported: {{date}}</p>
            <p> Calendar Number: {{currentMismatch.calNo}}</p>
            <p>Session Date: {{currentMismatch.key.calDate}}</p>
            <p>Error Type: {{currentMismatch.mismatchType}}</p>
          </md-card>

          <md-card  ng-if="contentType == 'AGENDA' " class="mismatch-diff-info-card mismatch-diff-info-border-radius mismatch-diff-info-background-color white-text-color">
            <p>Date Reported: {{date}}</p>
            <p> Week:</p>
            <p>Agenda: {{currentMismatch.agendaNo}}</p>
            <p>Error Type: {{currentMismatch.mismatchType}}</p>
          </md-card>

          <md-card class="padding-5 mismatch-diff-info-border-radius mismatch-diff-info-background-color-border">
            <select ng-model="textControls.whitespace" ng-change="formatDisplayData()"
                    ng-options="value as label for (value, label) in whitespaceOptions"></select>
            <md-checkbox ng-model="textControls.removeLinePageNums" ng-change="formatDisplayData()">
              Strip Line/Page Numbers
            </md-checkbox>
            <md-checkbox ng-model="textControls.capitalize" ng-change="formatDisplayData()">All Caps</md-checkbox>
          </md-card>
        </div>
      </md-content>
      <md-content class="mismatch-diff-box">
        <div layout="row" layout-align="space-around start">
          <div layout="column" flex="45" id="mismatch-diff-left-side" class="scrollable">
             <div class="mismatch-ref-source-color align-text-hor-vert-center white-text-color">
               <a ng-if="currentMismatch.datasource === 'LBDC'"
                  ng-href="{{currentMismatch.key | referenceUrl:currentMismatch.datasource:currentMismatch.contentType}}" target="_blank" style="color: lightblue;">
                 <span>{{com[0]}}</span>
               </a>
               <a ng-if="currentMismatch.datasource === 'NYSENATE'"
                  ng-href="{{currentMismatch.key | contentUrl:currentMismatch.contentType}}" target="_blank" style="color: lightblue;">
                 <span>{{com[0]}}</span>
               </a>
            </div>

            <div id="mismatch-diff-data">
              <mismatch-diff show-lines="false" left="referenceData" right="observedData"></mismatch-diff>
            </div>
          </div>

          <div layout="column" flex="45" id="mismatch-diff-right-side" class="scrollable">
            <div class="mismatch-ref-source-color align-text-hor-vert-center white-text-color">
              <a ng-if="currentMismatch.datasource === 'LBDC'"
                 ng-href="{{currentMismatch.key | contentUrl:currentMismatch.contentType}}" target="_blank" style="color: lightblue;">
                <span>{{com[1]}}</span>
              </a>
               <a ng-if="currentMismatch.datasource === 'NYSENATE'"
                 ng-href="{{currentMismatch.key | referenceUrl:currentMismatch.datasource:currentMismatch.contentType}}" target="_blank" style="color: lightblue;">
                <span>{{com[1]}}</span>
              </a>
            </div>

            <div id="mismatch-diff-source">
              <mismatch-diff show-lines="false" left="referenceData" right="observedData"></mismatch-diff>
            </div>
          </div>
        </div>
      </md-content>
    </md-content>
  </md-dialog>
</script>