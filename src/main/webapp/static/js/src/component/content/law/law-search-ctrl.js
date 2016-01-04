var lawModule = angular.module('open.law');

/**
 * Law Search Ctrl
 */
lawModule.controller('LawSearchCtrl', ['$scope', '$location', '$routeParams', 'LawListingApi', 'LawFullSearchApi', 'PaginationModel', 'safeHighlights',
    function($scope, $location, $routeParams, LawListingApi, LawFullSearchApi, PaginationModel, safeHighlights) {

        $scope.lawListingResponse = null;
        $scope.lawListing = null;
        $scope.pagination = angular.extend({}, PaginationModel);

        $scope.listingLimit = 20;

        $scope.lawSearch = {
            term: $routeParams.search || '',
            searching: false,
            searched: false,
            response: null,
            error: null,
            results: []
        };

        function tabInit() {
            $scope.setHeaderVisible(true);
        }

        $scope.$on('viewChange', function() {
            tabInit();
        });

        $scope.init = function() {
            tabInit();
            $scope.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
            $scope.simpleSearch();
            $scope.getListings();
        };

        $scope.getListings = function() {
            if (!$scope.lawListingResponse || !$scope.lawListingResponse.success) {
                $scope.lawListingResponse = LawListingApi.get({}, function() {
                    $scope.lawListing = $scope.lawListingResponse.result.items;
                });
            }
        };

        $scope.keepScrolling = function() {
            $scope.listingLimit += 10;
        };

        $scope.simpleSearch = function(resetPagination) {
            var term = $scope.lawSearch.term;
            $scope.setSearchParam('search', term);
            if (term) {
                if (resetPagination) {
                    $scope.pagination.reset();
                }
                $scope.lawSearch.searched = false;
                $scope.lawSearch.searching = true;
                $scope.lawSearch.response = LawFullSearchApi.get(
                    {term: term, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()},
                    function() {
                        if ($scope.lawSearch.response.result) {
                            $scope.lawSearch.results = $scope.lawSearch.response.result.items;
                            safeHighlights($scope.lawSearch.results);
                        }
                        $scope.pagination.setTotalItems($scope.lawSearch.response.total);
                        $scope.lawSearch.searched = true;
                        $scope.lawSearch.searching = false;
                        $scope.lawSearch.error = false;
                    },
                    function(error) {
                        $scope.pagination.setTotalItems(0);
                        $scope.lawSearch.error = error.data;
                        $scope.lawSearch.searched = true;
                        $scope.lawSearch.searching = false;
                    });
            }
            else {
                $scope.pagination.setTotalItems(0);
                $scope.lawSearch.searched = true;
            }
        };

        /** Watch for changes to the current search page. */
        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.setSearchParam('searchPage', newPage);
                $scope.simpleSearch();
            }
        });

        $scope.init();
    }]);

