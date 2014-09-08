var reportModule = angular.module('report');

/** --- REST resource for retrieving a single daybreak report --- */

reportModule.factory('DaybreakDetail', ['$resource', function($resource) {
    return $resource(apiPath + "/spotcheck/daybreaks/:reportDateTime", {
        reportDateTime: '@reportDateTime'
    });
}]);

/** --- Controller that handles report detail page --- */

reportModule.controller('DaybreakReportErrorCtrl', ['$scope', '$filter', '$routeParams', 'DaybreakDetail',
                         function($scope, $filter, $routeParams, DaybreakDetail) {
    $scope.reportData = null;
    $scope.observations = [];
    $scope.totals = null;
    $scope.labels = getLabels();
    $scope.errorFilter = null;
    $scope.filteredTypeTotals = null;

    // Fetch the report by parsing the url for the report date/time
    $scope.getReportDetails = function() {
        var reportDateTime = $routeParams.reportDateTime;
        $scope.report = DaybreakDetail.get({reportDateTime: reportDateTime}, function() {
            $scope.tableData = $scope.extractTableData();
        });
    };

    // Get the reports immediately
    $scope.getReportDetails();

    $scope.extractTableData = function() {
        var tableData = [];
        if ($scope.report && $scope.report.success) {
            angular.forEach($scope.report.details.observations, function(obs) {
                angular.forEach(obs.mismatches.items, function(m) {
                    var rowData = [obs.key.printNo, m.mismatchType, m.status, '<div style="height:20px;overflow:hidden;">' + m.observedData + '</div>', 'Insert Diff here...'];
                    tableData.push(rowData);
                });
            });
        }
        return tableData;
    };

//    $scope.getReportData = function() { $scope.reportData = testReport; };
    $scope.onFilterUpdate = function(){
        $scope.filteredTypeTotals = getFilteredTypeTotals($scope.errorFilter, $scope.totals);
        console.log($scope.filteredTypeTotals);
    };
    $scope.getLabel = function(labelType, field){
        if($scope.labels[labelType] && $scope.labels[labelType][field]){
            return $scope.labels[labelType][field];
        }
        return field;
    };

//    $scope.getReportData();
//    $scope.totals = getTotals($scope.reportData);
//    $scope.errorFilter = getDefaultFilter($scope.totals);
//    $scope.$watch('errorFilter', $scope.onFilterUpdate);
}]);

function getTotals(reportData){
    var totals = {
        total: 0,
        statuses: reportData.details.mismatchStatuses,
        types: reportData.details.mismatchTypes
    };
    for(status in totals.statuses){
        totals.total += totals.statuses[status];
    }
    return totals;
}

function getDefaultFilter(totals){
    errorFilter = { statuses: {}, types: {} };
    for(status in totals.statuses){
        errorFilter.statuses[status] = true;
    }
    for(type in totals.types){
        errorFilter.types[type] = true;
    }
    return errorFilter;
}

function getFilteredTypeTotals(errorFilter, totals){
    var filteredTypeTotals = {};
    console.log(totals.types);
    for(type in totals.types){
        var runningTotal = 0;
        for(status in totals.types[type]){
            if(errorFilter.statuses[status]){
                runningTotal += totals.types[type][status];
            }
        }
        filteredTypeTotals[type] = runningTotal;
    }
    return filteredTypeTotals;
}

function getLabels(){
    return {
        statuses: {
            RESOLVED: "Closed",
            NEW: "Opened",
            EXISTING: "Existing",
            REGRESSION: "Reopened",
            IGNORE: "Ignored"
        },
        types: {
            BILL_ACTIVE_AMENDMENT: "Amendment",
            BILL_SPONSOR: "Sponsor",
            BILL_MULTISPONSOR: "Mul.Sponsor",
            BILL_ACTION: "Action",
            BILL_COSPONSOR: "Co Sponsor",
            BILL_AMENDMENT_PUBLISH: "Publish",
            BILL_FULLTEXT_PAGE_COUNT: "Page Count",
            BILL_LAW_CODE: "Law Code",
            BILL_LAW_CODE_SUMMARY: "Law/Summary",
            BILL_SPONSOR_MEMO: "Sponsor Memo",
            BILL_SAMEAS: "Same As",
            BILL_SUMMARY: "Summary",
            BILL_TITLE: "Title",
            BILL_LAW_SECTION: "Law Section",
            BILL_MEMO: "Memo",
            REFERENCE_DATA_MISSING: "Missing Ref.",
            OBSERVE_DATA_MISSING: "Missing Bill"
        }
    }
}