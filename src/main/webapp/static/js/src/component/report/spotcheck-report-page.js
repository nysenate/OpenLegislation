angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$location', '$routeParams', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, $location, $routeParams, spotcheckMismatchApi, mismatchSummaryApi) {

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

    $scope.updateMismatches = function () {
        switch (contentTypes[$scope.selectedTab]) {
            case 'BILL':
                getBillMismatches($scope.datasource.selected.value, toMismatchStatus($scope.status));
                break;
            case 'CALENDAR':
                getCalendarMismatches($scope.datasource.selected.value, toMismatchStatus($scope.status));
                break;
            case 'AGENDA':
                console.log("agenda");
                break;
            default:
                console.log("default");
                break;
        }
    };

    function getBillMismatches(datasource, statuses) {
        spotcheckMismatchApi.getBills(datasource, statuses)
            .then(function (billMismatches) {
                $scope.mismatches = billMismatches;
            });
    }

    function getCalendarMismatches(datasource, statuses) {
        spotcheckMismatchApi.getCalendars(datasource, statuses)
            .then(function (calMismatches) {

            })
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

    function onDateChange() {
        $location.search('date', $scope.date.format(dateFormat)).replace();
    }

    $scope.init = function () {
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
