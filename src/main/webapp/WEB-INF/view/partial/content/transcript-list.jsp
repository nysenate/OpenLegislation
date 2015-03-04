<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="TranscriptListingCtrl">

  <%----- Browsing Tabs -----%>

    <md-tabs class="md-primary">
    <md-tab label="Browse">
      <md-divider></md-divider>
      <md-tabs class="md-primary">
        <md-tab label="Session Transcripts">
          <md-divider></md-divider>
          <md-card>
            <md-content>
              <md-list>
                <a ng-repeat="transcript in transcriptListing" ng-click="go('${ctxPath}/transcripts/session/' + transcript.filename)"
                   class="result-link transcript-result-link">
                  <md-item>
                    <md-item-content>
                      <div class="transcript-result-left">
                        <h3>
                          {{transcript.dateTime | date:'mediumDate'}}
                        </h3>
                      </div>
                      <div class="md-tile-content">
                        <h3>
                          {{transcript.sessionType}}
                        </h3>
                      </div>
                    </md-item-content>
                    <md-divider></md-divider>
                  </md-item>
                </a>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>

        <md-tab label="Public Hearing Transcripts">
          <md-divider></md-divider>
          <md-card>
            <md-content>
              <md-list>
                <a ng-repeat="hearing in publicHearingListing" ng-click="go('${ctxPath}/transcripts/hearing/' + hearing.filename)"
                   class="result-link transcript-result-link">
                  <md-item>
                    <md-item-content>
                      <div class="transcript-result-left">
                        <h3>
                          {{hearing.date | date:'mediumDate'}}
                        </h3>
                      </div>
                      <div class="md-tile-content">
                        <h3>
                          {{hearing.title}}
                        </h3>
                      </div>
                    </md-item-content>
                    <md-divider></md-divider>
                  </md-item>
                </a>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>
      </md-tabs>
    </md-tab>

    <%----- Search Tabs -----%>

    <md-tab label="Search">
      <md-divider></md-divider>
      <md-tabs>

        <%----- Session Transcript Search -----%>
        <md-tab label="Session Transcripts">
          <md-card>
            <md-content class="md-padding">
              <form name="searchForm">
                <md-input-container class="md-primary">
                  <label for="sessionSearch">
                    <i class="prefix-icon2 icon-search"></i>
                    Search Session Transcripts
                  </label>
                  <input ng-model="transcriptSearch.term" ng-model-options="{debounce: 300}" ng-change="searchTranscripts()" id="sessionSearch">
                </md-input-container>
              </form>
            </md-content>
          </md-card>
          <md-card class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{transcriptSearch.response.total}} matches were found.</div>
              <div flex style="text-align: right;"><dir-pagination-controls boundary-links="true"></dir-pagination-controls></div>
            </div>
            <md-content>
              <md-list>
                <a dir-paginate="match in transcriptSearch.matches | itemsPerPage: 10"
                   ng-click="go('${ctxPath}/transcripts/session/' + match.result.filename)"
                   class="result-link transcript-result-link">
                  <md-item>
                    <md-item-content>
                      <div class="transcript-result-left">
                        <h3>
                          {{match.result.dateTime | date:'mediumDate'}}
                        </h3>
                      </div>
                      <div class="md-tile-content">
                        <h4>
                      <span ng-repeat="field in match.highlights">
                        <span ng-repeat="fragment in field">
                          <span ng-bind-html="fragment"></span>
                          <br/>
                        </span>
                      </span>
                        </h4>
                      </div>
                    </md-item-content>
                    <md-divider></md-divider>
                  </md-item>
                </a>
              </md-list>
            </md-content>
            <div class="subheader" layout="row" layout-align="end center">
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card class="content-card">
            <md-subheader><strong>Session Transcript Search Tips</strong></md-subheader>
            <table class="docs-table">
            <thead>
            <tr><th>To Search for</th><th>Use the field</th><th>Example</th></tr>
            </thead>
              <tbody>
              <tr><td>Filename</td><td>filename</td><td>filename:031297.v1</td></tr>
              <tr><td>Date and Time</td><td>dateTime</td><td>dateTime:"2014-03-03T15:44"</td></tr>
              <tr><td>Session Type</td><td>sessionType</td><td>sessionType:extraordinary session</td></tr>
              <tr><td>Text</td><td>text</td><td>text:property tax</td></tr>
              </tbody>
            </table>
          </md-card>
        </md-tab>

        <%----- Public Hearing Search -----%>
        <md-tab label="Public Hearing Transcripts">
          <md-card>
            <md-content class="md-padding">
              <form name="searchForm"> <%--  --%>
                <md-input-container class="md-primary">
                  <label for="hearingSearch">
                    <i class="prefix-icon2 icon-search"></i>
                    Search Public Hearing Transcripts
                  </label>
                  <input ng-model="hearingSearch.term" ng-model-options="{debounce: 300}" ng-change="searchHearings()" id="hearingSearch">
                </md-input-container>
              </form>
            </md-content>
          </md-card>
          <md-card>
            <md-content>
              <md-list>
                <a ng-repeat="match in hearingSearch.matches"
                   ng-click="go('${ctxPath}/transcripts/hearing/' + match.result.filename)"
                   class="result-link transcript-result-link">
                  <md-item>
                    <md-item-content>
                      <div class="transcript-result-left">
                        <h3>
                          {{match.result.date | date:'mediumDate'}}
                        </h3>
                      </div>
                      <div class="md-tile-content">
                        <h4>
                      <span ng-repeat="field in match.highlights">
                        <span ng-repeat="fragment in field">
                          <span ng-bind-html="fragment"></span>
                          <br/>
                        </span>
                      </span>
                        </h4>
                      </div>
                    </md-item-content>
                    <md-divider></md-divider>
                  </md-item>
                </a>
              </md-list>
            </md-content>
            <%--<div>--%>
            <%--pagnation stuff here--%>
            <%--</div>--%>
          </md-card>
          <md-card class="content-card">
            <md-subheader><strong>Public Hearing Transcript Search Tips</strong></md-subheader>
            <table class="docs-table">
              <thead>
              <tr><th>To Search for</th><th>Use the field</th><th>Example</th></tr>
              </thead>
              <tbody>
              <tr><td>Filename</td><td>filename</td><td>filename:"10-10-13 NYsenate_Fuschillo_MTA_FINAL.txt"</td></tr>
              <tr><td>Date</td><td>date</td><td>date:2013-11-13</td></tr>
              <tr><td>Address</td><td>address</td><td>address:Suffolk County</td></tr>
              <tr><td>Committee</td><td>committees.name</td><td>committees.name:ALCOHOLISM AND DRUG ABUSE</td></tr>
              <tr><td>Text</td><td>text</td><td>text:property tax</td></tr>
              </tbody>
            </table>
          </md-card>
        </md-tab>
      </md-tabs>
    </md-tab>
  </md-tabs>

</section>