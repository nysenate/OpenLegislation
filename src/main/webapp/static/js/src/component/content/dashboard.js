var openModule = angular.module('open');

openModule.controller('ContentDashboard', ['$scope', function($scope) {
    $scope.setHeaderVisible(true);
    $scope.setHeaderText('Legislation Dashboard');
}]);
