angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'YYYY-MM-DD h:mm:ss a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches", {datasource: '@datasource'});

    // TODO: date range, limit offset
    function getBills(datasource, statuses) {
        // TODO: Add mismatchStatus array to API params.
        return mismatchApi.get({datasource: datasource, contentType: 'BILL', limit: 10}).$promise
            .then(createBillMismatches);
    }

    function getCalendars(datasource, statuses) {
        // TODO: Add mismatchStatus array to API params.
        return mismatchApi.get({datasource: datasource, contentType: 'CALENDAR', limit: 10}).$promise
            .then(createCalendarMismatches);
    }

    function createBillMismatches(response) {
        var mismatches = [];
        angular.forEach(response.observations, function (observation) {
            var bill = observation.key.printNo;
            var date = moment(observation.reportId.referenceDateTime).format(DATE_FORMAT);
            var refType = referenceTypeMap[observation.reportId.referenceType];
            angular.forEach(observation.mismatches.items, function (mismatch) {
                if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                    var status = mismatch.status;
                    var mismatchType = mismatchMap[mismatch.mismatchType];
                    var issue = extractIssues(mismatch.issueIds.items);
                    mismatches.push({
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
        return mismatches;
    }

    function createCalendarMismatches(response) {
        var mismatches = [];
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
                    mismatches.push({
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
        return mismatches;
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
