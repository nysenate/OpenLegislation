<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="TranscriptListingCtrl">
  <md-tabs>
    <md-tab label="Recent">
      <md-divider></md-divider>
      <md-tabs class="md-hue-2">
        <md-tab label="Session Transcripts">
          <md-divider></md-divider>
          <md-card>
            <md-content>
              <md-list>
                <md-item ng-repeat="transcript in transcriptListing">
                  <md-item-content class="transcript-node" ng-click="go('${ctxPath}/transcripts/session/' + transcript.filename)">
                    <div class="md-tile-left padding-20">
                      {{formatDate(transcript.dateTime)}}
                    </div>
                    <div class="md-tile-content">
                      {{transcript.sessionType}}
                    </div>
                  </md-item-content>
                  <md-divider></md-divider>
                </md-item>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>

        <md-tab label="Public Hearing Transcripts">
          <md-divider></md-divider>
          <md-card>
            <md-content>
              <md-list>
                <md-item ng-repeat="hearing in publicHearingListing">
                  <md-item-content class="transcript-node" ng-click="go('${ctxPath}/transcripts/hearing/' + hearing.filename)">
                    <div class="md-tile-left padding-20">
                      {{formatDate(hearing.date)}}
                    </div>
                    <div class="md-tile-content">
                      {{hearing.title}}
                    </div>
                  </md-item-content>
                  <md-divider></md-divider>

                </md-item>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>
      </md-tabs>
    </md-tab>

    <md-tab label="Search">
      <md-divider></md-divider>
    </md-tab>
  </md-tabs>

</section>