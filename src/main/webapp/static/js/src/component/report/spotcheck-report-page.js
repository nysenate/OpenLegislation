angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$location', '$routeParams', 'PaginationModel', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, $location, $routeParams, paginationModel, spotcheckMismatchApi, mismatchSummaryApi) {

    const dateFormat = 'YYYY-MM-DD';
    /** Used to look up content types corresponding to the selected tab. */
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
    $scope.loading = false; // TODO remove this using promises?
    $scope.pagination = angular.extend({}, paginationModel);

    $scope.mismatchResponse = {
        mismatches: [],
        error: false,
        errorMessage: ''
    };

    $scope.summaryResponse = {
        summary: {},
        error: false,
        errorMessage: ''
    };

    $scope.onDatasourceChange = function () {
        resetPagination();
        $scope.updateMismatchSummary();
        $scope.updateMismatches();
    };

    $scope.onTabChange = function () {
        resetPagination();
        $scope.updateMismatches();
    };

    $scope.onPageChange = function (pageNum) {
        $scope.updateMismatches();
    };

    $scope.updateMismatchSummary = function () {
        $scope.summaryResponse.error = false;
        mismatchSummaryApi.get($scope.datasource.selected.value)
            .then(function (mismatchSummary) {
                $scope.summaryResponse.summary = mismatchSummary;
            })
            .catch(function (response) {
                $scope.summaryResponse.error = true;
                $scope.summaryResponse.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatches = function () {
        $scope.loading = true;
        $scope.mismatchResponse.error = false;
        $scope.mismatchResponse.mismatches = [];
        spotcheckMismatchApi.getMismatches($scope.datasource.selected.value, contentTypes[$scope.selectedTab],
            toMismatchStatus($scope.status), $scope.pagination.getLimit(), $scope.pagination.getOffset())
            .then(function (result) {
                $scope.pagination.setTotalItems(result.pagination.total);
                $scope.mismatchResponse.mismatches = result.mismatches;
                $scope.loading = false;
            })
            .catch(function (response) {
                $scope.loading = false;
                $scope.mismatchResponse.error = true;
                $scope.mismatchResponse.errorMessage = response.statusText;
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
        $scope.pagination.itemsPerPage = 10;
    }

    function initializeDate() {
        if ($routeParams.hasOwnProperty('date')) {
            $scope.date = moment($routeParams.date, dateFormat);
        }
        else {
            $scope.date = moment().startOf('day');
            onDateChange();
        }
    }

    function onDateChange() {
        $location.search('date', $scope.date.format(dateFormat)).replace();
    }

    $scope.init = function () {
        resetPagination();
        initializeDate();
        $scope.datasource.selected = $scope.datasource.values[0];
        $scope.updateMismatchSummary();
        $scope.updateMismatches();
    };

    $scope.init();

}
