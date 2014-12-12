<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<shiro:authenticated>
<a href="${ctxPath}/admin/report/daybreak"><span class="prefix-icon icon-arrow-left2"></span>Summary List</a>
<hr class="no-top-margin"/>

<!--Title-->
<div class="row">
    <div class="small-12 columns">
        <h4><span class="icon-graph blue-title-icon"></span>LBDC <span ng-bind="titleReferenceDate"></span> | Report Date: <span ng-bind="titleReportDate"></span></h4>
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
            <check-button btn-class="button small" ng-model="errorFilter.all">
                Total<br/>{{ totals.total }}
            </check-button>
            <check-button btn-class="button small" ng-model="errorFilter.none">
                None<br/>&nbsp;
            </check-button>
            <check-button btn-class="button small" ng-model="errorFilter.statuses[status]"
                          ng-repeat="(status, total) in totals.statuses">
                {{getLabel('statuses', status)}}<br/>{{totals.statuses[status]}}
            </check-button>
        </div>

        <div class="row button-group panel minimal">
            <check-button btn-class="button small" ng-model="errorFilter.allTypes">
                All<br/>Types
            </check-button>
            <check-button btn-class="button small" ng-model="errorFilter.noTypes">
                No<br/>Types
            </check-button>
            <check-button btn-class="button small" ng-model="errorFilter.types[type]"
                          ng-repeat="(type, total) in filteredTypeTotals">
                {{getLabel('types', type)}}<br/>{{total}}
            </check-button>
        </div>
    </div>

    <div class="row collapse"></div>
</form>

<!--Observation Table-->
<div class="row" id="report-detail-container">
    <!-- Table -->
    <table ng-table="tableParams" id="report-detail-table" template-pagination="custom/pager" class="table">
        <tr ng-repeat="row in $data">
            <td data-title="'Bill Id'" sortable="'printNo'" style="width: 100px;">{{row.printNo}}</td>
            <td data-title="'Mismatch Type'" sortable="'type'">{{getLabel('types', row.type)}}</td>
            <td data-title="'Status'" sortable="'status'">{{getLabel('statuses', row.status)}}</td>
            <td data-title="'Opened At'" sortable="'firstOpened'">
                <a ng-href="${ctxPath}/admin/report/daybreak/{{row.firstOpened}}"
                   ng-show="row.firstOpened!=reportDateTime && row.firstOpened!='Unknown'">
                    {{formatReportDate(row.firstOpened)}}
                </a>
                <span ng-show="row.firstOpened==reportDateTime || row.firstOpened=='Unknown'">
                    {{formatReportDate(row.firstOpened)}}
                </span>
            </td>
            <td data-title="'Snippet'"><div class="report-table-snippet"><mismatch-diff diff="row.diff"/></div></td>
            <td data-title="'Details'"><a href='#' ng-click='showDetailModal(row.mismatchId, "diff")'>Details</a></td>
        </tr>
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

<!-- Details Modal Template -->
<script type="text/ng-template" id="detailsModal.html">
    <div class="row">
        <h5 class="small-4 column">
            Bill: {{printNo}}<br/>
            Mismatch: {{getLabel('types', currentMismatch.mismatchType)}}<br/>
            Status: {{getLabel('statuses', currentMismatch.status)}}
        </h5>
        <h5 class="small-5 column">
            Opened on: {{formatReportDate(firstOpened.reportDateTime)}}<br/>
            First Reference: {{formatReferenceDate(firstOpened.referenceDateTime)}}<br/>
            Current Reference: {{formatReferenceDate(observation.refDateTime)}}
        </h5>
        <div class="small-4 column"/>
    </div>
    <tabset>
        <tab heading="DIFF" active="tabs.diff">
            <mismatch-diff diff="currentMismatch.diff"></mismatch-diff>
        </tab>
        <tab heading="LBDC" active="tabs.lbdc"><span ng-bind="currentMismatch.referenceData"/></tab>
        <tab heading="Openleg" active="tabs.openleg"><span ng-bind="currentMismatch.observedData"/></tab>
        <tab heading="Prior Occurrences" active="tabs.prior">
            <accordion>
                <accordion-group ng-repeat="priorMismatch in currentMismatch.prior.items"
                     heading="REPORT: {{formatReportDate(priorMismatch.reportId.reportDateTime)}} DAYBREAK: {{formatReferenceDate(priorMismatch.reportId.referenceDateTime)}} STATUS: {{getLabel('statuses', priorMismatch.status)}}">
                    <mismatch-diff diff="priorMismatch.diff"></mismatch-diff>
                </accordion-group>
            </accordion>
        </tab>
        <tab heading="Other Mismatches" active="tabs.other">
            <ul ng-show="allMismatches.length > 1">
                <li ng-repeat="mismatch in allMismatches">
                    <span ng-show="mismatch.mismatchType==currentMismatch.mismatchType">
                        {{getLabel('types', mismatch.mismatchType)}} - {{getLabel('statuses', mismatch.status)}}
                    </span>
                    <a href="#" ng-show="mismatch.mismatchType!=currentMismatch.mismatchType"
                            ng-click="openNewModal(getMismatchId(observation, mismatch), 'other')">
                        {{getLabel('types', mismatch.mismatchType)}} - {{getLabel('statuses', mismatch.status)}}
                    </a>
                </li>
            </ul>
        </tab>
    </tabset>
    <a class="close-reveal-modal" ng-click="cancel()">&#215;</a>
</script>
</shiro:authenticated>
