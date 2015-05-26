<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="CalendarViewCtrl" ng-init="setHeaderVisible(true)">

  <section ng-if="calendarResponse.success === true">
    <md-tabs md-selected="curr.activeIndex" md-dynamic-height="false">
      <!-- Back to search -->
      <md-tab md-on-select="backToSearch()">
        <md-tab-label ng-if="previousPage == 'search'"><i class="icon-magnifying-glass prefix-icon2"></i>back to search</md-tab-label>
        <md-tab-label ng-if="previousPage == 'browse'"><i class="icon-calendar prefix-icon2"></i>back to browse</md-tab-label>
        <md-tab-label ng-if="previousPage == 'updates'"><i class="icon-flag prefix-icon2"></i>back to updates</md-tab-label>
        <md-tab-label ng-if="!previousPage"><i class="icon-calendar prefix-icon2"></i>browse</md-tab-label>
      </md-tab>

      <!-- Active Lists -->
      <md-tab label="Active List" md-on-select="setCalendarHeaderText()" ng-disabled="calendarView.activeLists.size < 1">
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="pageNames[curr.activeIndex] === 'active-list'" ng-controller="CalendarActiveListCtrl">
            <md-toolbar class="md-toolbar-tools md-tall md-hue-2 supplemental-toolbar" ng-if="activeLists.length > 1">
            <span ng-show="activeLists.length > 0" class="text-medium bold margin-right-20">Supplementals</span>
            <span layout="row" layout-sm="column">
              <md-checkbox ng-repeat="activeList in activeLists" class="md-accent md-hue-1"
                           ng-init="seqNo = activeList['sequenceNumber']; selected = activeListFilter[seqNo]"
                           ng-model="selected" ng-change="activeListFilter[seqNo] = selected"
                           ng-disabled="activeLists.length<2">
                <span class="text-medium bold" ng-if="$first">Original</span>
                <span class="text-medium bold" ng-if="!$first">Supplemental {{seqNo}}</span>
                <br/>
                <span class="text-small">{{activeLists[seqNo].releaseDateTime | moment:'MMM D h:mm A'}}</span>
              </md-checkbox>
            </span>
            </md-toolbar>
            <md-content class="no-background">
              <md-card ng-show="displayedEntries.length > 0" class="content-card">
                <md-card-content>
                  <calendar-entry-table section-type="active-list" cal-entries="displayedEntries" year="year"
                                        highlight-value="highlightValue"
                                        get-cal-bill-num-url="getCalBillNumUrl"></calendar-entry-table>
                </md-card-content>
              </md-card>
            </md-content>
          </section>
        </md-tab-body>
      </md-tab>

      <!-- Supplemental Calendars -->
      <md-tab label="Floor" md-on-select="setCalendarHeaderText()">
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="pageNames[curr.activeIndex] === 'floor'" ng-controller="FloorCalendarCtrl">
            <md-toolbar class="md-toolbar-tools md-tall md-hue-2 supplemental-toolbar" ng-if="floorCalVersions.length > 1">
              <label class="text-medium bold margin-right-20">Supplementals</label>
              <div layout="row" layout-sm="column" ng-model="amdVersion">
                <md-checkbox ng-repeat="version in floorCalVersions |orderBy:versionSortValue"
                             class="md-accent md-hue-2" ng-model="floorCalFilter[version]"
                             ng-disabled="floorCalVersions.length<2">
                  <span class="text-medium bold" ng-if="$first">Original</span>
                  <span class="text-medium bold" ng-if="!$first">Supplemental {{version}}</span>
                  <br/>
                  <span class="text-small">{{floorCals[version].releaseDateTime | moment:'MMM D h:mm A'}}</span>
                </md-checkbox>
              </div>
            </md-toolbar>
            <md-content class="no-background">
              <toggle-panel ng-repeat="(section, entries) in displayedSections" class="content-card"
                            open="{{openSections[section]}}"
                            label="{{section | sectionDisplayName}} - {{entries.length}} Bills">
                <calendar-entry-table section-type="{{section}}" cal-entries="entries" year="year" highlight-value="highlightValue"
                                      get-cal-bill-num-url="getCalBillNumUrl">
                </calendar-entry-table>
              </toggle-panel>
            </md-content>
          </section>
        </md-tab-body>
      </md-tab>

      <md-tab label="Updates" md-on-select="setCalendarHeaderText()">
        <md-tab-body>
          <md-divider></md-divider>
          <section ng-if="pageNames[curr.activeIndex] === 'updates'" ng-controller="CalendarUpdatesCtrl">
            <md-card class="content-card">
              <md-content layout="row" layout-sm="column">
                <div flex>
                  <label>Sort By: </label>
                  <select ng-model="updatesOrder" class="margin-left-10">
                    <option value="DESC">Newest First</option>
                    <option value="ASC">Oldest First</option>
                  </select>
                </div>
              </md-content>
            </md-card>
            <md-progress-linear md-mode="indeterminate" ng-if="loadingUpdates"></md-progress-linear>
            <update-list ng-if="!loadingUpdates" update-response="updateResponse" ng-init="showId=false" show-id="showId"></update-list>
          </section>
        </md-tab-body>
      </md-tab>
    </md-tabs>
  </section>
  <section ng-if="calendarResponse.success === false">
    <md-card>
      <md-content class="content-card padding-20">
        <h4>Really sorry about that. You probably got to this page because the requested calendar is not
          in our system. Data for senate calendars are available from the 2009 to current session years.</h4>
      </md-content>
    </md-card>
  </section>
</section>
