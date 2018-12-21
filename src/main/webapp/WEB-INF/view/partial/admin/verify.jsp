<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="VerifyCtrl" ng-init="init()">
    <!--
    <div class="member-header-section">
        <h1 class="member-header">{{headerText}}</h1>
    </div>
    -->

    <!-- STEP ONE -->
    <md-card class="verify-content-section" ng-show="member" ng-show="step == 1" ng-hide="step != 1">
        <p class="verify-card-margin">Verification for <span class="bold">{{member.firstName}} {{member.lastName}} <span class="grey">{{member.chamber}} {{member.sessionYear}}</span></span></p>
        <md-card-content>
            <div class="verify-table-row verify-create-new" ng-click="toStepTwo(null)">
                <span class="icon-squared-plus verify-icon"></span>
                <p>Create New Member</p>
            </div>
            <p class="member-text-center">-or link to existing member-</p>

            <input class="verify-search" ng-model="searchInput" placeholder="Search"/>
            <p class="member-text-center" ng-show="pagination.totalItems == 0">No results found.</p>
            <div dir-paginate="member in membersList | itemsPerPage: pagination.itemsPerPage"
                 total-items="pagination.totalItems" current-page="pagination.currPage"
                 pagination-id="paginationId" ng-click="toStepTwo(member)"
                 class="verify-table-row">
                <img class="verify-image-small" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{member.imgName}}">
                <p class="member-table-item">{{member.firstName}} {{member.lastName}}</p>
                <p class="member-table-item grey">{{member.chamber}} {{member.sessionYear}} in District {{member.districtCode}}</p>
            </div>
            <dir-pagination-controls class="member-text-center verify-search" pagination-id="paginationId" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber, type)" max-size="12">
            </dir-pagination-controls>
            <div style="float:right;">
                <md-button class="verify-button md-raised" href="${ctxPath}/admin/members">Back</md-button>
            </div>
        </md-card-content>
    </md-card>

    <!-- STEP TWO -->
    <md-card class="verify-content-section" ng-show="step == 2">
        <p class="verify-card-margin">Verification for <span class="bold">{{member.firstName}} {{member.lastName}} <span class="grey">{{member.chamber}} {{member.sessionYear}}</span></span>{{verifyInformation}}</p>
        <md-card-content style="padding-right: 65px;">
            <form name="verificationForm">
                <img class="verify-card-margin verify-image" ng-show="linking" width="134" height="200" ng-src="${ctxPath}/static/img/business_assets/members/mini/{{inputMember.imgName}}">
                <div class="verify-basic-info">
                    <div layout-gt-sm="row">
                        <h2 class="verify-card-margin" ng-show="!linking">Name: </h2>
                        <md-input-container class="md-block" flex-gt-sm="">
                            <label>First Name</label>
                            <input ng-model="inputMember.firstName" name="firstName" required md-no-asterisk>

                            <span ng-show="verificationForm.firstName.$error.required" class="verify-required red">This field is required</span>
                        </md-input-container>

                        <md-input-container class="md-block" flex-gt-sm="">
                            <label>Middle Name</label>
                            <input ng-model="inputMember.middleName" name="middleName">
                        </md-input-container>

                        <md-input-container class="md-block" flex-gt-sm="">
                            <label>Last Name</label>
                            <input ng-model="inputMember.lastName" name="lastName" required md-no-asterisk>

                            <span ng-show="verificationForm.lastName.$error.required" class="verify-required red">This field is required</span>
                        </md-input-container>

                        <md-input-container class="md-block" flex-gt-sm="">
                            <label>Suffix</label>
                            <input ng-model="inputMember.suffix">
                        </md-input-container>
                    </div>

                    <div layout-gt-sm="row">
                        <h2 class="verify-card-margin" ng-show="!linking">Email: </h2>
                        <md-input-container class="md-block" flex-gt-sm="">
                            <label>Email</label>
                            <input ng-model="inputMember.email" type="email" name="email">

                            <span ng-show="verificationForm.email.$error.email" class="verify-required red">Not valid email</span>
                        </md-input-container>
                    </div>
                </div>

                <div layout-gt-sm="row">
                    <h2 class="verify-card-margin">Incumbent: </h2>
                    <md-input-container class="md-block" flex-gt-sm="">
                        <md-checkbox ng-model="inputMember.incumbent" aria-label="Incumbent Checkbox" ng-false-value="false"></md-checkbox>
                    </md-input-container>
                </div>

                <div layout-gt-sm="row">
                    <h2 class="verify-card-margin">District Code: </h2>
                    <md-input-container class="md-block" flex-gt-sm="">
                        <label>District Code</label>
                        <input ng-model="inputMember.districtCode" name="districtCode" ng-pattern="/^[0-9]{1,3}$/" required md-no-asterisk>

                        <span ng-show="verificationForm.districtCode.$error.pattern" class="verify-required red">Not a valid district code</span>
                        <span ng-show="verificationForm.districtCode.$error.required" class="verify-required red">This field is required</span>
                    </md-input-container>
                </div>

                <div style="float:right;">
                    <md-button class="md-raised verify-button" ng-click="back()">Back</md-button>
                    <md-button class="md-raised verify-button" ng-class="{'verify-disabled' : !verificationForm.$valid}" ng-click="submit()" ng-disabled="!verificationForm.$valid">Submit</md-button>
                </div>
            </form>
        </md-card-content>
    </md-card>

    <!-- STEP THREE -->
    <div class="member-header-section" ng-show="step == 3">
        <p>
            {{confirmationText}}<br/>
            <md-button class="md-raised verify-button" style="margin: 0" href="${ctxPath}/admin/members">Done</md-button>
        </p>
    </div>

</div>