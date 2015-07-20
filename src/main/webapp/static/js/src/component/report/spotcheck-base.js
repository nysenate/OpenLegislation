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
    return $resource(adminApiPath + "/spotcheck/open-mismatches/:reportType", {
        reportType: '@reportType'
    });
}]);

var reportTypeMap = {};
var mismatchTypeMap = {};

// Returns a formatted label for the given mismatch status
spotcheckModule.filter('mismatchStatusLabel', ['$filter', function ($filter) {
    var statusLabelMap = {
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
        return $filter('label')(type, mismatchTypeMap);
    };
}]);

spotcheckModule.filter('reportTypeLabel', ['$filter', function ($filter) {
    return function(type) {
        return $filter('label')(type, reportTypeMap);
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
        LBDC_ACTIVE_LIST: "Active List",
        LBDC_AGENDA_ALERT: "Agenda",
        LBDC_DAYBREAK: "Bill",
        LBDC_CALENDAR_ALERT: "Floor Cal",
        LBDC_SCRAPED_BILL: "Bill"
    };
    return function(reportType) {
        if (contentTypeMap.hasOwnProperty(reportType)) {
            return contentTypeMap[reportType];
        }
        return "Content";
    };
});

/** --- Parent Spotcheck Controller --- */

spotcheckModule.controller('SpotcheckCtrl', ['$scope', '$routeParams', '$location', '$timeout', '$filter', '$mdDialog',
function ($scope, $routeParams, $location, $timeout, $filter, $mdDialog) {

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
    };

    $scope.getReportURL = function (reportType, reportRunTime) {
        return ctxPath + "/admin/report/spotcheck/" +
            $filter('reportTypeLabel')(reportType) + "/" + $filter('moment')(reportRunTime, "YYYY-MM-DD[T]HH:mm:ss.SSS");
    };

    /**
     * Generates a human readable string representing the given content key
     * @param reportType - The type of report the key is present in
     * @param key - A content key that references a piece of legislative content
     */
    $scope.getContentId = function(reportType, key) {
        if (contentTypeIdMap.hasOwnProperty(reportType)) {
            return contentTypeIdMap[reportType](key);
        }
        return reportType + "?!";
    };
    var contentTypeIdMap = {
        LBDC_DAYBREAK: getBillId,
        LBDC_SCRAPED_BILL: getBillId,
        LBDC_AGENDA_ALERT: getAgendaId,
        LBDC_CALENDAR_ALERT: getCalendarId
    };
    function getBillId(key) {
        return key.basePrintNo;
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

    /**
     * Generates an appropriate URL to view the data referenced by the given content key
     * @param reportType - the type of report that the key is present in
     * @param key - A content key that references a piece of legislative content
     */
    $scope.getContentUrl = function(reportType, key) {
        if (contentTypeUrlMap.hasOwnProperty(reportType)) {
            return contentTypeUrlMap[reportType](key);
        }
        return "";
    };
    var contentTypeUrlMap = {
        LBDC_DAYBREAK: getBillUrl,
        LBDC_SCRAPED_BILL: getBillUrl,
        LBDC_AGENDA_ALERT: getAgendaUrl,
        LBDC_CALENDAR_ALERT: getCalendarUrl
    };
    function getBillUrl(key) {
        return ctxPath + "/bills/" + key.session.year + "/" + key.basePrintNo;
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

    // Searches through the prior mismatches of a mismatch to find the date that it was first opened
    $scope.findFirstOpenedDates = function(mismatch, observation){
        if(mismatch.status == "NEW") {
            return {reportDateTime: observation.observedDateTime, referenceDateTime: observation.refDateTime};
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
                mismatchRows.push({
                    key: obs.key,
                    observation: obs,
                    mismatch: m,
                    type: m.mismatchType,
                    status: m.status,
                    observed: obs.observedDateTime,
                    firstOpened: $scope.findFirstOpenedDates(m, obs).reportDateTime,
                    refData: m.referenceData,
                    obsData: m.observedData,
                    mismatchId: $scope.getMismatchId(obs, m),
                    refType: refType
                });
            });
        });
        return mismatchRows;
    };

    // Triggers a detail sheet popup for the mismatch designated by mismatchId
    $scope.showMismatchDetails = function(mismatchRow, getDetails) {
        $mdDialog.show({
            templateUrl: 'mismatchDetailWindow',
            controller: 'detailDialogCtrl',
            locals: {
                mismatchRow: mismatchRow
            },
            resolve: {
                getDetails: function() { return getDetails},
                findFirstOpenedDates: function() {return $scope.findFirstOpenedDates;},
                getMismatchId: function() { return $scope.getMismatchId; },
                getContentId: function() { return $scope.getContentId;},
                getContentUrl: function() {return $scope.getContentUrl;}
            }
        });
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
                        $scope.lines = elementHeight / $scope.lineHeight;
                        $scope.pre = $scope.lines > 1;
                    }, 50);
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
