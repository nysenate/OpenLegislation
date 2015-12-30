var adminModule = angular.module('open.admin');

adminModule.factory('LogSearchAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/apiLogs/');
}]);

adminModule.controller('LogsCtrl', ['$scope', '$routeParams', 'PaginationModel', 'LogSearchAPI',
    function($scope, $routeParams, PaginationModel, LogSearchAPI) {

    $scope.view = (parseInt($routeParams.view, 10) || 0);

    /** Watch for changes to the current view. */
    $scope.$watch('view', function(n, o) {
        if (n !== o && $routeParams.view !== n) {
            $scope.setSearchParam('view', $scope.view);
        }
    });

    // Api log monitor

    $scope.stompClient = null;
    $scope.now = moment();
    $scope.newApiRequestsCount = 0;
    $scope.newApiRequests = [];

    $scope.connectToSocket = function() {
        var socket = new SockJS(window.ctxPath + '/sock');
        $scope.stompClient = Stomp.over(socket);
        $scope.stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            $scope.now = moment();
            $scope.stompClient.subscribe('/event/apiLogs', function(logEvent) {
                $scope.$apply(function() {
                    $scope.newApiRequests.unshift(JSON.parse(logEvent.body));
                    if ($scope.newApiRequests.length > 2000) {
                        $scope.newApiRequests = [];
                    }
                    $scope.newApiRequestsCount++;
                });
            });
        });
    };

    $scope.resetRunningLog = function() {
        $scope.now = moment();
        $scope.newApiRequests = [];
        $scope.newApiRequestsCount = 0;
    };

    $scope.disconnect = function() {
        if ($scope.stompClient != null) {
            $scope.stompClient.disconnect();
        }
    };

    // Api log search

    $scope.apiLogTerm = $routeParams.apiLogTerm || '';
    $scope.apiLogSort = $routeParams.apiLogSort || 'requestTime:desc';
    $scope.apiLogFromDate = ($routeParams.apiLogStart) ? moment($routeParams.apiLogStart).toDate()
                                                       : moment().subtract('days', 1).toDate();
    $scope.apiLogToDate = ($routeParams.apiLogEnd) ? moment($routeParams.apiLogEnd).toDate()
                                                   : moment().add('days', 1).toDate();
    $scope.apiLogSearchPagination = angular.extend({}, PaginationModel);
    $scope.apiLogSearchPagination.currPage = $routeParams.page || 1;
    $scope.apiLogSearchPagination.itemsPerPage = 20;

    $scope.searchLogs = function() {
        var start = $scope.toZonelessISOString(moment($scope.apiLogFromDate));
        var end = $scope.toZonelessISOString(moment($scope.apiLogToDate));
        $scope.setSearchParam('apiLogStart', start);
        $scope.setSearchParam('apiLogEnd', end);
        $scope.setSearchParam('apiLogTerm', $scope.apiLogTerm);
        $scope.setSearchParam('apiLogSort', $scope.apiLogSort);

        var term = '(' + ($scope.apiLogTerm || '*') + ')' +
            ' AND requestTime:[' + start + ' TO ' + end + ']';
        LogSearchAPI.get({
            term: term, sort: $scope.apiLogSort,
            limit: $scope.apiLogSearchPagination.getLimit(),
            offset: $scope.apiLogSearchPagination.getOffset()}, function(resp) {
            if (resp && resp.success) {
                $scope.lawSearchResp = resp;
                $scope.lawSearchResults = resp.result.items;
            }
        });
    };

    $scope.apiLogSearchPageChange = function(newPageNumber) {
        $scope.setSearchParam('page', newPageNumber);
        $scope.searchLogs();
    };

    /** --- Initialize --- */

    $scope.init = function() {
        $scope.setHeaderText("View Logs");
        $scope.setHeaderVisible(true);
        $scope.searchLogs();
        $scope.connectToSocket();
    };

    $scope.init();
}]);