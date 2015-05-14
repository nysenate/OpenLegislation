<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="open" tagdir="/WEB-INF/tags/component" %>


<%@ page import="gov.nysenate.openleg.model.bill.BillStatusType" %>
<!-- We set the statusTypes here to make it easy to create a select menu out of the status types. -->
<c:set var="statusTypes" value="<%=BillStatusType.values()%>"/>

<section ng-controller="BillCtrl">
  <section class="content-section">
    <md-tabs md-selected="selectedView" md-dynamic-height="false" md-stretch-tabs="auto">
      <md-tab>
        <md-tab-label><i class="icon-magnifying-glass prefix-icon2"></i>Search</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 0" ng-controller="BillSearchCtrl" class="margin-top-10">
            <form name="bill-search-form">
              <md-content class="relative padding-20">
                <md-input-container class="md-primary">
                  <label><i class="prefix-icon2 icon-magnifying-glass"></i>Search for legislation</label>
                  <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                         ng-model="billSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
                </md-input-container>
                <div ng-if="curr.searching" class="loading-pulse">
                  Searching bills...
                </div>
              </md-content>
              <md-divider></md-divider>
              <md-subheader ng-show="billSearch.searched && billSearch.term && !billSearch.error && curr.pagination.totalItems === 0
                                     && billSearch.refine.isRefined === false"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>No search results were found for '{{billSearch.term}}'</h4>
              </md-subheader>
              <md-subheader ng-show="billSearch.searched && billSearch.term && billSearch.error"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>{{billSearch.error.message}}</h4>
              </md-subheader>
            </form>
            <section ng-show="(billSearch.searched || curr.searching) && curr.pagination.totalItems > 0 || billSearch.refine.isRefined">
              <md-card class="content-card">
                <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
                  <div flex> {{curr.pagination.totalItems}} bills were found.
                    <span ng-if="curr.pagination.totalItems > 0">Viewing page {{curr.pagination.currPage}} of {{curr.pagination.lastPage}}.</span>
                  </div>
                  <div flex style="text-align: right;">
                    <dir-pagination-controls pagination-id="bill-search" boundary-links="true" max-size="5"></dir-pagination-controls>
                  </div>
                </div>
                <md-content layout="row" style="padding:0;" class="no-top-margin">
                  <div class="search-refine-panel" hide-sm>
                    <h3>Refine your search</h3>
                    <md-divider></md-divider>
                    <div class="refine-controls">
                      <label for="refine_sort_by">Sort By</label>
                      <select id="refine_sort_by" ng-model="billSearch.refine.sort">
                        <option value="_score:desc,session:desc">Relevance</option>
                        <option value="status.actionDate:desc">Recent Status Update</option>
                        <option value="milestones.size:desc">Milestone Count</option>
                        <option value="votes.items.memberVotes.items.NAY.size:desc">Most Nay Votes (Voted Bills only)</option>
                      </select>
                      <hr/>
                      <label for="refine_session">Session</label>
                      <select id="refine_session" ng-model="billSearch.refine.session">
                        <option value="">All Sessions</option>
                        <option value="2015">2015</option>
                        <option value="2013">2013</option>
                        <option value="2011">2011</option>
                        <option value="2009">2009</option>
                      </select>
                      <label for="refine_chamber">Chamber</label>
                      <select id="refine_chamber" ng-model="billSearch.refine.chamber">
                        <option value="">Any</option><option value="SENATE">Senate</option><option value="ASSEMBLY">Assembly</option>
                      </select>
                      <label for="refine_type">Type</label>
                      <select id="refine_type" ng-model="billSearch.refine.type">
                        <option value="">Any</option>
                        <option value="bills">Bills</option>
                        <option value="resolutions">Resolution</option>
                      </select>
                      <label for="refine_sponsor">Primary Sponsor (2015-2016)</label>
                      <select id="refine_sponsor" ng-model="billSearch.refine.sponsor">
                        <open:member-select-menu showSenators="true" showAssembly="true"/>
                      </select>
                      <label for="refine_status">Current Status</label>
                      <select id="refine_status" ng-model="billSearch.refine.status">
                        <option value="">Any</option>
                        <c:forEach items="${statusTypes}" var="status">
                          <option value="${status.name()}">${status.desc}</option>
                        </c:forEach>
                      </select>
                      <md-checkbox ng-model="billSearch.refine.hasVotes" class="md-hue-3">Voted on at least once</md-checkbox>
                      <md-checkbox ng-model="billSearch.refine.isSigned" class="md-hue-3">Is Signed / Adopted</md-checkbox>
                      <md-checkbox ng-model="billSearch.refine.isGovProg" class="md-hue-3">Governor's Bill</md-checkbox>
                      <md-button ng-click="resetRefine()" class="md-primary margin-top-10">Reset Filters</md-button>
                    </div>
                  </div>
                  <div class="padding-20" ng-if="billSearch.refine.isRefined === true && billSearch.response.total === 0">
                    <p class="red1 text-medium bold">No results were found after applying your filters.</p>
                  </div>
                  <div flex class="padding-20">
                    <md-list>
                      <a class="result-link"
                         dir-paginate="r in billSearch.results | itemsPerPage: 6"
                         total-items="billSearch.response.total" current-page="curr.pagination.currPage"
                         ng-init="bill = r.result; highlights = r.highlights;" pagination-id="bill-search"
                         ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearch.term}}&searchPage={{curr.pagination.currPage}}">
                        <md-list-item class="md-3-line" style="cursor: pointer;">
                          <div class="md-list-item-text">
                            <h3>
                              <span class="blue3 no-margin bold" ng-if="!highlights.basePrintNo">{{bill.basePrintNo}}</span>
                              <span class="blue3 bold" ng-if="highlights.basePrintNo" ng-bind-html="highlights.basePrintNo[0]"></span>
                              <span class="blue3 bold"> - {{bill.session}}</span>
                              <span class="margin-left-20">{{bill.sponsor.member.fullName}}</span>
                            </h3>
                            <hr/>
                            <p class="text-medium" ng-if="!highlights.title">{{bill.title}}</p>
                            <p class="text-medium" ng-if="highlights.title" ng-bind-html="highlights.title[0]"></p>
                            <p style="color: #43ac6a" class="text-small no-margin capitalize">
                              {{bill.status.actionDate | moment:'ll'}} - {{getStatusDesc(bill.status) | lowercase}}
                            </p>
                          </div>
                        </md-list-item>
                      </a>
                    </md-list>
                  </div>
                </md-content>
                <div class="subheader" layout="row" layout-align="end center">
                  <div flex style="text-align: right;">
                    <dir-pagination-controls pagination-id="bill-search" max-size="5" boundary-links="true"></dir-pagination-controls>
                  </div>
                </div>
              </md-card>
            </section>
            <section>
              <toggle-panel label="Quick Search Tips" open="true" extra-classes="content-card">
                <div class="padding-20">
                  <p class="text-medium">Each bill and resolution has a print number and session year. If you are looking for a specific
                    piece of legislation, you can simply enter it's print number in the search box, e.g. <code>S1234-2013</code>.
                  </p>
                  <p class="text-medium">If you would like to search for legislation where a certain term or phrase appears, you
                    can enter the term in the search box, e.g.&nbsp;<code>public schools</code>. If you want to match a specific phrase
                    you will need to enter it in quotes, e.g.&nbsp;<code>"Start UP NY"</code>. For more advanced queries see below.
                  </p>
                </div>
              </toggle-panel>
              <toggle-panel label="Advanced Search Guide" open="false" extra-classes="content-card">
                <div class="padding-20">
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
            </section>
          </section>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-show="selectedView === 1" ng-controller="BillUpdatesCtrl">
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
                  <input class="margin-left-10" ng-model="curr.fromDate" type="datetime-local">
                </div>
                <div flex>
                  <label>To</label>
                  <input class="margin-left-10" ng-model="curr.toDate" type="datetime-local">
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
                     ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearch.term}}&view=1&searchPage={{curr.pagination.currPage}}">
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
  </section>
</section>