<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
    <section ng-controller="BillViewCtrl">
        <open-component:bill-search-bar/>
        <div class="margin-top-5 clearfix">
            <div class="blue5-bg clearfix" style="line-height:2.5rem;color:white;">
                <div class="columns large-6">
                    <span class="text-medium bold">New York State {{bill.billType.desc}} {{bill.billType.resolution | resolutionOrBill}}</span>
                </div>
                <div class="text-right columns large-6 hide-for-medium-down">
                    <a target="_blank" ng-href="${ctxPath}/api/3/bills/{{bill.session}}/{{bill.basePrintNo}}"
                       class="text-medium bold white-2-blue"><i class="icon-code prefix-icon"></i>View Source</a>
                </div>
            </div>
            <div class="blue4-bg clearfix" style="color:white;">
                <div class="columns large-4">
                    <h1 class="no-top-margin"
                        style="color:white;font-size:2.5rem;line-height:80px;font-weight:600;margin-bottom:0;">
                        <span>{{bill.basePrintNo}}{{selectedVersion}}</span><span class=""> - {{bill.session}}</span>
                    </h1>
                </div>
                <div class="columns large-8 vertical-align" style="height:80px;">
                <span class="">
                    {{bill.title}}
                </span>
                </div>
            </div>
        </div>

        <!-- Bill Substitution Alert -->
        <div ng-show="bill.substitutedBy" class="text-medium clearfix" style="line-height:40px;background:#faffa5;">
            <div class="margin-left-20">
                <i class="icon-switch prefix-icon2"></i>
                This {{bill.billType.resolution | resolutionOrBill}} has been substituted by
                <a class="bold"
                   ng-href="${ctxPath}/bills/{{bill.substitutedBy.session}}/{{bill.substitutedBy.basePrintNo}}">
                    {{bill.substitutedBy.basePrintNo}}-{{bill.substitutedBy.session}}</a>. Future updates are made
                there.
            </div>
        </div>

        <!-- Bill Status -->
        <div ng-hide="bill.substitutedBy" class="blue5-bg" style="padding:10px;">
            <span class="text-medium bold" style="color:white;text-transform: capitalize;">
                {{bill.status.actionDate | moment:'MMMM DD, YYYY'}} - {{getStatusDesc(bill.status) | lowercase}}
            </span>
        </div>

        <div class="hide text-medium clearfix margin-bottom-10" style="line-height:40px;background:white;">
            <div class="columns large-4">
                <span class="bold">Versions - </span>
                <a ng-repeat="amendment in bill.amendments.items" ng-click="setSelectedVersion(amendment.version)"
                   class="margin-right-10" ng-class="{'bold': selectedVersion == amendment.version}">
                    {{amendment.version | defaultVersion}}
                </a>
            </div>
        </div>

        <!-- Bill Enacting Clause -->
        <div ng-if="bill.amendments.items[selectedVersion].actClause">
            <toggle-panel label="Enacting Clause" open="true" extra-classes="columns large-12 white margin-top-10">
                <div class="padding-20">
                    {{bill.amendments.items[selectedVersion].actClause}}
                </div>
            </toggle-panel>
        </div>

        <!-- Bill Summary -->
        <div ng-if="bill.summary">
            <toggle-panel label="Bill Summary" open="true" extra-classes="columns large-12 white">
                <div class="padding-20">{{bill.summary}}</div>
            </toggle-panel>
        </div>

        <!-- Bill Milestones -->
        <div ng-if="!bill.billType.resolution">
            <toggle-panel open="false" label="Legislative Milestones"
                          extra-classes="hide-for-medium-down columns large-12">
                <ul class="bill-milestone-large small-block-grid-2 medium-block-grid-4 large-block-grid-8">
                    <li ng-class="{'met': milestone.actionDate != null, 'active': $last && milestone.actionDate != null}"
                        ng-repeat="milestone in paddedMilestones">
                        <div class="milestone-desc">
                            <!-- Render icons depending on the milestone -->
                            <div class="milestone-icon"
                                 ng-class="{'icon-users': $index == 0 || $index == 3, 'icon-newspaper': $index == 1 || $index == 4,
                                    'icon-checkmark': $index == 2 || $index == 5, 'icon-drawer': $index == 6,
                                    'icon-pencil': $index == 7}"></div>
                            {{milestone.statusDesc}}
                        </div>
                        <div class="milestone-date">{{milestone.actionDate | moment:'MMMM DD, YYYY':'--'}}</div>
                    </li>
                </ul>
            </toggle-panel>
        </div>

        <!-- Bill Sponsor Container -->
        <toggle-panel label="Sponsor Information" open="true" extra-classes="columns large-12 white">
            <div class="padding-20 clearfix">
                <div class="columns large-4">
                    <img class="left margin-right-10" src="http://placekitten.com/g/50/50"
                         style="border-radius:50px;height: 50px;width:50px;"/>
                    <div id="sponsorName">
                        <span class="bold text-medium"
                              title="A Primary sponsor is the first member that is listed upon the introduction of a bill. A bill is allowed to have multiple prime sponsors in some cases.">Primary Sponsor</span><br/>
                        <span>
                            <a>{{bill.sponsor.member.fullName}}</a> - District {{bill.sponsor.member.districtCode}}
                        </span>
                    </div>
                </div>
                <div class="columns large-4">
                    <hr class="show-for-medium-down"/>
                    <span class="bold text-medium">Co-Sponsors</span><br/>
                    <span ng-show="bill.amendments.items[selectedVersion].coSponsors.size > 0"
                          ng-repeat="csp in bill.amendments.items[selectedVersion].coSponsors.items">
                        <a>{{csp.fullName}}</a>{{$last ? '' : ', '}}
                    </span>
                    <span ng-show="bill.amendments.items[selectedVersion].coSponsors.size == 0">
                        <span>No co-sponsors</span>
                    </span>
                </div>
                <div class="columns large-4">
                    <hr class="show-for-medium-down"/>
                    <span class="bold text-medium">Multi-Sponsors</span><br/>
                    <span ng-show="bill.amendments.items[selectedVersion].multiSponsors.size > 0"
                          ng-repeat="msp in bill.amendments.items[selectedVersion].multiSponsors.items">
                        <a>{{msp.fullName}}</a>{{$last ? '' : ', '}}
                    </span>
                    <span ng-show="bill.amendments.items[selectedVersion].multiSponsors.size == 0">
                        <span>No multi-sponsors</span>
                    </span>
                </div>
            </div>
        </toggle-panel>

        <!-- Similar Bills TODO: Condense this into one table. Makes no sense to have two. -->
        <div ng-if="bill.amendments.items[selectedVersion].sameAs.size > 0 || bill.previousVersions.size > 0">
            <toggle-panel label="Similar Legislation" open="true" extra-classes="columns large-12 white">
                <div class="margin-top-10 clearfix">
                    <!-- Same As Bills -->
                    <div class="columns large-6 margin-bottom-10">
                        <span class="bold-span-1">Identical bills within the same legislative session</span>
                        <hr class="margin-top-5"/>
                        <table style="width:100%;" class="text-left"
                               ng-show="bill.amendments.items[selectedVersion].sameAs.size > 0">
                            <thead>
                            <tr>
                                <td style="width: 30%">Bill Id</td>
                                <td>Last Status</td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="sameAs in bill.amendments.items[selectedVersion].sameAs.items">
                                <td>
                                    <a ng-href="${ctxPath}/bills/{{sameAs.session}}/{{sameAs.printNo}}">
                                        {{sameAs.printNo}} - {{sameAs.session}}
                                    </a>
                                </td>
                                <td>
                                    <span class="text-medium" style="text-transform: capitalize"
                                          ng-init="refBill=bill.billInfoRefs.items[sameAs.basePrintNo + '-' + sameAs.session]">
                                        {{getStatusDesc(refBill.status) | lowercase}}
                                    </span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="gray10 text-medium" ng-hide="bill.amendments.items[selectedVersion].sameAs.size > 0">
                            No same-as bills currently.
                        </div>
                    </div>
                    <!-- Prior Session Bills -->
                    <div class="columns large-6 margin-bottom-10">
                        <span class="bold-span-1">Versions of this bill in prior legislative sessions.</span>
                        <hr class="margin-top-5"/>
                        <table style="width:100%;" class="text-left" ng-show="bill.previousVersions.size > 0">
                            <thead>
                            <tr>
                                <td style="width: 30%">Bill Id</td>
                                <td>Last Status</td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="prevVersion in bill.previousVersions.items">
                                <td>
                                    <a ng-href="${ctxPath}/bills/{{prevVersion.session}}/{{prevVersion.printNo}}">
                                        {{prevVersion.printNo}} - {{prevVersion.session}}
                                    </a>
                                </td>
                                <td>
                                    <span class="text-medium" style="text-transform: capitalize"
                                          ng-init="refBill=bill.billInfoRefs.items[prevVersion.basePrintNo + '-' + prevVersion.session]">
                                        {{getStatusDesc(refBill.status) | lowercase}}
                                    </span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="gray10 text-medium" ng-hide="bill.previousVersions.size > 0">
                            No prior bills.
                        </div>
                    </div>
                </div>
            </toggle-panel>
        </div>

        <!-- Bill Votes -->
        <div ng-if="bill.votes.size > 0">
            <toggle-panel label="Senate Votes" open="true" extra-classes="columns large-12 white">
                <div class="padding-20 clearfix" ng-repeat="vote in bill.votes.items">
                    <span class="bold-span-1 capitalize">
                        {{vote.committee.name + ' ' + vote.voteType | lowercase}} Vote - {{vote.voteDate | moment:'MMMM DD, YYYY'}}
                    </span>
                    <br/>
                    <span class="text-medium">
                        {{bill.billType.resolution | resolutionOrBill}} Version: {{vote.version | defaultVersion}}
                    </span>
                    <hr/>
                    <div class="columns large-3 center">
                        <vote-pie votes="vote.memberVotes.items" height="200" width="250" plot-bg="#ffffff"></vote-pie>
                    </div>
                    <table class="columns large-9">
                        <thead>
                        <tr>
                            <th style="width:25%">Vote</th>
                            <th>Members</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="(k,v) in vote.memberVotes.items">
                            <td>{{k | voteTypeFilter}}
                                    <span class="label"
                                          ng-class="{'alert': k == 'NAY', 'success': k == 'AYE' || k == 'AYEWR',
                                                     'secondary': k == 'ABD' || k == 'ABS' || k == 'EXC'}">
                                        {{v.size}}
                                    </span>
                            </td>
                            <td>
                                <a ng-repeat="m in v.items">
                                    {{m.shortName + ($last ? '' : ',&nbsp;&nbsp;')}}
                                </a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </toggle-panel>
        </div>

        <!-- Actions -->
        <toggle-panel label="Actions" open="true" extra-classes="columns large-12 white">
            <div class="">
                <table style="width:100%;">
                    <thead>
                    <tr>
                        <th>No</th>
                        <th>Bill Id</th>
                        <th>Date</th>
                        <th>Chamber</th>
                        <th>Text</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="action in bill.actions.items">
                        <td>{{action.sequenceNo}}</td>
                        <td>{{action.billId.printNo}}</td>
                        <td>{{action.date | moment:'MMMM DD, YYYY'}}</td>
                        <td>{{action.chamber}}</td>
                        <td>{{action.text}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </toggle-panel>

        <!-- Sponsor's Memorandum -->
        <div ng-if="bill.amendments.items[selectedVersion].memo">
            <toggle-panel label="Sponsor's Memorandum" open="true" extra-classes="columns large-12 white">
                <div class="padding-20">
                    <span class="text-medium"
                          ng-bind-html="bill.amendments.items[selectedVersion].memo | prettySponsorMemo"></span>
                </div>
            </toggle-panel>
        </div>

        <!-- Bill Full Text -->
        <div ng-if="!bill.billType.resolution && bill.amendments.items[selectedVersion].fullText">
            <toggle-panel label="Full Text of Bill" open="false" extra-classes="columns large-12 white">
                <div class="padding-20 text-medium"
                     ng-bind-html="bill.amendments.items[selectedVersion].fullText | prettyFullText">
                </div>
            </toggle-panel>
        </div>

        <!-- Resolution Full Text -->
        <div ng-if="bill.billType.resolution">
            <toggle-panel label="Resolution Text" open="true" extra-classes="columns large-12 white">
                <div class="padding-20 text-medium"
                     ng-bind-html="bill.amendments.items[selectedVersion].fullText | prettyResolutionText">
                </div>
            </toggle-panel>
        </div>

    </section>
</section>