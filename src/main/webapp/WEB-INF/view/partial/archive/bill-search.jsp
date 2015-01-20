<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
    <section ng-controller="BillSearchCtrl">
        <open-component:bill-search-bar/>
        <div ng-show="performedSearch && totalResults > 0" class="">
            <h3 style="color:white;" class="blue4-bg no-bottom-margin padding-20"><i class="icon-docs prefix-icon2"></i>
                <span class="bold">{{totalResults}}</span> bills were found that match <span class="gray5">{{::searchTerm}}</span></h3>
        </div>

        <div class="bill-result-pagination" ng-show="totalResults > 0">
            <div class="row">
                <div class="columns large-6 hide-for-medium-down text-medium text-left">
                    <span class="margin-left-20 margin-right-20 bold-span-1">Sort Results By</span>
                    <a class="margin-right-20 bold-span-1">Relevance</a>
                    <a class="margin-right-20 text-small">Most Activity</a>
                    <a class="margin-right-20 text-small">Recent Updates</a>
                </div>
                <div class="columns large-6" ng-show="totalResults > limit">
                    <div class="right" style="width:320px;">
                        <pagination page="currentPage" total-items="totalResults"
                                    previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;"
                                    max-size="5" boundary-links="true"></pagination>
                    </div>
                </div>
            </div>
        </div>

        <div class="columns large-12 bill-result bill-result-anim" ng-repeat="r in billResults.result.items" >
            <a ng-init="bill = r.result" ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.printNo}}" style="color:inherit;">
                <div class="columns small-5 large-3">
                    <span class="bill-result-id bold">
                        <span class="bill-result-print-no">{{bill.printNo}}</span> - {{bill.session}}</span>
                    <span class="bold blue2">
                        <span ng-show="bill.sponsor.budget">Budget Bill</span>
                        <span ng-show="bill.sponsor.rules">Rules</span>
                        <span ng-show="bill.sponsor.rules && bill.sponsor.member"> via </span>
                        <span>{{bill.sponsor.member.fullName}}</span>
                    </span><br/>
                    <span class="gray10 text-small hide-for-small-down">Published: {{bill.publishedDateTime | moment:'MMMM d, YYYY'}}</span>
                </div>
                <div class="columns small-7 large-6">
                    <div class="vertical-align" style="height: 60px">
                        <span class="text-medium">{{bill.title}}</span>
                    </div>
                    <div style="display: block;" class="">
                            <a ng-href="${ctxPath}/bills/{{bill.substitutedBy.session}}/{{bill.substitutedBy.basePrintNo}}"
                               ng-show="bill.substitutedBy" class="bold gray-2-blue text-small margin-right-20">
                                <i class="icon-switch prefix-icon"></i>
                                Substituted By: {{bill.substitutedBy.basePrintNo}} - {{bill.substitutedBy.session}}</a>
                            <span ng-show="bill.programInfo" class="bold text-small margin-right-20">
                                <i class="icon-tag prefix-icon"></i>{{bill.programInfo.name}} #{{bill.programInfo.sequenceNo}}</span>
                    </div>
                </div>
                <div class="columns large-3 hide-for-medium-down bill-milestone-container">
                    <div>
                        <!-- Bill milestone plot -->
                        <ul ng-show="bill.billType.resolution == false" class="large-block-grid-8 bill-milestone-small">
                            <li><div ng-class="{'met': (bill.milestones.size > 0)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 1)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 2)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 3)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 4)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 5)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 6)}" class="milestone">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 7)}" class="milestone">&nbsp;</div></li>
                        </ul>
                        <!-- Resolution milestone plot -->
                        <ul ng-show="bill.billType.resolution == true" class="large-block-grid-2 bill-milestone-small">
                            <li><div class="milestone met">&nbsp;</div></li>
                            <li><div ng-class="{'met': (bill.milestones.size > 0)}" class="milestone">&nbsp;</div></li>
                        </ul>
                        <span class="text-small milestone-text bold">{{getMilestoneDesc(bill.milestones)}}</span><br/>
                        <span class="text-small" style="color:#666;">{{getMilestoneDate(bill.milestones)}}</span>
                    </div>
                </div>
            </a>
        </div>

        <section class="margin-top-20" ng-show="performedSearch && totalResults == 0">
            <h3 style="color:#777;"><i class="icon-warning prefix-icon2"></i>Sorry, no matching bills were found for: {{::searchTerm}}</h3>
            <hr/>
            <p class="bold">How to search for a bill</p>
            <span>Insert bill search tips here...</span>

        </section>

        <section ng-hide="performedSearch || searchTerm" class="margin-top-10">
            <h3 style="color:white;" class="blue4-bg no-bottom-margin padding-20"><i class="icon-docs prefix-icon2"></i>
                <span class="bold">NYS Bills and Resolutions</span>
            </h3>
        </section>

        <!-- Lower Pagination -->
        <div class="columns large-12 center bill-result-pagination" ng-show="totalResults > 10">
            <div class="large-offset-4">
                <pagination ng-show="totalResults > limit" page="currentPage" total-items="totalResults"
                            previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;"
                            max-size="8" boundary-links="true"></pagination>
            </div>
        </div>
    </section>
</section>