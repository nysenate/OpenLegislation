/** --- Report Detail Controller --- */

angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl', ['$scope', '$element', '$filter', '$location', '$timeout',
            '$routeParams', '$rootScope', 'SpotcheckDetailAPI', 'SpotcheckDefaultFilter',
function ($scope, $element, $filter, $location, $timeout, $routeParams, $rootScope, SpotcheckDetailAPI, defaultFilter) {

    $scope.state = {
        loadingReport: false,
        mismatches: [],
        filteredMismatches: [],
        filter: angular.copy(defaultFilter),
        summary: null,
        report: null,
        dataDetails: null
    };

    // Initialization function
    $scope.init = function () {
        if ($routeParams.hasOwnProperty('runTime') && $routeParams.hasOwnProperty('type')) {
            $scope.state.reportType = $routeParams['type'];
            $scope.state.reportDateTime = $routeParams['runTime'];
            $scope.getReportDetails();
        }
    };

    // Fetch the report via the report detail API
    $scope.getReportDetails = function() {
        SpotcheckDetailAPI.get({reportType: $scope.state.reportType, reportDateTime: $scope.state.reportDateTime},
            function(response) {
                $scope.state.report = response.details;
                $scope.state.referenceDateTime = $scope.state.report.referenceDateTime;
                $scope.state.reportType = $scope.state.report.referenceType;
                $scope.extractTableData();
                $scope.state.summary = {
                    mismatchStatuses: $scope.state.report.mismatchStatuses,
                    mismatchCounts: $scope.state.report.mismatchCounts
                };
                $timeout(filterMismatches);
                $scope.state.loadingReport = false;
                console.log("report detail received:", $scope.state.report.referenceType, $scope.state.report.referenceDateTime);
        });
        $scope.state.loadingReport = true;
        console.log("new report detail requested: ", $scope.state.reportType, $scope.state.reportDateTime);
    };

    // Extracts an array of table rows from the report data
    $scope.extractTableData = function() {
        $scope.state.mismatches = $scope.extractMismatchRows($scope.state.report.observations, $scope.state.reportType);
        angular.forEach($scope.tableData, function(row) {
            $scope.state.dataDetails[row.mismatchId] = row;
        });
    };

    $scope.getMismatchDetails = function(mismatchId) {
        return $scope.state.dataDetails[mismatchId];
    };

    $scope.openDetailWindow = function(mismatchRow) {
        $scope.showMismatchDetails(mismatchRow, $scope.getMismatchDetails);
    };

    $rootScope.$on('mismatchFilterChange', filterMismatches);

    function filterMismatches() {
        var filtered = $filter('filter')($scope.state.mismatches, function (mismatch) {
            return $scope.state.filter.passes(mismatch)
        });
        var ordered = $filter('orderBy')(filtered, getOrderByField(), $scope.state.filter.sortOrder === 'DESC');
        $scope.state.filteredMismatches = $filter('limitTo')(ordered, $scope.state.filter.limit, $scope.state.filter.offset - 1);
        console.log('limited', $scope.state.filter.limit, $scope.state.filter.offset, $scope.state.filteredMismatches);
    }


    var orderByFields = {
        OBSERVED_DATE: "observation.observedDateTime",
        CONTENT_KEY: "keyString",
        REFERENCE_DATE: "observation.refDateTime",
        MISMATCH_TYPE: "mismatch.mismatchType",
        STATUS: "mismatch.status"
    };
    function getOrderByField() {
        return orderByFields.hasOwnProperty($scope.state.filter.orderBy)
            ? orderByFields[$scope.state.filter.orderBy]
            : "!?";
    }
}]);
