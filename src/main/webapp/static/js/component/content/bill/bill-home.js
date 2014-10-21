var contentModule = angular.module('content');

contentModule.factory('BillListing', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear', {
        sessionYear: '@sessionYear'
    });
}]);

contentModule.factory('BillSearch', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&limit=:limit&offset=:offset', {
        term: '@term',
        limit: '@limit',
        offset: '@offset'
    });
}]);

contentModule.controller('BillHomeCtrl', ['$scope', '$filter', 'BillListing', 'BillSearch',
    function($scope, $filter, BillListing, BillSearch) {
    $scope.title = 'Bills and Resolutions';
    $scope.searchTerm = '';
    $scope.billResults = {};
    $scope.totalResults = 0;
    $scope.limit = 10;
    $scope.offset = 1;
    $scope.page = 1;

    $scope.$watch('searchTerm', function(newTerm, oldTerm) {
        if (newTerm && newTerm != oldTerm && newTerm.trim() != '') {
            $scope.page = 1;
            $scope.search();
        }
    });

    $scope.$watch('page', function(newPage, oldPage) {
        if (newPage != oldPage) {
            $scope.offset = $scope.limit * newPage;
            $scope.search();
        }
    });

    $scope.search = function() {
        if ($scope.searchTerm != null && $scope.searchTerm.trim() != '') {
            $scope.billResults = BillSearch.get({term: $scope.searchTerm, limit: $scope.limit, offset: $scope.offset},
                function() {
                    if ($scope.billResults.total != $scope.totalResults) {
                        $scope.totalResults = $scope.billResults.total;
                    }
                });
        }
    }

}]);

