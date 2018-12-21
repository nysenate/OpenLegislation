var adminModule = angular.module('open.admin');

adminModule.factory('MemberSearchAPI', ['$resource', function ($resource) {
    return $resource(window.ctxPath + '/api/3/members/search');
}]);

adminModule.controller('MembersCtrl', ['$scope', '$timeout', '$mdDialog', '$routeParams', '$location', 'MemberSearchAPI', 'PaginationModel',
                        function($scope, $timeout, $mdDialog, $routeParams, $location, MemberSearchAPI, PaginationModel) {

    $scope.unverifiedOnly = $routeParams.unverifiedOnly !== "false";
    $scope.membersList = [];
    $scope.loadingMembers = false;
    $scope.pagination = angular.extend({}, PaginationModel);
    $scope.pagination.itemsPerPage = 12;
    $scope.termParam = '';
    $scope.searchInput = '';

    $scope.toggleVerified = function() {
        $scope.pagination.reset();
        $scope.unverifiedOnly = !$scope.unverifiedOnly;
        $scope.updateMembersList(($scope.unverifiedOnly ? '(verified:false)' : '(*)') + 'AND' + '(' + $scope.searchInput + '*)');
        $location.search("unverifiedOnly", $scope.unverifiedOnly);
    };

    $scope.init = function() {
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Manage Members");
        $scope.updateMembersList('*');
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

    $scope.updateMembersList = function(termParam) {
        $scope.loadingMembers = true;
        var params = {
            term: termParam,
            sort: 'sessionYear:desc,shortName:asc',
            limit: $scope.pagination.getLimit(),
            offset: $scope.pagination.getOffset(),
            full: true
        };
        MemberSearchAPI.get(params, function(resp) {
            if (resp.success === true) {
                $scope.membersList = resp.result.items;
                $scope.pagination.setTotalItems(resp.total);
            }
        }, $scope.showErrorMessage).$promise.finally(function(){
            $scope.loadingMembers = false;
        });
    };

    $scope.onPageChange = function (pageNum, contentType) {
        $scope.updateMembersList(($scope.unverifiedOnly ? '(verified:false)' : '(*)') + 'AND' + '(' + $scope.searchInput + '*)');
    };

    var timeoutPromise;
    $scope.$watch('searchInput', function(){
        $timeout.cancel(timeoutPromise);  //does nothing, if timeout alrdy done
        timeoutPromise = $timeout(function(){   //Set timeout
            $scope.pagination.reset();
            $scope.updateMembersList(($scope.unverifiedOnly ? '(verified:false)' : '(*)') + 'AND' + '(' + $scope.searchInput + '*)');
        },300);
    });


}]);