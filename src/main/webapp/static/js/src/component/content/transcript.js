var transcriptModule = angular.module('open.transcript', ['open.core']);

transcriptModule.factory('SessionTranscriptListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts?summary=true", {
    });
}]);

transcriptModule.factory('SessionTranscriptDetailsApi', ['$resource', function($resource) {
    return $resource(apiPath + "/transcripts/:filename", {
        filename: '@filename'
    });
}]);

transcriptModule.factory('PublicHearingListingApi', ['$resource', function($resource) {
    return $resource(apiPath + "/hearings?full=true", {
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
    return $resource(apiPath + "/hearings/search?term=:term&full=true", {term: '@term'})
}]);

/** --- Transcript Controllers --- */

transcriptModule.controller('TranscriptListingCtrl', ['$scope', '$routeParams', '$sce', 'SessionTranscriptListingApi',
    'PublicHearingListingApi', 'TranscriptSearchApi', 'PublicHearingSearchApi',
    function($scope, $routeParams, $sce, SessionTranscriptListingApi, PublicHearingListingApi,
             TranscriptSearchApi, PublicHearingSearchApi) {
        $scope.init = function() {
            $scope.setHeaderVisible(true);
            $scope.setHeaderText("Transcripts");
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
        };
        $scope.init();

        $scope.transcriptSearch = {
            term: '',
            response: {},
            matches: [],
            error: false
        };

        $scope.searchTranscripts = function() {
            $scope.transcriptSearch.response = TranscriptSearchApi.get({term: $scope.transcriptSearch.term},
                function () {
                    if ($scope.transcriptSearch.response && $scope.transcriptSearch.response.success) {
                        $scope.transcriptSearch.error = false;
                        $scope.transcriptSearch.matches = $scope.transcriptSearch.response.result.items || [];

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
                    }
                })
        };

        $scope.hearingSearch = {
            term: '',
            response: {},
            matches: [],
            error: false
        };

        $scope.searchHearings = function() {
            $scope.hearingSearch.response = PublicHearingSearchApi.get({term: $scope.hearingSearch.term},
                function () {
                    if ($scope.hearingSearch.response && $scope.hearingSearch.response.success) {
                        $scope.hearingSearch.error = false;
                        $scope.hearingSearch.matches = $scope.hearingSearch.response.result.items || [];

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
                    }
                })
        };
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
