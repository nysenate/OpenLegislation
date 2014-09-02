var reportModule = angular.module('report');

reportModule.controller('DaybreakReportsCtrl', ['$scope', '$filter', '$http', function($scope, $filter, $http) {
    $scope.title = 'LBDC Daybreak Reports';

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

}]);

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