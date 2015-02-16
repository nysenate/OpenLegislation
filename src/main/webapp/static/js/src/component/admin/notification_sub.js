var notificationModule = angular.module('open.notification.subscription', ['open.core']);

notificationModule.factory('GetNotifSubsApi', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/notifications/subscriptions');
}]);

notificationModule.factory('SubscribeToNotifApi', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/notifications/subscribe', {type: '@type', target: '@target', address: '@address'});
}]);

notificationModule.factory('UnsubscribeToNotifApi', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/notifications/unsubscribe', {type: '@type', target: '@target', address: '@address'});
}]);

/** --- Notification Subscription Controller --- */

notificationModule.controller('NotificationSubCtrl', ['$scope', '$timeout', '$q', 'GetNotifSubsApi', 'SubscribeToNotifApi', 'UnsubscribeToNotifApi',
function ($scope, $timeout, $q, getSubsApi, subscribeApi, unSubscribeApi) {

    // a list of active notification subscriptions
    $scope.subscriptions = [];

    // a blank subscription
    var cleanSubscription = {
        type: "",
        target: "",
        address: ""
    };

    // model for the entry form to enter a new subscription
    $scope.newSubscription = Object.create(cleanSubscription);

    $scope.newSubscriptionShown = false;

    $scope.selectAll = false;

    $scope.selectedSubs = 0;

    // Retrieves current subscriptions for the authenticated user
    $scope.getSubscriptions = function() {
        var response = null;
        response = getSubsApi.get({}, function () {
            if (response.success) {
                $scope.subscriptions = response.result.items;
                $scope.selectAll = false;
                $scope.selectedSubs = 0;
            }
        });
    };

    // Submits the new subscription in the form and refreshes the active subscription list
    $scope.registerNewSubscription = function() {
        var response = null;
        response = subscribeApi.get($scope.newSubscription, function() {
            if (response.success) {
                $scope.newSubscription = Object.create(cleanSubscription);
                $scope.getSubscriptions();
                $scope.hideNewSubscription();
            }
        })
    };

    $scope.unsubscribeSelected = function () {
        $q.all(getSelectedSubs().map(function(sub) {return unSubscribeApi.get(sub).$promise;}))
            .then($scope.getSubscriptions);
    };

    $scope.toggleNewSubscription = function() {
        if ($scope.newSubscriptionShown) {
            $scope.hideNewSubscription();
        } else {
            $scope.showNewSubscription();
        }
    };

    $scope.showNewSubscription = function() {
        $scope.newSubscriptionShown = true;
    };

    $scope.hideNewSubscription = function() {
        $scope.newSubscriptionShown = false;
        $scope.newSubscription = Object.create(cleanSubscription);
    };

    var getSelectedSubs = function() {
        return $scope.subscriptions.filter(function(sub) {
            return sub.selected;
        })
    };

    // Counts up the number of selected subscriptions
    $scope.tallySelectedSubs = function() {
        $timeout(function() {
            $scope.selectedSubs = getSelectedSubs().length;
            if ($scope.selectedSubs < $scope.subscriptions.length) {
                $scope.selectAll = false;
            }
        }, 0);
    };

    function modifyAllSelections(selected) {
        angular.forEach($scope.subscriptions, function (subscription) {
            subscription.selected = selected;
        });
    }

    $scope.applySelectAll = function() {
        $timeout(function() {
            modifyAllSelections($scope.selectAll);
            $scope.tallySelectedSubs();
        }, 0);
    };

    function startsWithFilter(searchTerm) {
        if(searchTerm) {
            var searchTermUpper = searchTerm.toUpperCase();
        }
        else {
            var searchTermUpper = "";
        }
        return function(str) {
            return str.slice(0, searchTerm.length) == searchTermUpper;
        }
    }

    $scope.searchForNotificationType = function(searchTerm) {
        return $scope.notificationTypes.filter(startsWithFilter(searchTerm));
    };

    $scope.searchForNotificationTarget = function (searchTerm) {
        return searchTerm ? $scope.notificationTargets.filter(startsWithFilter(searchTerm)) : [];
    };

    $scope.getSubscriptions();
}]);