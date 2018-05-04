var adminModule = angular.module('open.admin');

adminModule.factory('MemberSearchAPI', ['$resource', function ($resource) {
    return $resource(window.ctxPath + '/api/3/members/search');
}]);

adminModule.controller('MemberCtrl', ['$scope', '$timeout', '$routeParams', '$mdDialog', 'MemberSearchAPI',
    function($scope, $timeout,$routeParams, $mdDialog, MemberSearchAPI) {

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Member Details");
            var params = {
                term: '(memberId:' + $routeParams.memberId + ')',
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
                    $scope.headerText = $scope.member.firstName + " " + $scope.member.lastName;
                    console.log($scope.member);
                }
            }, $scope.showErrorMessage);
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