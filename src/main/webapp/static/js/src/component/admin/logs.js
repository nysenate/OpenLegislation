var adminModule = angular.module('open.admin');

adminModule.factory('LogSearchAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/apiLogs/');
}]);

adminModule.controller('LogsCtrl', ['$scope', 'LogSearchAPI', function($scope, LogSearchAPI) {

    $scope.stompClient = null;
    $scope.now = moment();
    $scope.term = '';

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

    $scope.searchLogs = function() {
        LogSearchAPI.get({term: $scope.term}, function(resp) {
            if (resp && resp.success) {
                $scope.lawSearchResp = resp;
                $scope.lawSearchResults = resp.result.items;
                console.log($scope.lawSearchResults);
            }
        });
    };

    $scope.init = function() {
        $scope.setHeaderText("View Logs");
        $scope.setHeaderVisible(true);
        $scope.searchLogs();
        $scope.connectToSocket();
    };

    $scope.init();
}]);