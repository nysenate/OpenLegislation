angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchStatusSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/status");
    var mismatchTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/mismatchtype");
    var mismatchContentTypeSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary/contenttype");

    /**
     * @param reportDate An ISO date time string. Returns summary data for mismatches observed before this date time.
     * @param datasource datasource filter
     * @param contentType content type filter
     */
    function getMismatchStatusSummary(reportDate, datasource, contentType) {
        var params = {
            reportDate: reportDate,
            datasource: datasource,
            contentType: contentType
        };
        return mismatchStatusSummaryApi.get(params).$promise
            .then(createStatusSummary)
    }

    function getMismatchTypeSummary(reportDate, datasource, contentType, mismatchStatus) {
        var params = {
            reportDate: reportDate,
            datasource: datasource,
            contentType: contentType,
            mismatchStatus: mismatchStatus
        };
        return mismatchTypeSummaryApi.get(params).$promise
            .then(createMismatchTypeSummary)
    }

    function getMismatchContentTypeSummary(reportDate, datasource) {
        var params = {
            datasource: datasource,
            reportDate: reportDate
        };
        return mismatchContentTypeSummaryApi.get(params).$promise
            .then(createContentTypeSummary)
    }

    function createStatusSummary(response) {
        return response.result;
    }

    function createMismatchTypeSummary(response) {
        return response.result.typeCount.items;
    }

    function createContentTypeSummary(response) {
        return response.result;
    }

    return {
        getMismatchStatusSummary: getMismatchStatusSummary,
        getMismatchTypeSummary: getMismatchTypeSummary,
        getMismatchContentTypeSummary: getMismatchContentTypeSummary
    }
}
