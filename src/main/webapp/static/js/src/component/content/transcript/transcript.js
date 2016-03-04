var transcriptModule = angular.module('open.transcript', ['open.core', 'open.api']);

transcriptModule.controller('TranscriptCtrl', ['$scope', '$routeParams', function($scope, $routeParams) {
    $scope.view = 0;
    $scope.init = function() {
        $scope.setHeaderVisible(true);
        $scope.setHeaderText("Transcripts");
    };
    $scope.init();
}]);
