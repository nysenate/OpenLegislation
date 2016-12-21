angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

/**
 * Calls the open-mismatch summary API for a given datasource.
 * Returns a value object containing the total open, new, and resolved counts.
 */
function mismatchSummaryApi($resource) {

    var mismatchSummaryApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches/summary", {datasource: '@datasource'});

    // TODO: resolved counts
    function MismatchSummary(openCount, newCount) {
        this.openCount = openCount;
        this.newCount = newCount;
    }

    // TODO: add date as parameter
    function get(datasource) {
        return mismatchSummaryApi.get({datasource: datasource}).$promise
            .then(createSummary)
    }

    function createSummary(response) {
        var openCount = 0;
        var newCount = 0;
        console.log(response);
        angular.forEach(response.result.summaryMap, function (refType) {
            openCount += refType.openMismatches;
            newCount += refType.mismatchStatuses.NEW || 0;
        });
        return new MismatchSummary(openCount, newCount);
    }

    return {
        get: get
    }
}
