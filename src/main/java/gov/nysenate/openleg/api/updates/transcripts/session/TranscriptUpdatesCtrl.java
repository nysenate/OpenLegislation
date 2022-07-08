package gov.nysenate.openleg.api.updates.transcripts.session;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDao;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class TranscriptUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TranscriptUpdatesCtrl.class);

    @Autowired private TranscriptDao transcriptDao;

    /**
     * Transcript Updates API
     * ----------------------
     *
     * Returns a List of Transcript ids that have been inserted or updated on or after the supplied date.
     *
     * Usages:
     * (GET) /api/3/transcripts/updates/             (last 7 days)
     * (GET) /api/3/transcripts/updates/{from}       (from date to now)
     * (GET) /api/3/transcripts/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params:  limit - Limit the number of results
     *                  offset - Start results from an offset.
     *
     * Expected Output: List of TranscriptUpdateTokenView
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getNewRecentTranscripts(WebRequest request) {
        return getNewTranscriptsDuring(LocalDateTime.now().minusDays(7), DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getNewTranscriptsSince(@PathVariable String from, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getNewTranscriptsDuring(fromDateTime, DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getNewTranscriptsDuring(@PathVariable String from,
                                                @PathVariable String to,
                                                WebRequest request) {
        return getNewTranscriptsDuring(parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /** --- Internal Methods --- */

    private BaseResponse getNewTranscriptsDuring(LocalDateTime from, LocalDateTime to, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 25);
        Range<LocalDateTime> range = getOpenRange(from, to, "from", "to");
        PaginatedList<TranscriptUpdateToken> updates = transcriptDao.transcriptsUpdatedDuring(range, SortOrder.ASC, limOff);
        return ListViewResponse.of(updates.getResults().stream()
                .map(TranscriptUpdateTokenView::new)
                .toList(), updates.getTotal(), limOff);
    }
}
