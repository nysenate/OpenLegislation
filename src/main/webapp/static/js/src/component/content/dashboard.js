var openModule = angular.module('open');

openModule.controller('ContentDashboardCtrl', ['$scope', '$timeout', function($scope, $timeout) {
    $scope.setHeaderVisible(true);
    $scope.setHeaderText('Legislation Dashboard');

    $scope.items = [];
    for (var i = 0; i < 1000; i++) {
        $scope.items.push(i);
    }
}]);
