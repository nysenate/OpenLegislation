<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="LawCtrl">
  <section class="content-section">
    <md-tabs md-selected="selectedView" md-dynamic-height="false">
      <md-tab>
        <md-tab-label>
          <i class="icon-list prefix-icon2"></i>Listings
        </md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 0" ng-controller="LawListingCtrl">
            <md-card class="content-card law-listing-filter">
              <md-content>
                <md-input-container>
                  <label><i class="icon-funnel prefix-icon2"></i>Filter law listing</label>
                  <input ng-model-options="{debounce: 200}" ng-model="lawFilter"/>
                </md-input-container>
              </md-content>
            </md-card>
            <md-card class="no-margin">
              <md-content class="text-medium">
                <md-list>
                  <md-list-item ng-repeat="law in lawListing | filter:lawFilter | limitTo:curr.listingLimit"
                                class="law-node md-2-line" ng-click="go('${ctxPath}/laws/' + law.lawId)">
                    <div style="width: 80px;">
                      <strong>{{law.lawId}}</strong>
                    </div>
                    <div class="md-list-item-text">
                      <h3>{{law.name}}</h3>
                      <p>{{law.lawType}} | Chapter {{law.chapter}}</p>
                    </div>
                    <md-divider></md-divider>
                  </md-list-item>
                </md-list>
                <div infinite-scroll="keepScrolling()" infinite-scroll-distance="1"></div>
              </md-content>
            </md-card>
          </section>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-magnifying-glass prefix-icon2"></i>Search</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 1" ng-controller="LawSearchCtrl">
            <form name="law-search-form">
              <md-content class="relative padding-20 margin-top-10">
                <md-input-container class="md-primary">
                  <label><i class="prefix-icon2 icon-magnifying-glass"></i>Search for law documents</label>
                  <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                         ng-model="lawSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
                </md-input-container>
                <div ng-if="lawSearch.searching" class="loading-pulse">
                  Searching laws...
                </div>
              </md-content>
              <md-divider></md-divider>
              <md-subheader ng-show="lawSearch.searched && lawSearch.term && !lawSearch.error && pagination.totalItems === 0"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>No search results were found for '{{lawSearch.term}}'</h4>
              </md-subheader>
              <md-subheader ng-show="lawSearch.searched && lawSearch.term && lawSearch.error"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>{{lawSearch.error.message}}</h4>
              </md-subheader>
              <section ng-show="pagination.totalItems > 0">
                <md-card class="content-card">
                  <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
                    <div flex> {{pagination.totalItems}} matching law documents were found.
                      <span ng-if="pagination.totalItems > 0">Viewing page {{pagination.currPage}} of {{pagination.lastPage}}.</span>
                    </div>
                    <div flex style="text-align: right;">
                      <dir-pagination-controls pagination-id="law-search" max-size="5" boundary-links="true"></dir-pagination-controls>
                    </div>
                  </div>
                  <md-content>
                    <div flex class="padding-20">
                      <md-list>
                        <a class="result-link"
                           dir-paginate="r in lawSearch.results | itemsPerPage: 6"
                           total-items="pagination.totalItems" current-page="pagination.currPage"
                           ng-init="law = r.result; highlights = r.highlights;" pagination-id="law-search"
                           ng-href="${ctxPath}/laws/{{law.lawId}}/?location={{law.locationId}}">
                          <md-list-item class="md-3-line" style="cursor: pointer;">
                            <div class="md-list-item-text">
                              <h3>
                                <span class="blue3 no-margin bold">{{law.lawName}}</span>
                                <span class="margin-left-20 bold text-medium">{{law.docType}} {{law.docLevelId}}</span>
                              </h3>
                              <hr/>
                              <p style="color:#444" class="text-medium" ng-if="!highlights.title">{{law.title | default:'No Title'}}</p>
                              <p style="color:#444" class="text-medium" ng-if="highlights.title" ng-bind-html="highlights.title[0]"></p>
                              <div ng-if="highlights.text">
                                <div class="margin-top-10">
                                  <p ng-repeat="snip in highlights.text" class="gray10 text-small tight-lines"
                                     ng-if="highlights.text" ng-bind-html="snip">
                                  </p>
                                </div>
                              </div>
                            </div>
                          </md-list-item>
                        </a>
                      </md-list>
                    </div>
                  </md-content>
                  <div class="subheader" layout="row" layout-align="end center">
                    <div flex style="text-align: right;">
                      <dir-pagination-controls pagination-id="law-search" max-size="5" boundary-links="true"></dir-pagination-controls>
                    </div>
                  </div>
                </md-card>
              </section>
            </form>
          </section>
          <section>
            <toggle-panel label="Quick Search Tips" open="true" extra-classes="content-card">
              <div class="padding-20">
                <p class="text-medium">Law documents are organized under a certain volume using a document id.
                  For example, if you are looking for §405.00 of Penal Law, simply enter <code>405.00 Penal</code>. You can also
                  search against the title and text of the law documents by entering in any keywords or phrases like <code>public
                  fireworks</code>. Search queries are made against only the current laws in the database and does not include repealed
                  laws.
                </p>
              </div>
            </toggle-panel>
          </section>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 2" ng-controller="LawUpdatesCtrl">
            <md-card class="content-card">
              <md-subheader>Show law updates during the following date range</md-subheader>
              <div layout="row" class="padding-20 text-medium">
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
              <div layout="row" class="padding-20 text-medium">
                <div flex>
                  <label>With </label>
                  <select class="margin-left-10" ng-model="curr.type">
                    <option value="processed">Processed Date</option>
                    <option value="published">Published Date</option>
                  </select>
                </div>
                <div flex>
                  <label>Sort </label>
                  <select class="margin-left-10" ng-model="curr.sortOrder">
                    <option value="desc" selected>Newest First</option>
                    <option value="asc">Oldest First</option>
                  </select>
                </div>
              </div>
            </md-card>
            <div ng-if="lawUpdates.fetching" class="text-medium text-align-center">Fetching updates, please wait.</div>
            <update-list ng-show="!lawUpdates.fetching && lawUpdates.response.success === true"
                         update-response="lawUpdates.response"
                         from-date="curr.fromDate" to-date="curr.toDate"
                         pagination="pagination" show-details="curr.detail">
            </update-list>
            <md-card class="content-card" ng-if="lawUpdates.response.success === false">
              <md-subheader class="margin-10 md-warn">
                <h4>{{lawUpdates.errMsg}}</h4>
              </md-subheader>
            </md-card>
          </section>
        </md-tab-body>
      </md-tab>
    </md-tabs>
  </section>
</section>