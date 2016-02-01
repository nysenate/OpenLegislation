
/** --- Open Mismatches Controller --- */

angular.module('open.spotcheck')
    .controller('SpotcheckMismatchCtrl', ['$scope', '$filter', '$routeParams', '$rootScope',
        'SpotcheckOpenMismatchAPI', 'SpotcheckOpenMismatchSummaryAPI', 'SpotcheckDefaultFilter',
function ($scope, $filter, $routeParams, $rootScope, OpenMMAPI, OpenMMSummaryAPI, defaultFilter) {

    $scope.unselectedReportType = "unselected";

    $scope.mismatchRows = [];
    $scope.summary = null;

    $scope.filter = angular.copy(defaultFilter);

    $scope.requestCount = 0;
    $scope.lastReceived = 0;

    $scope.init = function() {
        $scope.reportType = $routeParams.reportType || $scope.unselectedReportType;
        $scope.getOpenMismatches();
    };

    $rootScope.$on('mismatchFilterChange', function() {
        console.log('mismatch filter change detected', $scope.filter);
        $scope.getOpenMismatches();
    });

    function getOpenMismatchParams() {
        var params = angular.merge({}, $scope.filter);
        params.reportType = $scope.reportType;
        params.mismatchType = [];
        angular.forEach(params.types, function (requested, type) {
            if (requested) {
                params.mismatchType.push(type);
            }
        });
        delete params.statuses;
        delete params.types;
        delete params.passes;
        delete params.filterFunction;
        return params;
    }

    $scope.getOpenMismatches = function() {
        $scope.parameterError = $scope.requestError = false;
        if ($scope.reportType !== "unselected") {
            var params = getOpenMismatchParams();
            var reqId = ++$scope.requestCount;
            console.log('getting open mismatches', params);
            OpenMMAPI.get(params, function (response) {
                    // Do not extract data if the received request was made before the latest received
                    if (reqId > $scope.lastReceived) {
                        var summaries = response.summary.summaryMap;
                        if ($scope.reportType in summaries) {
                            $scope.summary = summaries[$scope.reportType];
                        }
                        $scope.mismatchRows = $scope.extractMismatchRows(response.observations, response.query.refTypes[0]);
                        $scope.lastReceived = reqId;
                        console.log('got it');
                    }
                }, function (response) {
                    if (reqId > $scope.lastReceived) {
                        console.log('uh oh:', response);
                        $scope.tableData = [];
                        $scope.lastReceived = reqId;
                        if (response.status === 400 && response.data.errorCode === 1) {
                            $scope.parameterError = true;
                            $scope.parameterErrorVal = response.data.errorData.parameterConstraint.name;
                            $scope.invalidParamDialog(response);
                        } else {
                            $scope.requestError = true;
                        }
                    }
                });
        }
    };
}]);
