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
