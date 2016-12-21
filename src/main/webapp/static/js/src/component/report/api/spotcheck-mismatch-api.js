angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    var mismatchApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches", {datasource: '@datasource'});

    function BillMismatch(status, bill, type, date, issue, source) {
        this.status = status;
        this.bill = bill;
        this.type = type;
        this.date = date;
        this.issue = issue;
        this.status = source;
    }

    // TODO: Calendar and Agenda Mismatches

    function getBills(datasource) {
        // TODO: date range, limit offset
        return mismatchApi.get({datasource: datasource, contentType: 'BILL', limit: 100}).$promise
            .then(createBillMismatches);
    }

    function createBillMismatches(response) {
        var mismatches = [];
        angular.forEach(response.observations, function (observation) {
            mismatches = mismatches.concat(mismatchesInObservation(observation));
        });
        return mismatches;
    }

    function mismatchesInObservation(observation) {
        var mismatches = [];
        var bill = observation.key.printNo;
        var date = observation.observedDateTime; // TODO formatting.
        angular.forEach(observation.mismatches.items, function (mismatch) {
            if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                var status = mismatch.status;
                var type = mismatch.mismatchType;
                var issue = extractIssues(mismatch.issueIds.items);
                var source = "Stub Reference Type"; // TODO need source from somewhere.
                mismatches.push(new BillMismatch(status, bill, type, date, issue, source));
            }
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
        getBills: getBills
    }
}
