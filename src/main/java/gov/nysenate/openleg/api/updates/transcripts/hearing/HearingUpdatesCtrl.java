package gov.nysenate.openleg.api.updates.transcripts.hearing;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDao;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class HearingUpdatesCtrl extends BaseCtrl {
    private final HearingDao hearingDao;

    @Autowired
    public HearingUpdatesCtrl(HearingDao hearingDao) {
        this.hearingDao = hearingDao;
    }

    /**
     * Hearing Updates API
     * ----------------------
     *
     * Returns a List of Hearing ids that have been inserted or updated on or after the supplied date.
     *
     * Usages:
     * (GET) /api/3/hearings/updates/             (last 7 days)
     * (GET) /api/3/hearings/updates/{from}       (from date to now)
     * (GET) /api/3/hearings/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params:  limit - Limit the number of resultList
     *                  offset - Start resultList from an offset.
     *
     * Expected Output: List of HearingUpdateTokenView
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getNewRecentHearings(WebRequest request) {
        return getNewHearingsDuring(LocalDateTime.now().minusDays(7), DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getNewHearingsSince(@PathVariable String from, WebRequest request) {
        return getNewHearingsDuring(parseISODateTime(from, "from"), DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getNewHearingsDuring(@PathVariable(required = false) String from,
                                             @PathVariable(required = false) String to,
                                             WebRequest request) {
        return getNewHearingsDuring(parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /** --- Internal Methods --- */

    private BaseResponse getNewHearingsDuring(LocalDateTime from, LocalDateTime to, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 25);
        Range<LocalDateTime> dateRange = getClosedOpenRange(from, to, "from", "to");
        PaginatedList<HearingUpdateToken> updates = hearingDao.hearingsUpdatedDuring(dateRange, SortOrder.ASC, limOff);
        return ListViewResponse.of(updates.results().stream()
                .map(r -> new HearingUpdateTokenView(r, hearingDao.getFilename(r.hearingId())))
                .toList(), updates.total(), limOff);
    }
}
