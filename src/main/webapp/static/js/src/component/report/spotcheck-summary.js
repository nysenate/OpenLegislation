
/** --- Report Summary Controller --- */

angular.module('open.spotcheck')
    .controller('SpotcheckSummaryCtrl', ['$scope', '$filter', '$routeParams', '$location', 'SpotcheckSummaryAPI',
function ($scope, $filter, $routeParams, $location, SpotcheckSummaryAPI) {
    $scope.reportSummaries = [];
    $scope.filteredReportSummaries = [];
    $scope.dataProvider = [];
    $scope.response = null;
    $scope.loadingSummaries = false;
    $scope.summariesNotFound = false;
    $scope.showErrorlessReports = false;
    $scope.resultsPerPage = 20;

    $scope.params = {
        summaryType: "LBDC_DAYBREAK",
        inputStartDate: null,
        inputEndDate: null
    };

    $scope.pagination = {
        currPage: 1,
        itemsPerPage: 10
    };

    $scope.init = function() {
        if ('type' in $routeParams) {
            $scope.params.summaryType = $filter('reportType')($routeParams['type']) || "all";
        }
        if ($routeParams.hasOwnProperty('endDate') && moment($routeParams['endDate']).isValid()) {
            $scope.endDate = moment($routeParams['endDate']);
        } else {
            $scope.endDate = moment();
        }
        if ($routeParams.hasOwnProperty('startDate') && moment($routeParams['startDate']).isValid()) {
            $scope.startDate = moment($routeParams['startDate']);
        } else {
            $scope.startDate = moment($scope.endDate).subtract(3, 'months');
        }
        $scope.params.inputStartDate = moment($scope.startDate).toDate();
        $scope.params.inputEndDate = moment($scope.endDate).toDate();
    };

    $scope.getSummaries = function() {
        console.log("getting new summaries");
        var summaryType = $scope.params.summaryType !== "all" ? $filter('reportTypeRefName')($scope.params.summaryType) : [];
        $scope.loadingSummaries = true;
        $scope.response = SpotcheckSummaryAPI.get({startDate: $scope.startDate.format(),
                endDate: $scope.endDate.endOf('day').format(),
                reportType: summaryType},
            function() {
                if ($scope.response.success) {
                    $scope.reportSummaries = $scope.response.reports.items;
                    console.log("summaries received");
                    $scope.setSummarySearchParams();
                    $scope.loadingSummaries = false;
                    $scope.summariesNotFound = false;
                    $scope.filterSummaries();
                }
            }, function (response) {
                console.log(response);
                $scope.loadingSummaries = false;
                $scope.summariesNotFound = true;
            });
    };

    // Watch the params for changes and make a request for report summaries if there were changes
    $scope.$watchCollection('params', function() {
        $scope.endDate = moment($scope.params.inputEndDate);
        $scope.startDate = moment($scope.params.inputStartDate);
        $scope.getSummaries();
    });

    $scope.setSummarySearchParams = function () {
        $scope.clearSearchParams();
        $scope.setSearchParam('type', $filter('reportTypeRefName')($scope.params.summaryType),
            $scope.params.summaryType !== "all");
        //$scope.setSearchParam('startDate', $scope.startDate.format('YYYY-MM-DD'));
        //$scope.setSearchParam('endDate', $scope.endDate.format('YYYY-MM-DD'));
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
            return "postfix-icon icon-arrow-up4 new-error";
        }
        else if (val < 0) {
            return "postfix-icon icon-arrow-down5 closed-error";
        }
        return "postfix-icon icon-minus3 existing-error";
    };

    $scope.noErrorFilter = function(row) {
        return $scope.showErrorlessReports || row.openMismatches > 0;
    };

    $scope.filterSummaries = function() {
        $scope.filteredReportSummaries = $scope.reportSummaries.filter($scope.noErrorFilter);
    };

    $scope.$watch('showErrorlessReports', $scope.filterSummaries);

    $scope.init();

}]);
