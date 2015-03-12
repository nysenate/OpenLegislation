<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="MemberSearchCtrl">

  <md-tabs class="md-primary">
    <md-tab>
      <md-tab-label>
        <i class="icon-search prefix-icon2"></i>
        Search
      </md-tab-label>

      <md-card>
        <md-content class="md-padding">
          <form name = searchForm>
            <md-input-container class="md-primary">
              <label for="memberSearch">
                <i class="icon-search prefix-icon2"></i>
                Search Members
              </label>
              <input ng-model="memberSearch.term" ng-model-options="{debounce: 300}" ng-change="searchMembers(true)" id="memberSearch">
            </md-input-container>
          </form>
        </md-content>
      </md-card>
      <md-card class="content-card" ng-if="memberSearch.doneLoadingResults && !memberSearch.paginate.totalItems">
        <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
        No results found.
        </div>
      </md-card>
      <md-card class="content-card" ng-if="memberSearch.doneLoadingResults && memberSearch.paginate.totalItems">
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
                    <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/369_john_l._sampson.jpg"
                         style="width:50%;">
                  </div>
                  <div flex class="md-tile-content">
                    <h3>{{match.fullName}}</h3>
                    <h6 class="gray7 no-margin">{{match.chamber}}: {{match.sessionYear}}</h6>
                  </div>
                  <div class="padding-16" align="right">
                    <h6 class="gray7">District Code: {{match.districtCode}}</h6>
                  </div>
                </md-item-content>
              </md-item>
              <md-divider></md-divider>
            </a>
          </md-list>
        </md-content>
      </md-card>


    </md-tab>
  </md-tabs>
</section>