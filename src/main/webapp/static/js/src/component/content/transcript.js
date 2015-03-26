var transcriptModule = angular.module('open.transcript', ['open.core']);

transcriptModule.factory('SessionTranscriptListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts?summary=true&limit=15", {
    });
}]);

transcriptModule.factory('SessionTranscriptDetailsApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts/:filename", {
        filename: '@filename'
    });
}]);

transcriptModule.factory('PublicHearingListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/hearings?full=true&limit=15", {
    });
}]);

transcriptModule.factory('PublicHearingDetailsApi', ['$resource', function($resource) {
    return $resource(apiPath + "/hearings/:filename", {
        filename: '@filename'
    });
}]);

transcriptModule.factory('TranscriptSearchApi', ['$resource', function ($resource) {
    return $resource(apiPath + "/transcripts/search?term=:term&full=true&limit=:limit&offset=:offset", {
        term: '@term',
        limit: '@limit',
        offset: '@offset'
    })
}]);

transcriptModule.factory('PublicHearingSearchApi', ['$resource', function ($resource) {
    return $resource(apiPath + "/hearings/search?term=:term&full=true&limit=:limit&offset=:offset", {
        term: '@term',
        limit: '@limit',
        offset: '@offset'
    })
}]);

/** --- Transcript Controllers --- */

transcriptModule.controller('TranscriptListingCtrl', ['$scope', '$routeParams', '$sce', '$location', 'PaginationModel',
    'SessionTranscriptListingApi', 'PublicHearingListingApi', 'TranscriptSearchApi', 'PublicHearingSearchApi',
    function($scope, $routeParams, $sce, $location, PaginationModel, SessionTranscriptListingApi, PublicHearingListingApi,
             TranscriptSearchApi, PublicHearingSearchApi) {

        /* --- Session Transcript Search --- */

        $scope.transcriptSearch = {
            term: "",
            response: {},
            matches: [],
            error: false,
            paginate: angular.extend({}, PaginationModel)
        };

        $scope.searchTranscripts = function(resetPagination) {
            if (resetPagination) {
                $scope.transcriptSearch.paginate.reset();
                $scope.currentPage.sessionSearchPage = 1;
                $scope.setSearchPage($scope.currentPage.sessionSearchPage);
            }
            $scope.transcriptSearch.response = TranscriptSearchApi.get({
                    term: $scope.transcriptSearch.term,
                    limit: $scope.transcriptSearch.paginate.getLimit(),
                    offset: $scope.transcriptSearch.paginate.getOffset()},
                function () {
                    if ($scope.transcriptSearch.response && $scope.transcriptSearch.response.success) {
                        $scope.transcriptSearch.error = false;
                        $scope.transcriptSearch.matches = $scope.transcriptSearch.response.result.items || [];
                        $scope.transcriptSearch.paginate.setTotalItems($scope.transcriptSearch.response.total);

                        angular.forEach($scope.transcriptSearch.matches, function(match) {
                           for (field in match.highlights) {
                               for (fragment in match.highlights[field]) {
                                   if (match.highlights[field][fragment]) {
                                       match.highlights[field][fragment] = $sce.trustAsHtml(match.highlights[field][fragment]);
                                   }
                               }
                           }
                        });
                    }
                    else {
                        $scope.transcriptSearch.error = true;
                        $scope.transcriptSearch.matches = [];
                        $scope.transcriptSearch.paginate.setTotalItems($scope.transcriptSearch.response.total);
                    }
                })
        };

        /* --- Public Hearing Transcript Search --- */

        $scope.hearingSearch = {
            term: "",
            response: {},
            matches: [],
            error: false,
            paginate: angular.extend({}, PaginationModel)
        };

        $scope.searchHearings = function(resetPagination) {
            if (resetPagination) {
                $scope.hearingSearch.paginate.reset();
                $scope.currentPage.hearingSearchPage = 1;
                $scope.setSearchPage($scope.currentPage.hearingSearchPage);
            }
            $scope.hearingSearch.response = PublicHearingSearchApi.get({
                    term: $scope.hearingSearch.term,
                    limit: $scope.hearingSearch.paginate.getLimit(),
                    offset: $scope.hearingSearch.paginate.getOffset()
                },
                function () {
                    if ($scope.hearingSearch.response && $scope.hearingSearch.response.success) {
                        $scope.hearingSearch.error = false;
                        $scope.hearingSearch.matches = $scope.hearingSearch.response.result.items || [];
                        $scope.hearingSearch.paginate.setTotalItems($scope.hearingSearch.response.total);

                        angular.forEach($scope.hearingSearch.matches, function(match) {
                            for (field in match.highlights) {
                                for (fragment in match.highlights[field]) {
                                    if (match.highlights[field][fragment]) {
                                        match.highlights[field][fragment] = $sce.trustAsHtml(match.highlights[field][fragment]);
                                    }
                                }
                            }
                        });
                    }
                    else {
                        $scope.hearingSearch.error = true;
                        $scope.hearingSearch.matches = [];
                        $scope.hearingSearch.paginate.setTotalItems($scope.hearingSearch.response.total);
                    }
                })
        };

        /* --- Url Parameters --- */

        var views = ["browse", "search"];
        var categories = ["session", "hearing"];

        $scope.currentPage = {
            viewIndex: parseInt($routeParams.view, 10) || 0,
            categoryIndex: parseInt($routeParams.category, 10) || 0,
            sessionSearchPage: 1, // Currently viewed search page for session search.
            hearingSearchPage: 1 // Currently viewed search page for hearing search.
        };

        $scope.changePage = function(newPageNumber) {
            if ($location.search().category == "session") {
                $scope.currentPage.sessionSearchPage = newPageNumber;
            }
            else if ($location.search().category == "hearing") {
                $scope.currentPage.hearingSearchPage = newPageNumber;
            }
            $scope.setSearchPage(newPageNumber);
        };

        // Manually reload page when back button used.
        // Updates search results when paginating through results.
        $scope.$on('$locationChangeSuccess', function() {
            $scope.currentPage.viewIndex = views.indexOf($location.search().view || 0);
            $scope.currentPage.categoryIndex = categories.indexOf($location.search().category || 0);

            if (viewingSearchTab()) {
                if (viewingSessionTab()) {
                    $scope.currentPage.sessionSearchPage = $location.search().searchPage;
                    $scope.transcriptSearch.paginate.currPage = $scope.currentPage.sessionSearchPage;
                    $scope.searchTranscripts(false);
                }
                else if (viewingHearingTab()) {
                    $scope.currentPage.hearingSearchPage = $location.search().searchPage;
                    $scope.hearingSearch.paginate.currPage = $scope.currentPage.hearingSearchPage;
                    $scope.searchHearings(false);
                }
            }
            else {
                $location.search('searchPage', null);
            }
        });

        /* --- Update url params when switching views. --- */
        $scope.$watch('currentPage.viewIndex', function() {
            $location.search('view', viewIndexToString());
            if (viewIndexToString() != "search") {
                $location.search('searchPage', null);
            }
            else if (viewIndexToString() == "search") {
                $scope.updateSearchTabParams(viewIndexToString());
            }
        });

        /* --- Update url params when switching categories. --- */
        $scope.$watch('currentPage.categoryIndex', function() {
            $location.search('category', categories[$scope.currentPage.categoryIndex]);
            $scope.updateSearchTabParams(viewIndexToString());
        });

        $scope.setSearchPage = function(pageNum) {
          $location.search('searchPage', pageNum);
        };

        /* --- Internal Methods --- */

        // Update the searchPage url parameter depending on the page being viewed.
        $scope.updateSearchTabParams = function(category) {
            if (category == "search") {
                if (categories[$scope.currentPage.categoryIndex] == "session") {
                    $location.search('searchPage', $scope.currentPage.sessionSearchPage);
                }
                else {
                    $location.search('searchPage', $scope.currentPage.hearingSearchPage);
                }
            }
        };

        var viewingSearchTab = function () {
            return $scope.currentPage.viewIndex == views.indexOf("search");
        };

        var viewingSessionTab = function () {
            return $scope.currentPage.categoryIndex == categories.indexOf("session");
        };

        var viewingHearingTab = function () {
            return $scope.currentPage.categoryIndex == categories.indexOf("hearing");
        };

        var viewIndexToString = function () {
            return views[$scope.currentPage.viewIndex];
        };

        var categoryParamIndex = function () {
            return categories.indexOf($location.search().category);
        };

        var viewParamIndex = function () {
            return views.indexOf($location.search().view);
        };

        /* --- Initialization --- */

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Transcripts");
            $scope.currentPage.viewIndex =  viewParamIndex();
            $scope.currentPage.categoryIndex = categoryParamIndex();

            if (!$scope.transcriptListingResponse || !$scope.transcriptListingResponse.success) {
                $scope.transcriptListingResponse = SessionTranscriptListingApi.get({}, function () {
                    $scope.transcriptListing = $scope.transcriptListingResponse.result.items;
                })
            }
            if (!$scope.publicHearingListingResponse || !$scope.publicHearingListingResponse.success) {
                $scope.publicHearingListingResponse = PublicHearingListingApi.get({}, function () {
                    $scope.publicHearingListing = $scope.publicHearingListingResponse.result.items;
                })
            }
            $scope.searchTranscripts(false);
        };
        $scope.init();
    }
]);

transcriptModule.controller('SessionTranscriptViewCtrl', ['$scope', '$routeParams', '$location', '$filter', 'SessionTranscriptDetailsApi',
    function($scope, $routeParams, $location, $filter, SessionTranscriptDetailsApi) {
        $scope.setHeaderVisible(true);
        $scope.init = function() {
            if (!$scope.transcriptDetailViewResponse || !$scope.transcriptDetailViewResponse.success) {
                $scope.transcriptDetailViewResponse = SessionTranscriptDetailsApi.get({filename: $routeParams.filename}, function () {
                    $scope.sessionTranscriptDetails = $scope.transcriptDetailViewResponse.result;
                    $scope.setHeaderText("Session Transcript: " + $filter('date')($scope.sessionTranscriptDetails.dateTime, 'mediumDate'));
                });
            }
        };
        $scope.init();

        $scope.back = function() {
            $location.path(ctxPath + '/transcripts');
        };
    }
]);

transcriptModule.controller('HearingTranscriptViewCtrl', ['$scope', '$routeParams', '$location', '$filter', 'PublicHearingDetailsApi',
    function($scope, $routeParams, $location, $filter, PublicHearingDetailsApi) {
        $scope.setHeaderVisible(true);
        $scope.init = function() {
            if (!$scope.hearingDetailViewResponse || !$scope.hearingDetailViewResponse.success) {
                $scope.hearingDetailViewResponse = PublicHearingDetailsApi.get({filename: $routeParams.filename}, function () {
                    $scope.hearingDetails = $scope.hearingDetailViewResponse.result;
                    $scope.setHeaderText("Public Hearing Transcript: " + $filter('date')($scope.hearingDetails.date, 'mediumDate'));
                });
            }
        };
        $scope.init();

        $scope.back = function() {
            $location.path(ctxPath + '/transcripts');
        };
    }
]);
