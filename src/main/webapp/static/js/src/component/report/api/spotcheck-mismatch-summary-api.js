angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchStatusSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/status");
    var mismatchTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/mismatchtype");
    var mismatchContentTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/contenttype");

    /**
     * @param datasource datasource filter
     * @param date An ISO date time string. Returns summary data for mismatches observed before this date time.
     */
    function getMismatchStatusSummary(datasource, reportDate) {
        return mismatchStatusSummaryApi.get({datasource: datasource, reportDate : reportDate}).$promise
            .then(createStatusSummary)
    }

    function getMismatchTypeSummary(datasource, reportDate, mismatchStatus) {
        return mismatchTypeSummaryApi.get({datasource: datasource, reportDate : reportDate, mismatchStatus: mismatchStatus}).$promise
            .then(createMismatchTypeSummary)
    }

    function getMismatchContentTypeSummary(datasource, reportDate, mismatchStatus, mismatchType) {
        return mismatchContentTypeSummaryApi.get({datasource: datasource, reportDate : reportDate, mismatchStatus: mismatchStatus, mismatchType:mismatchType}).$promise
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
