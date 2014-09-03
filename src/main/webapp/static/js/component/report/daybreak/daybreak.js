var reportModule = angular.module('report');

var dateOutputFormat = "M/DD/YYYY H:mm";

reportModule.controller('DaybreakReportsCtrl', ['$scope', '$filter', '$http', function($scope, $filter, $http) {
    $scope.title = 'LBDC Daybreak Reports';
    $scope.reports = [];
    $scope.errorCounts = getErrorCounts([]);
    $scope.reportChartStatus = "openClosed";
    $scope.validYears = getValidYears();
    $scope.months = getMonths();
    $scope.endDate = getDefaultEndDate();
    $scope.startDate = getDefaultStartDate();

    $scope.updateReports = function(){
        $scope.reports = getReports($scope.startDate.date, $scope.endDate.date);
        $scope.errorCounts = getErrorCounts($scope.reports);
        $scope.updateReportChart();
    };

    $scope.updateReportChart = function(){ updateReportChart($scope.reportChartStatus, $scope.errorCounts); };

    $scope.getEntryDiff = function(currentCount, previousCount){
        return getEntryDiff(currentCount, previousCount);
    };

    $scope.getEntryDiffClass = function(currentCount, previousCount){
        return getEntryDiffClass(currentCount, previousCount);
    };

    $scope.$watch('reportChartStatus', $scope.updateReportChart );

    $scope.$watch('startDate.month', function() { updateStartDate($scope.startDate); $scope.updateReports(); }, true);
    $scope.$watch('endDate', function() { updateEndDate($scope.endDate); $scope.updateReports(); }, true);

    $scope.updateReports();

}]);

function getReports(startDate, endDate){
    var dateFormat = "YYYY-MM-DD-HH";
    var stockReports = [
        {
            reportDate: moment("2014-8-23-12", dateFormat),
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
            reportDate: moment("2014-8-16-12", dateFormat),
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
            reportDate: moment("2014-8-11-12", dateFormat),
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
            reportDate: moment("2014-7-28-12", dateFormat),
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

    var validReports = [];

    stockReports.forEach( function(report){
        if( !report.reportDate.isBefore(startDate) && !report.reportDate.isAfter(endDate) ){
            validReports.push(report);
        }
    });
    return validReports;
}

function getErrorCounts(reportContainer){
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
        errorCounts.reportDates.push(report.reportDate.format(dateOutputFormat));
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

function updateReportChart(status, errorCounts){
    if(status === 'openClosed'){
        unHideReportChart();
        drawOpenClosedChart(errorCounts);
    }
    else if(status === 'errorType'){
        unHideReportChart();
        drawErrorTypeChart(errorCounts);
    }
    else if(status === 'hidden'){
        hideReportChart();
    }
    else{
        console.log("Invalid chart view option: " + status);
    }
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

function unHideReportChart(){
    $(".reportChart").css("height", "300px")
        .css("width", "100%")
        .css("visibility", "visible");
}

function hideReportChart(){
    $(".reportChart").css("height", "0px")
        .css("width", "0%")
        .css("visibility", "hidden");
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
    if(difference === 0 || isNaN(difference)) {
        return "reportEntryDiffHidden";
    }
    else if(difference>0){
        return "reportEntryDiffPositive";
    }
    return "reportEntryDiffNegative";
}

function getValidYears(){
    var years = [];
    var currentYear = new Date().getFullYear();
    for(var year = 2014; year <= currentYear; year++){
        years.push(year);
        console.log(year);
    }
    return years
}

function getMonths(){
    return [
        {value: "1", name: "Jan"},
        {value: "2", name: "Feb"},
        {value: "3", name: "Mar"},
        {value: "4", name: "Apr"},
        {value: "5", name: "May"},
        {value: "6", name: "Jun"},
        {value: "7", name: "Jul"},
        {value: "8", name: "Aug"},
        {value: "9", name: "Sep"},
        {value: "10", name: "Oct"},
        {value: "11", name: "Nov"},
        {value: "12", name: "Dec"}
    ]
}

function sortByIntValue(input){

}

function getDefaultEndDate(){
    var now = moment();
    return { date: now, month: (now.month()+1), year: now.year() };
}

function getDefaultStartDate(startDate){
    var offsetDate = moment(startDate).subtract('2', 'months');
    return { date: offsetDate, month: (offsetDate.month()+1), year: offsetDate.year() };
}

function updateStartDate(startDate){
    var dateString = startDate.year + "-" + startDate.month;
    startDate.date = moment(dateString, "YYYY-MM");
    console.log("updated start date object: " + startDate.date.format(dateOutputFormat));
}

function updateEndDate(endDate){
    var dateString = endDate.year + "-" + endDate.month;
    endDate.date = moment(dateString, "YYYY-MM").endOf('month');
    console.log("updated end date object: " + endDate.date.format(dateOutputFormat));
}