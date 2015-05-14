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

memberModule.controller('MemberCtrl', ['$scope', '$routeParams', '$location',
    function($scope, $routeParams, $location) {
        $scope.selectedView =  (parseInt($routeParams.view, 10) || 0);

        $scope.$watch('selectedView', function() {
            $location.search('view', $scope.selectedView);
        });

        $scope.$on('$locationChangeSuccess', function() {
            $scope.selectedView = $location.search().view;
        });

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Search Members");
        };

        $scope.init();

    }]);

memberModule.controller('MemberSearchCtrl', ['$scope', '$routeParams', '$location', 'MemberSearchApi', 'PaginationModel',
    function($scope, $routeParams, $location, MemberSearchApi, PaginationModel) {

        $scope.memberSearch = {
            term: "",
            matches: [],
            response: {},
            paginate: angular.extend({}, PaginationModel),
            error: false
        };

        $scope.searchMembers = function(resetPagination) {
            if (resetPagination) {
                $scope.memberSearch.paginate.reset();
                $scope.memberSearch.paginate.itemsPerPage = 10;
                $location.search('term', $scope.memberSearch.term);
                $location.search('searchPage', $scope.memberSearch.paginate.currPage);
            }
            $scope.memberSearch.response = MemberSearchApi.get(
                {
                    term: addDefaultSessionYearToSearchTerm(),
                    limit: $scope.memberSearch.paginate.getLimit(),
                    offset: $scope.memberSearch.paginate.getOffset()
                },
                function () {
                    if ($scope.memberSearch.response && $scope.memberSearch.response.success) {
                        $scope.memberSearch.error = false;
                        $scope.memberSearch.matches = $scope.memberSearch.response.result.items || [];
                        $scope.memberSearch.paginate.setTotalItems($scope.memberSearch.response.total);
                    }
                    else {
                        $scope.memberSearch.error = true;
                        $scope.memberSearch.matches = [];
                    }
                })
        };

        /* Add the current session year to the search term if none was specified. */
        var addDefaultSessionYearToSearchTerm = function() {
            if ($scope.memberSearch.term.indexOf("sessionYear") == -1) {
                var currentSessionYear = new Date().getFullYear();
                return $scope.memberSearch.term + " sessionYear:" + currentSessionYear;
            }
            else {
                return $scope.memberSearch.term;
            }
        };

        $scope.changePage = function(newPageNumber) {
            $scope.memberSearch.paginate.currPage = newPageNumber;
            $location.search('searchPage', $scope.memberSearch.paginate.currPage);
            $scope.searchMembers(false);
        };

        $scope.$on('$locationChangeSuccess', function() {
            $scope.memberSearch.term = $location.search().term;
            $scope.memberSearch.paginate.currPage = $location.search().searchPage;
            if ($scope.memberSearch.term) {
                $scope.searchMembers(false);
            }
        });

    }]);

memberModule.controller('MemberBrowseCtrl', ['$scope', '$routeParams', 'MemberSearchApi',
    function($scope, $routeParams, MemberSearchApi) {

        $scope.sessionYears = [2015, 2013, 2011, 2009];
        $scope.memberBrowse = {
            response: {},
            results: [],
            filter: "",
            chamberSelected: 'SENATE',
            sessionYear: 2015
        };

        $scope.filterMembers = function() {
            $scope.memberBrowse.response = MemberSearchApi.get(
                {
                    term: $scope.buildQuery(),
                    limit: 999,
                    offset: 0
                },
                function () {
                    if ($scope.memberBrowse.response && $scope.memberBrowse.response.success) {
                        $scope.memberBrowse.results = $scope.memberBrowse.response.result.items || [];
                    }
                })
        };

        $scope.buildQuery = function() {
            return "chamber:" + $scope.memberBrowse.chamberSelected + " AND sessionYear:" + $scope.memberBrowse.sessionYear;
        };

        $scope.$watch('memberBrowse.sessionYear', function() {
            $scope.filterMembers();
        });

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Browse Members");
            $scope.filterMembers();
        };

        $scope.init();

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


memberModule.filter('capitalize', function() {
    return function(term) {
        if (term) {
            return term.charAt(0).toUpperCase() + term.slice(1).toLowerCase();
        }
    };
});
