<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<dir-pagination-controls class="text-align-center" pagination-id="bill-updates"
                         on-page-change="pageChange(newPageNumber)" boundary-links="true" max-size="10"></dir-pagination-controls>
<md-list>
  <md-list-item dir-paginate="billUpdate in billUpdateResponse.result.items | itemsPerPage: 20"
                total-items="billUpdateResponse.total" current-page="pagination.currPage"
                ng-init="bill = billUpdate.item" pagination-id="bill-updates"
                class="margin-bottom-10">
    <a class="result-link"
       ng-href="${ctxPath}/bills/{{bill.session}}/{{bill.basePrintNo}}?search={{billSearchTerm}}&searchPage={{pagination.currPage}}">
      <div layout-padding layout="column">
        <div layout="row" layout-align="start center">
          <div ng-show="showImg">
            <img class="margin-right-10" ng-if="bill.sponsor"
                 ng-src="${ctxPath}/static/img/business_assets/members/mini/{{bill.sponsor.member.imgName}}"
                 style="width: 45px;"/>
          </div>
          <div flex="none" class="margin-right-20" style="width: 160px;">
            <h3 class="margin-top-10 no-bottom-margin">{{bill.printNo}} - {{bill.session}}</h3>
            <p ng-if="bill.sponsor" class="no-margin text-medium" ng-if="bill.sponsor.member.fullName">
              {{bill.sponsor.member.fullName}}
            </p>
          </div>
          <div flex layout="row">
            <p ng-if="showTitle" class="text-medium">
              <span>{{bill.title | limitTo:150}}</span>
            </p>
          </div>
          <div layout="column" class="text-small margin-left-16">
            <p class="no-margin"><strong>Last Published:</strong> {{billUpdate.sourceDateTime | moment:'llll'}}</p>
            <p class="no-margin"><strong>Last Processed:</strong> {{billUpdate.processedDateTime | moment:'llll'}}</p>
            <p class="no-margin"><strong>Update Source Id:</strong> {{billUpdate.sourceId}}</p>
          </div>
        </div>
        <div layout="row" ng-if="showDetail" class="margin-top-20 gray1-bg">
          <div flex>
            <span class="text-medium bold green2">{{billUpdate.action}} {{billUpdate.scope}}</span>
            <table class="bill-updates-table" style="width:100%;">
              <thead>
              <tr>
                <th style="width:150px;">Field Name</th>
                <th>Data</th>
              </tr>
              </thead>
              <tbody>
              <tr ng-repeat="(field, value) in billUpdate.fields">
                <td>{{field}}</td>
                <td><pre>{{value}}</pre></td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </a>
    <md-divider ng-if="!$last"></md-divider>
  </md-list-item>
</md-list>
<dir-pagination-controls class="text-align-center" pagination-id="bill-updates" on-page-change="pageChange(newPageNumber)"
                         boundary-links="true" max-size="10"></dir-pagination-controls>