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
    $scope.$watch('filterMethod', function(){console.log("filterMethod Changed");}, true);
    $scope.report = null;
    $scope.totals = null;
    $scope.labels = getLabels();
    $scope.errorFilter = null;
    $scope.filteredTypeTotals = null;

    // Fetch the report by parsing the url for the report date/time
    $scope.getReportDetails = function() {
        var reportDateTime = $routeParams.reportDateTime;
        $scope.report = DaybreakDetail.get({reportDateTime: reportDateTime}, function() {
            $scope.tableData = $scope.extractTableData();
            $scope.filterInit();
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

    // Binds each filter entry such that if it is unset, the 'all' filter entry is unset
    $scope.bindUpdateFilterAll = function(){
        for (filterClass in $scope.errorFilter) {
            if(filterClass != 'all'){
                for(filterAttribute in $scope.errorFilter[filterClass]){
                    $scope.$watch('errorFilter.' + filterClass + '.' + filterAttribute,
                        $scope.getFilterUpdateProcedure(filterClass, filterAttribute));
                }
            }
        }
    };

    // A function tailored for a single filter value that sets the 'all' filter value to false if the single value is false
    $scope.getFilterUpdateProcedure = function(filterClass, filterAttribute){
        return function(){
            if($scope.errorFilter[filterClass][filterAttribute]){
                $scope.errorFilter.none = false;
            }
            else{
                $scope.errorFilter.all = false;
            }
        };
    };

    $scope.onFilterAllUpdate = function(){
        if($scope.errorFilter.all){
            $scope.errorFilter = getDefaultFilter($scope.totals);
        }
    };

    $scope.onFilterNoneUpdate = function() {
        if($scope.errorFilter.none){
            $scope.errorFilter = getNoneFilter($scope.totals);
        }
    };

    // Updates the visible data based on the filter configuration
    $scope.onFilterUpdate = function(){
        $scope.filterMethod = function(row){
            var status = row[2];
            var type = row[1];
            if($scope.errorFilter.statuses[status] && $scope.errorFilter.types[type]){
                return true;
            }
            return false;
        }
    };

    // Updates the displayed error type totals based on status in addition to updating the visible data
    $scope.onStatusFilterUpdate = function(){
        $scope.onFilterUpdate();
        $scope.filteredTypeTotals = getFilteredTypeTotals($scope.errorFilter, $scope.totals);
    };

    // Attempts to get a label for the given field
    $scope.getLabel = function(labelType, field){
        if($scope.labels[labelType] && $scope.labels[labelType][field]){
            return $scope.labels[labelType][field];
        }
        return field;
    };

    $scope.labelRow = function(row){
//        var status = row[2];
//        var type = row[1];
//        row[1] = $scope.getLabel('types', type);
//        row[2] = $scope.getLabel('statuses', status);
    };

    $scope.filterInit = function() {
        $scope.totals = getTotals($scope.report);
        $scope.errorFilter = getDefaultFilter($scope.totals);
        $scope.$watch('errorFilter.all', $scope.onFilterAllUpdate);
        $scope.$watch('errorFilter.none', $scope.onFilterNoneUpdate);
        $scope.$watch('errorFilter.statuses', $scope.onStatusFilterUpdate, true);
        $scope.$watch('errorFilter.types', $scope.onFilterUpdate, true);
        $scope.bindUpdateFilterAll();
        $scope.statusCount=Object.keys($scope.filteredTypeTotals.statuses).length + 2;
        $scope.typeCount=Object.keys($scope.filteredTypeTotals.types).length;
    };
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
    errorFilter = { statuses: {}, types: {}, all: true, none: false};
    for(status in totals.statuses){
        if(!errorFilter.statuses[status]) {
            errorFilter.statuses[status] = true;
        }
    }
    for(type in totals.types){
        if(!errorFilter.types[type]) {
            errorFilter.types[type] = true;
        }
    }
    return errorFilter;
}

function getNoneFilter(totals){
    errorFilter = { statuses: {}, types: {}, all: false, none: true};
    for(status in totals.statuses){
        if(!errorFilter.statuses[status]) {
            errorFilter.statuses[status] = false;
        }
    }
    for(type in totals.types){
        if(!errorFilter.types[type]) {
            errorFilter.types[type] = false;
        }
    }
    return errorFilter;
}

function getFilteredTypeTotals(errorFilter, totals){
    var filteredTypeTotals = {};
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
            BILL_LAW_CODE_SUMMARY: "Law/Summ.",
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