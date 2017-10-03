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
    var contentTypeUrlFns = {
        'AGENDA': getAgendaUrl,
        'BILL': getBillUrl,
        'CALENDAR': getCalendarUrl
    };

    return function(key, contentType) {
        if (contentTypeUrlFns.hasOwnProperty(contentType)) {
            return contentTypeUrlFns[contentType](key);
        }
        return "";
    };

    function getAgendaUrl(key) {
        if (key.agendaId.year > 0) {
            return ctxPath + "/agendas/" + key.agendaId.year + "/" + key.agendaId.number + "?comm=" + key.committeeId.name;
        }
        return "";
    }
    function getBillUrl(key) {
        var url = ctxPath + "/bills/" + key.session.year + "/" + key.basePrintNo;
        if (key.hasOwnProperty('version')) {
            url += '?version=' + key.version;
        }
        return url;
    }
    function getCalendarUrl(key) {
        var url = ctxPath + "/calendars/" +  key.year + "/" + key.calNo;
        if (key.hasOwnProperty('type')) {
            switch (key.type) {
                case 'ACTIVE_LIST':
                    url += '?view=active-list';
                    break;
                case 'FLOOR_CALENDAR':
                case 'SUPPLEMENTAL_CALENDAR':
                    url += '?view=floor';
                    break;
            }
        }
        return url;
    }
});

spotcheckModule.filter('referenceUrl', function() {
    // multi-map of url generating functions by datasource and content type
    var refUrlFns = {
        LBDC: {
            BILL: getLrsBillUrl
        },
        NYSENATE: {
            AGENDA: getSenSiteAgendaUrl,
            BILL: getSenSiteBillUrl,
            CALENDAR: getSenSiteCalendarUrl
        }
    };

    return function(key, datasource, contentType) {
        // Get a url function for the given datasource and content type, if it exists
        var refUrlFn = (refUrlFns[datasource] || {})[contentType];
        if (refUrlFn) {
            return refUrlFn(key);
        }
        return null;
    };

    function getLrsBillUrl(key) {
        return "http://public.leginfo.state.ny.us/navigate.cgi" +
            "?NVDTO:=&QUERYTYPE=BILLNO&CBTEXT=Y&CBSPONMEMO=Y" +
            "&SESSYR=" + key.session.year +
            "&QUERYDATA=" + key.printNo;
    }

    function getSenSiteAgendaUrl(key) {
        // TODO Need meeting date time to create link.
        // Example: https://www.nysenate.gov/calendar/meetings/codes/january-23-2017/codes-meeting
        // return senSitePath + "/calendar/meetings/" + key.committeeId.name + "/";
        return null;
    }

    function getSenSiteBillUrl(key) {
        var billType = "bills";
        if (!/^[SA]/i.test(key.printNo)) {
            billType = "resolutions";
        }
        return senSitePath + "/legislation/" + billType + "/" + key.session.year + "/" + key.printNo;
    }

    function getSenSiteCalendarUrl(key) {
        // TODO Need session date time to create link.
        // Example: https://www.nysenate.gov/calendar/sessions/june-05-2017/session-6-5-17
        return null;
    }
});
