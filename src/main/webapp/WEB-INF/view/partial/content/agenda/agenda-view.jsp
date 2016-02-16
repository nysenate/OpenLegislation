<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="AgendaCtrl">
  <section class="content-section" ng-controller="AgendaViewCtrl">
    <md-progress-linear class="md-accent md-hue-2" md-mode="{{(curr.loading) ? 'query' : ''}}"></md-progress-linear>
    <section ng-if="response.success === true">
      <md-toolbar class="md-toolbar-tools md-tall" layout="row">
        <div flex-gt-xs="33" flex-xs="100">
          <div class="bold text-large">
            <i class="icon-calendar prefix-icon2"></i>Week of {{agenda.weekOf | moment:'MMM Do'}}
          </div>
          <span class="text-medium">Published {{agenda.publishedDateTime | moment:'MM/DD/YYYY h:mm:ss A'}}</span>
        </div>
        <div hide-xs flex class="text-align-center" style="border-right: 1px solid #eee;">
          <div class="bold">{{agenda.totalAddendum}}</div>
          <span class="text-medium">Addenda</span></div>
        <div hide-xs flex class="text-align-center" style="border-right: 1px solid #eee;">
          <div class="bold">{{agenda.totalCommittees}}</div><span class="text-medium">Committee(s)</span>
        </div>
        <div hide-xs flex class="text-align-center" style="border-right: 1px solid #eee;">
          <div class="bold">{{agenda.totalBillsConsidered}}</div><span class="text-medium">Bills on Agenda</span>
        </div>
        <div hide-xs flex class="text-align-center">
          <div class="bold">{{agenda.totalBillsVotedOn}}</div><span class="text-medium">Bills Voted On</span>
        </div>
      </md-toolbar>
      <md-tabs md-selected="1" layout-padding class="margin-top-10 md-hue-2" md-dynamic-height="true" md-stretch-tabs="never">
        <md-tab md-on-select="backToSearch()">
          <md-tab-label>
            <span><i class="icon-search prefix-icon2"></i>{{searchTabName}}</span>
          </md-tab-label>
        </md-tab>
        <md-tab>
          <md-tab-label>Committees</md-tab-label>
          <md-tab-body>
            <section>
              <md-card class="content-card">
                <p class="text-medium margin-left-10 gray10">
                  <i class="prefix-icon2 icon-info"></i>Note: A committee may receive multiple updates (i.e. addenda) which can either overwrite prior meeting details or supplement it.
                </p>
              </md-card>
              <toggle-panel label="{{comm.committeeId.name}}" data-committee="{{comm.committeeId.name.toLowerCase()}}"
                            class="content-card"
                            ng-repeat="comm in agenda.committeeAgendas.items"
                            open="{{selectedComm[comm.committeeId.name.toLowerCase()]}}">
                <section ng-repeat="addn in comm.addenda.items">
                  <p class="bold padding-10 blue1 no-bottom-margin">
                    <span ng-if="addn.addendumId === ''">Initial&nbsp;</span>Addendum {{addn.addendumId}}
                  </p>
                  <md-divider></md-divider>
                  <section>
                    <div layout-gt-sm="row" layout="column">
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
                            <md-list-item ng-repeat="senator in addn.voteInfo.attendanceList.items">
                              <div style="width:20px" class="margin-right-10">{{senator.rank}}</div>
                              <img style="max-height:50px;width:auto;"
                                   ng-src="${ctxPath}/static/img/business_assets/members/mini/{{senator.member.imgName}}"/>
                              <div class="md-tile-content" style="padding:10px;">
                                {{senator.member.fullName}} ({{senator.party}}) - {{senator.attend}}
                              </div>
                            </md-list-item>
                          </md-list>
                        </md-content>
                      </md-card>
                    </div>
                    <toggle-panel class="margin-top-10 no-margin content-card" ng-if="addn.bills.size > 0" open="false"
                                  label="Bills added to the Agenda ({{addn.bills.size}})">
                      <%--<md-content style="max-height:500px;">--%>
                        <%--<md-list>--%>
                          <%--<a id="{{bill.billId.basePrintNo}}-{{bill.billId.session}}" class="result-link"--%>
                             <%--ng-repeat="bill in addn.bills.items"--%>
                             <%--ng-href="${ctxPath}/bills/{{bill.billId.session}}/{{bill.billId.basePrintNo}}">--%>
                            <%--<md-list-item class="md-3-line" style="cursor: pointer;">--%>
                              <%--<div class="md-list-item-text" ng-init="billVote = votes[bill.billId.basePrintNo][comm.committeeId.name]">--%>
                                <%--<h3>--%>
                                  <%--<span class="blue3 no-margin bold">{{bill.billId.printNo}} - {{bill.billId.session}}</span>--%>
                                  <%--<span class="margin-left-20">{{bill.billInfo.sponsor.member.fullName}}</span>--%>
                                <%--</h3>--%>
                                <%--<p class="text-medium" ng-if="!highlights.title">{{bill.billInfo.title}}</p>--%>

                                <%--<p>--%>
                                  <%--<span ng-show="billVote.action">Action: {{billVote.action | agendaActionFilter}}</span>--%>
                                <%--</p>--%>
                                <%--<md-divider></md-divider>--%>
                              <%--</div>--%>
                            <%--</md-list-item>--%>
                          <%--</a>--%>
                        <%--</md-list>--%>
                      <%--</md-content>--%>
                      <agenda-bill-listing agenda-bills="addn.bills.items" votes="votes" committee="comm" show-title="true"
                                           show-img="false">
                      </agenda-bill-listing>
                    </toggle-panel>
                  </section>
                </section>
              </toggle-panel>
            </section>
          </md-tab-body>
        </md-tab>
        <md-tab label="Updates" md-on-select="getUpdates()">
          <md-tab-body>
            <md-card class="content-card">
              <md-content layout-gt-sm="row" layout="column">
                <div flex>
                  <label>Sort By: </label>
                  <select ng-model="curr.updateOrder" ng-change="getUpdates()" class="margin-left-10">
                    <option value="desc">Newest First</option>
                    <option value="asc">Oldest First</option>
                  </select>
                </div>
              </md-content>
            </md-card>
            <update-list update-response="updatesResponse" pagination="updatesPagination" show-details="true"></update-list>
          </md-tab-body>
        </md-tab>
      </md-tabs>
    </section>
    <section ng-if="response.success == false">
      <p>Sorry, there was an issue retrieving this agenda.</p>
    </section>
  </section>
</section>
