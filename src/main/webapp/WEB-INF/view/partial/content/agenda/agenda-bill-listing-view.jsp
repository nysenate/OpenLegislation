<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
  <div ng-repeat="bill in agendaBills" style="padding:15px 0">
    <a class="result-link"
       ng-href="${ctxPath}/bills/{{bill.billId.session}}/{{bill.billId.basePrintNo}}?view=2">
      <div layout="column" layout-gt-sm="row" layout-align="start center"
           ng-init="billVote = votes[bill.billId.basePrintNo][committee.committeeId.name]">
        <div flex="20" layout="column">
          <div layout="row">
            <div ng-show="showImg">
              <img class="margin-right-10" ng-if="bill.billInfo.sponsor"
                   ng-src="${ctxPath}/static/img/business_assets/members/mini/{{bill.billInfo.sponsor.member.imgName}}"
                   style="width: 45px;"/>
            </div>
            <div flex="none" class="margin-right-20">
              <h3 class="margin-top-10 no-bottom-margin">{{bill.billId.printNo}} - {{bill.billId.session}}</h3>
              <p ng-if="bill.billInfo.sponsor" class="no-margin text-medium" ng-if="bill.billInfo.sponsor.member.fullName">
                {{bill.billInfo.sponsor.member.fullName}}
              </p>
            </div>
          </div>
        </div>
        <div flex="25" layout="column" class="margin-right-20">
          <p class="no-margin text-small">
            <strong>Votes</strong>
            <span ng-hide="billVote">No Vote Taken On Bill</span>
          </p>
          <md-divider></md-divider>
          <div>
            <div ng-repeat="(type, vote) in billVote.vote.memberVotes.items" class="agenda-vote-chip"
                 ng-class="{'positive': (type === 'AYE' || type === 'AYEWR'), 'negative': (type === 'NAY')}">
              <span>{{type}} ({{vote.size}})</span>
            </div>
          </div>
          <p class="no-bottom-margin text-small" ng-show="billVote.action"><strong>Action:</strong> {{billVote.action | agendaActionFilter}}</p>
        </div>
        <div flex="55" layout="column" ng-if="bill.billInfo.status" ng-class="{'margin-top-10': !showTitle}">
          <div flex>
            <p ng-if="showTitle" class="no-margin text-small">{{bill.billInfo.title}}</p>
            <p class="no-margin text-small" ng-if="bill.billInfo.status.actionDate">
              {{bill.billInfo.status.actionDate | moment:'MMMM D, YYYY'}} - {{billUtils.getStatusDesc(bill.billInfo.status)}}
            </p>
            <milestones flex milestone-arr="bill.billInfo.milestones" chamber="bill.billInfo.billType.chamber"></milestones>
          </div>
        </div>
      </div>
    </a>
  </div>
</div>