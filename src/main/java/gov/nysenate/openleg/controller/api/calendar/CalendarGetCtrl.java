package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarGetCtrl.class);

    @Autowired
    private CalendarDataService calendarDataService;

    @RequestMapping(value = "/{year:\\d{4}}")
    public BaseResponse getCalendars(@PathVariable int year,
                                       @RequestParam MultiValueMap<String, String> parameters) {
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarDataService.getCalendars(year, sortOrder, limitOffset).stream()
                            .map(SimpleCalendarView::new)
                            .collect(Collectors.toList()),
                    calendarDataService.getCalendarCount(year),
                    limitOffset
            );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No calendars exist under the year: " + year);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get calendars by year");
        }
    }

    @RequestMapping(value = "/{year:\\d{4}}/activelist")
    public BaseResponse getActiveLists(@PathVariable int year,
                                         @RequestParam MultiValueMap<String, String> parameters) {
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarDataService.getActiveLists(year, sortOrder, limitOffset).stream()
                            .map(SimpleActiveListView::new)
                            .collect(Collectors.toList()),
                    calendarDataService.getActiveListCount(year),
                    limitOffset
            );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No active lists exist under the year: " + year);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get calendar active lists by year");
        }
    }

    @RequestMapping(value = "/{year:\\d{4}}/floor")
    public BaseResponse getFloorCalendars(@PathVariable int year,
                                            @RequestParam MultiValueMap<String, String> parameters) {
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarDataService.getFloorCalendars(year, sortOrder, limitOffset).stream()
                            .map(SimpleCalendarSupView::new)
                            .collect(Collectors.toList()),
                    calendarDataService.getFloorCalendarCount(year),
                    limitOffset
            );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No floor calendars exist under the year: " + year);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get floor calendars by year");
        }
    }

    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}")
    public BaseResponse getCalendar(@PathVariable int year, @PathVariable int calNo) {
        try {
            return new ViewObjectResponse<>(
                    new CalendarView(calendarDataService.getCalendar(new CalendarId(calNo, year)) ) );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No calendar exists with the given year and calendar number: " + year + ", " + calNo);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get calendar");
        }
    }

    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{sequenceNo:\\d+}")
    public BaseResponse getActiveList(@PathVariable int year, @PathVariable int calNo, @PathVariable int sequenceNo) {
        try {
            return new ViewObjectResponse<>(
                    new ActiveListView(
                            calendarDataService.getActiveList(new CalendarActiveListId(calNo, year, sequenceNo)) ) );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No active list exists with the given year, calendar number, " +
                    "and sequence number: " + year + ", " + calNo + ", " + sequenceNo);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get calendar active list");
        }
    }

    @RequestMapping(value = "/{year:\\d{4}}/{calNo:\\d+}/{version:[A-z]+}")
    public BaseResponse getFloorCalendar(@PathVariable int year, @PathVariable int calNo, @PathVariable String version) {
        try {
            return new ViewObjectResponse<>(
                    new CalendarSupView(
                            calendarDataService.getFloorCalendar(new CalendarSupplementalId(calNo, year, Version.of(version))) ) );
        }
        catch (CalendarNotFoundEx ex) {
            return new SimpleErrorResponse("No floor calendar exists with the given year, calendar number, " +
                    "and version: " + year + ", " + calNo + ", " + version);
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get floor calendar");
        }
    }
}