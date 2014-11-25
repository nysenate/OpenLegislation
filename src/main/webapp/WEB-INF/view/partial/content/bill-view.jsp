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
                <div class="columns large-6 hide-for-medium-down">

                </div>
            </div>
            <div class="blue4-bg clearfix" style="color:white;">
                <div class="columns large-4">
                    <h1 class="no-top-margin" style="color:white;font-size:2.5rem;line-height:80px;font-weight:600;margin-bottom:0;">
                        <span>{{bill.basePrintNo}}{{selectedVersion}}</span ><span class=""> - {{bill.session}}</span></h1>
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
                <a class="bold" ng-href="${ctxPath}/bills/{{bill.substitutedBy.session}}/{{bill.substitutedBy.basePrintNo}}">
                    {{bill.substitutedBy.basePrintNo}}-{{bill.substitutedBy.session}}</a>. Future updates are made there.
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
            <div class="columns large-4">
                <span class="bold">Same As - </span>
                <a ng-repeat="sameAs in bill.amendments.items[selectedVersion].sameAs.items"
                   ng-href="${ctxPath}/bills/{{sameAs.session}}/{{sameAs.printNo}}">{{sameAs.printNo}}-{{sameAs.session}}</a>
                <span ng-if="bill.amendments.items[selectedVersion].sameAs.size == 0">No same as bill</span>
            </div>
            <div class="columns large-4">
                <span class="bold">Previous Session - </span>
                <a ng-repeat="prevVersion in bill.previousVersions.items"
                   ng-href="${ctxPath}/bills/{{prevVersion.session}}/{{prevVersion.printNo}}">{{prevVersion.printNo}}-{{prevVersion.session}}</a>
                <span ng-if="bill.previousVersions.size == 0">No prior identical bill</span>
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
            <toggle-panel open="true" label="Legislative Milestones" extra-classes="hide-for-medium-down columns large-12">
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
                <div class="columns large-4 no-padding margin-top-5">
                    <img class="left margin-right-10" src="http://placekitten.com/g/37/50" style="height: 50px;"/>
                    <div id="sponsorName">
                        <span class="bold text-medium"
                              title="A Primary sponsor is the first member that is listed upon the introduction of a bill. A bill is allowed to have multiple prime sponsors in some cases.">Primary Sponsor</span><br/>
                        <span>
                            <a>{{bill.sponsor.member.fullName}}</a> - District {{bill.sponsor.member.districtCode}}
                        </span>
                    </div>
                </div>
                <div class="columns large-4 margin-top-5">
                    <div class="margin-top-10">
                        <span class="bold text-medium">Co-Sponsors</span><br/>
                        <span ng-show="bill.amendments.items[selectedVersion].coSponsors.size > 0"
                              ng-repeat="csp in bill.amendments.items[selectedVersion].coSponsors.items">
                            <a>{{csp.fullName}}</a>{{$last ? '' : ', '}}
                        </span>
                        <span ng-show="bill.amendments.items[selectedVersion].coSponsors.size == 0">
                            <span>No co-sponsors</span>
                        </span>
                    </div>
                </div>
                <div class="columns large-4 margin-top-5">
                    <div class="margin-top-10">
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
            </div>
        </toggle-panel>

        <!-- Similar Bills -->
        <toggle-panel label="Similar Legislation" open="true" extra-classes="columns large-12 white">
            <div class="padding-20 clearfix">
                <div class="columns large-6">
                    <h6>Identical bills within the same legislative session</h6>
                    <hr class="margin-top-5"/>
                    {{bill.sameAs}}
                    <ul class="no-bullet">
                        <li ng-repeat="sameAs in bill.amendments.items[selectedVersion].sameAs.items">
                            <a ng-href="${ctxPath}/bills/{{sameAs.session}}/{{sameAs.printNo}}">
                                {{sameAs.printNo}} - {{sameAs.session}}
                            </a>
                            <span class="margin-left-20 text-medium" style="text-transform: capitalize"
                                  ng-init="refBill=bill.billInfoRefs.items[sameAs.basePrintNo + '-' + sameAs.session]">
                                {{getStatusDesc(refBill.status) | lowercase}}
                            </span>
                        </li>
                    </ul>
                    <div class="gray10" ng-hide="bill.amendments.items[selectedVersion].sameAs.size > 0">
                        No same-as bills currently.
                    </div>
                </div>
                <div class="columns large-6">
                    <h6>Versions of this bill in prior legislative sessions.</h6>
                    <hr class="margin-top-5"/>
                    <ul class="no-bullet">
                        <li ng-repeat="prevVersion in bill.previousVersions.items">
                            <a ng-href="${ctxPath}/bills/{{prevVersion.session}}/{{prevVersion.printNo}}">
                                {{prevVersion.printNo}} - {{prevVersion.session}}</a>
                            <span style="text-transform: capitalize"
                                ng-init="refBill=bill.billInfoRefs.items[prevVersion.basePrintNo + '-' + prevVersion.session]">
                                {{getStatusDesc(refBill.status) | lowercase}}
                            </span>
                        </li>
                    </ul>
                    <div class="gray10" ng-hide="bill.previousVersions.size > 0">
                        No prior bills.
                    </div>
                </div>
            </div>
        </toggle-panel>

        <!-- Bill Votes -->
        <div ng-if="bill.votes.size > 0">
            <toggle-panel label="Votes" open="true" extra-classes="columns large-12 white">
                <div ng-repeat="vote in bill.votes.items" class="panel">
                    <h5 style="text-transform: capitalize">
                        {{vote.voteType | lowercase}} Vote On {{vote.voteDate | moment:'MMMM DD, YYYY'}}
                    </h5>
                    <hr/>
                    <vote-pie votes="vote.memberVotes.items" height="300" width="300" plot-bg="#f1f1f1"></vote-pie>
                    {{vote}}
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
                    <span ng-bind-html="bill.amendments.items[selectedVersion].memo | prettySponsorMemo"></span>
                </div>
            </toggle-panel>
        </div>

        <!-- Bill Full Text -->
        <div ng-if="!bill.billType.resolution && bill.amendments.items[selectedVersion].fullText">
            <toggle-panel label="Full Text of Bill" open="false" extra-classes="columns large-12 white">
                <div class="padding-20 text-medium" style="white-space: pre;font-family: monospace, monospace"
                     ng-bind-html="bill.amendments.items[selectedVersion].fullText | prettyFullText">
                </div>
            </toggle-panel>
        </div>

        <!-- Resolution Full Text -->
        <div ng-init="_show_resText=true" ng-if="bill.billType.resolution && bill.amendments.items[selectedVersion].fullText"
             class="columns large-12 panel no-padding white margin-bottom-10">
            <label class="panel-label" id="resolutionText" ng-click="_show_resText=!_show_resText">
                <a class="gray-2-blue" href="#resolutionText">Resolution Text</a>
                <i class="right" ng-class="{'icon-arrow-up4': _show_resText, 'icon-arrow-down5': !_show_resText}"></i>
            </label>
            <div class="panel-content" ng-class="{'panel-content-hide': !_show_resText}">
                <div class="text-medium padding-20" ng-bind-html="bill.amendments.items[selectedVersion].fullText | prettyResolutionText"></div>
            </div>
        </div>
    </section>
</section>