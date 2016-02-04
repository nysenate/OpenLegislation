<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<md-list>
  <md-list-item ng-repeat="bill in billViews" style="padding-left:0">
    <a class="result-link"
       ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearchTerm}}&view=1">
      <div flex="none" layout="column" layout-gt-sm="row">
        <div ng-show="showImg">
          <img class="margin-right-10" ng-if="bill.sponsor"
               ng-src="${ctxPath}/static/img/business_assets/members/mini/{{bill.sponsor.member.imgName}}"
               style="width: 45px;"/>
        </div>
        <div flex="none" class="margin-right-20" style="width: 180px;">
          <h3 class="margin-top-10 no-bottom-margin">{{bill.printNo}} - {{bill.session}}</h3>
          <p ng-if="bill.sponsor" class="no-margin text-medium" ng-if="bill.sponsor.member.fullName">
            {{bill.sponsor.member.fullName}}
          </p>
        </div>
        <div flex layout="column" ng-if="bill.status" ng-class="{'margin-top-10': !showTitle}">
          <div flex>
            <p ng-if="showTitle" class="no-margin">{{bill.title}}</p>
            <p class="no-margin text-small" ng-if="bill.status.actionDate">
              {{bill.status.actionDate | moment:'MMMM D, YYYY'}} - {{billUtils.getStatusDesc(bill.status)}}
            </p>
          </div>
          <milestones ng-if="!bill.billType.resolution" flex milestone-arr="bill.milestones" chamber="bill.billType.chamber"></milestones>
        </div>
      </div>
    </a>
  </md-list-item>
</md-list>