<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillSearchCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab>
        <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
        <md-divider></md-divider>
        <section>
          <form name="bill-search-form">
            <md-content class="padding-20">
              <md-input-container class="md-primary">
                <label><i class="prefix-icon2 icon-search"></i>Search for a term or print no (e.g. 'S1234')</label>
                <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                       ng-model="billSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
              </md-input-container>
            </md-content>
            <md-divider></md-divider>
            <md-subheader ng-show="billSearch.searched && billSearch.term && curr.pagination.totalItems === 0"
                          class="margin-10 md-warn md-whiteframe-z0">
              <h4>No search results were found for '{{billSearch.term}}'</h4>
            </md-subheader>
          </form>
          <section ng-show="billSearch.searched && curr.pagination.totalItems > 0">
            <md-card class="content-card">
              <md-subheader>
                {{curr.pagination.totalItems}} bills were matched. Viewing page {{curr.pagination.currPage}} of {{curr.pagination.lastPage}}.
              </md-subheader>
              <md-content class="no-top-margin">
                <md-list>
                  <a ng-repeat="r in billSearch.results" ng-init="bill = r.result; highlights = r.highlights;" class="result-link"
                     ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearch.term}}&view=1&searchPage={{curr.pagination.currPage}}">
                    <md-item>
                      <md-item-content layout-sm="column" layout-align-sm="center start" style="cursor: pointer;">
                        <div>
                          <%--<img src="http://lorempixel.com/50/50/people/{{$index}}"--%>
                          <%--style="width:40px;"/>--%>
                        </div>
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
                      <%--<md-divider ng-if="!$last"/>--%>
                    </md-item>
                  </a>
                </md-list>
              </md-content>
              <div ng-show="curr.pagination.needsPagination()" class="text-medium margin-10 padding-10"
                   layout="row" layout-align="left center">
                <md-button ng-click="paginate('first')" class="md-primary md-no-ink margin-right-10"><i class="icon-first"></i>&nbsp;First</md-button>
                <md-button ng-disabled="!curr.pagination.hasPrevPage()"
                           ng-click="paginate('prev')" class="md-primary md-no-ink margin-right-10"><i class="icon-arrow-left5"></i>&nbsp;Previous</md-button>
                <md-button ng-click="paginate('next')"
                           ng-disabled="!curr.pagination.hasNextPage()"
                           class="md-primary md-no-ink margin-right-10">Next&nbsp;<i class="icon-arrow-right5"></i></md-button>
                <md-button ng-click="paginate('last')" class="md-primary md-no-ink margin-right-10">Last&nbsp;<i class="icon-last"></i></md-button>
              </div>
            </md-card>
          </section>
          <section>
            <md-card class="content-card">
              <md-subheader><strong>Basic Search Terms</strong></md-subheader>
              <table class="docs-table">
                <thead>
                <tr><th>To search for</th><th>Use the field</th><th>Example</th></tr>
                </thead>
                <tbody>
                <tr><td>Original Print No</td><td>basePrintNo</td><td>basePrintNo:S1234</td></tr>
                <tr><td>Session Year</td><td>session</td><td>session:2015</td></tr>
                <tr><td>Title</td><td>title</td><td>title:moose elk</td></tr>
                <tr><td>Chamber</td><td>billType.chamber</td><td>billType.chamber:SENATE, billType.chamber:ASSEMBLY</td></tr>
                <tr><td>Is a Resolution</td><td>billType.resolution</td><td>billType.resolution:true</td></tr>
                <tr><td>Active Amendment Version</td><td>activeVersion</td><td>activeVersion:A</td></tr>
                <tr><td>Published Year</td><td>year</td><td>year:2014</td></tr>
                <tr><td>Published Date/Time</td><td>publishedDateTime</td><td>publishedDateTime:2015summary-01-02</td></tr>
                <tr><td>Summary</td><td>summary</td><td>summary:moose, elk, or deer</td></tr>
                </tbody>
              </table>
            </md-card>
          </section>
        </section>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-archive prefix-icon2"></i>Browse</md-tab-label>
        <md-divider></md-divider>
        <section ng-controller="BillExploreCtrl">
          <a>Bills with recent status updates: api/3/bills/2015?sort=status.actionDate:DESC&limit=5</a><br/>
          <a>Governor program bills: api/3/bills/2015/search?term=programInfo.name:Governor&sort=programInfo.sequenceNo:ASC</a><br/>
          <a>Budget Bills api/3/bills/2015/search?term=sponsor.budget:true</a><br/>
          <a></a>

        </section>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        <md-divider></md-divider>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <i class="icon-question prefix-icon2"></i>About
        </md-tab-label>
        <section class="padding-20 text-medium">
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