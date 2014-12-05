<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="OpenLegislation 2.0">
    <script src="${ctxPath}/static/js/src/component/content/content.js"></script>
    <script src="${ctxPath}/static/js/src/component/content/bill/bill-home.js"></script>
</open-layout:head>
<open-layout:body>
    <div ng-controller="BillHomeCtrl" class="row" style="margin-top:1.5em">
        <!--<h3 ng-show="view == VIEWS.SEARCH" style="text-align: center;color: #555;">Browse NYS Bills and Resolutions</h3>-->
        <form style="margin-top:20px;">
            <div style="position:relative;">
                <div style="height:42px;width:50px;line-height:50px;text-align:center;background:#2B6A90;position:absolute;top:0;left:0;">
                    <i style="font-size:1.5em;color:white;" class="icon-search"></i>
                </div>
                <label>
                    <input ng-model="searchTerm" ng-model-options="{debounce: 500}" placeholder="Search for NYS Bills and Resolutions"
                           type="text" style="width:100%;font-size:1.3em;margin-bottom:0;text-indent: 60px;height:42px;"/>
                </label>
            </div>
            <div ng-show="view == VIEWS.SEARCH">
                <div class="columns large-9" style="background:#175B81;color:white;height:35px;line-height:35px;">
                    <span ng-show="totalResults > 0" style="font-size:.9em;font-weight: 600;">Showing Results 1-20 out of {{totalResults}}</span>
                </div>
                <div class="columns large-3 hide-for-medium-down" style="background:#004D71;color:white;height:35px;line-height:35px;text-align: center;">
                    <span style="font-size:.9em;font-weight: 600;"><i class="icon-arrow-down5 prefix-icon2"></i>Advanced Search</span>
                </div>
            </div>
        </form>

        <section style="" ng-show="view == VIEWS.SEARCH && totalResults > 0">
            <!-- Top Pagination -->
            <div class="" style="margin-top:50px;margin-bottom:10px;background:white;padding:5px;">
                <div class="row">
                    <div class="columns large-6">
                        <pagination page="1" total-items="totalResults" max-size="5" boundary-links="true"></pagination>
                    </div>
                    <div class="columns large-6 hide-for-medium-down" style="font-size:0.9em;text-align: right;">
                        <span style="margin-right:20px;">Sort Results By</span>
                        <a style="margin-right:20px;">Relevance</a>
                        <a style="margin-right:20px;">Most Activity</a>
                        <a style="margin-right:20px;">Recent Updates</a>
                    </div>
                </div>
            </div>

            <div class="columns large-12 bill-result" style="padding:10px 5px;border-bottom:1px solid #eee;" ng-repeat="r in billResults.result.items" >
                <div ng-init="bill = r.result" ng-click="getBill('S1234', 2013)">
                    <div class="columns small-4 large-3">
                        <span style="color:#555;font-size:1.8em;font-weight: 600;display: block;">{{bill.printNo}} - {{bill.session}}</span>
                        <span style="color:#006b80;font-weight:600;">{{bill.sponsor.member.fullName}}</span>
                    </div>
                    <div class="columns small-8 large-6 vertical-align">
                        <div>
                    <span style="font-size:.9em;">
                        {{bill.title}}
                    </span>
                        </div>
                    </div>
                    <div class="columns large-3 hide-for-medium-down" style="height: 70px; padding-right:25px;text-align: center;">
                        <div>
                            <ul ng-show="bill.billType.resolution == false" class="large-block-grid-8 bill-milestone-small">
                                <li ng-class="{'met': (bill.milestones.size > 0)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 1)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 2)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 3)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 4)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 5)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 6)}"></li>
                                <li ng-class="{'met': (bill.milestones.size > 7)}"></li>
                            </ul>
                            <ul ng-show="bill.billType.resolution == true" class="large-block-grid-2 bill-milestone-small">
                                <li class="met"></li>
                                <li ng-class="{'met': (bill.milestones.size > 0)}"></li>
                            </ul>
                            <span class="text-small" style="text-transform: capitalize;font-weight:600;color:#444;">{{getMilestoneDesc(bill.milestones)}}</span><br/>
                            <span class="text-small" style="color:#444;">{{getMilestoneDate(bill.milestones)}}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Lower Pagination -->
            <div class="columns large-12" style="margin-top:10px;background:white;padding:5px;" ng-show="totalResults > 10">
                <pagination page="1" total-items="totalResults" max-size="10" boundary-links="true"></pagination>
            </div>
        </section>

        <section style="line-height:150px;text-align: center;" ng-show="view == VIEWS.SEARCH && totalResults == 0">
            <span style="color:#777;font-size:2.5em;">
                Sorry, no matching bills were found.</span>
        </section>

        <section ng-show="view == VIEWS.BILL">
            <div class="row">
                <div class="columns large-4">
                    <span style="font-weight: 600;color: #006b80;position: relative;top: 10px;">Senate Bill</span>
                </div>
                <div class="columns large-8 hide-for-medium-down">
                    <div style="text-align: right;">
                        <a style="font-size:.9em;font-weight: 600;color: #666;position: relative;top: 10px;">
                            <i class="icon-printer prefix-icon"></i>Printer Friendly View</a>
                        <a style="margin-left:20px;font-size:.9em;font-weight: 600;color: #666;position: relative;top: 10px;">
                            <i class="icon-download prefix-icon"></i>Download PDF</a>
                    </div>
                </div>
            </div>
            <div class="row" style="padding-top:10px;">
                <div class="columns large-4">
                    <h1 style="font-size:3.5em;font-weight:600;color:#444;margin-bottom:0;">S1234 -2013</h1>
                </div>
                <div class="columns large-8 vertical-align" style="height:80px;">
                    <span class="medium">
                        Office of the taz payer advocateddj s fd Office of the taz payer advocateddj s fdw
                        Office of the taz payer advocateddj s fdw Office of the taz payer advocateddj s fdw
                        Office of the taz payer advocateddj s fdw Office of the taz payer advocateddj s fdw
                    </span>
                </div>
                <hr style="margin:0;"/>
            </div>
            <div class="row" style="line-height:40px;">
                <div class="columns large-3">
                    <span style="font-weight:600;">Viewing Version - </span><a>Initial</a> <a>A</a> <a>B</a>
                </div>
                <div class="columns large-3">
                    <span style="font-weight:600;">Published - </span><span>June 12, 2013</span>
                </div>
                <div class="columns large-3">
                    <span style="font-weight:600;">Same As - </span><a> A1234-2013</a>
                </div>
                <div class="columns large-3">
                    <span style="font-weight:600;">Previous Session - </span><a>S3453-2013</a>
                </div>
                <hr/>
            </div>
            <div class="row">
                <div class="columns large-12">
                    Lengthy summary goes here...
                </div>
            </div>
            <br/>
            <div class="row">
                <div class="columns large-4">
                    <img src="http://www.nysenate.gov/files/profile-pictures/young%20headshot-ret.jpg" style="float:left;height: 80px;"/>
                    <div id="sponsorName">
                        <span class="bold">Primary Sponsor</span><br/>
                        <span class="bold" style="font-size:1.3em">Sponsor's Full Name</span>
                    </div>
                </div>
                <div class="columns large-4">
                    <div class="margin-top-10">
                        <span class="bold">Cosponsors</span><br/>
                        <span class="bold" style="font-size:1.3em">None</span>
                    </div>
                </div>
                <div class="columns large-4">
                    <div class="margin-top-10">
                        <span class="bold">Multisponsors</span><br/>
                        <span class="bold" style="font-size:1.3em">None</span>
                    </div>
                </div>
            </div>
        </section>
    </div>
</open-layout:body>
<open-layout:footer/>