var logoutModule = angular.module('open.logout', ['open.core']);

logoutModule.factory('LogoutApi', ['$resource', function ($resource){
    return $resource(ctxPath + "/logout");
}]);


logoutModule.controller('LogoutCtrl', ['$scope', '$window', '$timeout', 'LogoutApi',
function ($scope, $window, $timeout, LogoutApi) {
    $scope.setHeaderVisible(true);
    $scope.setHeaderText("Logging you out...");
    LogoutApi.get({}, function() {
        $scope.setHeaderText("redirecting...");
        console.log("redirecting...");
        $window.history.back();
        window.location.reload(true);
    })
}]);