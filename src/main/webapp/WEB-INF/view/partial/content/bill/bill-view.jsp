<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillViewCtrl" class="content-section">
    <section ng-if="response.success === true" md-theme="{{billTheme}}">
      <md-toolbar class="md-toolbar-tools auto-height">
        <h5 style="margin:20px 0;">{{bill.title}}</h5>
      </md-toolbar>
      <md-toolbar class=" md-toolbar-tools auto-height">
        <section layout="row" layout-sm="column"
                 layout-align="start center" layout-align-sm="start start">
          <div class="margin-bottom-10 margin-top-10" layout="row" layout-align="start center" style="margin-right:60px;">
            <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{bill.sponsor.member.imgName}}"
                 style="height: 60px;"/>
            <div layout="column" ng-if="!bill.sponsor.budget">
              <div ng-if="!bill.sponsor.rules" class="text-medium">Sponsored By</div>
              <div ng-if="bill.sponsor.rules" class="text-medium bold">
                From the Rules Committee<span ng-if="bill.sponsor.member"> Via</span>
              </div>
              <h5 class="bold no-margin">{{bill.sponsor.member.fullName}}</h5>
              <div ng-if="bill.sponsor.member" class="text-small">District {{bill.sponsor.member.districtCode}}</div>
            </div>
            <div layout="column" ng-if="bill.sponsor.budget">
              <h5 class="bold no-margin">Budget Bill</h5>
            </div>
          </div>
          <div class="margin-bottom-10 margin-top-10" layout="column" style="margin-right:60px;min-width: 280px;">
            <div class="text-medium">Status as of {{bill.status.actionDate | moment:'MMMM D, YYYY'}}</div>
            <h5 class="bold no-margin">
              <i ng-if="bill.signed === true || bill.adopted === true" class="prefix-icon icon-checkmark"></i>
              <i ng-if="bill.vetoed === true" class="prefix-icon icon-blocked"></i>
              <span>{{getStatusDesc(bill.status)}}</span>
            </h5>
            <milestones ng-hide="bill.billType.resolution" milestone-arr="bill.milestones" chamber="bill.billType.chamber"></milestones>
          </div>
          <div class="margin-bottom-10 margin-top-10" layout="column" ng-if="bill.programInfo">
            <div class="text-medium">Bill #{{bill.programInfo.sequenceNo + 1}} on the program for </div>
            <h5 class="bold no-margin">{{bill.programInfo.name}}</h5>
          </div>
        </section>
      </md-toolbar>
      <md-toolbar ng-if="bill.amendments.size > 1" class="md-toolbar-tools md-hue-2 auto-height">
        <label class="margin-right-20 text-medium">Amendment Version </label>
        <md-radio-group layout="row" layout-sm="column" ng-model="curr.amdVersion">
          <md-radio-button ng-repeat="(version, amd) in bill.amendments.items" class="md-accent md-hue-1"
                           value="{{version}}">
            <span class="text-medium bold" ng-if="$first">Original</span>
            <span class="text-medium bold" ng-if="!$first">Revision {{version}}</span>
            <span class="text-medium bold" ng-if="$last"> (Latest)</span>
            <br/>
            <span class="text-small">{{amd.publishDate | moment:'MMM D, YYYY'}}</span>
          </md-radio-button>
        </md-radio-group>
      </md-toolbar>

      <md-tabs md-selected="curr.selectedView" class="margin-top-10" md-dynamic-height="false">
        <md-tab md-on-select="backToSearch()">
          <md-tab-label>
            <span><i class="icon-magnifying-glass prefix-icon2"></i>Search</span>
          </md-tab-label>
        </md-tab>
        <md-tab label="Details">
          <md-tab-body>
            <md-divider></md-divider>
            <%-- Substituted By --%>
            <section ng-if="curr.selectedView === 1">
              <div ng-if="bill.substitutedBy" class="margin-20">
                <md-button style="text-align: left;text-transform: none;" class="md-warn"
                           ng-href="${ctxPath}/bills/{{bill.substitutedBy.session}}/{{bill.substitutedBy.basePrintNo}}">
                  <i class="icon-copy prefix-icon2"></i>
                  This bill has been substituted by {{bill.substitutedBy.basePrintNo}} - {{bill.substitutedBy.session}}.
                </md-button>
              </div>
              <md-card class="content-card" ng-if="bill.amendments.items[curr.amdVersion].sameAs.size > 0">
                <md-subheader>Same As Bill</md-subheader>
                <md-content>
                  <md-list>
                    <md-list-item ng-repeat="sameAs in bill.amendments.items[curr.amdVersion].sameAs.items">
                      <a class="result-link"
                         ng-href="${ctxPath}/bills/{{sameAs.session}}/{{sameAs.basePrintNo}}?search={{billSearch.term}}&view=1">
                        <div layout="row" layout-sm="column" layout-align-sm="start start" class="padding-10">
                          <div class="text-medium margin-right-20" style="width:120px">
                            <h4 class="no-margin">{{sameAs.printNo}} - {{sameAs.session}}</h4>
                            <p class="no-margin">Same As Bill</p>
                          </div>
                          <div class="text-medium"
                               ng-init="sameAsBill = bill.billInfoRefs.items[sameAs.basePrintNo + '-' + sameAs.session]">
                            <p class="no-margin" ng-if="sameAsBill.sponsor.member.fullName">
                              Sponsored By: {{sameAsBill.sponsor.member.fullName}}
                            </p>
                            <p class="no-margin" ng-if="sameAsBill.status.actionDate">
                              Last Status as of {{sameAsBill.status.actionDate | moment:'MMMM D, YYYY'}} - {{getStatusDesc(sameAsBill.status)}}
                            </p>
                          </div>
                        </div>
                      </a>
                    </md-list-item>
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
              <%-- Co/Multi --%>
              <md-card layout="row" layout-sm="column" layout-align="start start" class="content-card"
                       ng-if="bill.amendments.items[curr.amdVersion].coSponsors.size > 0 ||
                        bill.amendments.items[curr.amdVersion].multiSponsors.size > 0">
                <%-- Co-Prime Sponsor --%>
                <section flex style="width:100%" ng-if="bill.additionalSponsors.size > 0">
                  <md-subheader>{{bill.additionalSponsors.size}} Co-Prime Sponsor(s)</md-subheader>
                  <md-divider/>
                  <md-content style="max-height: 200px;" class="padding-10">
                    <md-list>
                      <md-list-item ng-repeat="coPrimeSponsor in bill.additionalSponsors.items">
                        <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{coPrimeSponsor.imgName}}"
                             style="height:60px;width:45px;"/>
                        <span class="text-medium">{{coPrimeSponsor.fullName}} - District {{coPrimeSponsor.districtCode}}</span>
                      </md-list-item>
                      <md-divider></md-divider>
                    </md-list>
                  </md-content>
                </section>
                <%-- Co Sponsor --%>
                <section flex style="width:100%" ng-if="bill.amendments.items[curr.amdVersion].coSponsors.size > 0">
                  <md-subheader>{{bill.amendments.items[curr.amdVersion].coSponsors.size}} Co Sponsor(s)</md-subheader>
                  <md-divider/>
                  <md-content style="max-height: 200px;" class="padding-10">
                    <md-list>
                      <md-list-item ng-repeat="coSponsor in bill.amendments.items[curr.amdVersion].coSponsors.items">
                          <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{coSponsor.imgName}}"
                               style="height:60px;width:45px;"/>
                          <span class="text-medium">{{coSponsor.fullName}} - District {{coSponsor.districtCode}}</span>
                        <md-divider></md-divider>
                      </md-list-item>
                    </md-list>
                  </md-content>
                </section>
                <%-- Multi Sponsor --%>
                <section flex style="width:100%" ng-if="bill.amendments.items[curr.amdVersion].multiSponsors.size > 0">
                  <md-subheader>{{bill.amendments.items[curr.amdVersion].multiSponsors.size}} Multi Sponsor(s)</md-subheader>
                  <md-divider/>
                  <md-content style="max-height: 200px;" class="padding-10">
                    <md-list>
                      <md-list-item ng-repeat="multiSponsor in bill.amendments.items[curr.amdVersion].multiSponsors.items">
                        <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{multiSponsor.imgName}}"
                             style="height: 60px;width:45px;"/>
                        <span class="text-medium">{{multiSponsor.fullName}} - District {{multiSponsor.districtCode}}</span>
                        <md-divider></md-divider>
                      </md-list-item>
                    </md-list>
                  </md-content>
                </section>
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
                      <p class="no-top-margin text-medium">Voted on Amendment Revision: {{vote.version | prettyAmendVersion}}</p>
                      <md-divider></md-divider>
                      <div layout="row" layout-align="center center" layout-sm="column" layout-align-sm="start start">
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
                            <md-list ng-repeat="(voteType, votes) in vote.memberVotes.items">
                              <md-list-item ng-repeat="voteItem in votes.items">
                                <div style="width:80px;" class="md-tile-left margin-right-10">
                                  <h5 style="margin:10px;">{{voteType}}</h5>
                                </div>
                                <div class="md-tile-left">
                                  <img class="margin-right-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{voteItem.imgName}}"
                                       style="height: 35px;width:29px;"/>
                                </div>
                                <div style="padding:0" class="md-tile-content">
                                  <span class="text-medium">{{voteItem.fullName}}</span>
                                </div>
                              </md-list-item>
                              <md-divider></md-divider>
                            </md-list>
                          </md-content>
                        </div>
                      </div>
                    </div>
                  </section>
                </md-content>
              </md-card>
              <%-- Enacting Clause --%>
              <md-card class="content-card" ng-if="!bill.billType.resolution">
                <md-subheader>Enacting Clause</md-subheader>
                <md-content>
                  <p class="text-medium">{{bill.amendments.items[curr.amdVersion].actClause | default:'Not Available'}}</p>
                </md-content>
              </md-card>
              <%-- Bill Summary --%>
              <md-card class="content-card" ng-if="!bill.billType.resolution">
                <md-subheader>Summary of Bill</md-subheader>
                <md-content>
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
              <%-- Identical Legislation --%>
              <md-card class="content-card" ng-if="bill.previousVersions.size > 0">
                <md-subheader>Previous Versions of this Bill</md-subheader>
                <md-content>
                  <md-list>
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
                            <p class="no-margin" ng-if="prevBill.sponsor.member.fullName">
                              Sponsored By: {{prevBill.sponsor.member.fullName}}
                            </p>
                            <p class="no-margin" ng-if="prevBill.status.actionDate">
                              Last Status as of {{prevBill.status.actionDate | moment:'MMMM D, YYYY'}} - {{getStatusDesc(prevBill.status)}}
                            </p>
                          </div>
                        </md-item-content>
                      </a>
                    </md-item>
                  </md-list>
                </md-content>
              </md-card>
              <%-- Agenda/Cal Refs --%>
              <md-card class="content-card" ng-if="bill.calendars.size > 0 || bill.committeeAgendas.size > 0">
                <md-subheader>Agenda/Calendar References</md-subheader>
                <md-content>
                  <section>
                    <div ng-repeat="agenda in bill.committeeAgendas.items">
                      <a ng-if="bill.committeeAgendas.size > 0" class="gray-2-blue"
                         ng-href="${ctxPath}/agendas/{{agenda.agendaId.year}}/{{agenda.agendaId.number}}/{{agenda.committeeId.name}}">
                        Committee Agenda #{{agenda.agendaId.number}} ({{agenda.agendaId.year}}) - {{agenda.committeeId.name}}
                      </a>
                    </div>
                    <div ng-repeat="calendar in bill.calendars.items">
                      <a class="gray-2-blue" ng-href="${ctxPath}/calendars/{{calendar.year}}/{{calendar.calendarNumber}}#{{bill.printNo}}">
                        Senate Floor Calendar {{calendar.calendarNumber}} ({{calendar.year}})
                      </a>
                    </div>
                  </section>
                </md-content>
              </md-card>
            </section>
          </md-tab-body>
        </md-tab>
        <%-- Sponsor Memo --%>
        <md-tab label="Memo" ng-disabled="bill.billType.resolution">
          <md-tab-body>
            <md-divider></md-divider>
            <section ng-if="curr.selectedView === 2">
              <md-card class="content-card">
                <md-subheader>
                  Sponsor's Memorandum
                </md-subheader>
                <md-content ng-if="bill.amendments.items[curr.amdVersion].memo">
                  <pre class="bill-full-text"
                       ng-bind="bill.amendments.items[curr.amdVersion].memo"></pre>
                </md-content>
                <md-content ng-if="bill.billType.chamber == 'ASSEMBLY'">
                  <div class="text-medium padding-20">Sponsor memos are not provided for Assembly bills.
                    <p ng-if="bill.amendments.items[curr.amdVersion].sameAs.size > 0"
                       ng-init="sameAsBill = bill.amendments.items[curr.amdVersion].sameAs.items[0]">
                      You can view the sponsor memo for the Senate version of this bill here:
                      <a target="_blank"
                         ng-href="${ctxPath}/bills/{{sameAsBill.session}}/{{sameAsBill.basePrintNo}}?view=2">{{sameAsBill.basePrintNo}}</a>.</p>
                  </div>
                </md-content>
                <md-content ng-if="!bill.amendments.items[curr.amdVersion].memo && bill.billType.chamber == 'SENATE'">
                  <div class="text-medium padding-20">Sponsor memo is not available.</div>
                </md-content>
              </md-card>
            </section>
          </md-tab-body>
        </md-tab>
        <%-- Bill Actions --%>
        <md-tab label="Actions">
          <md-tab-body>
            <md-divider></md-divider>
            <section ng-if="curr.selectedView === 3">
              <md-card class="content-card">
                <md-subheader>
                  Bill Actions
                </md-subheader>
                <md-content>
                  <md-list>
                    <md-list-item class="md-2-line" ng-repeat="action in bill.actions.items">
                      <div class="md-list-item-text">
                        <h2 class="text-medium">{{action.date | moment:'MMMM D, YYYY'}} - {{action.chamber}} - <span class="gray7">{{action.billId.printNo}}</span></h2>
                        <h3 class="text-medium margin-10 capitalize">{{action.text | lowercase}}</h3>
                      </div>
                      <md-divider ng-if="!$last"/>
                    </md-list-item>
                  </md-list>
                </md-content>
              </md-card>
            </section>
          </md-tab-body>
        </md-tab>
        <%-- Bill Text --%>
        <md-tab label="Full Text">
          <md-tab-body>
            <md-divider></md-divider>
            <section ng-if="curr.selectedView === 4">
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
              <md-card class="content-card">
                <md-subheader>Full Text</md-subheader>
                <md-content class="margin-10 padding-20">
                <span ng-if="!loading && !bill.amendments.items[curr.amdVersion].fullText">
                  Not available.</span>
                  <span ng-if="loading">Loading full text, please wait.</span>
                  <div ng-if="bill.amendments.items[curr.amdVersion].fullText">
                    <pre ng-if="!diffHtml" class="margin-left-20 bill-full-text">{{bill.amendments.items[curr.amdVersion].fullText}}</pre>
                    <pre ng-if="diffHtml" class="margin-left-20 bill-full-text" ng-bind-html="diffHtml"></pre>
                  </div>
                </md-content>
              </md-card>
            </section>
          </md-tab-body>
        </md-tab>
        <%-- Updates --%>
        <md-tab label="Updates" md-on-select="initialGetUpdates()">
          <md-tab-body>
            <md-divider></md-divider>
            <section ng-if="curr.selectedView === 5">
              <md-card class="content-card">
                <md-content layout="row" layout-sm="column">
                  <div flex>
                    <label>Filter by update type: </label>
                    <select ng-model="curr.updateTypeFilter" ng-change="getUpdates()" class="margin-left-10">
                      <option value="">All</option>
                      <option value="action">Action</option>
                      <option value="active_version">Active Version</option>
                      <option value="approval">Approval Memo</option>
                      <option value="cosponsor">Co Sponsor</option>
                      <option value="act_clause">Enacting Clause</option>
                      <option value="fulltext">Full Text</option>
                      <option value="law">Law</option>
                      <option value="memo">Memo</option>
                      <option value="multisponsor">Multi Sponsor</option>
                      <option value="sponsor">Sponsor</option>
                      <option value="status">Status</option>
                      <option value="summary">Summary</option>
                      <option value="title">Title</option>
                      <option value="veto">Veto</option>
                      <option value="vote">Vote</option>
                    </select>
                  </div>
                  <div flex>
                    <label class="margin-left-10">Order</label>
                    <select ng-model="curr.updateOrder" ng-change="getUpdates()" class="margin-left-10">
                      <option value="asc">Oldest First</option>
                      <option value="desc">Newest First</option>
                    </select>
                  </div>
                </md-content>
              </md-card>
              <update-list update-response="updateHistoryResponse" show-details="true"></update-list>
            </section>
          </md-tab-body>
        </md-tab>
    </section>
    <section ng-if="response.success === false">
      <md-card>
        <md-content class="content-card padding-20">
          <h4>Please note that data for bills and resolutions are only available from 2009.</h4>
        </md-content>
      </md-card>
    </section>
  </section>
</section>