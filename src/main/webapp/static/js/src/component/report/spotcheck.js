var daybreakModule = angular.module('open.daybreak', ['open.core', 'smart-table']);


/** --- REST resources for retrieving daybreak summaries and reports --- */

// Gets summaries for reports that were generated within the specified range
daybreakModule.factory('DaybreakSummaryAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/summaries/:startDate/:endDate", {
        startDate: '@startDate', endDate: '@endDate'
    });
}]);

// Gets a full detailed report corresponding to the given date time
daybreakModule.factory('DaybreakDetailAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/:reportType/:reportDateTime", {
        reportType: '@reportType',
        reportDateTime: '@reportDateTime'
    });
}]);

var reportTypeMap = {};
var mismatchTypeMap = {};

daybreakModule.filter('mismatchStatusLabel', ['$filter', function ($filter) {
    var statusLabelMap = {
        RESOLVED: "Closed",
        NEW: "Opened",
        EXISTING: "Existing",
        REGRESSION: "Reopened",
        IGNORE: "Ignored"
    };
    return function(status) {
        return $filter('label')(status, statusLabelMap);
    };
}]);

daybreakModule.filter('mismatchTypeLabel', ['$filter', function ($filter) {
    return function(type) {
        return $filter('label')(type, mismatchTypeMap);
    };
}]);

daybreakModule.filter('reportTypeLabel', ['$filter', function ($filter) {
    return function(type) {
        return $filter('label')(type, reportTypeMap);
    }
}]);

daybreakModule.filter('reportType', function(){
    return function(type) {
        for (var key in reportTypeMap) {
            if (reportTypeMap.hasOwnProperty(key) && (type === key || type === reportTypeMap[key])) {
                return key;
            }
        }
        return null;
    }
});

daybreakModule.filter('contentType', function() {
    var contentTypeMap = {
        LBDC_ACTIVE_LIST: "Active List",
        LBDC_AGENDA_ALERT: "Agenda",
        LBDC_DAYBREAK: "Bill",
        LBDC_FLOOR_CALENDAR: "Floor Cal",
        LBDC_SCRAPED_BILL: "Bill"
    };
    return function(reportType) {
        if (contentTypeMap.hasOwnProperty(reportType)) {
            return contentTypeMap[reportType];
        }
        return "Content";
    };
});

/** --- Parent Daybreak Controller --- */

daybreakModule.controller('DaybreakCtrl', ['$scope', '$routeParams', '$location', '$timeout', '$filter',
function ($scope, $routeParams, $location, $timeout, $filter) {

    // The date of the currently open report, null if no open reports
    $scope.openReportDateTime = null;

    $scope.openReportType = null;

    // The index of the currently selected tab
    $scope.selectedIndex = 0;

    $scope.tabNames = ['summary', 'report'];

    $scope.rtmap = {};
    $scope.mtmap = {};

    $scope.init = function (rtmap, mtmap) {
        $scope.rtmap = reportTypeMap = rtmap;
        $scope.mtmap = mismatchTypeMap = mtmap;

        $scope.setHeaderVisible(true);
        $scope.setHeaderText("View Spotcheck Reports");

        if ($routeParams.hasOwnProperty('runTime') && $routeParams.hasOwnProperty('type')) {
            $scope.openReportDetail($routeParams['type'], $routeParams['runTime']);
        }
    };

    // Loads a new report in the detail tab
    $scope.openReportDetail = function(reportType, reportDateTime) {
        $scope.selectedIndex = 1;
        if ($scope.openReportDateTime != reportDateTime || $scope.openReportType != reportType) {
            $scope.openReportType = reportType;
            $scope.openReportDateTime = reportDateTime;
            console.log("new report: ", $scope.openReportType, $scope.openReportDateTime);
            $timeout(function () {$scope.$broadcast('newReportDetail')});
            $scope.setReportSearchParams();
        }
    };

    $scope.setReportSearchParams = function() {
        if ($scope.tabNames[$scope.selectedIndex] === 'report') {
            $scope.clearSearchParams();
            $scope.setSearchParam('type', $filter('reportTypeLabel')($scope.openReportType));
            $scope.setSearchParam('runTime', $scope.openReportDateTime);
        }
    };

    $scope.$watch('selectedIndex', function() {
        $scope.$broadcast('tabChangeEvent', $scope.selectedIndex);
        $scope.setReportSearchParams();
    });

}]);

/** --- Report Summary Controller --- */

daybreakModule.controller('DaybreakSummaryCtrl', ['$scope', '$filter', '$routeParams', '$location', 'DaybreakSummaryAPI',
function ($scope, $filter, $routeParams, $location, DaybreakSummaryAPI) {
    $scope.reportSummaries = [];
    $scope.dataProvider = [];
    $scope.response = null;

    $scope.params = {
        summaryType: "all",
        inputStartDate: null,
        inputEndDate: null
    };

    $scope.init = function() {
        if ('type' in $routeParams) {
            $scope.params.summaryType = $filter('reportType')($routeParams['type']) || "all";
        }
        if ($routeParams.hasOwnProperty('endDate') && moment($routeParams['endDate']).isValid()) {
            $scope.endDate = moment($routeParams['endDate']);
        } else {
            $scope.endDate = moment();
        }
        if ($routeParams.hasOwnProperty('startDate') && moment($routeParams['startDate']).isValid()) {
            $scope.startDate = moment($routeParams['startDate']);
        } else {
            $scope.startDate = moment($scope.endDate).subtract(3, 'months');
        }
        $scope.params.inputStartDate = moment($scope.startDate).toDate();
        $scope.params.inputEndDate = moment($scope.endDate).toDate();
    };

    $scope.getSummaries = function() {
        console.log("getting new summaries");
        var summaryType = $scope.params.summaryType !== "all" ? $filter('reportTypeLabel')($scope.params.summaryType) : [];
        $scope.response = DaybreakSummaryAPI.get({startDate: $scope.startDate.format(),
                                                  endDate: $scope.endDate.endOf('day').format(),
                                                  reportType: summaryType},
            function() {
                if ($scope.response.success) {
                    $scope.reportSummaries = $scope.response.reports.items;
                    console.log("summaries received");
                }
            });
    };

    // Watch the params for changes and make a request for report summaries if there were changes
    $scope.$watchCollection('params', function() {
        $scope.endDate = moment($scope.params.inputEndDate);
        $scope.startDate = moment($scope.params.inputStartDate);
        $scope.getSummaries();
        $scope.setSummarySearchParams();
    });

    $scope.setSummarySearchParams = function () {
        if ($scope.tabNames[$scope.selectedIndex] === "summary") {
            $scope.clearSearchParams();
            $scope.setSearchParam('type', $filter('reportTypeLabel')($scope.params.summaryType),
                $scope.params.summaryType !== "all");
            $scope.setSearchParam('startDate', $scope.startDate.format('YYYY-MM-DD'));
            $scope.setSearchParam('endDate', $scope.endDate.format('YYYY-MM-DD'));
        }
    };

    $scope.$on('tabChangeEvent', $scope.setSummarySearchParams);

    $scope.getChartPoint = function(reportSummary) {
        var point = reportSummary.mismatchStatuses;
        point.reportDateTime = reportSummary.reportDateTime;
        return point;
    };

    // Compute the total number of mismatches for a given type.
    $scope.computeMismatchCount = function(summaryItem, type) {
        var defaultFilter = $filter('default');
        var mismatchType = summaryItem['mismatchTypes'][type];
        if (!mismatchType) return 0;
        return (defaultFilter(mismatchType['NEW'], 0) +
        defaultFilter(mismatchType['EXISTING'], 0) +
        defaultFilter(mismatchType['REGRESSION'], 0));
    };

    // Compute the difference between open issues and resolved issues. Set 'abs' to true to
    // return the absolute value of the result.
    $scope.computeMismatchDiff = function(summaryItem, type, abs) {
        var defaultFilter = $filter('default');
        var mismatchType = summaryItem['mismatchTypes'][type];
        if (!mismatchType) return 0;
        var diff = (defaultFilter(mismatchType['NEW'], 0) +
        defaultFilter(mismatchType['REGRESSION'], 0) -
        defaultFilter(mismatchType['RESOLVED'], 0));
        return (abs) ? Math.abs(diff) : diff;
    };

    // Return a css class based on whether the mismatch count is positive or negative
    $scope.mismatchDiffClass = function(summaryItem, type) {
        var val = $scope.computeMismatchDiff(summaryItem, type, false);
        if (val > 0) {
            return "postfix-icon icon-arrow-up4 new-error";
        }
        else if (val < 0) {
            return "postfix-icon icon-arrow-down5 closed-error";
        }
        return "postfix-icon icon-minus3 existing-error";
    };

    $scope.init();

}]);

/** --- Report Detail Controller --- */

daybreakModule.directive('mismatchDiff', function(){
    return {
        restrict: 'E',
        scope: {
            diff: '='
        },
        template:
        "<span ng-repeat='segment in diff' ng-class=\"{'mismatch-diff-equal': segment.operation=='EQUAL', " +
        "'mismatch-diff-insert': segment.operation=='INSERT', 'mismatch-diff-delete': segment.operation=='DELETE'}\" >" +
        "{{segment.text}}" +
        "</span>"
    };
});

daybreakModule.controller('detailDialogCtrl', ['$scope', '$mdDialog', 'initialMismatchId', 'reportType',
    'getDetails', 'findFirstOpenedDates', 'getMismatchId', 'getContentId', 'getContentUrl',
function($scope, $mdDialog, initialMismatchId, reportType,
         getDetails, findFirstOpenedDates, getMismatchId, getContentId, getContentUrl) {

    $scope.selectedIndex = 0;

    $scope.getMismatchId = getMismatchId;

    $scope.getDetails = getDetails;

    $scope.findFirstOpenedDates = findFirstOpenedDates;

    $scope.reportType = reportType;

    $scope.newDetails = function (details) {
        $scope.details = details;

        $scope.contentId = getContentId(reportType, details.observation.key);
        $scope.contentUrl = getContentUrl(reportType, details.observation.key);
        $scope.observation = details.observation;
        $scope.currentMismatch = details.mismatch;
        $scope.allMismatches = details.observation.mismatches.items;

        $scope.firstOpened = $scope.findFirstOpenedDates($scope.currentMismatch);
    };

    $scope.openNewDetail = function(mismatchId) {
        $scope.newDetails($scope.getDetails(mismatchId));
    };

    $scope.cancel = function () {
        $mdDialog.hide();
    };

    function init() {
        $scope.openNewDetail(initialMismatchId);
    }

    init();
}]);

daybreakModule.controller('DaybreakDetailCtrl', ['$scope', '$element', '$filter', '$location', '$timeout', '$mdDialog', 'DaybreakDetailAPI',
function ($scope, $element, $filter, $location, $timeout, $mdDialog, DaybreakDetailAPI) {
    $scope.resultsPerPage = 10;
    $scope.rppOptions = [10, 20, 30, 50, 100];
    $scope.errorFilter = null;
    $scope.displayData = [];
    $scope.filterWatchersInitialized = false;

    // Initialization function
    $scope.init = function () {
        $scope.report = null;
        $scope.totals = null;
        $scope.filteredTypeTotals = null;
        $scope.dataDetails = {};
        $scope.tableData = [];
        $scope.filteredTableData = [];
        $scope.reportType = $scope.openReportType;
        $scope.reportDateTime = $scope.openReportDateTime;
        $scope.getReportDetails();
    };

    $scope.$on('newReportDetail', function () {
        $scope.init();
    });

    // Fetch the report by parsing the url for the report date/time
    $scope.getReportDetails = function() {
        $scope.report = DaybreakDetailAPI.get({reportType: $scope.reportType, reportDateTime: $scope.reportDateTime}, function() {
            $scope.referenceDateTime = $scope.report.details.referenceDateTime;
            $scope.extractTableData();
            $scope.filterInit();
            $scope.activateFilterWatchers();
            console.log("report detail received:", $scope.report.details.referenceType, $scope.report.details.referenceDateTime);
        });
        console.log("new report detail requested: ", $scope.reportType, $scope.reportDateTime);
    };

    // Extracts an array of table rows from the report data
    $scope.extractTableData = function() {
        if ($scope.report && $scope.report.success) {
            angular.forEach($scope.report.details.observations, function(obs) {
                angular.forEach(obs.mismatches.items, function(m) {
                    var reportType = $scope.report.details.referenceType;
                    var mismatchId = $scope.getMismatchId(obs, m);
                    var firstOpened = $scope.findFirstOpenedDates(m).reportDateTime;
                    var rowData = {
                        contentId: $scope.getContentId(reportType, obs.key),
                        contentUrl: $scope.getContentUrl(reportType, obs.key),
                        type: m.mismatchType,
                        status: m.status,
                        firstOpened: firstOpened,
                        diff: m.diff,
                        mismatchId: mismatchId
                    };
                    $scope.dataDetails[mismatchId] = {
                        observation: obs,
                        mismatch: m
                    };
                    $scope.tableData.push(rowData);
                });
            });
        }
    };

    // Searches through the prior mismatches of a mismatch to find the date that it was first opened
    $scope.findFirstOpenedDates = function(currentMismatch){
        if(currentMismatch.status == "NEW") {
            return {reportDateTime: $scope.reportDateTime, referenceDateTime: $scope.report.details.referenceDateTime};
        }
        for (var index in currentMismatch.prior.items) {
            if(currentMismatch.prior.items[index].status == "NEW") {
                return {
                    reportDateTime: currentMismatch.prior.items[index].reportId.reportDateTime,
                    referenceDateTime: currentMismatch.prior.items[index].reportId.referenceDateTime
                };
            }
        }
        return {reportDateTime: "Unknown", referenceDateTime: "Unknown"};
    };

    // Generates a mismatch id
    $scope.getMismatchId = function (observation, mismatch) {
        return JSON.stringify(observation.key) + '-' + mismatch.mismatchType;
    };

    var contentTypeIdMap = {
        LBDC_DAYBREAK: getBillId,
        LBDC_SCRAPED_BILL: getBillId,
        LBDC_AGENDA_ALERT: getAgendaId
    };
    $scope.getContentId = function(reportType, key) {
        if (contentTypeIdMap.hasOwnProperty(reportType)) {
            return contentTypeIdMap[reportType](key);
        }
        return reportType + "?!";
    };

    function getBillId(key) {
        return key.basePrintNo;
    }

    function getAgendaId(key) {
        return key.agendaId.year + '-' + key.agendaId.number + ' ' + key.committeeId.name +
            (key.addendum !== "DEFAULT" ? ('-' + key.addendum) : "");
    }

    var contentTypeUrlMap = {
        LBDC_DAYBREAK: getBillUrl,
        LBDC_SCRAPED_BILL: getBillUrl,
        LBDC_AGENDA_ALERT: getAgendaUrl
    };
    $scope.getContentUrl = function(reportType, key) {
        if (contentTypeUrlMap.hasOwnProperty(reportType)) {
            return contentTypeUrlMap[reportType](key);
        }
        return "";
    };

    function getBillUrl(key) {
        return ctxPath + "/bills/" + key.session.year + "/" + key.basePrintNo;
    }

    function getAgendaUrl(key) {
        return ctxPath + "/agendas/" + key.agendaId.year + "/" + key.agendaId.number + "?comm=" + key.committeeId.name;
    }

    $scope.getMismatchDetails = function(mismatchId) {
        return $scope.dataDetails[mismatchId];
    };

    // Triggers a detail sheet popup for the mismatch designated by mismatchId
    $scope.openDetailWindow = function(mismatchId) {
        $mdDialog.show({
            templateUrl: 'mismatchDetailWindow',
            controller: 'detailDialogCtrl',
            locals: {
                initialMismatchId: mismatchId,
                reportType: $filter('reportType')($scope.reportType)
            },
            resolve: {
                getDetails: function() { return $scope.getMismatchDetails; },
                findFirstOpenedDates: function() {return $scope.findFirstOpenedDates;},
                getMismatchId: function() { return $scope.getMismatchId; },
                getContentId: function() { return $scope.getContentId;},
                getContentUrl: function() {return $scope.getContentUrl;}
            }
        });
    };

    $scope.setRpp = function(number) {$scope.resultsPerPage = number;};

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