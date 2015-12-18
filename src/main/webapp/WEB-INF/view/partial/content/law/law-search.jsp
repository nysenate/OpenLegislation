<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="LawCtrl">
  <section class="content-section">
    <md-tabs class="md-hue-2" md-selected="selectedView" md-dynamic-height="true">
      <md-tab>
        <md-tab-label>
          <i class="icon-magnifying-glass prefix-icon2"></i>Search
        </md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <div ng-controller="LawSearchCtrl">
            <div class="gray2-bg" layout-padding>
              <%-- Main Search Controls --%>
              <form name="law-search-form">
                <div class="relative" layout="column">
                  <label class="margin-bottom-10">
                    Search for laws
                  </label>
                  <input tabindex="1" class="padding-10" style="font-size:1.4em;" name="quick-term"
                         placeholder="e.g. official state muffin, STL 84" ng-model="lawSearch.term" ng-model-options="{debounce: 400}"
                         ng-change="simpleSearch(true)">
                </div>
              </form>
              <md-progress-linear class="md-accent md-hue-1" md-mode="{{(lawSearch.searching) ? 'query' : ''}}">
              </md-progress-linear>
                <md-subheader ng-show="lawSearch.term && pagination.totalItems === 0"
                              class="margin-10 md-warn">
                  <h3>No search results were found</h3>
                  <h3 ng-show="lawSearch.error">{{curr.billSearch.error.message}}</h3>
                </md-subheader>
                <div ng-show="pagination.totalItems > 0">
                  <md-card class="content-card">
                    <div layout-padding layout="row" layout-sm="column" layout-align="space-between center">
                      <div flex> {{pagination.totalItems}} matching law documents were found. </div>
                      <div flex style="text-align: right;">
                        <dir-pagination-controls pagination-id="law-search" max-size="5" boundary-links="true"></dir-pagination-controls>
                      </div>
                    </div>
                    <div flex class="margin-left-20">
                      <md-list>
                        <a class="result-link"
                           dir-paginate="r in lawSearch.results | itemsPerPage: 6"
                           total-items="pagination.totalItems" current-page="pagination.currPage"
                           ng-init="law = r.result; highlights = r.highlights;" pagination-id="law-search"
                           ng-href="${ctxPath}/laws/{{law.lawId}}/?location={{law.locationId}}">
                          <md-list-item style="cursor: pointer;">
                            <div flex="100">
                              <h3>
                                <span class="blue3 no-margin bold">{{law.lawName}}</span>
                                <span class="margin-left-20 bold text-medium">{{law.docType}} {{law.docLevelId}}</span>
                                <span class="margin-left-20 text-medium" ng-if="!highlights.title">{{law.title | default:'No Title'}}</span>
                                <span class="margin-left-20 text-medium" ng-if="highlights.title" ng-bind-html="highlights.title[0]"></span>

                              </h3>
                              <hr/>
                              <div ng-if="highlights.text">
                                <div class="margin-top-10">
                                  <span ng-repeat="snip in highlights.text" class="gray10 text-small tight-lines"
                                     ng-if="highlights.text" ng-bind-html="snip">
                                  </span>
                                </div>
                              </div>
                            </div>
                          </md-list-item>
                        </a>
                      </md-list>
                    </div>
                    <div class="subheader" layout="row" layout-align="end center">
                      <div flex style="text-align: right;">
                        <dir-pagination-controls pagination-id="law-search" max-size="5" boundary-links="true"></dir-pagination-controls>
                      </div>
                    </div>
                  </md-card>
                </div>
            </div>
            <div class="law-listing-filter">
              <label class="margin-bottom-10 margin-right-10">
                Browse by Law Volume
              </label>
              <input class="padding-5" ng-model-options="{debounce: 200}" ng-model="lawFilter" placeholder="e.g. TAX"/>
            </div>
            <md-card class="no-margin">
              <md-content class="text-medium">
                <md-list>
                  <md-list-item ng-repeat="law in lawListing | filter:lawFilter | limitTo:listingLimit"
                                class="law-node md-2-line" ng-click="go('${ctxPath}/laws/' + law.lawId)">
                    <div style="width: 80px;">
                      <strong>{{law.lawId}}</strong>
                    </div>
                    <div class="md-list-item-text">
                      <h3>{{law.name}}</h3>
                      <p>{{law.lawType}} | Chapter {{law.chapter}}</p>
                    </div>
                  </md-list-item>
                </md-list>
                <div infinite-scroll="keepScrolling()" infinite-scroll-distance="1"></div>
              </md-content>
            </md-card>
          </div>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flow-branch prefix-icon2"></i>Updates</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-controller="LawUpdatesCtrl">
            <div class="padding-20 gray3-bg">
              <div>Show law updates during the following date range</div>
              <div layout="column" layout-gt-sm="row" layout-align="start center"
                   class="padding-20 text-medium">
                <div flex>
                  <label>From</label>
                  <md-datepicker ng-model="curr.fromDate" md-max-date="curr.toDate"
                                 ng-change="onParamChange()"></md-datepicker>
                </div>
                <div flex>
                  <label>To</label>
                  <md-datepicker ng-model="curr.toDate" md-min-date="curr.fromDate"
                                 ng-change="onParamChange()"></md-datepicker>
                </div>
                <div flex>
                  <label>With </label>
                  <select class="margin-left-10" ng-model="curr.type" ng-change="onParamChange()">
                    <option value="processed">Processed Date</option>
                    <option value="published">Published Date</option>
                  </select>
                </div>
                <div flex>
                  <label>Sort </label>
                  <select class="margin-left-10" ng-model="curr.sortOrder" ng-change="onParamChange()">
                    <option value="desc" selected>Newest First</option>
                    <option value="asc">Oldest First</option>
                  </select>
                </div>
              </div>
            </div>
            <md-progress-linear class="md-accent md-hue-1" md-mode="{{(lawUpdates.fetching) ? 'query' : ''}}"></md-progress-linear>
            <div class="padding-20">
              <update-list ng-show="!lawUpdates.fetching && lawUpdates.response.success === true"
                           update-response="lawUpdates.response"
                           from-date="curr.fromDate" to-date="curr.toDate"
                           pagination="pagination" show-details="curr.detail">
              </update-list>
            </div>
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