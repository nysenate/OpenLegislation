<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="AgendaCtrl">
  <section class="content-section" ng-controller="AgendaViewCtrl">
    <h4 ng-hide="response.success === true">Loading Agenda</h4>
    <section ng-if="response.success === true">
      <md-toolbar class="md-toolbar-tools md-tall" layout="row">
        <div flex-gt-sm="33" flex-sm="100">
          <div class="bold">
            <i class="icon-calendar prefix-icon2"></i>Week of {{agenda.weekOf | moment:'MMM Do'}}
          </div>
          <span class="text-medium">Published {{agenda.publishedDateTime | moment:'MM/DD/YYYY h:mm:ss A'}}</span>
        </div>
        <div hide-sm flex class="text-align-center">
          <div class="bold">{{agenda.totalAddendum}}</div>
          <span class="text-medium">Addenda</span></div>
        <div hide-sm flex class="text-align-center">
          <div class="bold">{{agenda.totalCommittees}}</div><span class="text-medium">Committee(s)</span>
        </div>
        <div hide-sm flex class="text-align-center">
          <div class="bold">{{agenda.totalBillsConsidered}}</div><span class="text-medium">Bills on Agenda</span>
        </div>
        <div hide-sm flex class="text-align-center">
          <div class="bold">{{agenda.totalBillsVotedOn}}</div><span class="text-medium">Bills Voted On</span>
        </div>
      </md-toolbar>
      <md-tabs md-selected="1" class="margin-top-10" md-stretch-tabs="never">
        <md-tab md-on-select="backToSearch()">
          <md-tab-label>
            <span><i class="icon-search prefix-icon2"></i>{{searchTabName}}</span>
          </md-tab-label>
        </md-tab>
        <md-tab>
          <md-tab-label>Committees</md-tab-label>
          <section>
            <md-card class="content-card">
              <p class="text-medium margin-left-10 gray10">
                <i class="prefix-icon2 icon-question"></i>Click on a committee to view the agenda.
                <md-divider></md-divider>
                <i class="prefix-icon2 icon-info"></i>Note: A committee may receive multiple updates (i.e. addenda) which can either overwrite prior meeting details or supplement it.
              </p>
            </md-card>
            <toggle-panel label="{{comm.committeeId.name}}" data-committee="{{comm.committeeId.name.toLowerCase()}}"
                          class="content-card"
                          ng-repeat="comm in agenda.committeeAgendas.items"
                          open="{{selectedComm[comm.committeeId.name.toLowerCase()]}}">
              <section ng-repeat="addn in comm.addenda.items">
                <p class="bold text-medium text-align-center padding-10 blue1 no-bottom-margin">
                  <span ng-if="addn.addendumId === ''">Initial&nbsp;</span>Addendum {{addn.addendumId}}
                </p>
                <div layout="row" layout-sm="column">
                  <md-card flex class="content-card no-margin">
                    <md-subheader>Meeting Information</md-subheader>
                    <md-card-content class="text-medium">
                      <p><strong>Meeting Date/Time:</strong> {{addn.meeting.meetingDateTime | moment:'MMM D, YYYY h:mm A'}}</p>
                      <p><strong>Location:</strong> {{addn.meeting.location}}</p>
                      <p><strong>Chair:</strong> {{addn.meeting.chair}}</p>
                      <p><strong>Notes:</strong> <pre style="white-space: pre-line">{{addn.meeting.notes}}</pre></p>
                    </md-card-content>
                  </md-card>
                  <md-card flex ng-if="addn.hasVotes && addn.voteInfo.attendanceList.items" class="content-card no-margin">
                    <md-subheader>Voting Attendance ({{addn.voteInfo.attendanceList.size}})</md-subheader>
                    <md-content class="text-medium" style="max-height:250px;">
                      <md-list>
                        <md-item ng-repeat="senator in addn.voteInfo.attendanceList.items">
                          <md-item-content>
                            <div style="width:20px" class="margin-right-10">{{senator.rank}}</div>
                            <div>
                              <img style="max-height:50px;width:auto;"
                                   ng-src="${ctxPath}/static/img/business_assets/members/mini/{{senator.member.imgName}}"/>
                            </div>
                            <div class="md-tile-content" style="padding:10px;">
                              {{senator.member.fullName}} ({{senator.party}}) - {{senator.attend}}
                            </div>
                          </md-item-content>
                        </md-item>
                      </md-list>
                    </md-content>
                  </md-card>
                </div>
                <toggle-panel class="content-card no-margin" label="Bills added to the Agenda ({{addn.bills.size}})" open="false">
                  <md-content style="max-height:500px;">
                    <md-list>
                      <a id="{{bill.billId.basePrintNo}}-{{bill.billId.session}}" class="result-link" ng-repeat="bill in addn.bills.items"
                         ng-href="${ctxPath}/bills/{{bill.billId.session}}/{{bill.billId.basePrintNo}}">
                        <md-item>
                          <md-item-content layout-sm="column" layout-align-sm="center start" style="cursor: pointer;">
                            <div style="width:180px;padding:16px;">
                              <h3 class="no-margin"><span>{{bill.billId.printNo}}</span> - {{bill.billId.session}}</h3>
                              <h5 class="no-margin">{{bill.billInfo.sponsor.member.fullName}}</h5>
                              <h5 ng-show="bill.message" class="no-margin green2"><i class="icon-forward prefix-icon2"></i>{{bill.message}}</h5>
                            </div>
                            <div flex class="md-tile-content">
                              <h4><span class="text-medium">{{bill.billInfo.title}}</span></h4>
                            </div>
                            <div flex class="md-tile-content" ng-if="addn.hasVotes" ng-init="billVote = votes[bill.billId.basePrintNo]">
                              <h5 ng-if="billVote.amended">Amended</h5>
                              <h4 ng-show="billVote.vote.memberVotes.size > 0">Votes: <span class="text-medium no-margin" ng-repeat="(type, vote) in billVote.vote.memberVotes.items">
                                  {{type}} ({{vote.size}})
                              </span></h4>
                              <h5 ng-show="billVote.action" class="no-margin">Action: {{billVote.action | agendaActionFilter}}</h5>
                              <h5 ng-hide="billVote" class="no-margin">No Vote Taken On Bill</h5>
                            </div>
                          </md-item-content>
                        </md-item>
                      </a>
                    </md-list>
                  </md-content>
                </toggle-panel>
              </section>
            </toggle-panel>
          </section>
        </md-tab>
        <md-tab label="Change Log">

        </md-tab>
      </md-tabs>
    </section>
    <section ng-if="response.success == false">

    </section>
  </section>
</section>
