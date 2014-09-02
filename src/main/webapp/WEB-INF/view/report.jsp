<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Open Legislation 2.0</title>
    <link rel="stylesheet" href="static/css/app.css" />
    <script src="static/bower_components/modernizr/modernizr.js"></script>
</head>
<body>
    <open:top-nav activeLink="report"/>

    <div class="row" style="margin-top:1.5em">
        <div class="large-2 columns side-menu-bg">
            <nav>
                <ul class="side-nav">
                    <li class='heading'>Report Types</li>
                    <li><a href="#">LBDC Daybreak</a></li>
                    <li><a href="#">Agenda/Calendar Check</a></li>
                    <li><a href="#">Memo Dump</a></li>
                </ul>
            </nav>
        </div>
        <div class="large-10 columns" style="padding-left:35px;" ng-app="" ng-controller="daybreakReportController">
            <div class="row">
                <div class="large-4 columns">
                    <h4>Daybreak Reports</h4>
                </div>
                <div class="large-offset-4 large-4 columns">
                    <div class="row">
                        <form>
                            <select class="columns large-3">
                                <option selected>Mar</option>
                            </select>
                            <select class="columns large-3"></select>
                            <select class="columns large-3">
                                <option selected>Dec</option>
                            </select>
                            <select class="columns large-3"></select>
                        </form>
                    </div>
                </div>
            </div>
            <div class="row">
                <!-- Chart Thing -->
                <section style="height:300px;width:100%;background:#ddd;">
                    <div id="report-chart-area" class="reportChart" style="display:block"></div>
                </section>
                <form class="reportChartForm">
                    <header class="reportChartForm">Chart View:&nbsp;&nbsp;</header>
                    <label for="openClosed" class="reportChartForm">Open/Closed/Existing </label>
                    <input type="radio" name="Chart" id="openClosed" value="openClosed" class="reportChartForm"
                           ng-click="drawOpenClosedChart()" checked="checked"/>
                    <label class="reportChartForm">&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <label for="errorType" class="reportChartForm">Error Type:  </label>
                    <input type="radio" name="Chart" id="errorType" value="errorType" class="reportChartForm"
                           ng-click="drawErrorTypeChart()"/>
                </form>
            </div>
            <br/>
            <div class="row">
                <table class="columns large-12" style="padding:0;">
                    <thead>
                        <tr>
                            <th>Report Date/Time</th>
                            <th>Total</th>
                            <th>Existing</th>
                            <th>New</th>
                            <th>Resolved</th>
                            <th>Sponsor</th>
                            <th>Co/Mulit-sponsor</th>
                            <th>Title</th>
                            <th>Law/ Summary</th>
                            <th>Action</th>
                            <th>Page</th>
                            <th>Versions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="report in reports">
                            <td>{{ report.reportDate | date:'short'}}</td>
                            <td>{{ report.totalErrors }}
                                <span ng-class="getEntryDiffClass(report.totalErrors, reports[$index+1].totalErrors)">
                                    ({{ getEntryDiff(report.totalErrors, reports[$index+1].totalErrors) }})
                                </span>
                            </td>
                            <td>{{ report.existingErrors }}</td>
                            <td>{{ report.newErrors }}</td>
                            <td>{{ report.resolvedErrors }}</td>
                            <td>{{ report.sponsorErrors }}
                                <span ng-class="getEntryDiffClass(report.sponsorErrors, reports[$index+1].sponsorErrors)">
                                    ({{ getEntryDiff(report.sponsorErrors, reports[$index+1].sponsorErrors) }})
                                </span>
                            </td>
                            <td>{{ report.coSponsorErrors }}
                                <span ng-class="getEntryDiffClass(report.coSponsorErrors, reports[$index+1].coSponsorErrors)">
                                    ({{ getEntryDiff(report.coSponsorErrors, reports[$index+1].coSponsorErrors) }})
                                </span>
                            </td>
                            <td>{{ report.titleErrors }}
                                <span ng-class="getEntryDiffClass(report.titleErrors, reports[$index+1].titleErrors)">
                                    ({{ getEntryDiff(report.titleErrors, reports[$index+1].titleErrors) }})
                                </span>
                            </td>
                            <td>{{ report.lawSummaryErrors }}
                                <span ng-class="getEntryDiffClass(report.lawSummaryErrors, reports[$index+1].lawSummaryErrors)">
                                    ({{ getEntryDiff(report.lawSummaryErrors, reports[$index+1].lawSummaryErrors) }})
                                </span>
                            </td>
                            <td>{{ report.actionErrors }}
                                <span ng-class="getEntryDiffClass(report.actionErrors, reports[$index+1].actionErrors)">
                                    ({{ getEntryDiff(report.actionErrors, reports[$index+1].actionErrors) }})
                                </span>
                            </td>
                            <td>{{ report.pageErrors }}
                                <span ng-class="getEntryDiffClass(report.pageErrors, reports[$index+1].pageErrors)">
                                    ({{ getEntryDiff(report.pageErrors, reports[$index+1].pageErrors) }})
                                </span>
                            </td>
                            <td>{{ report.versionErrors }}
                                <span ng-class="getEntryDiffClass(report.versionErrors, reports[$index+1].versionErrors)">
                                    ({{ getEntryDiff(report.versionErrors, reports[$index+1].versionErrors) }})
                                </span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        function daybreakReportController($scope, $filter){

            $scope.updateReports = function(startDate, endDate){
                $scope.reports = getReports(startDate, endDate);
                $scope.errorCounts = getErrorCounts($scope.reports, $filter);
            };

            $scope.getEntryDiff = function(currentCount, previousCount){
                return getEntryDiff(currentCount, previousCount);
            };

            $scope.getEntryDiffClass = function(currentCount, previousCount){
                return getEntryDiffClass(currentCount, previousCount);
            };

            $scope.drawOpenClosedChart = function() { drawOpenClosedChart($scope.errorCounts); };
            $scope.drawErrorTypeChart = function() { drawErrorTypeChart($scope.errorCounts); };

            $scope.updateReports(new Date(-8640000000000000), new Date());

            $scope.drawOpenClosedChart();
        }

        function getReports(startDate, endDate){
            var stockReports = [
                {
                    reportDate: new Date(2014, 7, 23, 12, 0, 0),
                    totalErrors: 200,
                    newErrors: 108,
                    existingErrors: 100,
                    resolvedErrors: 8,
                    sponsorErrors: 1,
                    coSponsorErrors: 19,
                    titleErrors: 0,
                    lawSummaryErrors: 11,
                    actionErrors: 87,
                    pageErrors: 10,
                    versionErrors: 80
                },
                {
                    reportDate: new Date(2014, 7, 16, 12, 0, 0, 0),
                    totalErrors: 100,
                    newErrors: 0,
                    existingErrors: 101,
                    resolvedErrors: 1,
                    sponsorErrors: 1,
                    coSponsorErrors: 7,
                    titleErrors: 0,
                    lawSummaryErrors: 3,
                    actionErrors: 1,
                    pageErrors: 10,
                    versionErrors: 79
                },
                {
                    reportDate: new Date(2014, 7, 11, 12, 0, 0, 0),
                    totalErrors: 101,
                    newErrors: 58,
                    existingErrors: 49,
                    resolvedErrors: 6,
                    sponsorErrors: 1,
                    coSponsorErrors: 11,
                    titleErrors: 0,
                    lawSummaryErrors: 3,
                    actionErrors: 1,
                    pageErrors: 10,
                    versionErrors: 81
                },
                {
                    reportDate: new Date(2014, 6, 28, 12, 0, 0, 0),
                    totalErrors: 49,
                    newErrors: 4,
                    existingErrors: 95,
                    resolvedErrors: 42,
                    sponsorErrors: 1,
                    coSponsorErrors: 38,
                    titleErrors: 1,
                    lawSummaryErrors: 9,
                    actionErrors: 6,
                    pageErrors: 12,
                    versionErrors: 24
                }
            ];

            stockReports.forEach( function(report){
                if(report.reportDate.getTime() < startDate.getTime() || report.reportDate.getTime() > endDate.getTime() ){
                    stockReports.pop(report);
                }
            });
            return stockReports;
        }

        function getErrorCounts(reportContainer, $filter){
            var errorCounts = {
                reportDates: [],
                totalErrorCounts: [],
                newErrorCounts: [],
                existingErrorCounts: [],
                resolvedErrorCounts: [],
                sponsorErrorCounts: [],
                coSponsorErrorCounts: [],
                titleErrorCounts: [],
                lawSummaryErrorCounts: [],
                actionErrorCounts: [],
                pageErrorCounts: [],
                versionErrorCounts: []
            };

            reportContainer.reverse().forEach( function(report) {
                errorCounts.reportDates.push($filter('date')(report.reportDate, 'short'));
                errorCounts.totalErrorCounts.push(report.totalErrors);
                errorCounts.newErrorCounts.push(report.newErrors);
                errorCounts.existingErrorCounts.push(report.existingErrors);
                errorCounts.resolvedErrorCounts.push(report.resolvedErrors);
                errorCounts.sponsorErrorCounts.push(report.sponsorErrors);
                errorCounts.coSponsorErrorCounts.push(report.coSponsorErrors);
                errorCounts.titleErrorCounts.push(report.titleErrors);
                errorCounts.lawSummaryErrorCounts.push(report.lawSummaryErrors);
                errorCounts.actionErrorCounts.push(report.actionErrors);
                errorCounts.pageErrorCounts.push(report.pageErrors);
                errorCounts.versionErrorCounts.push(report.versionErrors);
            });
            reportContainer.reverse();

            return errorCounts;
        }

        function drawOpenClosedChart(errorCounts){
            $('#report-chart-area').highcharts({
                chart: {
                    type: 'area',
                    height: 300
                },
                title: {
                    text: ''
                },
                xAxis: {
                    categories: errorCounts.reportDates,
                    title: {
                        text: 'Report Date'
                    }
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Error Count'
                    },
                    stackLabels: {
                        enabled: true,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                    },
                    gridLineColor: '#ddd',
                    gridLineDashStyle: 'longdash'
                },
                legend: {
                    borderWidth: 0
                },
                tooltip: {
                    formatter: function() {
                        return '<b>'+ this.x +'</b><br/>'+
                                this.series.name +': '+ this.y +'<br/>'+
                                'Total: '+ this.point.stackTotal;
                    }
                },
                plotOptions: {
                    area: { stacking: 'normal'},
                    column: {
                        stacking: 'normal',
                        dataLabels: {
                            enabled: false
                        }
                    }
                },
                colors: ['#6BFFF5', '#FF6B75', '#FFB44A'],
                series: [{
                    name: 'Closed',
                    data: errorCounts.resolvedErrorCounts
                }, {
                    name: 'Opened',
                    data: errorCounts.newErrorCounts
                }, {
                    name: 'Existing',
                    data: errorCounts.existingErrorCounts
                }]
            });
        }

        function drawErrorTypeChart(errorCounts){
            $('#report-chart-area').highcharts({
                chart: {
                    type: 'area',
                    height: 300
                },
                title: {
                    text: ''
                },
                xAxis: {
                    categories: errorCounts.reportDates,
                    title: {
                        text: 'Report Date'
                    }
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Error Count'
                    },
                    stackLabels: {
                        enabled: true,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                    },
                    gridLineColor: '#ddd',
                    gridLineDashStyle: 'longdash'
                },
                legend: {
                    borderWidth: 0
                },
                tooltip: {
                    formatter: function() {
                        return '<b>'+ this.x +'</b><br/>'+
                                this.series.name +': '+ this.y +'<br/>'+
                                'Total: '+ this.point.stackTotal;
                    }
                },
                plotOptions: {
                    area: { stacking: 'normal'},
                    column: {
                        stacking: 'normal',
                        dataLabels: {
                            enabled: false
                        }
                    }
                },
                series: [{
                    name: 'Sponsor',
                    data: errorCounts.sponsorErrorCounts
                }, {
                    name: 'Co/Multi Sponsor',
                    data: errorCounts.coSponsorErrorCounts
                }, {
                    name: 'Title',
                    data: errorCounts.titleErrorCounts
                }, {
                    name: 'Law / Summary',
                    data: errorCounts.lawSummaryErrorCounts
                }, {
                    name: 'Action',
                    data: errorCounts.actionErrorCounts
                }, {
                    name: 'Page',
                    data: errorCounts.pageErrorCounts
                }, {
                    name: 'Versions',
                    data: errorCounts.versionErrorCounts
                }]
            });
        }

        function getEntryDiff(currentCount, previousCount) {
            var difference = currentCount - previousCount;
            var numberSign = "";
            if(difference==0 || isNaN(difference)) {
                return "";
            }
            else if(difference>0){
                numberSign = "+";
            }
            return numberSign + difference;
        }

        function getEntryDiffClass(currentCount, previousCount) {
            var difference = currentCount - previousCount;
            var elementClass = "";
            if(difference==0 || isNaN(difference)) {
                return "reportEntryDiffHidden";
            }
            else if(difference>0){
                return "reportEntryDiffPositive";
            }
            return "reportEntryDiffNegative";
        }
    </script>
    <script src="static/bower_components/jquery/dist/jquery.min.js"></script>
    <script src="static/bower_components/foundation/js/foundation.min.js"></script>
    <script src="static/bower_components/highcharts/highcharts.js"></script>
    <script src="static/bower_components/angular/angular.js"></script>
    <script src="static/js/app.js"></script>
</body>
</html>
