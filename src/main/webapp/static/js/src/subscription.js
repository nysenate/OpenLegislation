(function () {
    var openPublicApp = angular.module('open-public', ['ngMaterial', 'ngResource']);

    openPublicApp.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    openPublicApp.factory('UserSubscriptions', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/email/subscription/update?key=:key");
    }]);

    openPublicApp.factory('CurrentSubscriptions', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/email/subscription/current?key=:key");
    }]);

    openPublicApp.factory('UpdateEmail', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/email/subscription/updateEmail?key=:key");
    }]);

    openPublicApp.controller('SubscriptionCtrl', ['$scope', '$location', 'UserSubscriptions',
        'CurrentSubscriptions', 'UpdateEmail', subscriptionCtrl]);


    function subscriptionCtrl($scope, $location, userSubApi, currentSubApi, updateEmailApi) {
        $scope.title = "Email Subscriptions";
        $scope.instructions = "Please check all subscriptions you would like to be enrolled in:";
        $scope.instructionsTwo = "To unsubscribe from all subscriptions, leave the boxes un-checked.";
        $scope.processingMessage = "Your subscriptions are being updated...";
        $scope.submitMessage = "Your changes have been submitted.";
        $scope.submitted = false;
        $scope.processing = false;
        $scope.pageLoaded = false;

        $scope.key = $location.search().key;
        $scope.unsub = $location.search().unsub;
        $scope.link = document.getElementById("back-link");
        $scope.link.href = window.location.href;
        $scope.errmsg = '';
        $scope.currentSubs = [];
        $scope.invalidKey = false;
        $scope.emailInput = '';
        $scope.validEmailMessageOn = false;
        $scope.processingEmail = false;
        $scope.emailSubmitted = false;

        //removing unsubscribe parameter to prevent loops
        $location.search("unsub", null);

        $scope.subscriptionsAvailable = [
            {
                title: 'Breaking Changes', enumVal: 'BREAKING_CHANGES', checked: false,
                desc: "Breaking changes to the API."
            },
            {
                title: 'New Features', enumVal: 'NEW_FEATURES', checked: false,
                desc: "New features added to the API."
            }
        ];

        /*  Check the boxes for the users current subscriptions  */
        if($scope.key) {
            currentSubApi.query({key: $scope.key}).$promise.then(
                //success
                function (data) {
                    if ($scope.unsub) {
                        $scope.submitMessage = "You are now unsubscribed from all subscriptions.";
                        $location.search('unsub', null);
                        $scope.link.href = window.location.href;
                        $scope.uncheckAll();
                    }
                    $scope.currentSubs = data;
                    $scope.subscriptionsAvailable.forEach(function (sub) {
                        if ($scope.currentSubs.indexOf(sub.enumVal) > -1) {
                            sub.checked = true;
                        }
                    });
                    $scope.pageLoaded = true;
                },
                function () {
                    $scope.errmsg = "Invalid Api User Key";
                    $scope.invalidKey = true;
                });
        } else {
            $scope.errmsg = "Invalid Api User Key";
            $scope.invalidKey = true;
        }

        $scope.uncheckAll = function () {
            $scope.subscriptionsAvailable.forEach(function (sub) {
                sub.checked = false;
            });
            $scope.submitMessage = "You are now unsubscribed from all subscriptions.";
            $scope.updateSubscriptions();
        };

        $scope.updateSubscriptions = function () {
            $scope.subscriptions = [];
            $scope.processing = true;

            /* Check for the subscriptions that are checked and send them in request */
            var subs = [];
            $scope.subscriptionsAvailable.forEach(function (sub) {
                if (sub.checked) {
                    subs.push(sub.enumVal);
                }
            });
            $scope.subscriptions = subs;

            userSubApi.save({key: $scope.key}, $scope.subscriptions).$promise.then(
                function (data) {
                    $scope.processing = false;
                    $scope.signedup = true;
                    $scope.submitted = true;
                },
                function () {
                    $scope.processing = false;
                    $scope.errmsg = 'Sorry, there was an error while processing your request.';
                });

        };

        $scope.updateEmail = function () {
            if ($scope.emailInput === "" || $scope.emailInput === undefined) {
                $scope.validEmailMessageOn = true;
            } else {
                $scope.validEmailMessageOn = false;
                $scope.processingEmail = true;

                //update their email
                updateEmailApi.save({key: $scope.key}, $scope.emailInput).$promise.then(
                    function (data) {
                        $scope.processingEmail = false;
                        $scope.emailSubmitted = true;
                        $scope.emailInput = '';

                    },
                    function () {
                        $scope.processingEmail = false;
                        $scope.errmsg = 'Sorry, there was an error while updating your email.';
                    });
            }
        };

        $scope.turnOffSubmitMessage = function () {
            $scope.emailSubmitted = false;
            $scope.validEmailMessageOn = false;
        };
    }

})();
