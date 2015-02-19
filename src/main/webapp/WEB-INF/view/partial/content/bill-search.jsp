<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillSearchCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-hue-2">
      <md-tab label="Explore">
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
              <md-subheader class="md-primary">
                {{curr.pagination.totalItems}} bills were matched. Viewing page {{curr.pagination.currPage}} of {{curr.pagination.lastPage}}.
              </md-subheader>
              <md-content class="padding-20 no-top-margin">
                <md-list>
                  <a ng-repeat="r in billSearch.results" ng-init="bill = r.result; highlights = r.highlights;" class="result-link"
                     ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearch.term}}&view=1&searchPage={{curr.pagination.currPage}}">
                    <md-item>
                      <md-item-content layout-sm="column" layout-align-sm="center start" style="cursor: pointer;">
                        <div style="width:180px;padding:16px;">
                          <h3 class="no-margin">{{bill.basePrintNo}} - {{bill.session}}</h3>
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
              <div ng-show="curr.pagination.needsPagination()" class="md-whiteframe-z0 white-bg text-medium margin-10 padding-10"
                   layout="row" layout-align="left center">
                <md-button ng-click="paginate('first')" class="md-primary md-no-ink margin-right-10">First</md-button>
                <md-button ng-disabled="!curr.pagination.hasPrevPage()"
                           ng-click="paginate('prev')" class="md-primary md-no-ink margin-right-10">Previous</md-button>
                <md-button ng-click="paginate('next')"
                           ng-disabled="!curr.pagination.hasNextPage()"
                           class="md-primary md-no-ink margin-right-10">Next</md-button>
                <md-button ng-click="paginate('last')" class="md-primary md-no-ink margin-right-10">Last</md-button>
              </div>
            </md-card>
            </section>
        </section>
      </md-tab>
      <md-tab label="Advanced Search">
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
      </md-tab>
      <md-tab label="Updates">
      </md-tab>
    </md-tabs>
  </section>
</section>