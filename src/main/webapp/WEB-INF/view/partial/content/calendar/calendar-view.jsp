<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="CalendarViewCtrl" ng-init="setHeaderVisible(true)">

  <section ng-if="calendarResponse.success === true">
    <md-tabs class="md-primary" md-selected="curr.activeIndex">

      <!-- Back to search -->
      <md-tab md-on-select="backToSearch()">
        <md-tab-label ng-if="previousPage == 'search'"><i class="icon-search prefix-icon2"></i>back to search</md-tab-label>
        <md-tab-label ng-if="previousPage == 'browse'"><i class="icon-calendar prefix-icon2"></i>back to browse</md-tab-label>
        <md-tab-label ng-if="previousPage == 'updates'"><i class="icon-flag prefix-icon2"></i>back to updates</md-tab-label>
        <md-tab-label ng-if="!previousPage"><i class="icon-calendar prefix-icon2"></i>browse</md-tab-label>
      </md-tab>

      <!-- Active Lists -->
      <md-tab label="Active List" md-on-select="setCalendarHeaderText()" ng-disabled="calendarView.activeLists.size < 1">
        <section ng-if="pageNames[curr.activeIndex] === 'active-list'" ng-controller="CalendarActiveListCtrl">
          <md-toolbar class="md-toolbar-tools supplemental-toolbar">
            <label ng-show="activeLists.length > 0" class="margin-right-20">Supplementals</label>
            <span layout="row" layout-sm="column">
              <md-checkbox ng-repeat="activeList in activeLists" class="md-accent md-hue-1"
                           ng-init="seqNo = activeList['sequenceNumber']; selected = activeListFilter[seqNo]"
                           ng-model="selected" ng-change="activeListFilter[seqNo] = selected"
                           ng-disabled="activeLists.length<2">
                <span ng-if="$first">Original</span>
                <span ng-if="!$first">Supplemental {{seqNo}}</span>
                <br/>
                <small>{{activeLists[seqNo].releaseDateTime | moment:'MMM D h:mm A'}}</small>
              </md-checkbox>
            </span>
          </md-toolbar>
          <md-content class="no-background">
            <md-card ng-show="displayedEntries.length>0" style="background: #fff">
              <md-card-content>
                <calendar-entry-table cal-entries="displayedEntries" get-cal-bill-num-url="getCalBillNumUrl"></calendar-entry-table>
              </md-card-content>
            </md-card>
          </md-content>
        </section>
      </md-tab>

      <!-- Supplemental Calendars -->
      <md-tab label="Floor" md-on-select="setCalendarHeaderText()">
        <section ng-if="pageNames[curr.activeIndex] === 'floor'" ng-controller="FloorCalendarCtrl">
          <md-toolbar class="md-toolbar-tools supplemental-toolbar">
            <label class="margin-right-20">Supplementals</label>
              <span layout="row" layout-sm="column" ng-model="amdVersion">
                <md-checkbox ng-repeat="version in floorCalVersions |orderBy:versionSortValue"
                             class="md-accent md-hue-1" ng-model="floorCalFilter[version]"
                             ng-disabled="floorCalVersions.length<2">
                  <span ng-if="$first">Original</span>
                  <span ng-if="!$first">Supplemental {{version}}</span>
                  <br/>
                  <small>{{floorCals[version].releaseDateTime | moment:'MMM D h:mm A'}}</small>
                </md-checkbox>
              </span>
          </md-toolbar>
          <md-content class="no-background">
            <toggle-panel ng-repeat="(section, entries) in displayedSections"
                          open="{{openSections[section]}}"
                          label="{{section | sectionDisplayName}}" show-tip="true">
              <calendar-entry-table cal-entries="entries" get-cal-bill-num-url="getCalBillNumUrl"></calendar-entry-table>
            </toggle-panel>
          </md-content>
        </section>
      </md-tab>

      <md-tab label="Updates" md-on-select="setCalendarHeaderText()">
        <section ng-if="pageNames[curr.activeIndex] === 'updates'" ng-controller="CalendarUpdatesCtrl">
          <md-toolbar class="md-toolbar-tools supplemental-toolbar">
              <span class="margin-right-10">Update Order:&nbsp;</span>
              <md-select ng-model="updatesOrder" class="no-margin">
                <md-select-label>{{ updatesOrder == 'ASC' ? 'Oldest First' : 'Newest First' }}</md-select-label>
                <md-option value="ASC">Oldest First</md-option>
                <md-option value="DESC">Newest First</md-option>
              </md-select>
          </md-toolbar>
          <md-progress-linear md-mode="indeterminate" ng-if="loadingUpdates"></md-progress-linear>
          <update-list ng-if="!loadingUpdates" update-response="updateResponse" ng-init="showId=false" show-id="showId"></update-list>
        </section>
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
