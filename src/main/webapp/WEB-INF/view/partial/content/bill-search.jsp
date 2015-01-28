<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
  <md-tabs md-selected="selectedView">
    <md-tab label="Quick Search">
      <section ng-controller="BillSearchCtrl">
        <form name="bill-search-form">
          <md-content class="padding-10 margin-10 md-whiteframe-z0">
            <md-input-container>
              <label><i class="prefix-icon2 icon-search"></i>Search for a term or print no (e.g. 'S1234')</label>
              <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                     ng-model="billSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch()">
            </md-input-container>
          </md-content>
          <md-subheader ng-show="billSearch.searched && billSearch.totalResultCount === 0"
                        class="margin-10 md-warn md-whiteframe-z0">
            <h4>No search results were found for '{{billSearch.term}}'</h4>
          </md-subheader>
        </form>
        <section ng-show="billSearch.searched && billSearch.totalResultCount > 0">
          <md-content class=" padding-10 margin-10 no-top-margin">
            <md-subheader class="md-primary padding-10">
                Showing results 1 - {{billSearch.limit}} out of {{billSearch.totalResultCount}}
            </md-subheader>
            <md-list>
              <a ng-repeat="r in billSearch.results" ng-init="bill = r.result" class="result-link"
                 ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}">
                <md-item>
                  <md-item-content layout-sm="column" layout-align-sm="center start" style="cursor: pointer;">
                    <div style="width:180px;padding:16px;">
                      <h3 class="no-margin">{{bill.basePrintNo}} - {{bill.session}}</h3>
                      <h5 class="no-margin">{{bill.sponsor.member.fullName}}</h5>
                    </div>
                    <div flex class="md-tile-content">
                      <h4>{{bill.title}}</h4>
                      <h6 class="gray7 no-margin capitalize">{{getStatusDesc(bill.status) | lowercase}}</h6>
                    </div>
                  </md-item-content>
                  <md-divider ng-if="!$last"/>
                </md-item>
              </a>
            </md-list>
          </md-content>
          <div ng-show="billSearch.totalResultCount > billSearch.limit"
               style="font-size:13px;" class="md-whiteframe-z0 white-bg margin-10 padding-10"
               layout="row" layout-align="left center">
            <md-button class="md-primary md-no-ink margin-right-10">First</md-button>
            <md-button class="md-primary md-no-ink margin-right-10">Previous</md-button>
            <md-button ng-click="nextPage()" class="md-primary md-no-ink margin-right-10">Next</md-button>
            <md-button class="md-primary md-no-ink margin-right-10">Last</md-button>
          </div>
        </section>
      </section>
    </md-tab>
    <md-tab label="Advanced Search">
      <md-content class="md-padding">
        <md-input-container flex>
          <label><i class="prefix-icon2 icon-search"></i>Search for a term or print no</label>
          <input required name="description">
        </md-input-container>
        <md-input-container flex>
          <label><i class="prefix-icon2 icon-search"></i>Sponsor</label>
          <input required name="sponsor">
        </md-input-container>
      </md-content>
    </md-tab>
  </md-tabs>
</section>