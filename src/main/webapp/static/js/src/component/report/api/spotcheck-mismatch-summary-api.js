angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchSummaryApi = $resource(adminApiPath + "/spotcheck/:datasource/open-mismatches/summary", {datasource: '@datasource'});

    /**
     * @param datasource
     * @param date An ISO date time string. Returns summary data for mismatches observed before this date time.
     */
    function get(datasource, date) {
        return mismatchSummaryApi.get({datasource: datasource, observedBefore: date}).$promise
            .then(createSummary)
    }

    function createSummary(response) {
        console.log(response);
        var summary = {
            NEW: 0,
            EXISTING: 0,
            OPEN: 0,
            BILL: {
                NEW: 0,
                EXISTING: 0,
                OPEN: 0
            },
            CALENDAR: {
                NEW: 0,
                EXISTING: 0,
                OPEN: 0
            },
            AGENDA: {
                NEW: 0,
                EXISTING: 0,
                OPEN: 0
            }
        };
        angular.forEach(response.result.summaryMap, function (refType) {
            summary.NEW += refType.mismatchStatuses.NEW || 0;
            summary.EXISTING += refType.mismatchStatuses.EXISTING || 0;
            summary.OPEN += refType.openMismatches || 0;
            switch (referenceContentTypeMap[refType.refType]) {
                case "BILL":
                    summary.BILL.NEW += refType.mismatchStatuses.NEW || 0;
                    summary.BILL.EXISTING += refType.mismatchStatuses.EXISTING || 0;
                    summary.BILL.OPEN += refType.openMismatches || 0;
                    break;
                case "CALENDAR":
                    summary.CALENDAR.NEW += refType.mismatchStatuses.NEW || 0;
                    summary.CALENDAR.EXISTING += refType.mismatchStatuses.EXISTING || 0;
                    summary.CALENDAR.OPEN += refType.openMismatches || 0;
                    break;
                case "AGENDA":
                    summary.AGENDA.NEW += refType.mismatchStatuses.NEW || 0;
                    summary.AGENDA.EXISTING += refType.mismatchStatuses.EXISTING || 0;
                    summary.AGENDA.OPEN += refType.openMismatches || 0;
                    break;
                default:
                    break;
            }
        });
        return summary;
    }

    return {
        get: get
    }
}
