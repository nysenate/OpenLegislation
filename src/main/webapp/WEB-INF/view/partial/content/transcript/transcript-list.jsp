<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="content-section" ng-controller="TranscriptCtrl">
  <div ng-controller="TranscriptSearchCtrl">
    <div class="gray2-bg" layout-padding>
      <%-- Main Search Controls --%>
      <form name="law-search-form">
        <div layout="column">
          <label class="margin-bottom-10">
            Search for Transcripts
          </label>
          <input tabindex="1" class="padding-10" style="font-size:1.4em;" name="quick-term"
                 placeholder='e.g. "a phrase" or keywords' ng-model="transcriptSearch.term" ng-model-options="{debounce: 400}"
                 ng-change="search(true)">
        </div>
        <div class="margin-top-10" layout="row" layout-align="start center">
          <div flex="33" class="margin-right-20" layout="row" layout-align="start center">
            <label class="text-medium margin-right-20">Type</label>
            <md-radio-group layout="row" layout-xs="column" ng-model="transcriptSearch.type" ng-change="search(true)">
              <md-radio-button value="session" class="md-primary md-hue-2">
                <span class="text-medium">Session</span>
              </md-radio-button>
              <md-radio-button value="hearing" class="md-primary md-hue-2">
                <span class="text-medium">Public Hearing</span>
              </md-radio-button>
            </md-radio-group>
          </div>
          <div flex layout="row" layout-align="start center">
            <label class="text-medium margin-right-20">Published Year</label>
            <select ng-model="transcriptSearch.year" ng-options="year as year for year in years" ng-change="search(true)">
              <option value="">Any</option>
            </select>
          </div>
        </div>
      </form>
    </div>

    <md-progress-linear class="md-accent md-hue-1" md-mode="{{(transcriptSearch.state === 'searching') ? 'query' : ''}}">
    </md-progress-linear>
    <div ng-show="transcriptSearch.state === 'searched'" class="padding-20">
      <div ng-if="transcriptSearch.paginate.totalItems === 0">
        <div layout="row" layout-sm="column" layout-align="space-between center">
          No transcript results found.
        </div>
      </div>
      <div ng-if="transcriptSearch.paginate.totalItems > 0" class="content-card">
        <div layout="row" layout-sm="column" layout-align="space-between center">
          <div flex="none" class="margin-right-10"><strong>{{transcriptSearch.paginate.totalItems}}</strong> transcripts were matched.</div>
          <div flex style="text-align: right;">
            <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"
                                     pagination-id="tx-paginate"></dir-pagination-controls>
          </div>
        </div>
        <div class="padding-20">
          <a dir-paginate="tx in transcriptSearch.matches | itemsPerPage: transcriptSearch.paginate.itemsPerPage"
             total-items="transcriptSearch.paginate.totalItems"
             current-page="transcriptSearch.paginate.currPage"
             pagination-id="tx-paginate"
             ng-href="${ctxPath}/transcripts/{{transcriptSearch.type}}/{{tx.result.filename}}/"
             class="result-link transcript-result-link">
            <div ng-if="transcriptSearch.type == 'session'">
              <h4>
                <span class="blue3">{{tx.result.dateTime | moment:'LLL'}}</span> -
                <span class="text-normal">{{tx.result.sessionType}}</span>
              </h4>
            </div>
            <div ng-if="transcriptSearch.type == 'hearing'">
              <h4>
                <span class="blue3">{{tx.result.date | moment:'LL'}}</span> -
                <span class="text-normal">{{tx.result.filename}}</span>
              </h4>
            </div>
            <pre class="tx-result-highlight" ng-if="tx.highlights.hasFields">
              <span ng-repeat="field in tx.highlights">
                <span ng-repeat="fragment in field">
                  <span ng-bind-html="fragment"></span>
                </span>
              </span>
            </pre>
          </a>
        </div>
      </div>
    </div>
  </div>
      <%-- Public Hearing Browse --%>

      <%--<md-card class="content-card" ng-if="checkbox == 2 && hearingResponse.success && !hearingResponse.total">--%>
        <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
          <%--No results found.--%>
        <%--</div>--%>
      <%--</md-card>--%>
      <%--<md-card ng-if="checkbox == 2 && hearingResponse.total" class="content-card">--%>
        <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
          <%--<div flex>{{hearingResponse.total}} public hearings were matched. Viewing page {{hearing.paginate.currPage}} of {{hearing.paginate.lastPage}}</div>--%>
          <%--<div flex style="text-align: right;">--%>
            <%--<dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"--%>
                                     <%--pagination-id="hearing2"></dir-pagination-controls>--%>
          <%--</div>--%>
        <%--</div>--%>
      <%--</md-card>--%>
      <%--<md-card ng-if="checkbox == 2" class="content-card">--%>
        <%--<md-content>--%>
          <%--<md-list>--%>
            <%--<a dir-paginate="hearing in hearing.results | itemsPerPage: hearing.paginate.itemsPerPage"--%>
               <%--total-items="hearing.paginate.totalItems"--%>
               <%--current-page="hearing.paginate.currPage"--%>
               <%--pagination-id="hearing2"--%>
               <%--ng-click="go('${ctxPath}/transcripts/hearing/' + hearing.filename)"--%>
               <%--class="result-link transcript-result-link">--%>
              <%--<md-list-item class="md-2-line">--%>
                <%--<div class="md-list-item-text">--%>
<%----%>
                  <%--<h5>--%>
<%----%>
                  <%--</h5>--%>
                <%--</div>--%>
              <%--</md-list-item>--%>
              <%--<md-divider></md-divider>--%>
            <%--</a>--%>
          <%--</md-list>--%>
        <%--</md-content>--%>
      <%--</md-card>--%>
    <%--</div>--%>
  <%--</div>--%>
<%----%>



    <%----- Search Tab -----%>

    <%--<md-tab>--%>
      <%--<md-tab-label>--%>
        <%--<i class="icon-magnifying-glass prefix-icon2"></i>--%>
        <%--Search--%>
      <%--</md-tab-label>--%>
      <%--<md-tab-body>--%>
        <%--<md-divider></md-divider>--%>
        <%--<section class="margin-top-10" ng-if="view == 1" ng-controller="TranscriptSearchCtrl">--%>
          <%--<md-tabs md-selected="currentPage.categoryIndex" md-no-bar>--%>

            <%--&lt;%&ndash;--- Session Transcript Search ---&ndash;%&gt;--%>
            <%--<md-tab label="Session Transcripts">--%>
              <%--<md-tab-body>--%>
                <%--<md-card class="content-card">--%>
                  <%--<md-content class="md-padding">--%>
                    <%--<form name="searchForm">--%>
                      <%--<md-input-container class="md-primary">--%>
                        <%--<label for="sessionSearch">--%>
                          <%--<i class="prefix-icon2 icon-magnifying-glass"></i>--%>
                          <%--Search Session Transcripts--%>
                        <%--</label>--%>
                        <%--<input ng-model="transcriptSearch.term" ng-model-options="{debounce: 300}" ng-change="searchTranscripts(true)" id="sessionSearch">--%>
                      <%--</md-input-container>--%>
                    <%--</form>--%>
                  <%--</md-content>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="transcriptSearch.term && !transcriptSearch.paginate.totalItems" class="content-card">--%>
                  <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
                    <%--<div flex>--%>
                      <%--No Results Found.--%>
                    <%--</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="transcriptSearch.term && transcriptSearch.paginate.totalItems" class="content-card">--%>
                  <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
                    <%--<div flex>{{transcriptSearch.response.total}} Transcripts were found. Viewing page--%>
                      <%--{{transcriptSearch.paginate.currPage}} of {{transcriptSearch.paginate.lastPage}}</div>--%>
                    <%--<div flex style="text-align: right;">--%>
                      <%--<dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="session1"></dir-pagination-controls>--%>
                    <%--</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="transcriptSearch.paginate.totalItems" class="content-card">--%>
                  <%--<md-content>--%>
                    <%--<md-list>--%>
                      <%--<a dir-paginate="match in transcriptSearch.matches | itemsPerPage: transcriptSearch.paginate.itemsPerPage"--%>
                         <%--total-items="transcriptSearch.paginate.totalItems"--%>
                         <%--current-page="transcriptSearch.paginate.currPage"--%>
                         <%--pagination-id="session1"--%>
                         <%--ng-href=${ctxPath}/transcripts/session/{{match.result.filename}}--%>
                         <%--class="result-link transcript-result-link"--%>
                         <%--target="_blank">--%>
                        <%--<md-list-item class="md-3-line">--%>
                          <%--<div class="md-list-item-text">--%>
                            <%--<h4>--%>
                              <%--{{match.result.dateTime | date:'mediumDate'}}--%>
                            <%--</h4>--%>
                            <%--<p>--%>
                              <%--<span ng-repeat="field in match.highlights">--%>
                                <%--<span ng-repeat="fragment in field">--%>
                                  <%--<span ng-bind-html="fragment"></span>--%>
                                  <%--<br/>--%>
                                <%--</span>--%>
                              <%--</span>--%>
                            <%--</p>--%>
                          <%--</div>--%>
                        <%--</md-list-item>--%>
                        <%--<md-divider></md-divider>--%>
                      <%--</a>--%>
                    <%--</md-list>--%>
                  <%--</md-content>--%>
                  <%--<div class="subheader" layout="row" layout-align="end center">--%>
                    <%--<div flex style="text-align: right;">--%>
                      <%--<dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="session1"></dir-pagination-controls>--%>
                    <%--</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card class="content-card">--%>
                  <%--<md-subheader><strong>Session Transcript Search Guide</strong></md-subheader>--%>
                  <%--<div class="padding-20">--%>
                    <%--<p class="text-medium">--%>
                      <%--You can combine the field definitions documented below to perform targeted searches.--%>
                      <%--You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>--%>
                      <%--as well as parenthesis for grouping. For more information refer to the--%>
                      <%--<a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.--%>
                    <%--</p>--%>
                  <%--</div>--%>
                  <%--<table class="docs-table">--%>
                    <%--<thead>--%>
                    <%--<tr><th>To Search for</th><th>Use the field</th><th>Example</th></tr>--%>
                    <%--</thead>--%>
                    <%--<tbody>--%>
                    <%--<tr><td>Filename</td><td>filename</td><td>filename:031297.v1</td></tr>--%>
                    <%--<tr><td>Date and Time</td><td>dateTime</td><td>dateTime:"2014-03-03T15:44"</td></tr>--%>
                    <%--<tr><td>Session Type</td><td>sessionType</td><td>sessionType:extraordinary session</td></tr>--%>
                    <%--<tr><td>Text</td><td>text</td><td>text:property tax</td></tr>--%>
                    <%--</tbody>--%>
                  <%--</table>--%>
                <%--</md-card>--%>
              <%--</md-tab-body>--%>
            <%--</md-tab>--%>

            <%--&lt;%&ndash;--- Public Hearing Search ---&ndash;%&gt;--%>
            <%--<md-tab label="Public Hearing Transcripts">--%>
              <%--<md-tab-body>--%>
                <%--<md-card class="content-card">--%>
                  <%--<md-content class="md-padding">--%>
                    <%--<form name="searchForm"> &lt;%&ndash;  &ndash;%&gt;--%>
                      <%--<md-input-container class="md-primary">--%>
                        <%--<label for="hearingSearch">--%>
                          <%--<i class="prefix-icon2 icon-magnifying-glass"></i>--%>
                          <%--Search Public Hearing Transcripts--%>
                        <%--</label>--%>
                        <%--<input ng-model="hearingSearch.term" ng-model-options="{debounce: 300}" ng-change="searchHearings()" id="hearingSearch">--%>
                      <%--</md-input-container>--%>
                    <%--</form>--%>
                  <%--</md-content>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="hearingSearch.term && !hearingSearch.paginate.totalItems" class="content-card">--%>
                  <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
                    <%--<div flex>No matches were found.</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="hearingSearch.term && hearingSearch.paginate.totalItems" class="content-card">--%>
                  <%--<div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">--%>
                    <%--<div flex>{{hearingSearch.response.total}} matches were found. Viewing page--%>
                      <%--{{currentPage.hearingSearchPage}} of {{hearingSearch.paginate.lastPage}}.</div>--%>
                    <%--<div flex style="text-align: right;">--%>
                      <%--<dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="hearing1"></dir-pagination-controls>--%>
                    <%--</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card ng-if="hearingSearch.paginate.totalItems" class="content-card">--%>
                  <%--<md-content>--%>
                    <%--<md-list>--%>
                      <%--<a dir-paginate="match in hearingSearch.matches | itemsPerPage: hearingSearch.paginate.itemsPerPage"--%>
                         <%--total-items="hearingSearch.paginate.totalItems"--%>
                         <%--current-page="hearingSearch.paginate.currPage"--%>
                         <%--pagination-id="hearing1"--%>
                         <%--ng-href="${ctxPath}/transcripts/hearing/{{match.result.filename}}"--%>
                         <%--class="result-link transcript-result-link"--%>
                         <%--target="_blank">--%>
                        <%--<md-list-item class="md-3-line">--%>
                          <%--<div class="transcript-result-left">--%>
                            <%--<h4>--%>
                              <%--{{match.result.date | date:'mediumDate'}}--%>
                            <%--</h4>--%>
                            <%--<p>--%>
                              <%--<span ng-repeat="field in match.highlights">--%>
                                <%--<span ng-repeat="fragment in field">--%>
                                  <%--<span ng-bind-html="fragment"></span>--%>
                                  <%--<br/>--%>
                                <%--</span>--%>
                              <%--</span>--%>
                            <%--</p>--%>
                          <%--</div>--%>
                        <%--</md-list-item>--%>
                        <%--<md-divider></md-divider>--%>
                      <%--</a>--%>
                    <%--</md-list>--%>
                  <%--</md-content>--%>
                  <%--<div class="subheader" layout="row" layout-align="end center">--%>
                    <%--<div flex style="text-align: right;">--%>
                      <%--<dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)" pagination-id="hearing1"></dir-pagination-controls>--%>
                    <%--</div>--%>
                  <%--</div>--%>
                <%--</md-card>--%>
                <%--<md-card class="content-card">--%>
                  <%--<md-subheader><strong>Public Hearing Search Guide</strong></md-subheader>--%>
                  <%--<div class="padding-20">--%>
                    <%--<p class="text-medium">--%>
                      <%--You can combine the field definitions documented below to perform targeted searches.--%>
                      <%--You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>--%>
                      <%--as well as parenthesis for grouping. For more information refer to the--%>
                      <%--<a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.--%>
                    <%--</p>--%>
                  <%--</div>--%>
                  <%--<table class="docs-table">--%>
                    <%--<thead>--%>
                    <%--<tr><th>To Search for</th><th>Use the field</th><th>Example</th></tr>--%>
                    <%--</thead>--%>
                    <%--<tbody>--%>
                    <%--<tr><td>Filename</td><td>filename</td><td>filename:"10-10-13 NYsenate_Fuschillo_MTA_FINAL.txt"</td></tr>--%>
                    <%--<tr><td>Date</td><td>date</td><td>date:2013-11-13</td></tr>--%>
                    <%--<tr><td>Address</td><td>address</td><td>address:Suffolk County</td></tr>--%>
                    <%--<tr><td>Committee</td><td>committees.name</td><td>committees.name:ALCOHOLISM AND DRUG ABUSE</td></tr>--%>
                    <%--<tr><td>Text</td><td>text</td><td>text:property tax</td></tr>--%>
                    <%--</tbody>--%>
                  <%--</table>--%>
                <%--</md-card>--%>
              <%--</md-tab-body>--%>
            <%--</md-tab>--%>
          <%--</md-tabs>--%>
        <%--</section>--%>
      <%--</md-tab-body>--%>
    <%--</md-tab>--%>
  <%--</md-tabs>--%>
<%--</div>--%>