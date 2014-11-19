<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
    <section ng-controller="BillSearchCtrl">
        <open-component:bill-search-bar/>

        <div id="billSearchInfoBar" ng-show="performedSearch && totalResults > 0" class="columns large-12">
            <span class="bold-span-1">
                Showing Results 1-20 out of {{totalResults}}
            </span>
        </div>

        <div id="billPaginationTop" ng-show="totalResults > 0">
            <div class="row">
                <div class="columns large-6">
                    <pagination ng-show="totalResults > limit" page="currentPage" total-items="totalResults"
                                max-size="5" boundary-links="true"></pagination>
                </div>
                <div class="columns large-6 hide-for-medium-down text-medium text-right">
                    <span class="margin-right-20">Sort Results By</span>
                    <a class="margin-right-20">Relevance</a>
                    <a class="margin-right-20">Most Activity</a>
                    <a class="margin-right-20">Recent Updates</a>
                </div>
            </div>
        </div>

        <div class="columns large-12 bill-result bill-result-anim" ng-repeat="r in billResults.result.items" >
            <a ng-init="bill = r.result" ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.printNo}}" style="color:inherit;">
                <div class="columns small-4 large-3">
                    <span class="bold bill-result-printno">{{bill.printNo}} - {{bill.session}}</span>
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
                            <span ng-show="bill.substitutedBy" class="label alert text-small">Substituted By: {{bill.substitutedBy.basePrintNo}} - {{bill.substitutedBy.session}}</span>
                            </span>
                            <span ng-show="bill.programInfo" class="label secondary text-small margin-right-20">
                                {{bill.programInfo.name}} #{{bill.programInfo.sequenceNo}}</span>
                    </div>
                </div>
                <div class="columns large-3 hide-for-medium-down bill-milestone-container">
                    <div>
                        <!-- Bill milestone plot -->
                        <ul ng-show="bill.billType.resolution == false" class="large-block-grid-8 bill-milestone-list">
                            <li ng-class="{'met': (bill.milestones.size > 0)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 1)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 2)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 3)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 4)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 5)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 6)}"></li>
                            <li ng-class="{'met': (bill.milestones.size > 7)}"></li>
                        </ul>
                        <!-- Resolution milestone plot -->
                        <ul ng-show="bill.billType.resolution == true" class="large-block-grid-2 bill-milestone-list">
                            <li class="met"></li>
                            <li ng-class="{'met': (bill.milestones.size > 0)}"></li>
                        </ul>
                        <span class="text-small bold milestone-text">{{getMilestoneDesc(bill.milestones)}}</span><br/>
                        <span class="text-small" style="color:#666;">{{getMilestoneDate(bill.milestones)}}</span>
                    </div>
                </div>
            </a>
        </div>

        <section class="margin-top-20" ng-show="performedSearch && totalResults == 0">
            <h3 style="color:#777;">Sorry, no matching bills were found for: {{::searchTerm}}</h3>
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
        <div id="billPaginationBottom" class="columns large-12" ng-show="totalResults > 10">
            <pagination page="currentPage" total-items="totalResults" max-size="10" boundary-links="true"></pagination>
        </div>
    </section>
</section>