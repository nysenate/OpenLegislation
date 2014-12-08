<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="CalendarViewCtrl">
    <!-- Search Bar -->
    <open-component:bill-search-bar/>

    <div accordion id="calendar-view-body">

        <!-- Calendar Date Picker -->
        <div accordion-group class="calendar-accordion calendar-date-picker-accordion"
             ng-class="{'calendar-accordion-open': isCalendarOpen}" is-open="$parent.isCalendarOpen">
            <div accordion-heading>
                <div class="calendar-date-picker-heading">
                    <span class="icon-calendar"></span>
                    Calendar &#35;{{calendarView.calendarNumber}}: {{calendarView.calDate | moment:'MMMM D, YYYY'}}
                </div>
            </div>
            <div id="calendar-date-picker" ui-calendar="calendarConfig" ng-model="eventSources"></div>
        </div>

        <!-- Active Lists -->
        <div accordion-group class="calendar-accordion" ng-repeat="activeList in activeLists"
             ng-class="{'calendar-accordion-open': isOpen}" is-open="isOpen">
            <div accordion-heading>
                <div class="row">
                    <div ng-switch on="activeList.sequenceNumber" class="small-5 column">
                        <div ng-switch-when="0">Active List</div>
                        <div ng-switch-default>Supplemental Active List {{activeList.sequenceNumber}}</div>
                    </div>
                    <div class="calendar-release-date small-7 column">
                        released&nbsp;
                        <span ng-bind="activeList.releaseDateTime | moment:'h:mm A MMMM D, YYYY'"></span>
                    </div>
                </div>
            </div>
            <div calendar-entry-table cal-entries="activeList.entries.items"></div>
        </div>

        <!-- Supplemental Calendars -->
        <div accordion-group class="calendar-accordion" ng-class="{'calendar-accordion-open': isOpen}"
             ng-repeat="supCal in supplementalCals" is-open="isOpen">
            <div accordion-heading>
                <div class="row">
                    <div ng-switch on="supCal.version">
                        <div ng-switch-when="" class="small-5 column">Floor Calendar</div>
                        <div ng-switch-default class="small-5 column">Supplemental Calendar {{supCal.version}}</div>
                    </div>
                    <div class="calendar-release-date small-7 column">
                        released&nbsp;
                        <span ng-bind="supCal.releaseDateTime | moment:'h:mm A MMMM D, YYYY'"></span>
                    </div>
                </div>
            </div>
            <div accordion>
                <div accordion-group class="calendar-accordion calendar-accordion-nested"
                     is-open="isOpen" ng-class="{'calendar-accordion-open': isOpen}"
                     ng-repeat="section in supCal.entriesBySection.items | orderBySection">
                    <div accordion-heading>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        {{section.items[0].sectionType | sectionDisplayName}}
                    </div>
                    <div calendar-entry-table cal-entries="section.items"></div>
                </div>
            </div>
        </div>
    </div>
</section>
