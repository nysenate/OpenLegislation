(function () {
    var openApp = angular.module('open.admin');

    openApp.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    openApp.factory('SendEmail', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/admin/email/batchEmail");
    }]);

    openApp.controller('EmailCtrl', ['$scope', '$location', '$mdDialog', '$sce', 'SendEmail', emailCtrl]);

    function emailCtrl($scope, $location, $mdDialog, $sce, SendEmail) {
        $scope.header.text = "Batch Email";
        $scope.header.visible = true;
        $scope.title = "Send a New Batch Email";
        $scope.validInput = false;
        $scope.subject = "";
        $scope.body = "";
        $scope.bodyHtml = "";
        $scope.invalidSubs = false;
        $scope.invalidSubject = false;
        $scope.invalidBody = false;
        $scope.invalidMessage = "*Please fill in the following fields: ";
        $scope.displayInvalidMessage = false;
        $scope.check = false;
        $scope.sending = false;
        $scope.sent = false;
        $scope.error = "";
        $scope.previewOn = false;

        $scope.subscriptionsAvailable = [
            {
                title: 'Breaking Changes', enumVal: "BREAKING_CHANGES", checked: false,
                desc: "Breaking changes to the API."
            },
            {
                title: 'New Features', enumVal: "NEW_FEATURES", checked: false,
                desc: "New features added to the API."
            }
        ];

        /* Function to check that no fields are blank */
        $scope.validation = function () {
            $scope.check = false;
            $scope.displayInvalidMessage = false;
            $scope.invalidSubs = false;
            $scope.invalidSubject = false;
            $scope.invalidBody = false;
            $scope.validInput = false;

            //Get the data entered by admin
            $scope.subscriptionsAvailable.forEach(function (sub) {
                if (sub.checked) {
                    $scope.check = true;
                }
            });

            $scope.invalidMessage = "*Please fill in the following fields: ";
            if (!$scope.check) {
                $scope.invalidSubs = true;
                $scope.invalidMessage += "\n\n\tGroups";
            }
            if (!$scope.subject) {
                $scope.invalidSubject = true;
                $scope.invalidMessage += "\n\n\tSubject";
            }
            if (!$scope.body) {
                $scope.invalidBody = true;
                $scope.invalidMessage += "\n\n\tBody";
            }
            if ($scope.invalidSubject || $scope.invalidBody || $scope.invalidSubs) {
                $scope.displayInvalidMessage = true;
            } else {
                $scope.validInput = true;
            }
        };

        $scope.showDialog = function () {
            var parentElement = angular.element(document.body);
            $mdDialog.show({
                parent: parentElement,
                template:
                    '<md-dialog id="batch-email-dialog">' +
                    '     <md-dialog-content>' +
                    '         <h3>Ready to send?</h3>' +
                    '         <p>Carefully read over your email. Are you <br>' +
                    '        sure you are ready to send it?</p>' +
                    '     </md-dialog-content>' +
                    '     <md-dialog-actions>' +
                    '         <md-button ng-click="closeDialog()" class="md-primary" id="no-button">' +
                    '             Cancel' +
                    '         </md-button>' +
                    '         <md-button ng-click="closeAndSend()" class="md-primary" id="yes-button">' +
                    '             Send Email' +
                    '         </md-button>' +
                    '     </md-dialog-actions>' +
                    '</md-dialog>',
                locals: {
                    sendMessage: $scope.sendMessage
                },
                controller: dialogController
            });

            function dialogController($scope, $mdDialog, sendMessage) {
                $scope.closeDialog = function () {
                    $mdDialog.hide();
                };
                $scope.closeAndSend = function () {
                    $mdDialog.hide();
                    sendMessage();
                }
            }
        };

        /* Function called when the user decides to send */
        $scope.submit = function () {
            $scope.validation();

            //confirm that the admin wants the message sent
            if ($scope.validInput) {
                $scope.showDialog();
            }
        };

        /* Function to send the message once the admin confirms it is ready to send */
        $scope.sendMessage = function () {
            $scope.sending = true;

            /* Check for the subscriptions in that are checked and send them in the request */
            $scope.subscriptions = [];
            $scope.subscriptionsAvailable.forEach(function (sub) {
                if (sub.checked) {
                    $scope.subscriptions.push(sub.enumVal);
                }
            });

            var emailObj = {
                "subscriptions": $scope.subscriptions,
                "subject": $scope.subject,
                "body": $scope.body
            };

            SendEmail.save(emailObj).$promise.then(
                function (data) {
                    $scope.sending = false;
                    $scope.sent = true;
                },
                function () {
                    $scope.sending = false;
                    $scope.error = "There was an error sending the message.";
                }
            );
        };

        $scope.enterPreview = function () {
            $scope.previewOn = true;
            $scope.bodyHtml = $sce.trustAsHtml($scope.body);
        };

        $scope.exitPreview = function () {
            $scope.previewOn = false;
        }
    }
})();