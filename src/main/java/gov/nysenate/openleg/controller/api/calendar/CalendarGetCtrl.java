package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.client.response.base.*;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarGetCtrl.class);

    @Autowired
    private CalendarDataService calendarDataService;

    /** --- Request Handlers --- */

    /**
     * Returns a simplified calendar view for all calendars in the given year
     * @param year
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}")
    public BaseResponse getCalendars(@PathVariable int year, WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarDataService.getCalendars(year, sortOrder, limitOffset).stream()
                        .map(SimpleCalendarView::new)
                        .collect(Collectors.toList()),
                calendarDataService.getCalendarCount(year),
                limitOffset
        );
    }

    /**
     * Returns a simplified calendar view for all active list calendars in the given year
     * @param year
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/activelist")
    public BaseResponse getActiveLists(@PathVariable int year, WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarDataService.getActiveLists(year, sortOrder, limitOffset).stream()
                        .map(SimpleActiveListView::new)
                        .collect(Collectors.toList()),
                calendarDataService.getActiveListCount(year),
                limitOffset
        );
    }

    /**
     * Returns a simplified calendar view for all floor calendars in the given year
     * @param year
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/floor")
    public BaseResponse getFloorCalendars(@PathVariable int year, WebRequest webRequest) {
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarDataService.getFloorCalendars(year, sortOrder, limitOffset).stream()
                        .map(SimpleCalendarSupView::new)
                        .collect(Collectors.toList()),
                calendarDataService.getFloorCalendarCount(year),
                limitOffset
        );
    }

    /**
     * Returns a calendar view response for the calendar matching the given id parameters
     * @param year
     * @param calNo
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}")
    public BaseResponse getCalendar(@PathVariable int year, @PathVariable int calNo) {
        return new ViewObjectResponse<>(
            new CalendarView(calendarDataService.getCalendar(new CalendarId(calNo, year)) ) );
    }

    /**
     * Returns an active list calendar view response for the active list calendar matching the given id parameters
     * @param year
     * @param calNo
     * @param sequenceNo
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{sequenceNo:\\d+}")
    public BaseResponse getActiveList(@PathVariable int year, @PathVariable int calNo, @PathVariable int sequenceNo) {
        return new ViewObjectResponse<>(
            new ActiveListView(
                calendarDataService.getActiveList(new CalendarActiveListId(calNo, year, sequenceNo)) ) );
    }

    /**
     * Returns a floor calendar view response for the floor calendar matching the given id parameters
     * @param year
     * @param calNo
     * @param version
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{version:[A-z]+}")
    public BaseResponse getFloorCalendar(@PathVariable int year, @PathVariable int calNo, @PathVariable String version) {
        return new ViewObjectResponse<>(
            new CalendarSupView(
                calendarDataService.getFloorCalendar(new CalendarSupplementalId(calNo, year, Version.of(version))) ) );
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
        return new ViewObjectErrorResponse(ErrorCode.CALENDAR_NOT_FOUND, new CalendarIdView(ex.getCalendarId()));
    }
}