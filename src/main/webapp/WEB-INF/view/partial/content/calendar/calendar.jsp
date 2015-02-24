<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="CalendarPageCtrl" ng-init="setHeaderVisible(true)">

    <md-tabs class="md-primary" md-selected="activeIndex">

        <md-tab md-on-select="setCalendarHeaderText()">
            <md-tab-label>
                <i class="icon-search prefix-icon2"></i>Search
            </md-tab-label>
        </md-tab>

        <!-- Calendar Date Picker -->
        <md-tab md-on-select="setCalendarHeaderText()">
            <md-tab-label>
                <i class="icon-calendar prefix-icon2"></i>Browse
            </md-tab-label>
            <section ng-controller="CalendarPickCtrl">
                <div id="calendar-date-picker" ui-calendar="calendarConfig" ng-model="eventSources"></div>
            </section>
        </md-tab>

        <!-- Active Lists -->
        <md-tab label="Active List" md-on-select="setCalendarHeaderText()">
        <section ng-controller="CalendarActiveListCtrl">
            <md-toolbar class="md-toolbar-tools auto-height">
                <label ng-show="activeLists.length > 0" class="margin-right-20">Supplementals</label>
                <h4 class="md-toolbar-tools" ng-show="activeLists.length == 0">
                    This calendar does not contain active lists
                </h4>
                <span layout="row" layout-sm="column">
                    <md-checkbox ng-repeat="(seqNo, selected) in activeListFilter" class="md-accent md-hue-1"
                                 ng-model="selected" ng-change="activeListFilter[seqNo] = selected">
                        <span ng-if="$first">Original</span>
                        <span ng-if="!$first">Supplemental {{seqNo}}</span>
                        <br/>
                        <small>{{activeLists[seqNo].releaseDateTime | moment:'MMM D h:mm A'}}</small>
                    </md-checkbox>
                </span>
            </md-toolbar>
            <md-content>
                <md-card ng-show="displayedEntries.length>0">
                    <md-card-content>
                        <calendar-entry-table cal-entries="displayedEntries"></calendar-entry-table>
                    </md-card-content>
                </md-card>
            </md-content>
        </section>
        </md-tab>

        <!-- Supplemental Calendars -->
        <md-tab label="Floor" md-on-select="setCalendarHeaderText()">
        <section ng-controller="FloorCalendarCtrl">
            <md-toolbar class="md-toolbar-tools auto-height">
                <label class="margin-right-20">Supplementals</label>
                    <span layout="row" layout-sm="column" ng-model="curr.amdVersion">
                        <md-checkbox ng-repeat="version in floorCalVersions |orderBy:versionSortValue"
                                     class="md-accent md-hue-1"
                                     ng-model="floorCalFilter[version]">
                            <span ng-if="$first">Original</span>
                            <span ng-if="!$first">Supplemental {{version}}</span>
                            <br/>
                            <small>{{floorCals[version].releaseDateTime | moment:'MMM D h:mm A'}}</small>
                        </md-checkbox>
                    </span>
            </md-toolbar>
            <md-content>
                <toggle-panel ng-repeat="(section, entries) in displayedSections"
                              label="{{section | sectionDisplayName}}" show-tip="true">
                    <calendar-entry-table cal-entries="entries"></calendar-entry-table>
                </toggle-panel>
            </md-content>
        </section>
        </md-tab>

        <md-tab label="Updates" md-on-select="setCalendarHeaderText()"></md-tab>
    </md-tabs>
</section>
