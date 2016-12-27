angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$location', '$routeParams', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, $location, $routeParams, spotcheckMismatchApi, mismatchSummaryApi) {

    const DATE_FORMAT = 'YYYY-MM-DD';
    $scope.date = {};
    $scope.datasource = 'OPENLEG';
    $scope.contentType = {}; // TODO init
    $scope.status = 'OPEN';
    $scope.mismatchSummary = {};
    $scope.billMismatches = {
        matches: [], // A master copy of all mismatches.
        filtered: [] // Mismatches which match the user specified filters.
    };

    $scope.onDatasourceChange = function () {
        // TODO: re query all content types?
        spotcheckMismatchApi.getBills($scope.datasource)
            .then(function (billMismatches) {
                $scope.billMismatches.matches = billMismatches;
                $scope.onStatusChange();
            });
        spotcheckMismatchApi.getCalendars($scope.datasource)
            .then(function (calMismatches) {

            })
    };

    $scope.onStatusChange = function () {
        // TODO: Filter all mismatch content types?
        $scope.billMismatches.filtered = mismatchesWithStatus($scope.billMismatches.matches, $scope.status);
    };

    $scope.onDateChange = function () {
        $location.search('date', $scope.date.format(DATE_FORMAT)).replace();
        // TODO: reload if necessary.
    };

    function mismatchesWithStatus(mismatches, status) {
        var filterByStatus = function (mismatch) {
            if (status === 'OPEN') {
                return mismatch.status === 'NEW' || mismatch.status === 'EXISTING';
            }
            return mismatch.status === status;
        };
        return mismatches.filter(filterByStatus)
    }

    $scope.toDate = function (date) {
        return date.format(DATE_FORMAT);
    };

    ($scope.init = function () {
        if ($routeParams.hasOwnProperty('date')) {
            $scope.date = moment($routeParams.date, DATE_FORMAT);
        }
        else {
            $scope.date = moment().startOf('day');
            $scope.onDateChange();
        }

        mismatchSummaryApi.get($scope.datasource)
            .then(function (mismatchSummary) {
                $scope.mismatchSummary = mismatchSummary;
            });

        $scope.onDatasourceChange();
    }).call();
}
