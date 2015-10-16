package gov.nysenate.openleg.controller.api.calendar;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.DateRangeListViewResponse;
import gov.nysenate.openleg.client.view.calendar.CalendarIdView;
import gov.nysenate.openleg.client.view.updates.UpdateDigestView;
import gov.nysenate.openleg.client.view.updates.UpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.data.CalendarUpdatesDao;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class CalendarUpdatesCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(CalendarUpdatesCtrl.class);

    @Autowired protected CalendarUpdatesDao calendarUpdatesDao;
    @Autowired protected CalendarDataService calendarDataService;

    /**
     * Updated Calendars API
     * ---------------------
     *
     * Return a list of calendar ids that have changed during a specified date/time range.
     * Usages:
     * (GET) /api/3/calendar/updates/             (last 7 days)
     * (GET) /api/3/calendar/updates/{from}       (from date to now)
     * (GET) /api/3/calendar/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *                 type (string) - Update type (processed, published) Default: published
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Output: List of UpdateTokenView<CalendarId> or UpdateDigestView<CalendarId> if detail = true.
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getUpdatesDuring(@RequestParam(defaultValue = "false") boolean detail,
                                         WebRequest webRequest) {
        return getUpdatesDuring(LocalDateTime.now().minusDays(7).toString(), LocalDateTime.now().toString(), detail, webRequest);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesDuring(@PathVariable String from,
                                         @RequestParam(defaultValue = "false") boolean detail,
                                         WebRequest webRequest) {
        return getUpdatesDuring(from, LocalDateTime.now().toString(), detail, webRequest);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesDuring(@PathVariable String from, @PathVariable String to,
                                         @RequestParam(defaultValue = "false") boolean detail,
                                         WebRequest webRequest) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        Range<LocalDateTime> updateRange = getOpenRange(fromDateTime, toDateTime, "from", "to");
        SortOrder dateOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        UpdateType updateType = getUpdateTypeFromParam(webRequest);
        BaseResponse response;
        if (!detail) {
            PaginatedList<UpdateToken<CalendarId>> updateTokens =
                calendarUpdatesDao.getUpdates(updateType, updateRange, dateOrder, limitOffset);
            response = DateRangeListViewResponse.of(
                updateTokens.getResults().stream()
                    .map(token -> new UpdateTokenView(token, new CalendarIdView(token.getId())))
                    .collect(Collectors.toList()),
                updateRange, updateTokens.getTotal(), updateTokens.getLimOff()
            );
        }
        else {
            PaginatedList<UpdateDigest<CalendarId>> updateDigests =
                calendarUpdatesDao.getDetailedUpdates(updateType, updateRange, dateOrder, limitOffset);
            response = DateRangeListViewResponse.of(
                updateDigests.getResults().stream()
                        .map(digest -> new UpdateDigestView(digest, new CalendarIdView(digest.getId())))
                        .collect(Collectors.toList()),
                updateRange, updateDigests.getTotal(), updateDigests.getLimOff()
            );
        }
        return response;
    }

    /**
     * Calendar Update Digests API
     * ---------------------------
     *
     * Get all updates for a specific calendar:
     * Usages:
     * (GET) /api/3/calendars/{year}/{calendarNo}/updates/
     * (GET) /api/3/calendars/{year}/{calendarNo}/updates/{from}
     * (GET) /api/3/calendars/{year}/{calendarNo}/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request parameters:  order - The sort order of the update response (orderd by published date) (default DESC)
     *                      limit, offset - Paginate
     */
    @RequestMapping(value = "/{year:[\\d]{4}}/{calendarNo:\\d+}/updates")
    public BaseResponse getUpdatesForCalendar(@PathVariable int year, @PathVariable int calendarNo, WebRequest webRequest) {
        return getUpdatesForCalendarDuring(year, calendarNo,
                DateUtils.LONG_AGO.atStartOfDay().toString(), LocalDateTime.now().toString(), webRequest);
    }

    @RequestMapping(value = "/{year:[\\d]{4}}/{calendarNo:\\d+}/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesForCalendar(@PathVariable int year, @PathVariable int calendarNo, @PathVariable String from,
                                              WebRequest webRequest) {
        return getUpdatesForCalendarDuring(year, calendarNo, from, LocalDateTime.now().toString(), webRequest);
    }

    @RequestMapping(value = "/{year:[\\d]{4}}/{calendarNo:\\d+}/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesForCalendarDuring(@PathVariable int year, @PathVariable int calendarNo,
                                                    @PathVariable String from, @PathVariable String to,
                                                    WebRequest webRequest) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        Range<LocalDateTime> updateRange = getOpenRange(fromDateTime, toDateTime, "from", "to");
        SortOrder dateOrder = getSortOrder(webRequest, SortOrder.ASC);
        UpdateType updateType = getUpdateTypeFromParam(webRequest);
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        PaginatedList<UpdateDigest<CalendarId>> updateDigests =
            calendarUpdatesDao.getDetailedUpdatesForCalendar(updateType, new CalendarId(calendarNo, year),
                updateRange, dateOrder, limitOffset);
        return DateRangeListViewResponse.of(
            updateDigests.getResults().stream()
                .map(digest -> new UpdateDigestView(digest, new CalendarIdView(digest.getId())))
                    .collect(Collectors.toList()),
            updateDigests.getTotal(), LimitOffset.ALL
        );
    }
}
