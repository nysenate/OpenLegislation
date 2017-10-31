angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', '$route', '$location', '$routeParams', '$mdDialog', '$mdDateLocale', '$timeout',
            'PaginationModel', 'SpotcheckMismatchApi',
            'SpotcheckMismatchSummaryApi', 'SpotcheckMismatchIgnoreAPI',
            'SpotcheckMismatchTrackingAPI', 'SpotcheckMismatchDeleteAllAPI',
            'SpotcheckMismatchDeleteAllAPI', ReportCtrl])
;

function ReportCtrl($scope, $route, $location, $routeParams, $mdDialog, $mdDateLocale, $timeout,
                    paginationModel, spotcheckMismatchApi, mismatchSummaryApi, mismatchIgnoreApi,
                    spotcheckMismatchTrackingAPI, spotcheckMismatchDeleteAllAPI) {

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
            },
            {
                value:'OPENLEG',
                label: 'Openleg Ref - Openleg Source'
            }
        ],
        selected: {}
    };

    /**
     * Defines unique id columns that are used for each content type
     */
    $scope.idColumns = {
        BILL: [
            {
                name: "Bill",
                orderId: "PRINT_NO",
                field: "billId",
                class: "spotcheck-col-bill-id"
            }
        ],
        CALENDAR: [
            {
                name: "Num",
                orderId: "CAL_NO",
                field: "calNo",
                class: "spotcheck-col-cal-no"
            },
            {
                name: "Type",
                orderId: "CAL_TYPE",
                field: "calType",
                class: "spotcheck-col-cal-type"
            }
        ],
        AGENDA: [
            {
                name: "Num",
                orderId: "AGENDA_NO",
                field: "agendaNo",
                class: "spotcheck-col-agenda-no"
            },
            {
                name: "Committee",
                orderId: "AGENDA_COMMITTEE",
                field: "committee",
                class: "spotcheck-col-agenda-comm"
            }
        ]
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
        $scope.updateMismatchContentTypeSummary();
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $location.search(searchParams.datasource, $scope.datasource.selected.value)
    };

    $scope.onTabChange = function () {
        resetPagination();
        $scope.updateMismatchStatusSummary();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $location.search(searchParams.contentType, contentTypes[$scope.selectedTab]);
    };

    $scope.onStatusChange = function () {
        resetPagination();
        $scope.updateMismatchTypeSummary();
        $scope.updateMismatches();
        $location.search(searchParams.mismatchStatus, $scope.mismatchStatusSummary.selected);
    };

    $scope.onMismatchTypeChange = function () {
        resetPagination();
        $scope.updateMismatches();
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

    $scope.currentDay = function () {
        return !(moment($scope.date).isValid && moment().isAfter($scope.date, 'day'));
    };

    $scope.jumpToToday = function () {
        $scope.pickedDate = new Date();
        $scope.onDateChange();
    };

    /* --- Content update functions --- */

    $scope.updateMismatchStatusSummary = function () {
        $scope.mismatchStatusSummary.error = false;
        mismatchSummaryApi.getMismatchStatusSummary($scope.date._i, $scope.datasource.selected.value, selectedContentType())
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
        mismatchSummaryApi.getMismatchTypeSummary($scope.date._i, $scope.datasource.selected.value,
                                                  selectedContentType(), $scope.mismatchStatusSummary.selected)
            .then(function (mismatchSummary) {
                $scope.mismatchTypeSummary.summary = mismatchSummary;
                if (!mismatchSummary.hasOwnProperty($scope.mismatchTypeSummary.selected)) {
                    var keys = Object.keys(mismatchSummary);
                    if (keys.length > 0) {
                        $scope.mismatchTypeSummary.selected = keys[0];
                        $scope.onMismatchTypeChange();
                    }
                }
            })
            .catch(function (response) {
                $scope.mismatchTypeSummary.error = true;
                $scope.mismatchTypeSummary.errorMessage = response.statusText;
            });
    };

    $scope.updateMismatchContentTypeSummary = function () {
        $scope.mismatchContentTypeSummary.error = false;
        mismatchSummaryApi.getMismatchContentTypeSummary($scope.date._i, $scope.datasource.selected.value)
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
        var strippedInput = (mismatch.issueInput || '').replace(/\s/g, '');
        var strippedIssue = (mismatch.issue || '').replace(/\s/g, '');

        if (strippedInput === strippedIssue) {
            // do nothing if the input issue id is not different from the existing issue id
            return;
        }

        var params = {
            mismatchId: mismatch.id,
            issueId: mismatch.issueInput
        };
        var promise = null;
        if (strippedInput.length === 0) {
            // if issue is empty, then clear all related issues
            promise = spotcheckMismatchDeleteAllAPI.delete(params).$promise;
        }
        else {
            // otherwise save the issue id
            promise = spotcheckMismatchTrackingAPI.save(params).$promise;
        }
        function onSuccess () {
            mismatch.issueSaved = true;
            $timeout(function () {  // toggle the saved flag to trigger css transition
                mismatch.issueSaved = false;
            });
            mismatch.issue = mismatch.issueInput;
        }
        function onFail (resp) {
            console.error('Error updating issue id:', resp)
        }

        promise.then(onSuccess, onFail);
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

