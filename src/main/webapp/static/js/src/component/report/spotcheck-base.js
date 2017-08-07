var spotcheckModule = angular.module('open.spotcheck', ['open.core', 'smart-table', 'diff-match-patch']);

spotcheckModule.factory('SpotcheckMismatchIgnoreAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + "/spotcheck/mismatches/:mismatchId/ignore", {
        mismatchId: '@mismatchId',
        ignoreLevel: '@ignoreLevel'
    });
}]);

spotcheckModule.factory('SpotcheckMismatchTrackingAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + "/spotcheck/mismatches/:mismatchId/issue/:issueId", {
        mismatchId: '@mismatchId',
        issueId: '@issueId'
    });
}]);
// Delete all issues corresponding to the given mismatch
spotcheckModule.factory('SpotcheckMismatchDeleteAllAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + "/spotcheck/mismatch/:mismatchId/delete", {
        mismatchId: '@mismatchId'
    });
}]);

spotcheckModule.filter('contentType', function() {
    var contentTypeMap = {
        LBDC_AGENDA_ALERT: "Agenda",
        LBDC_DAYBREAK: "Bill",
        LBDC_CALENDAR_ALERT: "Floor Cal",
        LBDC_SCRAPED_BILL: "Bill",
        SENATE_SITE_BILLS: "Bill",
        SENATE_SITE_CALENDAR: "Calendar"
    };
    return function(reportType) {
        if (contentTypeMap.hasOwnProperty(reportType)) {
            return contentTypeMap[reportType];
        }
        return "Content";
    };
});

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

spotcheckModule.filter('contentUrl', function() {
    var contentTypeUrlMap = {
        LBDC_DAYBREAK: getBaseBillUrl,
        LBDC_SCRAPED_BILL: getBaseBillUrl,
        SENATE_SITE_BILLS: getBillAmendmentUrl,
        LBDC_AGENDA_ALERT: getAgendaUrl,
        LBDC_CALENDAR_ALERT: getCalendarUrl,
        SENATE_SITE_CALENDAR: getCalListUrl
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

    function getCalListUrl(key){
        return getCalendarUrl(key)+ (key.type==='ALL' ? "?view=" + (key.type==='ACTIVE_LIST' ? "active-list": "floor") : "");
    }
    return function(key, reportType) {
        if (contentTypeUrlMap.hasOwnProperty(reportType)) {
            return contentTypeUrlMap[reportType](key);
        }
        return "";
    }
});
