<md-list>
    <md-item hide-sm>
        <md-item-content layout="row">
            <h4 style="width: 70px"><br/>Bill &#35;</h4>
            <h4 style="width: 40px">Cal.<br/> Bill &#35;</h4>
            <h4 class="md-tile-content no-margin"><br/>Title &amp; Sponsor</h4>
        </md-item-content>
        <md-divider class="md-default-theme"></md-divider>
    </md-item>
    <md-item ng-repeat="calEntry in calEntries">
        <md-item-content layout="row" class="md-padding">
            <div style="width: 70px" hide-sm>
                <a ng-bind="calEntry.basePrintNo" class="text-large"
                   ng-href="{{billPageBaseUrl}}/{{calEntry.session}}/{{calEntry.basePrintNo}}"></a>
            </div>
            <div style="width: 40px" hide-sm>
                <a ng-bind="calEntry.billCalNo" class="cal-entry-cal-no"
                        ng-href="{{getCalBillNumUrl(calEntry.billCalNo)}}"></a>
            </div>
            <div class="md-tile-content">
                <a ng-bind="calEntry.basePrintNo" class="text-medium" hide-gt-sm
                   ng-href="{{billPageBaseUrl}}/{{calEntry.session}}/{{calEntry.basePrintNo}}"></a>
                <a hide-gt-sm class="cal-entry-cal-no">Calendar Bill \#{{calEntry.billCalNo}}</a>
                <div ng-bind="calEntry.title" class="truncate-text"></div>
                <div layout="row" hide-sm style="height: 40px">
                    <div style="overflow: hidden">
                        <img style="max-width: 40px; max-height: 52px;"
                             ng-src="${ctxPath}/static/img/business_assets/members/mini/{{calEntry.sponsor.member.imgName}}"
                             err-src="${ctxPath}/static/img/NYSS_seal_fancy.jpg"/>
                    </div>
                    <span class="margin-left-10">
                        <span ng-if="calEntry.sponsor.member">
                            <span ng-bind="calEntry.sponsor.member.fullName"></span> <br>
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