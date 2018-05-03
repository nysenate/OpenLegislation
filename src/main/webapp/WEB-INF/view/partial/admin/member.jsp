<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="MemberCtrl" ng-init="init()">
    <!--
    <div class="member-header-section">
        <h1 class="member-header">{{headerText}}</h1>
    </div>
    -->

    <!-- STEP ONE -->
    <md-card class="verify-content-section" ng-show="member">
        <md-card-content>
            <div class="member-info">
                <h1 style="margin-bottom: 0px; width: 625px;">{{member.prefix}}<br/>{{member.fullName}} {{ member.suffix}}</h1>
                <div class="member-even-columns">
                    <p>Short Name: {{member.shortName}}</p>
                    <p>Email: {{member.email}}</p>
                    <p>District Code: {{member.districtCode}}</p>
                </div>
                <div class="member-even-columns">
                    <p>Incumbent: {{member.incumbent}}</p>
                    <p>Chamber: {{member.chamber}}</p>
                    <p>Session Year: {{member.sessionYear}}</p>
                </div>
            </div>
            <img class="verify-card-margin verify-image" style="margin-top: 50px" width="200" height="300" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{member.imgName}}">

            <h2 style="margin-left: 110px; clear:both;">Active Sessions</h2>
            <div class="member-session-map-container">
                <div style="overflow:auto;" >
                    <div class="member-uneven-columns-1 bold">Session Year</div>
                    <div class="member-uneven-columns-2 bold">
                        <div class="member-uneven-columns-2 bold">
                            Short Name
                        </div>
                        <div class="member-uneven-columns-1 bold text-align-center">
                            District Code
                        </div>
                    </div>
                </div>
                <div class="member-session-map" ng-repeat="(key,entry) in member.sessionShortNameMap">
                    <div style="overflow:auto;">
                        <div class="member-uneven-columns-1">{{key}}</div>
                        <div class="member-uneven-columns-2">
                            <div class="member-uneven-columns-2" ng-repeat-start="item in entry">
                                {{item.shortName}}
                            </div>
                            <div class="member-uneven-columns-1 text-align-center" ng-repeat-end>
                                {{item.districtCode}}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div style="float:right;">
                <md-button class="md-raised verify-button" href="${ctxPath}/admin/members">Back</md-button>
            </div>
        </md-card-content>

        </md-card-content>
    </md-card>
</div>