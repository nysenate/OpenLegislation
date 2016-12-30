angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$location', '$routeParams', 'PaginationModel', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, $location, $routeParams, paginationModel, spotcheckMismatchApi, mismatchSummaryApi) {

    const dateFormat = 'YYYY-MM-DD';
    /** Used to look up content types corresponding to tab indexes. */
    const contentTypes = ['BILL', 'CALENDAR', 'AGENDA'];

    $scope.datasource = {
        values: [
            {
                value: 'OPENLEG',
                label: 'LBDC - OpenLegislation'
            },
            {
                value: 'NYSENATE_DOT_GOV',
                label: 'OpenLegislation - NYSenate.gov'
            }
        ],
        selected: {}
    };
    $scope.status = 'OPEN'; // Show all open issues by default.
    $scope.selectedTab = 0; // Select Bills tab by default.
    $scope.date = {};
    $scope.mismatchSummary = {};
    $scope.mismatches = [];
    $scope.loading = false;
    $scope.pagination = angular.extend({}, paginationModel);

    $scope.onPageChange = function (pageNum) {
        $scope.updateMismatches();
    };

    $scope.onTabChange = function () {
        resetPagination();
        $scope.updateMismatches();
    };

    $scope.updateMismatches = function () {
        $scope.loading = true;
        $scope.mismatches = [];
        spotcheckMismatchApi.getMismatches($scope.datasource.selected.value, contentTypes[$scope.selectedTab],
            toMismatchStatus($scope.status), $scope.pagination.getLimit(), $scope.pagination.getOffset())
            .then(function (result) {
                $scope.pagination.setTotalItems(result.pagination.total);
                $scope.mismatches = result.mismatches;
                $scope.loading = false;
            });

        /**
         * Returns array of mismatch statuses corresponding to the selected status.
         */
        function toMismatchStatus(status) {
            if (status === 'OPEN') {
                return ['NEW', 'EXISTING'];
            }
            return [status];
        }
    };

    $scope.formatDate = function (date) {
        return date.format(dateFormat);
    };

    function resetPagination() {
        $scope.pagination.reset();
        $scope.pagination.setTotalItems(0);
    }

    function onDateChange() {
        $location.search('date', $scope.date.format(dateFormat)).replace();
    }

    $scope.init = function () {
        $scope.pagination.itemsPerPage = 10;
        // Init Date
        if ($routeParams.hasOwnProperty('date')) {
            $scope.date = moment($routeParams.date, dateFormat);
        }
        else {
            $scope.date = moment().startOf('day');
            onDateChange();
        }

        // Init Datasource
        $scope.datasource.selected = $scope.datasource.values[0];

        // Init Summary
        mismatchSummaryApi.get($scope.datasource.selected.value)
            .then(function (mismatchSummary) {
                $scope.mismatchSummary = mismatchSummary;
            });

        // Init Mismatches
        $scope.updateMismatches();
    };

    $scope.init();
}
