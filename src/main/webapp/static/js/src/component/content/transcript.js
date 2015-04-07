var transcriptModule = angular.module('open.transcript', ['open.core']);

transcriptModule.factory('SessionListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts/:year?summary=true&limit=:limit&offset=:offset&sort=:sort", {
        year: '@year',
        limit: '@limit',
        offset: '@offset',
        sort: 'dateTime:desc'
    });
}]);

transcriptModule.factory('SessionTranscriptDetailsApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts/:filename", {
        filename: '@filename'
    });
}]);

transcriptModule.factory('PublicHearingListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/hearings/:year?full=true&limit=:limit&offset=:offset&sort=:soft", {
        year: '@year',
        limit: '@limit',
        offset: '@offset',
        sort: 'date:desc'
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

transcriptModule.controller('TranscriptCtrl', ['$scope', '$routeParams', function($scope, $routeParams) {

    $scope.view = 0;

    $scope.init = function() {
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Transcripts");
    };

    $scope.init();
}]);

transcriptModule.controller('TranscriptBrowseCtrl', ['$scope', '$routeParams', '$location', 'PaginationModel',
    'SessionListingApi', 'PublicHearingListingApi',
    function($scope, $routeParams, $location, PaginationModel, SessionListingApi, PublicHearingListingApi) {

        $scope.years = [2015, 2014, 2013, 2012, 2011, 2010, 2009, 2008, 2007, 2006, 2005, 2004, 2003,
            2002, 2001, 2000, 1999, 1998, 1997, 1996, 1995, 1994, 1993];

        $scope.selectedYear = $scope.years[0];

        $scope.checkbox = 1; // 1 = session selected, 2 = hearing selected

        $scope.hearing = {
            results: [],
            paginate: angular.extend({}, PaginationModel)
        };

        $scope.session = {
            results: [],
            paginate: angular.extend({}, PaginationModel)
        };

        $scope.filterResults = function() {
            if (sessionIsSelected()) {
                $scope.fetchSessions();
            }
            if (hearingIsSelected()) {
                $scope.fetchHearings();
            }
        };

        $scope.fetchSessions = function() {
            $scope.sessionResponse = SessionListingApi.get(
                {
                    year: $scope.selectedYear,
                    limit: $scope.session.paginate.getLimit(),
                    offset: $scope.session.paginate.getOffset()
                },
                function() {
                    if ($scope.sessionResponse && $scope.sessionResponse.success) {
                        $scope.session.results = $scope.sessionResponse.result.items || [];
                        $scope.session.paginate.setTotalItems($scope.sessionResponse.total);
                    }
                    else {
                        $scope.session.results = [];
                    }
                }
            )
        };

        $scope.fetchHearings = function() {
            $scope.hearingResponse = PublicHearingListingApi.get(
                {
                    year: $scope.selectedYear,
                    limit: $scope.hearing.paginate.getLimit(),
                    offset: $scope.hearing.paginate.getOffset()
                },
                function() {
                    if ($scope.hearingResponse && $scope.hearingResponse.success) {
                        $scope.hearing.results = $scope.hearingResponse.result.items || [];
                        $scope.hearing.paginate.setTotalItems($scope.hearingResponse.total);
                    }
                    else {
                        $scope.hearing.results = [];
                    }
                }
            )
        };

        $scope.changePage = function(newPageNumber) {
            if (sessionIsSelected()) {
                $scope.session.paginate.currPage = newPageNumber;
            }
            if (hearingIsSelected()) {
                $scope.hearing.paginate.currPage = newPageNumber
            }
            $scope.filterResults();
        };

        $scope.$watch('selectedYear', function() {
            $scope.filterResults();
        });

        $scope.init = function() {
            $scope.session.paginate.itemsPerPage = 20;
            $scope.hearing.paginate.itemsPerPage = 20;

            $scope.fetchSessions();
        };

        $scope.init();

        var sessionIsSelected = function() {
            return $scope.checkbox == 1;
        };

        var hearingIsSelected = function() {
            return $scope.checkbox == 2;
        };
}]);

transcriptModule.controller('TranscriptSearchCtrl', ['$scope', '$routeParams', '$sce', '$location', 'PaginationModel',
    'TranscriptSearchApi', 'PublicHearingSearchApi',
    function($scope, $routeParams, $sce, $location, PaginationModel,
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
            $scope.test = "Start Search";
            if (resetPagination) {
                $scope.transcriptSearch.paginate.reset();
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

        /* --- Initialization --- */

        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Transcripts");
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
