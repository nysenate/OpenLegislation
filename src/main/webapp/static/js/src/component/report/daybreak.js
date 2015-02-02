var daybreakModule = angular.module('open.daybreak', ['open.core', 'smart-table']);


/** --- REST resources for retrieving daybreak summaries and reports --- */

// Gets summaries for reports that were generated within the specified range
daybreakModule.factory('DaybreakSummaryAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/daybreaks/:startDate/:endDate", {
        startDate: '@startDate', endDate: '@endDate'
    });
}]);

// Gets a full detailed report corresponding to the given date time
daybreakModule.factory('DaybreakDetailAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/daybreaks/:reportDateTime", {
        reportDateTime: '@reportDateTime'
    });
}]);

/** --- Parent Daybreak Controller --- */

daybreakModule.controller('DaybreakCtrl', ['$scope', '$routeParams', '$location', '$timeout', '$filter',
function ($scope, $routeParams, $location, $timeout, $filter) {

    function init() {
        $scope.setHeaderText("View Daybreak Reports");

        $scope.tabs = [{type: "summary", title: "Summaries"}];

        // The index of the currently selected tab
        $scope.selectedIndex = 0;

        if ($routeParams.hasOwnProperty('reports')) {
            var initialReports;
            if (typeof $routeParams['reports'] === 'string'){
                initialReports = [$routeParams['reports']];
            } else {
                initialReports = $routeParams['reports'];
            }
            initialReports.forEach($scope.openReportDetail);
        }

    }

    // Creates a new report detail tab for a given report date
    function newReportDetailTab(reportDateTime) {
        return {
            type: "detail",
            title: $filter('moment')(reportDateTime, 'lll'),
            reportDateTime: reportDateTime
        };
    }

    // Adds a new report detail tab for the given report date or opens it if it already exists
    $scope.openReportDetail = function(reportDateTime) {
        var i = $scope.getReportIndex(reportDateTime);
        if (i < 0) {
            i = $scope.tabs.push(newReportDetailTab(reportDateTime)) - 1;
            // TODO replace this klooge when angular md .8 comes out
            $timeout(function() {$scope.selectedIndex = i;}, 100);
        } else {
            $scope.selectedIndex = i;
        }
    };

    // Removes an open tab corresponding to the given report date time
    $scope.closeReportDetail = function(reportDateTime) {
        var i = $scope.getReportIndex(reportDateTime);
        if (i > 0) {
            $scope.selectedIndex = i;
            $scope.tabs.splice(i, 1);
        }
    };

    // Gets the tab index for the given report date time, returns -1 if no such tab exists
    $scope.getReportIndex = function(reportDateTime) {
        for (var i=0; i<$scope.tabs.length; i++) {
            var tab = $scope.tabs[i];
            if (tab.type === "detail" && tab.reportDateTime === reportDateTime) {
                return i;
            }
        }
        return -1;
    };

    $scope.$watch('tabs', function () {
        var reportDates = [];
        angular.forEach($scope.tabs, function (tab) {
            if (tab.type === 'detail') {
                reportDates.push(tab.reportDateTime);
            }
        });
        $location.search('reports', reportDates);
    }, true);

    $scope.$watch('selectedIndex', function () {
        if ($scope.selectedIndex === -1) {
            $scope.selectedIndex = 0;
        }
    });

    init();
}]);

/** --- Report Summary Controller --- */

daybreakModule.controller('DaybreakSummaryCtrl', ['$scope', '$filter', 'DaybreakSummaryAPI',
    function ($scope, $filter, DaybreakSummaryAPI) {

        $scope.rsEndDate = moment();
        $scope.rsStartDate = moment($scope.rsEndDate).subtract(1, 'months');
        $scope.reportSummaries = [];
        $scope.dataProvider = [];
        $scope.response = null;

        var testData = [
            {
                EXISTING: 271,
                IGNORE: 0,
                NEW: 0,
                REGRESSION: 0,
                RESOLVED: 0,
                reportDateTime: "2015-01-23T13:57:54"
            }, {
                EXISTING: 200,
                IGNORE: 0,
                NEW: 0,
                REGRESSION: 0,
                RESOLVED: 71,
                reportDateTime: "2015-01-23T14:57:54"
            }, {
                EXISTING: 200,
                IGNORE: 0,
                NEW: 0,
                REGRESSION: 35,
                RESOLVED: 0,
                reportDateTime: "2015-01-23T15:57:54"
            }
        ];

        $scope.chartConfig = {
            "type": "serial",
            "theme": "none",
            "marginLeft": 20,
            "pathToImages": "http://www.amcharts.com/lib/3/images/",
            "dataProvider": testData,
            "valueAxes": [{
                "stackType": "regular",
                "gridAlpha": 0.07,
                "position": "left",
                "title": "Mismatches"
            }],
            "graphs": [{
                "fillAlphas": 0.6,
                "lineAlpha": 0.4,
                "title": "New",
                "valueField": "NEW"
            }, {
                "fillAlphas": 0.6,
                "lineAlpha": 0.4,
                "title": "Existing",
                "valueField": "EXISTING"
            }, {
                "fillAlphas": 0.6,
                "lineAlpha": 0.4,
                "title": "Regression",
                "valueField": "REGRESSION"
            }, {
                "fillAlphas": 0.6,
                "lineAlpha": 0.4,
                "title": "Resolved",
                "valueField": "RESOLVED"
            }, {
                "fillAlphas": 0.6,
                "lineAlpha": 0.4,
                "title": "Ignored",
                "valueField": "IGNORE"
            }],
            "chartScrollbar": {},
            "chartCursor": {
                "categoryBalloonDateFormat": "YYYY-MM-DD hh-mm",
                "cursorAlpha": 0,
                "cursorPosition": "mouse"
            },
    //        "dataDateFormat": "mm",
            "categoryField": "reportDateTime",
            "categoryAxis": {
                "minPeriod": "mm",
                "parseDates": true,
                "minorGridAlpha": 0.1,
                "minorGridEnabled": true,
                "title": "Report Date"
            }
        };


        $scope.init = function() {
            $scope.getSummaries();
        };

        $scope.getSummaries = function() {
            $scope.response = DaybreakSummaryAPI.get({startDate: $scope.rsStartDate.format(), endDate: $scope.rsEndDate.format()},
                function() {
                    if ($scope.response.success) {
                        $scope.reportSummaries = $scope.response.reports.items;
                        var newChartConfig = angular.copy($scope.chartConfig);

                        $scope.dataProvider = $scope.reportSummaries.map($scope.getChartPoint);
                    }
                });
        };

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

daybreakModule.filter('mismatchStatusLabel', function () {
    var statusLabelMap = {
        RESOLVED: "Closed",
        NEW: "Opened",
        EXISTING: "Existing",
        REGRESSION: "Reopened",
        IGNORE: "Ignored"
    };
    return function(status) {
        if (statusLabelMap.hasOwnProperty(status)) {
            return statusLabelMap[status];
        }
        return status;
    };
});

daybreakModule.filter('mismatchTypeLabel', function () {
    var typeLabelMap = {
        BILL_ACTIVE_AMENDMENT: "Amendment",
        BILL_SPONSOR: "Sponsor",
        BILL_MULTISPONSOR: "Multi Sponsor",
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
    };
    return function(type) {
        if (typeLabelMap.hasOwnProperty(type)) {
            return typeLabelMap[type];
        }
        return type;
    };
});

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

daybreakModule.controller('DaybreakDetailCtrl', ['$scope', '$filter', '$location', 'DaybreakDetailAPI',
function ($scope, $filter, $location, DaybreakDetailAPI) {
    $scope.report = null;
    $scope.totals = null;
    $scope.errorFilter = null;
    $scope.filteredTypeTotals = null;
    $scope.dataDetails = {};
    $scope.filterWatchersInitialized = false;
    $scope.filteredTableData = [];

    // Initialization function
    $scope.init = function (reportDateTime) {
        $scope.reportDateTime = reportDateTime;
        $scope.getReportDetails();
    };

    // Fetch the report by parsing the url for the report date/time
    $scope.getReportDetails = function() {
        $scope.report = DaybreakDetailAPI.get({reportDateTime: $scope.reportDateTime}, function() {
            $scope.referenceDateTime = $scope.report.details.referenceDateTime;
            $scope.tableData = $scope.extractTableData();
            $scope.filterInit();
            $scope.activateFilterWatchers();
        });
    };

    // Extracts an array of table rows from the report data
    $scope.extractTableData = function() {
        var tableData = [];
        if ($scope.report && $scope.report.success) {
            angular.forEach($scope.report.details.observations, function(obs) {
                angular.forEach(obs.mismatches.items, function(m) {
                    var mismatchId = $scope.getMismatchId(obs, m);
                    var firstOpened = $scope.findFirstOpenedDates(m).reportDateTime;
                    var rowData = {
                        printNo: obs.key.printNo,
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
                    tableData.push(rowData);
                });
            });
        }
        return tableData;
    };

    // Searches through the prior mismatches of a mismatch to find the date that it was first opened
    $scope.findFirstOpenedDates = function(currentMismatch){
        if(currentMismatch.status == "NEW") {
            return {reportDateTime: $scope.reportDateTime, referenceDateTime: $scope.report.details.referenceDateTime};
        }
        for (index in currentMismatch.prior.items) {
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
        return observation.key.printNo + '-' + observation.key.session.year + '-' + mismatch.mismatchType;
    };

    $scope.getBillLink = function(printNo) {
        return ctxPath + "/bills/" + $filter('sessionYear')(moment($scope.referenceDateTime).year()) + "/" + printNo;
    };

    // Triggers a detail modal popup for the mismatch designated by mismatchId
    $scope.showDetailModal = function(mismatchId, activeTab) {
        $modal.open({
            templateUrl: 'detailsModal.html',
            controller: $scope.detailModalCtrl,
            resolve: {
                activeTab: function() { return activeTab; },
                details: function() { return $scope.dataDetails[mismatchId]; },
                parentFunctions: function() { return {
                    getLabel: $scope.getLabel,
                    showDetailModal: $scope.showDetailModal,
                    findFirstOpenedDates: $scope.findFirstOpenedDates,
                    getMismatchId: $scope.getMismatchId
                };}
            }
        });
    };

    // The controller for detail modals
    $scope.detailModalCtrl = function($scope, $modalInstance, details, activeTab, parentFunctions) {
        $scope.getLabel = parentFunctions.getLabel;
        $scope.getMismatchId = parentFunctions.getMismatchId;
        $scope.findFirstOpenedDates = parentFunctions.findFirstOpenedDates;

        $scope.formatReportDate = formatReportDate;
        $scope.formatReferenceDate = formatReferenceDate;
        $scope.printNo = details.observation.key.printNo;
        $scope.observation = details.observation;
        $scope.currentMismatch = details.mismatch;
        $scope.allMismatches = details.observation.mismatches.items;

        $scope.firstOpened = $scope.findFirstOpenedDates($scope.currentMismatch);

        $scope.tabs = { diff: false, lbdc: false, openleg: false, prior: false, other: false };
        $scope.tabs[activeTab] = true;


        $scope.openNewModal = function(mismatchId, activeTab) {
            $scope.cancel();
            parentFunctions.showDetailModal(mismatchId, activeTab);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('close');
        };
    };

    /** --- Filter functions --- */

    // given an array of rows, returns an array of rows that pass the filter
    $scope.filterData = function(data){
        var filteredData = [];
        for(index in data){
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