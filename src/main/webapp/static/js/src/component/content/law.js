var lawModule = angular.module('open.law', ['open.core']);

lawModule.factory('LawListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws');
}]);

lawModule.factory('LawTreeApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId', {
        lawId: '@lawId'
    });
}]);

lawModule.factory('LawDocApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/:docId', {
        lawId: '@lawId',
        docId: '@docId'
    });
}]);

lawModule.controller('LawCtrl', ['$scope', '$location', '$route', function($scope, $location, $route) {

}]);

lawModule.controller('LawListingCtrl', ['$scope', '$location', '$route', 'LawListingApi',
                        function($scope, $location, $route, LawListingApi) {
    $scope.setHeaderText('NYS Laws');
    $scope.curr = {
        selectedView: 0
    };
    $scope.lawListingResponse = LawListingApi.get({}, function(){
        console.log($scope.lawListingResponse);
      $scope.lawListing = $scope.lawListingResponse.result.items;
    });

}]);

lawModule.controller('LawViewCtrl', ['$scope', '$routeParams', '$location', '$route', 'LawTreeApi',
                        function($scope, $routeParams, $location, $route, LawTreeApi) {

}]);


