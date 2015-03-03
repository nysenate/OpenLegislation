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

/** --- Transcript Controllers --- */

transcriptModule.controller('TranscriptListingCtrl', ['$scope', '$routeParams', 'SessionTranscriptListingApi', 'PublicHearingListingApi',
    function($scope, $routeParams, SessionTranscriptListingApi, PublicHearingListingApi) {
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Transcripts");

        $scope.init = function() {
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

        $scope.formatDate = function(dateTime) {
            return getDate(dateTime)
        };

        $scope.init();
    }
]);

transcriptModule.controller('SessionTranscriptViewCtrl', ['$scope', '$routeParams', '$location', 'SessionTranscriptDetailsApi',
    function($scope, $routeParams, $location, SessionTranscriptDetailsApi) {
        $scope.setHeaderVisible(true);
        $scope.init = function() {
            if (!$scope.transcriptDetailViewResponse || !$scope.transcriptDetailViewResponse.success) {
                $scope.transcriptDetailViewResponse = SessionTranscriptDetailsApi.get({filename: $routeParams.filename}, function () {
                    $scope.sessionTranscriptDetails = $scope.transcriptDetailViewResponse.result;
                    $scope.setHeaderText("Session Transcript: " + getDate($scope.sessionTranscriptDetails.dateTime));
                });
            }
        };
        $scope.init();

        $scope.back = function() {
            $location.path(ctxPath + '/transcripts');
        };
    }
]);

transcriptModule.controller('HearingTranscriptViewCtrl', ['$scope', '$routeParams', '$location', 'PublicHearingDetailsApi',
    function($scope, $routeParams, $location, PublicHearingDetailsApi) {
        $scope.setHeaderVisible(true);
        $scope.init = function() {
            if (!$scope.hearingDetailViewResponse || !$scope.hearingDetailViewResponse.success) {
                $scope.hearingDetailViewResponse = PublicHearingDetailsApi.get({filename: $routeParams.filename}, function () {
                    $scope.hearingDetails = $scope.hearingDetailViewResponse.result;
                    $scope.setHeaderText("Public Hearing Transcript: " + getDate($scope.hearingDetails.date));
                });
            }
        };
        $scope.init();

        $scope.back = function() {
            $location.path(ctxPath + '/transcripts');
        };
    }
]);

/** --- Javascript functions --- */

var getDate = function(dateTime) {
    var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "June",
        "July", "Aug", "Sep", "Oct", "Nov", "Dec" ];
    var date = new Date(dateTime);
    return monthNames[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
};









