angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$location', '$routeParams', '$mdDialog', 'PaginationModel', 'SpotcheckMismatchApi',
            'SpotcheckMismatchSummaryApi', 'SpotcheckMismatchIgnoreAPI','SpotcheckMismatchTrackingAPI','SpotcheckMismatchDeleteAllAPI','SpotcheckMismatchDeleteAllAPI', ReportCtrl]);

function ReportCtrl($scope, $location, $routeParams, $mdDialog, paginationModel, spotcheckMismatchApi,
                    mismatchSummaryApi, mismatchIgnoreApi,spotcheckMismatchTrackingAPI,spotcheckMismatchDeleteAllAPI) {

    const dateFormat = 'YYYY-MM-DD';
    const isoFormat = 'YYYY-MM-DDTHH:mm:ss';
    /** Used to look up content types corresponding to the selected tab. */
    const contentTypes = ['BILL', 'CALENDAR', 'AGENDA'];

    $scope.datasource = {
        values: [
            {
                value: 'LBDC',
                label: 'LBDC - OpenLegislation'
            },
            {
                value: 'NYSENATE',
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
    $scope.diffLoading = false;

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

    $scope.onPageChange = function (pageNum, contentType) {
        if(contentType ==selectedContentType())
            $scope.updateMismatches();
    };

    $scope.updateMismatchSummary = function () {
        $scope.summaryResponse.error = false;
        mismatchSummaryApi.get($scope.datasource.selected.value, $scope.date.endOf('day').format(isoFormat))
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
        spotcheckMismatchApi.getMismatches($scope.datasource.selected.value, selectedContentType(),
            toMismatchStatus($scope.status), $scope.date.startOf('day').format(isoFormat),$scope.date.endOf('day').format(isoFormat),
            $scope.pagination.getLimit(), $scope.pagination.getOffset())
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
                return ['NEW', 'EXISTING','REGRESSION'];
            }
            else if (status == 'NEW'){
                return ['NEW'];

            }else {
                return ['RESOLVED']
            }
        }
    };
    // update mismatch's issues, issue splits by comma
    $scope.updateIssue = function (mismatch) {
        if (mismatch.issue == "") { // if issue is empty, then clear all related issues
            var params = {
                mismatchId: mismatch.id
            };
            spotcheckMismatchDeleteAllAPI.delete(params, function (response) {
            })
        }
        else {
            var params = { // update issue
                mismatchId: mismatch.id,
                issueId:mismatch.issue
            };
            spotcheckMismatchTrackingAPI.save(params, function (response) {
            })
        }
        $('#report-page-toast'+mismatch.id).fadeIn("5000");
        $('#report-page-toast'+mismatch.id).fadeOut("slow");
    }

    // update mismatch when user change status
    $scope.onStatusChange= function () {
        resetPagination();
        $scope.updateMismatchSummary();
        $scope.updateMismatches();
    };

    $scope.formatDate = function (date) {
        return date.format(dateFormat);
    };

    function resetPagination() {
        $scope.pagination.reset();
        $scope.pagination.setTotalItems(0);
        $scope.pagination.itemsPerPage = 10;
    }

    $scope.confirmIgnoreMismatch = function (mismatch) {
        var confirm = $mdDialog.confirm()
            .title("Ignore mismatch?")
            .ok('Yes')
            .cancel('No')

        $mdDialog.show(confirm).then(function() {
            ignoreMismatch(mismatch);
        })
    };
    // show the diff window.
    $scope.showDetailedDiff = function(mismatchRow) {
        mismatchRow.diffLoading = true;
        setTimeout(function (){
            $mdDialog.show({
                templateUrl: 'mismatchDetailWindow',
                controller: 'detailDialogCtrl',
                locals: {
                    mismatchRow: mismatchRow,
                    source:$scope.datasource.selected.value,
                    contentType:selectedContentType()
                }
            });
            mismatchRow.diffLoading = false;
        }, 1000); // delay 1 sec
    };
    // ignore a mismatch, the default ignore level is 'IGNORE_UNTIL_RESOLVED'
    function ignoreMismatch(mismatch) {
        var params = {
            mismatchId: mismatch.id,
            ignoreLevel: 'IGNORE_UNTIL_RESOLVED'
        };
        mismatchIgnoreApi.save(params, function (response) {
            $scope.updateMismatchSummary();
            $scope.updateMismatches();
        })
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

    function selectedContentType() {
        return contentTypes[$scope.selectedTab];
    }

    /**
     * Updates the total mismatch count of each content type's tab
     * for the selected mismatch status.
     */
    $scope.getSummaryCountForContentType = function (contentType) {
        if ($scope.summaryResponse.summary[contentType] == null) {
            return '';
        }
        return $scope.summaryResponse.summary[contentType][$scope.status] || '';
    };

    $scope.init = function () {
        resetPagination();
        initializeDate();
        $scope.datasource.selected = $scope.datasource.values[0];
        $scope.updateMismatchSummary();
        $scope.updateMismatches();
    };

    $scope.init();

}
