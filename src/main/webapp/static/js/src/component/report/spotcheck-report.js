/** --- Report Detail Controller --- */

angular.module('open.spotcheck')
    .controller('SpotcheckDetailCtrl', ['$scope', '$element', '$filter', '$location', '$timeout',
            '$routeParams', 'SpotcheckDetailAPI',
function ($scope, $element, $filter, $location, $timeout, $routeParams, SpotcheckDetailAPI) {
    $scope.resultsPerPage = 10;
    $scope.errorFilter = null;
    $scope.displayData = [];
    $scope.filterWatchersInitialized = false;
    $scope.loadingReport = false;

    // Initialization function
    $scope.init = function () {
        $scope.report = null;
        $scope.totals = null;
        $scope.filteredTypeTotals = null;
        $scope.dataDetails = {};
        $scope.tableData = [];
        $scope.filteredTableData = [];
        if ($routeParams.hasOwnProperty('runTime') && $routeParams.hasOwnProperty('type')) {
            $scope.reportType = $routeParams['type'];
            $scope.reportDateTime = $routeParams['runTime'];
            $scope.getReportDetails();
        }
    };

    // Fetch the report via the report detail API
    $scope.getReportDetails = function() {
        $scope.report = SpotcheckDetailAPI.get({reportType: $scope.reportType, reportDateTime: $scope.reportDateTime}, function() {
            $scope.referenceDateTime = $scope.report.details.referenceDateTime;
            $scope.reportType = $scope.report.details.referenceType;
            $scope.extractTableData();
            $scope.filterInit();
            $scope.activateFilterWatchers();
            $scope.loadingReport = false;
            console.log("report detail received:", $scope.report.details.referenceType, $scope.report.details.referenceDateTime);
        });
        $scope.loadingReport = true;
        console.log("new report detail requested: ", $scope.reportType, $scope.reportDateTime);
    };

    // Extracts an array of table rows from the report data
    $scope.extractTableData = function() {
        $scope.tableData = $scope.extractMismatchRows($scope.report.details.observations, $scope.reportType);
        angular.forEach($scope.tableData, function(row) {
            $scope.dataDetails[row.mismatchId] = row;
        });
    };

    $scope.getMismatchDetails = function(mismatchId) {
        return $scope.dataDetails[mismatchId];
    };

    $scope.openDetailWindow = function(mismatchRow) {
        $scope.showMismatchDetails(mismatchRow, $scope.getMismatchDetails);
    };

    /** --- Filter functions --- */

        // given an array of rows, returns an array of rows that pass the filter
    $scope.filterData = function(data){
        var filteredData = [];
        for(var index in data){
            if($scope.filterSelector(data[index])){
                filteredData.push(data[index]);
            }
        }
        return filteredData;
    };

    // Given a row, returns true if it passes the filter
    $scope.filterSelector = function(row){
        return $scope.errorFilter.statuses[row.status] && $scope.errorFilter.types[row.type];
    };

    // Binds each filter entry such that if it is unset, the 'all' filter entry is unset
    $scope.bindUpdateFilterAll = function(){
        for (var filterClass in $scope.errorFilter) {
            if(filterClass != 'all'){
                for(var filterAttribute in $scope.errorFilter[filterClass]){
                    $scope.$watch('errorFilter.' + filterClass + '.' + filterAttribute,
                        $scope.getFilterUpdateProcedure(filterClass, filterAttribute));
                }
            }
        }
    };

    // A function tailored for a single filter value that sets the 'all' filter value to false if the single value is false
    $scope.getFilterUpdateProcedure = function(filterClass, filterAttribute) {
        return function(){
            if($scope.errorFilter[filterClass][filterAttribute]) {
                $scope.errorFilter.none = false;
            }
            else{
                $scope.errorFilter.all = false;
            }

            if(filterClass == 'types') {
                if($scope.errorFilter[filterClass][filterAttribute]) {
                    $scope.errorFilter.noTypes = false;
                }
                else {
                    $scope.errorFilter.allTypes = false;
                }
            }
        };
    };

    // These methods handle the select all or select none filter options
    $scope.onFilterAllUpdate = function() {
        if($scope.errorFilter.all){
            $scope.errorFilter = getDefaultFilter($scope.totals);
        }
    };
    $scope.onFilterNoneUpdate = function() {
        if($scope.errorFilter.none){
            $scope.errorFilter = getNoneFilter($scope.totals);
        }
    };
    $scope.onFilterAllTypesUpdate = function() {
        if($scope.errorFilter.allTypes) {
            $scope.errorFilter.types = getFilterCategory($scope.totals, 'types', true);
            $scope.errorFilter.noTypes = false;
        }
    };
    $scope.onFilterNoTypesUpdate = function() {
        if($scope.errorFilter.noTypes) {
            $scope.errorFilter.types = getFilterCategory($scope.totals, 'types', false);
            $scope.errorFilter.allTypes = false;
        }
    };

    // Updates the visible data based on the filter configuration
    $scope.onFilterUpdate = function(){
        $scope.filteredTableData = $scope.filterData($scope.tableData);
    };

    // Updates the displayed error type totals based on status in addition to updating the visible data
    $scope.onStatusFilterUpdate = function(){
        $scope.onFilterUpdate();
        $scope.filteredTypeTotals = getFilteredTypeTotals($scope.errorFilter, $scope.totals);
    };

    // Constructs an object containing the totals of each mismatch type and status from a report object
    function getTotals(reportData){
        var totals = {
            total: 0,
            statuses: reportData.details.mismatchStatuses,
            types: reportData.details.mismatchTypes
        };
        for(var status in reportData.details.mismatchStatuses) {
            totals.total += reportData.details.mismatchStatuses[status];
        }
        return totals;
    }

    // Generates an altered type total object based on which statuses are active in the error filter
    function getFilteredTypeTotals(errorFilter, totals) {
        var filteredTypeTotals = {};
        for(var type in totals.types){
            var runningTotal = 0;
            for(var status in totals.types[type]){
                if(errorFilter.statuses[status]){
                    runningTotal += totals.types[type][status];
                }
            }
            filteredTypeTotals[type] = runningTotal;
        }
        return filteredTypeTotals;
    }

    // Constructs a filter for the given totals where all rows are allowed
    function getDefaultFilter(totals) {
        return {
            statuses: getFilterCategory(totals, 'statuses', true),
            types: getFilterCategory(totals, 'types', true),
            all: true,
            none: false,
            allTypes: true,
            noTypes: false
        };
    }

    // Constructs a filter for the given totals where no rows are allowed
    function getNoneFilter(totals) {
        return {
            statuses: getFilterCategory(totals, 'statuses', false),
            types: getFilterCategory(totals, 'types', false),
            all: false,
            none: true,
            allTypes: false,
            noTypes: true
        };
    }

    // Generates a filter category(types or statuses) object with all facets set to the initialSetting
    function getFilterCategory(totals, category, initialSetting){
        var filterCategory = {};
        for(var item in totals[category]) {
            filterCategory[item] = initialSetting;
        }
        return filterCategory;
    }

    // Initialize the filter model
    $scope.filterInit = function() {
        $scope.totals = getTotals($scope.report);
        $scope.errorFilter = getDefaultFilter($scope.totals);
        $scope.statusCount=Object.keys($scope.totals.statuses).length + 2;
        $scope.typeCount=Object.keys($scope.totals.types).length;
        $scope.onStatusFilterUpdate();
    };

    // Activate filter watchers
    $scope.activateFilterWatchers = function(){
        if(!$scope.filterWatchersInitialized) {
            $scope.$watch('errorFilter.all', $scope.onFilterAllUpdate);
            $scope.$watch('errorFilter.none', $scope.onFilterNoneUpdate);
            $scope.$watch('errorFilter.allTypes', $scope.onFilterAllTypesUpdate);
            $scope.$watch('errorFilter.noTypes', $scope.onFilterNoTypesUpdate);
            $scope.$watch('errorFilter.statuses', $scope.onStatusFilterUpdate, true);
            $scope.$watch('errorFilter.types', $scope.onFilterUpdate, true);
            $scope.bindUpdateFilterAll();
            $scope.filterWatchersInitialized = true;
        }
    };

}]);
