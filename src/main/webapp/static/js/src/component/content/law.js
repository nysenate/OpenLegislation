var contentModule = angular.module('content');

contentModule.factory('LawListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws');
}]);

contentModule.factory('LawTreeApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId', {
        lawId: '@lawId'
    });
}]);

contentModule.controller('LawCtrl', ['$scope', '$location', '$route', function($scope, $location, $route) {

}]);

contentModule.controller('LawListingCtrl', ['$scope', '$location', '$route', 'LawListingApi',
                        function($scope, $location, $route, LawListingApi) {
    $scope.lawListingResult = LawListingApi.get({}, function() {
        $scope.lawListings = {};
        for (var i = 0; i < $scope.lawListingResult.result.size; i++) {
            var item = $scope.lawListingResult.result.items[i];
            if (!$scope.lawListings.hasOwnProperty(item.lawType)) {
                $scope.lawListings[item.lawType] = [];
            }
            $scope.lawListings[item.lawType].push(item);
        }
    });
}]);

contentModule.controller('LawViewCtrl', ['$scope', '$routeParams', '$location', '$route', 'LawTreeApi',
                        function($scope, $routeParams, $location, $route, LawTreeApi) {
    $scope.lawTreeResult = LawTreeApi.get({lawId: $routeParams.lawId}, function(){
        $scope.lawTree = $scope.lawTreeResult.result;
        console.log($scope.lawTree);
    });

}]);


