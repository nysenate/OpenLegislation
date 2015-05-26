angular.module('open.dashboard', ['open.core'])
    .controller('DashboardCtrl', ['$scope', '$routeParams',
function ($scope, $routeParams) {
    $scope.views = ['environment'];
    $scope.activeIndex = 0;

    $scope.init = function() {
        $scope.setHeaderVisible(true);
        if ($routeParams.hasOwnProperty('view')) {
            $scope.goToView($routeParams['view']);
        }
    };

    var headerText = {
        environment: "View and Set Environment Variables"
    };
    $scope.$watch('activeIndex', function() {
        $scope.setHeaderText(headerText[$scope.views[$scope.activeIndex]]);
        $scope.setSearchParam('view', $scope.views[$scope.activeIndex]);
    });

    $scope.goToView = function(viewName) {
        var index = $scope.views.indexOf(viewName);
        if (index >= 0) {
            $scope.activeIndex = index;
        } else {
            console.log("no such view:", viewName);
        }
    };
}]);