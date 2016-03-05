var spotcheckModule = angular.module('open.spotcheck', ['open.core', 'smart-table', 'diff-match-patch']);


/** --- REST resources for retrieving daybreak summaries and reports --- */

// Gets summaries for reports that were generated within the specified range
spotcheckModule.factory('SpotcheckSummaryAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/summaries/:startDate/:endDate", {
        startDate: '@startDate', endDate: '@endDate'
    });
}]);

// Gets a full detailed report corresponding to the given date time
spotcheckModule.factory('SpotcheckDetailAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/:reportType/:reportDateTime", {
        reportType: '@reportType',
        reportDateTime: '@reportDateTime'
    });
}]);

spotcheckModule.factory('SpotcheckOpenMismatchAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/open-mismatches");
}]);

spotcheckModule.factory('SpotcheckOpenMismatchSummaryAPI', ['$resource', function($resource) {
    return $resource(adminApiPath + "/spotcheck/open-mismatches/summary");
}]);

spotcheckModule.factory('SpotcheckMismatchIgnoreAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + "/spotcheck/mismatch/:mismatchId/ignore",
        {mismatchId: '@mismatchId', ignoreLevel: '@ignoreLevel'});
}]);

spotcheckModule.factory('SpotcheckMismatchTrackingAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + "/spotcheck/mismatch/:mismatchId/issue/:issueId", {
        mismatchId: '@mismatchId',
        issueId: '@issueId'
    });
}]);

var reportTypeMap = {};
var reportTypeDispMap = {};
var mismatchTypeMap = {};

// Returns a formatted label for the given mismatch status
spotcheckModule.filter('mismatchStatusLabel', ['$filter', function ($filter) {
    var statusLabelMap = {
        all: 'All',
        RESOLVED: "Closed",
        NEW: "New",
        EXISTING: "Existing",
        REGRESSION: "Reopened",
        IGNORE: "Ignored"
    };
    return function(status) {
        return $filter('label')(status, statusLabelMap);
    };
}]);

// Returns a formatted label for the given mismatch type
spotcheckModule.filter('mismatchTypeLabel', ['$filter', function ($filter) {
    return function(type) {
        if (type === 'all') {
            return 'All';
        }
        return $filter('label')(type, mismatchTypeMap);
    };
}]);

spotcheckModule.filter('reportTypeRefName', ['$filter', function ($filter) {
    return function(type) {
        return $filter('label')(type, reportTypeMap);
    }
}]);

spotcheckModule.filter('reportTypeLabel', ['$filter', function ($filter) {
    return function(type) {
        return $filter('label')(type, reportTypeDispMap);
    }
}]);

// Returns a report type enum value for the given shorthand or enum report type value
spotcheckModule.filter('reportType', function(){
    return function(type) {
        for (var key in reportTypeMap) {
            if (reportTypeMap.hasOwnProperty(key) && (type === key || type === reportTypeMap[key])) {
                return key;
            }
        }
        return null;
    }
});

spotcheckModule.filter('contentType', function() {
    var contentTypeMap = {
        LBDC_AGENDA_ALERT: "Agenda",
        LBDC_DAYBREAK: "Bill",
        LBDC_CALENDAR_ALERT: "Floor Cal",
        LBDC_SCRAPED_BILL: "Bill",
        SENATE_SITE_BILLS: "Bill"
    };
    return function(reportType) {
        if (contentTypeMap.hasOwnProperty(reportType)) {
            return contentTypeMap[reportType];
        }
        return "Content";
    };
});

spotcheckModule.filter('reportDataProvider', function () {
    var defaultDataProvider = "Openleg";
    return function (refType) {
        return defaultDataProvider;
    }
});

spotcheckModule.filter('reportReferenceProvider', function () {
    var refProviderMap = {
        LBDC_DAYBREAK: 'LBDC',
        LBDC_SCRAPED_BILL: 'LBDC',
        SENATE_SITE_BILLS: 'Nysenate.gov',
        LBDC_AGENDA_ALERT: 'LBDC',
        LBDC_CALENDAR_ALERT: 'LBDC'
    };
    return function (refType) {
        if (refProviderMap.hasOwnProperty(refType)) {
            return refProviderMap[refType];
        }
        return "unknown provider of [" + refType + "]";
    }
});

spotcheckModule.filter('refTypeLabel', function () {
    var refTypeLabelMap = {
        LBDC_DAYBREAK: 'Daybreak report',
        LBDC_SCRAPED_BILL: 'scraped bill',
        SENATE_SITE_BILLS: 'node dump',
        LBDC_AGENDA_ALERT: 'alert',
        LBDC_CALENDAR_ALERT: 'alert'
    };
    return function (refType) {
        if (refTypeLabelMap.hasOwnProperty(refType)) {
            return refTypeLabelMap[refType];
        }
        return "'" + refType + "'?!";
    };
});

spotcheckModule.filter('contentUrl', function() {
    var contentTypeUrlMap = {
        LBDC_DAYBREAK: getBaseBillUrl,
        LBDC_SCRAPED_BILL: getBaseBillUrl,
        SENATE_SITE_BILLS: getBillAmendmentUrl,
        LBDC_AGENDA_ALERT: getAgendaUrl,
        LBDC_CALENDAR_ALERT: getCalendarUrl
    };
    function getBaseBillUrl(key) {
        return ctxPath + "/bills/" + key.session.year + "/" + key.basePrintNo;
    }
    function getBillAmendmentUrl(key) {
        return getBaseBillUrl(key) + "?version=" + key.version;
    }
    function getAgendaUrl(key) {
        if (key.agendaId.year > 0) {
            return ctxPath + "/agendas/" + key.agendaId.year + "/" + key.agendaId.number + "?comm=" + key.committeeId.name;
        }
        if (key.agendaId.year == -1) {
            return "http://open.nysenate.gov/legislation/meeting/" + key.committeeId.name.replace(/[ ,]+/g, '-') + '-' +
                moment(key.agendaId.number).format('MM-DD-YYYY');
        }
        return "";
    }
    function getCalendarUrl(key) {
        return ctxPath + "/calendars/" +  key.year + "/" + key.calNo;
    }
    return function(key, reportType) {
        if (contentTypeUrlMap.hasOwnProperty(reportType)) {
            return contentTypeUrlMap[reportType](key);
        }
        return "";
    }
});

spotcheckModule.filter('contentId', function () {
    var contentTypeIdMap = {
        LBDC_DAYBREAK: getBaseBillId,
        LBDC_SCRAPED_BILL: getBaseBillId,
        SENATE_SITE_BILLS: getBillId,
        LBDC_AGENDA_ALERT: getAgendaId,
        LBDC_CALENDAR_ALERT: getCalendarId
    };
    function getBaseBillId(key) {
        return key.basePrintNo;
    }
    function getBillId(key) {
        return key.printNo;
    }
    function getAgendaId(key) {
        var commNameAndAddendum = ' ' + key.committeeId.name + (key.addendum !== "DEFAULT" ? ('-' + key.addendum) : "");
        if (key.agendaId.year > 0) {
            return key.agendaId.year + '-' + key.agendaId.number + commNameAndAddendum;
        }
        var dateString = moment(key.agendaId.number).format('l');
        return dateString + commNameAndAddendum;
    }
    function getCalendarId(key) {
        return key.calNo + ', ' + key.year;
    }
    return function (key, reportType) {
        if (contentTypeIdMap.hasOwnProperty(reportType)) {
            return contentTypeIdMap[reportType](key);
        }
        return reportType + "?!";
    };
});

spotcheckModule.factory('SpotcheckDefaultFilter', function () {
    function passes(arg1, type, ignoreStatus, trackedStatus) {
        if (typeof arg1 === 'object') {
            return this.filterFunction(arg1.status, arg1.mismatch.mismatchType,
                arg1.mismatch.ignoreStatus, arg1.mismatch.issueIds.size > 0);
        }
        return this.filterFunction(arg1, type, ignoreStatus, trackedStatus);
    }

    function isWildcard(field) {
        return field === true || field === 'all';
    }

    function filterFunction(status, type, ignoreStatus, trackedStatus) {
        var ignored = ignoreStatus === true || ignoreStatus && ignoreStatus !== "NOT_IGNORED";
        var tracked = trackedStatus === true || trackedStatus === "TRACKED";
        return (isWildcard(status) || this.statuses[status] === true) &&
            (isWildcard(type) || this.types[type] === true) &&
            ((this.ignoredShown || !ignored) && (!this.ignoredOnly || ignored)) &&
            ((this.trackedShown || !tracked) && (this.untrackedShown || tracked));
    }

    return {
        passes: passes,
        filterFunction: filterFunction,
        orderBy: 'OBSERVED_DATE',
        sortOrder: 'DESC',
        limit: 10,
        offset: 1,
        statuses: {},
        types: {},
        ignoredShown: false,
        ignoredOnly: false,
        trackedShown: true,
        untrackedShown: true
    };
});

spotcheckModule.factory('IgnoreStatuses', function () {
    return {
        NOT_IGNORED: "Not Ignored",
        IGNORE_PERMANENTLY: "Ignore Permanently",
        IGNORE_UNTIL_RESOLVED: "Ignore Until Resolved",
        IGNORE_ONCE: "Ignore Once"
    };
});

spotcheckModule.filter('ignoreLabel', ['IgnoreStatuses', function (ignoreStatuses) {
    return function (ignoreStatus) {
        return ignoreStatuses[ignoreStatus];
    };
}]);

spotcheckModule.filter('mismatchCount', function () {
    return function(summary, filter, wildcardFields) {
        if (!(summary && summary.mismatchCounts)) {
            console.log('attempt to extract count from invalid summary', summary);
            return "!?";
        }
        if (!(filter && filter.hasOwnProperty('passes'))) {
            var simpleFilter = true;
            angular.forEach(filter, function (value, field) {
                if (value === 'all') {
                    delete filter[field];
                }
            });
        } else {
            if (typeof wildcardFields != 'object') {
                wildcardFields = [wildcardFields];
            }
            for (var field in wildcardFields) {
                if (field === "status") {
                    var statusWildcard = true;
                } else if (field === "type") {
                    var typeWildcard = true;
                } else if (field === "ignored") {
                    var ignoreWildcard = true;
                } else if (field === "tracked") {
                    var trackedWildcard = true;
                }
            }
        }
        var totalCount = 0;
        angular.forEach(summary.mismatchCounts, function (statusCounts, type) {
            angular.forEach(statusCounts, function (ignoredCounts, status) {
                angular.forEach(ignoredCounts, function (trackedCounts, ignoreStatus) {
                    angular.forEach(trackedCounts, function (count, trackedStatus) {
                        if (simpleFilter) {
                            if ('type' in filter && filter.type !== type ||
                                    'status' in filter && filter.status !== status ||
                                    'ignored' in filter && filter.ignored !== (ignoreStatus !== "NOT_IGNORED") ||
                                    'tracked' in filter && filter.tracked !== (trackedStatus === 'TRACKED')) {
                                return;
                            }
                        } else if (!filter.passes(status, type, ignoreStatus, trackedStatus)) {
                            return;
                        }
                        totalCount += count;
                    })
                })
            })
        });
        return totalCount;
    }
});

spotcheckModule.filter('hasIgnoredMismatches', ['$filter', function ($filter) {
    return function (summary, filter) {
        filter = angular.copy(filter);
        filter.ignored = true;
        return $filter('mismatchCount')(summary, filter) > 0;
    }
}]);


/** --- Parent Spotcheck Controller --- */

spotcheckModule.controller('SpotcheckCtrl', ['$scope', '$routeParams', '$location', '$timeout', '$filter', '$mdDialog',
function ($scope, $routeParams, $location, $timeout, $filter, $mdDialog) {

    $scope.rtmap = {};
    $scope.rtDispMap = {};
    $scope.mtmap = {};

    $scope.init = function (rtmap, rtDispMap, mtmap) {
        $scope.rtmap = reportTypeMap = rtmap;
        $scope.rtDispMap = reportTypeDispMap = rtDispMap;
        $scope.mtmap = mismatchTypeMap = mtmap;

        $scope.setHeaderVisible(true);
        $scope.setHeaderText("View Spotcheck Reports");
    };

    $scope.getReportURL = function (reportType, reportRunTime) {
        return ctxPath + "/admin/report/spotcheck/" +
            $filter('reportTypeRefName')(reportType) + "/" + $filter('moment')(reportRunTime, "YYYY-MM-DD[T]HH:mm:ss.SSS");
    };

    /**
     * Generates a human readable string representing the given content key
     * @param reportType - The type of report the key is present in
     * @param key - A content key that references a piece of legislative content
     */
    $scope.getContentId = function(reportType, key) {
        return $filter('contentId')(key, reportType);
    };

    /**
     * Generates an appropriate URL to view the data referenced by the given content key
     * @param reportType - the type of report that the key is present in
     * @param key - A content key that references a piece of legislative content
     */
    $scope.getContentUrl = function(reportType, key) {
        return $filter('contentUrl')(key, reportType);
    };

    // Searches through the prior mismatches of a mismatch to find the date that it was first opened
    $scope.findFirstOpenedDates = function(mismatch, observation){
        if(mismatch.status == "NEW") {
            return {reportDateTime: observation.reportDateTime, referenceDateTime: observation.refDateTime};
        }
        for (var index in mismatch.prior.items) {
            if(mismatch.prior.items[index].status == "NEW") {
                return {
                    reportDateTime: mismatch.prior.items[index].reportId.reportDateTime,
                    referenceDateTime: mismatch.prior.items[index].reportId.referenceDateTime
                };
            }
        }
        return {reportDateTime: "Unknown", referenceDateTime: "Unknown"};
    };

    // Generates a mismatch id
    $scope.getMismatchId = function (observation, mismatch) {
        return JSON.stringify(observation.key) + '-' + mismatch.mismatchType;
    };

    $scope.extractMismatchRows = function(observations, refType) {
        var mismatchRows = [];
        angular.forEach(observations, function(obs) {
            angular.forEach(obs.mismatches.items, function(m) {
                var chips = [];
                if (m.ignoreStatus && m.ignoreStatus !== "NOT_IGNORED") {
                    chips.push(m.ignoreStatus);
                }
                angular.forEach(m.issueIds.items, function (issueId) {
                    chips.push(issueId);
                });
                mismatchRows.push({
                    key: obs.key,
                    keyString: $scope.getContentId(refType, obs.key),
                    observation: obs,
                    mismatch: m,
                    type: m.mismatchType,
                    status: m.status,
                    observed: obs.observedDateTime,
                    reportDateTime: obs.reportDateTime,
                    firstOpened: $scope.findFirstOpenedDates(m, obs).reportDateTime,
                    refData: m.referenceData,
                    obsData: m.observedData,
                    mismatchId: $scope.getMismatchId(obs, m),
                    refType: refType,
                    chips: chips
                });
            });
        });
        return mismatchRows;
    };

    $scope.showSummaryDetails = function(summary) {
        $scope.notImplementedDialog();
    };
}]);

spotcheckModule.directive('mismatchDiff', ['$timeout', function($timeout){
    return {
        restrict: 'E',
        scope: {
            left: '=',
            right: '='
        },
        template:
        "<span ng-class='{preformatted: pre, \"word-wrap\": !pre}' style='line-height: {{lineHeight}}px'>" +
            "<line-numbers ng-if='pre && showLines' line-end='lines'></line-numbers>" +
            "<semantic-diff left-obj='left' right-obj='right'></semantic-diff>" +
        "</span></span>"
        ,
        link: function($scope, $element, $attrs) {
            $scope.pre = !$attrs.hasOwnProperty('noPre');
            $scope.wrap = !$attrs.hasOwnProperty('noWrap');
            $scope.showLines = $attrs.showLines !== "false";
            $scope.lines = 1;
            $scope.lineHeight = 20;
            $scope.adjustLineCount = function() {
                if (!$scope.pre && !$attrs.hasOwnProperty('noPre')) {
                    $scope.pre = $scope.left.split(/\n/).length > 1 || $scope.right.split(/\n/).length > 1;
                }
                if ($scope.pre && $scope.showLines) {
                    $timeout(function () {
                        var childElement = $element.children()[0];
                        var elementHeight = childElement.offsetHeight;
                        $scope.lines = Math.ceil(elementHeight / $scope.lineHeight);
                        $scope.pre = $scope.lines > 1;
                    }, 100);
                }
            };
            $scope.$watchGroup(['left', 'right'], $scope.adjustLineCount);
        }
    };
}]);

spotcheckModule.directive('diffSummary', function () {
    return {
        restrict: 'E',
        scope: {
            fullDiff: '='
        },
        template:
        "<div ng-repeat='diff in selectedDiffs'>" +
            "<span class='diff-summary-header'>Lines {{diff.startLineNum}} to {{diff.endLineNum}}:<br></span>" +
            "<div class='preformatted'>" +
                "<line-numbers line-start='diff.startLineNum' line-end='diff.endLineNum'></line-numbers>" +
                "<span ng-bind='diff.startText'></span>" +
                "<span ng-repeat='seg in diff.segments'>" +
                    "<span ng-if='seg.operation === \"EQUAL\"' ng-bind='seg.text'></span>" +
                    "<ins ng-if='seg.operation === \"INSERT\"' ng-bind='seg.text'></ins>" +
                    "<del ng-if='seg.operation === \"DELETE\"' ng-bind='seg.text'></del>" +
                "</span>" +
                "<span ng-bind='diff.endText'></span>" +
            "</div>" +
            "<br ng-if='!$last'><br ng-if='!$last'><md-divider ng-if='!$last'></md-divider>" +
        "</div>",
        link: function($scope, $element, $attrs) {
            $scope.selectedDiffs = [];
            $scope.$watch('selectedDiffs', function () {
                console.log($scope.selectedDiffs);
            }, true);
            var currentLineNum = 1;
            var currentLineText = "";
            var currentDiff = null;
            // Isolate differences from full text
            for (var iSeg in $scope.fullDiff) {
                var segment = $scope.fullDiff[iSeg];
                var segLines = segment.text.split(/\n/);
                if (["DELETE", "INSERT"].indexOf(segment.operation) >= 0) {
                    if (currentDiff == null) {
                        currentDiff = {
                            startText: currentLineText,
                            startLineNum: currentLineNum,
                            segments: []
                        };
                    }
                    currentDiff.segments.push(segment);
                } else {
                    if (currentDiff != null) {
                        if (segLines.length > 2) {
                            currentDiff.endText = segLines[0];
                            currentDiff.endLineNum = currentLineNum;
                            $scope.selectedDiffs.push(currentDiff);
                            currentDiff = null;
                        } else {
                            currentDiff.segments.push(segment);
                        }
                    }
                    currentLineText = segLines.length > 1 ? segLines[segLines.length - 1] : currentLineText + segLines[0];
                }
                currentLineNum += segLines.length - 1
            }
            if (currentDiff != null) {
                currentDiff.endText = "";
                currentDiff.endLineNum = currentLineNum;
                $scope.selectedDiffs.push(currentDiff);
            }
            $scope.singleLine = function(diff) {
                return diff.startLineNum == diff.endLineNum;
            };
        }
    }
});
