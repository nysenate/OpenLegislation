angular.module('open.spotcheck').factory('SpotcheckMismatchSummaryApi',
    ['$resource', mismatchSummaryApi]);

function mismatchSummaryApi($resource) {

    var mismatchSummaryApi = $resource(adminApiPath + "/spotcheck/mismatches/summary");

    /**
     * @param datasource
     * @param date An ISO date time string. Returns summary data for mismatches observed before this date time.
     */
    function get(datasource, date) {
        return mismatchSummaryApi.get({datasource: datasource, summaryDateTime : date}).$promise
            .then(createSummary)
    }

    function createSummary(response) {

        var summary = {
            OPEN: 0,
            NEW: 0,
            RESOLVED: 0,
            BILL: {
                OPEN: 0,
                NEW: 0,
                RESOLVED: 0,
            },
            CALENDAR: {
                OPEN: 0,
                NEW: 0,
                RESOLVED: 0,
            },
            AGENDA: {
                OPEN: 0,
                NEW: 0,
                RESOLVED: 0,
            }
        };

        console.log(response);

        try {
            summary.OPEN += response.result.summary.items.REGRESSION.total || 0
        } catch (e) {
        }
        ;
        try {
            summary.OPEN += response.result.summary.items.EXISTING.total || 0
        } catch (e) {
        }
        ;
        try {
            summary.OPEN += response.result.summary.items.NEW.total || 0
        } catch (e) {
        }
        ;
        try {
            summary.NEW += response.result.summary.items.NEW.total || 0
        } catch (e) {
        }
        ;
        try {
            summary.RESOLVED += response.result.summary.items.RESOLVED.total || 0
        } catch (e) {
        }
        ;

        try {
            summary.BILL.OPEN += response.result.summary.items.REGRESSION.contentTypeCounts.items.BILL || 0
        } catch (e) {
        }
        ;
        try {
            summary.BILL.OPEN += response.result.summary.items.EXISTING.contentTypeCounts.items.BILL || 0
        } catch (e) {
        }
        ;
        try {
            summary.BILL.OPEN += response.result.summary.items.NEW.contentTypeCounts.items.BILL || 0
        } catch (e) {
        }
        ;
        try {
            summary.BILL.NEW += response.result.summary.items.NEW.contentTypeCounts.items.BILL || 0
        } catch (e) {
        }
        ;
        try {
            summary.BILL.RESOLVED += response.result.summary.items.RESOLVED.contentTypeCounts.items.BILL || 0
        } catch (e) {
        }
        ;

        try {
            summary.CALENDAR.OPEN += response.result.summary.items.REGRESSION.contentTypeCounts.items.CALENDAR || 0
        } catch (e) {
        }
        ;
        try {
            summary.CALENDAR.OPEN += response.result.summary.items.EXISTING.contentTypeCounts.items.CALENDAR || 0
        } catch (e) {
        }
        ;
        try {
            summary.CALENDAR.OPEN += response.result.summary.items.NEW.contentTypeCounts.items.CALENDAR || 0
        } catch (e) {
        }
        ;
        try {
            summary.CALENDAR.NEW += response.result.summary.items.NEW.contentTypeCounts.items.CALENDAR || 0
        } catch (e) {
        }
        ;
        try {
            summary.CALENDAR.RESOLVED += response.result.summary.items.RESOLVED.contentTypeCounts.items.CALENDAR || 0
        } catch (e) {
        }
        ;

        try {
            summary.AGENDA.OPEN += response.result.summary.items.REGRESSION.contentTypeCounts.items.AGENDA || 0
        } catch (e) {
        }
        ;
        try {
            summary.AGENDA.OPEN += response.result.summary.items.EXISTING.contentTypeCounts.items.AGENDA || 0
        } catch (e) {
        }
        ;
        try {
            summary.AGENDA.OPEN += response.result.summary.items.NEW.contentTypeCounts.items.AGENDA || 0
        } catch (e) {
        }
        ;
        try {
            summary.AGENDA.NEW += response.result.summary.items.NEW.contentTypeCounts.items.AGENDA || 0
        } catch (e) {
        }
        ;
        try {
            summary.AGENDA.RESOLVED += response.result.summary.items.AGENDA.contentTypeCounts.items.AGENDA || 0
        } catch (e) {
        }
        ;
        return summary;
    }

    return {
        get: get
    }
}
