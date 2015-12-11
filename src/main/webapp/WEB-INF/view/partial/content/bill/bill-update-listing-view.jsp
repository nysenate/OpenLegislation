<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<dir-pagination-controls class="text-align-center" pagination-id="bill-updates" boundary-links="true" max-size="10"></dir-pagination-controls>
<md-list>
  <md-list-item dir-paginate="billUpdate in billUpdates.result.items | itemsPerPage: 20"
                total-items="billUpdates.total" current-page="pagination.currPage"
                ng-init="bill = billUpdate.item" pagination-id="bill-updates"
                class="margin-bottom-10">
    <a class="result-link"
       ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearchTerm}}&searchPage={{pagination.currPage}}">
      <div flex="none" layout-padding layout="column" layout-gt-sm="row">
        <h4 style="color:#444"><strong>Last Published:</strong> {{billUpdate.sourceDateTime | moment:'llll'}}</h4>
        <h4 style="color:#444"><strong>Last Processed:</strong> {{billUpdate.processedDateTime | moment:'MMM D, YYYY h:mm:ss A'}}</h4>
        <h4 style="color:#444"><strong>Update Source Id:</strong> {{billUpdate.sourceId}}</h4>
        <div layout="row">
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
        </div>
        <div flex layout="column" ng-if="bill.status" ng-class="{'margin-top-10': !showTitle}">
          <div flex>
            <p ng-if="showTitle" class="no-margin text-medium">
              <span ng-if="!highlights.title">{{bill.title | limitTo:150}}</span>
              <span ng-if="highlights.title" ng-bind-html="highlights.title[0]"></span>
            </p>
            <p class="no-margin text-medium blue2" ng-if="bill.status.actionDate">
              {{bill.status.actionDate | moment:'MMMM D, YYYY'}} - {{billUtils.getStatusDesc(bill.status)}}
            </p>
          </div>
          <milestones flex milestone-arr="bill.milestones" chamber="bill.billType.chamber"></milestones>
        </div>
      </div>
    </a>
    <md-divider ng-if="!$last"></md-divider>
  </md-list-item>
</md-list>
<dir-pagination-controls class="text-align-center" pagination-id="bill-updates" boundary-links="true" max-size="10"></dir-pagination-controls>