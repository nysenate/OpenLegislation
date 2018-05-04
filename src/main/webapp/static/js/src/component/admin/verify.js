var adminModule = angular.module('open.admin');

adminModule.factory('MemberAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/member');
}]);

adminModule.factory('MemberSearchAPI', ['$resource', function ($resource) {
    return $resource(window.ctxPath + '/api/3/members/search');
}]);

/* This controller is for the verifying process of the Member Management Page.
 * There are three different steps which is controlled by $scope.step.
 * 1. Select "create new member" or "link to existing"
 * 2. fill out additional fields
 * 3. Finish confirmation
 * Upon completion the user is directed back to the members page.
 */

adminModule.controller('VerifyCtrl', ['$scope', '$timeout', '$routeParams', '$mdDialog', 'MemberAPI', 'MemberSearchAPI', 'PaginationModel',
    function($scope, $timeout,$routeParams, $mdDialog, MemberAPI, MemberSearchAPI, PaginationModel) {
        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 4;

        $scope.step = 1; // controls which step we are on in the verification process
        $scope.headerText = 'Verify Member';
        $scope.verifyInformation = '.';
        $scope.linking = false; // verify as new member or link to existing member?

    // STEP ONE //
        //$scope.member; // the dummy record that we are verifying
        $scope.membersList = [];
        $scope.searchInput = '';
        $scope.totalResults = 0;

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Manage Members");
            // get the member that we are trying to verify. Id is passed in as a routeParams.
            var params = {
                term: '(verified:false)AND(memberId:' + $routeParams.memberId + ')',
                full: true
            };
            MemberSearchAPI.get(params, function(resp) {
                if (resp.success === true) {
                    if (resp.total != 1) { // if we find more than one member with the given id then we have an error
                        var errorResp = {
                            "status" : null,
                            "data" : {
                                "message" : "Found more than one member with id " + $routeParams.memberId
                            }
                        };
                        $scope.showErrorMessage(errorResp);
                        return;
                    }
                    $scope.member = resp.result.items[0];
                }
            }, $scope.showErrorMessage);
        };

        $scope.updateMembersList = function(termParam) {
            var params = {
                term: termParam + 'AND(verified:true)', // we only care about verified members when verifying
                limit: $scope.pagination.getLimit(),
                offset: $scope.pagination.getOffset(),
                full: true
            };
            MemberSearchAPI.get(params, function(resp) {
                if (resp.success === true) {
                    $scope.membersList = resp.result.items;
                    $scope.pagination.setTotalItems(resp.total);
                    $scope.totalResults = resp.total;
                }
            }, $scope.showErrorMessage);
        };

        $scope.toStepTwo = function(member) {
            $scope.step = 2;
            if (member == null) { // create new member
                $scope.verifyInformation = ' as new member.';
                $scope.linking = false;
                $scope.inputMember = {
                    firstName: '',
                    middleName: '',
                    lastName: '',
                    suffix: '',
                    email: '',
                    incumbent: false,
                    districtCode: '',
                    imgName: ''
                };
            } else { // link to existing member
                $scope.verifyInformation = ' by linking to existing member.';
                $scope.linking = true;
                $scope.inputMember = member;
                $scope.inputMember.incumbent = false;
                $scope.inputMember.districtCode = null;
            }
            console.log($scope.inputMember);
        };

        $scope.onPageChange = function (pageNum, contentType) {
            $scope.updateMembersList('(' + $scope.searchInput + '*)');
        };

        // watch for changes in searchInput with a 300ms buffer time
        var timeoutPromise;
        $scope.$watch("searchInput", function(){
            $timeout.cancel(timeoutPromise);  //does nothing, if timeout already done
            timeoutPromise = $timeout(function(){
                $scope.pagination.reset();
                $scope.updateMembersList('(' + $scope.searchInput + '*)');
            },300);
        });


    // STEP TWO //
        var overwriteMember = function() {
            $scope.inputMember.memberId = $scope.member.memberId;
            $scope.inputMember.chamber = $scope.member.chamber;
        };

        var overwriteSessionMember = function() {
            $scope.inputMember.sessionMemberId = $scope.member.sessionMemberId;
            $scope.inputMember.shortName = $scope.member.shortName;
            $scope.inputMember.sessionYear = $scope.member.sessionYear;
            $scope.inputMember.alternate = false;
        };


        // $scope.member is dummy record
        // $scope.inputMember is the linking record
        $scope.submit = function() {
            if (!$scope.linking) { // create new
                if ($scope.member.chamber == 'ASSEMBLY') {
                    $scope.member.prefix = 'Assembly Member';
                } else if ($scope.member.chamber == 'SENATE') {
                    $scope.member.prefix = 'Senator';
                }
                $scope.member.firstName = $scope.inputMember.firstName;
                $scope.member.middleName = $scope.inputMember.middleName;
                $scope.member.lastName = $scope.inputMember.lastName;
                $scope.member.suffix = $scope.inputMember.suffix;
                $scope.member.fullName = $scope.inputMember.firstName;
                if ($scope.inputMember.middleName) {
                    $scope.member.fullName += ' ';
                    $scope.member.fullName += $scope.inputMember.middleName;
                }
                $scope.member.fullName += ' ';
                $scope.member.fullName += $scope.inputMember.lastName;
                $scope.member.email = $scope.inputMember.email;
                if ($scope.member.imgName == null) {
                    $scope.member.imgName = 'no_image.jpg';
                }
                $scope.member.incumbent = $scope.inputMember.incumbent;
                $scope.member.districtCode = $scope.inputMember.districtCode;
                $scope.member.verified = true;

                // update members call
                $scope.indexResp = MemberAPI.save([$scope.member], function() {
                    if ($scope.indexResp.success === true) {
                        $scope.toStepThree(true);
                    }
                }, function(resp) {
                    console.log(resp);
                    $scope.toStepThree(false);
                    $scope.showErrorMessage(resp);
                });

            } else { // link to existing
                if ($scope.member.chamber != $scope.inputMember.chamber) {
                    // scenario 1: same person but new member and session_member
                    console.log("scenario 1");
                    overwriteMember();
                    overwriteSessionMember();

                    // update members call
                    $scope.indexResp = MemberAPI.save([$scope.inputMember], function() {
                        if ($scope.indexResp.success === true) {
                            $scope.toStepThree(true);
                        }
                    }, function(resp) {
                        console.log(resp);
                        $scope.toStepThree(false);
                        $scope.showErrorMessage(resp);
                    });


                } else if ($scope.member.sessionYear in $scope.inputMember.sessionShortNameMap) {
                    // scenario 2: same person and member and existing session member(s), set alternates
                    console.log("scenario 2");
                    overwriteSessionMember();
                    var sendMembers = [];
                    sendMembers.push($scope.inputMember);

                    for (var sessionYear in $scope.inputMember.sessionShortNameMap) {
                        if ($scope.inputMember.sessionShortNameMap.hasOwnProperty(sessionYear)) {
                            if (sessionYear == $scope.inputMember.sessionYear) {
                                $scope.inputMember.sessionShortNameMap[sessionYear].forEach(function (entry) {
                                    var copyMember = angular.copy($scope.inputMember);
                                    copyMember.memberId = entry.memberId;
                                    copyMember.sessionMemberId = entry.sessionMemberId;
                                    copyMember.shortName = entry.shortName;
                                    copyMember.chamber = entry.chamber;
                                    copyMember.alternate = true;
                                    sendMembers.push(copyMember);
                                });
                            }
                        }
                    }
                    // update members call
                    $scope.indexResp = MemberAPI.save(sendMembers, function() {
                        if ($scope.indexResp.success === true) {
                            $scope.toStepThree(true);
                        }
                    }, function(resp) {
                        console.log(resp);
                        $scope.toStepThree(false);
                        $scope.showErrorMessage(resp);
                    });
                } else {
                    // scenario 3: same person and member but new session_member
                    console.log("scenario 3");
                    overwriteSessionMember();

                    // update members call
                    $scope.indexResp = MemberAPI.save([$scope.inputMember], function() {
                        if ($scope.indexResp.success === true) {
                            $scope.toStepThree(true);
                        }
                    }, function(resp) {
                        console.log(resp);
                        $scope.toStepThree(false);
                        $scope.showErrorMessage(resp);
                    });
                }
            }
        };

        $scope.back = function() {
            $scope.step = '1';
        };

        $scope.toStepThree = function(success) {
            if (success) {
                $scope.headerText = 'Member Verified';
                $scope.confirmationText = $scope.member.firstName + " " + $scope.member.lastName + " has been verified.";
            } else {
                $scope.headerText = 'Failed to Verify';
                $scope.confirmationText = "An error occured while trying to verify " + $scope.member.firstName + " " + $scope.member.lastName;
            }
            $scope.step = 3;
        };

        // show generic error message when passed in resp
        $scope.showErrorMessage = function(resp) {
            console.error(resp);
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Error ' + resp.status)
                    .textContent(resp.data.message)
                    .ariaLabel('Error Message')
                    .ok('Close')
            );
        };
    }]);