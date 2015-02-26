<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillSearchCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab>
        <md-tab-label><i class="icon-archive prefix-icon2"></i>Explore</md-tab-label>
        <md-divider></md-divider>
          <section ng-controller="BillExploreCtrl">
            <section class="padding-20 text-medium">
              <p>Open Legislation provides data for all New York State Bills and Resolutions that have
              been introduced since 2009.</p>
            </section>
            <md-divider></md-divider>
            <section class="padding-20">

            </section>
          </section>
      </md-tab>
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
          <section hide>
            <md-content class="padding-20">
              <p class="text-medium"><i class="icon-info prefix-icon2"></i>
                Perform an advanced search by entering in one or more of the fields below. Each field will
                be treated as an 'AND' operation.
              </p>
            </md-content>
            <md-divider></md-divider>
            <md-content layout="row" layout-wrap class="md-padding">
              <md-input-container layout="column" flex="33">
                <md-checkbox flex>Chamber: Senate</md-checkbox>
                <md-checkbox flex>Chamber: Assembly</md-checkbox>
                <md-divider></md-divider>
                <md-checkbox flex>Type: Bill</md-checkbox>
                <md-checkbox flex>Type: Resolution</md-checkbox>
              </md-input-container>
              <md-input-container layout="column" flex="33">
                <md-checkbox flex>Has Vote Rolls</md-checkbox>
                <md-checkbox flex>Has Amendments</md-checkbox>
                <md-checkbox flex>Has Veto Memo</md-checkbox>
                <md-checkbox flex>Has Approval Memo</md-checkbox>
              </md-input-container>
              <md-input-container layout="column" flex="33">
                <md-checkbox flex>Is Budget Bill</md-checkbox>
                <md-checkbox flex>Is Program Bill</md-checkbox>
                <md-checkbox flex>Is Substituted</md-checkbox>
                <md-checkbox flex>Is Uni-Bill</md-checkbox>
              </md-input-container>
            </md-content>
            <md-divider></md-divider>
            <md-content layout="row" layout-wrap class="md-padding">
              <md-input-container flex="33">
                <label>Title</label>
                <input name="title"/>
              </md-input-container>
              <md-input-container flex="33">
                <label>Enacting Clause</label>
                <input name="act_clause"/>
              </md-input-container>
              <md-input-container flex="33">
                <label>Sponsored By</label>
                <input name="sponsor"/>
              </md-input-container>
              <md-input-container flex="33">
                <label>Law Section</label>
                <input name="law_section">
              </md-input-container>
              <md-input-container flex="33">
                <label>Full Text</label>
                <input name="fulltext"/>
              </md-input-container>
              <md-input-container flex="33">
                <label>Memo</label>
                <input name="memo"/>
              </md-input-container>
              <div flex="33">
                <label class="margin-right-10">Status</label>
                <select>
                  <option>Any</option>
                  <option>In Senate Committee</option>
                  <option>In Assembly Committee</option>
                </select>
              </div>
            </md-content>
            <md-button class="md-accent md-raised md-hue-3 padding-10">Search</md-button>
          </section>
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