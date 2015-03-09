<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillSearchCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-primary" md-stretch-tabs="auto">
      <md-tab>
        <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
        <md-divider></md-divider>
        <section class="margin-top-10">
          <form name="bill-search-form">
            <md-content class="padding-20">
              <md-input-container class="md-primary">
                <label><i class="prefix-icon2 icon-search"></i>Search for legislation</label>
                <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                       ng-model="billSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
              </md-input-container>
            </md-content>
            <md-divider></md-divider>
            <md-subheader ng-show="billSearch.searched && billSearch.term && !billSearch.error && curr.pagination.totalItems === 0"
                          class="margin-10 md-warn md-whiteframe-z0">
              <h4>No search results were found for '{{billSearch.term}}'</h4>
            </md-subheader>
            <md-subheader ng-show="billSearch.searched && billSearch.term && billSearch.error"
                          class="margin-10 md-warn md-whiteframe-z0">
              <h4>{{billSearch.error.message}}</h4>
            </md-subheader>
          </form>
          <section ng-show="(billSearch.searched || curr.searching) && curr.pagination.totalItems > 0">
            <md-card class="content-card">
              <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
                <div flex> {{curr.pagination.totalItems}} bills were matched. Viewing page {{curr.pagination.currPage}} of {{curr.pagination.lastPage}}.  </div>
                <div flex style="text-align: right;"><dir-pagination-controls boundary-links="true"></dir-pagination-controls></div>
              </div>
              <md-content class="no-top-margin">
                <md-list>
                  <a class="result-link"
                     dir-paginate="r in billSearch.results | itemsPerPage: 20"
                     total-items="billSearch.response.total" current-page="curr.pagination.currPage"
                     ng-init="bill = r.result; highlights = r.highlights;"
                     ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearch.term}}&view=1&searchPage={{curr.pagination.currPage}}">
                    <md-item>
                      <md-item-content layout-sm="column" layout-align-sm="center start" style="cursor: pointer;">
                        <div style="width:180px;padding:16px;">
                          <h3 class="no-margin">
                            <span ng-if="!highlights.basePrintNo">{{bill.basePrintNo}}</span>
                            <span ng-if="highlights.basePrintNo" ng-bind-html="highlights.basePrintNo[0]"></span>
                            - {{bill.session}}
                          </h3>
                          <h5 class="no-margin">{{bill.sponsor.member.fullName}}</h5>
                        </div>
                        <div flex class="md-tile-content">
                          <h4>
                            <span ng-if="!highlights.title">{{bill.title}}</span>
                            <span ng-if="highlights.title" ng-bind-html="highlights.title[0]"></span>
                          </h4>
                          <h6 class="gray7 no-margin capitalize">{{getStatusDesc(bill.status) | lowercase}}</h6>
                        </div>
                      </md-item-content>
                      <md-divider ng-if="!$last"/>
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
          </section>
          <section>
            <md-card class="content-card">
              <md-subheader><strong>Quick search for Legislation</strong></md-subheader>
              <div class="padding-20">
                <p class="text-medium">Each bill and resolution has a print number and session year. If you are looking for a specific
                  piece of legislation, you can simply enter it's print number in the search box, e.g. <code>S1234-2013</code>.
                </p>
                <p class="text-medium">If you would like to search for legislation where a certain term or phrase appears, you
                  can enter the term in the search box, e.g.&nbsp;<code>public schools</code>. If you want to match a specific phrase
                  you will need to enter it in quotes, e.g.&nbsp;<code>"Start UP NY"</code>. For more advanced queries see below.
                </p>
              </div>
            </md-card>
            <md-card class="content-card">
              <md-subheader><strong>Advanced Search Guide</strong></md-subheader>
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
                <tr><td>Action Text</td><td>actions.\*.text</td><td>text</td><td>actions.\*.text:>Signed Chap</td></tr>

                </tbody>
              </table>
            </md-card>
          </section>
        </section>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        <md-divider></md-divider>
        <section ng-controller="BillUpdatesCtrl">
          Date Range<br/> From <input type="date"/> To <input type="date" />
          Type:
          <md-select><md-option>Processed</md-option><md-option>Published</md-option></md-select>
          <md-divider></md-divider>
          {{billUpdates.response}}
        </section>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <i class="icon-question prefix-icon2"></i>About
        </md-tab-label>
        <section class="padding-20 margin-top-20 text-medium white-bg">
          <h4>The Basics</h4>
          <p>A <strong>bill</strong> is a formal proposal to add, amend,
            or repeal a body of law whereas a <strong>resolution</strong> is an official document of
            the NYS Legislature that is usually written with the intent to recognize the achievements
            of individuals, communities, and organizations.</p>

          <p>Every bill must go through several stages in the legislative process before it can be
            signed into law.
          </p>
          <ul>
            <li>The Idea</li>
            <li>Sponsorship</li>
            <li>Bill Drafting</li>
            <li>Introduction</li>
            <li>Committee Actions</li>
            <li>Revision</li>
            <li>Assembly Ways & Means and Senate Finance</li>
            <li>Rules Committees</li>
            <li>Floor Vote</li>
            <li>The Governor</li>
            <li>Veto</li>
            <li>Signed Into Law</li>
          </ul>
        </section>
      </md-tab>
    </md-tabs>
  </section>
</section>