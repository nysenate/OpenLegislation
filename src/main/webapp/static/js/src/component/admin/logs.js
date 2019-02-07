var adminModule = angular.module('open.admin');

adminModule.factory('LogSearchAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/apiLogs/');
}]);

adminModule.factory('DataProcessRunsAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/process/runs/:from/:to');
}]);

adminModule.factory('DataProcessRunsDetailsAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/process/runs/id/:id');
}]);

adminModule.controller('LogsCtrl', ['$scope', '$routeParams', '$timeout', 'PaginationModel', 'LogSearchAPI',
    'DataProcessRunsAPI', 'DataProcessRunsDetailsAPI',
    function($scope, $routeParams, $timeout, PaginationModel, LogSearchAPI, DataProcessRunsAPI, DataProcessRunsDetailsAPI) {

    const TAB = {
        API_MONITOR: 0,
        API_LOG_SEARCH: 1,
        DATA_PROCESS_LOG: 2
    };

    $scope.view = (parseInt($routeParams.view, 10) || 0);

    /** Watch for changes to the current view. */
    $scope.$watch('view', function(n, o) {
        if (n !== o && $routeParams.view !== n) {
            $scope.setSearchParam('view', $scope.view || null);
        }
        if (n === TAB.API_MONITOR) {
            $scope.resetRunningLog();
            $scope.connectToSocket();
        }
        if (n !== TAB.API_MONITOR) {
            $scope.disconnect();
        }
    });

    // Api log monitor

    $scope.stompClient = null;
    $scope.now = moment();
    $scope.newApiRequestsCount = 0;
    $scope.newApiRequests = [];
    $scope.newApiRequestLimit = 100;
    $scope.connectToSocket = function() {
        var socket = new SockJS(window.ctxPath + '/sock');
        $scope.stompClient = Stomp.over(socket);
        $scope.stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            $scope.now = moment();
            $scope.stompClient.subscribe('/event/apiLogs', function(logEvent) {
                $scope.$apply(function() {
                    $scope.newApiRequests.unshift(JSON.parse(logEvent.body));
                    if ($scope.newApiRequests.length > $scope.newApiRequestLimit) {
                        $scope.newApiRequests = $scope.newApiRequests.slice(0, $scope.newApiRequestLimit);
                    }
                    $scope.newApiRequestsCount++;
                });
            });
        });
    };

    $scope.padLeft = function (n) {
        return (n < 10) ? ("0" + n) : n;
    };

    $scope.resetRunningLog = function() {
        $scope.now = moment();
        $scope.newApiRequests = [];
        $scope.newApiRequestsCount = 0;
    };

    // Disconnect when leaving this template.
    $scope.$on("$destroy", function() {
        $scope.disconnect();
    });

    $scope.disconnect = function() {
        if ($scope.stompClient != null) {
            $scope.stompClient.disconnect();
        }
    };

    // Api log search

    $scope.apiLogTerm = $routeParams.apiLogTerm || '';
    const defaultLogSort = 'requestTime:desc';
    $scope.apiLogSort = $routeParams.apiLogSort || defaultLogSort;

    function getDefaultFromDate() {
        return moment().subtract('days', 1).startOf('day');
    }
    $scope.apiLogFromDate = ($routeParams.apiLogStart) ? moment($routeParams.apiLogStart).toDate()
                                                       : getDefaultFromDate().toDate();
    $scope.apiLogToDate = ($routeParams.apiLogEnd) ? moment($routeParams.apiLogEnd).toDate()
                                                   : moment().add('days', 1).toDate();
    $scope.apiLogSearchPagination = angular.extend({}, PaginationModel);
    $scope.apiLogSearchPagination.currPage = $routeParams.page || 1;
    $scope.apiLogSearchPagination.itemsPerPage = 20;

    $scope.searchLogs = function() {
        var start = moment($scope.apiLogFromDate);
        var end = moment($scope.apiLogToDate);
        var startStr = $scope.toZonelessISOString(start);
        var endStr = $scope.toZonelessISOString(end);
        $scope.setSearchParam('apiLogStart', getDefaultFromDate().isSame(start) ? null : startStr);
        $scope.setSearchParam('apiLogEnd', moment().isBefore(end) ? null : endStr);
        $scope.setSearchParam('apiLogTerm', $scope.apiLogTerm || null);
        $scope.setSearchParam('apiLogSort', $scope.apiLogSort !== defaultLogSort ? $scope.apiLogSort : null);

        var term = '(' + ($scope.apiLogTerm || '*') + ')' +
            ' AND requestTime:[' + startStr + ' TO ' + endStr + ']';
        LogSearchAPI.get({
            term: term, sort: $scope.apiLogSort,
            limit: $scope.apiLogSearchPagination.getLimit(),
            offset: $scope.apiLogSearchPagination.getOffset()}, function(resp) {
            if (resp && resp.success) {
                $scope.logSearchResp = resp;
                $scope.logSearchResults = resp.result.items;
            }
        });
    };

    $scope.apiLogSearchPageChange = function(newPageNumber) {
        $scope.setSearchParam('page', newPageNumber > 1 ? newPageNumber : null);
        $scope.searchLogs();
    };

    // Data Process Runs

    $scope.runsResults = [];
    $scope.pollPromise = null;
    $scope.hideEmptyRuns = $routeParams.hideEmptyRuns !== 'false';
    $scope.runsFromDate = ($routeParams.runsStart) ? moment($routeParams.runsStart).toDate()
        : getDefaultFromDate().toDate();
    $scope.runsToDate = ($routeParams.runsEnd) ? moment($routeParams.runsEnd).toDate()
        : moment().add('days', 1).toDate();
    $scope.runsPagination = angular.extend({}, PaginationModel);
    $scope.runsPagination.currPage = $routeParams.runLogPage || 1;
    $scope.runsPagination.itemsPerPage = 20;

    $scope.getRuns = function() {
        var fromDate = moment($scope.runsFromDate);
        var toDate = moment($scope.runsToDate);
        var fromStr = $scope.toZonelessISOString(fromDate);
        var toStr = $scope.toZonelessISOString(toDate);
        $scope.setSearchParam('hideEmptyRuns', $scope.hideEmptyRuns && null);
        $scope.setSearchParam('runsStart', getDefaultFromDate().isSame(fromDate) ? null : fromStr);
        $scope.setSearchParam('runsEnd', moment().isBefore(toDate) ? null : toStr);
        DataProcessRunsAPI.get({
            from: fromStr,
            to: toStr,
            full: !$scope.hideEmptyRuns,
            detail: true,
            limit: $scope.runsPagination.getLimit(),
            offset: $scope.runsPagination.getOffset()
            },
            function(resp) {
                $scope.runsResp = resp;
                if (resp.success) {
                    $scope.runsResults = resp.result.items;
                }
            });
    };

    $scope.getRunsPolling = function() {
        $scope.getRuns();
        console.log("In runs polling");
        $scope.pollPromise = $timeout(function() {
            $scope.getRunsPolling();
        }, 15000);
    };

    $scope.dataProcessLogPageChange = function(newPageNumber) {
        if ($scope.pollPromise) {
            $timeout.cancel($scope.pollPromise);
        }
        $scope.getRunsPolling();
        $scope.setSearchParam('runLogPage', newPageNumber > 1 ? newPageNumber : null);
    };

    /** --- Initialize --- */

    $scope.init = function() {
        $scope.setHeaderText("View Logs");
        $scope.setHeaderVisible(true);
        $scope.searchLogs();
        $scope.getRuns();
    };

    $scope.init();
}]);