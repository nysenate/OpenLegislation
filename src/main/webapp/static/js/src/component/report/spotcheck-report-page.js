angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$route', '$location', '$routeParams', '$mdDialog', '$mdDateLocale', 'PaginationModel', 'SpotcheckMismatchApi',
            'SpotcheckMismatchSummaryApi', 'SpotcheckMismatchIgnoreAPI', 'SpotcheckMismatchTrackingAPI', 'SpotcheckMismatchDeleteAllAPI',
            'SpotcheckMismatchDeleteAllAPI', ReportCtrl]);

function ReportCtrl($scope, $route, $location, $routeParams, $mdDialog, $mdDateLocale, paginationModel, spotcheckMismatchApi,
                    mismatchSummaryApi, mismatchIgnoreApi, spotcheckMismatchTrackingAPI, spotcheckMismatchDeleteAllAPI) {

    const dateFormat = 'YYYY-MM-DD';
    const isoFormat = 'YYYY-MM-DDTHH:mm:ss';
    /** Used to look up content types corresponding to the selected tab. */
    const contentTypes = ['BILL', 'CALENDAR', 'AGENDA'];

    const searchParams = {
        date: 'date',
        datasource: 'source',
        contentType: 'content',
        mismatchStatus: 'status',
        mismatchType: 'type',
        currentPage: 'page'
    };

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

    $scope.mismatchStatusSummary = {
        selected: "OPEN",
        summary: {},
        error: false,
        errorMessage: ''
    };
    $scope.mismatchTypeSummary = {
        selected: "All",
        summary: {},
        error: false,
        errorMessage: ''
    };
    $scope.mismatchContentTypeSummary = {
        summary: {},
        error: false,
        errorMessage: ''
    };

    /* --- Input callbacks --- */

    $scope.onDateChange = function () {
        $scope.date = moment($scope.pickedDate);
        $location.search(searchParams.date, $scope.date.format(dateFormat)).replace();
        $route.reload();
    };

    $scope.onDatasourceChange = function () {
        resetPagination();
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $location.search(searchParams.datasource, $scope.datasource.selected.value)
    };

    $scope.onTabChange = function () {
        resetPagination();
        $scope.updateMismatches();
        $scope.updateMismatchContentTypeSummary();
        $location.search(searchParams.contentType, contentTypes[$scope.selectedTab]);
    };

    // update mismatch when user change status
    $scope.onStatusChange = function () {
        resetPagination();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatches();
        $location.search(searchParams.mismatchStatus, $scope.mismatchStatusSummary.selected);
    };

    $scope.onMismatchTypeChange = function () {
        resetPagination();
        $scope.updateMismatches();
        $scope.updateMismatchContentTypeSummary();
        $location.search(searchParams.mismatchType, $scope.mismatchTypeSummary.selected);
    };

    $scope.onPageChange = function (pageNum, contentType) {
        if (contentType === selectedContentType()) {
            $scope.updateMismatches();
        }
        $location.search(searchParams.currentPage, pageNum);
    };

    $scope.onGotoChange = function () {
        $scope.pagination.currPage = $scope.currentPage;
    };

    /* --- Content update functions --- */

    $scope.updateMismatchStatusSummary = function () {
        $scope.mismatchStatusSummary.error = false;
        mismatchSummaryApi.getMismatchStatusSummary($scope.datasource.selected.value, $scope.date._i)
            .then(function (mismatchSummary) {
                $scope.mismatchStatusSummary.summary = mismatchSummary.summary;
            })
            .catch(function (response) {
                $scope.mismatchStatusSummary.error = true;
                $scope.mismatchStatusSummary.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatchTypeSummary = function () {
        $scope.mismatchTypeSummary.error = false;
        mismatchSummaryApi.getMismatchTypeSummary($scope.datasource.selected.value, $scope.date._i, $scope.mismatchStatusSummary.selected)
            .then(function (mismatchSummary) {
                $scope.mismatchTypeSummary.summary = mismatchSummary;
            })
            .catch(function (response) {
                $scope.mismatchTypeSummary.error = true;
                $scope.mismatchTypeSummary.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatchContentTypeSummary = function () {
        $scope.mismatchContentTypeSummary.error = false;
        mismatchSummaryApi.getMismatchContentTypeSummary($scope.datasource.selected.value, $scope.date._i, $scope.mismatchStatusSummary.selected, $scope.mismatchTypeSummary.selected)
            .then(function (mismatchSummary) {
                $scope.mismatchContentTypeSummary.summary = mismatchSummary.summary;
            })
            .catch(function (response) {
                $scope.mismatchContentTypeSummary.error = true;
                $scope.mismatchContentTypeSummary.errorMessage = response.statusText;
            });
    };

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
        spotcheckMismatchApi.getMismatches(
            $scope.datasource.selected.value,
            selectedContentType(),
            $scope.mismatchStatusSummary.selected,
            $scope.mismatchTypeSummary.selected,
            $scope.formatDate($scope.date),
            $scope.pagination.getLimit(),
            $scope.pagination.getOffset(),
            $scope.orderby,
            $scope.sort
        )
            .then(function (result) {
                $scope.pagination.setTotalItems(result.pagination.total);
                $scope.mismatchResponse.mismatches = result.mismatches;
                $scope.loading = false;
                $scope.currentPage = $scope.pagination.currPage;
                $scope.showGoto = (result.pagination.total > $scope.pagination.getLimit())
            })
            .catch(function (response) {
                $scope.loading = false;
                $scope.mismatchResponse.error = true;
                $scope.mismatchResponse.errorMessage = response.statusText;
            });

    };

    $scope.numberWithCommas = function (x) {
        if (x == undefined || x == "")
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

    $scope.formatDate = function (date) {
        return date.format(dateFormat);
    };

    function resetPagination() {
        $scope.pagination.reset();
        $scope.pagination.setTotalItems(0);
        $scope.pagination.itemsPerPage = 10;
        $location.search(searchParams.currentPage, 1);
    }

    $scope.confirmIgnoreMismatch = function (mismatch) {
        var confirm = $mdDialog.confirm()
            .title("Ignore mismatch?")
            .ok('Yes')
            .cancel('No');

        $mdDialog.show(confirm).then(function () {
            ignoreMismatch(mismatch);
        })
    };

    // show the diff window.
    $scope.showDetailedDiff = function (mismatch) {
        mismatch.diffLoading = true;
        setTimeout(function () {
            $mdDialog.show({
                templateUrl: 'mismatchDetailWindow',
                controller: 'detailDialogCtrl',
                locals: {
                    mismatch: mismatch,
                    source: $scope.datasource.selected.value,
                    contentType: selectedContentType()
                }
            });
            mismatch.diffLoading = false;
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

    function initializeDate() {
        if ($routeParams.hasOwnProperty(searchParams.date)) {
            $scope.date = moment($routeParams[searchParams.date], dateFormat);
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

    $scope.mismatchTypeLabel = function (type, count) {
        return window.mismatchMap[type] + " (" + count + ")"
    };

    function selectedContentType() {
        return contentTypes[$scope.selectedTab];
    }

    /* --- Parameter initialization --- */

    function initDatasource() {
        $scope.datasource.selected = $scope.datasource.values[0];
        if ($routeParams.hasOwnProperty(searchParams.datasource)) {
            $scope.datasource.values.forEach(function (datasource) {
                if (datasource.value === $routeParams[searchParams.datasource]) {
                    $scope.datasource.selected = datasource;
                }
            })
        }
    }

    function initContentType() {
        if ($routeParams.hasOwnProperty(searchParams.contentType)) {
            $scope.selectedTab = contentTypes.indexOf($routeParams[searchParams.contentType]);
        }
    }

    function initMismatchStatus() {
        if ($routeParams.hasOwnProperty(searchParams.mismatchStatus)) {
            $scope.mismatchStatusSummary.selected = $routeParams[searchParams.mismatchStatus];
        }
    }

    function initMismatchType() {
        if ($routeParams.hasOwnProperty(searchParams.mismatchType)) {
            $scope.mismatchTypeSummary.selected = $routeParams[searchParams.mismatchType];
        }
    }

    function initPage() {
        if ($routeParams.hasOwnProperty(searchParams.currentPage)) {
            $scope.currentPage = $routeParams[searchParams.currentPage];
            $scope.pagination.currPage = $scope.currentPage;
        }
    }

    function initializeParameters() {
        initializeDate();
        initDatasource();
        initContentType();
        initMismatchStatus();
        initMismatchType();
        initPage();
    }

    $scope.init = function () {
        resetPagination();
        initializeParameters();
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Spotcheck Reports");
    };

    $scope.init();

}
