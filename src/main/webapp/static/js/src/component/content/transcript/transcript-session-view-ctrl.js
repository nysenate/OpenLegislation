var transcriptModule = angular.module('open.transcript');

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