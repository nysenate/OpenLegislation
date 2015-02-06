<section ng-if="tab.type=='summary'" ng-controller="DaybreakSummaryCtrl" style="padding-left: 10px">
    <h4>
        <span class="icon-statistics blue-title-icon"></span>
        Daybreak Reports: {{rsStartDate | moment:'lll'}} - {{rsEndDate | moment:'lll'}}
    </h4>
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
</section>
