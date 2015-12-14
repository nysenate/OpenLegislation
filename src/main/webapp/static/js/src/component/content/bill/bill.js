var billModule = angular.module('open.bill', ['open.core', 'open.api']);

/** --- Parent Bill Controller --- */

billModule.controller('BillCtrl', ['$scope', '$rootScope', '$location', '$route', '$routeParams',
                      'BillUtils', 'MemberApi',
                       function($scope, $rootScope, $location, $route, $routeParams, BillUtils, MemberApi) {
    $scope.setHeaderVisible(true);
    $scope.setHeaderText('Search NYS Legislation');

    $scope.selectedView = (parseInt($routeParams.view, 10) || 0);

    /** Watch for changes to the current view. */
    $scope.$watch('selectedView', function(n, o) {
        if (n !== o && $location.search().view !== n) {
            $location.search('view', $scope.selectedView);
            $scope.$broadcast('viewChange', $scope.selectedView);
        }
    });

    $scope.$on('$locationChangeSuccess', function() {
        $scope.selectedView = +($location.search().view) || 0;
    });

    // Convenience method for formatting bill status description
    $scope.getStatusDesc = function(status) {
        return BillUtils.getStatusDesc(status);
    }
}]);