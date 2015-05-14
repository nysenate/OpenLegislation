<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-section" ng-controller="MemberCtrl">
  <md-tabs md-selected="selectedView" md-dynamic-height="false">
    <md-tab>
      <md-tab-label>
        <i class="icon-archive prefix-icon2"></i>
        Browse
      </md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <section ng-if="selectedView === 0" class="margin-top-20" ng-controller="MemberBrowseCtrl">
          <md-toolbar class="md-hue-2">
            <div class="margin-left-20" layout="row" layout-align="start center">
              <div>
                <md-radio-group layout="row" ng-model="memberBrowse.chamberSelected" ng-change="filterMembers()">
                  <md-radio-button value="SENATE"><span class="text-medium">Senate</span></md-radio-button>
                  <md-radio-button value="ASSEMBLY"><span class="text-medium">Assembly</span></md-radio-button>
                </md-radio-group>
              </div>
              <div flex>
                <span class="text-medium margin-left-20">Session Year</span>
                <md-select ng-model="memberBrowse.sessionYear" class="medium" style="padding-left: 12px;">
                  <md-option ng-value="year" ng-repeat="year in sessionYears">
                    {{year}}
                  </md-option>
                </md-select>
              </div>
            </div>
          </md-toolbar>
          <md-card class="content-card">
            <md-content>
              <form name="filterForum">
                <md-input-container class="md-primary">
                  <label for="filterMembers">
                    <i class="icon-funnel prefix-icon2"></i>
                    Filter Members
                  </label>
                  <input id="filterMembers" ng-model="memberBrowse.filter">
                </md-input-container>
              </form>
            </md-content>
          </md-card>

          <div layout="row" layout-wrap layout-align="center center">
            <md-card ng-repeat="member in memberBrowse.results | filter:memberBrowse.filter" ng-if="memberBrowse.response.success"
                     class="content-card" style="width: 300px;margin-right: 10px;">
              <a ng-href="${ctxPath}/members/{{member.sessionYear}}/{{member.memberId}}"
                 class="result-link" style="display:block; height:100%;">
                <div layout="row">
                  <div flex style="height: 125px;max-width:100px;" class="margin-right-10">
                    <img ng-src="${ctxPath}/static/img/business_assets/members/mini/{{member.imgName}}"
                         style="height:100%;width:auto;">
                  </div>
                  <div flex>
                    <h4 class="blue3">{{member.fullName}}</h4>
                    <h6 class="no-margin">{{member.chamber | capitalize}}: {{member.sessionYear}}</h6>
                    <h6 class="no-margin">Member Id: {{member.memberId}}</h6>
                    <h6 class="no-margin">District Code: {{member.districtCode}}</h6>
                  </div>
                </div>
              </a>
            </md-card>
          </div>
        </section>
      </md-tab-body>
    </md-tab>
    <%-- Disabling search for now. It's not entirely necessary at this point.
    <md-tab>
      <md-tab-label>
        <i class="icon-magnifying-glass prefix-icon2"></i>
        Search
      </md-tab-label>
      <md-tab-body>
        <section ng-if="selectedView === 1" ng-controller="MemberSearchCtrl">
          <md-card class="content-card">
            <md-content class="md-padding">
              <form name = searchForm>
                <md-input-container class="md-primary">
                  <label for="memberSearch">
                    <i class="icon-magnifying-glass prefix-icon2"></i>
                    Search Members
                  </label>
                  <input ng-model="memberSearch.term" ng-model-options="{debounce: 300}" ng-change="searchMembers(true)" id="memberSearch">
                </md-input-container>
              </form>
            </md-content>
          </md-card>
          <md-card class="content-card" ng-if="memberSearch.term && !memberSearch.paginate.totalItems">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              No results found.
            </div>
          </md-card>
          <md-card class="content-card" ng-if="memberSearch.term && memberSearch.paginate.totalItems">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>{{memberSearch.response.total}} members were matched. Viewing page {{memberSearch.paginate.currPage}} of {{memberSearch.paginate.lastPage}}</div>
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"></dir-pagination-controls>
              </div>
            </div>
            <md-content>
              <md-list>
                <a dir-paginate="match in memberSearch.matches | itemsPerPage: memberSearch.paginate.itemsPerPage"
                   total-items="memberSearch.paginate.totalItems"
                   current-page="memberSearch.paginate.currPage"
                   ng-href=${ctxPath}/members/{{match.sessionYear}}/{{match.memberId}}
                   class="result-link">
                  <md-item>
                    <md-item-content>
                      <div class="padding-5" style="width:180px;">
                        <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{match.imgName}}"
                             style="width:50%;">
                      </div>
                      <div flex class="md-tile-content">
                        <h3>{{match.fullName}}</h3>
                        <h6 class="gray7 no-margin">{{match.chamber | capitalize}}: {{match.sessionYear}}</h6>
                        <h6 class="gray7 no-margin">Member Id: {{match.memberId}}</h6>
                        <h6 class="gray7 no-margin">District Code: {{match.districtCode}}</h6>
                      </div>
                    </md-item-content>
                  </md-item>
                  <md-divider></md-divider>
                </a>
              </md-list>
            </md-content>
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex style="text-align: right;">
                <dir-pagination-controls boundary-links="true" on-page-change="changePage(newPageNumber)"></dir-pagination-controls>
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
            <table class="docs-table">
              <thead>
              <tr><th>To Search for</th><th>Use the field</th><th>Example</th></tr>
              </thead>
              <tbody>
              <tr><td>Full name</td><td>fullName</td><td>fullName:Ruben Diaz</td></tr>
              <tr><td>By chamber</td><td>chamber</td><td>chamber:senate</td></tr>
              <tr><td>Session year</td><td>sessionYear</td><td>sessionYear:2013</td></tr>
              <tr><td>District</td><td>districtCode</td><td>districtCode:22</td></tr>
              <tr><td>Member Id</td><td>memberId</td><td>memberId:400</td></tr>
              </tbody>
            </table>
          </md-card>
        </section>
      </md-tab-body>
    </md-tab> --%>
  </md-tabs>
</section>