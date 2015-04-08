<section class="{{sectionType}}" style="max-height: 65vh; overflow-y: scroll">
<md-list>
    <md-item hide-sm>
        <md-item-content layout="row">
            <h4 style="width: 70px"><br/>Print &#35;</h4>
            <h4 style="width: 40px">Bill<br/>Cal &#35;</h4>
            <h4 class="md-tile-content no-margin"><br/>Title &amp; Sponsor</h4>
        </md-item-content>
        <md-divider class="md-default-theme"></md-divider>
    </md-item>
    <md-item ng-repeat="calEntry in calEntries" data-print-no="{{calEntry.printNo}}" data-cal-no="{{calEntry.billCalNo}}">
        <md-item-content layout="row" class="md-padding"
                         ng-class="{'cal-entry-highlight': calEntry.printNo === highlightValue || calEntry.billCalNo == highlightValue}">
            <div style="width: 70px" hide-sm>
                <a ng-bind="calEntry.basePrintNo" class="text-large blue3"
                   ng-href="{{billPageBaseUrl}}/{{calEntry.session}}/{{calEntry.basePrintNo}}">
                    <md-tooltip>View this bill</md-tooltip>
                </a>
            </div>
            <div style="width: 40px" hide-sm>
                <a ng-bind="calEntry.billCalNo" class="cal-entry-cal-no blue4"
                        ng-href="{{getCalBillNumUrl(year, calEntry.billCalNo)}}">
                  <md-tooltip>Search for calendars <br>that contain this bill</md-tooltip>
                </a>
            </div>
            <div class="md-tile-content">
                <a ng-bind="calEntry.basePrintNo" class="text-medium" hide-gt-sm
                   ng-href="{{billPageBaseUrl}}/{{calEntry.session}}/{{calEntry.basePrintNo}}"></a>
                <span hide-gt-sm>Bill Calendar </span>
                <a hide-gt-sm class="cal-entry-cal-no" ng-href="{{getCalBillNumUrl(year, calEntry.billCalNo)}}">
                  \#{{calEntry.billCalNo}}
                </a>
                <div class="margin-bottom-10 text-medium" ng-bind="calEntry.title"></div>
                <div layout="row" hide-sm style="height: 40px">
                    <div style="overflow: hidden">
                        <img style="max-width: 40px; max-height: 52px;"
                             ng-src="${ctxPath}/static/img/business_assets/members/mini/{{calEntry.sponsor.member.imgName}}"
                             err-src="${ctxPath}/static/img/NYSS_seal_fancy.jpg"/>
                    </div>
                    <span class="margin-left-10 text-medium">
                        <span ng-if="calEntry.sponsor.member">
                            <span class="blue1 bold" ng-bind="calEntry.sponsor.member.fullName"></span> <br>
                            <span>
                                {{calEntry.sponsor.member.districtCode | ordinalSuffix}} District
                            </span>
                        </span>
                        <span ng-if="calEntry.sponsor.budget">Budget Sponsor</span>
                        <span ng-if="calEntry.sponsor.rules">Rules Sponsor</span>
                    </span>
                </div>
            </div>
        </md-item-content>
        <md-divider ng-show="!$last" class="md-default-theme"></md-divider>
    </md-item>
</md-list>
</section>