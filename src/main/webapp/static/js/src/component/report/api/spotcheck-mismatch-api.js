angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'YYYY-MM-DD h:mm:ss a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches", {datasource: '@datasource'});

    function CalendarMismatch(status, date, mismatchType, calType, calNum, issue, source) {
        this.status = status;
        this.date = date;
        this.mismatchType = mismatchType;
        this.calType = calType;
        this.calNum = calNum;
        this.issue = issue;
        this.source = source;
    }

    // TODO: date range, limit offset
    function getBills(datasource, statuses) {
        // TODO: Add mismatchStatus array to API params.
        return mismatchApi.get({datasource: datasource, contentType: 'BILL', limit: 100}).$promise
            .then(createBillMismatches);
    }

    function getCalendars(datasource, statuses) {
        // TODO: Add mismatchStatus array to API params.
        return mismatchApi.get({datasource: datasource, contentType: 'CALENDAR', limit: 100}).$promise
            .then(createCalendarMismatches);
    }

    function createBillMismatches(response) {
        var mismatches = [];
        angular.forEach(response.observations, function (observation) {
            var bill = observation.key.printNo;
            var date = moment(observation.reportId.referenceDateTime).format(DATE_FORMAT);
            var source = referenceTypeMap[observation.reportId.referenceType];
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
                        source: source
                    });
                }
            });
        });
        return mismatches;
    }

    function createCalendarMismatches(response) {
        // TODO: API needs fixing.
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
