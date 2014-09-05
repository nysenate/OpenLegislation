var reportModule = angular.module('report');

var ISODateFormat = "YYYY-MM-DD";

/** --- REST resource for retrieving daybreak summaries within a date range --- */

reportModule.factory('DaybreakSummary', ['$resource', function($resource) {
    return $resource(apiPath + "/spotcheck/daybreaks/:startDate/:endDate", {
        startDate: '@startDate', endDate: '@endDate'
    });
}]);

/** --- Controller that handles report summary page --- */

reportModule.controller('DaybreakSummaryCtrl', ['$scope', '$filter', '$routeParams', '$location', 'DaybreakSummary',
                        function($scope, $filter, $routeParams, $location, DaybreakSummary) {
    $scope.title = 'LBDC Daybreak Reports';

    // Indicates whether or not to show the summary data or an error message
    $scope.showSummaries = true;

    $scope.showChart = false;

    // Initialize the date range
    (function() {
        $scope.startDate = ($routeParams.startDate && moment($routeParams.startDate).isValid())
            ? moment($routeParams.startDate).startOf('month')
            : moment().subtract(6, 'months').startOf('month');
        $scope.endDate = ($routeParams.endDate && moment($routeParams.endDate).isValid())
            ? moment($routeParams.endDate).endOf('month')
            : moment().endOf('month');
    })();

    // Date filter properties
    $scope.yearList = getValidYears();
    $scope.monthList = getMonths();

    // Used for binding to the select menus
    $scope.dateRange = {
        startMonth: $scope.monthList[$scope.startDate.get('month')],
        startYear: $scope.yearList[$scope.startDate.get('year')],
        endMonth: $scope.monthList[$scope.endDate.get('month')],
        endYear: $scope.yearList[$scope.endDate.get('year')]
    };

    $scope.reportChartStatus = "openClosed";

    $scope.$watch('dateRange', function() {
        // Adjust the start and end month/years accordingly such that they represent a valid range
        if ($scope.dateRange.endMonth.value < $scope.dateRange.startMonth.value) {
            $scope.dateRange.startMonth = $scope.dateRange.endMonth;
        }
        else if ($scope.dateRange.startMonth.value > $scope.dateRange.endMonth.value) {
            $scope.dateRange.endMonth = $scope.dateRange.startMonth;
        }
        if ($scope.dateRange.endYear.value < $scope.dateRange.startYear.value) {
            $scope.dateRange.startYear = $scope.dateRange.endYear;
        }
        else if ($scope.dateRange.startYear.value > $scope.dateRange.endYear.value) {
            $scope.dateRange.endYear = $scope.dateRange.startYear;
        }
        // Update the start and end moments as well
        $scope.startDate.set('month', $scope.dateRange.startMonth.value).set('year', $scope.dateRange.startYear.value);
        $scope.endDate.set('month', $scope.dateRange.endMonth.value).set('year', $scope.dateRange.endYear.value);
    }, true);

    // Obtains the summaries based on the start and end date filters
    $scope.updateSummaries = function() {
        $scope.summaries = DaybreakSummary.get({startDate: $scope.startDate.format(ISODateFormat),
            endDate: $scope.endDate.format(ISODateFormat)}, function() {
            if ($scope.summaries && $scope.summaries.success && $scope.summaries.reports.size > 0) {
                $scope.showSummaries = true;
                if ($scope.summaries.reports.size > 1) {
                    $scope.showChart = true;
                }
                else {
                    $scope.showChart = false;
                }
                drawMismatchStatusGraph($scope.getReportDateSeries(), $scope.getMismatchStatusSeries());
            }
            else {
                $scope.showSummaries = false;
                $scope.showChart = false;
            }

            // Update the url params with the current start and end dates
            $location.search('startDate', $scope.startDate.format(ISODateFormat));
            $location.search('endDate', $scope.endDate.format(ISODateFormat));
        });
    };

    // Compute the total number of mismatches for a given type.
    $scope.computeMismatchCount = function(summaryItem, type) {
        var defaultFilter = $filter('default');
        var mismatchType = summaryItem['mismatchTypes'][type];
        if (!mismatchType) return 0;
        return (defaultFilter(mismatchType['NEW'], 0) +
            defaultFilter(mismatchType['EXISTING'], 0) +
            defaultFilter(mismatchType['REGRESSION'], 0));
    };

    // Compute the difference between open issues and resolved issues. Set 'abs' to true to
    // return the absolute value of the result.
    $scope.computeMismatchDiff = function(summaryItem, type, abs) {
        var defaultFilter = $filter('default');
        var mismatchType = summaryItem['mismatchTypes'][type];
        if (!mismatchType) return 0;
        var diff = (defaultFilter(mismatchType['NEW'], 0) +
                defaultFilter(mismatchType['REGRESSION'], 0) -
                defaultFilter(mismatchType['RESOLVED'], 0));
        return (abs) ? Math.abs(diff) : diff;
    };

    // Return a css class based on whether the mismatch count is positive or negative
    $scope.mismatchDiffClass = function(summaryItem, type) {
        var val = $scope.computeMismatchDiff(summaryItem, type, false);
        if (val > 0) {
            return "postfix-icon icon-arrow-up2 new-error";
        }
        else if (val < 0) {
            return "postfix-icon icon-arrow-down2 closed-error";
        }
        return "postfix-icon icon-minus3 existing-error";
    };

    // Obtains an array containing mismatch status series to be consumed by the chart
    $scope.getMismatchStatusSeries = function() {
        if ($scope.summaries && $scope.summaries.reports.size > 0) {
            var existing = [], newRegr = [], resolved = [];
            angular.forEach($scope.summaries.reports.items, function(value, key) {
                existing.push(value.mismatchStatuses['EXISTING']);
                newRegr.push(value.mismatchStatuses['NEW'] + value.mismatchStatuses['REGRESSION']);
                resolved.push(value.mismatchStatuses['RESOLVED']);
            });
            return [{ name: 'New/Regression', data: newRegr.reverse()},
                    { name: 'Existing', data: existing.reverse()},
                    { name: 'Resolved', data: resolved.reverse()}];
        }
        return [];
    };

    // Obtains an array containing nicely formatted report dates to be used in the x-axis of the chart
    $scope.getReportDateSeries = function() {
        var reportDates = [];
        if ($scope.summaries && $scope.summaries.reports.size > 0) {
            angular.forEach($scope.summaries.reports.items, function(value, key) {
                reportDates.push(moment(value.reportDateTime).format('lll'));
            });
        }
        return reportDates.reverse();
    };

    // Update the series data in the chart
    $scope.updateReportChart = function() {
        if ($scope.reportChartStatus === 'openClosed'){
            unHideReportChart();
            drawMismatchStatusGraph();
        }
        else {
            console.log("Invalid chart view option: " + $scope.reportChartStatus);
        }
    };

    // Obtain the initial summaries
    $scope.updateSummaries();
}]);

/** --- Internal Methods --- */

function drawMismatchStatusGraph(reportDates, dataSeries) {
    $('#report-chart-area').highcharts({
        chart: {
            type: 'area',
            height: 300
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: reportDates,
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
            series: {
                animation: false
            },
            area: { stacking: 'normal'},
            column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: false
                }
            }
        },
        colors: ['#FF4E50','#FC913A', '#B3CC57'],
        series: dataSeries
    });
}

function getValidYears() {
    var years = {};
    var currentYear = moment().get('year');
    for (var year = 2014; year <= currentYear; year++) {
        years[year] = {value: year};
    }
    return years
}

function getMonths() {
    return [{value: 0, name: "Jan"}, {value: 1, name: "Feb"}, {value: 2, name: "Mar"}, {value: 3, name: "Apr"},
            {value: 4, name: "May"}, {value: 5, name: "Jun"}, {value: 6, name: "Jul"}, {value: 7, name: "Aug"},
            {value: 8, name: "Sep"}, {value: 9, name: "Oct"}, {value: 10, name: "Nov"}, {value: 11, name: "Dec"}];
}