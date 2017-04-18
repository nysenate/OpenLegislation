angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchStatusSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/status");
    var mismatchTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/type");
    var mismatchContentTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/contenttype");

    /**
     * @param datasource datasource filter
     * @param date An ISO date time string. Returns summary data for mismatches observed before this date time.
     */
    function getMismatchStatusSummary(datasource, date) {
        return mismatchStatusSummaryApi.get({datasource: datasource, summaryDateTime : date}).$promise
            .then(createStatusSummary)
    }

    function getMismatchTypeSummary(datasource, date, status) {
        return mismatchTypeSummaryApi.get({datasource: datasource, summaryDateTime : date, status: status}).$promise
            .then(createMismatchTypeSummary)
    }

    function getMismatchContentTypeSummary(datasource, date, status, type) {
        return mismatchContentTypeSummaryApi.get({datasource: datasource, summaryDateTime : date, status: status, type:type}).$promise
            .then(createContentTypeSummary)
    }

    function createStatusSummary(response) {
        return response.result;
    }
    function createMismatchTypeSummary(response) {
        return response.result;
    }
    function createContentTypeSummary(response) {
        return response.result;
    }

    return {
        getMismatchStatusSummary: getMismatchStatusSummary,
        getMismatchTypeSummary:getMismatchTypeSummary,
        getMismatchContentTypeSummary:getMismatchContentTypeSummary
    }
}
