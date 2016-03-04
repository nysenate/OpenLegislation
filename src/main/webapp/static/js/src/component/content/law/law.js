var lawModule = angular.module('open.law', ['open.core', 'open.api', 'infinite-scroll']);

/**
 * LawCtrl
 */
lawModule.controller('LawCtrl', ['$scope', '$location', '$routeParams', function($scope, $location, $routeParams) {
    $scope.selectedView = (parseInt($routeParams.view, 10) || 0);

    $scope.setHeaderText("NYS Laws");

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
}]);
