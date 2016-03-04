<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="content-section" ng-controller="CalendarViewCtrl" ng-init="setHeaderVisible(true)">
    <div ng-if="calendarResponse.success === true">
      <md-tabs md-selected="curr.activeIndex" class="md-hue-2" md-dynamic-height="true" md-no-ink>
        <!-- Back to search -->
        <md-tab md-on-select="backToSearch()">
          <md-tab-label ng-if="previousPage == 'search'"><i class="icon-magnifying-glass prefix-icon2"></i>back to search</md-tab-label>
          <md-tab-label ng-if="previousPage == 'browse'"><i class="icon-calendar prefix-icon2"></i>back to browse</md-tab-label>
          <md-tab-label ng-if="previousPage == 'updates'"><i class="icon-flag prefix-icon2"></i>back to updates</md-tab-label>
          <md-tab-label ng-if="!previousPage"><i class="icon-calendar prefix-icon2"></i>browse</md-tab-label>
        </md-tab>
        <md-progress-linear class="md-accent md-hue-1" md-mode="{{(curr.state === 'fetching') ? 'query' : ''}}"></md-progress-linear>

        <!-- Active Lists -->
        <md-tab label="Active List" md-on-select="setCalendarHeaderText()"
                ng-disabled="calendarView.activeLists.size < 1">
          <md-tab-label>
            Active Lists ({{calendarView.activeLists.size}})
          </md-tab-label>
          <md-tab-body>
            <md-divider></md-divider>
            <div ng-if="pageNames[curr.activeIndex] === 'active-list'" ng-controller="CalendarActiveListCtrl">
              <md-toolbar class="md-toolbar-tools auto-height" style="padding:15px"
                          ng-if="activeLists.length > 0">
                <div layout-gt-sm="row" layout="column" layout-align="start center">
                  <label class="margin-right-20 text-medium" ng-show="activeLists.length > 0">Supplementals</label>
                  <md-checkbox ng-repeat="activeList in activeLists" class="md-primary md-hue-2 no-bottom-margin"
                               ng-init="seqNo = activeList['sequenceNumber']; selected = activeListFilter[seqNo]"
                               ng-model="selected" ng-change="activeListFilter[seqNo] = selected"
                               ng-disabled="activeLists.length<2">
                    <span class="text-medium bold" ng-if="$first">Original</span>
                    <span class="text-medium bold" ng-if="!$first">Supplemental {{seqNo}}</span>
                    <br/>
                    <span class="text-small">{{activeLists[seqNo].releaseDateTime | moment:'MMM D h:mm A'}}</span>
                  </md-checkbox>
                </div>
              </md-toolbar>
              <div ng-show="displayedEntries.length > 0" layout-padding>
                <calendar-entry-table section-type="active-list" cal-entries="displayedEntries" year="year"
                                      highlight-value="highlightValue"
                                      get-cal-bill-num-url="getCalBillNumUrl"
                                      scroll-to="curr.topListIndex[section]">
                </calendar-entry-table>
              </div>
            </div>
          </md-tab-body>
        </md-tab>

        <!-- Supplemental Calendars -->

        <md-tab md-on-select="setCalendarHeaderText()">
          <md-label>
            Floor ({{calendarView.supplementalCalendars.size + 1}})
          </md-label>
          <md-tab-body>
            <md-divider></md-divider>
            <section ng-if="pageNames[curr.activeIndex] === 'floor'" ng-controller="FloorCalendarCtrl">
              <md-toolbar class="md-toolbar-tools auto-height" style="padding:15px;" ng-if="floorCalVersions.length > 1">
                <label class="text-medium bold margin-right-20">Supplementals</label>
                <div layout-gt-sm="row" layout="column" layout-align="start center" ng-model="amdVersion">
                  <div ng-repeat="version in floorCalVersions |orderBy:versionSortValue" class="margin-right-20">
                    <md-checkbox class="md-primary md-hue-2 no-bottom-margin" ng-model="floorCalFilter[version]"
                                 ng-disabled="floorCalVersions.length<2">
                      <span class="text-medium bold" ng-if="$first">Original</span>
                      <span class="text-medium bold" ng-if="!$first">Supplemental {{version}}</span>
                      <br/>
                      <span class="text-small">{{floorCals[version].releaseDateTime | moment:'MMM D h:mm A'}}</span>
                    </md-checkbox>
                  </div>
                </div>
              </md-toolbar>
              <div layout-padding>
                <toggle-panel ng-repeat="(section, entries) in displayedSections" class="content-card gray4-bg"
                              open="{{curr.openSections[section]}}"
                              label="{{section | sectionDisplayName}} - {{entries.length}} Bills">
                  <calendar-entry-table section-type="{{section}}" cal-entries="entries" year="year" highlight-value="highlightValue"
                                        get-cal-bill-num-url="getCalBillNumUrl" scroll-to="curr.topListIndex[section]">
                  </calendar-entry-table>
                </toggle-panel>
              </div>
            </section>
          </md-tab-body>
        </md-tab>

        <!-- Updates -->
        <md-tab label="Updates" md-on-select="setCalendarHeaderText()">
          <md-tab-body>
            <md-divider></md-divider>
            <div ng-if="pageNames[curr.activeIndex] === 'updates'"
                 ng-controller="CalendarUpdatesCtrl">
              <div layout="row" layout-sm="column" class="padding-20 gray3-bg">
                <div flex>
                  <label>Sort By: </label>
                  <select ng-model="updatesOrder" class="margin-left-10">
                    <option value="DESC">Newest First</option>
                    <option value="ASC">Oldest First</option>
                  </select>
                </div>
                </div>
              <div class="padding-20">
                <md-progress-linear md-mode="indeterminate" ng-if="loadingUpdates"></md-progress-linear>
                <update-list ng-if="!loadingUpdates" update-response="updateResponse" ng-init="showId=false" show-id="showId"></update-list>
              </div>
              </div>
          </md-tab-body>
        </md-tab>
      </md-tabs>
    </div>
    <section ng-if="calendarResponse.success === false">
      <md-card>
        <md-content class="content-card padding-20">
          <h4>Really sorry about that. You probably got to this page because the requested calendar is not
            in our system. Data for senate calendars are available from the 2009 to current session years.</h4>
        </md-content>
      </md-card>
    </section>
  </div>
