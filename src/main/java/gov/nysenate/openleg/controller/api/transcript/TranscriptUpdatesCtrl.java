package gov.nysenate.openleg.controller.api.transcript;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.transcript.TranscriptUpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.transcript.TranscriptDao;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
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
        return getNewTranscriptsDuring(LocalDateTime.now().minusDays(7), DateUtils.THE_FUTURE.atStartOfDay(), request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getNewTranscriptsSince(@PathVariable String from, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getNewTranscriptsDuring(fromDateTime, DateUtils.THE_FUTURE.atStartOfDay(), request);
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
                .collect(Collectors.toList()), updates.getTotal(), limOff);
    }
}
