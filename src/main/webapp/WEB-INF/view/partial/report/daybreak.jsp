<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="DaybreakCtrl">
    <div style="text-align: center" ng-bind="selectedIndex"></div>
    <md-tabs md-selected="selectedIndex">
        <md-tab ng-repeat="tab in tabs">
        <md-tab-label ng-if="tab.type=='summary'">Summaries</md-tab-label>
        <section ng-if="tab.type=='summary'" ng-controller="DaybreakSummaryCtrl" style="padding-left: 10px">
            <h4>
                <span class="icon-statistics blue-title-icon"></span>
                Daybreak Reports: {{rsStartDate | moment:'lll'}} - {{rsEndDate | moment:'lll'}}
            </h4>
            <table id='report-summary-table' st-table="reportSummaries" class="table table-striped">
                <thead>
                <tr>
                    <th rowspan="2">Report Date/Time</th>
                    <th class="th-section"  colspan="5">Mismatch Statuses</th>
                    <th class="th-section" colspan="12">Mismatch Types</th>
                </tr>
                <tr>
                    <th style="border-left:1px solid #ccc;">Total Open</th>
                    <th>New</th>
                    <th>Re-opened</th>
                    <th>Existing</th>
                    <th>Resolved</th>
                    <th style="border-left:1px solid #ccc;" colspan="2">Sponsor</th>
                    <th colspan="2">Co-sp</th>
                    <th colspan="2">Multi-sp</th>
                    <th colspan="2">Title</th>
                    <th colspan="2">Law/ Summary</th>
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
        </section>
        <md-tab-label ng-if="tab.type=='detail'">
            {{tab.reportDateTime | moment:'lll'}}&nbsp;&nbsp;
            <button ng-click="closeReportDetail(tab.reportDateTime)">x</button>
        </md-tab-label>
        <section ng-if="tab.type=='detail'" ng-controller="DaybreakDetailCtrl" ng-init="init(tab.reportDateTime)">
            <!--Title-->
            <div class="row">
                <div class="small-12 columns">
                    <h4>
                        <span class="icon-graph blue-title-icon"></span>
                        LBDC {{referenceDateTime | moment:'ll'}} | Report Date: {{reportDateTime | moment:'lll'}}</h4>
                </div>
            </div>

            <hr style="margin-top:.5em;"/>

            <!--Error summary/filter-->
            <form>
                <a ng-init="showMismatchFilter=false" ng-click="showMismatchFilter=!showMismatchFilter" ng-switch on="showMismatchFilter">
                    <span ng-switch-when="false"><span class="icon-arrow-right prefix-icon"/>Filter mismatches</span>
                    <span ng-switch-when="true"><span class="icon-arrow-up prefix-icon"/>Hide filter</span>
                </a>
                <br/>
                <div ng-show="showMismatchFilter">
                    <div class="row button-group panel minimal" style="margin-bottom:10px;">
                        <check-button btn-class="" ng-model="errorFilter.all">
                            Total<br/>{{ totals.total }}
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.none">
                            None<br/>&nbsp;
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.statuses[status]"
                                      ng-repeat="(status, total) in totals.statuses">
                            {{status | mismatchStatusLabel}}<br/>{{totals.statuses[status]}}
                        </check-button>
                    </div>

                    <div class="row button-group panel minimal">
                        <check-button btn-class="" ng-model="errorFilter.allTypes">
                            All<br/>Types
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.noTypes">
                            No<br/>Types
                        </check-button>
                        <check-button btn-class="" ng-model="errorFilter.types[type]"
                                      ng-repeat="(type, total) in filteredTypeTotals">
                            {{type | mismatchTypeLabel}}<br/>{{total}}
                        </check-button>
                    </div>
                </div>

                <div class="row collapse"></div>
            </form>

            <!--Observation Table-->
            <div class="row" id="report-detail-container">
                <!-- Table -->
                <table st-table="tableParams" class="table table-striped report-detail-table">
                    <thead>
                    <tr>
                        <th>Bill Id</th>
                        <th>Mismatch Type</th>
                        <th>Status</th>
                        <th>Opened At</th>
                        <th>Snippet</th>
                        <th>Details</th>
                    </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="row in filteredTableData">
                            <td sortable="'printNo'" style="width: 100px;">
                                <a ng-href="{{getBillLink(row.printNo)}}" target="_blank">{{row.printNo}}</a>
                            </td>
                            <td sortable="'type'">{{row.type | mismatchTypeLabel}}</td>
                            <td sortable="'status'">{{row.status | mismatchStatusLabel}}</td>
                            <td sortable="'firstOpened'">
                                <a href="#" ng-click="openReportDetail(row.firstOpened)"
                                   ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                                    {{row.firstOpened | moment:'lll'}}
                                </a>
                                <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                                    {{row.firstOpened | moment:'lll'}}
                                </span>
                            </td>
                            <td><div class="report-table-snippet"><mismatch-diff diff="row.diff"/></div></td>
                            <td><a href='#' ng-click='showDetailModal(row.mismatchId, "diff")'>Details</a></td>
                        </tr>
                    </tbody>
                </table>
                <!-- Paginator -->
                <script type="text/ng-template" id="custom/pager">
                    <ul class="pagination ng-cloak">
                        <li ng-repeat="page in pages"
                            ng-class="{'disabled': !page.active}"
                            ng-show="params.count() > 0"
                            ng-switch="page.type">
                            <a ng-switch-when="prev" ng-click="params.page(page.number)" href="">&laquo; Previous</a>
                            <a ng-switch-when="first" ng-click="params.page(1)" href="">
                                <span ng-bind="page.number" ng-class="{'current-page': page.number==params.page()}"></span>
                            </a>
                            <a ng-switch-when="page" ng-click="params.page(page.number)" ng-show="page.number==params.page()" href="">
                                <span ng-bind="page.number" class="current-page"></span>
                            </a>
                            <a ng-switch-when="last" ng-click="params.page(page.number)" href="">
                                <span ng-bind="page.number" ng-class="{'current-page': page.number==params.page()}"></span>
                            </a>
                            <a ng-switch-when="next" ng-click="params.page(page.number)" href="">Next &raquo;</a>
                        </li>
                        <div class="button-group round" id="pagination-button-group">
                            <button type="button" ng-class="{'success':params.count() == 10}" ng-click="params.count(10)" class="button tiny">10</button>
                            <button type="button" ng-class="{'success':params.count() == 25}" ng-click="params.count(25)" class="button tiny">25</button>
                            <button type="button" ng-class="{'success':params.count() == 50}" ng-click="params.count(50)" class="button tiny">50</button>
                            <button type="button" ng-class="{'success':params.count() == 100}" ng-click="params.count(100)" class="button tiny">100</button>
                            <button type="button" ng-class="{'success':params.count() == 0}" ng-click="params.count(0)" class="button tiny">All: {{params.total()}}</button>
                        </div>
                    </ul>
                </script>
            </div>
        </section>
        </md-tab>
    </md-tabs>
</section>