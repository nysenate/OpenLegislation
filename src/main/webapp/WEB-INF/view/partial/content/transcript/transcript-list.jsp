<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="TranscriptCtrl">

  <%----- Browsing Tabs -----%>

  <md-tabs md-selected="view" md-dynamic-height="false">
    <md-tab>
      <md-tab-label>
        <i class="icon-archive prefix-icon2"></i>
        Browse
      </md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <section class="margin-top-20" ng-if="view === 0" ng-controller="TranscriptBrowseCtrl">
          <md-toolbar class="md-toolbar-tools md-hue-2">
          <div layout="row" layout-sm="column">
            <div layout="row" layout-align="center center">
              <md-checkbox ng-model="checkbox"
                           ng-true-value="1"
                           ng-change="filterResults()"
                           class="md-accent md-hue-1">
                <span class="text-medium">Session</span>
              </md-checkbox>
              <md-checkbox ng-model="checkbox"
                           ng-true-value="2"
                           ng-change="filterResults()"
                           class="md-accent md-hue-1">
                <span class="text-medium">Hearing</span>
              </md-checkbox>
            </div>
            <div>
              <span class="text-medium margin-left-20">Year</span>
              <md-select ng-model="selectedYear" style="padding-left: 12px;">
                <md-option ng-value="year" ng-repeat="year in years">{{year}}</md-option>
              </md-select>
            </div>
          </div>
          </md-toolbar>

          <%-- Floor Session Browse --%>

          <md-card class="content-card" ng-if="checkbox == 1 && sessionResponse.success && !sessionResponse.total">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              No results found.
            </div>
          </md-card>
          <md-card ng-if="checkbox == 1 && sessionResponse.total" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{sessionResponse.total}} session's were matched. Viewing page {{session.paginate.currPage}} of {{session.paginate.lastPage}}</div>
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"
                                         pagination-id="session2"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card ng-if="checkbox == 1" class="content-card">
            <md-content>
              <md-list>
                <a dir-paginate="session in session.results | itemsPerPage: session.paginate.itemsPerPage"
                   total-items="session.paginate.totalItems"
                   current-page="session.paginate.currPage"
                   pagination-id="session2"
                   ng-click="go('${ctxPath}/transcripts/session/' + session.filename)"
                   class="result-link transcript-result-link">
                  <md-list-item class="md-2-line">
                    <div class="md-list-item-text">
                      <h4>
                        {{session.dateTime | date:'mediumDate'}}
                      </h4>
                      <h5>
                        {{session.sessionType}}
                      </h5>
                    </div>
                  </md-list-item>
                  <md-divider></md-divider>
                </a>
              </md-list>
            </md-content>
          </md-card>

          <%-- Public Hearing Browse --%>

          <md-card class="content-card" ng-if="checkbox == 2 && hearingResponse.success && !hearingResponse.total">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              No results found.
            </div>
          </md-card>
          <md-card ng-if="checkbox == 2 && hearingResponse.total" class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{hearingResponse.total}} public hearings were matched. Viewing page {{hearing.paginate.currPage}} of {{hearing.paginate.lastPage}}</div>
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"
                                         pagination-id="hearing2"></dir-pagination-controls>
              </div>
            </div>
          </md-card>
          <md-card ng-if="checkbox == 2" class="content-card">
            <md-content>
              <md-list>
                <a dir-paginate="hearing in hearing.results | itemsPerPage: hearing.paginate.itemsPerPage"
                   total-items="hearing.paginate.totalItems"
                   current-page="hearing.paginate.currPage"
                   pagination-id="hearing2"
                   ng-click="go('${ctxPath}/transcripts/hearing/' + hearing.filename)"
                   class="result-link transcript-result-link">
                  <md-list-item class="md-2-line">
                    <div class="md-list-item-text">
                      <h4>
                        {{hearing.date | date:'mediumDate'}}
                      </h4>
                      <h5>
                        {{hearing.filename}}
                      </h5>
                    </div>
                  </md-list-item>
                  <md-divider></md-divider>
                </a>
              </md-list>
            </md-content>
          </md-card>
        </section>
      </md-tab-body>
    </md-tab>

    <%----- Search Tab -----%>

    <md-tab>
      <md-tab-label>
        <i class="icon-magnifying-glass prefix-icon2"></i>
        Search
      </md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <section class="margin-top-10" ng-if="view == 1" ng-controller="TranscriptSearchCtrl">
          <md-tabs md-selected="currentPage.categoryIndex" md-no-bar>

            <%----- Session Transcript Search -----%>
            <md-tab label="Session Transcripts">
              <md-tab-body>
                <md-card class="content-card">
                  <md-content class="md-padding">
                    <form name="searchForm">
                      <md-input-container class="md-primary">
                        <label for="sessionSearch">
                          <i class="prefix-icon2 icon-magnifying-glass"></i>
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
                        <md-list-item class="md-3-line">
                          <div class="md-list-item-text">
                            <h4>
                              {{match.result.dateTime | date:'mediumDate'}}
                            </h4>
                            <p>
                              <span ng-repeat="field in match.highlights">
                                <span ng-repeat="fragment in field">
                                  <span ng-bind-html="fragment"></span>
                                  <br/>
                                </span>
                              </span>
                            </p>
                          </div>
                        </md-list-item>
                        <md-divider></md-divider>
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
                  <md-subheader><strong>Session Transcript Search Guide</strong></md-subheader>
                  <div class="padding-20">
                    <p class="text-medium">
                      You can combine the field definitions documented below to perform targeted searches.
                      You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                      as well as parenthesis for grouping. For more information refer to the
                      <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.
                    </p>
                  </div>
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
              </md-tab-body>
            </md-tab>

            <%----- Public Hearing Search -----%>
            <md-tab label="Public Hearing Transcripts">
              <md-tab-body>
                <md-card class="content-card">
                  <md-content class="md-padding">
                    <form name="searchForm"> <%--  --%>
                      <md-input-container class="md-primary">
                        <label for="hearingSearch">
                          <i class="prefix-icon2 icon-magnifying-glass"></i>
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
                        <md-list-item class="md-3-line">
                          <div class="transcript-result-left">
                            <h4>
                              {{match.result.date | date:'mediumDate'}}
                            </h4>
                            <p>
                              <span ng-repeat="field in match.highlights">
                                <span ng-repeat="fragment in field">
                                  <span ng-bind-html="fragment"></span>
                                  <br/>
                                </span>
                              </span>
                            </p>
                          </div>
                        </md-list-item>
                        <md-divider></md-divider>
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
                  <md-subheader><strong>Public Hearing Search Guide</strong></md-subheader>
                  <div class="padding-20">
                    <p class="text-medium">
                      You can combine the field definitions documented below to perform targeted searches.
                      You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                      as well as parenthesis for grouping. For more information refer to the
                      <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.
                    </p>
                  </div>
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
              </md-tab-body>
            </md-tab>
          </md-tabs>
        </section>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</section>