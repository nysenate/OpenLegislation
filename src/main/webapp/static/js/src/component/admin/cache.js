var adminModule = angular.module('open.admin');

adminModule.factory('CacheAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/cache/:cacheType', {
        'cacheType': '@cacheType'
    }, {
        'update': { method: 'PUT' }
    });
}]);

adminModule.controller('CacheCtrl', ['$scope', '$timeout', 'CacheAPI',
    function($scope, $timeout, CacheAPI) {
    $scope.caches = [];

    $scope.loading = {};

    $scope.fetchCaches = function() {
        $scope.cacheResp = CacheAPI.get({}, function() {
            if ($scope.cacheResp.success === true) {
                $scope.caches = $scope.cacheResp.result.items;
            }
        });
    };

    $scope.evictCache = function(cacheName) {
        CacheAPI.delete({cacheType: cacheName}, function(resp) {
            console.log(resp);
            $scope.fetchCaches();
        });
    };

    $scope.warmCache = function(cacheName) {
        $scope.loading[cacheName] = true;
        CacheAPI.update({cacheType: cacheName}, function(resp) {
            $scope.loading[cacheName] = false;
            $scope.fetchCaches();
        });
        var poll = function() {
            if ($scope.loading[cacheName] === true) {
                $scope.fetchCaches();
                $timeout(poll, 5000);
            }
        };
        poll();
    };

    $scope.init = function() {
        $scope.fetchCaches();
    };
}]);
