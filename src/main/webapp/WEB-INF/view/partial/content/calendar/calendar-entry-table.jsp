<table>
    <thead>
        <tr>
            <th>Bill &#35;</th>
            <th>Title &amp; Sponsor</th>
            <th>Cal &#35;</th>
        </tr>
    </thead>
    <tbody>
        <tr ng-repeat="calEntry in calEntries">
            <td>
                <a ng-bind="calEntry.basePrintNo" class="cal-entry-print-no"
                   ng-href="{{billPageBaseUrl}}/{{calEntry.session}}/{{calEntry.basePrintNo}}"></a>
            </td>
            <td>
                <div ng-bind="calEntry.title" class="cal-entry-title"></div>
                <div>
                    <img class="cal-entry-sponsor-img left margin-right-10" src="http://placekitten.com/g/35/35"/>
                    <span>
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
            </td>
            <td><a ng-bind="calEntry.billCalNo" class="cal-entry-cal-no"></a></td>
        </tr>
    </tbody>
</table>