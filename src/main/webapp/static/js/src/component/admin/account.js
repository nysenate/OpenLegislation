var adminModule = angular.module('open.admin');

/** --- REST resources for getting and setting admin account data --- */

// Allows for retrieval, creation, and deletion of admin accounts
adminModule.factory('AccountsAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + '/accounts/:username', {username: '@username', master: '@master'});
}]);

// Changes the password of the currently authenticated user
adminModule.factory('PassChangeAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/accounts/passchange', {password: '@password'});
}]);

/** --- Account Settings Ctrl --- */

adminModule.controller('AccountSettingsCtrl', ['$scope', '$routeParams', '$location',
function($scope, $routeParams, $location) {

    var pageNames = ['passchange', 'notification_subscriptions', 'manage_users'];

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

adminModule.controller('PassChangeCtrl', ['$scope', '$element', '$mdToast', 'PassChangeAPI',
function($scope, $element, $mdToast, PassChangeAPI) {
    $scope.minPassLength = 5;
    $scope.newPass = "";
    $scope.newPassRepeat = "";
    $scope.response = null;

    $scope.submitNewPass = function() {
        if ($scope.newPass.length >= $scope.minPassLength) {
            $scope.response = PassChangeAPI.save({password: $scope.newPass}, function() {
                $scope.showToast('Password Changed');
                $scope.newPass="";
                $scope.newPassRepeat = "";
            }, function(errorResponse) {
                if (errorResponse.data.errorCode === 1) {
                    $scope.showToast(errorResponse.data.errorData.parameterConstraint);
                } else {
                    $scope.showToast(errorResponse.data.message);
                }
            });
        } else if ($scope.newPass === $scope.newPassRepeat) {
            $scope.showToast("Password too short!");
        } else {
            $scope.showToast("Passwords must match!");
        }
    };

    $scope.showToast = function(content) {
        return $mdToast.show($mdToast.simple().position('right').content(content));
    }
}]);

/** -- Manage Admin Users controller -- */

adminModule.controller('ManageAdminUsersCtrl', ['$scope', '$mdToast', '$mdDialog', 'AccountsAPI',
function ($scope, $mdToast, $mdDialog, AccountsApi) {
    $scope.accounts = [];
    var blankAccount = {username: '', master: false};
    $scope.newAccount = blankAccount;
    $scope.newAccountShown = false;

    $scope.init = function() {
        $scope.getAccounts();
    };

    $scope.getAccounts = function() {
        $scope.loadingAccounts = true;
        AccountsApi.get({username: ""}, function(response) {
            $scope.accounts = response.result.items;
            $scope.loadingAccounts = false;
        });
    };

    $scope.createAccount = function() {
        $scope.creatingAccount = true;
        AccountsApi.save($scope.newAccount, function() {
            $scope.creatingAccount = false;
            $scope.getAccounts();
            $scope.newAccount = blankAccount;
            $scope.newAccountShown = false;
        }, function(errorResponse) {
            $scope.creatingAccount = false;
            if (errorResponse.data.errorCode === 1 || !errorResponse.data.errorCode) {
                $scope.errorToast('Invalid username: must be @nysenate.gov email address');
            } else {
                $scope.errorToast(errorResponse.data.message);
            }
        })
    };

    $scope.removeAccount = function(username) {
        $scope.removingAccount = true;
        AccountsApi.delete({username: username}, function(response) {
            $scope.removingAccount = false;
            $scope.getAccounts();
        }, function (errorResponse) {
            $scope.removingAccount = false;
            console.log("err0r removing", username);
            $scope.errorToast(errorResponse.data.message);
        });
    };

    $scope.errorToast = function(text) {
        $mdToast.show({
            template: '<md-toast>' + text + '</md-toast>',
            parent: angular.element('#admin-management'),
            position: 'fit left'
        })
    };

    $scope.deletePrompt = function(username) {
        $mdDialog.show($mdDialog.confirm()
                .title("Confirm Admin Removal")
                .content("Delete admin account " + username + "?")
                .ok("Yes!").cancel("Never Mind")
        ).then(function () {
                $scope.removeAccount(username)
            });
    }

}]);