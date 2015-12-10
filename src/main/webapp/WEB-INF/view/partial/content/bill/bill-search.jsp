<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="open" tagdir="/WEB-INF/tags/component" %>

<div ng-controller="BillCtrl">
  <div class="content-section">
    <md-tabs class="md-hue-2" md-selected="selectedView" md-dynamic-height="true" md-stretch-tabs="auto">
      <md-tab>
        <md-tab-label><i class="icon-magnifying-glass prefix-icon2"></i>Search</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <div ng-controller="BillSearchCtrl">
            <div class="gray2-bg" layout-padding>
              <%-- Main Search Controls --%>
              <form name="bill-search-form">
                <div class="relative" layout="column">
                  <label class="margin-bottom-10">
                    Search for legislation by print no or term.
                  </label>
                  <input tabindex="1" class="padding-10" style="font-size:1.4em;" name="quick-term"
                         placeholder="e.g. S1234-2015 or yogurt" ng-model="curr.billSearch.term" ng-model-options="{debounce: 400}"
                         ng-change="simpleSearch(true)">
                </div>
              </form>
              <%-- Refine Panel --%>
              <div>
                <toggle-panel label="Advanced search options {{(curr.billSearch.isRefined) ? '(Filtered)' : ''}}"
                              open="{{curr.billSearch.isRefined}}" extra-classes="content-card no-margin">
                  <bill-refine-search-panel search-params="curr.billSearch.refine" on-change="onRefineUpdate">
                  </bill-refine-search-panel>
                  <div ng-show="curr.billSearch.isRefined">
                    <md-divider></md-divider>
                    <md-button ng-click="resetRefine()" class="md-accent margin-top-10">
                      Reset Filters
                    </md-button>
                  </div>
                </toggle-panel>
              </div>
            </div>
            <md-progress-linear class="md-accent md-hue-1" md-mode="{{(curr.state === 'searching') ? 'query' : ''}}"></md-progress-linear>
            <div layout-padding>
              <md-subheader ng-show="curr.state === 'searched' && curr.pagination.totalItems === 0"
                            class="margin-10 md-warn">
                <h3>No search results were found
                  <span ng-show="curr.billSearch.isRefined"> with the advanced search filters you entered.</span>
                </h3>
                <h3 ng-show="curr.billSearch.error">{{curr.billSearch.error.message}}</h3>
              </md-subheader>

              <div ng-show="curr.pagination.totalItems > 0">
                <div class="subheader" layout="column" layout-gt-sm="row" layout-align="space-between center">
                  <div flex>
                    <h3>{{curr.pagination.totalItems}} <span class="text-normal">bills were found.</span></h3>
                  </div>
                  <div>
                    <label class="bold margin-right-10" for="sort-by-select">Sort By</label>
                    <select id="sort-by-select" ng-model="curr.billSearch.sort" ng-change="simpleSearch(true)">
                      <option value="_score:desc,session:desc">Relevant</option>
                      <option value="status.actionDate:desc,_score:desc">Recent Status Update</option>
                      <option value="milestones.size:desc,_score:desc">Most Progress</option>
                      <option value="votes.items.memberVotes.items.NAY.size:desc,_score:desc">Most Nay Votes</option>
                      <option value="amendments.size:desc,_score:desc">Most Amendments</option>
                    </select>
                  </div>
                </div>

                <md-card class="content-card">
                  <md-content layout="row" style="padding:0;" class="no-top-margin">
                    <div flex>
                      <bill-search-listing bill-search-response="curr.billSearch.response" bill-search-term="curr.billSearch.term"
                                           pagination="curr.pagination" show-title="true" show-img="true">
                      </bill-search-listing>
                    </div>
                  </md-content>
                </md-card>
              </div>

              <div>
                <toggle-panel label="Quick Query Search Tips" open="true" extra-classes="content-card">
                  <p class="text-medium">Each bill and resolution has a print number and session year. If you are looking for a specific
                    piece of legislation, you can simply enter it's print number in the search box, e.g. <code>S1234-2013</code>.
                  </p>
                  <p class="text-medium">If you would like to search for legislation where a certain term or phrase appears, you
                    can enter the term in the search box, e.g.&nbsp;<code>public schools</code>. If you want to match a specific phrase
                    you will need to enter it in quotes, e.g.&nbsp;<code>"Start UP NY"</code>. For more advanced queries see below.
                  </p>
                </toggle-panel>
                <toggle-panel label="Advanced Query Search Guide" open="false" extra-classes="content-card">
                  <div>
                    <p class="text-medium">You can combine the field definitions documented below to perform targeted searches.
                      You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                      as well as parenthesis for grouping. For more information refer to the
                      <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.</p>
                  </div>
                  <table class="docs-table">
                    <thead>
                    <tr><th>To search for</th><th>Use the field</th><th>With value type</th><th>Examples</th></tr>
                    </thead>
                    <tbody>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>Basic Details</strong></td></tr>
                    <tr><td>Original Print No</td><td>basePrintNo</td><td>text</td><td>basePrintNo:S1234</td></tr>
                    <tr><td>Session Year</td><td>session</td><td>number</td><td>session:2015</td></tr>
                    <tr><td>Title</td><td>title</td><td>text</td><td>title:moose elk</td></tr>
                    <tr><td>Chamber</td><td>billType.chamber</td><td>enum</td><td>billType.chamber:SENATE<br/>billType.chamber:ASSEMBLY</td></tr>
                    <tr><td>Is a Resolution</td><td>billType.resolution</td><td>boolean</td><td>billType.resolution:true</td></tr>
                    <tr><td>Active Amendment Version</td><td>activeVersion</td><td>text</td><td>activeVersion:A</td></tr>
                    <tr><td>Published Year</td><td>year</td><td>number</td><td>year:2014</td></tr>
                    <tr><td>Published Date/Time</td><td>publishedDateTime</td><td>date-time</td><td>publishedDateTime:2015-01-02<br/>publishedDateTime:[2015-01-02 TO 2015-01-04]</td></tr>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>Sponsor</strong></td></tr>
                    <tr><td>Summary</td><td>summary</td><td>text</td><td>summary:moose, elk, or deer</td></tr>
                    <tr><td>Sponsor Last Name</td><td>sponsor.member.shortName</td><td>text</td><td>sponsor.member.shortName:martins</td></tr>
                    <tr><td>Sponsor Full Name</td><td>sponsor.member.fullName</td><td>text</td><td>sponsor.member.fullName:jack</td></tr>
                    <tr><td>Is Budget Bill</td><td>sponsor.budget</td><td>boolean</td><td>sponsor.budget:true</td></tr>
                    <tr><td>Is Rules Sponsored</td><td>sponsor.rules</td><td>boolean</td><td>sponsor.rules:true</td></tr>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>Status</strong></td></tr>
                    <tr><td>Bill Signed Into Law</td><td>signed</td><td>boolean</td><td>signed:true</td></tr>
                    <tr><td>Resolution Adopted</td><td>adopted</td><td>boolean</td><td>adopted:true</td></tr>
                    <tr><td>Status</td><td>status.statusType</td><td>enum</td><td>status.statusType:"INTRODUCED"<br/>
                      status.statusType:"IN_SENATE_COMM"<br/>status.statusType:"IN_ASSEMBLY_COMM"<br/>status.statusType:"SENATE_FLOOR"<br/>
                      status.statusType:"ASSEMBLY_FLOOR"<br/>status.statusType:"PASSED_SENATE"<br/>status.statusType:"PASSED_ASSEMBLY"<br/>
                      status.statusType:"DELIVERED_TO_GOV"<br/>status.statusType:"SIGNED_BY_GOV"<br/>status.statusType:"VETOED"<br/>
                      status.statusType:"STRICKEN"<br/>
                    </td>

                    </tr>
                    <tr><td>Status Action Date</td><td>status.actionDate</td><td>date</td><td>status.actionDate:[2015-02-01 TO 2015-02-02]</td></tr>
                    <tr><td>Current Committee</td><td>status.committeeName</td><td>text</td><td>status.committeeName:Finance</td></tr>
                    <tr><td>Current Calendar No</td><td>status.billCalNo</td><td>number</td><td>status.billCalNo:123</td></tr>
                    <tr><td>Associated Program</td><td>programInfo.name</td><td>text</td><td>programInfo.name:Governor</td></tr>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>The fields below are associated with each amendment and are always prefixed with '\*.'</strong></td> </tr>
                    <tr><td>Sponsor's Memo</td><td>\*.memo</td><td>text</td><td>\*.memo:Yogurt</td></tr>
                    <tr><td>Full Text</td><td>\*.fullText</td><td>text</td><td>\*.fullText:(cats OR kittens OR puppies)</td></tr>
                    <tr><td>Law Section</td><td>\*.lawSection</td><td>text</td><td>\*.lawSection:"Agriculture and Markets Law"</td></tr>
                    <tr><td>Law Code</td><td>\*.lawCode</td><td>text</td><td>\*.lawCode:Amd?12</td></tr>
                    <tr><td>Enacting Clause</td><td>\*.actClause</td><td>text</td><td>\*.actClause:lemon</td></tr>
                    <tr><td>Is Uni Bill</td><td>\*.uniBill</td><td>boolean</td><td>\*.uniBill:true</td></tr>
                    <tr><td>Cosponsor Last Name</td><td>\*.coSponsors.\*.shortName</td><td>text</td><td>\*.coSponsors.\*.shortName:martins</td></tr>
                    <tr><td>Multi Sponsor Last Name</td><td>\*.multiSponsors.\*.shortName</td><td>text</td><td>\*.multiSponsors.\*.shortName:barron</td></tr>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>Vote Roll Data</strong></td> </tr>
                    <tr><td>Vote Count</td><td>votes.size</td><td>number</td><td>votes.size:>0</td></tr>
                    <tr><td>Vote Type</td><td>votes.\*.voteType</td><td>enum</td><td>votes.\*.voteType:COMMITTEE<br/>votes.\*.voteType:FLOOR</td></tr>
                    <tr><td colspan="4"><strong>There are 6 vote codes: AYE, NAY, AYEWR (Aye with reservations), ABS (Absent), EXC (Excused), ABD (Abstained)<br/>
                      Only AYE is shown in the examples below but you can use any of them.</strong></td></tr>
                    <tr><td>Ayes Count</td><td>votes.\*.AYE.size</td><td>number</td><td>votes.\*.AYE.size:>10</td></tr>
                    <tr><td>Member that voted Aye</td><td>votes.\*.AYE.\*.shortName</td><td>text</td><td>votes.\*.AYE.\*.shortName:Funke</td></tr>
                    <tr style="background:#f1f1f1;"><td colspan="4"><strong>Bill Actions</strong></td></tr>
                    <tr><td>Action Count</td><td>actions.size</td><td>number</td><td>actions.size:>10</td></tr>
                    <tr><td>Action Date</td><td>actions.\*.date</td><td>date</td><td>actions.\*.date:>2015-02-01</td></tr>
                    <tr><td>Action Text</td><td>actions.\*.text</td><td>text</td><td>actions.\*.text:"Signed Chap"</td></tr>
                    </tbody>
                  </table>
                </toggle-panel>
              </div>
            </div>
          </div>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-controller="BillUpdatesCtrl">
            <md-card class="content-card">
              <md-subheader>Show bill updates during the following date range</md-subheader>
              <div layout="row" layout-sm="column" class="padding-20 text-medium">
                <div flex>
                  <label>With </label>
                  <select class="margin-left-10" ng-model="curr.type">
                    <option value="processed">Processed Date</option>
                    <option value="published">Published Date</option>
                  </select>
                </div>
                <div flex>
                  <label>From</label>
                  <md-datepicker class="margin-left-10" ng-model="curr.fromDate" md-max-date="curr.toDate"></md-datepicker>
                </div>
                <div flex>
                  <label>To</label>
                  <md-datepicker class="margin-left-10" ng-model="curr.toDate" md-min-date="curr.fromDate"></md-datepicker>
                </div>
              </div>
              <md-divider></md-divider>
              <div layout="row" layout-sm="column"class="padding-20 text-medium">
                <div flex>
                  <label>Type </label>
                  <select class="margin-left-10" ng-model="curr.filter">
                    <option value="">All</option>
                    <option value="published_bill">Newly Published</option>
                    <option value="action">Action</option>
                    <option value="active_version">Active Version</option>
                    <option value="approval">Approval Memo</option>
                    <option value="cosponsor">Co Sponsor</option>
                    <option value="act_clause">Enacting Clause</option>
                    <option value="fulltext">Full Text</option>
                    <option value="law">Law</option>
                    <option value="memo">Memo</option>
                    <option value="multisponsor">Multi Sponsor</option>
                    <option value="sponsor">Sponsor</option>
                    <option value="status">Status</option>
                    <option value="summary">Summary</option>
                    <option value="title">Title</option>
                    <option value="veto">Veto</option>
                    <option value="vote">Vote</option>
                  </select>
                </div>
                <div flex>
                  <label>Sort </label>
                  <select class="margin-left-10" ng-model="curr.sortOrder">
                    <option value="desc" selected>Newest First</option>
                    <option value="asc">Oldest First</option>
                  </select>
                </div>
                <div flex>
                  <md-checkbox class="md-hue-3 no-margin" ng-model="curr.detail" aria-label="detail">Show Detail</md-checkbox>
                </div>
              </div>
            </md-card>
            <div ng-if="billUpdates.fetching" class="text-medium text-align-center">Fetching updates, please wait.</div>
            <md-card class="content-card" ng-if="billUpdates.response.success === true">
              <md-subheader>
                <div>
                  <strong>{{billUpdates.total}} </strong>
                  <span ng-if="!curr.detail">bills were updated </span>
                  <span ng-if="curr.detail"> granular bill updates were made </span>
                  between {{billUpdates.response.fromDateTime | moment:'llll'}} and {{curr.toDate | moment:'llll'}}
                </div>
              </md-subheader>
              <div class="subheader" ng-show="billUpdates.total > 0">
                <div flex style="text-align: right;">
                  <dir-pagination-controls pagination-id="bill-updates" max-size="5" boundary-links="true"></dir-pagination-controls>
                </div>
              </div>
              <section ng-if="billUpdates.total > 0">
                <md-list>
                  <a dir-paginate="billUpdate in billUpdates.result.items | itemsPerPage: 20"
                     total-items="billUpdates.total" current-page="pagination.currPage"
                     ng-init="bill = billUpdate.item" pagination-id="bill-updates"
                     class="result-link text-medium"
                     ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{curr.billSearch.term}}&view=1&searchPage={{curr.pagination.currPage}}">
                    <md-list-item class="md-3-line" style="cursor: pointer;">
                      <div class="md-list-item-text">
                        <h3>
                          <span class="blue3 bold">{{bill.basePrintNo}} - {{bill.session}}</span>
                          <span class="margin-left-20">{{bill.sponsor.member.fullName}}</span>
                        </h3>
                        <hr/>
                        <p class="text-medium" ng-if="!highlights.title">{{bill.title}}</p>

                        <h4 style="color:#444"><strong>Last Published:</strong> {{billUpdate.sourceDateTime | moment:'llll'}}</h4>
                        <h4 style="color:#444"><strong>Last Processed:</strong> {{billUpdate.processedDateTime | moment:'MMM D, YYYY h:mm:ss A'}}</h4>
                        <h4 style="color:#444"><strong>Update Source Id:</strong> {{billUpdate.sourceId}}</h4>
                        <div ng-if="curr.detail" class="margin-top-20">
                          <span class="text-medium bold green2">{{billUpdate.action}} {{billUpdate.scope}}</span>
                          <table class="bill-updates-table" style="width:100%;">
                            <thead>
                            <tr>
                              <th style="width:150px;">Field Name</th>
                              <th>Data</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="(field, value) in billUpdate.fields">
                              <td>{{field}}</td>
                              <td><pre>{{value}}</pre></td>
                            </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </md-list-item>
                  </a>
                </md-list>
              </section>
              <div class="subheader" ng-show="billUpdates.total > 0">
                <div flex style="text-align: right;">
                  <dir-pagination-controls pagination-id="bill-updates" max-size="5" boundary-links="true"></dir-pagination-controls>
                </div>
              </div>
            </md-card>
            <md-card class="content-card" ng-if="billUpdates.response.success === false">
              <md-subheader class="margin-10 md-warn">
                <h4>{{billUpdates.errMsg}}</h4>
              </md-subheader>
            </md-card>
          </section>
        </md-tab-body>
      </md-tab>
    </md-tabs>
  </div>
</div>