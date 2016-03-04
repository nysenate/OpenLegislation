<section class="{{sectionType}}">
  <div class="listing-filter">
    <label class="margin-right-10">Filter calendar listing</label>
    <input type="text" class="padding-5" ng-model="calEntryFilter"/>
  </div>
  <md-virtual-repeat-container class="cal-entry-repeat-container white-bg" md-top-index="scrollTo">
    <div class="repeated-item" md-virtual-repeat="calEntry in calEntries | filter:calEntryFilter" md-item-size="150"
           data-print-no="{{calEntry.printNo}}" data-cal-no="{{calEntry.billCalNo}}">
        <div flex="none" layout="column" layout-align="start center" layout-gt-sm="row"
             ng-class="{'cal-entry-highlight': calEntry.printNo === highlightValue || calEntry.billCalNo == highlightValue}">
          <div>
            <img class="margin-right-10" ng-if="calEntry.sponsor"
                 ng-src="${ctxPath}/static/img/business_assets/members/mini/{{calEntry.sponsor.member.imgName}}"
                 style="width: 45px;"/>
          </div>
          <div flex="none" class="margin-right-20" style="width: 180px;">
            <h3 class="margin-top-10 no-bottom-margin">
              <a target="_blank" class="blue3"
                 ng-href="${ctxPath}/bills/{{calEntry.session}}/{{calEntry.basePrintNo}}?version={{calEntry.selectedVersion}}&view=1">
                {{calEntry.basePrintNo}}{{calEntry.selectedVersion}} - {{calEntry.session}}
              </a>
              <md-tooltip>View this bill</md-tooltip>
            </h3>
            <h3 class="no-margin">
              <a class="cal-entry-cal-no blue4 text-medium"
                   ng-href="{{getCalBillNumUrl(year, calEntry.billCalNo)}}">
                Bill Calendar No. {{calEntry.billCalNo}}
                <md-tooltip>Search for calendars that contain this bill</md-tooltip>
              </a>
            </h3>
            <p ng-if="calEntry.sponsor" class="no-margin text-medium" ng-if="calEntry.sponsor.member.fullName">
              {{calEntry.sponsor.member.fullName}}
            </p>
          </div>
          <div flex hide-xs layout="column" ng-if="calEntry.status">
            <div>
              <p class="no-margin text-medium">{{calEntry.title}}</p>
              <p class="no-margin text-small" ng-if="calEntry.status.actionDate">
                {{calEntry.status.actionDate | moment:'MMMM D, YYYY'}} - {{billUtils.getStatusDesc(calEntry.status)}}
              </p>
            </div>
            <milestones milestone-arr="calEntry.milestones" chamber="calEntry.billType.chamber"></milestones>
          </div>
        </div>
      </div>
  </md-virtual-repeat-container>
</section>