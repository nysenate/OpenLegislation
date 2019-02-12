angular.module('open.spotcheck').factory('SpotcheckMismatchApi', ['$resource', spotcheckMismatchApi]);

function spotcheckMismatchApi($resource) {

    const DATE_FORMAT = 'M-D-YY h:mm a';
    var mismatchApi = $resource(adminApiPath + "/spotcheck/mismatches");

    function getMismatches(datasource, contentType, mismatchStatuses, mismatchType, reportDate, limit, offset, orderBy, sort) {
        var params = {
            datasource: datasource,
            contentType: contentType,
            mismatchStatus: mismatchStatuses,
            mismatchType:mismatchType,
            reportDate:reportDate,
            limit: limit,
            offset: offset,
            orderBy: orderBy,
            sort: sort
        };
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
            key: mismatch.key,
            contentType: mismatch.contentType,
            status: parseStatus(mismatch),
            datasource: mismatch.dataSource,
            mismatchType: parseMismatchType(mismatch),
            observedDate: parseObservedDate(mismatch),
            referenceDate: parseReferenceDate(mismatch),
            reportDate: parseReportDate(mismatch),
            issue: parseIssues(mismatch),
            issueInput: parseIssues(mismatch),
            refTypeLabel: parseRefTypeLabel(mismatch),
            refType: parseRefType(mismatch),
            printNo: printNo(mismatch),
            billId: parseBillId(mismatch),
            calNo: parseCalNo(mismatch),
            calType:parseCalType(mismatch),
            session: parseSession(mismatch),
            year: mismatch.key.year || '',
            basePrintNo: parseBasePrintNo(mismatch),
            referenceData: parseReferenceData(mismatch),
            observedData: parseObserveredData(mismatch),
            diffLoading: false,
            agendaNo: parseAgendaNo(mismatch),
            weekOf: mismatch.key.weekOf || "",
            weekOfDisp: parseWeekOfDisp(mismatch),
            committee: parseCommitteeAddendum(mismatch)
        }
    }

    function parseCalType(mismatch) {
        if (mismatch.key.type != undefined){
            var result = "";
            if (mismatch.key.type == "ACTIVE_LIST")
                result = "Active";
            if (mismatch.key.type == "FLOOR_CALENDAR")
                result = "Floor";
            if (mismatch.key.type == "SUPPLEMENTAL_CALENDAR")
                result = "Suppl";
            return result;
        }
    }

    function parseSession(mismatch) {
        return (mismatch.key.session || {}).year || "";
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
        return patternWords(mismatch.status);
    }

    function parseObserveredData(mismatch) {
        return mismatch.observedData;
    }

    function parseMismatchType(mismatch) {
        return mismatch.mismatchType;
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

    function parseRefTypeLabel(mismatch) {
        return referenceTypeDisplayMap[mismatch.referenceType];
    }

    function parseRefType(mismatch) {
        return mismatch.referenceType;
    }

    function printNo(mismatch) {
        return mismatch.key.printNo || "";
    }

    function parseBillId(mismatch) {
        return printNo(mismatch) + '-' + parseSession(mismatch);
    }

    function parseCalNo(mismatch) {
        var calNo = mismatch.key.calNo;
        if(!calNo) {
            return "";
        }

        switch (mismatch.key.type) {
            case 'FLOOR_CALENDAR':
                return calNo + 'Floor';
            case 'SUPPLEMENTAL_CALENDAR':
                return calNo + mismatch.key.version;
            case 'ACTIVE_LIST':
                return calNo + '-' + mismatch.key.sequenceNo;
            default:
                console.error('Could not parse calendar no for mismatch:', mismatch);
                return 'Error';
        }

    }

    function parseAgendaNo(mismatch) {
        if (!mismatch.key.agendaId) {
            return "";
        }
        return mismatch.key.agendaId.number;
    }

    function parseWeekOfDisp(mismatch) {
        var date = moment(mismatch.key.weekOf, 'YYYY-MM-DD');
        if (!mismatch.key.weekOf || !date.isValid()) {
            return ""
        }
        return date.format("M/D/YY");
    }

    function parseCommitteeAddendum(mismatch) {
        if (!mismatch.key.committeeId || !mismatch.key.committeeId.name) {
            return "";
        }

        var committee = mismatch.key.committeeId.name || "";
        var addendum = mismatch.key.addendum || "";
        // Add addendum label if unoriginal
        if (addendum && addendum !== "ORIGINAL") {
            committee += " - " + addendum;
        }
        return committee;
    }

    return {
        getMismatches: getMismatches
    };

    function patternWords(input) {
        var lower =  input.toLowerCase();
        return lower.charAt(0).toUpperCase() + lower.slice(1);
    }
}
