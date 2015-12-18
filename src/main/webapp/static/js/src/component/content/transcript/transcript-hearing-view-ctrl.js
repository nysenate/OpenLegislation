var transcriptModule = angular.module('open.transcript');

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
