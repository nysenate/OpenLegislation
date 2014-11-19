package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
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
     * Calendar Search API
     *
     * Performs a search across all calendars: (GET) /api/3/calendars/search
     * Request Parameters:      term - The lucene query string
     *                          sort - The lucene sort string (blank by default)
     *                          calendarType - The type of calendar to search (full, active_list, supplemental)
     *                                  (default supplemental)
     *                          full - If true, full calendars will be returned including
     *                                  active list and supplemental entries (default false)
     *                          limit - Limit the number of results (default 100)
     *                          offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchCalendars(@RequestParam(required = true) String term,
                                        @RequestParam(defaultValue = "") String sort,
                                        @RequestParam(defaultValue = "full") String calendarType,
                                        @RequestParam(defaultValue = "false") boolean full,
                                        WebRequest webRequest) throws SearchException, InvalidRequestParameterException {
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return getCalendarSearchResponse(term, sort, calendarType, full, limitOffset, null);
    }

    /**
     * Calendar Search API
     *
     * Performs a search across all calendars: (GET) /api/3/calendars/{year}/search
     * Request Parameters:      term - The lucene query string
     *                          sort - The lucene sort string (blank by default)
     *                          calendarType - The type of calendar to search (full, active_list, supplemental)
     *                                  (default supplemental)
     *                          full - If true, full calendars will be returned including
     *                                  active list and supplemental entries (default false)
     *                          limit - Limit the number of results (default 100)
     *                          offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse searchCalendarsOfYear(@PathVariable Integer year,
                                              @RequestParam(required = true) String term,
                                              @RequestParam(defaultValue = "") String sort,
                                              @RequestParam(defaultValue = "full") String calendarType,
                                              @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException, InvalidRequestParameterException {
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return getCalendarSearchResponse(term, sort, calendarType, full, limitOffset, year);
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Performs a calendar search based on the input parameters and returns a search response
     *
     * @param term
     * @param sort
     * @param calendarType
     * @param full
     * @param limitOffset
     * @param year
     * @return
     * @throws SearchException
     * @throws InvalidRequestParameterException
     */
    private BaseResponse getCalendarSearchResponse(String term, String sort, String calendarType, boolean full,
                                                   LimitOffset limitOffset, Integer year)
            throws SearchException, InvalidRequestParameterException {
        BaseResponse response;
        switch (calendarType) {
            case "full":
                SearchResults<CalendarId> calResults = (year == null)
                        ? calendarSearchService.searchForCalendars(term, sort, limitOffset)
                        : calendarSearchService.searchForCalendarsByYear(year, term, sort, limitOffset);
                response = getCalendarSearchResultResponse(calResults, full);
                break;
            case "active_list":
                SearchResults<CalendarActiveListId> activeListResults = (year == null)
                        ? calendarSearchService.searchForActiveLists(term, sort, limitOffset)
                        : calendarSearchService.searchForActiveListsByYear(year, term, sort, limitOffset);
                response = getActiveListSearchResultResponse(activeListResults, full);
                break;
            case "supplemental":
                SearchResults<CalendarSupplementalId> supplementalCalendarResults = (year == null)
                        ? calendarSearchService.searchForSupplementalCalendars(term, sort, limitOffset)
                        : calendarSearchService.searchForSupplementalCalendarsByYear(year, term, sort, limitOffset);
                response = getFloorCalendarSearchResultResponse(supplementalCalendarResults, full);
                break;
            default:
                throw new InvalidRequestParameterException(calendarType, "calendarType", "String", "full|active_list|floor");
        }
        return response;
    }

    /**
     * Generates a calendar list response from calendar search results
     * @param results
     * @param full
     * @return
     */
    private BaseResponse getCalendarSearchResultResponse(SearchResults<CalendarId> results, boolean full) {
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> new SearchResultView((full)
                                ? new CalendarView(calendarDataService.getCalendar(result.getResult()))
                                : new SimpleCalendarView(calendarDataService.getCalendar(result.getResult())),
                                result.getRank()))
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
                        .map(result -> new SearchResultView(( full)
                                ? new ActiveListView(calendarDataService.getActiveList(result.getResult()))
                                : new SimpleActiveListView(calendarDataService.getActiveList(result.getResult())),
                                result.getRank()))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset());
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
                        .map(result -> new SearchResultView((full)
                                ? new CalendarSupView(calendarDataService.getCalendarSupplemental(result.getResult()))
                                : new SimpleCalendarSupView(calendarDataService.getCalendarSupplemental(result.getResult())),
                                result.getRank()))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset());
    }

    /** --- Exception Handlers --- */
}
