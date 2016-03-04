<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AgendaCtrl">
  <section class="content-section">
    <md-tabs md-selected="selectedView" class="md-hue-2" md-dynamic-height="true">
      <md-tab>
        <md-tab-label><i class="icon-calendar prefix-icon2"></i>Browse</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 0" ng-controller="AgendaBrowseCtrl">
            <p class="text-medium margin-left-10 gray10">
              <i class="prefix-icon2 icon-info"></i>Repeated meetings typically have notes associated with them to indicate changes to the time/location.
            </p>
            <md-progress-linear ng-if="loading" class="md-accent md-hue-2" md-mode="indeterminate"></md-progress-linear>
            <md-card class="content-card">
              <md-card-content id="agenda-date-picker" ui-calendar="calendarConfig" ng-model="meetingEventSources"></md-card-content>
            </md-card>
          </section>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-magnifying-glass prefix-icon2"></i>Search</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <div ng-if="selectedView === 1" ng-controller="AgendaSearchCtrl">
            <div>
              <div class="gray2-bg" layout-padding>
                <form class="agenda-search">
                  <div flex>
                    <label>Search for agendas by year</label>
                    <select ng-model="searchParams.year" ng-change="selectedYearChanged()"
                            ng-options="year as year for year in years">
                    </select>
                  </div>
                </form>
              </div>
              <md-card ng-if="agendaSearch.error" class="content-card">
                <md-subheader class="md-warn">{{agendaSearch.error.message}}</md-subheader>
              </md-card>
              <md-card class="content-card">
                <div class="margin-left-20">
                  <div><strong>{{pagination.totalItems}}</strong> committee agendas were matched.</div>
                </div>
                <md-content layout="row" class="no-top-margin">
                  <div class="search-refine-panel gray2-bg" flex="25" hide show-gt-sm>
                    <div class="refine-controls">
                      <label for="sort_by_param">Sort By</label>
                      <select id="sort_by_param" ng-model="searchSort" ng-change="simpleSearch(false)">
                        <option value="">Relevance</option>
                        <option value="agenda.id.number:desc">Newest First</option>
                        <option value="agenda.id.number:asc">Oldest First</option>
                      </select>
                      <hr/>
                      <label for="week_of_param">Week Of</label>
                      <select id="week_of_param" ng-model="searchParams.weekOf">
                        <option value="">Any</option>
                        <option ng-repeat="weekOf in weekOfListing">{{weekOf}}</option>
                      </select>
                      <label for="agenda_no_param">Agenda No</label>
                      <select id="agenda_no_param" ng-model="searchParams.agendaNo">
                        <option value="">Any</option>
                        <option ng-repeat="agendaNo in agendaNoList">{{agendaNo}}</option>
                      </select>
                      <label for="committee_param">Committee</label>
                      <select id="committee_param" ng-model="searchParams.commName">
                        <option value="">Any</option>
                        <option ng-repeat="comm in committeeListing">{{comm.name}}</option>
                      </select>
                      <label for="bill_print_no_param">Bill Base Print No</label>
                      <input id="bill_print_no_param" type="text" ng-model="searchParams.printNo" ng-model-options="{debounce: 300}"
                             placeholder="e.g. S1234"/>
                      <label for="notes_param">Meeting Notes</label>
                      <input id="notes_param" type="text" ng-model="searchParams.notes" ng-model-options="{debounce: 300}"
                             placeholder="e.g. Off the floor"/>
                      <md-button ng-click="resetSearchParams() && simpleSearch(true)" class="md-accent md-hue-2 margin-top-10">Reset Filters</md-button>
                    </div>
                  </div>
                  <div class="padding-20" ng-if="agendaSearch.response.total === 0">
                    <p class="red1 text-medium bold">No results were found after applying your filters.</p>
                  </div>
                  <div flex>
                    <div flex class="text-align-right">
                      <dir-pagination-controls pagination-id="agenda-search" boundary-links="true" max-size="10"></dir-pagination-controls>
                    </div>
                    <md-list>
                      <a class="result-link"
                         dir-paginate="r in agendaSearch.results | itemsPerPage: 6"
                         total-items="agendaSearch.response.total" current-page="pagination.currPage"
                         ng-init="commAgenda = r.result;" pagination-id="agenda-search"
                         ng-href="${ctxPath}/agendas/{{commAgenda.agendaId.year}}/{{commAgenda.agendaId.number}}/{{commAgenda.committeeId.name}}?view=1">
                        <md-list-item class="md-3-line" style="cursor: pointer;">
                          <div class="md-list-item-text">
                            <h3 style="color:#008cba" class="bold">Agenda {{commAgenda.agendaId.number}} ({{commAgenda.agendaId.year}})</h3>
                            <p class="bold">{{commAgenda.committeeId.name}}</p>
                            <p>Week Of: {{commAgenda.weekOf | moment:'ll'}}</p>
                          </div>
                        </md-list-item>
                      </a>
                    </md-list>
                    <div flex style="text-align: right;">
                      <dir-pagination-controls pagination-id="agenda-search" boundary-links="true" max-size="10"></dir-pagination-controls>
                    </div>
                  </div>
                </md-content>
              </md-card>
            </div>
          </div>
        </md-tab-body>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flow-branch prefix-icon2"></i>Updates</md-tab-label>
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="selectedView === 2" ng-controller="AgendaUpdatesCtrl">
            <div class="gray2-bg padding-20 no-bottom-padding">
              <label class="margin-bottom-20">Show agenda updates during the following date range</label>
              <div class="text-medium padding-20">
                <div layout="row" class="margin-bottom-20 text-medium" layout-align="center center">
                  <div flex>
                    <label class="margin-right-10">Using</label>
                    <select ng-model="curr.type" class="no-top-margin margin-right-20">
                      <option value="processed">Processed Date</option>
                      <option value="published">Published Date</option>
                    </select>
                  </div>
                  <div flex>
                    <label class="margin-right-10">Sort</label>
                    <select ng-model="curr.sortOrder" class="no-top-margin margin-right-20">
                      <option value="ASC">Oldest First</option>
                      <option value="DESC">Newest First</option>
                    </select>
                  </div>
                  <div flex>
                    <md-checkbox class="md-accent md-hue-3 no-bottom-margin" ng-model="curr.detail" aria-label="detail">
                      Show Detail
                    </md-checkbox>
                  </div>
                </div>
                <div layout="row">
                  <div flex>
                    <label class="margin-right-10">From</label>
                    <md-datepicker class="margin-right-10" md-max-date="curr.toDate"
                                   ng-model="curr.fromDate" ng-model-options="{debounce: 300}">
                    </md-datepicker>
                  </div>
                  <div flex>
                    <label class="margin-right-10">To</label>
                    <md-datepicker class="margin-right-10"
                                   ng-model="curr.toDate" ng-model-options="{debounce: 300}">
                    </md-datepicker>
                  </div>
                </div>
              </div>
            </div>
            <md-progress-linear class="md-accent md-hue-1" md-mode="{{(agendaUpdates.fetching) ? 'query' : ''}}"></md-progress-linear>
            <div class="padding-20">
              <update-list ng-show="!agendaUpdates.fetching && agendaUpdates.response.success === true"
                           update-response="agendaUpdates.response"
                           from-date="curr.fromDate" to-date="curr.toDate"
                           pagination="pagination" show-details="curr.detail">
              </update-list>
              <md-card class="content-card" ng-if="agendaUpdates.response.success === false">
                <md-subheader class="margin-10 md-warn">
                  <h4>{{agendaUpdates.errMsg}}</h4>
                </md-subheader>
              </md-card>
            </div>
          </section>
        </md-tab-body>
      </md-tab>
    </md-tabs>
  </section>
</section>