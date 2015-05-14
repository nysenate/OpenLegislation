<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="SessionTranscriptViewCtrl">

  <md-toolbar>
    <div class="md-toolbar-tools">
      {{sessionTranscriptDetails.sessionType}}: {{sessionTranscriptDetails.location}}
    </div>

  </md-toolbar>
  <md-tabs md-selected="1" md-dynamic-height="false">
    <md-tab label="Back" md-on-select="back()">
      <md-divider></md-divider>
    </md-tab>
    <md-tab label="Details">
      <md-content class="margin-top-20">
        <div class="margin-left-20 bill-full-text">
          {{sessionTranscriptDetails.text}}
        </div>
      </md-content>
    </md-tab>
  </md-tabs>

</section>