angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchSummaryApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches/summary", {datasource: '@datasource'});

    // TODO: Total counts for each content type - requires mapping from refType to contentType.
    // TODO: resolved counts
    function MismatchSummary(openCount, newCount, existingCount) {
        this.openCount = openCount;
        this.newCount = newCount;
        this.existingCount = existingCount;
    }

    // TODO: add date as parameter
    function get(datasource) {
        return mismatchSummaryApi.get({datasource: datasource}).$promise
            .then(createSummary)
    }

    function createSummary(response) {
        var openCount = 0;
        var newCount = 0;
        var existingCount = 0;
        console.log(response);
        angular.forEach(response.result.summaryMap, function (refType) {
            openCount += refType.openMismatches;
            newCount += refType.mismatchStatuses.NEW || 0;
            existingCount += refType.mismatchStatuses.EXISTING || 0;
        });
        return new MismatchSummary(openCount, newCount, existingCount);
    }

    return {
        get: get
    }
}
