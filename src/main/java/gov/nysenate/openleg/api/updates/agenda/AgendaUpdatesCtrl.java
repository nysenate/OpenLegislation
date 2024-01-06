package gov.nysenate.openleg.api.updates.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaIdView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.DateRangeListViewResponse;
import gov.nysenate.openleg.api.updates.view.UpdateDigestView;
import gov.nysenate.openleg.api.updates.view.UpdateTokenView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateToken;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.agenda.AgendaUpdatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Agenda Updates API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/agendas", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class AgendaUpdatesCtrl extends BaseCtrl {
    private final AgendaUpdatesDao agendaUpdatesDao;

    @Autowired
    public AgendaUpdatesCtrl(AgendaUpdatesDao agendaUpdatesDao) {
        this.agendaUpdatesDao = agendaUpdatesDao;
    }

    /**
     * Updated Agendas API
     * -------------------
     *
     * Return a list of agenda ids that have changed during a specified date/time range.
     * Usages:
     * (GET) /api/3/agendas/updates/             (last 7 days)
     * (GET) /api/3/agendas/updates/{from}       (from date to now)
     * (GET) /api/3/agendas/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *                 type (string) - Update type (processed, published) Default: processed
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Output: List of UpdateTokenView<AgendaId> or UpdateDigestView<AgendaId> if detail = true.
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getRecentUpdates(WebRequest request) {
        return getUpdatesDuring(LocalDate.now().minusDays(7).atStartOfDay(), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesFrom(@PathVariable String from, WebRequest request) {
        return getUpdatesDuring(parseISODateTime(from, "from"), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesDuring(@PathVariable String from, @PathVariable String to, WebRequest request) {
        return getUpdatesDuring(parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /**
     * Agenda Update Digests API
     * -------------------------
     *
     * Return update digests for an agenda during a given date/time range
     * Usages:
     * (GET) /api/3/agendas/{year}/{agendaNo}/updates
     * (GET) /api/3/agendas/{year}/{agendaNo}/updates/{from}
     * (GET) /api/3/agendas/{year}/{agendaNo}/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params: type (string) - Update type (processed, published) Default: processed
     *                 limit, offset (int) - Paginate
     *
     * Expected Output: List of UpdateDigestView<BaseBillId>
     */

    @RequestMapping(value = "/{year:\\d{4}}/{agendaNo}/updates")
    public BaseResponse getUpdatesForBill(@PathVariable int year, @PathVariable int agendaNo, WebRequest request) {
        return getUpdatesForAgendaDuring(new AgendaId(agendaNo, year), DateUtils.LONG_AGO,
            LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{year:\\d{4}}/{agendaNo}/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesForBill(@PathVariable int year, @PathVariable int agendaNo, @PathVariable String from,
                                          WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getUpdatesForAgendaDuring(new AgendaId(agendaNo, year), fromDateTime, LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{year:\\d{4}}/{agendaNo}/updates/{from}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesForBillDuring(@PathVariable int year, @PathVariable int agendaNo, @PathVariable String from,
                                                @PathVariable String to, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        return getUpdatesForAgendaDuring(new AgendaId(agendaNo, year), fromDateTime, toDateTime, request);
    }

    /** --- Internal --- */

    private BaseResponse getUpdatesDuring(LocalDateTime from, LocalDateTime to, WebRequest request) {
        // Fetch params
        LimitOffset limOff = getLimitOffset(request, 50);
        Range<LocalDateTime> updateRange = getOpenClosedRange(from, to, "from", "to");
        boolean detail = getBooleanParam(request, "detail", false);
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        UpdateType updateType = getUpdateTypeFromParam(request);

        if (!detail) {
            PaginatedList<UpdateToken<AgendaId>> updateTokens =
                agendaUpdatesDao.getUpdates(updateRange, updateType, sortOrder, limOff);
            return DateRangeListViewResponse.of(updateTokens.results().stream()
                .map(token -> new UpdateTokenView(token, new AgendaIdView(token.getId())))
                .toList(), updateRange, updateTokens.total(), limOff);
        }
        else {
            PaginatedList<UpdateDigest<AgendaId>> updateDigests =
                agendaUpdatesDao.getDetailedUpdates(updateRange, updateType, sortOrder, limOff);
            return DateRangeListViewResponse.of(updateDigests.results().stream()
                .map(digest -> new UpdateDigestView(digest, new AgendaIdView(digest.getId())))
                .toList(), updateRange, updateDigests.total(), limOff);
        }
    }

    private BaseResponse getUpdatesForAgendaDuring(AgendaId agendaId, LocalDateTime from, LocalDateTime to, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 50);
        Range<LocalDateTime> updateRange = getOpenClosedRange(from, to, "from", "to");
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        UpdateType updateType = getUpdateTypeFromParam(request);

        PaginatedList<UpdateDigest<AgendaId>> digests = agendaUpdatesDao.getDetailedUpdatesForAgenda(
            agendaId, updateRange, updateType, sortOrder, limOff);
        return DateRangeListViewResponse.of(digests.results().stream()
            .map(digest -> new UpdateDigestView(digest, new AgendaIdView(digest.getId())))
                .toList(), updateRange, digests.total(), limOff);
    }
}
