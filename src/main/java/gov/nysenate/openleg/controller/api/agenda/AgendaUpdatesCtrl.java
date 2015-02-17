package gov.nysenate.openleg.controller.api.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.DateRangeListViewResponse;
import gov.nysenate.openleg.client.view.agenda.AgendaIdView;
import gov.nysenate.openleg.client.view.updates.UpdateDigestView;
import gov.nysenate.openleg.client.view.updates.UpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.agenda.data.AgendaUpdatesDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Agenda Updates API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/agendas", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class AgendaUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaUpdatesCtrl.class);

    @Autowired protected AgendaUpdatesDao agendaUpdatesDao;

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
     *                 type (string) - Update type (processed, published) Default: published
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Output: List of UpdateTokenView<AgendaId> or UpdateDigestView<AgendaId> if detail = true.
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getRecentUpdates(WebRequest request) {
        return getUpdatesDuring(LocalDate.now().minusDays(7).atStartOfDay(), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from}")
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
     * Request Params: type (string) - Update type (processed, published) Default: published
     *                 limit, offset (int) - Paginate
     *
     * Expected Output: List of UpdateDigestView<BaseBillId>
     */

    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}/updates")
    public BaseResponse getUpdatesForBill(@PathVariable int year, @PathVariable int agendaNo, WebRequest request) {
        return getUpdatesForAgendaDuring(new AgendaId(agendaNo, year), DateUtils.LONG_AGO.atStartOfDay(),
            LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}/updates/{from}")
    public BaseResponse getUpdatesForBill(@PathVariable int year, @PathVariable int agendaNo, @PathVariable String from,
                                          WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getUpdatesForAgendaDuring(new AgendaId(agendaNo, year), fromDateTime, LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}/updates/{from}/{to:.*\\.?.*}")
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
        Range<LocalDateTime> updateRange = getClosedOpenRange(from, to, "from", "to");
        boolean detail = getBooleanParam(request, "detail", false);
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        UpdateType updateType = getUpdateTypeFromParam(request);

        if (!detail) {
            PaginatedList<UpdateToken<AgendaId>> updateTokens =
                agendaUpdatesDao.getUpdates(updateRange, updateType, sortOrder, limOff);
            return DateRangeListViewResponse.of(updateTokens.getResults().stream()
                .map(token -> new UpdateTokenView(token, new AgendaIdView(token.getId())))
                .collect(toList()), updateRange, updateTokens.getTotal(), limOff);
        }
        else {
            PaginatedList<UpdateDigest<AgendaId>> updateDigests =
                agendaUpdatesDao.getDetailedUpdates(updateRange, updateType, sortOrder, limOff);
            return DateRangeListViewResponse.of(updateDigests.getResults().stream()
                .map(digest -> new UpdateDigestView(digest, new AgendaIdView(digest.getId())))
                .collect(toList()), updateRange, updateDigests.getTotal(), limOff);
        }
    }

    private BaseResponse getUpdatesForAgendaDuring(AgendaId agendaId, LocalDateTime from, LocalDateTime to, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 50);
        Range<LocalDateTime> updateRange = getClosedOpenRange(from, to, "from", "to");
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        UpdateType updateType = getUpdateTypeFromParam(request);

        PaginatedList<UpdateDigest<AgendaId>> digests = agendaUpdatesDao.getDetailedUpdatesForAgenda(
            agendaId, updateRange, updateType, sortOrder, limOff);
        return DateRangeListViewResponse.of(digests.getResults().stream()
            .map(digest -> new UpdateDigestView(digest, new AgendaIdView(digest.getId())))
            .collect(toList()), updateRange, digests.getTotal(), limOff);
    }
}
