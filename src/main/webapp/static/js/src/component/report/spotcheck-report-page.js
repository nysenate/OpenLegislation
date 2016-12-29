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
        switch (contentTypes[$scope.selectedTab]) {
            case 'BILL':
                getBillMismatches($scope.datasource.selected.value, toMismatchStatus($scope.status),
                    $scope.pagination.getLimit(), $scope.pagination.getOffset());
                break;
            case 'CALENDAR':
                getCalendarMismatches($scope.datasource.selected.value, toMismatchStatus($scope.status),
                    $scope.pagination.getLimit(), $scope.pagination.getOffset());
                break;
            case 'AGENDA':
                console.log("agenda");
                break;
            default:
                console.log("default");
                break;
        }

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

    function getBillMismatches(datasource, statuses, limit, offset) {
        spotcheckMismatchApi.getBills(datasource, statuses, limit, offset)
            .then(function (results) {
                $scope.pagination.setTotalItems(results.pagination.total);
                $scope.mismatches = results.mismatches;
                $scope.loading = false;
            });
    }

    function getCalendarMismatches(datasource, statuses, limit, offset) {
        spotcheckMismatchApi.getCalendars(datasource, statuses, limit, offset)
            .then(function (results) {
                $scope.pagination.setTotalItems(results.pagination.total);
                $scope.mismatches = results.mismatches;
                $scope.loading = false;
            })
    }

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
