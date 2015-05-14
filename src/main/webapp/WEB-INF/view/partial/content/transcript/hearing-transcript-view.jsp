<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="HearingTranscriptViewCtrl">

  <md-tabs md-selected="1" md-dynamic-height="false">
    <md-tab label="Back" md-on-select="back()">
    <md-divider></md-divider>
    </md-tab>
    <md-tab label="Details">
      <md-divider></md-divider>
      <md-content>
        <div class="margin-left-20 bill-full-text">
          {{hearingDetails.text}}
        </div>
      </md-content>
    </md-tab>
  </md-tabs>

</section>