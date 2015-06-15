<section class="{{sectionType}}" style="max-height: 65vh; overflow-y: scroll">
  <md-list>
    <md-list-item class="md-3-line" ng-repeat="calEntry in calEntries"
                  data-print-no="{{calEntry.printNo}}" data-cal-no="{{calEntry.billCalNo}}">
      <div class="md-list-item-text"
           ng-class="{'cal-entry-highlight': calEntry.printNo === highlightValue || calEntry.billCalNo == highlightValue}">
        <h3>
          <a class="bold blue3"
             ng-href="{{calEntry.publishedDateTime ? billPageBaseUrl + '/' + calEntry.session + '/' + calEntry.basePrintNo : ''}}">
            {{calEntry.basePrintNo}}<md-tooltip ng-if="calEntry.publishedDateTime">View this bill</md-tooltip>
          </a>
          <a class="cal-entry-cal-no bold blue4 text-medium margin-left-20"
             ng-href="{{getCalBillNumUrl(year, calEntry.billCalNo)}}">
            Bill Calendar No. {{calEntry.billCalNo}}
            <md-tooltip>Search for calendars that contain this bill</md-tooltip>
          </a>
        </h3>
        <p class="margin-bottom-10 text-medium" ng-bind="calEntry.title"></p>
        <div>
          <div layout="row" style="height: 40px" ng-if="calEntry.sponsor">
            <div class="margin-right-10" style="overflow: hidden">
              <img style="max-width: 40px; max-height: 52px;"
                   ng-src="${ctxPath}/static/img/business_assets/members/mini/{{calEntry.sponsor.member.imgName || 'null'}}"
                   err-src="${ctxPath}/static/img/NYSS_seal_fancy.jpg"/>
            </div>
            <span class="text-medium">
              <span ng-if="calEntry.sponsor.member">
                <span class="blue1 bold" ng-bind="calEntry.sponsor.member.fullName"></span> <br>
                  <span>{{calEntry.sponsor.member.districtCode | ordinalSuffix}} District</span>
              </span>
              <span ng-if="calEntry.sponsor.budget">Budget Sponsor</span>
              <span ng-if="calEntry.sponsor.rules">Rules Sponsor</span>
            </span>
          </div>
        </div>
      </div>
      <md-divider ng-show="!$last" class="md-default-theme"></md-divider>
    </md-list-item>
  </md-list>
</section>