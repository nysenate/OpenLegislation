<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-section" ng-controller="MemberViewCtrl">
  <md-card class="content-card" flex>
    <md-item>
      <md-item-content>
        <div style="width:180px;">
          <img class="padding-10" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{memberView.result.imgName}}"
               style="width:80%;">
        </div>
        <div class="md-tile-content">
          <h2 class="no-margin">{{memberView.result.fullName}}</h2>
          <h3 class="gray10 no-margin">New York State {{memberView.result.chamber | capitalize}}</h3>
          <h6 class="gray7 no-margin">Member Id: {{memberView.result.memberId}}</h6>
          <h5 class="gray7 no-margin">District {{memberView.result.districtCode}}</h5>
          <a ng-href="${ctxPath}/bills?search=sponsor.member.fullName:&quot;{{memberView.result.fullName}}&quot;&searchPage=1">
            <h4 class="">View Legislation</h4>
          </a>
        </div>

      </md-item-content>
    </md-item>
  </md-card>
  <small>More coming soon..</small>
</section>
