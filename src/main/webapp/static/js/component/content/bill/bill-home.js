var contentModule = angular.module('content');

contentModule.factory('BillListing', ['$resource', function($resource) {
    return $resource(apiPath + "/bills/:sessionYear", {
        sessionYear: '@sessionYear'
    });
}]);

contentModule.controller('BillHomeCtrl', ['$scope', '$filter', 'BillListing', function($scope, $filter, BillListing) {
    $scope.title = 'Bills and Resolutions';
}]);