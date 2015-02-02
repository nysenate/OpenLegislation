<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillViewCtrl">
    <section ng-if="response.success === true" >
      <md-toolbar class="md-toolbar-tools auto-height">
        <h6 class="margin-top-10 margin-bottom-10">{{bill.title}}</h6>
      </md-toolbar>
      <md-toolbar class="md-hue-2 md-toolbar-tools auto-height">
        <section layout="row" layout-sm="column"
                 layout-align="start center" layout-align-sm="start start">
          <div class="margin-bottom-10 margin-top-10" layout="row" layout-align="start center" style="margin-right:60px;">
            <img class="margin-right-10" src="http://lorempixel.com/50/50/animals/8"
                 style="height: 60px;min-width:60px;"/>
            <div layout="column" ng-if="!bill.sponsor.budget">
              <div ng-if="!bill.sponsor.rules" class="text-medium">Sponsored By</div>
              <div ng-if="bill.sponsor.rules" class="text-medium">
                From the Rules Committee<span ng-if="bill.sponsor.member"> Via</span>
              </div>
              <div class="bold">{{bill.sponsor.member.fullName}}</div>
              <%--<div class="text-small">District {{bill.sponsor.member.districtCode}}</div>--%>
            </div>
            <div layout="column" ng-if="bill.sponsor.budget">
              <div class="bold">Budget Bill</div>
            </div>
          </div>
          <div class="margin-bottom-10 margin-top-10" layout="column" style="margin-right:60px;">
            <div class="text-medium">Status as of {{bill.status.actionDate | moment:'MMMM D, YYYY'}}</div>
            <div class="bold">
              <i ng-if="bill.signed === true" class="prefix-icon icon-checkmark"></i>
              <span>{{getStatusDesc(bill.status)}}</span>
            </div>
          </div>
          <div class="margin-bottom-10 margin-top-10" layout="column" ng-if="bill.programInfo">
            <div class="text-medium">Bill #{{bill.programInfo.sequenceNo + 1}} on the program for </div>
            <div class="bold">{{bill.programInfo.name}}</div>
          </div>
        </section>
      </md-toolbar>
      <md-toolbar ng-if="bill.amendments.size > 1" class="md-toolbar-tools auto-height">
        <label class="margin-right-20">Version </label>
        <md-radio-group layout="row" layout-sm="column" ng-model="curr.amdVersion">
          <md-radio-button ng-repeat="(version, amd) in bill.amendments.items" class="md-accent md-hue-1"
                           value="{{version}}">
            <span ng-if="$first">Initial</span>
            <span ng-if="!$first">Revision {{version}}</span>
            <span ng-if="$last"> (Latest)</span>
            <br/>
            <small>{{amd.publishDate | moment:'MMM D, YYYY'}}</small>
          </md-radio-button>
        </md-radio-group>
      </md-toolbar>
      <md-tabs md-selected="curr.selectedView" class="md-hue-2">
        <md-tab md-on-select="backToSearch()">
          <md-tab-label>
            <span><i class="icon-search prefix-icon2"></i>Back to Search</span>
          </md-tab-label>
        </md-tab>
        <md-tab label="Details">
          <%-- Substituted By --%>
          <md-card class="white-bg padding-10" ng-if="bill.substitutedBy">
            <md-button style="text-align: left;text-transform: none;" class="margin-left-10 md-warn"
                       ng-href="${ctxPath}/bills/{{bill.substitutedBy.session}}/{{bill.substitutedBy.basePrintNo}}">
              <i class="icon-switch prefix-icon2"></i>
              This bill has been substituted by {{bill.substitutedBy.basePrintNo}} - {{bill.substitutedBy.session}}.
            </md-button>
          </md-card>
          <%-- Enacting Clause --%>
          <md-card class="content-card" ng-if="!bill.billType.resolution">
            <md-subheader>Enacting Clause</md-subheader>
            <md-content style="margin-left:16px;">
              <p class="text-medium">{{bill.amendments.items[curr.amdVersion].actClause | default:'Not Available'}}</p>
            </md-content>
          </md-card>
          <%-- Bill Summary --%>
          <md-card class="md-whiteframe-z0 white-bg padding-10" ng-if="!bill.billType.resolution">
            <md-subheader>Summary of Bill</md-subheader>
            <md-content style="margin-left:16px;">
              <p class="text-medium">{{bill.summary | default:'Not Available'}}</p>
            </md-content>
          </md-card>
          <%-- Law Section --%>
          <md-card class="content-card">
            <md-subheader>Affected Law</md-subheader>
            <md-content>
              <span class="text-medium">Primary Law Section - {{bill.amendments.items[curr.amdVersion].lawSection}}</span>
              <p class="text-medium">Law Code - {{bill.amendments.items[curr.amdVersion].lawCode | default:'N/A'}}</p>
            </md-content>
          </md-card>
          <%-- Co/Multi --%>
          <md-card layout="row" layout-sm="column" layout-align="start start" class="content-card"
                   ng-if="bill.amendments.items[curr.amdVersion].coSponsors.size > 0 ||
                        bill.amendments.items[curr.amdVersion].multiSponsors.size > 0">
            <%-- Co Sponsor --%>
            <section flex style="width:100%" ng-if="bill.amendments.items[curr.amdVersion].coSponsors.size > 0">
              <md-subheader>{{bill.amendments.items[curr.amdVersion].coSponsors.size}} Co Sponsor(s)</md-subheader>
              <md-divider/>
              <md-content style="margin-left:16px;max-height: 200px;" class="padding-10">
                <md-list>
                  <md-item ng-repeat="coSponsor in bill.amendments.items[curr.amdVersion].coSponsors.items">
                    <md-item-content>
                      <div class="md-tile-left">
                        <img class="margin-right-10" ng-src="http://lorempixel.com/50/50/food/{{$index}}"
                             style="height: 50px;width:50px;"/>
                      </div>
                      <div class="md-tile-content">
                        <span class="text-medium">{{coSponsor.fullName}} (D)</span>
                      </div>
                    </md-item-content>
                  </md-item>
                </md-list>
              </md-content>
            </section>
            <%-- Multi Sponsor --%>
            <section flex style="width:100%" ng-if="bill.amendments.items[curr.amdVersion].multiSponsors.size > 0">
              <md-subheader>{{bill.amendments.items[curr.amdVersion].multiSponsors.size}} Multi Sponsor(s)</md-subheader>
              <md-divider/>
              <md-content style="margin-left:16px;max-height: 200px;" class="padding-10">
                <md-list>
                  <md-item ng-repeat="multiSponsor in bill.amendments.items[curr.amdVersion].multiSponsors.items">
                    <md-item-content>
                      <div class="md-tile-left">
                        <img class="margin-right-10" ng-src="http://lorempixel.com/50/50/food/{{$index}}"
                             style="height: 50px;width:50px;"/>
                      </div>
                      <div class="md-tile-content">
                        <span class="text-medium">{{multiSponsor.fullName}} (D)</span>
                      </div>
                    </md-item-content>
                  </md-item>
                </md-list>
              </md-content>
            </section>
          </md-card>
          <%-- Identical Legislation --%>
          <md-card class="content-card" ng-if="bill.amendments.items[curr.amdVersion].sameAs.size > 0 ||
                                               bill.previousVersions.size > 0">
            <md-subheader>Identical Legislation</md-subheader>
            <md-content>
              <md-list>
                <md-item ng-repeat="sameAs in bill.amendments.items[curr.amdVersion].sameAs.items">
                  <a class="result-link"
                     ng-href="${ctxPath}/bills/{{sameAs.session}}/{{sameAs.basePrintNo}}?search={{billSearch.term}}&view=1">
                    <md-item-content layout="row" layout-sm="column" layout-align-sm="start start" class="padding-10">
                      <div class="text-medium margin-right-20" style="width:120px">
                        <h4 class="no-margin">{{sameAs.printNo}} - {{sameAs.session}}</h4>
                        <p class="no-margin">Same As Bill</p>
                      </div>
                      <div class="text-medium"
                           ng-init="sameAsBill = bill.billInfoRefs.items[sameAs.basePrintNo + '-' + sameAs.session]">
                        <p class="no-margin">
                          Sponsored By: {{sameAsBill.sponsor.member.fullName}}
                        </p>
                        <p class="no-margin">
                          Last Status as of {{sameAsBill.status.actionDate | moment:'MMMM D, YYYY'}} - {{getStatusDesc(sameAsBill.status)}}
                        </p>
                      </div>
                    </md-item-content>
                  </a>
                </md-item>
                <md-item ng-repeat="prevVersion in bill.previousVersions.items">
                  <a class="result-link"
                     ng-href="${ctxPath}/bills/{{prevVersion.session}}/{{prevVersion.basePrintNo}}?search={{billSearch.term}}&view=1">
                    <md-item-content layout="row" layout-sm="column" layout-align-sm="start start" class="padding-10">
                      <div class="text-medium margin-right-20" style="width:120px">
                        <h4 class="no-margin">{{prevVersion.printNo}} - {{prevVersion.session}}</h4>
                        <p class="no-margin">Prior Bill</p>
                      </div>
                      <div class="text-medium"
                           ng-init="prevBill = bill.billInfoRefs.items[prevVersion.basePrintNo + '-' + prevVersion.session]">
                        <p class="no-margin">
                          Sponsored By: {{prevBill.sponsor.member.fullName}}
                        </p>
                        <p class="no-margin">
                          Last Status as of {{prevBill.status.actionDate | moment:'MMMM D, YYYY'}} - {{getStatusDesc(prevBill.status)}}
                        </p>
                      </div>
                    </md-item-content>
                  </a>
                </md-item>
              </md-list>
            </md-content>
          </md-card>
          <%-- Veto Messages --%>
          <md-card class="content-card" ng-if="bill.vetoMessages.size > 0">
            <md-subheader>Veto Message From Governor</md-subheader>
            <md-content ng-repeat="veto in bill.vetoMessages.items">
              <span class="text-medium">Veto #{{veto.vetoNumber}} for Year {{veto.year}}</span>
              <md-divider></md-divider>
              <pre class="bill-full-text">{{veto.memoText}}</pre>
            </md-content>
          </md-card>
          <%-- Approval Message --%>
          <md-card class="content-card" ng-if="bill.approvalMessage">
            <md-subheader>Approval Message From Governor</md-subheader>
            <md-content>
              <span class="text-medium">
                Approval #{{bill.approvalMessage.approvalNumber}} for Year {{bill.approvalMessage.year}} - Chapter {{bill.approvalMessage.chapter}}
              </span>
              <md-divider></md-divider>
              <pre class="bill-full-text">{{bill.approvalMessage.text}}</pre>
            </md-content>
          </md-card>
          <%-- Votes --%>
          <md-card class="content-card" ng-if="bill.votes.size > 0">
            <md-subheader>Committee/Floor Votes</md-subheader>
            <md-content>
              <section ng-repeat="vote in bill.votes.items">
                <div>
                  <h4 class="no-bottom-margin">{{vote.voteDate | moment:'MMM DD, YYYY'}} - {{vote.committee.name}}
                    <span class="capitalize">{{vote.voteType | lowercase}}</span>
                    Vote
                  </h4>
                  <p class="no-top-margin text-medium">Voted on Amendment Revision: {{veto.version | prettyAmendVersion}}</p>
                  <md-divider></md-divider>
                  <div layout="row" layout-align="center center">
                    <div flex>
                      <table class="bill-votes-table">
                        <thead>
                        <tr>
                          <th style="min-width: 100px;">Vote</th>
                          <th>Count</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-class="{'positive': (voteType === 'AYE' || voteType === 'AYEWR'),
                                     'negative': (voteType === 'NAY')}"
                            ng-repeat="(voteType, votes) in vote.memberVotes.items">
                          <td>{{voteType | voteTypeFilter}}</td>
                          <td>{{votes.size}}</td>
                        </tr>
                        </tbody>
                      </table>
                    </div>
                    <div flex>
                      <md-content class="margin-left-16 padding-10" style="max-height: 200px;">
                        <md-list>
                          <md-item ng-repeat="(voteType, votes) in vote.memberVotes.items">
                            <md-item-content ng-repeat="voteItem in votes.items">
                              <div class="md-tile-left margin-right-10">
                                <h5>{{voteType}}</h5>
                              </div>
                              <div class="md-tile-left">
                                <img class="margin-right-10" ng-src="http://lorempixel.com/50/50/food/{{$index}}"
                                     style="height: 50px;width:50px;"/>
                              </div>
                              <div class="md-tile-content">
                                <span class="text-medium">{{voteItem.shortName}}</span>
                              </div>
                            </md-item-content>
                          </md-item>
                        </md-list>
                      </md-content>
                    </div>
                  </div>
                </div>
              </section>
            </md-content>
          </md-card>
          <%-- Agenda/Cal Refs --%>
          <md-card class="content-card" ng-if="bill.calendars.size > 0 || bill.committeeAgendas.size > 0">
            <md-subheader>Agenda/Calendar References</md-subheader>
            <md-content style="margin-left:16px;">
              <md-list>
                <md-item>
                  <md-item-content ng-repeat="agenda in bill.committeeAgendas.items">
                    <md-button ng-if="bill.committeeAgendas.size > 0" class="text-medium md-primary margin-right-20"
                               ng-href="${ctxPath}/agendas/{{agenda.agendaId.year}}/{{agenda.agendaId.number}}/{{agenda.committeeId.name}}">
                      Committee Agenda #{{agenda.agendaId.number}} ({{agenda.agendaId.year}}) - {{agenda.committeeId.name}}
                    </md-button>
                  </md-item-content>
                  <md-item-content ng-repeat="calendar in bill.calendars.items">
                    <md-button class="text-medium md-primary"
                               ng-href="${ctxPath}/calendars/{{lastCalendar.year}}/{{lastCalendar.calendarNumber}}">
                      Senate Floor Calendar {{calendar.calendarNumber}} ({{calendar.year}})
                    </md-button>
                  </md-item-content>
                </md-item>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>
        <%-- Sponsor Memo --%>
        <md-tab label="Memo" ng-disabled="bill.billType.resolution">
          <md-card class="content-card">
            <md-content ng-if="bill.amendments.items[curr.amdVersion].memo">
            <pre class="bill-full-text margin-20" style=""
                 ng-bind-html="bill.amendments.items[curr.amdVersion].memo | prettySponsorMemo"></pre>
            </md-content>
            <md-content ng-if="bill.billType.chamber == 'ASSEMBLY'">
              <div class="text-medium padding-20">Sponsor memos are not provided for Assembly bills.</div>
            </md-content>
            <md-content ng-if="!bill.amendments.items[curr.amdVersion].memo && bill.billType.chamber == 'SENATE'">
              <div class="text-medium padding-20">Sponsor memo is not available.</div>
            </md-content>
          </md-card>
        </md-tab>
        <%-- Bill Actions --%>
        <md-tab label="Actions">
          <md-card class="content-card">
            <md-content>
              <md-list>
                <md-item hide-sm>
                  <md-item-content class="text-medium bold">
                    <div style="width:140px" class="md-tile-left margin-10"><span>Date</span></div>
                    <div style="width:60px" class="md-tile-left margin-10"><span>Bill</span></div>
                    <div style="width:100px" class="md-tile-left margin-10"><span>Chamber</span></div>
                    <div class="md-tile-content"><span>Action Text</span></div>
                  </md-item-content>
                  <md-divider/>
                </md-item>
                <md-item ng-repeat="action in bill.actions.items">
                  <md-item-content>
                    <div style="width:140px" hide-sm class="margin-10">
                      <span class="text-medium">{{action.date | moment:'MMMM D, YYYY'}}</span>
                    </div>
                    <div style="width:60px" hide-sm class="margin-10">
                      <span class="text-medium">{{action.billId.printNo}}</span>
                    </div>
                    <div style="width:100px" hide-sm class="margin-10">
                      <span class="text-medium capitalize">{{action.chamber | lowercase}}</span>
                    </div>
                    <div class="md-tile-content">
                      <h4 class="text-medium margin-10 capitalize">{{action.text | lowercase}}</h4>
                      <h4 hide-gt-sm>{{action.date | moment:'MMMM D, YYYY'}}</h4>
                      <p hide-gt-sm>{{action.billId.printNo}} - {{action.chamber}}</p>
                    </div>
                  </md-item-content>
                  <md-divider ng-if="!$last"/>
                </md-item>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>
        <%-- Bill Text --%>
        <md-tab label="Full Text">
          <md-card class="content-card" ng-if="bill.amendments.size > 1">
            <md-content layout="row">
              Compare with revision:
              <select ng-model="curr.compareVersion" ng-change="diffBills()" class="margin-left-20 white-bg">
                <option value="None">---</option>
                <option ng-repeat="(version, amd) in bill.amendments.items" ng-if="version !== curr.amdVersion">
                  {{version | prettyAmendVersion}}
                </option>
              </select>
            </md-content>
          </md-card>
          <md-content class="margin-10 padding-20">
          <span ng-if="!bill.amendments.items[curr.amdVersion].fullText">Bill Text is not available yet. New bills or revisions
          may not have full text available right away.</span>
          <div ng-if="bill.amendments.items[curr.amdVersion].fullText">
             <pre ng-if="!diffHtml" class="margin-left-20 bill-full-text">{{bill.amendments.items[curr.amdVersion].fullText}}</pre>
             <pre ng-if="diffHtml" class="margin-left-20 bill-full-text" ng-bind-html="diffHtml"></pre>
          </div>
          </md-content>
        </md-tab>
        <%-- Updates --%>
        <md-tab label="Update History" md-on-select="getUpdates()">
          <md-card ng-repeat="update in updateHistory.items">
            <md-content class="content-card">
              <md-list>
                <md-item>
                  <md-item-content>
                    <div class="text-medium md-tile-left">
                      <p>Published Date - {{update.sourceDateTime | moment:'MMM DD, YYYY'}}</p>
                      <p>Processed Date- {{update.processedDateTime | moment:'MMM DD, YYYY hh:mm:ss'}}</p>
                    </div>
                  </md-item-content>
                </md-item>
              </md-list>
            </md-content>
          </md-card>
        </md-tab>
    </section>
    <section ng-if="response.success === false">
      <md-card>
        <md-content class="content-card padding-20">
          <h4>Really sorry about that.</h4>
          <a class="result-link text-medium padding-10"
                     ng-href="${ctxPath}/bills"><span><i class="prefix-icon2 icon-arrow-left5"></i>Return to Bill Search</span></a>
        </md-content>
      </md-card>
    </section>
  </section>
</section>