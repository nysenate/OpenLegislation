<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="SessionTranscriptViewCtrl">

  <md-toolbar>
    <div class="md-toolbar-tools">
      {{sessionTranscriptDetails.sessionType}}: {{sessionTranscriptDetails.location}}
    </div>

  </md-toolbar>
  <md-tabs md-selected="1">
    <md-tab label="Back" md-on-select="back()">
      <md-divider></md-divider>
    </md-tab>
    <md-tab label="Details">
      <md-content>
        <div class="margin-left-20 bill-full-text">
          {{sessionTranscriptDetails.text}}
        </div>
      </md-content>
    </md-tab>
  </md-tabs>

</section>