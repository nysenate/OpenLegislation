package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParameterException;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarSearchCtrl extends BaseCtrl{

    private static final Logger logger = LoggerFactory.getLogger(CalendarSearchCtrl.class);

    @Autowired
    private CalendarSearchService calendarSearchService;

    @Autowired
    private CalendarDataService calendarDataService;

    /** --- Request Handlers --- */

    /**
     * Performs a search on all types of calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchCalendars(@RequestParam(required = true) String term,
                                        @RequestParam(defaultValue = "") String sort,
                                        @RequestParam(defaultValue = "full") String calendarType,
                                        @RequestParam(defaultValue = "false") boolean full,
                                        WebRequest webRequest) throws SearchException, InvalidRequestParameterException {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        BaseResponse response;
        switch (calendarType) {
            case "full":
                SearchResults<CalendarId> calResults = calendarSearchService.searchForCalendars(term, sort, limitOffset);
                response = getCalendarSearchResultResponse(calResults, full);
                break;
            case "active_list":
                SearchResults<CalendarActiveListId> activeListResults =
                        calendarSearchService.searchForActiveLists(term, sort, limitOffset);
                response = getActiveListSearchResultResponse(activeListResults, full);
                break;
            case "floor":
                SearchResults<CalendarSupplementalId> floorCalResults =
                        calendarSearchService.searchForFloorCalendars(term, sort, limitOffset);
                response = getFloorCalendarSearchResultResponse(floorCalResults, full);
                break;
            default:
                throw new InvalidRequestParameterException(calendarType, "calendarType", "String", "full|active_list|floor");
        }
        return response;
    }

    /**
     * Performs a search on all types of calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse searchCalendarsOfYear(@PathVariable Integer year,
                                              @RequestParam(required = true) String term,
                                              @RequestParam(defaultValue = "") String sort,
                                              @RequestParam(defaultValue = "full") String calendarType,
                                              @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException, InvalidRequestParameterException {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        BaseResponse response;
        switch (calendarType) {
            case "full":
                SearchResults<CalendarId> calResults =
                        calendarSearchService.searchForCalendarsByYear(year, term, sort, limitOffset);
                response = getCalendarSearchResultResponse(calResults, full);
                break;
            case "active_list":
                SearchResults<CalendarActiveListId> activeListResults =
                        calendarSearchService.searchForActiveListsByYear(year, term, sort, limitOffset);
                response = getActiveListSearchResultResponse(activeListResults, full);
                break;
            case "floor":
                SearchResults<CalendarSupplementalId> floorCalResults =
                        calendarSearchService.searchForFloorCalendarsByYear(year, term, sort, limitOffset);
                response = getFloorCalendarSearchResultResponse(floorCalResults, full);
                break;
            default:
                throw new InvalidRequestParameterException(calendarType, "calendarType", "String", "full|active_list|floor");
        }
        return response;
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Generates a calendar list response from calendar search results
     * @param results
     * @param full
     * @return
     */
    private BaseResponse getCalendarSearchResultResponse(SearchResults<CalendarId> results, boolean full) {
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ?
                                new CalendarView(calendarDataService.getCalendar(result.getResult()))
                              : new SimpleCalendarView(calendarDataService.getCalendar(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /**
     * Generates an active list list response from active list search results
     * @param results
     * @param full
     * @return
     */
    private BaseResponse getActiveListSearchResultResponse(SearchResults<CalendarActiveListId> results, boolean full) {
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ?
                                new ActiveListView(calendarDataService.getActiveList(result.getResult()))
                              : new SimpleActiveListView(calendarDataService.getActiveList(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /**
     * Generates a floor calendar list response from floor calendar search results
     * @param results
     * @param full
     * @return
     */
    private BaseResponse getFloorCalendarSearchResultResponse(SearchResults<CalendarSupplementalId> results, boolean full) {
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ? new CalendarSupView(calendarDataService.getFloorCalendar(result.getResult()))
                                : new SimpleCalendarSupView(calendarDataService.getFloorCalendar(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /** --- Exception Handlers --- */
}
