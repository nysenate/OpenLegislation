var reportModule = angular.module('report');

/** --- REST resource for retrieving a single daybreak report --- */

reportModule.factory('DaybreakDetail', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/daybreaks/:reportDateTime", {
        reportDateTime: '@reportDateTime'
    });
}]);

/** --- Controller that handles report detail page --- */

reportModule.controller('DaybreakReportErrorCtrl',
        ['$scope', '$filter', '$timeout', '$routeParams', '$modal', 'DaybreakDetail', 'ngTableParams',
         function($scope, $filter, $timeout, $routeParams, $modal, DaybreakDetail, ngTableParams) {
    $scope.report = null;
    $scope.totals = null;
    $scope.labels = getLabels();
    $scope.errorFilter = null;
    $scope.filteredTypeTotals = null;
    $scope.dataDetails = {};
    $scope.filterWatchersInitialized = false;

    // Fetch the report by parsing the url for the report date/time
    $scope.getReportDetails = function() {
        $scope.reportDateTime = $routeParams.reportDateTime;
        $scope.report = DaybreakDetail.get({reportDateTime: $scope.reportDateTime}, function() {
            $scope.tableData = $scope.extractTableData();
            $scope.setDefaultNgTableParams();
            $scope.filterInit();
            $scope.setTitleDates();
        });
    };

    // Get the reports immediately
    $scope.getReportDetails();

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

    // Sets the default properties for the data table
    $scope.setDefaultNgTableParams = function(){
        $scope.tableParams = new ngTableParams({
                page: 1,
                count: 25,
                sorting: {
                    printNo: 'asc'
                }
            }, {
                total: $scope.tableData.length,
                getData: function($defer, params){
                    // Sort data
                    $scope.tableData = params.sorting() ? $filter('orderBy')($scope.tableData, params.orderBy()) : $scope.tableData;

                    // Filter data
                    var filteredData = $scope.filterData($scope.tableData);
                    params.total(filteredData.length);

                    // Calculate page offset
                    if(params.count()>0) {
                        var start = (params.page() - 1) * params.count();
                        var end = start + params.count();
                    }
                    else {  // Dont paginate if count is zero or less
                        var start = 0;
                        var end = params.total();
                    }

                    $defer.resolve(filteredData.slice(start, end));
                    $scope.activateFilterWatchers();
                }
        });
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
            $scope.errorFilter.types = getAllTypesFilter($scope.totals);
            $scope.errorFilter.noTypes = false;
        }
    };
    $scope.onFilterNoTypesUpdate = function() {
        if($scope.errorFilter.noTypes) {
            $scope.errorFilter.types = getNoTypesFilter($scope.totals);
            $scope.errorFilter.allTypes = false;
        }
    };

    // Updates the visible data based on the filter configuration
    $scope.onFilterUpdate = function(){
        if($scope.tableParams.data) {
            $scope.tableParams.reload();
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
    // Formats dates for display purposes
    $scope.formatReportDate = formatReportDate;
    $scope.formatReferenceDate = formatReferenceDate;

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

    // Sets the variables that display the report and reference dates in the title
    $scope.setTitleDates = function() {
        $scope.titleReportDate = formatReportDate($scope.reportDateTime);
        $scope.titleReferenceDate = formatReferenceDate($scope.report.details.referenceDateTime);
    }

}]);

function getTotals(reportData){
    var totals = {
        total: 0,
        statuses: reportData.details.mismatchStatuses,
        types: reportData.details.mismatchTypes
    };
    for(status in reportData.details.mismatchStatuses) {
        totals.total += reportData.details.mismatchStatuses[status];
    }
    return totals;
}

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

function getAllTypesFilter(totals) {
    return getFilterCategory(totals, 'types', true);
}

function getNoTypesFilter(totals) {
    return getFilterCategory(totals, 'types', false);
}

function getFilterCategory(totals, category, initialSetting){
    var filterCategory = {};
    for(item in totals[category]) {
        filterCategory[item] = initialSetting;
    }
    return filterCategory;
}

function getFilteredTypeTotals(errorFilter, totals) {
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

function formatReportDate(rawReportDate) {
    var reportDate =  moment(rawReportDate, "YYYY-MM-DDTHH:mm:ss");
    if(reportDate.isValid()){
        return reportDate.format("M/D/YYYY h:mm:ss A");
    }
    else{
        return rawReportDate;
    }
}

function formatReferenceDate(rawReferenceDate) {
    var referenceDate = moment(rawReferenceDate, "YYYY-MM-DDTHH:mm");
    if(referenceDate.isValid()){
        return referenceDate.format("M/D/YYYY");
    }
    else {
        return rawReferenceDate;
    }
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
        }
    }
}