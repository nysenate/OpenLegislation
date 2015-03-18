<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="TranscriptListingCtrl">

  <%----- Browsing Tabs -----%>

  <md-tabs class="md-primary" md-selected="currentPage.viewIndex">
    <md-tab label="Browse">
      <md-divider></md-divider>
      <md-tabs class="md-primary" md-selected="currentPage.categoryIndex">
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
      <md-tabs md-selected="currentPage.categoryIndex">

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
                  <input ng-model="transcriptSearch.term" ng-model-options="{debounce: 300}" ng-change="searchTranscripts(true)" id="sessionSearch">
                </md-input-container>
              </form>
            </md-content>
          </md-card>
          <md-card ng-if="transcriptSearch.term && !transcriptSearch.paginate.totalItems" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>
                No Results Found.
              </div>
            </div>
          </md-card>
          <md-card ng-if="transcriptSearch.term && transcriptSearch.paginate.totalItems" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{transcriptSearch.response.total}} Transcripts were found. Viewing page
                {{transcriptSearch.paginate.currPage}} of {{transcriptSearch.paginate.lastPage}}</div>
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="session1"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card ng-if="transcriptSearch.paginate.totalItems" class="content-card">
            <md-content>
              <md-list>
                <a dir-paginate="match in transcriptSearch.matches | itemsPerPage: transcriptSearch.paginate.itemsPerPage"
                   total-items="transcriptSearch.paginate.totalItems"
                   current-page="transcriptSearch.paginate.currPage"
                   pagination-id="session1"
                   ng-href=${ctxPath}/transcripts/session/{{match.result.filename}}
                   class="result-link transcript-result-link"
                   target="_blank">
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
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="session1"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card class="content-card">
            <md-subheader><strong>Advanced Search Guide</strong></md-subheader>
            <div class="padding-20">
              <p class="text-medium">
                You can combine the field definitions documented below to perform targeted searches.
                You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                as well as parenthesis for grouping. For more information refer to the
                <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.
              </p>
            </div>
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
          <md-card ng-if="hearingSearch.term && !hearingSearch.paginate.totalItems" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>No matches were found.</div>
            </div>
          </md-card>
          <md-card ng-if="hearingSearch.term && hearingSearch.paginate.totalItems" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{hearingSearch.response.total}} matches were found. Viewing page
                {{currentPage.hearingSearchPage}} of {{hearingSearch.paginate.lastPage}}.</div>
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="hearing1"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card ng-if="hearingSearch.paginate.totalItems" class="content-card">
            <md-content>
              <md-list>
                <a dir-paginate="match in hearingSearch.matches | itemsPerPage: hearingSearch.paginate.itemsPerPage"
                   total-items="hearingSearch.paginate.totalItems"
                   current-page="hearingSearch.paginate.currPage"
                   pagination-id="hearing1"
                   ng-href="${ctxPath}/transcripts/hearing/{{match.result.filename}}"
                   class="result-link transcript-result-link"
                   target="_blank">
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
            <div class="subheader" layout="row" layout-align="end center">
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="hearing1"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card class="content-card">
            <md-subheader><strong>Advanced Search Guide</strong></md-subheader>
            <div class="padding-20">
              <p class="text-medium">
                You can combine the field definitions documented below to perform targeted searches.
                You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                as well as parenthesis for grouping. For more information refer to the
                <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.
              </p>
            </div>
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