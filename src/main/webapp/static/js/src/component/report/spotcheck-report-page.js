angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$route','$location', '$routeParams', '$mdDialog', '$mdDateLocale','PaginationModel', 'SpotcheckMismatchApi',
            'SpotcheckMismatchSummaryApi', 'SpotcheckMismatchIgnoreAPI', 'SpotcheckMismatchTrackingAPI', 'SpotcheckMismatchDeleteAllAPI', 'SpotcheckMismatchDeleteAllAPI', ReportCtrl]);

function ReportCtrl($scope, $route,$location, $routeParams, $mdDialog, $mdDateLocale,paginationModel, spotcheckMismatchApi,
                    mismatchSummaryApi, mismatchIgnoreApi, spotcheckMismatchTrackingAPI, spotcheckMismatchDeleteAllAPI) {

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
    $scope.orderby = 'OBSERVED_DATE';
    $scope.sort = 'DESC';
    $scope.currentPage = 1;
    $scope.showGoto = false;

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
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatches();
    };

    $scope.onTabChange = function () {
        resetPagination();
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatches();
    };

    $scope.onPageChange = function (pageNum, contentType) {
        if (contentType == selectedContentType())
            $scope.updateMismatches();
    };

    $scope.updateMismatchStatusSummary = function () {
        $scope.summaryResponse.error = false;
        mismatchSummaryApi.getMismatchStatusSummary($scope.datasource.selected.value, $scope.date.endOf('day').format(isoFormat)).then(function (mismatchSummary) {
            $scope.summaryResponse.summary = mismatchSummary;
        })
            .catch(function (response) {
                $scope.summaryResponse.error = true;
                $scope.summaryResponse.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatchTypeSummary = function () {
        $scope.summaryResponse.error = false;
        mismatchSummaryApi.getMismatchTypeSummary($scope.datasource.selected.value, $scope.date.endOf('day').format(isoFormat), $scope.status)
            .then(function (mismatchSummary) {
                $scope.summaryResponse.summary = mismatchSummary;
            })
            .catch(function (response) {
                $scope.summaryResponse.error = true;
                $scope.summaryResponse.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatchContentTypeSummary = function () {
        $scope.summaryResponse.error = false;
        mismatchSummaryApi.getMismatchContentTypeSummary($scope.datasource.selected.value, $scope.date.endOf('day').format(isoFormat), $scope.status, $scope.selectedMismatchType)
            .then(function (mismatchSummary) {
                $scope.summaryResponse.summary = mismatchSummary;
            })
            .catch(function (response) {
                $scope.summaryResponse.error = true;
                $scope.summaryResponse.errorMessage = response.statusText;
            });
    };

    $scope.onMismatchTypeChange = function () {
        resetPagination();
        $scope.updateMismatches();
        $scope.updateMismatchTypeSummary();
    }
    $scope.updateOrder = function (column, $event) {
        if ($scope.orderby == column) {
            if ($scope.sort == 'DESC')
                $scope.sort = 'ASC';
            else
                $scope.sort = 'DESC';
        }
        else {
            $scope.orderby = column;
            $scope.sort = 'DESC';
        }
        $scope.updateMismatches();
        updateOrderIcon($event);
    }
    function updateOrderIcon(event) {
        $("i.icon-arrow-long-down").remove() // remove all down arrows
        $("i.icon-arrow-long-up").remove()// remove all up arrows
        var ASC = document.createElement('i');
        ASC.className = 'icon-arrow-long-up';
        var DESC = document.createElement('i');
        DESC.className = 'icon-arrow-long-down';
        if ($scope.sort == "ASC")
            event.toElement.appendChild(ASC);
        else
            event.toElement.appendChild(DESC);
    }

    $scope.updateMismatches = function () {
        $scope.loading = true;
        $scope.mismatchResponse.error = false;
        $scope.mismatchResponse.mismatches = [];
        spotcheckMismatchApi.getMismatches($scope.datasource.selected.value, selectedContentType(),
            toMismatchStatus($scope.status), $scope.date.startOf('day').format(isoFormat), $scope.date.endOf('day').format(isoFormat),
            $scope.pagination.getLimit(), $scope.pagination.getOffset(), $scope.orderby, $scope.sort, $scope.selectedMismatchType)
            .then(function (result) {
                $scope.pagination.setTotalItems(result.pagination.total);
                $scope.mismatchResponse.mismatches = result.mismatches;
                $scope.loading = false;
                $scope.currentPage =  $scope.pagination.currPage;
                $scope.showGoto = (result.pagination.total > $scope.pagination.getLimit())
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
                return ['NEW', 'EXISTING', 'REGRESSION'];
            }
            else if (status == 'NEW') {
                return ['NEW'];

            } else {
                return ['RESOLVED']
            }
        }
    };

    $scope.onGotoChange = function () {
        $scope.pagination.currPage = $scope.currentPage;
    };

    $scope.numberWithCommas = function(x) {
        if(x == undefined || x == "" )
            return null;
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
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
                issueId: mismatch.issue
            };
            spotcheckMismatchTrackingAPI.save(params, function (response) {
            })
        }
        $('#report-page-toast' + mismatch.id).fadeIn("5000");
        $('#report-page-toast' + mismatch.id).fadeOut("slow");
    };

    // update mismatch when user change status
    $scope.onStatusChange = function () {
        resetPagination();
        $scope.updateMismatchTypeSummary();
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

        $mdDialog.show(confirm).then(function () {
            ignoreMismatch(mismatch);
        })
    };

    // show the diff window.
    $scope.showDetailedDiff = function (mismatchRow) {
        mismatchRow.diffLoading = true;
        setTimeout(function () {
            $mdDialog.show({
                templateUrl: 'mismatchDetailWindow',
                controller: 'detailDialogCtrl',
                locals: {
                    mismatchRow: mismatchRow,
                    source: $scope.datasource.selected.value,
                    contentType: selectedContentType()
                }
            });
            mismatchRow.diffLoading = false;
        }, 10); // delay 1 sec
    };

    // ignore a mismatch, the default ignore level is 'IGNORE_UNTIL_RESOLVED'
    function ignoreMismatch(mismatch) {
        var params = {
            mismatchId: mismatch.id,
            ignoreLevel: 'IGNORE_UNTIL_RESOLVED'
        };
        mismatchIgnoreApi.save(params, function (response) {
            $scope.updateMismatchTypeSummary();
            $scope.updateMismatchContentTypeSummary();
            $scope.updateMismatchStatusSummary();
            $scope.updateMismatches();
        })
    }

    $scope.onDateChange = function () {
        $scope.date = moment(  $scope.pickedDate);
        $location.search('date', $scope.date.format(dateFormat)).replace();
        $route.reload();
    }

    function initializeDate() {
        if ($routeParams.hasOwnProperty('date')) {
            $scope.date = moment($routeParams.date, dateFormat);
            $scope.pickedDate = new Date($scope.date);
        }
        else {
            $scope.date = moment().startOf('day');
            $location.search('date', $scope.date.format(dateFormat)).replace();
            $scope.pickedDate = new Date(moment().startOf('day'));
        }
        $scope.maxDate = new Date(moment().startOf('day'));

        $mdDateLocale.formatDate = function (date) {
            return moment(date).format(dateFormat);
        };
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
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $scope.mismatchTypes = Object.values(window.mismatchMap);
    };

    $scope.init();

}
