var memberModule = angular.module('open.member', ['open.core']);

memberModule.factory('MemberViewApi', ['$resource', function($resource) {
    return $resource(apiPath + "/members/:sessionYear/:memberId", {
        sessionYear: '@sessionYear',
        memberId: '@memberId'
    });
}]);

memberModule.factory('MemberSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + "/members/search?term=:term&full=true&limit=:limit&offset=:offset", {
        term: '@term',
        limit: '@limit',
        offset: '@offset'
    })
}]);

memberModule.controller('MemberSearchCtrl', ['$scope', '$routeParams', '$location', 'MemberSearchApi', 'PaginationModel',
    function($scope, $routeParams, $location, MemberSearchApi, PaginationModel) {

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Members");
        };

        $scope.init();

        $scope.memberSearch = {
            term: "",
            matches: [],
            response: {},
            paginate: angular.extend({}, PaginationModel),
            doneLoadingResults: false,
            error: false
        };

        $scope.searchMembers = function(resetPagination) {
            $scope.memberSearch.doneLoadingResults = false;
            if (resetPagination) {
                $scope.memberSearch.paginate.reset();
            }
            $scope.memberSearch.response = MemberSearchApi.get(
                {
                    term: $scope.memberSearch.term,
                    limit: $scope.memberSearch.paginate.getLimit(),
                    offset: $scope.memberSearch.paginate.getOffset()
                },
                function () {
                    if ($scope.memberSearch.response && $scope.memberSearch.response.success) {
                        $scope.memberSearch.error = false;
                        $scope.memberSearch.matches = $scope.memberSearch.response.result.items || [];
                        $scope.memberSearch.paginate.setTotalItems($scope.memberSearch.response.total);
                        $scope.memberSearch.doneLoadingResults = true;
                    }
                    else {
                        $scope.memberSearch.error = true;
                        $scope.memberSearch.matches = [];
                        $scope.memberSearch.paginate.setTotalItems($scope.memberSearch.response.total);
                    }
                })
        };

        $scope.changePage = function(newPageNumber) {
            $scope.memberSearch.paginate.currPage = newPageNumber;
            $scope.searchMembers(false);
        };

        $scope.$on('$locationChangeSuccess', function() {
            $scope.memberSearch.term = $location.search().term;
            $scope.memberSearch.paginate.currPage = $location.search().searchPage;
            if ($scope.memberSearch.doneLoadingResults) {
                $scope.searchMembers(false);
            }
        });

        $scope.$watch('memberSearch.term', function() {
           $location.search('term', $scope.memberSearch.term);
        });

        $scope.$watch('memberSearch.paginate.currPage', function() {
            $location.search('searchPage', $scope.memberSearch.paginate.currPage);
        });

    }]);


memberModule.controller('MemberViewCtrl', ['$scope', '$routeParams', 'MemberViewApi',
    function($scope, $routeParams, MemberViewApi) {

        $scope.memberView = {
            response: {},
            result: {}
        };

        $scope.init = function() {
            $scope.setHeaderVisible(true);

            if (!$scope.memberView.response || !$scope.memberView.response.success) {
                $scope.memberView.response = MemberViewApi.get({sessionYear: $routeParams.sessionYear,
                memberId: $routeParams.memberId}, function() {
                    $scope.memberView.result = $scope.memberView.response.result;
                    $scope.setHeaderText($scope.memberView.result.fullName);
                })
            }
        };

        $scope.init();
}]);
