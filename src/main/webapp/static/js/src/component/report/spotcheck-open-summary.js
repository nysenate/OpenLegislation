angular.module('open.spotcheck')
    .controller('SpotcheckOpenSummaryCtrl',
        ['$scope', '$filter', 'SpotcheckOpenMismatchSummaryAPI',  openSummaryCtrl]);

function openSummaryCtrl($scope, $filter, openSummaryApi) {
    $scope.summaries = {};

    $scope.init = function () {
        $scope.setHeaderText('Summary of Open Mismatches');
        getSummaries();
    };

    function getSummaries() {
        $scope.loadingSummaries = true;
        openSummaryApi.get({}, function (response) {
            $scope.summaries = response.result.summaryMap;
            $scope.loadingSummaries = false;
        }, function (errorResponse) {
            $scope.loadingSummaries = false;
            $scope.summaryRequestError = true;
        })
    }
}