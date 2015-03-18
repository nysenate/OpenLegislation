<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section class="content-section" ng-controller="CalendarSearchPageCtrl" ng-init="setHeaderVisible(true)">
  <md-tabs class="md-primary" md-selected="activeIndex">
    <md-tab md-on-select="setCalendarHeaderText()">
      <md-tab-label>
        <i class="icon-search prefix-icon2"></i>Search
      </md-tab-label>
      <section class="margin-top-10" ng-if="pageNames[activeIndex] === 'search'" ng-controller="CalendarSearchCtrl">
        <form name="calendar-search-form">
          <md-content class="padding-20">
            <md-input-container class="md-primary">
              <label><i class="prefix-icon2 icon-search"></i>Search for calendars</label>
              <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                  ng-model="searchTerm" ng-model-options="{debounce: 300}" ng-change="termSearch(true)">
            </md-input-container>
          </md-content>
          <md-divider></md-divider>
          <md-subheader ng-show="searched && searchTerm && pagination.totalItems === 0"
              class="margin-10 md-warn md-whiteframe-z0">
            <h4>No search results were found for '{{searchTerm}}'</h4>
          </md-subheader>
        </form>
        <section ng-show="searched && pagination.totalItems > 0">
          <md-card class="content-card">
            <div class="subheader" layout="row" layout-sm="column" layout-align="space-between center">
              <div flex>
                {{pagination.totalItems}} calendars were matched.&nbsp;&nbsp;
                Viewing page {{pagination.currPage}} of {{pagination.lastPage}}.
              </div>
              <div flex style="text-align: right;"><dir-pagination-controls pagination-id="33" boundary-links="true"></dir-pagination-controls></div>
            </div>
            <md-content class="no-top-margin">
              <md-list>
                <a dir-paginate="r in searchResults | itemsPerPage: 6"
                   total-items="searchResponse.total" current-page="pagination.currPage"
                   ng-init="cal = r.result" class="result-link" pagination-id="33"
                   ng-href="{{getCalendarUrl(cal.year, cal.calendarNumber)}}"
                   ng-click="changeTab(cal.activeLists.size>0 ? 'active-list' : 'floor')">
                  <md-item>
                    <md-item-content layout-sm="column" layout-align-sm="center start"
                        style="cursor: pointer;" >
                      <div layout-sm="row" layout-align-sm="start end" style="width:180px;padding:16px;">
                        <h3 class="no-margin">
                          {{cal.year}} &#35;{{cal.calendarNumber}}
                          <span hide-gt-sm>&nbsp;</span>
                        </h3>
                        <h5 class="no-margin">
                          {{cal.calDate | moment:'MMMM D'}}
                        </h5>
                      </div>
                      <div class="md-tile-content" layout="column">
                        <div layout-gt-sm="row" layout-align="start end">
                          <h5 class="no-margin" style="width: 200px">
                            {{cal.activeLists.size}} Active List Supplementals
                          </h5>
                          <h6 class="no-margin">
                            {{getTotalActiveListBills(cal)}} Total Active List Bills
                          </h6>
                        </div>
                        <div layout-gt-sm="row" layout-align="start end">
                          <h5 class="no-margin" style="width: 200px">
                            {{(cal.floorCalendar.year ? 1 : 0) + cal.supplementalCalendars.size}}
                            Floor Supplementals
                          </h5>
                          <h6 class="no-margin">
                            {{getTotalFloorBills(cal)}} Total Floor Bills
                          </h6>
                        </div>
                      </div>
                    </md-item-content>
                  </md-item>
                  <md-divider hide-gt-sm ng-hide="$last"></md-divider>
                </a>
              </md-list>
            </md-content>
          </md-card>
        </section>
      </section>
      <section>
        <md-card class="content-card">
          <md-subheader><strong>Quick search for Calendars</strong></md-subheader>
          <div class="padding-20">
            <p class="text-medium">Calendars are uniquely identified by their year and a calendar number, which corresponds
              to their order within a year.  To find a specific calendar, enter its year and calendar number e.g.
              <code>2015#5</code>.
            </p>
          </div>
        </md-card>
        <md-card class="content-card">
          <md-subheader><strong>Advanced Search Guide</strong></md-subheader>
          <div class="padding-20">
            <p class="text-medium">You can combine the field definitions documented below to perform targeted searches.
              You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
              as well as parenthesis for grouping. For more information refer to the
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
        </md-card>
      </section>
    </md-tab>

    <!-- Calendar Date Picker -->
    <md-tab md-on-select="setCalendarHeaderText(); renderCalendarEvent()">
      <md-tab-label>
        <i class="icon-calendar prefix-icon2"></i>Browse
      </md-tab-label>
      <md-card ng-if="pageNames[activeIndex] === 'browse'" ng-controller="CalendarPickCtrl" class="content-card">
        <md-card-content id="calendar-date-picker" ui-calendar="calendarConfig" ng-model="eventSources"></md-card-content>
      </md-card>
    </md-tab>

    <!-- Calendar Updates -->
    <md-tab md-on-select="setCalendarHeaderText()">
      <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
      <section ng-if="pageNames[activeIndex] === 'updates'" ng-controller="CalendarFullUpdatesCtrl">
        <md-card class="content-card">
          <md-card-content>
            <div layout="row">
              <label class="margin-right-10">Calendar updates from</label>
              <input type="datetime-local" ng-model="updateOptions.fromDateTime" class="margin-right-10">
              <label class="margin-right-10">to</label>
              <input type="datetime-local" ng-model="updateOptions.toDateTime" class="margin-right-10">
            </div>
            <div layout="row" layout-align="start center">
              <label class="margin-right-10">Using</label>
              <md-select ng-model="updateOptions.type" class="no-top-margin margin-right-20">
                <md-select-label>{{updateOptions.type=="processed" ? "Processed Date" : "Published Date"}}</md-select-label>
                <md-option value="processed">Processed Date</md-option>
                <md-option value="published">Published Date</md-option>
              </md-select>
              <md-select ng-model="updateOptions.order" class="no-top-margin margin-right-20">
                <md-select-label>{{updateOptions.order=="ASC" ? "Oldest First" : "Newest First"}}</md-select-label>
                <md-option value="ASC">Oldest First</md-option>
                <md-option value="DESC">Newest First</md-option>
              </md-select>
              <md-checkbox class="md-accent md-hue-1" ng-model="updateOptions.detail" aria-label="detaail">Detail</md-checkbox>
            </div>
          </md-card-content>
        </md-card>
        <md-progress-linear md-mode="indeterminate" ng-show="loadingUpdates"></md-progress-linear>
        <update-list class="error-toast-parent" ng-show="!loadingUpdates"
                     update-response="updateResponse" pagination="pagination" show-details="updateOptions.detail"></update-list>
      </section>
    </md-tab>
  </md-tabs>
</section>
