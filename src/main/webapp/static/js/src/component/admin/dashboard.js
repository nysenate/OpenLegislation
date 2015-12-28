angular.module('open.admin')
    .controller('DashboardCtrl', ['$scope', '$routeParams',
function ($scope, $routeParams) {
    $scope.activeIndex = parseInt($routeParams.view, 10) || 0;

    $scope.init = function() {
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Manage configuration");
    };

    $scope.$watch('activeIndex', function(n, o) {
        if (n !== o && $routeParams.view !== n) {
            $scope.setSearchParam('view', $scope.activeIndex);
        }
    });

    $scope.viewMap = {
        'environment': 0,
        'cache': 1,
        'index': 2
    };

    $scope.init();
}]);