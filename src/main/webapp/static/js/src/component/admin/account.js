var accountModule = angular.module('open.account', ['open.core']);

/** --- REST resources for getting and setting admin account data --- */

// Creates a new admin user with the specified username
accountModule.factory('CreateAccountAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/accounts/create', {username: '@username', master: '@master'});
}]);

// Removes the admin user with the specified username
accountModule.factory('RemoveAccountAPI', ['$resource', function ($resource){
    return $resource(adminApiPath + '/accounts/remove', {username: '@username'});
}]);

// Changes the password of the currently authenticated user
accountModule.factory('PassChangeAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/accounts/passchange', {password: '@password'});
}]);

/** --- Account Settings Ctrl --- */

accountModule.controller('AccountSettingsCtrl', ['$scope', '$routeParams', '$location',
function($scope, $routeParams, $location) {

    var pageNames = ['passchange', 'notification_subscriptions'];

    $scope.init = function() {
        if ($routeParams.hasOwnProperty('view')) {
            var view = $routeParams['view'];
            if (Number(view)) {
                $scope.selectedIndex = Number(view);
            } else {
                var viewIndex = pageNames.indexOf(view);
                if (viewIndex >= 0) {
                    $scope.selectedIndex = viewIndex;
                }
            }
        }
    };

    $scope.$watch('selectedIndex', function () {
        if ($scope.selectedIndex >= 0 && $scope.selectedIndex < pageNames.length) {
            $location.search('view', pageNames[$scope.selectedIndex]).replace()
        }
    });

    $scope.init();
}]);

/** --- Password change controller --- */

accountModule.controller('PassChangeCtrl', ['$scope', '$element', '$mdToast', 'PassChangeAPI',
function($scope, $element, $mdToast, PassChangeAPI) {
    $scope.minPassLength = 5;
    $scope.newPass = "";
    $scope.response = null;

    $scope.submitNewPass = function() {
        if ($scope.newPass.length >= $scope.minPassLength) {
            $scope.response = PassChangeAPI.save({password: $scope.newPass}, function() {
                if ($scope.response.success) {
                    $scope.showToast('Password Changed');
                    $scope.newPass="";
                } else if (!$scope.response.success && $scope.response.hasOwnProperty('errorCode')
                            && $scope.response['errorCode'] === 193) {
                    $scope.showToast('Error: the entered password matches the existing password');
                }
            })
        } else {
            $scope.showToast('Error: The password must be at least 5 characters');
        }
    };

    $scope.showToast = function(content) {
        return $mdToast.show($mdToast.simple().position('right').content(content));
    }
}]);