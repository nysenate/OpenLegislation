<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
    <section ng-controller="BillSearchCtrl">
        <open-component:bill-search-bar/>

        <div ng-show="performedSearch && totalResults > 0" class="">
            <h3 style="color:#444;margin:20px;"><i class="icon-docs prefix-icon2"></i>
                <span class="blue2">{{totalResults}}</span> bills were found that match <span class="gray6">{{::searchTerm}}</span></h3>
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
                    <div class="right" style="width:300px;">
                        <pagination page="currentPage" total-items="totalResults"
                                    previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;"
                                    max-size="5" boundary-links="true"></pagination>
                    </div>
                </div>
            </div>
        </div>

        <div class="columns large-12 bill-result bill-result-anim" ng-repeat="r in billResults.result.items" >
            <a ng-init="bill = r.result" ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.printNo}}" style="color:inherit;">
                <div class="columns small-4 large-3">
                    <span class="bill-result-printno bold">{{bill.printNo}} - {{bill.session}}</span>
                    <span class="bold blue2">
                        <span ng-show="bill.sponsor.budget">Budget Bill</span>
                        <span ng-show="bill.sponsor.rules">Rules</span>
                        <span ng-show="bill.sponsor.rules && bill.sponsor.member"> via </span>
                        <span>{{bill.sponsor.member.fullName}}</span>
                    </span><br/>
                    <span class="gray6 text-small">Published: {{bill.publishedDateTime | moment:'MMMM d, YYYY'}}</span>
                </div>
                <div class="columns small-8 large-6">
                    <div class="vertical-align" style="height: 60px">
                        <span class="text-small">{{bill.title}}</span>
                    </div>
                    <div style="display: block;" class="">
                            <span class="label text-small secondary">{{bill.billType.desc}}</span>
                            <span ng-show="bill.substitutedBy" class="label bold alert text-small">
                                <i class="icon-switch prefix-icon"></i>Substituted By: {{bill.substitutedBy.basePrintNo}} - {{bill.substitutedBy.session}}</span>
                            </span>
                            <span ng-show="bill.programInfo" class="label bold secondary text-small margin-right-20">
                                <i class="icon-tag prefix-icon"></i>{{bill.programInfo.name}} #{{bill.programInfo.sequenceNo}}</span>
                    </div>
                </div>
                <div class="columns large-3 hide-for-medium-down bill-milestone-container">
                    <div>
                        <!-- Bill milestone plot -->
                        <ul ng-show="bill.billType.resolution == false" class="large-block-grid-8 bill-milestone-list">
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
                        <ul ng-show="bill.billType.resolution == true" class="large-block-grid-2 bill-milestone-list">
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

        <section ng-hide="performedSearch || searchTerm" class="margin-top-20">
            <h4>Overview of the 2013 - 2014 Legislative Session</h4>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">Bills Introduced</h6>
                <span class="stats-block-number"><i class="icon-docs prefix-icon2"></i>26000</span>
            </div>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">In Committees</h6>
                <span class="blue1 stats-block-number"><i class="icon-users prefix-icon2"></i>125</span>
            </div>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">On Floor Calendars</h6>
                <span class="blue2 stats-block-number"><i class="icon-calendar prefix-icon2"></i>435</span>
            </div>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">Passed Both Houses</h6>
                <span class="blue3 stats-block-number"><i class="icon-thumbsup prefix-icon2"></i>12</span>

            </div>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">Signed Into Law</h6>
                <span class="green1 stats-block-number"><i class="icon-book prefix-icon2"></i>34</span>
            </div>
            <div class="stats-block-container text-center columns large-2 small-6">
                <h6 class="stats-block-title">Vetoed</h6>
                <span class="red1 stats-block-number"><i class="icon-trash prefix-icon2"></i>1</span>
            </div>

            <hr/>

            <div class="panel">Bills by Sponsor...</div>
            <div class="panel">Bills by Law Section...</div>
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