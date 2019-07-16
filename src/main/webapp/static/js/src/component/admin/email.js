(function () {
    var openApp = angular.module('open.admin');

    openApp.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    openApp.factory('SendEmail', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/admin/email/batchEmail");
    }]);

    openApp.factory('SendTestEmail', ['$resource', function ($resource) {
        return $resource(ctxPath + "/api/3/admin/email/testModeEmail");
    }]);

    openApp.controller('EmailCtrl', ['$scope', '$location', '$mdDialog', '$sce', '$window',
        'SendEmail', 'SendTestEmail', emailCtrl]);

    function emailCtrl($scope, $location, $mdDialog, $sce, $window, SendEmail, SendTestEmail) {
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
        $scope.signatureOn = true;
        $scope.testMode = true;
        $scope.testModeMessage = "ON";
        $scope.testModeMessageTwo = "In test mode, the e-mail will be sent only to you.";

        /* Create an optional signature for the emails */
        $scope.logoUrl = "https://legislation.nysenate.gov/static/img/nys_logo224x224.png";
        $scope.signature =  '\n<!--Signature-->\n' +
                            '<div style="background:lightgrey;text-align:left; ' +
                                'display:flex;flex-direction:row">\n' +
                            '  <img src="'+$scope.logoUrl+'" alt="NYS Logo" style="padding:5px;width:70px;height:70px;"/>\n' +
                            '  <div style="font-size:12px;padding-top:5px;">\n' +
                            '    <span style="font-size:16px;">Open Legislation</span><br>\n' +
                            '    <span>From the <a href="https://www.nysenate.gov/">New York State Senate</a></span><br>\n' +
                            '    <a href="https://legislation.nysenate.gov/">https://legislation.nysenate.gov/</a><br>\n' +
                            '    <a href="https://github.com/nysenate/OpenLegislation">https://github.com/nysenate/OpenLegislation</a>\n' +
                            '  </div>\n' +
                            '</div>\n' +
                            '<!--SignatureEnd-->';
        $scope.body = $scope.signature;
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

        /* Function to turn testMode on and off */
        $scope.testModeButton = function(testMode) {
            if(testMode) {
                $scope.testModeMessage = "ON";
                $scope.testModeMessageTwo = "In test mode, the e-mail will be sent only to you.";
            } else {
                $scope.testModeMessage = "OFF";
                $scope.testModeMessageTwo = "CAREFUL: The batch email will be sent out.";
            }
        }

        /* Function to update the body to add/remove signature */
        $scope.signatureButton = function(signatureOn){
            if(signatureOn) {
                $scope.body += $scope.signature;
            } else {
                var start = $scope.body.indexOf("<!--Signature-->");
                var end = $scope.body.indexOf("SignatureEnd") + 14;
                //remove the newline characters before and after the script as well
                $scope.body = $scope.body.substring(0,start-1) + $scope.body.substring(end+1, $scope.body.length);
            }
            $scope.bodyHtml = $sce.trustAsHtml($scope.body);
        };

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
                $scope.invalidMessage += "\nGroups";
            }
            if (!$scope.subject) {
                $scope.invalidSubject = true;
                $scope.invalidMessage += "\nSubject";
            }
            if (!$scope.body) {
                $scope.invalidBody = true;
                $scope.invalidMessage += "\nBody";
            }
            if ($scope.invalidSubject || $scope.invalidBody || $scope.invalidSubs) {
                $scope.displayInvalidMessage = true;
            } else {
                $scope.validInput = true;
            }
        };



        $scope.showDialog = function () {
            var parentElement = angular.element(document.body);
            if($scope.testMode) {
                $scope.dialogMessage ='<strong>You are in test mode.</strong> <br> The email  will only be sent to you. <br>' +
                    'Are you sure you want to send it?';
            } else {
                $scope.dialogMessage = '<strong>Batch email WILL be sent out!</strong> <br> Carefully read over your email. ' +
                    'Are you <br> sure you are ready to send it?';
            }
            $mdDialog.show({
                parent: parentElement,
                template:
                    '<md-dialog id="batch-email-dialog">' +
                    '     <md-dialog-content>' +
                    '         <h3>Ready to send?</h3>' +
                    '         <p>' + $scope.dialogMessage + '</p>' +
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

            if($scope.testMode) {
                //send the message to the admin only
                SendTestEmail.save(emailObj).$promise.then(
                  function(data) {
                      $scope.sending = false;
                      $scope.sent = true;
                  },
                  function() {
                      $scope.sending = false;
                      $scope.error = "There was an error sending the message.";
                  }
                );
            } else {
                //send the email to all the subscribers
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
            }
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