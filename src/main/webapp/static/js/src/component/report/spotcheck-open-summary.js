angular.module('open.spotcheck')
    .controller('SpotcheckOpenSummaryCtrl',
        ['$scope', '$filter', 'SpotcheckOpenMismatchSummaryAPI',  openSummaryCtrl]);

function openSummaryCtrl($scope, $filter, openSummaryApi) {
    $scope.summaries = {};

    $scope.reportTypes = ['LBDC_SCRAPED_BILL', 'LBDC_CALENDAR_ALERT', 'LBDC_AGENDA_ALERT'];

    $scope.init = function () {
        $scope.setHeaderText('Summary of Open Mismatches');
        getSummaries();
    };

    function getSummaries() {
        $scope.loadingSummaries = true;
        var params = {
            reportType: $scope.reportTypes
        };
        openSummaryApi.get(params, function (response) {
            $scope.summaries = response.result.summaryMap;
            $scope.loadingSummaries = false;
        }, function (errorResponse) {
            $scope.loadingSummaries = false;
            $scope.summaryRequestError = true;
            console.log('Err0r', errorResponse);
        })
    }
}