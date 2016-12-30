angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'YYYY-MM-DD h:mm:ss a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches", {datasource: '@datasource'});

    function getMismatches(datasource, contentType, statuses, limit, offset) {
        // TODO: date range
        // TODO: filter API by mismatchStatuses
        // TODO API filter to return only non ignored mismatches?
        var params = {
            datasource: datasource,
            contentType: contentType,
            limit: limit,
            offset: offset
        };
        var promise = mismatchApi.get(params).$promise;
        return createMismatches(promise, contentType);
    }

    function createMismatches(mismatchsPromise, contentType) {
        switch (contentType) {
            case 'BILL':
                return mismatchsPromise.then(createBillMismatches);
            case 'CALENDAR':
                return mismatchsPromise.then(createCalendarMismatches);
            case 'AGENDA':
                return mismatchsPromise.then(createAgendaMismatches);
        }
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
            var date = moment(observation.refDateTime).format(DATE_FORMAT);
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
            var refType = referenceTypeMap[observation.reportId.referenceType];
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
                        calType: "", // TODO Need calType in API response.
                        issue: issue,
                        refType: refType
                    });
                }
            });
        });
        return results;
    }

    function createAgendaMismatches(response) {
        var results = {
            pagination: {
                total: 0
            },
            mismatches: []
        };
        results.pagination.total = response.total;
        angular.forEach(response.observations, function (observation) {
            var agendaNo = observation.key.agendaId.number;
            var date = moment(observation.refDateTime).format(DATE_FORMAT);
            var committee = observation.key.committeeId.name;
            var refType = referenceTypeMap[observation.reportId.referenceType];
            angular.forEach(observation.mismatches.items, function (mismatch) {
                if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                    var status = mismatch.status;
                    var mismatchType = mismatchMap[mismatch.mismatchType];
                    var issue = extractIssues(mismatch.issueIds.items);
                    results.mismatches.push({
                        status: status,
                        mismatchType: mismatchType,
                        date: date,
                        agendaNo: agendaNo,
                        committee: committee,
                        issue: issue,
                        refType: refType
                    })
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
        getMismatches: getMismatches
    }
}
