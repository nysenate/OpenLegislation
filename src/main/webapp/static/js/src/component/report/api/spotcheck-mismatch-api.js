angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'M-D-YY h:mm a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/mismatches");

    function getMismatches(datasource, contentType, mismatchStatuses, fromDate, toDate, limit, offset, orderBy, sort) {
        var params = {
            datasource: datasource,
            contentType: contentType,
            mismatchStatuses: mismatchStatuses,
            limit: limit,
            offset: offset,
            toDate: toDate,
            orderBy: orderBy,
            sort: sort
        };
        // for resolve
        if (mismatchStatuses.indexOf("RESOLVED") != -1)
            params['fromDate'] = fromDate;
        return mismatchApi.get(params).$promise
            .then(parseMismatches);
    }


    function parseMismatches(response) {
        var result = {
            pagination: {
                total: 0
            },
            mismatches: []
        };
        result.pagination.total = response.total;
        angular.forEach(response.result.items, function (mismatch) {
            if (mismatch.ignoreStatus === 'NOT_IGNORED') {
                result.mismatches.push(createMismatch(mismatch));
            }
        });
        return result;
    }

    function createMismatch(mismatch) {
        return {
            id: parseMismatchId(mismatch),
            status: parseStatus(mismatch),
            disableDiff: parseDisableDiff(mismatch),
            mismatchType: parseMismatchType(mismatch),
            observedDate: parseObservedDate(mismatch),
            referenceDate: parseReferenceDate(mismatch),
            reportDate: parseReportDate(mismatch),
            issue: parseIssues(mismatch),
            refType: parseRefType(mismatch),
            bill: parseBill(mismatch),
            source: parseRefType(mismatch),
            calNo: parseCalNo(mismatch),
            calType:parseCalType(mismatch),
            session: parseSession(mismatch),
            basePrintNo: parseBasePrintNo(mismatch),
            referenceData: parseReferenceData(mismatch),
            observedData: parseObserveredData(mismatch),
            diffLoading: false,
            agendaNo: parseAgendaNo(mismatch),
            committee: parseCommittee(mismatch)
        }
    }

    function parseDisableDiff(mismatch) {
        if (mismatch.mismatchType == "REFERENCE_DATA_MISSING" || mismatch.mismatchType == "OBSERVE_DATA_MISSING") {
            return true;
        }
        else{
            return false;
        }
    }

    function parseCalType(mismatch) {
        if (mismatch.key.type != undefined)
            return mismatch.key.type.replace('_',' ');
    }

    function parseSession(mismatch) {
        return mismatch.key.session;
    }

    function parseBasePrintNo(mismatch) {
        return mismatch.key.basePrintNo;
    }

    function parseMismatchId(mismatch) {
        return mismatch.mismatchId;
    }

    function parseReferenceData(mismatch) {
        return mismatch.referenceData;
    }

    function parseStatus(mismatch) {
        return mismatch.status;
    }

    function parseObserveredData(mismatch) {
        return mismatch.observedData;
    }

    function parseMismatchType(mismatch) {
        if (mismatch.mismatchType == "OBSERVE_DATA_MISSING") {
            if (mismatch.source == 'LBDC') {
                if (mismatch.contentType == "BILL")
                    mismatch.mismatchType = 'Missing LBDC Bill';
                if (mismatch.contentType == "CALENDAR")
                    mismatch.mismatchType = 'Missing LBDC Cal';
                if (mismatch.contentType == "AGENDA")
                    mismatch.mismatchType = 'Missing LBDC Agenda';
            }
            else {
                if (mismatch.contentType == "BILL")
                    mismatch.mismatchType = 'Missing OpenLeg  Bill';
                if (mismatch.contentType == "CALENDAR")
                    mismatch.mismatchType = 'Missing OpenLeg  Cal';
                if (mismatch.contentType == "AGENDA")
                    mismatch.mismatchType = 'Missing OpenLeg  Agenda';
            }
            return mismatch.mismatchType;
        }
        else if (mismatch.mismatchType == "REFERENCE_DATA_MISSING") {
            if (mismatch.source == 'LBDC') {
                if (mismatch.contentType == "BILL")
                    mismatch.mismatchType = 'Missing OpenLeg  Bill';
                if (mismatch.contentType == "CALENDAR")
                    mismatch.mismatchType = 'Missing OpenLeg  Cal';
                if (mismatch.contentType == "AGENDA")
                    mismatch.mismatchType = 'Missing OpenLeg  Agenda';
            }
            else {
                if (mismatch.contentType == "BILL")
                    mismatch.mismatchType = 'Missing NYSenate.gov  Bill';
                if (mismatch.contentType == "CALENDAR")
                    mismatch.mismatchType = 'Missing NYSenate.gov  Cal';
                if (mismatch.contentType == "AGENDA")
                    mismatch.mismatchType = 'Missing NYSenate.gov  Agenda';
            }
            return mismatch.mismatchType;
        }
        else {
            return mismatchMap[mismatch.mismatchType];
        }
    }

    function parseReferenceDate(mismatch) {
        return moment(mismatch.referenceDateTime).format(DATE_FORMAT);
    }

    function parseObservedDate(mismatch) {
        return moment(mismatch.observedDateTime).format(DATE_FORMAT);
    }

    function parseReportDate(mismatch) {
        return moment(mismatch.reportDateTime).format(DATE_FORMAT);
    }

    function parseIssues(mismatch) {
        return mismatch.issueIds.items.join(', ')
    }

    function parseRefType(mismatch) {
        return referenceTypeDisplayMap[mismatch.referenceType];
    }

    function parseBill(mismatch) {
        return mismatch.key.printNo || "";
    }

    function parseCalNo(mismatch) {
        return mismatch.key.calNo || "";
    }

    function parseAgendaNo(mismatch) {
        if (mismatch.key.agendaId == undefined || mismatch.key.agendaId == null)
            return "";
        if (mismatch.key.agendaId.number == -1) // if the missing data is the agenda number, we set it to -1
            return "N/A";
        if (mismatch.key.addendum == 'DEFAULT')
            return mismatch.key.agendaId.number;
         return mismatch.key.agendaId.number + mismatch.key.addendum;
    }

    function parseCommittee(mismatch) {
        if (mismatch.key.committeeId == null) {
            return "";
        }
        return mismatch.key.committeeId.name;
    }

    return {
        getMismatches: getMismatches
    }
}
