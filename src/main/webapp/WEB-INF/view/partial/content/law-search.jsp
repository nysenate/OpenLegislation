<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="LawCtrl">
    <section ng-controller="LawListingCtrl">
        <h3 style="color:white;" class="green2-bg no-bottom-margin padding-20"><i class="icon-book prefix-icon2"></i>
            <span class="bold">NYS Law Listing</span>
        </h3>

        <div class="text-medium margin-top-20">
            <div class="columns large-6">
                <h4>Consolidated Laws</h4>
                <hr class="margin-top-10 margin-bottom-10"/>
                <div ng-repeat="law in lawListings['CONSOLIDATED']">
                    <a ng-href="${ctxPath}/laws/{{law.lawId}}">
                        <label style="width:50px;" class="label secondary">{{law.lawId}}</label> {{law.name}}</a>
                </div>
            </div>

            <div class="columns large-6">
                <h4>Unconsolidated Laws</h4>
                <hr class="margin-top-10 margin-bottom-10"/>
                <div ng-repeat="law in lawListings['UNCONSOLIDATED']">
                    <a ng-href="${ctxPath}/laws/{{law.lawId}}">
                        <label style="width:50px;" class="label secondary">{{law.lawId}}</label> {{law.name}}</a>
                </div>

                <h4 class="margin-top-20">Court Acts</h4>
                <hr class="margin-top-10 margin-bottom-10"/>
                <div ng-repeat="law in lawListings['COURT_ACTS']">
                    <a ng-href="${ctxPath}/laws/{{law.lawId}}">
                        <label style="width:50px;" class="label secondary">{{law.lawId}}</label> {{law.name}}</a>
                </div>

                <h4 class="margin-top-20">Rules</h4>
                <hr class="margin-top-10 margin-bottom-10"/>
                <div ng-repeat="law in lawListings['RULES']">
                    <a ng-href="${ctxPath}/laws/{{law.lawId}}">
                        <label style="width:50px;" class="label secondary">{{law.lawId}}</label>{{law.name}}</a>
                </div>
            </div>
        </div>

    </section>
</section>