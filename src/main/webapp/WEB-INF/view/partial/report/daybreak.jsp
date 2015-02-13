<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="DaybreakCtrl" id="daybreak-page">
    <md-tabs class='md-primary' md-selected="selectedIndex">

        <!-- Summary Tab -->
        <md-tab>
        <md-tab-label>Summaries</md-tab-label>
        <md-content ng-controller="DaybreakSummaryCtrl" style="padding-left: 10px">
            <div>
                <form style="display: inline-block">
                    <h4 style="display: inline-block">
                        <span class="icon-statistics blue-title-icon"></span>
                        Daybreak Reports from&nbsp;
                    </h4>
                    <input type="date" ng-model="inputStartDate">
                    <h4 style="display: inline-block">&nbsp;to&nbsp;</h4>
                    <input type="date" ng-model="inputEndDate">
                    <md-button ng-click="newDateRange()" class="md-primary md-raised" aria-label="change date range">Go</md-button>
                </form>
            </div>
            <table id='daybreak-summary-table' st-table="reportSummaries" class="table table-striped">
                <thead>
                <tr>
                    <th rowspan="2">Report Date/Time</th>
                    <th class="th-section"  colspan="5">Mismatch Statuses</th>
                    <th class="th-section" colspan="20">Mismatch Types</th>
                </tr>
                <tr>
                    <th style="border-left:1px solid #ccc;">Total Open</th>
                    <th>New</th>
                    <th>Regress</th>
                    <th>Existing</th>
                    <th>Resolved</th>
                    <th style="border-left:1px solid #ccc;" colspan="2">Sponsor</th>
                    <th colspan="2">Co-sp</th>
                    <th colspan="2">Multi-sp</th>
                    <th colspan="2">Title</th>
                    <th colspan="2">Law/Sum</th>
                    <th colspan="2">Action</th>
                    <th colspan="2">Page</th>
                    <th colspan="2">Publish</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="summary in reportSummaries">
                    <td>
                        <a href="#" ng-click="openReportDetail(summary.reportDateTime.toString())">
                            {{summary.reportDateTime | moment:'lll'}}
                        </a>
                    </td>
                    <td style="border-left:1px solid #ccc; font-weight:bold">{{summary.openMismatches}}</td>
                    <td>
                        <span class="prefix-icon icon-arrow-up4 new-error"></span>
                        {{ (summary.mismatchStatuses['NEW'] | default:0) }}
                    </td>
                    <td>
                        <span class="prefix-icon icon-arrow-up4 new-error"></span>
                        {{ (summary.mismatchStatuses['REGRESSION'] | default:0) }}
                    </td>
                    <td>
                        <span class="prefix-icon icon-cycle existing-error"></span>
                        {{ summary.mismatchStatuses['EXISTING'] | default:0 }}
                    </td>
                    <td>
                        <span class="prefix-icon icon-arrow-down5 closed-error"></span>
                        {{ summary.mismatchStatuses['RESOLVED'] | default:0 }}
                    </td>
                    <td style="border-left:1px solid #ccc;">
                        {{ computeMismatchCount(summary, 'BILL_SPONSOR') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_SPONSOR')">{{ computeMismatchDiff(summary, 'BILL_SPONSOR', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_COSPONSOR') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_COSPONSOR')">{{ computeMismatchDiff(summary, 'BILL_COSPONSOR', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_MULTISPONSOR') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_MULTISPONSOR')">{{ computeMismatchDiff(summary, 'BILL_MULTISPONSOR', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_TITLE') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_TITLE')">{{ computeMismatchDiff(summary, 'BILL_TITLE', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_LAW_CODE_SUMMARY') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_LAW_CODE_SUMMARY')">{{ computeMismatchDiff(summary, 'BILL_LAW_CODE_SUMMARY', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_ACTION') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_ACTION')">{{ computeMismatchDiff(summary, 'BILL_ACTION', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_FULLTEXT_PAGE_COUNT') }}
                    </td>
                    <td class="delta-column">
                        <span ng-class="mismatchDiffClass(summary,'BILL_FULLTEXT_PAGE_COUNT')">{{ computeMismatchDiff(summary, 'BILL_FULLTEXT_PAGE_COUNT', true) }}</span>
                    </td>
                    <td>
                        {{ computeMismatchCount(summary, 'BILL_AMENDMENT_PUBLISH') }}
                    </td>
                    <td class="delta-column" style="border-right:none;">
                        <span ng-class="mismatchDiffClass(summary,'BILL_AMENDMENT_PUBLISH')">{{ computeMismatchDiff(summary, 'BILL_AMENDMENT_PUBLISH', true) }}</span>
                    </td>
                </tr>
                </tbody>
            </table>
        </md-content>
        </md-tab>

        <!-- Detail Tab -->

        <md-tab ng-show="openReport!=null">
        <md-tab-label>
            {{openReport | moment:'lll'}}
        </md-tab-label>
        <md-content ng-controller="DaybreakDetailCtrl">
            <!--Title-->
            <h4>
                <span class="icon-graph blue-title-icon"></span>
                LBDC {{referenceDateTime | moment:'ll'}} | Report Date: {{reportDateTime | moment:'lll'}}
            </h4>

            <hr style="margin-top:.5em;"/>

            <!--Error summary/filter-->
            <form style="margin-bottom: 5px">
                <md-button ng-init="showMismatchFilter=false" ng-click="showMismatchFilter=!showMismatchFilter"
                           aria-label="show mismatch filter"
                           ng-switch on="showMismatchFilter" class="md-raised">
                    <span ng-switch-when="false"><span class="icon-arrow-right prefix-icon"/>Filter mismatches</span>
                    <span ng-switch-when="true"><span class="icon-arrow-up prefix-icon"/>Hide filter</span>
                </md-button>
                <br/>
                <div ng-show="showMismatchFilter">
                    <div class="row button-group panel minimal" style="margin-bottom:10px;">
                        <check-button btn-class="" ng-model="errorFilter.all" aria-label="Show all mismatches">
                            Total<br/>{{ totals.total }}
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.none" aria-label="Show no mismatches">
                            None<br/>&nbsp;
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.statuses[status]"
                                      ng-repeat="(status, total) in totals.statuses"
                                      aria-label="Show {{status | mismatchStatusLabel}} mismatches">
                            {{status | mismatchStatusLabel}}<br/>{{totals.statuses[status]}}
                        </check-button>
                    </div>

                    <div class="row button-group panel minimal">
                        <check-button btn-class="" ng-model="errorFilter.allTypes" aria-label="Show all mismatch types">
                            All<br/>Types
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.noTypes" aria-label="Show no mismatch types">
                            No<br/>Types
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.types[type]"
                                      ng-repeat="(type, total) in filteredTypeTotals"
                                      aria-label="Show {{type | mismatchTypeLabel}} mismatches">
                            {{type | mismatchTypeLabel}}<br/>{{total}}
                        </check-button>
                    </div>
                </div>

                <div class="row collapse"></div>
            </form>

            <!--Observation Table-->
            <table st-table="displayData" st-safe-src="filteredTableData" class="table table-striped report-detail-table">
                <thead>
                <tr>
                    <td colspan="5">
                        <span st-pagination="" st-template="paginationTemplate" st-items-by-page="resultsPerPage"
                              st-displayed-pages="5"></span>
                        <span class="rpp-selector">
                            Displayed:&nbsp;&nbsp;
                            <md-button ng-repeat="number in rppOptions" ng-bind="number" ng-click="setRpp(number)"
                                       aria-label="Display {{number}} mismatches per page"
                                       class="md-raised" ng-class='{"md-primary": resultsPerPage === number}'></md-button>
                        </span>
                    </td>
                </tr>
                <tr>
                    <th st-sort="printNo">Bill Id</th>
                    <th st-sort="type">Mismatch Type</th>
                    <th st-sort="status">Status</th>
                    <th st-sort="firstOpened">Opened At</th>
                    <th>Snippet</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="row in displayData">
                        <td style="width: 100px;">
                            <a ng-href="{{getBillLink(row.printNo)}}" target="_blank">{{row.printNo}}</a>
                        </td>
                        <td>{{row.type | mismatchTypeLabel}}</td>
                        <td>{{row.status | mismatchStatusLabel}}</td>
                        <td>
                            <a href="#" ng-click="openReportDetail(row.firstOpened)"
                               ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                                {{row.firstOpened | moment:'lll'}}
                            </a>
                            <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                                {{row.firstOpened | moment:'lll'}}
                            </span>
                        </td>
                        <td><div class="report-table-snippet"><mismatch-diff diff="row.diff"/></div></td>
                        <td><a href='#' ng-click='openDetailWindow(row.mismatchId)'>Details</a></td>
                    </tr>
                </tbody>
                <tfoot>
                </tfoot>
            </table>
        </md-content>
        </md-tab>
    </md-tabs>
</section>

<!-- Pagination Template -->
<script type="text/ng-template" id="paginationTemplate">
    <span class="pagination" ng-if="pages.length > 1">
        <md-button ng-click="selectPage(currentPage - 1)" aria-label="previous page" class="md-raised">&laquo;</md-button>
        <md-button ng-click="selectPage(currentPage + 1)" aria-label="next page" class="md-raised">&raquo;</md-button>
        <md-button ng-click="selectPage(1)" aria-label="1st page"
                   class="md-raised" ng-class='{"md-primary": currentPage === 1}'>1</md-button>
        <md-button ng-repeat="page in pages" ng-bind="page" ng-if="page > 1 && page < numPages"
                   aria-label="{{page | ordinalSuffix}} page"
                   ng-click="selectPage(page)" class="md-raised" ng-class='{"md-primary": currentPage === page}'></md-button>
        <md-button ng-click="selectPage(numPages)" ng-bind="numPages"
                   aria-label="{{numPages | ordinalSuffix}} page"
                   class="md-raised" ng-class='{"md-primary": currentPage === numPages}'></md-button>
    </span>
</script>

<!-- Detail Template -->
<script type="text/ng-template" id="mismatchDetailWindow">
    <md-dialog aria-label="">
    <md-content class="md-padding mismatch-dialog">
        <div>
            <h5 style="display: inline-block">
                Bill: {{printNo}}<br/>
                Mismatch: {{currentMismatch.mismatchType | mismatchTypeLabel}}<br/>
                Status: {{currentMismatch.status | mismatchStatusLabel}}
            </h5>
            <h5 style="display: inline-block; text-align: right">
                Opened on: {{firstOpened.reportDateTime | moment:'lll'}}<br/>
                First Reference: {{firstOpened.referenceDateTime | moment:'lll'}}<br/>
                Current Reference: {{observation.refDateTime | moment:'lll'}}
            </h5>
        </div>
        <md-tabs class="mismatch-dialog-tabs">
            <md-tab label="DIFF">
                <md-content>
                    <mismatch-diff diff="currentMismatch.diff"></mismatch-diff>
                </md-content>
            </md-tab>
            <md-tab label="LBDC"><md-content ng-bind="currentMismatch.referenceData"></md-content></md-tab>
            <md-tab label="Openleg"><md-content ng-bind="currentMismatch.observedData"></md-content></md-tab>
            <md-tab label="Prior Occurrences">
                <%--<accordion>--%>
                <%--<accordion-group ng-repeat="priorMismatch in currentMismatch.prior.items"--%>
                <%--label="REPORT: {{formatReportDate(priorMismatch.reportId.reportDateTime)}} DAYBREAK: {{formatReferenceDate(priorMismatch.reportId.referenceDateTime)}} STATUS: {{getLabel('statuses', priorMismatch.status)}}">--%>
                <%--<mismatch-diff diff="priorMismatch.diff"></mismatch-diff>--%>
                <%--</accordion-group>--%>
                <%--</accordion>--%>
            </md-tab>
            <md-tab label="Other Mismatches">
                <md-content>
                <ul ng-show="allMismatches.length > 1">
                    <li ng-repeat="mismatch in allMismatches">
                                <span ng-show="mismatch.mismatchType==currentMismatch.mismatchType">
                                    {{mismatch.mismatchType | mismatchTypeLabel}} - {{mismatch.status | mismatchStatusLabel}}
                                </span>
                        <a href="#" ng-show="mismatch.mismatchType!=currentMismatch.mismatchType"
                           ng-click="openNewDetail(getMismatchId(observation, mismatch))">
                            {{mismatch.mismatchType | mismatchTypeLabel}} - {{mismatch.status | mismatchStatusLabel}}
                        </a>
                    </li>
                </ul>
                <md-content>
            </md-tab>
        </md-tabs>
    </md-content>
    </md-dialog>
</script>