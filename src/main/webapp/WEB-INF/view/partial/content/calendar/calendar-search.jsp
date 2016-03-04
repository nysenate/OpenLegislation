<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="CalendarSearchPageCtrl" ng-init="setHeaderVisible(true); setCalendarHeaderText()">
  <md-tabs md-selected="activeIndex" class="md-hue-2" md-dynamic-height="true">

    <!-- Browse Calendars -->

    <md-tab md-on-select="setCalendarHeaderText(); renderCalendarEvent()">
      <md-tab-label>
        <i class="icon-calendar prefix-icon2"></i>Browse
      </md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <div ng-if="pageNames[activeIndex] === 'browse'" ng-controller="CalendarBrowseCtrl" class="content-card">
          <div id="calendar-date-picker" ui-calendar="calendarConfig" ng-model="eventSources" class="padding-20"></div>
        </div>
      </md-tab-body>
    </md-tab>

    <!-- Search Calendars -->

    <md-tab md-on-select="setCalendarHeaderText()">
      <md-tab-label>
        <i class="icon-magnifying-glass prefix-icon2"></i>Search
      </md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <section ng-if="pageNames[activeIndex] === 'search'" ng-controller="CalendarSearchCtrl">
          <form name="calendar-search-form" layout-padding class="gray3-bg">
            <md-tabs md-selected="searchActiveIndex" md-no-ink-bar md-dynamic-height="true">
              <md-tab label="field search">
                <md-tab-body>
                  <div layout="row" layout-sm="column">
                    <div class="search-refine-panel">
                      <div class="refine-controls" layout-gt-xs="row"  layout="column">
                        <div flex class="margin-right-20">
                          <label>Year</label>
                          <select name="year-select" ng-model="searchFields.year" class="margin-right-20"
                                  ng-options="year for year in activeYears">
                            <option value="">All Years</option>
                          </select>
                        </div>
                        <div flex class="margin-right-20">
                          <label>Search By</label>
                          <select ng-model="searchFields.fieldName" class="margin-right-10"
                                  ng-options="value as label for (value, label) in fieldOptions">
                          </select>
                        </div>
                        <div flex class="margin-right-20">
                          <label>Search Value</label>
                          <input type="text" ng-model="searchFields.fieldValue" ng-model-options="{debounce: 300}">
                        </div>
                        <div flex class="margin-right-20">
                          <label>Sort By</label>
                          <select ng-model="searchFields.order"
                                  ng-options="value as label for (value, label) in orderOptions">
                            <option value="">--</option>
                          </select>
                        </div>
                        <div flex>
                          <md-checkbox ng-model="searchFields.activeList" ng-disabled="searchFields.fieldName === 'calendarNumber'"
                                       class="margin-top-20 md-hue-2" style="height: 33px">
                            Active List Only
                          </md-checkbox>
                        </div>
                      </div>
                    </div>
                  </div>
                </md-tab-body>
              </md-tab>
              <md-tab label="query search">
                <md-tab-body>
                  <div class="padding-20" layout="row" layout-sm="column">
                    <md-input-container class="md-primary" flex="60">
                      <label><i class="prefix-icon2 icon-magnifying-glass"></i>Advanced Calendar Query Search</label>
                      <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                             ng-model="searchQuery.term" ng-model-options="{debounce: 300}">
                    </md-input-container>
                    <md-input-container class="md-primary" flex="40">
                      <label><i class="prefix-icon2 icon-align-bottom"></i>Sort By</label>
                      <input tabindex="2" style="font-size:1.4rem;" name="quick-term"
                             ng-model="searchQuery.sort" ng-model-options="{debounce: 300}">
                    </md-input-container>
                  </div>
                </md-tab-body>
              </md-tab>
            </md-tabs>
          </form>

          <md-divider></md-divider>
          <div layout-padding>
            <md-subheader ng-show="!searching && searchQuery.term && pagination.totalItems === 0"
                          class="margin-10 md-warn md-whiteframe-z0">
              <h3>No search results were found for '{{searchQuery.term}}'</h3>
            </md-subheader>
            <md-progress-linear class="md-accent md-hue-1" md-mode="{{(searching) ? 'query' : ''}}"></md-progress-linear>

            <div ng-show="!searching && searchQuery.term && pagination.totalItems > 0">
              <div>
                <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
                  <div class="margin-5">
                    <strong>{{pagination.totalItems}}</strong> calendars were matched.&nbsp;&nbsp;<br hide-gt-sm/>
                    Viewing page {{pagination.currPage}} of {{pagination.lastPage}}.
                  </div>
                  <dir-pagination-controls pagination-id="33" boundary-links="true"></dir-pagination-controls>
                </div>
                <md-content class="no-top-margin">
                  <md-list>
                    <a dir-paginate="r in searchResults | itemsPerPage: 6"
                       total-items="searchResponse.total" current-page="pagination.currPage"
                       ng-init="cal = r.result" class="result-link" pagination-id="33"
                       ng-href="{{getCalendarUrl(cal.year, cal.calendarNumber, searchActiveIndex === 0 && ['billCalNo', 'printNo'].indexOf(searchFields.fieldName) >= 0 ? searchFields.fieldValue : null)}}"
                       ng-click="changeTab(cal.activeLists.size>0 ? 'active-list' : 'floor')">

                      <md-list-item layout-sm="column" layout-align-sm="center start"
                                    style="cursor: pointer;" >
                        <div layout-sm="row" layout-align-sm="start end" style="padding:16px;">
                          <h3 class="no-margin blue3 margin-right-20">
                            Senate Calendar {{cal.calendarNumber}}
                            <span hide-gt-sm>&nbsp;</span>
                          </h3>
                          <h5 class="no-margin">
                            {{cal.calDate | moment:'MMMM D, YYYY'}}
                          </h5>
                        </div>
                        <div class="md-tile-content" layout="column">
                          <div layout-gt-sm="row" layout-align="start end">
                            <h5 class="no-margin" style="width: 200px">
                              {{getTotalActiveListBills(cal)}} Total Active List Bills
                            </h5>
                            <h5 class="no-margin" hide-sm style="width: 200px">
                              {{cal.activeLists.size > 0 ? cal.activeLists.size - 1 : 0}} Active List Supplementals
                            </h5>
                          </div>
                          <div layout-gt-sm="row" layout-align="start end">
                            <h5 class="no-margin" style="width: 200px">
                              {{getTotalFloorBills(cal)}} Total Floor Bills
                            </h5>
                            <h5 class="no-margin" hide-sm style="width: 200px">
                              {{cal.supplementalCalendars.size}} Floor Supplementals
                            </h5>
                          </div>
                        </div>
                      </md-list-item>

                      <md-divider hide-gt-sm ng-hide="$last"></md-divider>
                    </a>
                  </md-list>
                </md-content>
              </div>
            </div>
          </div>

          <!-- Search Documentation -->
          <section ng-if="searchActiveIndex == 1" class="fade fade-out padding-20">
            <toggle-panel label="Quick search for Calendars" open="true" extra-classes="content-card">
              <div class="padding-20">
                <p class="text-medium">Calendars are uniquely identified by their year and a calendar number, which corresponds
                  to their order within a year.  To find a specific calendar, enter its year and calendar number e.g.
                  <code>2015#5</code>.
                </p>
              </div>
            </toggle-panel>
            <toggle-panel label="Advanced Search Guide" open="false" extra-classes="content-card">
              <div class="padding-20">
                <p class="text-medium">You can combine the field definitions documented below to perform targeted searches.
                  You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
                  as well as parenthesis for grouping. Additionally, you can specify a sort order for the search results
                  using one or more of the fields below e.g. <code>activeLists.size:ASC, calDate:DESC</code>
                  For more information refer to the
                  <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene query docs</a>.</p>
              </div>
              <table class="docs-table">
                <thead>
                <tr><th>To search for</th><th>Use the field</th><th>With value type</th><th>Examples</th></tr>
                </thead>
                <tbody>
                <tr style="background:#f1f1f1;"><td colspan="4"><strong>Basic Details</strong></td></tr>
                <tr><td>Year</td><td>year</td><td>number</td><td>year:2015</td></tr>
                <tr><td>Calendar Number</td><td>calendarNumber</td><td>number</td><td>calendarNumber:13</td></tr>
                <tr><td>Calendar Date</td><td>calDate</td><td>date</td><td>calDate:2015-03-03<br/>calDate:[2015-03-01 TO 2015-03-10]</td></tr>
                <tr><td>Release Date/Time</td><td>releaseDateTime</td><td>date-time</td><td>releaseDateTime:2015-03-03<br/>releaseDateTime:[2015-03-01 TO 2015-03-10]</td></tr>
                <tr><td>Active List Present</td><td>activeLists.size</td><td>number</td><td>activeLists.size:>0</td></tr>
                <tr style="background:#f1f1f1;"><td colspan="4"><strong>The fields below are associated with calendar bill entries and are always prefixed with '\*.'</strong></td> </tr>
                <tr><td>Bill field on active list</td><td>activeLists\*.anyBillField</td><td></td><td>activeLists\*.title:acupuncture</td></tr>
                <tr><td>Bill Print No.</td><td>\*.printNo</td><td>string</td><td>\*.printNo:S1111</td></tr>
                <tr><td>Bill Calendar No.</td><td>\*.billCalNo</td><td>string</td><td>\*.billCalNo:81</td></tr>
                <tr><td>Bill Title</td><td>\*.title</td><td>string</td><td>\*.title:town of Chester</td></tr>
                <tr><td>Bill Sponsor</td><td>\*.shortName</td><td>string</td><td>\*.shortName:YOUNG</td></tr>
                <tr><td>Bill Sponsor</td><td>\*.fullName</td><td>string</td><td>\*.fullName:Catharine Young</td></tr>
                <tr><td>Bill Sponsor</td><td>\*.districtCode</td><td>number</td><td>\*.districtCode:57</td></tr>
                </tbody>
              </table>
            </toggle-panel>
          </section>
        </section>
      </md-tab-body>
    </md-tab>

    <!-- Calendar Updates -->
    <md-tab md-on-select="setCalendarHeaderText()">
      <md-tab-label><i class="icon-flow-branch prefix-icon2"></i>Updates</md-tab-label>
      <md-tab-body>
        <md-divider></md-divider>
        <section ng-if="pageNames[activeIndex] === 'updates'" ng-controller="CalendarFullUpdatesCtrl">
          <div class="gray2-bg padding-20 no-bottom-padding">
            <label class="margin-bottom-20">Show calendar updates during the following date range</label>
            <div class="text-medium padding-20">
              <div layout="row" class="margin-bottom-20 text-medium" layout-align="center center">
                <div flex>
                  <label class="margin-right-10">Using</label>
                  <select ng-model="updateOptions.type" class="no-top-margin margin-right-20">
                    <option value="processed">Processed Date</option>
                    <option value="published">Published Date</option>
                  </select>
                </div>
                <div flex>
                  <label class="margin-right-10">Sort</label>
                  <select ng-model="updateOptions.order" class="no-top-margin margin-right-20">
                    <option value="ASC">Oldest First</option>
                    <option value="DESC">Newest First</option>
                  </select>
                </div>
                <div flex>
                  <md-checkbox class="md-accent md-hue-3 no-bottom-margin" ng-model="updateOptions.detail" aria-label="detail">
                    Show Detail
                  </md-checkbox>
                </div>
              </div>
              <div layout="row">
                <div flex>
                  <label class="margin-right-10">From</label>
                  <md-datepicker class="margin-right-10" md-max-date="updateOptions.toDateTime"
                         ng-model="updateOptions.fromDateTime" ng-model-options="{debounce: 300}">
                  </md-datepicker>
                </div>
                <div flex>
                  <label class="margin-right-10">To</label>
                  <md-datepicker class="margin-right-10"
                         ng-model="updateOptions.toDateTime" ng-model-options="{debounce: 300}">
                  </md-datepicker>
                </div>
              </div>
            </div>
          </div>
          <md-progress-linear class="md-accent md-hue-1" md-mode="{{(loadingUpdates) ? 'query' : ''}}"></md-progress-linear>
          <div class="padding-20">
            <update-list class="error-toast-parent" ng-show="!loadingUpdates"
                         update-response="updateResponse" pagination="pagination" show-details="updateOptions.detail">
            </update-list>
          </div>
          </section>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</section>