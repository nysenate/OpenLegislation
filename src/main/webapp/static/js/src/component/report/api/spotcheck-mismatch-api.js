angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'YYYY-MM-DD h:mm:ss a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches", {datasource: '@datasource'});

    // TODO: date range, limit offset
    function getBills(datasource, statuses, limit, offset) {
        // TODO: Add mismatchStatus array to API params.
        var params = {
            datasource: datasource,
            contentType: 'BILL',
            limit: limit,
            offset: offset
        };
        return mismatchApi.get(params).$promise
            .then(createBillMismatches);
    }

    function getCalendars(datasource, statuses, limit, offset) {
        // TODO: Add mismatchStatus array to API params.
        var params = {
            datasource: datasource,
            contentType: 'CALENDAR',
            limit: limit,
            offset: offset
        };
        return mismatchApi.get(params).$promise
            .then(createCalendarMismatches);
    }

    function createBillMismatches(response) {
        var results = {
            pagination: {
                total: 0
            },
            mismatches: []
        };
        results.pagination.total = response.total;
        angular.forEach(response.observations, function (observation) {
            var bill = observation.key.printNo;
            var date = moment(observation.reportId.referenceDateTime).format(DATE_FORMAT);
            var refType = referenceTypeMap[observation.reportId.referenceType];
            angular.forEach(observation.mismatches.items, function (mismatch) {
                if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                    var status = mismatch.status;
                    var mismatchType = mismatchMap[mismatch.mismatchType];
                    var issue = extractIssues(mismatch.issueIds.items);
                    results.mismatches.push({
                        status: status,
                        bill: bill,
                        mismatchType: mismatchType,
                        date: date,
                        issue: issue,
                        refType: refType
                    });
                }
            });
        });
        return results;
    }

    function createCalendarMismatches(response) {
        var results = {
            pagination: {
                total: 0
            },
            mismatches: []
        };
        results.pagination.total = response.total;
        angular.forEach(response.observations, function (observation) {
            var calNo = observation.key.calNo;
            var date = moment(observation.refDateTime).format(DATE_FORMAT);
            // TODO: Add refType to API response.
            var refType = "Stub refType";
            angular.forEach(observation.mismatches.items, function (mismatch) {
                if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                    var status = mismatch.status;
                    var mismatchType = mismatchMap[mismatch.mismatchType];
                    var issue = extractIssues(mismatch.issueIds.items);
                    results.mismatches.push({
                        status: status,
                        mismatchType: mismatchType,
                        date: date,
                        calNo: calNo,
                        calType: "Stub Type", // TODO Need calType in API response.
                        issue: issue,
                        refType: refType
                    });
                }
            });
        });
        return results;
    }

    /**
     * @param issues an array of strings containing issue numbers.
     */
    function extractIssues(issues) {
        return issues.join(', ')
    }

    return {
        getBills: getBills,
        getCalendars: getCalendars
    }
}
