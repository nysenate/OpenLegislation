package gov.nysenate.openleg.api.legislation.calendar;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.calendar.view.*;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarGetCtrl extends BaseCtrl {
    private final CalendarDataService calendarDataService;
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public CalendarGetCtrl(CalendarDataService calendarDataService,
                           CalendarViewFactory calendarViewFactory) {
        this.calendarDataService = calendarDataService;
        this.calendarViewFactory = calendarViewFactory;
    }


    /** --- Request Handlers --- */

    /**
     * Calendar Year API
     *
     * Get all calendars for one year:  (GET) /api/3/calendars/{year}
     * Request Parameters:  full - If true, full calendars will be returned including
     *                              active list and supplemental entries (default false)
     *                      order - Determines if the returned calendars are in ascending(ASC) or descending(DESC)
     *                              order (default ASC)
     *                      limit - Limit the number of results (default 100)
     *                      offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/{year:\\d{4}}")
    public BaseResponse getCalendars(@PathVariable int year,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return ListViewResponse.of(
                calendarDataService.getCalendars(year, sortOrder, limitOffset).stream()
                        .map(full ? calendarViewFactory::getCalendarView : SimpleCalendarView::new)
                        .toList(),
                calendarDataService.getCalendarCount(year), limitOffset);
    }

    /**
     * Active List Year API
     *
     * Get all active list calendars for one year:  (GET) /api/3/calendars/{year}/activelist
     * Request Parameters:  full - If true, full active lists will be returned including all entries (default false)
     *                      order - Determines if the returned calendars are in ascending(ASC) or descending(DESC)
     *                              order (default ASC)
     *                      limit - Limit the number of results (default 100)
     *                      offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/{year:\\d{4}}/activelist")
    public BaseResponse getActiveLists(@PathVariable int year,
                                       @RequestParam(defaultValue = "false") boolean full,
                                       WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return ListViewResponse.of(
                calendarDataService.getActiveLists(year, sortOrder, limitOffset).stream()
                        .map(full ? calendarViewFactory::getActiveListView : SimpleActiveListView::new)
                        .toList(),
                calendarDataService.getActiveListCount(year),
                limitOffset
        );
    }

    /**
     * Active List Year API
     *
     * Get all supplemental calendars for one year:  (GET) /api/3/calendars/{year}/supplemental
     * Request Parameters:  full - If true, full active lists will be returned including all entries (default false)
     *                      order - Determines if the returned calendars are in ascending(ASC) or descending(DESC)
     *                              order (default ASC)
     *                      limit - Limit the number of results (default 100)
     *                      offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/{year:\\d{4}}/supplemental")
    public BaseResponse getCalendarSupplementals(@PathVariable int year,
                                                 @RequestParam(defaultValue = "false") boolean full,
                                                 WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return ListViewResponse.of(
                calendarDataService.getCalendarSupplementals(year, sortOrder, limitOffset).stream()
                        .map(full ? calendarViewFactory::getCalendarSupView : SimpleCalendarSupView::new)
                        .toList(),
                calendarDataService.getSupplementalCount(year),
                limitOffset
        );
    }

    /**
     * Calendar Get API
     *
     * Gets a single calendar via year and calendar number:
     *      (GET) /api/3/calendars/{year}/{calendarNumber}
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}")
    public BaseResponse getCalendar(@PathVariable int year,
                                    @PathVariable int calNo,
                                    @RequestParam(defaultValue = "true") boolean full) {
        Calendar calendar = calendarDataService.getCalendar(new CalendarId(calNo, year));
        return new ViewObjectResponse<>(full ? calendarViewFactory.getCalendarView(calendar)
                                             : new SimpleCalendarView(calendar));
    }

    /**
     * Active List Calendar Get API
     *
     * Gets a single active list via year, calendar number, and sequence number:
     *      (GET) /api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{sequenceNo:\\d+}")
    public BaseResponse getActiveList(@PathVariable int year,
                                      @PathVariable int calNo,
                                      @PathVariable int sequenceNo,
                                      @RequestParam(defaultValue = "true") boolean full) {
        CalendarActiveList activeList = calendarDataService.getActiveList(
                                            new CalendarActiveListId(calNo, year, sequenceNo));
        return new ViewObjectResponse<>(full ? calendarViewFactory.getActiveListView(activeList)
                                             : new SimpleActiveListView(activeList));
    }

    /**
     * Supplemental Calendar Get API
     *
     * Gets a single supplemental via year, calendar number, and supplemental version:
     *      (GET) /api/3/calendars/{year}/{calendarNumber}/{version}
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{version:[A-z]+}")
    public BaseResponse getCalendarSupplemental(@PathVariable int year,
                                                @PathVariable int calNo,
                                                @PathVariable String version,
                                                @RequestParam(defaultValue = "true") boolean full) {
        if (version.equalsIgnoreCase("floor")) {
            version = Version.ORIGINAL.toString();
        }
        CalendarSupplemental calSup = calendarDataService.getCalendarSupplemental(
                                            new CalendarSupplementalId(calNo, year, parseVersion(version, "version")));
        return new ViewObjectResponse<>(full ? calendarViewFactory.getCalendarSupView(calSup)
                                             : new SimpleCalendarSupView(calSup));
    }

    /** --- Exception Handlers --- */

    /**
     * Returns an error response when a calendar not found exception is caught
     * @param ex
     * @return
     */
    @ExceptionHandler(CalendarNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse handleCalNotFoundEx(CalendarNotFoundEx ex) {
        CalendarId calId = ex.getCalendarId();
        ViewObject calendarIdView;
        if (calId instanceof CalendarSupplementalId) {
            calendarIdView = new CalendarSupIdView((CalendarSupplementalId) calId);
        }
        else if (calId instanceof CalendarActiveListId) {
            calendarIdView = new CalendarActiveListIdView((CalendarActiveListId) calId);
        }
        else {
            calendarIdView = new CalendarIdView(calId);
        }
        return new ViewObjectErrorResponse(ErrorCode.CALENDAR_NOT_FOUND, calendarIdView);
    }
}
